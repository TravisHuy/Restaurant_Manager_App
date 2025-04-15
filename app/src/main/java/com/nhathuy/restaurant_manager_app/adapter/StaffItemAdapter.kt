package com.nhathuy.restaurant_manager_app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nhathuy.restaurant_manager_app.data.model.Invoice
import com.nhathuy.restaurant_manager_app.data.model.User
import com.nhathuy.restaurant_manager_app.databinding.ItemStaffBinding

class StaffItemAdapter(private val onUserClick: (User) -> Unit) : ListAdapter<User,StaffItemAdapter.StaffItemViewHolder>(StaffDiffCallback()) {

    inner class StaffItemViewHolder(private val binding: ItemStaffBinding,
                                    private val onUserClick: (User) -> Unit
    ) :RecyclerView.ViewHolder(binding.root){

        private var currentUser: User? = null

        init {
            binding.menuButton.setOnClickListener {
                currentUser?.let {
                    user ->
                    onUserClick(user)
                }
            }
        }

        fun bind(user: User) {
            binding?.apply {
                staffName.text = user.name
                staffEmail.text = user.email
                staffPhone.text = user.phoneNumber
                staffRole.text = user.role.toString()
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StaffItemAdapter.StaffItemViewHolder {
        val binding = ItemStaffBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return StaffItemViewHolder(binding,onUserClick)
    }

    override fun onBindViewHolder(holder: StaffItemAdapter.StaffItemViewHolder, position: Int) {
        val staff = getItem(position)
        holder.bind(staff)
    }

    class StaffDiffCallback: DiffUtil.ItemCallback<User>(){
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

    }

}