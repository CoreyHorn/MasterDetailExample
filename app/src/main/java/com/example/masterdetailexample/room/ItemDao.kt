package com.example.masterdetailexample.room

import android.arch.persistence.room.*
import io.reactivex.Flowable

@Dao
interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(item: Item)

    @Update
    fun updateItem(vararg items: Item)

    @Delete
    fun deleteItem(item: Item)

    @Query("DELETE FROM Items Where id = :arg0")
    fun deleteItem(id: Long)

    @Query("SELECT * FROM Items")
    fun getItems(): Flowable<List<Item>>

    @Query("SELECT * FROM Items WHERE id = :arg0")
    fun getItem(id: Long): Flowable<List<Item>>

    @Query("SELECT * FROM Items WHERE selected = 1")
    fun getSelectedItem(): Flowable<List<Item>>

    @Query("UPDATE Items SET selected = 0 WHERE selected = 1")
    fun deselectItem()

    @Query("UPDATE Items SET selected = 1 WHERE id = :arg0")
    fun selectItem(id: Long)
}