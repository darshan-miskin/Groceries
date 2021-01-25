package com.gne.groceries.screens.main.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gne.groceries.R
import com.gne.groceries.databinding.LayoutRecyclerItemBinding
import com.gne.groceries.pojo.GroceryData
import com.gne.groceries.screens.main.GroceryDiffCallback

class GroceriesAdapter : PagedListAdapter<GroceryData,GroceriesAdapter.GroceriesHolder>(GroceryDiffCallback()) {
//    val list=ArrayList<GroceryData>()

//    fun setData(list:ArrayList<GroceryData>){
//        this.list.addAll(list)
//        notifyDataSetChanged()
//    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceriesHolder {
        val inflater=LayoutInflater.from(parent.context)
        val binding=DataBindingUtil.inflate<LayoutRecyclerItemBinding>(inflater, R.layout.layout_recycler_item,parent,false)
        return GroceriesHolder(binding)
    }

    override fun onBindViewHolder(holder: GroceriesHolder, position: Int) {
        holder.binding.grocery=getItem(position)
    }

    class GroceriesHolder(val binding: LayoutRecyclerItemBinding) : RecyclerView.ViewHolder(binding.root)
}