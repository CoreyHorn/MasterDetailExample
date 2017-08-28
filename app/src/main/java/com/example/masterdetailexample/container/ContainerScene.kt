package com.example.masterdetailexample.container

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * For now these scenes will be singletons. If we decide that we want to be able to have multiple scenes in memory at once,
 * we will need to determine a better way to manage instances.
 */
object ContainerScene {

    private val selections = PublishSubject.create<Any>()

    fun pushSelection() {
        selections.onNext(Any())
    }

    fun selections(): Observable<Any> {
        return selections
    }
}