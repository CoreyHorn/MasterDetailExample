package com.example.masterdetailexample.basearchitecture

import android.content.Context
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import com.example.masterdetailexample.addTo
import com.example.masterdetailexample.basemodels.Action
import com.example.masterdetailexample.basemodels.Event
import com.example.masterdetailexample.basemodels.Result
import com.example.masterdetailexample.basemodels.State
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

interface PresenterView<P: Presenter<E, R, A, S>, E: Event, R: Result, A: Action, S: State> {

    val events: PublishSubject<E>

    var presenter: P?
    var disposables: CompositeDisposable
    var attachAttempted: Boolean

    val loaderCallbacks: LoaderManager.LoaderCallbacks<P>
    get() = object: LoaderManager.LoaderCallbacks<P> {
        override fun onLoaderReset(loader: Loader<P>?) {
            presenter = null
        }

        override fun onLoadFinished(loader: Loader<P>?, data: P) {
            presenter = data
            onPresenterAvailable(data)
        }

        override fun onCreateLoader(id: Int, args: Bundle?): Loader<P> {
            return PresenterLoader(getContext(), presenterFactory())
        }
    }

    fun initializePresenter(loaderManager: LoaderManager) {
        @Suppress("UNCHECKED_CAST")
        val loader = loaderManager.getLoader<P>(loaderId()) as PresenterLoader<E, R, A, S, P>?

        if (loader == null) {
            initializeLoader(loaderCallbacks)
        }
        else {
            loader.presenter?.let {
                presenter = it
                onPresenterAvailable(it)
            }
        }
    }

    fun attachStream() {
        attachAttempted = true
        presenter?.let {
            it.attachEventStream(events)
            it.states()
                    .subscribe { renderViewState(it) }
                    .addTo(disposables)
        }
    }

    fun detachStream() {
        attachAttempted = false
        disposables.clear()
        disposables = CompositeDisposable()
        presenter?.detachEventStream()
    }

    fun onPresenterAvailable(presenter: P) {
        if (attachAttempted) { attachStream() }
    }

    fun initializeLoader(loaderCallbacks: LoaderManager.LoaderCallbacks<P>)
    fun getContext(): Context
    fun loaderId(): Int
    fun presenterFactory(): PresenterFactory<P>
    fun renderViewState(state: S)
    fun setupViewBindings()
}