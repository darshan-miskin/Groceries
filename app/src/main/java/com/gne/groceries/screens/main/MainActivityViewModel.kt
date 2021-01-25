package com.gne.groceries.screens.main

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.PagedList
import com.gne.groceries.pojo.GroceryData
import com.gne.groceries.repo.GroceriesRepository

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val repository=GroceriesRepository(application, viewModelScope)
    var liveData:LiveData<PagedList<GroceryData>>

    val isFiler:Boolean
        get() = (priceFilter || dateFilter)
    private var priceFilter=false
    private var dateFilter=false

    init {
        liveData=repository.getDbData()
    }

    fun deleteAll(){
        repository.deleteAll()
    }

    fun getDbData(columnName:String="", order:Int=0):LiveData<PagedList<GroceryData>>{
        liveData=repository.getDbData(columnName, order)
        liveData.value?.dataSource?.invalidate()
        return liveData
    }

    fun dateFilterApplied(pos:Int){
        dateFilter=true
        priceFilter=false

        liveData=getDbData(GroceryData.ColumnNames.timestamp.toString(),pos)
    }

    fun priceFilterApplied(pos:Int){
        priceFilter=true
        dateFilter=false

        liveData=getDbData(GroceryData.ColumnNames.modal_price.toString(),pos)
    }
}