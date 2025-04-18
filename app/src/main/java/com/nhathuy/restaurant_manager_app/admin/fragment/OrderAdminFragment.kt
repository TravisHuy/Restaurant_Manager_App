package com.nhathuy.restaurant_manager_app.admin.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.adapter.OrderItemAdminAdapter
import com.nhathuy.restaurant_manager_app.data.dto.OrderItemDTO
import com.nhathuy.restaurant_manager_app.data.model.Order
import com.nhathuy.restaurant_manager_app.data.model.Status
import com.nhathuy.restaurant_manager_app.data.model.Table
import com.nhathuy.restaurant_manager_app.databinding.FragmentOrderAdminBinding
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.viewmodel.OrderItemViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.OrderViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.TableViewModel
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
    private val tableViewModel: TableViewModel by viewModels { viewModelFactory }

    private var currentOrder: Order? = null
    private var orderItems = mutableListOf<OrderItemDTO>()
    private val orderItemsMap = mutableMapOf<String, List<OrderItemDTO>>()
    private val tablesMap = mutableMapOf<String, Table>()
    private var orders = mutableListOf<Order>()
    private var filteredOrders = mutableListOf<Order>()
    private var currentStatus: Status? =null
    private var searchQuery: String = ""
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

        setupStatusTabs()
        setupRecyclerView()
        setupListeners()
        setupSearchView()
        observeViewModel()

        orderViewModel.getAllOrders()
    }

    private fun setupStatusTabs(){
        Status.values().forEach {
            status ->
            val tab = binding.orderStatusTabs.newTab().apply {
                text = status.name
                tag = status
            }
            binding.orderStatusTabs.addTab(tab)
        }
        val allTab = binding.orderStatusTabs.newTab().apply {
            text = "ALL"
            tag = null
        }
        binding.orderStatusTabs.addTab(allTab,0,true)

        binding.orderStatusTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                currentStatus = tab.tag as? Status
                filterOrders()
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })
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
    private fun setupSearchView() {
        binding.searchOrderInput.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                searchQuery = s.toString().trim().lowercase()
                filterOrders()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    private fun filterOrders(){
        filteredOrders.clear()

        val statusFiltered = if (currentStatus != null) {
            orders.filter { it.status == currentStatus }
        } else {
            orders
        }

        if (searchQuery.isNotEmpty()) {
            filteredOrders.addAll(statusFiltered.filter { order ->
                order.id.lowercase().contains(searchQuery) ||
                        order.customerName.lowercase().contains(searchQuery) ||
                        order.tableId.lowercase().contains(searchQuery) ||
                        orderItemsMap[order.id]?.any { orderItem ->
                            orderItem.menuItems.any { menuItem ->
                                menuItem.menuItemName.lowercase().contains(searchQuery)
                            }
                        } ?: false||

                        order.totalAmount.toString().contains(searchQuery) ||
                        order.status.name.lowercase().contains(searchQuery) ||
                        tablesMap[order.tableId]?.number?.toString()?.lowercase()?.contains(searchQuery) ?: false
            })
        } else {
            filteredOrders.addAll(statusFiltered)
        }

        if (tablesMap.isNotEmpty()) {
            adapter.setOrdersTable(filteredOrders, orderItemsMap, tablesMap)
        } else {
            adapter.setOrders(filteredOrders, orderItemsMap)
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
        tableViewModel.tablesByOrderId.observe(viewLifecycleOwner) {
            result ->
            when(result){
                is Resource.Success -> {
                    result?.data?.let {
                        table ->
                        val orderId = currentOrder?.id
                        if (orderId != null) {
                            tablesMap[orderId] = table
                        }
                        val currentIndex = orders.indexOfFirst { it.id == orderId }
                        if (currentIndex >= 0 && currentIndex < orders.size - 1) {
                            processNextOrder(currentIndex + 1)
                        } else {
                            adapter.setOrdersTable(orders, orderItemsMap, tablesMap)
                        }
                    }
                }
                is Resource.Error -> {
                    Log.e("Tables", "Error fetching table: ${result.message}")
                    val currentIndex = orders.indexOfFirst { it.id == currentOrder?.id }
                    if (currentIndex >= 0 && currentIndex < orders.size - 1) {
                        processNextOrder(currentIndex + 1)
                    } else {
                        adapter.setOrdersTable(orders, orderItemsMap, tablesMap)
                    }
                }
                else -> {}
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