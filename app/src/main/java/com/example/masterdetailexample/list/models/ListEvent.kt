package com.example.masterdetailexample.list.models

import android.os.Parcelable
import com.example.masterdetailexample.basemodels.Event
import com.example.masterdetailexample.room.Item

sealed class ListEvent : Event() {
    class ClearedInput(val listState: Parcelable): ListEvent()
    data class Create(val text: String, val listState: Parcelable): ListEvent()
    data class Delete(val item: Item, val listState: Parcelable): ListEvent()
    data class Select(val item: Item, val listState: Parcelable): ListEvent()
    data class UpdateListState(val listState: Parcelable): ListEvent()
}