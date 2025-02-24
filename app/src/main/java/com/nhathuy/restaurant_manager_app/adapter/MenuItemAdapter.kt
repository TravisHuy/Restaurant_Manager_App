package com.nhathuy.restaurant_manager_app.adapter

import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.card.MaterialCardView
import com.google.android.material.datepicker.MaterialCalendar
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.data.model.MenuItem
import com.nhathuy.restaurant_manager_app.databinding.ItemMenuBinding
import com.nhathuy.restaurant_manager_app.util.Constants

/**
 * Adapter class for the menu items recyclerview
 *
 * @return 0.1
 * @since 20-02-2025
 * @author TravisHuy
 */
class MenuItemAdapter(
    private val onMenuItemClick: (MenuItem) -> Unit,
    private val onAddClick: (MenuItem) -> Unit,
    private val onMinusClick: (MenuItem) -> Unit,
    private val onQuantityChanged: (MenuItem, Int) -> Unit,
    private val onLongPress: (MenuItem) -> Unit,
    private val onNoteButtonClick : (MenuItem) -> Unit
): RecyclerView.Adapter<MenuItemAdapter.MenuItemViewHolder>() {

    private var menuItems: List<MenuItem> = listOf()
    private var itemQuantities = mutableMapOf<String, Int>()
    private var selectedItems = mutableSetOf<String>()

    inner class MenuItemViewHolder(val binding: ItemMenuBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(menuItem: MenuItem, position: Int) {
            binding.apply {
                menuItem?.let {
                    menuItemName.text = it.name
                    menuItemPrice.text = String.format("%.2f", it.price)

                    val imageData = it.imageData
                    if(imageData != null) {
                        val imageByteArray = android.util.Base64.decode(imageData, android.util.Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
                        imageMenu.setImageBitmap(bitmap)
                    }

                    // Update check_menu visibility and image alpha based on selection
                    checkMenu.visibility = if(selectedItems.contains(menuItem.id)) View.VISIBLE else View.GONE
                    imageMenu.alpha = if(selectedItems.contains(menuItem.id)) 0.2f else 1.0f

                    val currentQuantity = itemQuantities[it.id] ?: 0
                    updateControlsVisibility(currentQuantity)

                    edNumberMenu.setText(currentQuantity.toString())

                    swipeRevealLayout.findViewById<MaterialCardView>(R.id.card_content_menu).apply {
                        setOnClickListener {
                            toggleSelection(menuItem)
                            onMenuItemClick(menuItem)
                        }

                        setOnLongClickListener {
                            toggleSelection(menuItem)
                            onLongPress(menuItem)
                            true
                        }
                    }

                    btnMenuAdd.setOnClickListener {
                        val currentText = edNumberMenu.text.toString()
                        val currentValue = currentText.toIntOrNull() ?: 0
                        val newQuantity = currentValue + 1

                        itemQuantities[menuItem.id] = newQuantity
                        updateControlsVisibility(newQuantity)
                        edNumberMenu.setText(newQuantity.toString())
                        onAddClick(menuItem)
                    }

                    btnMinusMenu.setOnClickListener {
                        val currentText = edNumberMenu.text.toString()
                        val currentValue = currentText.toIntOrNull() ?: 0
                        val newQuantity = currentValue - 1

                        if(currentValue > 0) {
                            itemQuantities[menuItem.id] = newQuantity
                            updateControlsVisibility(newQuantity)
                            edNumberMenu.setText(newQuantity.toString())
                            onMinusClick(menuItem)
                        }
                    }

                    edNumberMenu.setOnEditorActionListener { _, actionId, _ ->
                        if(actionId == EditorInfo.IME_ACTION_DONE) {
                            val newQuantity = edNumberMenu.text.toString().toIntOrNull() ?: 0
                            itemQuantities[menuItem.id] = newQuantity
                            updateControlsVisibility(newQuantity)
                            onQuantityChanged(menuItem, newQuantity)
                            edNumberMenu.setSelection(edNumberMenu.text!!.length)
                            true
                        } else {
                            false
                        }
                    }
                    btnNote.setOnClickListener {
                        onNoteButtonClick(menuItem)
                        swipeRevealLayout.close(true)
                    }
                }
            }
        }

        private fun updateControlsVisibility(quantity: Int) {
            binding.apply {
                if(quantity > 0) {
                    btnMinusMenu.visibility = View.VISIBLE
                    edNumberMenu.visibility = View.VISIBLE
                } else {
                    btnMinusMenu.visibility = View.GONE
                    edNumberMenu.visibility = View.GONE
                }
            }
        }
    }

    fun toggleSelection(menuItem: MenuItem) {
        if(selectedItems.contains(menuItem.id)) {
            selectedItems.remove(menuItem.id)
        } else {
            selectedItems.add(menuItem.id)
        }
        // Notify the specific item change to update UI
        notifyItemChanged(menuItems.indexOfFirst { it.id == menuItem.id })
    }

    // Rest of the adapter implementation remains the same
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        val binding = ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        holder.bind(menuItems[position], position)
    }

    override fun getItemCount(): Int = menuItems.size

    fun updateMenuItems(newMenuItems: List<MenuItem>) {
        menuItems = newMenuItems
        notifyDataSetChanged()
    }

    fun getQuantity(menuItemId: String): Int {
        return itemQuantities[menuItemId] ?: 0
    }

    fun clearQuantities() {
        itemQuantities.clear()
        selectedItems.clear()
        notifyDataSetChanged()
    }

    fun getSelectItems(): List<MenuItem> {
        return menuItems.filter { selectedItems.contains(it.id) }
    }
}