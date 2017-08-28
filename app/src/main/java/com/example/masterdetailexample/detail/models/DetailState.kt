package com.example.masterdetailexample.detail.models

import com.example.masterdetailexample.room.Item
import com.example.masterdetailexample.basemodels.State

data class DetailState(val item: Item?,
                       val editing: Boolean,
                       val buttonsActive: Boolean): State() {

    companion object {
        fun idle() = DetailState(null, false, true)
    }
}