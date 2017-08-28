package com.example.masterdetailexample.detail

import com.example.masterdetailexample.addTo
import com.example.masterdetailexample.basearchitecture.Presenter
import com.example.masterdetailexample.detail.models.DetailAction
import com.example.masterdetailexample.detail.models.DetailEvent
import com.example.masterdetailexample.detail.models.DetailResult
import com.example.masterdetailexample.detail.models.DetailState
import com.example.masterdetailexample.merge
import com.example.masterdetailexample.room.ItemRepo
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

class DetailPresenter(itemRepo: ItemRepo) : Presenter<DetailEvent, DetailResult, DetailAction, DetailState>() {

    init {
        attachResultStream(DetailInteractor(actions, itemRepo).results())
    }

    override fun attachEventStream(events: Observable<DetailEvent>) {
        super.attachEventStream(events)

        events.compose(EventToAction())
                .subscribe { actions.onNext(it) }
                .addTo(eventDisposables)
    }

    override fun attachResultStream(results: Observable<DetailResult>) {
        results.scan(DetailState.idle(), ::detailAccumulator).subscribe { states.onNext(it) }
    }

    private inner class EventToAction : ObservableTransformer<DetailEvent, DetailAction> {
        override fun apply(upstream: Observable<DetailEvent>): ObservableSource<DetailAction> {
            return upstream.publish { source ->
                merge<DetailAction>(
                        source.ofType(DetailEvent.DeleteItem::class.java).map { DetailAction.DeleteItem(it.id) },
                        source.ofType(DetailEvent.EditItem::class.java).map { DetailAction.EditItem() },
                        source.ofType(DetailEvent.SaveItem::class.java).map { DetailAction.SaveItem(it.id, it.text) }
                )
            }
        }
    }

}

fun detailAccumulator(previousState: DetailState, result: DetailResult): DetailState {
    return when (result) {
        is DetailResult.ChangeInProgress -> previousState.copy(buttonsActive = false, editing = false)
        is DetailResult.BeginEditing -> previousState.copy(editing = true, buttonsActive = true)
        is DetailResult.ItemRemoved -> previousState.copy(item = null, buttonsActive = false, editing = false)
        is DetailResult.ItemChanged -> previousState.copy(item =  result.item, buttonsActive = true, editing = false)
    }
}