package com.nhathuy.restaurant_manager_app.adapter

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nhathuy.restaurant_manager_app.data.model.MenuItem
import com.nhathuy.restaurant_manager_app.databinding.TestItemBinding

class TestAdapter() :RecyclerView.Adapter<TestAdapter.TestViewHolder>() {
    private var menuItems: List<MenuItem> = listOf()

    inner class TestViewHolder(val itemTestItemBinding: TestItemBinding):RecyclerView.ViewHolder(itemTestItemBinding.root){
        fun bind(menuItem: MenuItem){
            itemTestItemBinding.apply {
                val imageData = menuItem.imageData
                if(imageData != null){
                    val imageByteArray = Base64.decode(imageData, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageByteArray,0,imageByteArray.size)
                    imageTest.setImageBitmap(bitmap)
                }

                nameTest.text = menuItem.name
                priceTest.text = menuItem.price.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestAdapter.TestViewHolder {
        val binding = TestItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TestAdapter.TestViewHolder, position: Int) {
        val menuItem = menuItems[position]
        holder.bind(menuItem)
    }

    override fun getItemCount(): Int = menuItems.size

    fun update(newMenuItems: List<MenuItem>) {
        menuItems = newMenuItems
        notifyDataSetChanged()
    }
}