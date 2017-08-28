package com.example.masterdetailexample.list

import com.example.masterdetailexample.addTo
import com.example.masterdetailexample.basearchitecture.Presenter
import com.example.masterdetailexample.list.models.ListAction
import com.example.masterdetailexample.list.models.ListEvent
import com.example.masterdetailexample.list.models.ListResult
import com.example.masterdetailexample.list.models.ListState
import com.example.masterdetailexample.merge
import com.example.masterdetailexample.room.ItemRepo
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

class ListPresenter(itemRepo: ItemRepo) : Presenter<ListEvent, ListResult, ListAction, ListState>() {

    init {
        attachResultStream(ListInteractor(actions, itemRepo).results())
    }

    override fun attachEventStream(events: Observable<ListEvent>) {
        super.attachEventStream(events)

        events.compose(EventToActionTransformer())
                .subscribe { actions.onNext(it) }
                .addTo(eventDisposables)
    }

    override fun attachResultStream(results: Observable<ListResult>) {
        results.scan(ListState.idle(), ::listAccumulator).subscribe { states.onNext(it) }
    }

    private inner class EventToActionTransformer : ObservableTransformer<ListEvent, ListAction> {
        override fun apply(upstream: Observable<ListEvent>): ObservableSource<ListAction> {
            return upstream.publish { source ->
                merge<ListAction>(
                        source.ofType(ListEvent.Create::class.java).map { ListAction.Create(it.text, it.listState) },
                        source.ofType(ListEvent.Delete::class.java).map { ListAction.Delete(it.item, it.listState) },
                        source.ofType(ListEvent.Select::class.java).map { ListAction.Select(it.item, it.listState) },
                        source.ofType(ListEvent.ClearedInput::class.java).map { ListAction.ClearedInput(it.listState) },
                        source.ofType(ListEvent.UpdateListState::class.java).map { ListAction.UpdateListState(it.listState) }
                )
            }
        }
    }
}

fun listAccumulator(previousState: ListState, result: ListResult): ListState {
    return when (result) {
        is ListResult.NewItems -> previousState.copy(items = result.items)
        is ListResult.RequestInProgress -> previousState.copy(clearText = true, listState = result.listState)
        is ListResult.ClearedInput -> previousState.copy(clearText = false, listState = result.listState)
        is ListResult.UpdatedListState -> previousState.copy(clearText = false, listState = result.listState)
        is ListResult.Selected -> previousState.copy(clearText = false, listState = result.listState)
    }
}