package com.nhathuy.restaurant_manager_app.admin.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.adapter.OrderItemAdminAdapter
import com.nhathuy.restaurant_manager_app.data.dto.OrderItemDTO
import com.nhathuy.restaurant_manager_app.data.model.Order
import com.nhathuy.restaurant_manager_app.databinding.FragmentOrderAdminBinding
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.viewmodel.OrderItemViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.OrderViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ViewModelFactory
import javax.inject.Inject

/**
 * OrderAdminFragment
 *
 * This fragment is responsible for displaying and managing the order
 * layout in the admin panel.
 *
 * @version 0.1
 * @author TravisHuy
 * @since 31.3.2025
 */

class OrderAdminFragment : Fragment() {

    private lateinit var binding: FragmentOrderAdminBinding
    private lateinit var adapter: OrderItemAdminAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val orderViewModel: OrderViewModel by viewModels { viewModelFactory }
    private val orderItemViewModel: OrderItemViewModel by viewModels { viewModelFactory }

    private var currentOrder: Order? = null
    private var orderItems = mutableListOf<OrderItemDTO>()
    private val orderItemsMap = mutableMapOf<String, List<OrderItemDTO>>()
    private var orders = mutableListOf<Order>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as RestaurantMangerApp).getRestaurantComponent().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
        observeViewModel()

        orderViewModel.getAllOrders()
    }

    private fun setupRecyclerView() {
        adapter = OrderItemAdminAdapter(
            onOrderClick = { order ->
                navigateToOrderDetail(order)
            },
            onUpdateStatusClick = { order ->
                showUpdateStatusDialog(order)
            }
        )
        binding.recyclerViewOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewOrders.adapter = adapter
    }

    private fun setupListeners() {
        binding.swipeRefreshOrderLayout.setOnRefreshListener {
            orderViewModel.getAllOrders()
        }
    }

    private fun observeViewModel() {
        orderViewModel.orderItems.observe(viewLifecycleOwner) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    showLoading(false)
                    resource.data?.let { ordersList ->
                        Log.d("OrderAdmin", "Received ${ordersList.size} orders")
                        orders.clear()
                        orders.addAll(ordersList)

                        // Process each order sequentially
                        if (orders.isNotEmpty()) {
                            processNextOrder(0)
                        } else {
                            adapter.setOrders(orders, orderItemsMap)
                        }
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), "Error: ${resource.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        orderItemViewModel.allOrderItem.observe(viewLifecycleOwner) { result ->
            when(result) {
                is Resource.Loading -> {
                    // Show loading indicator if needed
                }
                is Resource.Success -> {
                    result.data?.let { items ->
                        val orderId = currentOrder?.id

                        if (orderId != null) {
                            Log.d("OrderItem", "Received ${items.size} items for order $orderId")
                            orderItems.clear()
                            orderItems.addAll(items)

                            // Store the items for this order
                            orderItemsMap[orderId] = items.toList()

                            // Process next order if available
                            val currentIndex = orders.indexOfFirst { it.id == orderId }
                            if (currentIndex >= 0 && currentIndex < orders.size - 1) {
                                processNextOrder(currentIndex + 1)
                            } else {
                                // All orders processed, update the adapter
                                adapter.setOrders(orders, orderItemsMap)
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    Log.e("OrderItem", "Error fetching items: ${result.message}")
                    Toast.makeText(requireContext(), "Error ${result.message}", Toast.LENGTH_SHORT).show()

                    // Continue with next order even if there's an error
                    val currentIndex = orders.indexOfFirst { it.id == currentOrder?.id }
                    if (currentIndex >= 0 && currentIndex < orders.size - 1) {
                        processNextOrder(currentIndex + 1)
                    } else {
                        // All orders processed, update the adapter
                        adapter.setOrders(orders, orderItemsMap)
                    }
                }
            }
        }
    }

    private fun processNextOrder(index: Int) {
        if (index < orders.size) {
            val order = orders[index]
            fetchOrderItems(order)
        }
    }

    private fun fetchOrderItems(order: Order) {
        if (order.orderItemIds.isNotEmpty()) {
            currentOrder = order
            orderItemViewModel.getListOrderItem(order.orderItemIds)
            Log.d("OrderItem", "Fetching items for order ${order.id}: ${order.orderItemIds}")
        } else {
            // Skip to next order if this one has no items
            val currentIndex = orders.indexOfFirst { it.id == order.id }
            if (currentIndex >= 0 && currentIndex < orders.size - 1) {
                processNextOrder(currentIndex + 1)
            } else {
                // All orders processed, update the adapter
                adapter.setOrders(orders, orderItemsMap)
            }
        }
    }

    private fun showUpdateStatusDialog(order: Order) {
        // Implementation for status update dialog
    }

    private fun navigateToOrderDetail(order: Order) {
        // Implementation for navigation to order detail
    }

    private fun showLoading(isLoading: Boolean) {
        binding.swipeRefreshOrderLayout.isRefreshing = isLoading
    }
}