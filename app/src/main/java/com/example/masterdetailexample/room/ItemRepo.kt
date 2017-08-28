package com.example.masterdetailexample.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = arrayOf(Item::class), version = 1, exportSchema = false)
abstract class ItemRepo: RoomDatabase() {
    abstract fun itemDao(): ItemDao
}