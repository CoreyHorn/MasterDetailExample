package com.example.masterdetailexample.container.models

import com.example.masterdetailexample.basemodels.Action

sealed class ContainerAction: Action() {
    class MarkDetailAsShown: ContainerAction()
    data class UpdateDualPane(val dualPane: Boolean): ContainerAction()
}