package com.example.masterdetailexample.detail.models

import com.example.masterdetailexample.basemodels.Event

sealed class DetailEvent: Event() {
    class DeleteItem(val id: String): DetailEvent()
    class EditItem: DetailEvent()
    data class SaveItem(val id: String, val text: String): DetailEvent()
}