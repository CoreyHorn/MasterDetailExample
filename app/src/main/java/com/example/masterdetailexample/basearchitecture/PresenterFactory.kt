package com.example.masterdetailexample.basearchitecture

abstract class PresenterFactory<out P> {
    abstract fun create(): P
}