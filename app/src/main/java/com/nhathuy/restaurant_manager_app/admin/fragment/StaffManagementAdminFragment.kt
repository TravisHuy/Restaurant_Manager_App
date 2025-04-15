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
import com.nhathuy.restaurant_manager_app.adapter.StaffItemAdapter
import com.nhathuy.restaurant_manager_app.databinding.FragmentStaffManagementAdminBinding
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.viewmodel.AuthViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ViewModelFactory
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 * Use the [StaffManagementAdminFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StaffManagementAdminFragment : Fragment() {

    private var _binding: FragmentStaffManagementAdminBinding? =null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val authViewModel: AuthViewModel by viewModels { viewModelFactory }

    private lateinit var staffItemAdapter : StaffItemAdapter

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
        setupObserverModel()

        loadUsers()
    }

    private fun setupRecyclerView(){
        staffItemAdapter = StaffItemAdapter(onUserClick = {

        })
        binding.recStaff.layoutManager = LinearLayoutManager(requireContext())
        binding.recStaff.adapter = staffItemAdapter
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
                        staffItemAdapter.submitList(users)
                    }
                }
                is Resource.Error -> {
                    hideLoading()
                    showError(resources.message ?: "Unknow error occured")
                }
            }
        }
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