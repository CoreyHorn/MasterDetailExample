package com.example.masterdetailexample.detail

import com.example.masterdetailexample.basearchitecture.Interactor
import com.example.masterdetailexample.detail.models.DetailAction
import com.example.masterdetailexample.detail.models.DetailResult
import com.example.masterdetailexample.merge
import com.example.masterdetailexample.room.Item
import com.example.masterdetailexample.room.ItemDao
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.schedulers.Schedulers

class DetailInteractor(actions: Observable<DetailAction>, private val itemDao: ItemDao) : Interactor<DetailResult>() {

    init {
        actions.observeOn(Schedulers.io())
                .compose(ActionToResult())
                .subscribe { results.onNext(it) }

        itemDao.getSelectedItem()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .distinctUntilChanged()
                .compose(ItemToResult())
                .subscribe { results.onNext(it) }
    }

    private inner class ActionToResult: ObservableTransformer<DetailAction, DetailResult> {
        override fun apply(upstream: Observable<DetailAction>): ObservableSource<DetailResult> {
            return upstream.publish { source ->
                merge<DetailResult>(
                        source.ofType(DetailAction.EditItem::class.java).map { DetailResult.BeginEditing() },
                        source.ofType(DetailAction.DeleteItem::class.java).map {
                            itemDao.deleteItem(it.id.toLong())
                            DetailResult.ChangeInProgress()
                        },
                        source.ofType(DetailAction.SaveItem::class.java).map {
                            itemDao.updateItem(Item(it.id.toLong(), it.text, true))
                            DetailResult.ChangeInProgress()
                        }
                )
            }
        }
    }

    private inner class ItemToResult: ObservableTransformer<List<Item>, DetailResult> {
        override fun apply(upstream: Observable<List<Item>>): ObservableSource<DetailResult> {
            return upstream.map {
                if (it.isNotEmpty()) DetailResult.ItemChanged(it[0]) else DetailResult.ItemRemoved()
            }
        }
    }

}