package com.nhathuy.restaurant_manager_app.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.adapter.MenuItemAdapter
import com.nhathuy.restaurant_manager_app.data.model.MenuItem
import com.nhathuy.restaurant_manager_app.databinding.ActivityCreateOderBinding
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.viewmodel.AuthViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.MenuItemViewModel
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
class CreateOderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateOderBinding

    private lateinit var adapter: MenuItemAdapter

    private val selectedItems = mutableMapOf<String, Int>()
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val menuItemViewModel: MenuItemViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateOderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as RestaurantMangerApp).getRestaurantComponent().inject(this)

        setupListeners()
        setupRecyclerview()
        observeViewModel()

        menuItemViewModel.getAllMenuItems()

    }
    private fun setupListeners(){
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    private fun observeViewModel(){
        lifecycleScope.launch {
            menuItemViewModel.allMenuItems.collect { resource ->
                when(resource){
                    is Resource.Loading -> {

                    }
                    is Resource.Success -> {
                        resource.data?.let {
                            menuItems ->
                            adapter.updateMenuItems(menuItems)
                        }
                    }
                    is Resource.Error -> {
                        showError(resource.message ?: "An unknown error occurred")
                        Log.d("CreateOrderActivity", "Error: ${resource.message}")
                    }
                }
            }
        }
    }
    private fun setupRecyclerview(){
        adapter = MenuItemAdapter(
            onMenuItemClick = {
                menuItem -> showItemDetails(menuItem)
            },
            onAddClick = {
                menuItem -> updateItemQuantity(menuItem,adapter.getQuantity(menuItem.id))
            },
            onMinusClick = {
                menuItem -> updateItemQuantity(menuItem, adapter.getQuantity(menuItem.id))
            },
            onQuantityChanged = {
                menuItem, newQuantity ->
                updateItemQuantity(menuItem, newQuantity)
            }
        )
        binding.menuItemRec.layoutManager = LinearLayoutManager(this)
        binding.menuItemRec.adapter = adapter
    }

    private fun showItemDetails(menuItem: MenuItem){
        Toast.makeText(this, "Show item details", Toast.LENGTH_SHORT).show()
    }
    private fun updateItemQuantity(menuItem: MenuItem, quantity: Int) {
        if (quantity > 0) {
            selectedItems[menuItem.id] = quantity
        } else {
            selectedItems.remove(menuItem.id)
        }
        updateOrderSummary()
    }

    private fun updateOrderSummary() {
        // Calculate total price and update UI
        val totalPrice = selectedItems.entries.sumOf { (itemId, quantity) ->
            menuItemViewModel.allMenuItems.value.data?.find { it.id == itemId }?.price?.times(quantity) ?: 0.0
        }

        // Update UI with total price and selected items
        // This would be implemented based on your UI requirements
    }

    private fun showError(message: String){
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}