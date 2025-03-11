package com.nhathuy.restaurant_manager_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nhathuy.restaurant_manager_app.data.dto.OrderItemDTO
import com.nhathuy.restaurant_manager_app.databinding.ItemOrderItemBinding

class OrderMenuItemAdapter : RecyclerView.Adapter<OrderMenuItemAdapter.OrderMenuItemViewHolder>() {

    private var orderItems = mutableListOf<OrderItemDTO>()

    inner class OrderMenuItemViewHolder(private val binding: ItemOrderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(orderItemDTO: OrderItemDTO?) {
            // Chỉ xử lý khi orderItemDTO không null
            orderItemDTO?.let { item ->
                binding.apply {
                    // Chỉ thiết lập adapter khi menuItems không null
                    item.menuItems?.let { menuItems ->
                        val adapter = OrderMenuPaymentItemAdapter(menuItems)
                        recMenuItem.layoutManager = LinearLayoutManager(itemView.context)
                        recMenuItem.adapter = adapter
                    }

                    if(item.note!!.isNotEmpty()){
                        tvNoteOrder.visibility = View.VISIBLE
                        tvOrderItemNote.text = item.note
                    }
                    else{
                        tvNoteOrder.visibility= View.GONE
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderMenuItemViewHolder {
        val binding = ItemOrderItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderMenuItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderMenuItemViewHolder, position: Int) {
        // Kiểm tra để đảm bảo position hợp lệ
        if (position in orderItems.indices) {
            holder.bind(orderItems[position])
        }
    }

    override fun getItemCount(): Int = orderItems.size

    fun updateOrderItem(newOrderItems: List<OrderItemDTO>?) {
        orderItems.clear()
        // Thêm kiểm tra null và lọc các item null
        newOrderItems?.filterNotNull()?.let {
            orderItems.addAll(it)
        }
        notifyDataSetChanged()
    }
}