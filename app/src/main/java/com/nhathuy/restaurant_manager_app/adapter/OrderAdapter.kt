package com.nhathuy.restaurant_manager_app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nhathuy.restaurant_manager_app.data.model.Order
import com.nhathuy.restaurant_manager_app.databinding.ItemOrderBinding

/**
 * Adapter class for the orders recyclerview
 *
 * @return 0.1
 * @since 20-02-2025
 * @author TravisHuy
 */
class OrderAdapter(private val onOrderClick: (Order) -> Unit):RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {
    private var orders = mutableListOf<Order>()

    inner class OrderViewHolder(val binding: ItemOrderBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(order: Order,position: Int){
            binding.apply {
                orderCustomerName.text = order.customerName
                orderPriceQuantity.text = "${order.orderItemIds.size} items"
                orderTotalPrice.text = "$${order.totalAmount}"

                root.setOnClickListener {
                    onOrderClick(order)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order,position)
    }
    override fun getItemCount(): Int = orders.size

    fun updateOrders(newOrders: List<Order>) {
        orders.clear()
        orders.addAll(newOrders)
        notifyDataSetChanged()
    }
}