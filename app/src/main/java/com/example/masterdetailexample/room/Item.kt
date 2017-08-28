package com.example.masterdetailexample.room

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "Items")
data class Item(@PrimaryKey(autoGenerate = true) var id: Long = 0,
                @ColumnInfo(name = "text") var text: String = "",
                @ColumnInfo(name = "selected") var selected: Boolean = false) {
}