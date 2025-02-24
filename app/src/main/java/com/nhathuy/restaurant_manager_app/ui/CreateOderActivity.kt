package com.nhathuy.restaurant_manager_app.ui

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.imageLoader
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.adapter.MenuItemAdapter
import com.nhathuy.restaurant_manager_app.data.model.MenuItem
import com.nhathuy.restaurant_manager_app.databinding.ActivityCreateOderBinding
import com.nhathuy.restaurant_manager_app.databinding.AddMostNoteBinding
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
        binding.menuItemSwipeRefreshLayout.setOnRefreshListener {
            menuItemViewModel.getAllMenuItems()
        }
    }
    private fun observeViewModel(){
        lifecycleScope.launch {
            menuItemViewModel.menuItemsState.collect { resource ->
                when(resource){
                    is Resource.Loading -> {
                        showLoading(true)
                    }
                    is Resource.Success -> {
                        resource.data?.let {
                            menuItems ->
                            adapter.updateMenuItems(menuItems)
                            showLoading(false)
                        }
                    }
                    is Resource.Error -> {
                        showError(resource.message ?: "An unknown error occurred")
                        Log.d("CreateOrderActivity", "Error: ${resource.message}")
                        showLoading(false)
                    }
                }
            }
            menuItemViewModel.addNoteMenuItemState.collect{
                resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Có thể hiển thị loading nếu cần
                    }
                    is Resource.Success -> {
                        Toast.makeText(
                            this@CreateOderActivity,
                            "Đã thêm note thành công",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is Resource.Error -> {
                        showError(resource.message ?: "Không thể thêm note")
                    }
                    null -> {
                        // Trạng thái ban đầu, không làm gì
                    }
                }
            }
        }
    }

    private fun showLoading(loading: Boolean) {
        binding.menuItemSwipeRefreshLayout.isRefreshing  = loading
    }

    private fun setupRecyclerview(){
        adapter = MenuItemAdapter(
            onMenuItemClick = {
                menuItem ->
                handleItemClick(menuItem)
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
            },
            onLongPress = {
                menuItem -> handleLongPress(menuItem)
            },
            onNoteButtonClick = {
                menuItem -> showNoteDialog(menuItem)
            }
        )
        binding.menuItemRec.layoutManager = LinearLayoutManager(this)
        binding.menuItemRec.adapter = adapter
    }

    private fun handleItemClick(menuItem: MenuItem) {
        // if there are items selected, switch to selection mode
        if (adapter.getSelectItems().isNotEmpty()) {
            // No need to call toggleSelection here as it is already handled in the adapter
            updateSelectionUI()
        } else {
            showItemDetails(menuItem)
        }
    }

    private fun handleLongPress(menuItem: MenuItem) {
        updateSelectionUI()
    }

    private fun updateSelectionUI() {
        // Update UI based on the current selection state
        val selectedCount = adapter.getSelectItems().size
        if (selectedCount > 0) {
            // Show controls for selection mode
            binding.toolbar.title = "$selectedCount selected"
            // Other UI elements can be added to the selection mode
        } else {
            // Back to normal UI
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
        updateOrderSummary()
    }

    private fun updateOrderSummary() {
        // Calculate total price and update UI
        val totalPrice = selectedItems.entries.sumOf { (itemId, quantity) ->
            menuItemViewModel.menuItemsState.value.data?.find { it.id == itemId }?.price?.times(quantity) ?: 0.0
        }

        // Update UI with total price and selected items
        // This would be implemented based on your UI requirements
    }

    private fun showError(message: String){
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun showNoteDialog(menuItem: MenuItem){
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

            if(note.isNotEmpty()){
                menuItemViewModel.addNoteMenuItem(menuItem.id, note)
                Toast.makeText(this, "Đã thêm note: $note", Toast.LENGTH_SHORT).show()
                dialog.dismiss() // Đóng dialog sau khi xác nhận
            }
            else{
                Toast.makeText(this, "Noted note empty: $note", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.attributes?.windowAnimations=R.style.DialogAnimation;
        dialog.window?.setGravity(Gravity.CENTER)
    }
}