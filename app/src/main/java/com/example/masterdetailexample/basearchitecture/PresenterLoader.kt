package com.example.masterdetailexample.basearchitecture

import android.content.Context
import android.support.v4.content.Loader
import com.example.masterdetailexample.basemodels.Action
import com.example.masterdetailexample.basemodels.Event
import com.example.masterdetailexample.basemodels.Result
import com.example.masterdetailexample.basemodels.State

class PresenterLoader<E: Event, R: Result, A: Action, S: State, P: Presenter<E, R, A, S>>
    (context: Context, private val factory: PresenterFactory<P>): Loader<P>(context) {

    var presenter: P? = null

    override fun onStartLoading() {
        super.onStartLoading()

        if (presenter != null) {
            deliverResult(presenter)
            return
        }

        forceLoad()
    }

    override fun forceLoad() {
        super.forceLoad()

        presenter = factory.create()
        deliverResult(presenter)
    }

    override fun onReset() {
        super.onReset()

        presenter = null
    }
}