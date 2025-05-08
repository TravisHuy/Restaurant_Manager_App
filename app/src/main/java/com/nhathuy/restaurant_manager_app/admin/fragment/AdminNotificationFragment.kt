package com.nhathuy.restaurant_manager_app.admin.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.adapter.AdminNotificationsAdapter
import com.nhathuy.restaurant_manager_app.data.model.AdminNotification
import com.nhathuy.restaurant_manager_app.databinding.FragmentAdminNotificationBinding
import com.nhathuy.restaurant_manager_app.databinding.FragmentStaffManagementAdminBinding
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.viewmodel.AdminNotificationViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ViewModelFactory
import javax.inject.Inject

/**
 *
 */
class AdminNotificationFragment : Fragment() {

    private var _binding: FragmentAdminNotificationBinding? =null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel : AdminNotificationViewModel by viewModels { viewModelFactory }
    private lateinit var adapter : AdminNotificationsAdapter
    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as RestaurantMangerApp).getRestaurantComponent().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =  FragmentAdminNotificationBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupRefreshLayout()

        // Load notifications initially
        viewModel.loadAllNotifications()
    }

    private fun setupRecyclerView() {
        adapter = AdminNotificationsAdapter { notification ->
            handleNotificationClick(notification)
        }
        binding.recyclerViewNotifications.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AdminNotificationFragment.adapter
        }
    }

    private fun setupObservers() {
        viewModel.notificationState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recyclerViewNotifications.visibility = View.GONE
                    binding.textViewNoNotifications.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefreshLayout.isRefreshing = false

                    val notifications = resource.data ?: emptyList()
                    if (notifications.isEmpty()) {
                        binding.recyclerViewNotifications.visibility = View.GONE
                        binding.textViewNoNotifications.visibility = View.VISIBLE
                    } else {
                        binding.recyclerViewNotifications.visibility = View.VISIBLE
                        binding.textViewNoNotifications.visibility = View.GONE
                        adapter.submitList(notifications)
                    }
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.swipeRefreshLayout.isRefreshing = false
                    Toast.makeText(requireContext(), "Error: ${resource.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.markAsReadState.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Optionally show some loading indicator for the mark as read operation
                }
                is Resource.Success -> {
                    Toast.makeText(requireContext(), "Notification marked as read", Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), "Failed to mark as read: ${resource.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadAllNotifications()
        }
    }

    private fun handleNotificationClick(notification: AdminNotification) {
        // Mark notification as read
        viewModel.markNotificationAsRead(notification.id)

        // Here you would handle navigation to related screens based on notification type
        // For example:
        when (notification.notificationType) {
            // Navigate to appropriate screens based on notification type
            // Example: NotificationType.ORDER -> navigateToOrderDetails(notification.relatedId)
            else -> {
                // Default navigation or just show a toast
                Toast.makeText(requireContext(), notification.message, Toast.LENGTH_LONG).show()
            }
        }
    }

}