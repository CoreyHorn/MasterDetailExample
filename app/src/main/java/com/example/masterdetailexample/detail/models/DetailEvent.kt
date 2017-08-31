package com.example.masterdetailexample.detail.models

import com.example.masterdetailexample.basemodels.Event

sealed class DetailEvent: Event() {
    data class DeleteItem(val id: String): DetailEvent()
    object EditItem: DetailEvent()
    data class SaveItem(val id: String, val text: String): DetailEvent()
}