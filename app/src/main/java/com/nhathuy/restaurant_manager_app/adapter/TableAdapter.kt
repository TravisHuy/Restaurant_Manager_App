package com.nhathuy.restaurant_manager_app.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.data.model.Table
import com.nhathuy.restaurant_manager_app.databinding.ItemTableBinding

/**
 * Adapter for RecyclerView to display list of tables.
 * @param tables list of tables to display
 *
 * @return 0.1
 * @since 14-02-2025
 * @author TravisHuy
 */
class TableAdapter(var tables:List<Table>,private val onTableClick: (Table) -> Unit) : RecyclerView.Adapter<TableAdapter.TableViewHolder>() {

    inner class TableViewHolder(val binding:ItemTableBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TableAdapter.TableViewHolder {
        val binding = ItemTableBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TableViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TableAdapter.TableViewHolder, position: Int) {
        val table = tables[position]
        with(holder.binding) {
            tvTableNumber.text = table.number.toString()

            val alphaValue = if (table.available == false) 0.3f else 1.0f

            ivTableImage.alpha = alphaValue

            val context = root.context
            val color = if(table.available == false){
                ContextCompat.getColor(context,R.color.red)
            }
            else{
                ContextCompat.getColor(context,R.color.green)
            }
            val backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.status_table)?.mutate()
            if (backgroundDrawable != null) {
                DrawableCompat.setTint(backgroundDrawable, color)
                tableStatus.background = backgroundDrawable
            }

            Log.d("TableAdapter", "Table ${table.number} available: ${table.available}, Setting alpha to $alphaValue")


            root.setOnClickListener {
                onTableClick(table)
                Log.d("TableAdapter", "Table ${table.reservationId} clicked")
                Log.d("TableAdapter", "Table ${table.orderId} clicked")
                Log.d("TableAdapter", "Table ${table.floorId} clicked")
            }
        }

    }
    override fun getItemCount(): Int  = tables.size

    fun updateTables(newTables: List<Table>){
        this.tables = newTables
        notifyDataSetChanged()
    }
}