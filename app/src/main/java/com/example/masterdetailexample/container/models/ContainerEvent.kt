package com.example.masterdetailexample.container.models

import com.example.masterdetailexample.basemodels.Event

sealed class ContainerEvent: Event() {
    class ShowingDetail: ContainerEvent()
    data class DualPaneUpdate(val dualPane: Boolean): ContainerEvent()
}