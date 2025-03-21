package com.nhathuy.restaurant_manager_app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.data.model.Reservation
import com.nhathuy.restaurant_manager_app.data.model.Table
import com.nhathuy.restaurant_manager_app.databinding.TableAdminItemBinding

class TableAdminAdapter(
    var tables: List<Table>,
    private val fetchReservationById: (String) -> Reservation?,
    private val onTableClick: (Table) -> Unit
) : RecyclerView.Adapter<TableAdminAdapter.TableAdminViewHolder>() {

    inner class TableAdminViewHolder(val binding: TableAdminItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(table: Table, position: Int) {
            binding.apply {
                tvNumberTable.text = table.number.toString()

                // Check if the table has a reservation ID and it's not empty
                if (!table.reservationId.isNullOrEmpty()) {
                    // Safely fetch the reservation - might be null if not loaded yet
                    val reservation = fetchReservationById(table.reservationId)

                    // Only use the reservation data if it's available
                    if (reservation != null) {
                        tvSeats.text = root.context.getString(R.string.seats,reservation.numberOfPeople)
                    } else {
                        // Show a loading state if reservation data isn't available yet
                        tvSeats.text = "Loading..."
                    }
                } else {
                    // No reservation, show capacity
                    tvSeats.text = root.context.getString(R.string.capacity,table.capacity)
                }

                // Set the background color based on availability
                cardTable.setBackgroundResource(
                    if (table.available) {
                        R.color.green
                    } else {
                        R.color.red
                    }
                )

                // Set click listener for each table
                root.setOnClickListener {
                    onTableClick(table)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableAdminViewHolder {
        val binding = TableAdminItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TableAdminViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TableAdminViewHolder, position: Int) {
        val table = tables[position]
        holder.bind(table, position)
    }

    override fun getItemCount(): Int = tables.size

    fun updateTables(newTables: List<Table>) {
        tables = newTables
        notifyDataSetChanged()
    }
}