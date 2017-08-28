package com.example.masterdetailexample.container

import com.example.masterdetailexample.basearchitecture.Interactor
import com.example.masterdetailexample.container.models.ContainerAction
import com.example.masterdetailexample.container.models.ContainerResult
import com.example.masterdetailexample.merge
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

class ContainerInteractor(actions: Observable<ContainerAction>): Interactor<ContainerResult>() {

    private var dualPane = false

    init {
        ContainerScene.selections()
                .filter { !dualPane }
                .subscribe { results.onNext(ContainerResult.ShowDetail()) }

        actions.compose(ActionToResult())
                .subscribe { results.onNext(it) }
    }

    private inner class ActionToResult: ObservableTransformer<ContainerAction, ContainerResult> {
        override fun apply(upstream: Observable<ContainerAction>): ObservableSource<ContainerResult> {
            return upstream.publish { source ->
                merge<ContainerResult>(
                        source.ofType(ContainerAction.MarkDetailAsShown::class.java).map { ContainerResult.DetailWasShown() },
                        source.ofType(ContainerAction.UpdateDualPane::class.java).map {
                            dualPane = it.dualPane
                            ContainerResult.UpdatedDualPane()
                        }
                )
            }
        }
    }

}