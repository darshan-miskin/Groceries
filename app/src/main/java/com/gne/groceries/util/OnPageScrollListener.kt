package com.gne.groceries.util

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class OnPageScrollListener(private val layoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (dy > 0) {
            val pastVisiblesItems: Int
            val visibleItemCount: Int
            val totalItemCount: Int
            visibleItemCount = layoutManager.childCount
            totalItemCount = layoutManager.itemCount
            pastVisiblesItems = layoutManager.findFirstVisibleItemPosition()
            if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                loadNewData()
            }
        }
    }

    abstract fun loadNewData()
}