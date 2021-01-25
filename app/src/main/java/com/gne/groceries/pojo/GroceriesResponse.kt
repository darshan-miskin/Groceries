package com.gne.groceries.pojo

import androidx.paging.PagedList

data class GroceriesResponse(
    val message: String="",
    val version: String="",
    val status: String="",
    val total: Int=0,
    val count: Int=0,
    val limit: String="",
    val offset: String="",
    val records:ArrayList<GroceryData>,
    val data:PagedList<GroceryData>
)