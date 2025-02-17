package com.nhathuy.restaurant_manager_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nhathuy.restaurant_manager_app.data.model.Floor
import com.nhathuy.restaurant_manager_app.databinding.ItemFloorBinding

class FloorAdapter(private val onFloorClick: (Floor) -> Unit): RecyclerView.Adapter<FloorAdapter.FloorViewHolder>() {

    private var floors = mutableListOf<Floor>()

    inner class FloorViewHolder(val binding:ItemFloorBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(floor: Floor, position: Int) {
            binding.apply {
                textFloor.text = floor.name

                itemView.setOnClickListener {
                    onFloorClick(floor)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FloorAdapter.FloorViewHolder {
        val binding = ItemFloorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FloorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FloorAdapter.FloorViewHolder, position: Int) {
        holder.bind(floors[position],position)
    }

    override fun getItemCount(): Int = floors.size

    fun updateFloors(newFloors: List<Floor>) {
        floors.clear()
        floors.addAll(newFloors)
        notifyDataSetChanged()
    }

}