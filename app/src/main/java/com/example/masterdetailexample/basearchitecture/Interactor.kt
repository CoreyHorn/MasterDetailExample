package com.example.masterdetailexample.basearchitecture

import com.example.masterdetailexample.basemodels.Result
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

abstract class Interactor<R: Result> {

    protected val results: PublishSubject<R> = PublishSubject.create()

    fun results(): Observable<R> {
        return results
    }
}