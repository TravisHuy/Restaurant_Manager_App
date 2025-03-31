package com.nhathuy.restaurant_manager_app.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.data.dto.OrderItemDTO
import com.nhathuy.restaurant_manager_app.data.model.Order
import com.nhathuy.restaurant_manager_app.data.model.OrderItem
import com.nhathuy.restaurant_manager_app.data.model.Status
import com.nhathuy.restaurant_manager_app.databinding.ItemOrderAdminBinding
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
/**
 * Adapter class for the orders admin recyclerview
 *
 * @return 0.1
 * @since 20-02-2025
 * @author TravisHuy
 */
class OrderItemAdminAdapter( private val onOrderClick: (Order) -> Unit,
                             private val onUpdateStatusClick: (Order) -> Unit):RecyclerView.Adapter<OrderItemAdminAdapter.OrderItemAdminViewModel>() {

    private var orders : List<Order> = listOf()
    private var orderItemsMap: Map<String, List<OrderItemDTO>> = mapOf()

    inner class OrderItemAdminViewModel(val binding:ItemOrderAdminBinding):RecyclerView.ViewHolder(binding.root){

        private val orderMenuItemAdapter = OrderMenuItemAdapter()

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onOrderClick(orders[position])
                }
            }

            binding.btnUpdateStatus.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onUpdateStatusClick(orders[position])
                }
            }

            binding.btnViewDetails.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onOrderClick(orders[position])
                }
            }

            binding.tvOrderItems.apply {
                adapter = orderMenuItemAdapter
                layoutManager = LinearLayoutManager(binding.root.context)
            }
        }


        fun bind(order: Order){
            binding.apply {
                tvOrderId.text = order.id
                tvOrderStatus.text = order.status.name
                tvCustomerName.text = order.customerName
                val context = binding.root.context
                tvOrderTotal.text = context.getString(R.string.total_amount,order.totalAmount)

//                val formattedTime = formatOrderTime(order.orderTime)
//                tvOrderTime.text = formattedTime
                tvOrderTime.text = formatDateTime(order.orderTime)

                when (order.status) {
                    Status.PENDING -> tvOrderStatus.setTextColor(root.context.getColor(R.color.status_pending))
                    Status.IN_PROCESS -> tvOrderStatus.setTextColor(root.context.getColor(R.color.status_in_process))
                    Status.COMPLETED -> tvOrderStatus.setTextColor(root.context.getColor(R.color.status_completed))
                    Status.PAID -> tvOrderStatus.setTextColor(root.context.getColor(R.color.status_paid))
                }



                val orderItems = orderItemsMap[order.id]
                orderMenuItemAdapter.updateOrderItem(orderItems)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderItemAdminAdapter.OrderItemAdminViewModel {
        val binding = ItemOrderAdminBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return OrderItemAdminViewModel(binding)
    }


    override fun onBindViewHolder(
        holder: OrderItemAdminAdapter.OrderItemAdminViewModel,
        position: Int
    ) {
        val order = orders[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int = orders.size

    fun setOrders(orders: List<Order>, orderItems: Map<String, List<OrderItemDTO>>) {
        this.orders = orders
        this.orderItemsMap = orderItems
        notifyDataSetChanged()
    }


    private fun formatDateTime(dateTimeStr:String):String{
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(dateTimeStr)
            outputFormat.format(date)
        }
        catch (e:Exception){
            dateTimeStr
        }
    }
}