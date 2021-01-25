package com.gne.groceries.room

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.gne.groceries.pojo.GroceryData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RoomGroceriesRepository(val context: Context, val scope:CoroutineScope) {
    private val daoInterface:DaoInterface=GroceriesDatabase.getDatabase(context,scope).daoInterface()

    fun deleteAll() = scope.launch(Dispatchers.IO) {
        daoInterface.deleteAll()
    }

    fun deleteGrocery(groceryData: GroceryData) = scope.launch(Dispatchers.IO) {
        daoInterface.delete(groceryData)
    }

    fun insertGroceries(groceries: List<GroceryData>) = scope.launch(Dispatchers.IO) {
        daoInterface.insertAll(groceries)
    }

    fun getGroceries(): DataSource.Factory<Int, GroceryData> {
        val factory: DataSource.Factory<Int, GroceryData> =daoInterface.getAllPaged()
        return factory
    }

    fun getAllByAsc(columnName:String): DataSource.Factory<Int, GroceryData>{
        val factory: DataSource.Factory<Int, GroceryData> =daoInterface.getAllAscPaged(columnName)
        return factory
    }

    fun getAllByDesc(columnName:String): DataSource.Factory<Int, GroceryData>{
        val factory: DataSource.Factory<Int, GroceryData> =daoInterface.getAllByDescPaged(columnName)
        return factory
    }

}