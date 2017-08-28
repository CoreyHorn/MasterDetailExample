package com.example.masterdetailexample.list.models

import android.os.Parcelable
import com.example.masterdetailexample.basemodels.Result
import com.example.masterdetailexample.room.Item

sealed class ListResult : Result() {
    data class NewItems(val items: List<Item>): ListResult()
    data class RequestInProgress(val listState: Parcelable): ListResult()
    data class ClearedInput(val listState: Parcelable): ListResult()
    data class UpdatedListState(val listState: Parcelable): ListResult()
    data class Selected(val listState: Parcelable): ListResult()
}