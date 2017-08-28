package com.example.masterdetailexample.detail.models

import com.example.masterdetailexample.basemodels.Result
import com.example.masterdetailexample.room.Item

sealed class DetailResult: Result() {
    data class ItemChanged(val item: Item): DetailResult()
    class ItemRemoved: DetailResult()
    class ChangeInProgress: DetailResult()

    class BeginEditing: DetailResult()
}