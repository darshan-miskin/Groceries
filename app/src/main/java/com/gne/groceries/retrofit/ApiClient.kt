package com.gne.groceries.retrofit

import com.gne.groceries.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient private constructor(){
    companion object {
        private var apiInterface:ApiInterface?=null

        val instance:ApiInterface
            get() {
                if(apiInterface==null){
                    val okHttpClient= OkHttpClient.Builder()
                        .addInterceptor(ApiInterceptor())
                        .build()

                    val retrofit= Retrofit.Builder()
                        .baseUrl(BuildConfig.BASE_URL)
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()

                    apiInterface=retrofit.create(ApiInterface::class.java)
                }
                return apiInterface!!
            }
    }
}