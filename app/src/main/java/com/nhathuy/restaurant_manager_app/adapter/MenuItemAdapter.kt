package com.nhathuy.restaurant_manager_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nhathuy.restaurant_manager_app.data.model.MenuItem
import com.nhathuy.restaurant_manager_app.databinding.ItemMenuBinding

/**
 * Adapter class for the menu items recyclerview
 *
 * @return 0.1
 * @since 20-02-2025
 * @author TravisHuy
 */
class MenuItemAdapter(private val onMenuItemClick: (MenuItem) -> Unit,
                      private val onAddClick: (MenuItem) -> Unit,
                      private val onMinusClick: (MenuItem) -> Unit,
                      private val onQuantityChanged: (MenuItem, Int) -> Unit
): RecyclerView.Adapter<MenuItemAdapter.MenuItemViewHolder>() {

    private var menuItems:List<MenuItem> = listOf()
    private var itemQuantities = mutableMapOf<String,Int>()


    inner class MenuItemViewHolder(val binding:ItemMenuBinding):RecyclerView.ViewHolder(binding.root) {

        fun bind(menuItem: MenuItem, position: Int) {
            binding.apply {
                menuItem?.let {
                    menuItemName.text = it.name
                    menuItemPrice.text = String.format("%.2f", it.price)

                    val imageUrl = it.imageId.toString()
                    Glide.with(itemView)
                        .load(it.imageId)
                        .into(imageMenu)

                    val currentQuantity  = itemQuantities[it.id] ?: 0
                    updateControlsVisibility(currentQuantity)

                    edNumberMenu.setText(currentQuantity.toString())

                    root.setOnClickListener {
                        onMenuItemClick(menuItem)
                    }

                    btnMenuAdd.setOnClickListener {
                        val newQuantity = (itemQuantities[menuItem.id] ?: 0) + 1
                        itemQuantities[menuItem.id] = newQuantity
                        updateControlsVisibility(newQuantity)
                        edNumberMenu.setText(newQuantity.toString())
                        onAddClick(menuItem)
                    }
                    btnMinusMenu.setOnClickListener {
                        val currentQty = itemQuantities[menuItem.id] ?: 0
                        if(currentQty > 0){
                            val newQuantity = currentQty - 1
                            itemQuantities[menuItem.id] = newQuantity
                            updateControlsVisibility(newQuantity)
                            edNumberMenu.setText(newQuantity.toString())
                            onMinusClick(menuItem)
                        }
                    }

                    edNumberMenu.setOnEditorActionListener { _, actionId, _ ->
                        if(actionId == EditorInfo.IME_ACTION_DONE){
                            val newQuantity = edNumberMenu.text.toString().toIntOrNull() ?: 0
                            itemQuantities[menuItem.id] = newQuantity
                            updateControlsVisibility(newQuantity)
                            onQuantityChanged(menuItem,newQuantity)
                            true
                        }
                        else {
                            false
                        }
                    }
                    btnNote.setOnClickListener {
                        slidingPaneLayout.openPane()
                    }
                }
            }


        }
        private fun updateControlsVisibility(quantity:Int){
            binding.apply {
                if(quantity> 0){
                    btnMinusMenu.visibility = View.VISIBLE
                    edNumberMenu.visibility = View.VISIBLE
                }
                else{
                    btnMinusMenu.visibility = View.GONE
                    edNumberMenu.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MenuItemAdapter.MenuItemViewHolder {
        val binding = ItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuItemAdapter.MenuItemViewHolder, position: Int) {
        holder.bind(menuItems[position],position)
    }

    override fun getItemCount(): Int = menuItems.size

    fun updateMenuItems(newMenuItems: List<MenuItem>){
        menuItems = newMenuItems
        notifyDataSetChanged()
    }

    fun getQuantity(menuItemId:String):Int {
        return itemQuantities[menuItemId] ?: 0
    }

    fun clearQuantities(){
        itemQuantities.clear()
        notifyDataSetChanged()
    }

}