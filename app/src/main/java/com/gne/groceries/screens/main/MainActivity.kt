package com.gne.groceries.screens.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gne.groceries.R
import com.gne.groceries.databinding.ActivityMainBinding
import com.gne.groceries.pojo.Response
import com.gne.groceries.screens.main.adapters.GroceriesAdapter
import com.gne.groceries.util.OnPageScrollListener
import com.gne.groceries.util.isNetworkAvailable

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private val dateList= arrayOf("Date", "Asc", "Desc")
    private val priceList= arrayOf("Price", "Asc", "Desc")
    private lateinit var binding:ActivityMainBinding
    private val viewModel by viewModels<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this, R.layout.activity_main)

        val adapter=GroceriesAdapter()
        binding.recyclerMain.adapter=adapter

        viewModel.liveData.observe(this, { response ->
            adapter.submitList(response)
            binding.swipeMain.isRefreshing=false
        })

 /*       binding.swipeMain.setOnRefreshListener {
//            if(isNetworkAvailable(this))
//                viewModel.deleteAll()
//            else
                binding.swipeMain.isRefreshing=false
            viewModel.getDbData()
        }

        binding.spnrDate.adapter=ArrayAdapter(this,android.R.layout.simple_list_item_1,dateList)
        binding.spnrPrice.adapter=ArrayAdapter(this,android.R.layout.simple_list_item_1,priceList)


        binding.spnrDate.onItemSelectedListener= this
        binding.spnrPrice.onItemSelectedListener= this*/
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val id=p0?.id
     /*   when(id){
            R.id.spnr_date->{
                if(p2==0){
                    viewModel.getDbData()
                }
                else{
                    viewModel.dateFilterApplied(p2)
                    binding.spnrPrice.onItemSelectedListener=null
                    binding.spnrPrice.setSelection(0)
                    binding.spnrPrice.onItemSelectedListener=this
                }
            }
            R.id.spnr_price->{
                if(p2==0){
                    viewModel.getDbData()
                }
                else {
                    viewModel.priceFilterApplied(p2)
                    binding.spnrDate.onItemSelectedListener=null
                    binding.spnrDate.setSelection(0)
                    binding.spnrDate.onItemSelectedListener=this
                }
            }
        }*/
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}