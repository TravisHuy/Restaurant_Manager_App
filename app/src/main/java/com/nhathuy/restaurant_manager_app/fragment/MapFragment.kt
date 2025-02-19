package com.nhathuy.restaurant_manager_app.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.nhathuy.restaurant_manager_app.adapter.FloorAdapter
import com.nhathuy.restaurant_manager_app.adapter.TableAdapter
import com.nhathuy.restaurant_manager_app.databinding.FragmentMapBinding
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.viewmodel.AuthViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.FloorViewModel
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
    private lateinit var tableAdapter : TableAdapter
    private lateinit var floorAdapter: FloorAdapter
    private var floorId : String? = null
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: FloorViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMapBinding.inflate(inflater, container, false)

        setupTableRecyclerView()
        setupFloorRecyclerView()
        observeViewModel()
        setupListeners()

        viewModel.getAllFloors()

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as RestaurantMangerApp).getRestaurantComponent().inject(this)
    }

    private fun setupTableRecyclerView(){
        tableAdapter= TableAdapter(listOf())
        binding.recTable.layoutManager = GridLayoutManager(requireContext(),3)
        binding.recTable.adapter = tableAdapter
    }
    private fun setupFloorRecyclerView(){
        floorAdapter = FloorAdapter { floor ->
            floorId = floor.id
            showListTable(floor.id)
            binding.circleRecyclerView.toggleVisibility()
        }
        binding.circleRecyclerView.adapter = floorAdapter
        binding.btnFloors.setCircleRecyclerView(binding.circleRecyclerView)
    }
    private fun setupListeners(){
        binding.btnFloors.post {
            val initialCenterX = binding.btnFloors.x + binding.btnFloors.width / 2
            val initialCenterY = binding.btnFloors.y + binding.btnFloors.height / 2
            binding.circleRecyclerView.setCenterPosition(initialCenterX, initialCenterY)
        }

        binding.btnFloors.setOnClickListener {
            binding.circleRecyclerView.toggleVisibility()
            val centerX = binding.btnFloors.x + binding.btnFloors.width / 2
            val centerY = binding.btnFloors.y + binding.btnFloors.height / 2
            binding.circleRecyclerView.setCenterPosition(centerX, centerY)
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getFloorById(floorId!!)
        }
    }
    private fun observeViewModel(){
        viewModel.floors.observe(requireActivity()){
            result ->
            when(result){
                is Resource.Success -> {
                    showLoading(false)
                    result.data?.let {
                        tables ->
                        floorAdapter.updateFloors(tables)
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
        viewModel.floorById.observe(requireActivity()){
                result ->
            when(result){
                is Resource.Success -> {
                    showLoading(false)
                    result.data?.let {
                            floor ->
                        tableAdapter.updateTables(floor.tables)
                        Log.d("MapFragment", "showListTable: ${floor.tables.size}")
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
    private fun showListTable(floorId: String) {
        viewModel.getFloorById(floorId)
    }
    private fun showLoading(isLoading:Boolean){
        binding.swipeRefreshLayout.isRefreshing = if(isLoading) true else false
    }
}