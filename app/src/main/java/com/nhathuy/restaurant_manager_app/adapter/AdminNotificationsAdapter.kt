package com.nhathuy.restaurant_manager_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.data.model.AdminNotification
import com.nhathuy.restaurant_manager_app.data.model.NotificationType
import com.nhathuy.restaurant_manager_app.databinding.ItemNotificationsBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AdminNotificationsAdapter(  private val onItemClick: (AdminNotification) -> Unit): ListAdapter<AdminNotification, AdminNotificationsAdapter.NotificationViewHolder>(
    DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdminNotificationsAdapter.NotificationViewHolder {
        val binding = ItemNotificationsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: AdminNotificationsAdapter.NotificationViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class NotificationViewHolder( private val binding: ItemNotificationsBinding):RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(notification: AdminNotification) {
            binding.apply {
                textViewTitle.text = notification.title
                textViewMessage.text = notification.message

                // Format the timestamp
                try {
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                    val date = inputFormat.parse(notification.timestamp)
                    val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                    textViewTimestamp.text = date?.let { outputFormat.format(it) } ?: notification.timestamp
                } catch (e: Exception) {
                    // Fallback if date parsing fails
                    textViewTimestamp.text = notification.timestamp
                }

//                // Set notification icon based on type
//                val iconRes = when (notification.notificationType) {
//                    NotificationType.ORDER -> R.drawable.ic_order_board
//                    NotificationType.PAYMENT -> R.drawable.ic_payments
//                    NotificationType.RESERVATION -> R.drawable.ic_restaurant
//                    NotificationType.SYSTEM -> R.drawable.ic_service
//                    else -> R.drawable.ic_notification_unread
//                }
//
//                imageViewIcon.setImageResource(iconRes)


                val isUnread = false // Replace with actual unread check
                if (isUnread) {
                    cardNotification.setCardBackgroundColor(
                        ContextCompat.getColor(root.context, R.color.unread_notification_bg)
                    )
                    indicatorUnread.visibility = View.VISIBLE
                } else {
                    cardNotification.setCardBackgroundColor(
                        ContextCompat.getColor(root.context, R.color.read_notification_bg)
                    )
                    indicatorUnread.visibility = View.GONE
                }
            }
        }
    }
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AdminNotification>() {
            override fun areItemsTheSame(oldItem: AdminNotification, newItem: AdminNotification): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: AdminNotification, newItem: AdminNotification): Boolean {
                return oldItem == newItem
            }
        }
    }
}