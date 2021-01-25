package com.gne.groceries.pojo

class Response<T>(val status:Status, val t:T?, val message:String) {
    enum class Status{
        SUCCESS,
        FAILURE,
        ERROR
    }
}