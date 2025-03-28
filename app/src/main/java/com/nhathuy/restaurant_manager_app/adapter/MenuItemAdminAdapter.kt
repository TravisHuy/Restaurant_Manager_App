package com.nhathuy.restaurant_manager_app.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nhathuy.restaurant_manager_app.data.model.MenuItem
import com.nhathuy.restaurant_manager_app.databinding.ItemMenuAdminBinding
/**
 * Adapter for RecyclerView to display list of menuItems.
 *
 * @return 0.1
 * @since 28-03-2025
 * @author TravisHuy
 */
class MenuItemAdminAdapter():RecyclerView.Adapter<MenuItemAdminAdapter.MenuItemAdminViewHolder>() {

    private var menuItems : List<MenuItem> = listOf()

    inner class MenuItemAdminViewHolder(val binding:ItemMenuAdminBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(menuItem: MenuItem){
            binding.apply {
                menuItemName.text = menuItem.name
                menuItemPrice.text = menuItem.price.toString()
                val imageData = menuItem.imageData
                if(imageData != null) {
                    val imageByteArray = android.util.Base64.decode(imageData, android.util.Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
                    imageMenu.setImageBitmap(bitmap)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemAdminViewHolder {
        val binding = ItemMenuAdminBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MenuItemAdminViewHolder(binding)
    }

    override fun getItemCount(): Int = menuItems.size

    override fun onBindViewHolder(holder: MenuItemAdminViewHolder, position: Int) {
        val menuItem = menuItems[position]
        holder.bind(menuItem)
    }
    fun updateMenuItem(newMenuItems:List<MenuItem>){
        menuItems= newMenuItems
        notifyDataSetChanged()
    }
}