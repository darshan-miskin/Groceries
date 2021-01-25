package com.gne.groceries.retrofit

import com.gne.groceries.pojo.GroceriesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("/resource/9ef84268-d588-465a-a308-a864a43d0070")
    fun getGroceries(@Query("offset") offSet:String,
                     @Query("limit") limit:String): Call<GroceriesResponse>
}