package com.nhathuy.restaurant_manager_app.admin.fragment

import android.content.Context
import android.os.Bundle
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.compose.ui.text.toLowerCase
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.adapter.StaffItemAdapter
import com.nhathuy.restaurant_manager_app.data.model.User
import com.nhathuy.restaurant_manager_app.databinding.FragmentStaffManagementAdminBinding
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.viewmodel.AuthViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ViewModelFactory
import javax.inject.Inject

/**
 * StaffManagementAdminFragment
 *
 * This fragment is responsible for get all user in system
 *
 * @version 0.1
 * @author TravisHuy
 * @since 16.4.2025
 */

class StaffManagementAdminFragment : Fragment() {

    private var _binding: FragmentStaffManagementAdminBinding? =null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val authViewModel: AuthViewModel by viewModels { viewModelFactory }

    private lateinit var staffItemAdapter : StaffItemAdapter

    //
    private var allUsersList: List<User> = emptyList()
    private var currentList: String = "ALL"
    private var currentQuery: String = ""


    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as RestaurantMangerApp).getRestaurantComponent().inject(this)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStaffManagementAdminBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchView()
        setupChipGroup()
        setupSwipeRefresh()
        setupObserverModel()

        loadUsers()
    }

    private fun setupRecyclerView(){
        staffItemAdapter = StaffItemAdapter(onUserClick = {

        })
        binding.recStaff.layoutManager = LinearLayoutManager(requireContext())
        binding.recStaff.adapter = staffItemAdapter
    }
    private fun setupSearchView(){
        val searchView = binding.searchCard.getChildAt(0) as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {
                currentQuery = newText?.trim()?.toLowerCase()?:""
                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
        })
    }
    private fun setupChipGroup(){
        binding.chipAll.isChecked = true

        binding.chipAll.setOnClickListener {
            currentQuery = "ALL"
            filterUserList()
        }
        binding.chipManager.setOnClickListener {
            currentQuery ="MANAGER"
            filterUserList()
        }
        binding.chipManager.setOnClickListener {
            currentQuery ="EMPLOYEE"
            filterUserList()
        }
        binding.chipManager.setOnClickListener {
            currentQuery ="ADMIN"
            filterUserList()
        }

    }
    private fun setupSwipeRefresh(){

    }
    private fun setupObserverModel(){
        authViewModel.allUsers.observe(viewLifecycleOwner) {
            resources ->
            when(resources){
                is Resource.Loading -> {
                    showLoading()
                }
                is Resource.Success -> {
                    hideLoading()
                    resources?.data.let {
                        users ->
                        allUsersList = users!!
                        filterUserList()
                    }
                }
                is Resource.Error -> {
                    hideLoading()
                    showError(resources.message ?: "Unknow error occured")
                }
            }
        }
    }
    private fun filterUserList(){
        if(allUsersList.isEmpty()) return

        val filteredList = allUsersList.filter {
            user ->
            val roleMatches = when(currentList){
                "ALL" -> true
                else -> user.role.toString() == currentList
            }

            val searchMatchers = if(currentQuery.isNotEmpty()){
                user.name.lowercase().contains(currentQuery) ||
                user.email.lowercase().contains(currentQuery) ||
                user.phoneNumber.lowercase().contains(currentQuery)
            }
            else{
                true
            }

            roleMatches && searchMatchers
        }
        staffItemAdapter.submitList(filteredList)
    }
    private fun loadUsers(){
        authViewModel.getAllUsers()
    }

    private fun showLoading(){
        binding.swipeRefreshStaffLayout.isRefreshing = true
    }
    private fun hideLoading(){
        binding.swipeRefreshStaffLayout.isRefreshing = false
    }
    private fun showError(errorMessage:String){
        Toast.makeText(requireContext(),errorMessage, Toast.LENGTH_SHORT).show()
    }

}