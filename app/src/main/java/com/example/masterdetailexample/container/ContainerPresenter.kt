package com.example.masterdetailexample.container

import com.example.masterdetailexample.addTo
import com.example.masterdetailexample.basearchitecture.Presenter
import com.example.masterdetailexample.container.models.ContainerAction
import com.example.masterdetailexample.container.models.ContainerEvent
import com.example.masterdetailexample.container.models.ContainerResult
import com.example.masterdetailexample.container.models.ContainerState
import com.example.masterdetailexample.merge
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

class ContainerPresenter: Presenter<ContainerEvent, ContainerResult, ContainerAction, ContainerState>() {

    init {
        attachResultStream(ContainerInteractor(actions).results())
    }

    override fun attachEventStream(events: Observable<ContainerEvent>) {
        super.attachEventStream(events)

        events.compose(EventToAction())
                .subscribe { actions.onNext(it) }
                .addTo(eventDisposables)
    }

    override fun attachResultStream(results: Observable<ContainerResult>) {
        results.scan(ContainerState.idle(), ::containerAccumulator).subscribe { states.onNext(it) }
    }

    private inner class EventToAction: ObservableTransformer<ContainerEvent, ContainerAction> {
        override fun apply(upstream: Observable<ContainerEvent>): ObservableSource<ContainerAction> {
            return upstream.publish { source ->
                merge<ContainerAction>(
                        source.ofType(ContainerEvent.ShowingDetail::class.java).map { ContainerAction.MarkDetailAsShown() },
                        source.ofType(ContainerEvent.DualPaneUpdate::class.java).map { ContainerAction.UpdateDualPane(it.dualPane) }
                )
            }
        }
    }
}

fun containerAccumulator(previousState: ContainerState, result: ContainerResult): ContainerState {
    return when (result) {
        is ContainerResult.ShowDetail -> previousState.copy(showDetail = true)
        is ContainerResult.DetailWasShown -> previousState.copy(showDetail = false)
        is ContainerResult.UpdatedDualPane -> previousState
    }
}