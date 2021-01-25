package com.gne.groceries.util

import androidx.paging.ItemKeyedDataSource
import androidx.paging.PageKeyedDataSource
import com.gne.groceries.pojo.GroceriesResponse
import com.gne.groceries.pojo.GroceryData
import com.gne.groceries.retrofit.ApiInterface
import com.gne.groceries.room.RoomGroceriesRepository
import retrofit2.Call
import retrofit2.Callback

class ListDataSource(val apiInterface: ApiInterface, val roomRepository:RoomGroceriesRepository) : PageKeyedDataSource<Int, GroceryData>() {
//    override fun getKey(item: GroceryData): Int {
//        return item.id
//    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int,GroceryData>
    ) {

        val call=apiInterface.getGroceries("0", "10")
        call.enqueue(object : Callback<GroceriesResponse> {
            override fun onResponse(
                call: Call<GroceriesResponse>,
                response: retrofit2.Response<GroceriesResponse>
            ) {
                val groceriesResponse = response.body()
                if (response.isSuccessful && groceriesResponse != null) {
                    if (groceriesResponse.status.equals("ok")) {
                        callback.onResult(groceriesResponse.records, 0, 10)

                        roomRepository.insertGroceries(groceriesResponse.records)
//                        mutableLiveData.postValue(Response(Response.Status.SUCCESS, groceriesResponse, groceriesResponse.message))
                    } else {
//                        mutableLiveData.postValue(Response(Response.Status.ERROR,null, groceriesResponse.message))
                    }
                } else {
//                    mutableLiveData.postValue(Response(Response.Status.ERROR,null,"Something wrong with api"))
                }
            }

            override fun onFailure(call: Call<GroceriesResponse>, t: Throwable) {
//                mutableLiveData.postValue(t.message?.let {
//                    Response(Response.Status.FAILURE, null, it)
//                })
            }
        })

    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int,GroceryData>) {
        val call=apiInterface.getGroceries((params.key+1).toString(), "10")
        call.enqueue(object : Callback<GroceriesResponse> {
            override fun onResponse(
                call: Call<GroceriesResponse>,
                response: retrofit2.Response<GroceriesResponse>
            ) {
                val groceriesResponse = response.body()
                if (response.isSuccessful && groceriesResponse != null) {
                    if (groceriesResponse.status.equals("ok")) {
                        val adjacentKey = params.key + 1
                        callback.onResult(groceriesResponse.records,adjacentKey)

                        roomRepository.insertGroceries(groceriesResponse.records)
                    } else {

                    }
                } else {

                }
            }

            override fun onFailure(call: Call<GroceriesResponse>, t: Throwable) {

            }
        })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int,GroceryData>) {

    }


}