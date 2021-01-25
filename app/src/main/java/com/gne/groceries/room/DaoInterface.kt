package com.gne.groceries.room

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.gne.groceries.pojo.GroceryData

@Dao
interface DaoInterface {

//    @Query("SELECT * FROM groceries")
//    fun getAll(): LiveData<List<GroceryData>>

    @Query("SELECT * FROM groceries")
    fun getAllPaged(): DataSource.Factory<Int, GroceryData>

    @Query("SELECT * FROM groceries ORDER BY :columnName ASC")
    fun getAllAscPaged(columnName:String): DataSource.Factory<Int, GroceryData>

    @Query("SELECT * FROM groceries ORDER BY :columnName DESC")
    fun getAllByDescPaged(columnName:String): DataSource.Factory<Int, GroceryData>

    @Insert
    suspend fun insertAll(persons: List<GroceryData>)

    @Delete
    suspend fun delete(grocery: GroceryData)

    @Query("DELETE FROM groceries")
    suspend fun deleteAll()
}