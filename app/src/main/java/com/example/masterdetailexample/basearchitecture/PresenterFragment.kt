package com.example.masterdetailexample.basearchitecture

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import com.example.masterdetailexample.basemodels.Action
import com.example.masterdetailexample.basemodels.Event
import com.example.masterdetailexample.basemodels.Result
import com.example.masterdetailexample.basemodels.State
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

abstract class PresenterFragment<P: Presenter<E, R, A, S>, E: Event, R: Result, A: Action, S: State>: Fragment(), PresenterView<P, E, R, A, S> {

    override val events: PublishSubject<E> = PublishSubject.create()

    override var presenter: P? = null
    override var disposables = CompositeDisposable()
    override var attachAttempted = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initializePresenter(loaderManager)
    }

    override fun onResume() {
        super.onResume()
        attachStream()
        setupViewBindings()
    }

    override fun onPause() {
        detachStream()
        super.onPause()
    }

    override fun initializeLoader(loaderCallbacks: LoaderManager.LoaderCallbacks<P>) {
        loaderManager.initLoader(loaderId(), null, loaderCallbacks)
    }
}