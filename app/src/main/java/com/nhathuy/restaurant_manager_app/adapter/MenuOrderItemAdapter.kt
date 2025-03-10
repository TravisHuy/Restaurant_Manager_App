package com.nhathuy.restaurant_manager_app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nhathuy.restaurant_manager_app.data.dto.MenuDTO
import com.nhathuy.restaurant_manager_app.data.dto.MenuItemDTO
import com.nhathuy.restaurant_manager_app.databinding.OrderMenuItemBinding

class MenuOrderItemAdapter(private val menuItems:List<MenuDTO>):RecyclerView.Adapter<MenuOrderItemAdapter.MenuOrderItemViewHolder>() {

    inner class MenuOrderItemViewHolder(val binding : OrderMenuItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(menuItem:MenuDTO){
            binding.apply {
                orderMenuItemName.text = menuItem.menuItemName
                orderMenuItemQuantity.text = "x${menuItem.quantity.toString()}"
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MenuOrderItemAdapter.MenuOrderItemViewHolder {
        val binding =OrderMenuItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MenuOrderItemViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MenuOrderItemAdapter.MenuOrderItemViewHolder,
        position: Int
    ) {
        val menuItem = menuItems[position]
        holder.bind(menuItem)
    }

    override fun getItemCount(): Int = menuItems.size

}