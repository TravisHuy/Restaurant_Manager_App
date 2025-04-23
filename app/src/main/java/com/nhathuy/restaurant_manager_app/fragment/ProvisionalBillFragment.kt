package com.nhathuy.restaurant_manager_app.fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.format.DateUtils.formatDateTime
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.adapter.OrderMenuItemAdapter
import com.nhathuy.restaurant_manager_app.data.dto.OrderItemDTO
import com.nhathuy.restaurant_manager_app.data.model.Order
import com.nhathuy.restaurant_manager_app.databinding.FragmentProvisionalBillBinding
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.viewmodel.InvoiceViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.OrderItemViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.OrderViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.PaymentViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

/**
 * Fragment responsible for displaying a provisional bill to customer
 * Used to provide a quick overview of the total cost before final checkout.
 *
 * @return 0.1
 * @version 20-04-2025
 * @author TravisHuy
 */

class ProvisionalBillFragment : DialogFragment() {
    private var _binding : FragmentProvisionalBillBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val paymentViewModel: PaymentViewModel by viewModels { viewModelFactory }
    private val orderViewModel: OrderViewModel by viewModels { viewModelFactory }
    private val orderItemViewModel : OrderItemViewModel by viewModels { viewModelFactory }
    private val invoiceViewModel : InvoiceViewModel by viewModels { viewModelFactory }

    private lateinit var adapter: OrderMenuItemAdapter
    private var orderItems = mutableListOf<OrderItemDTO>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as RestaurantMangerApp).getRestaurantComponent().inject(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,R.style.FullScreenDialog)
    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            dialog ->
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width,height)
            dialog.window?.setWindowAnimations(R.style.DialogAnimation)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProvisionalBillBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val orderId = arguments?.getString("orderId")
        val tableNumber = arguments?.getString("tableNumber")


        setupListeners()
        setupRecyclerview()
        observeViewModel()

        binding.tvTableNumber.text = "Table $tableNumber"
        orderViewModel.getOrderById(orderId!!)
        if (orderId != null) {
            paymentViewModel.createVnPayQrPayment(orderId)
        }
    }
    private fun setupListeners(){

    }
    private fun setupRecyclerview(){
        adapter = OrderMenuItemAdapter()
        binding.recOrderMenuItem.layoutManager = LinearLayoutManager(requireContext())
        binding.recOrderMenuItem.adapter = adapter
    }
    private fun observeViewModel(){
        setupOrderViewModel()
        setupOrderItemViewModel()
        setupPaymentViewModel()
    }

    private fun setupOrderViewModel(){
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
                    Toast.makeText(requireContext(),"Error ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupOrderItemViewModel(){
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
                    Toast.makeText(requireContext(), "Error ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun setupPaymentViewModel(){
        paymentViewModel.qrCodePaymentResponse.observe(this) {
            result ->
            when(result) {
                is Resource.Loading -> {
                    // Show loading indicator if needed
                }
                is Resource.Success -> {
                    result.data?.let { qr ->
                        val qrBitMap = decodeBase64ToBitMap(qr.qrCodeImage)
                        binding.qrProvisionalBill.setImageBitmap(qrBitMap)
                    }
                }
                is Resource.Error -> {
                    Log.e("OrderItem", "Error fetching items: ${result.message}")
                    Toast.makeText(requireContext(), "Error ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun decodeBase64ToBitMap(qrCodeImage: String):Bitmap {
        val base64Data = if(qrCodeImage.contains(",")){
            qrCodeImage.split(",")[1]
        }
        else{
            qrCodeImage
        }

        val imageByteArray = Base64.decode(base64Data, Base64.DEFAULT)

        Log.d("QR_CODE", "QR code size: ${imageByteArray.size} bytes")

        return  BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
    }

    private fun showInformation(order: Order) {
        binding.apply {
            tvOrderId.text = order.id
            tvOrderCustomerName.text = order.customerName
            tvOrderCreateTime.text= formatDateTime(order.orderTime)
            provisionalPrice.text = getString(R.string.price,order.totalAmount)
            provisionalTotalPrice.text = getString(R.string.price,order.totalAmount)
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