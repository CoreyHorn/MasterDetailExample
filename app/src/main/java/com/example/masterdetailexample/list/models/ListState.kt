package com.example.masterdetailexample.list.models

import android.os.Parcelable
import com.example.masterdetailexample.room.Item
import com.example.masterdetailexample.basemodels.State

data class ListState(val clearText: Boolean = false,
                     val listState: Parcelable?,
                     val items: List<Item>?) : State() {
    companion object {
        fun idle() = ListState(listState = null, items = null)
    }
}