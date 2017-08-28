package com.example.masterdetailexample.container.models

import com.example.masterdetailexample.basemodels.State


data class ContainerState(val showDetail: Boolean): State() {
    companion object {
        fun idle() = ContainerState(false)
    }
}