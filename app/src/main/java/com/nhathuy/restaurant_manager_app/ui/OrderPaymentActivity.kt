package com.nhathuy.restaurant_manager_app.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.adapter.OrderMenuItemAdapter
import com.nhathuy.restaurant_manager_app.data.dto.OrderItemDTO
import com.nhathuy.restaurant_manager_app.data.model.Order
import com.nhathuy.restaurant_manager_app.data.model.PaymentMethod
import com.nhathuy.restaurant_manager_app.databinding.ActivityOrderPaymentBinding
import com.nhathuy.restaurant_manager_app.fragment.ProvisionalBillFragment
import com.nhathuy.restaurant_manager_app.oauth2.request.InvoiceRequest
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.viewmodel.InvoiceViewModel
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
    private val invoiceViewModel : InvoiceViewModel by viewModels { viewModelFactory }

    private lateinit var adapter: OrderMenuItemAdapter
    private var orderItems = mutableListOf<OrderItemDTO>()

    private var selectedPaymentMethod: PaymentMethod? = null
    private var currentOrder: Order? = null
    private var orderId:String? = null
    private var tableNumber:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as RestaurantMangerApp).getRestaurantComponent().inject(this)

        orderId = intent.getStringExtra("ORDER_ID")
        tableNumber = intent.getStringExtra("TABLE_NUMBER")

        setupListeners()
        setupRecyclerview()
        observeViewModel()

        binding.tvTableNumber.text = "Table ${tableNumber}"
        orderViewModel.getOrderById(orderId!!)
    }

    private fun setupListeners(){
        binding.apply {
            cardCash.setOnClickListener { selectPaymentMethod(PaymentMethod.CASH, cardCash, cardCredit, cardOnline) }
            cardCredit.setOnClickListener { selectPaymentMethod(PaymentMethod.CARD, cardCredit, cardCash, cardOnline) }
            cardOnline.setOnClickListener { selectPaymentMethod(PaymentMethod.ONLINE, cardOnline, cardCash, cardCredit) }

            btnPay.setOnClickListener {
                if (selectedPaymentMethod == null) {
                    Toast.makeText(this@OrderPaymentActivity, "Please select a payment method", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
//                showConfirmationDialog()

                navigateToProvisionalBill()
            }

            // Setup toolbar back button
            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }
    private fun selectPaymentMethod(method: PaymentMethod, selectedCard: MaterialCardView, vararg otherCards: MaterialCardView) {
        selectedPaymentMethod = method
        selectedCard.isChecked = true
        selectedCard.strokeColor = getColor(R.color.blue_500)
        binding.btnPay.isEnabled = true

        otherCards.forEach { card ->
            card.isChecked = false
            card.strokeColor = getColor(R.color.grey_300)
        }
    }
    private fun navigateToProvisionalBill(){
        try {
            val fragment = ProvisionalBillFragment().apply {
                arguments = Bundle().apply {
                    putString("orderId",orderId)
                    putString("tableNumber",tableNumber)
                }
            }
            fragment.show(supportFragmentManager,"ProvisionalBillDialog")
        }
        catch (e:Exception){
            Log.e("OrderPaymentActivity","Error navigating to provisional bill: ${e.message}")
            Toast.makeText(this, "Error navigating to payment screen", Toast.LENGTH_SHORT).show()
        }
    }
    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Confirm Payment")
            .setMessage("Are you sure you want to complete this payment?")
            .setPositiveButton("Confirm") { _, _ ->
                processPayment()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun processPayment(){
        currentOrder?.let { order ->
            selectedPaymentMethod?.let { paymentMethod ->
                val invoiceRequest = InvoiceRequest(
                    orderId = order.id,
                    totalAmount = order.totalAmount,
                    paymentMethod = paymentMethod
                )
                invoiceViewModel.addInvoice(invoiceRequest)
            }
        }
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
                        currentOrder = order
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
        invoiceViewModel.addInvoice.observe(this) {
            result ->
            when(result) {
                is Resource.Loading -> {
                    showLoading(true)
                    binding.btnPay.isEnabled = false
                }
                is Resource.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "Payment completed successfully", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
                is Resource.Error -> {
                    showLoading(false)
                    binding.btnPay.isEnabled = true
                    Log.d("OrderPaymentActivity","${result.message}")
                    Toast.makeText(this, "Payment failed: ${result.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    private fun showLoading(show: Boolean) {
        binding.progressBar?.visibility = if (show) View.VISIBLE else View.GONE
    }
    private fun showInformation(order: Order){
        binding.apply {
            tvOrderId.text = order.id
            tvOrderCustomerName.text = order.customerName
            tvOrderCreateTime.text= formatDateTime(order.orderTime)
            tvOrderTotalAmount.text = getString(R.string.price,order.totalAmount)
            btnPay.isEnabled = false
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