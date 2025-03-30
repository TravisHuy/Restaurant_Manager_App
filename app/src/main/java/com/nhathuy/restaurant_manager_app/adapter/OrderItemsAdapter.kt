package com.nhathuy.restaurant_manager_app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nhathuy.restaurant_manager_app.data.model.OrderItem
import com.nhathuy.restaurant_manager_app.databinding.OrderItemsLayoutBinding

class OrderItemsAdapter : RecyclerView.Adapter<OrderItemsAdapter.OrderItemViewHolder>() {

    private var orderItems: List<OrderItem.OrderItemDetail> = emptyList()

    fun setOrderItems(items: List<OrderItem.OrderItemDetail>) {
        this.orderItems = items
        notifyDataSetChanged()
    }

    inner class OrderItemViewHolder(private val binding: OrderItemsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(orderItemDetail: OrderItem.OrderItemDetail) {
            binding.tvQuantity.text = "${orderItemDetail.quantity}x"
            binding.tvMenuName.text = orderItemDetail.menuItemName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val binding = OrderItemsLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        val orderItem = orderItems[position]
        holder.bind(orderItem)
    }

    override fun getItemCount(): Int = orderItems.size
}