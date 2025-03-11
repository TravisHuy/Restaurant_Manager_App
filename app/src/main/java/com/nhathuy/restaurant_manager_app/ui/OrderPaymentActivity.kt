package com.nhathuy.restaurant_manager_app.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.adapter.OrderMenuItemAdapter
import com.nhathuy.restaurant_manager_app.data.dto.OrderItemDTO
import com.nhathuy.restaurant_manager_app.data.model.Order
import com.nhathuy.restaurant_manager_app.databinding.ActivityOrderPaymentBinding
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.viewmodel.OrderItemViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.OrderViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class OrderPaymentActivity : AppCompatActivity() {
    private lateinit var binding : ActivityOrderPaymentBinding
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val orderViewModel: OrderViewModel by viewModels { viewModelFactory }
    private val orderItemViewModel : OrderItemViewModel by viewModels { viewModelFactory }

    private lateinit var adapter: OrderMenuItemAdapter
    private var orderItems = mutableListOf<OrderItemDTO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as RestaurantMangerApp).getRestaurantComponent().inject(this)

        val orderId = intent.getStringExtra("ORDER_ID")
        val tableNumber = intent.getStringExtra("TABLE_NUMBER")

        setupListeners()
        setupRecyclerview()
        observeViewModel()

        binding.tvTableNumber.text = "Table ${tableNumber}"
        orderViewModel.getOrderById(orderId!!)
    }

    private fun setupListeners(){

    }
    private fun setupRecyclerview(){
        adapter = OrderMenuItemAdapter()
        binding.recOrderMenuItem.layoutManager = LinearLayoutManager(this)
        binding.recOrderMenuItem.adapter = adapter
    }
    private fun observeViewModel(){
        orderViewModel.orderId.observe(this){
                result ->
            when(result) {
                is Resource.Loading ->{

                }
                is Resource.Success -> {
                    result.data?.let {
                            order ->
                        showInformation(order)
                        orderItems.clear()
                        Log.d("OrderItem","order item ${order.orderItemIds}")

                        if (order.orderItemIds.isNotEmpty()) {
                            orderItemViewModel.getListOrderItem(order.orderItemIds)
                            Log.d("OrderItem", "Fetching all items: ${order.orderItemIds}")
                        }

                    }
                }
                is Resource.Error -> {
                    Toast.makeText(this,"Error ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        orderItemViewModel.allOrderItem.observe(this) { result ->
            when(result) {
                is Resource.Loading -> {
                    // Show loading indicator if needed
                }
                is Resource.Success -> {
                    result.data?.let { items ->
                        orderItems.clear()
                        orderItems.addAll(items)
                        adapter.updateOrderItem(orderItems)
                        Log.d("OrderItem", "All items fetched successfully: ${items.size}")
                    }
                }
                is Resource.Error -> {
                    Log.e("OrderItem", "Error fetching items: ${result.message}")
                    Toast.makeText(this, "Error ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun showInformation(order: Order){
        binding.apply {
            tvOrderId.text = order.id
            tvOrderCustomerName.text = order.customerName
            tvOrderCreateTime.text= formatDateTime(order.orderTime)
            tvOrderTotalAmount.text = getString(R.string.price,order.totalAmount)
        }
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