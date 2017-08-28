package com.example.masterdetailexample.container.models

import com.example.masterdetailexample.basemodels.Result

sealed class ContainerResult: Result() {
    class ShowDetail: ContainerResult()
    class DetailWasShown: ContainerResult()
    class UpdatedDualPane: ContainerResult()
}