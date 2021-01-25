package com.gne.groceries.pojo

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "groceries")
data class GroceryData(
    val timestamp: Int,
    val state: String,
    val district: String,
    val market: String,
    val commodity: String,
    val variety: String,
    val arrival_date: String,
    val min_price: Int,
    val max_price: Int,
    val modal_price: Int
){
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0

    enum class ColumnNames{
        modal_price, timestamp
    }
}
