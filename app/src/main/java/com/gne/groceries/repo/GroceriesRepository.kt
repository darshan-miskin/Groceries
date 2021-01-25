package com.gne.groceries.repo

import android.content.Context
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.gne.groceries.pojo.GroceriesResponse
import com.gne.groceries.pojo.GroceryData
import com.gne.groceries.pojo.Response
import com.gne.groceries.retrofit.ApiClient
import com.gne.groceries.retrofit.ApiInterface
import com.gne.groceries.room.RoomGroceriesRepository
import com.gne.groceries.util.GroceriesBoundaryCallBack
import com.gne.groceries.util.ListDataSource
import com.gne.groceries.util.UiThreadExecutor
import com.gne.groceries.util.isNetworkAvailable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback

class GroceriesRepository(private val context: Context, private val viewModelScope: CoroutineScope) {
    private val roomRepository=RoomGroceriesRepository (context, viewModelScope)
    private var apiInterface=ApiClient.instance
    private val mutableLiveData=MutableLiveData<Response<GroceriesResponse>>()

    fun deleteAll() {
        roomRepository.deleteAll()
    }

    fun getDbData(columnName:String="", order:Int=0):LiveData<PagedList<GroceryData>>{
        if(order==0){
            val factory= roomRepository.getGroceries()
            val boundaryCallBack=GroceriesBoundaryCallBack(apiInterface,roomRepository)
            val livePagedList: LiveData<PagedList<GroceryData>> = LivePagedListBuilder(factory, 10)
                .setBoundaryCallback(boundaryCallBack).build()
            return livePagedList
        }
        else if(order==1){
            val factory= roomRepository.getAllByAsc(columnName)
            val livePagedList: LiveData<PagedList<GroceryData>> = LivePagedListBuilder(factory, 10).build()
            return livePagedList
        }
        else {
            val factory= roomRepository.getAllByDesc(columnName)
            val livePagedList: LiveData<PagedList<GroceryData>> = LivePagedListBuilder(factory, 10).build()
            return livePagedList
        }
    }
}