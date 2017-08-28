package com.example.masterdetailexample

import android.app.Application
import android.arch.persistence.room.Room
import com.example.masterdetailexample.room.ItemRepo

class App: Application() {
    companion object {
        lateinit var itemRepo: ItemRepo
            private set
    }

    override fun onCreate() {
        super.onCreate()
        itemRepo = Room.databaseBuilder(applicationContext, ItemRepo::class.java, "whatever").build()
    }
}