package com.example.masterdetailexample.basearchitecture

import android.support.annotation.CallSuper
import android.util.Log
import com.example.masterdetailexample.basemodels.Action
import com.example.masterdetailexample.basemodels.Event
import com.example.masterdetailexample.basemodels.Result
import com.example.masterdetailexample.basemodels.State
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

abstract class Presenter<E: Event, R: Result, A: Action, S: State> {

    protected val actions: PublishSubject<A> = PublishSubject.create()
    protected val states: BehaviorSubject<S> = BehaviorSubject.create()

    protected var eventDisposables = CompositeDisposable()

    fun actions(): Observable<A> {
        return actions
                .doOnNext { Log.d("Debug Streams - " + javaClass.canonicalName.split(".").last(),
                        "Action: " + it) }
    }

    fun states(): Observable<out S> {
        return states
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { Log.d("Debug Streams - " + javaClass.canonicalName.split(".").last(),
                        "State: " + it) }
    }

    @CallSuper
    open fun attachEventStream(events: Observable<E>) {
        eventDisposables.clear()
        eventDisposables = CompositeDisposable()
    }

    fun detachEventStream() {
        eventDisposables.clear()
        eventDisposables = CompositeDisposable()
    }

    abstract fun attachResultStream(results: Observable<R>)

}



