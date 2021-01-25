package com.gne.groceries.util

import androidx.paging.PagedList
import com.gne.groceries.pojo.GroceriesResponse
import com.gne.groceries.pojo.GroceryData
import com.gne.groceries.retrofit.ApiInterface
import com.gne.groceries.room.RoomGroceriesRepository
import retrofit2.Call
import retrofit2.Callback

class GroceriesBoundaryCallBack(
    val apiInterface: ApiInterface,
    val roomRepository: RoomGroceriesRepository
) : PagedList.BoundaryCallback<GroceryData>() {
    companion object {var offset = 0}
    override fun onItemAtEndLoaded(itemAtEnd: GroceryData) {
        apiCall()
    }

    override fun onItemAtFrontLoaded(itemAtFront: GroceryData) {
//        apiCall()
    }

    override fun onZeroItemsLoaded() {
        apiCall()
    }

    fun apiCall(){
        if(isNetworkAvailable(roomRepository.context)) {
            val call = apiInterface.getGroceries(offset.toString(), "10")
            call.enqueue(object : Callback<GroceriesResponse> {
                override fun onResponse(
                    call: Call<GroceriesResponse>,
                    response: retrofit2.Response<GroceriesResponse>
                ) {
                    val groceriesResponse = response.body()
                    if (response.isSuccessful && groceriesResponse != null) {
                        if (groceriesResponse.status.equals("ok")) {
                            offset += 10
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
    }
}