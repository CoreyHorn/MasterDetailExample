package com.example.masterdetailexample.list.models

import android.os.Parcelable
import com.example.masterdetailexample.basemodels.Action
import com.example.masterdetailexample.room.Item

sealed class ListAction : Action() {
    data class Create(val text: String, val listState: Parcelable) : ListAction()
    data class Delete(val item: Item, val listState: Parcelable) : ListAction()
    data class Select(val item: Item, val listState: Parcelable) : ListAction()
    class ClearedInput(val listState: Parcelable): ListAction()
    data class UpdateListState(val state: Parcelable): ListAction()
}