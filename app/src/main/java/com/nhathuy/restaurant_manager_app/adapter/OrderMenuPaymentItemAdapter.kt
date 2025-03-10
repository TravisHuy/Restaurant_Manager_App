package com.nhathuy.restaurant_manager_app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.data.dto.MenuDTO
import com.nhathuy.restaurant_manager_app.databinding.OrderMenuPaymentItemBinding

class OrderMenuPaymentItemAdapter(private var menuItems :List<MenuDTO>) : RecyclerView.Adapter<OrderMenuPaymentItemAdapter.OrderPaymentMenuItemViewHolder>() {

    inner class OrderPaymentMenuItemViewHolder(val binding:OrderMenuPaymentItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(menuItem:MenuDTO){
            binding.apply {
                orderMenuItemName.text = menuItem.menuItemName
                orderMenuItemQuantity.text = "x${menuItem.quantity.toString()}"
                val context = binding.root.context
                orderMenuItemPrice.text = context.getString(R.string.price,menuItem.price)
            }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderMenuPaymentItemAdapter.OrderPaymentMenuItemViewHolder {
        val binding = OrderMenuPaymentItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return OrderPaymentMenuItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderPaymentMenuItemViewHolder, position: Int) {
        val menuItem = menuItems[position]
        holder.bind(menuItem)
    }

    override fun getItemCount(): Int = menuItems.size

}