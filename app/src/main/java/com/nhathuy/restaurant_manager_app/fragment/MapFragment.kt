package com.nhathuy.restaurant_manager_app.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.adapter.TableAdapter
import com.nhathuy.restaurant_manager_app.databinding.FragmentMapBinding
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.viewmodel.AuthViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.TableViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ViewModelFactory
import javax.inject.Inject

/**
 * Adapter for RecyclerView to display list of tables.
 *
 * @return 0.1
 * @since 14-02-2025
 * @author TravisHuy
 */
class MapFragment : Fragment() {
    private lateinit var binding: FragmentMapBinding
    private lateinit var adapter : TableAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: TableViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMapBinding.inflate(inflater, container, false)

        setupRecyclerView()
        observeViewModel()
        viewModel.getAllTables()

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as RestaurantMangerApp).getRestaurantComponent().inject(this)
    }

    private fun setupRecyclerView(){
        adapter= TableAdapter(listOf())
        binding.recTable.layoutManager = GridLayoutManager(requireContext(),3)
        binding.recTable.adapter = adapter
    }
    private fun observeViewModel(){
        viewModel.tables.observe(requireActivity()){
            result ->
            when(result){
                is Resource.Success -> {
                    showLoading(false)
                    result.data?.let {
                        tables ->
                        adapter.updateTables(tables)
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    Toast.makeText(requireContext(),result.message,Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading ->{
                    showLoading(true)
                }
            }
        }
    }
    private fun showLoading(isLoading:Boolean){
        binding.swipeRefreshLayout.isRefreshing = if(isLoading) true else false
    }
}