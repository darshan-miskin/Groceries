package com.gne.groceries.screens.main

import androidx.recyclerview.widget.DiffUtil
import com.gne.groceries.pojo.GroceryData

class GroceryDiffCallback : DiffUtil.ItemCallback<GroceryData>() {
    override fun areItemsTheSame(oldItem: GroceryData, newItem: GroceryData): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GroceryData, newItem: GroceryData): Boolean {
        return oldItem == newItem
    }
}