package com.nhathuy.restaurant_manager_app.ui

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.adapter.MenuItemAdapter
import com.nhathuy.restaurant_manager_app.data.model.MenuItem
import com.nhathuy.restaurant_manager_app.databinding.ActivityMenuItemBinding
import com.nhathuy.restaurant_manager_app.databinding.AddMostNoteBinding
import com.nhathuy.restaurant_manager_app.oauth2.request.MenuItemRequest
import com.nhathuy.restaurant_manager_app.oauth2.request.OrderItemRequest
import com.nhathuy.restaurant_manager_app.oauth2.request.OrderRequest
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.viewmodel.MenuItemViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.OrderViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Activity for creating a new order.
 * This activity allows the user to create a new order by selecting menu items.
 *
 * @since 0.1
 * @version 20-02-2025
 * @author TravisHuy
 */
class MenuItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuItemBinding

    private lateinit var adapter: MenuItemAdapter

    private val selectedItems = mutableMapOf<String, Int>()

    private var tableId: String = ""
    private var customerName: String = ""
    private var orderId: String = ""

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val menuItemViewModel: MenuItemViewModel by viewModels { viewModelFactory }
    private val orderViewModel: OrderViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as RestaurantMangerApp).getRestaurantComponent().inject(this)

        tableId = intent.getStringExtra("TABLE_ID") ?: ""
        customerName = intent.getStringExtra("CUSTOMER_NAME") ?: ""
        orderId = intent.getStringExtra("ORDER_ID") ?: ""

        Log.d("MenuItemActivity", "tableId: $tableId")
        Log.d("MenuItemActivity", "customerName: $customerName")
        Log.d("MenuItemActivity", "orderId: $orderId")

        setupListeners()
        setupRecyclerview()
        observeViewModel()

        menuItemViewModel.getAllMenuItems()
    }

    private fun setupListeners() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.menuItemSwipeRefreshLayout.setOnRefreshListener {
            menuItemViewModel.getAllMenuItems()
        }
        binding.btnCancel.setOnClickListener {
            finish()
        }
        binding.btnConfirm.setOnClickListener {
            if (orderId.isNotBlank()) {
                addItemToOrders(orderId)
            } else {
                createOrderRequest()
            }
        }
    }

    private fun createOrderRequest() {
        val menuItems = menuItemViewModel.menuItemsState.value.data ?: return

        // Only get items with quantity > 0
        val selectedMenuItems = menuItems.filter { adapter.getQuantity(it.id) > 0 }

        if (selectedMenuItems.isEmpty()) {
            showError("Please select at least one item")
            return
        }

        // Create a list of MenuItemRequest objects
        val menuItemRequests = selectedMenuItems.map { menuItem ->
            MenuItemRequest(
                menuItemId = menuItem.id,
                quantity = adapter.getQuantity(menuItem.id)
            )
        }

        // Create a single OrderItemRequest that contains all menu items
        // This matches the server's expectation based on OrderServiceImpl.createOrder
        val orderItemRequest = OrderItemRequest(
            menuItems = menuItemRequests,
            note = ""
        )

        // Create the order request with a list containing the single OrderItemRequest
        val orderRequest = OrderRequest(
            tableId = tableId,
            customerName = customerName,
            items = listOf(orderItemRequest)
        )

        // Submit the order
        Log.d("MenuItemActivity", "Creating order with: $orderRequest")
        orderViewModel.createOrder(orderRequest)
        observeOrderCreation()
    }

    private fun addItemToOrders(orderId: String) {
        val menuItems = menuItemViewModel.menuItemsState.value.data ?: return

        // Only get items with quantity > 0
        val selectedMenuItems = menuItems.filter { adapter.getQuantity(it.id) > 0 }

        if (selectedMenuItems.isEmpty()) {
            showError("Please select at least one item")
            return
        }

        // Create a list of MenuItemRequest objects
        val menuItemRequests = selectedMenuItems.map { menuItem ->
            MenuItemRequest(
                menuItemId = menuItem.id,
                quantity = adapter.getQuantity(menuItem.id)
            )
        }

        // Create a single OrderItemRequest that contains all selected menu items
        val orderItemRequest = OrderItemRequest(
            menuItems = menuItemRequests,
            note = ""
        )

        // Add items to the order
        Log.d("MenuItemActivity", "Adding items to order $orderId: $orderItemRequest")
        orderViewModel.addItemsOrder(orderId, listOf(orderItemRequest))
        observeOrderItemCreation()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            menuItemViewModel.menuItemsState.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        showLoading(true)
                    }
                    is Resource.Success -> {
                        resource.data?.let { menuItems ->
                            adapter.updateMenuItems(menuItems)
                            showLoading(false)
                        }
                    }
                    is Resource.Error -> {
                        showError(resource.message ?: "An unknown error occurred")
                        Log.d("MenuItemActivity", "Error loading menu items: ${resource.message}")
                        showLoading(false)
                    }
                }
            }
        }
    }

    private fun observeOrderCreation() {
        lifecycleScope.launch {
            orderViewModel.orderState.collect { resource ->
                Log.d("MenuItemActivity", "Order state: $resource")
                when (resource) {
                    is Resource.Loading -> {
                        showLoading(true)
                    }
                    is Resource.Success -> {
                        showLoading(false)
                        resource.data?.let { orderResponse ->
                            Log.d("MenuItemActivity", "Order created successfully: ${orderResponse.id}")
                            Toast.makeText(
                                this@MenuItemActivity,
                                "Order created: ID ${orderResponse.id}",
                                Toast.LENGTH_SHORT
                            ).show()

                            val resultIntent = Intent()
                            resultIntent.putExtra("ORDER_ID", orderResponse.id)
                            setResult(RESULT_OK, resultIntent)
                            finish()
                        }
                    }
                    is Resource.Error -> {
                        showLoading(false)
                        val errorMsg = resource.message ?: "An error occurred"
                        Log.e("MenuItemActivity", "Error creating order: $errorMsg")
                        showError(errorMsg)
                    }
                    else -> {
                        // Do nothing for initial state
                    }
                }
            }
        }
    }

    private fun observeOrderItemCreation() {
        lifecycleScope.launch {
            orderViewModel.addOrderItemResult.collect { resource ->
                Log.d("MenuItemActivity", "Add order item state: $resource")
                when (resource) {
                    is Resource.Loading -> {
                        showLoading(true)
                    }
                    is Resource.Success -> {
                        showLoading(false)
                        resource.data?.let { orderResponse ->
                            Log.d("MenuItemActivity", "Items added to order: ${orderResponse.id}")
                            // Create an intent with the order ID
                            val resultIntent = Intent().apply {
                                putExtra("ORDER_ID", orderResponse.id)
                                putExtra("TABLE_ID", tableId)
                            }

                            // Set the result as OK and attach the intent
                            setResult(RESULT_OK, resultIntent)

                            // Show a toast to confirm items were added
                            Toast.makeText(
                                this@MenuItemActivity,
                                "Items added to order: ID ${orderResponse.id}",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Close the current activity and return to the previous screen
                            finish()
                        }
                    }
                    is Resource.Error -> {
                        showLoading(false)
                        val errorMsg = resource.message ?: "An error occurred"
                        Log.e("MenuItemActivity", "Error adding items to order: $errorMsg")
                        showError(errorMsg)
                    }
                    else -> {
                        // Do nothing for initial state
                    }
                }
            }
        }
    }

    private fun showLoading(loading: Boolean) {
        binding.menuItemSwipeRefreshLayout.isRefreshing = loading
    }

    private fun setupRecyclerview() {
        adapter = MenuItemAdapter(
            onMenuItemClick = { menuItem ->
                handleItemClick(menuItem)
            },
            onAddClick = { menuItem ->
                updateItemQuantity(menuItem, adapter.getQuantity(menuItem.id))
            },
            onMinusClick = { menuItem ->
                updateItemQuantity(menuItem, adapter.getQuantity(menuItem.id))
            },
            onQuantityChanged = { menuItem, newQuantity ->
                updateItemQuantity(menuItem, newQuantity)
            },
            onLongPress = { menuItem ->
                handleLongPress(menuItem)
            },
            onNoteButtonClick = { menuItem ->
                showNoteDialog(menuItem)
            }
        )
        binding.menuItemRec.layoutManager = LinearLayoutManager(this)
        binding.menuItemRec.adapter = adapter
    }

    private fun handleItemClick(menuItem: MenuItem) {
        val quantity = adapter.getQuantity(menuItem.id)
        updateItemQuantity(menuItem, quantity)

        if (adapter.getSelectItems().isNotEmpty()) {
            updateSelectionUI()
        } else {
            showItemDetails(menuItem)
        }
    }

    private fun handleLongPress(menuItem: MenuItem) {
        updateSelectionUI()
    }

    private fun updateSelectionUI() {
        val selectedCount = adapter.getSelectItems().size
        if (selectedCount > 0) {
            binding.toolbar.title = "$selectedCount selected"
        } else {
            binding.toolbar.title = "Create Order"
        }
    }

    private fun showItemDetails(menuItem: MenuItem) {
        Toast.makeText(this, "Show item details", Toast.LENGTH_SHORT).show()
    }

    private fun updateItemQuantity(menuItem: MenuItem, quantity: Int) {
        if (quantity > 0) {
            selectedItems[menuItem.id] = quantity
        } else {
            selectedItems.remove(menuItem.id)
        }

        Log.d("MenuItemActivity", "Selected items: ${selectedItems.size}, IDs: ${selectedItems.keys}")
        updateOrderSummary()
    }

    private fun updateOrderSummary() {
        val totalPrice = selectedItems.entries.sumOf { (itemId, quantity) ->
            menuItemViewModel.menuItemsState.value.data?.find { it.id == itemId }?.price?.times(quantity) ?: 0.0
        }

        // You can update UI elements with the total price info if needed
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun showNoteDialog(menuItem: MenuItem) {
        val dialog = Dialog(this)
        val dialogBinding = AddMostNoteBinding.inflate(LayoutInflater.from(this))
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.tvNote.text = "Add note for ${menuItem.name}"

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnConfirm.setOnClickListener {
            val note = dialogBinding.edNumberMenu.text.toString()

            if (note.isNotEmpty()) {
                menuItemViewModel.addNoteMenuItem(menuItem.id, note)
                Toast.makeText(this, "Added note: $note", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Note cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.CENTER)
    }
}