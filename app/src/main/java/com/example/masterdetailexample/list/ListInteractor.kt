package com.example.masterdetailexample.list

import com.example.masterdetailexample.basearchitecture.Interactor
import com.example.masterdetailexample.container.ContainerScene
import com.example.masterdetailexample.list.models.ListAction
import com.example.masterdetailexample.list.models.ListResult
import com.example.masterdetailexample.merge
import com.example.masterdetailexample.room.Item
import com.example.masterdetailexample.room.ItemRepo
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.schedulers.Schedulers

class ListInteractor(actions: Observable<ListAction>, val itemRepo: ItemRepo): Interactor<ListResult>() {

    init {
        actions.observeOn(Schedulers.io())
                .compose(ActionToResult())
                .subscribe { results.onNext(it) }

        itemRepo.itemDao()
                .getItems()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .compose(ItemsToResult())
                .subscribe { results.onNext(it) }

    }

    private inner class ActionToResult: ObservableTransformer<ListAction, ListResult> {
        override fun apply(upstream: Observable<ListAction>): ObservableSource<ListResult> {
            return upstream.publish { source ->
                merge<ListResult>(
                        source.ofType(ListAction.Create::class.java).map {
                            itemRepo.itemDao().insertItem(Item(text = it.text))
                            ListResult.RequestInProgress(it.listState)
                        },
                        source.ofType(ListAction.Delete::class.java).map {
                            itemRepo.itemDao().deleteItem(it.item)
                            ListResult.RequestInProgress(it.listState)
                        },
                        source.ofType(ListAction.Select::class.java).map {
                            itemRepo.itemDao().deselectItem()
                            itemRepo.itemDao().selectItem(it.item.id)
                            ContainerScene.pushSelection()
                            ListResult.Selected(it.listState)
                        },
                        source.ofType(ListAction.ClearedInput::class.java).map { ListResult.ClearedInput(it.listState) },
                        source.ofType(ListAction.UpdateListState::class.java).map { ListResult.UpdatedListState(it.state) }
                )
            }
        }
    }

    private inner class ItemsToResult: ObservableTransformer<List<Item>, ListResult> {
        override fun apply(upstream: Observable<List<Item>>): ObservableSource<ListResult> {
            return upstream.map { ListResult.NewItems(it) }
        }
    }
}