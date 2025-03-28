package com.nhathuy.restaurant_manager_app.admin.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.adapter.MenuItemAdminAdapter
import com.nhathuy.restaurant_manager_app.admin.add.AddMenuItemActivity
import com.nhathuy.restaurant_manager_app.databinding.FragmentMenuItemsAdminBinding
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.viewmodel.MenuItemViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * MenuItemsAdminFragment
 *
 * This fragment is responsible for displaying and managing the menuItem
 * layout in the admin panel.
 *
 * @version 0.1
 * @author TravisHuy
 * @since 28.3.2025
 */
class MenuItemsAdminFragment : Fragment() {

    private lateinit var binding: FragmentMenuItemsAdminBinding
    private lateinit var menuItemAdminAdapter: MenuItemAdminAdapter
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val menuItemViewModel: MenuItemViewModel by viewModels { viewModelFactory  }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as RestaurantMangerApp).getRestaurantComponent().inject(this)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMenuItemsAdminBinding.inflate(LayoutInflater.from(context),container,false)

        setupRecyclerView()
        setupListeners()
        observeViewModel()

        menuItemViewModel.getAllMenuItems()

        return binding.root
    }
    private fun setupRecyclerView(){
        menuItemAdminAdapter = MenuItemAdminAdapter()
        binding.menuItemAdminRec.layoutManager = LinearLayoutManager(requireContext())
        binding.menuItemAdminRec.adapter = menuItemAdminAdapter
    }
    private fun setupListeners(){
        binding.btnAddFloor.setOnClickListener {
            val intent = Intent(requireContext(),AddMenuItemActivity::class.java)
            startActivity(intent)
        }
    }
    private fun observeViewModel(){
        lifecycleScope.launch {
            menuItemViewModel.menuItemsState.collect {
                    resource ->
                when (resource) {
                    is Resource.Loading -> {
                        showLoading(true)
                    }
                    is Resource.Success -> {
                        resource.data?.let { menuItems ->
                            menuItemAdminAdapter.updateMenuItem(menuItems)
                            showLoading(false)
                        }
                    }
                    is Resource.Error -> {
//                        showError(resource.message ?: "An unknown error occurred")
                        Log.d("MenuItemActivity", "Error loading menu items: ${resource.message}")
                        showLoading(false)
                    }
                }
            }
        }
    }

    private fun showLoading(loading: Boolean) {
        binding.menuItemAdminSwipeRefreshLayout.isRefreshing = loading
    }
}