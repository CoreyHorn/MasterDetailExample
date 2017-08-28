package com.example.masterdetailexample.detail.models

import com.example.masterdetailexample.basemodels.Action

sealed class DetailAction: Action() {
    data class DeleteItem(val id: String): DetailAction()
    class EditItem: DetailAction()
    class SaveItem(val id: String, val text: String): DetailAction()
}