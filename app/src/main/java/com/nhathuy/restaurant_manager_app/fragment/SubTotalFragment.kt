package com.nhathuy.restaurant_manager_app.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.adapter.TableAdapter
import com.nhathuy.restaurant_manager_app.data.model.Table
import com.nhathuy.restaurant_manager_app.databinding.FragmentSubTotalBinding
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.viewmodel.FloorViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.OrderViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.TableViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ViewModelFactory
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Use the [SubTotalFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 * @return 0.1
 * @version 14-02-2025
 * @author TravisHuy
 */
class SubTotalFragment : Fragment() {
    private lateinit var binding: FragmentSubTotalBinding
    private lateinit var tableAdapter: TableAdapter
    private lateinit var floorAdapter: ArrayAdapter<String>

    private var floorId : String? = null

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: FloorViewModel by viewModels { viewModelFactory }
    private val tableViewModel: TableViewModel by viewModels { viewModelFactory }
    private val orderViewModel: OrderViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSubTotalBinding.inflate(inflater, container, false)

        setupTableRecyclerView()
        setupFloorSpinner()
        observeViewModel()
        setupListeners()

        viewModel.getAllFloors()
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as RestaurantMangerApp).getRestaurantComponent().inject(this)
    }

    private fun setupListeners() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            floorId?.let {
                viewModel.getFloorById(it)
            }
        }
    }

    private fun handleTableSelection(table: Table) {
        if (table.orderId.isNullOrEmpty()) {
            showNoOrderMessage()
        } else {
            table.orderId?.let {
                showConfirmUpdateOrderDialog(it)
            } ?: showNoOrderMessage()
        }
    }

    private fun showNoOrderMessage() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.table_not_order))
            .setMessage(getString(R.string.not_order_message))
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showConfirmUpdateOrderDialog(orderId: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.update_order_status))
            .setMessage(getString(R.string.confirm_update_order_status))
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                dialog.dismiss()
                updateOrderStatus(orderId)
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun updateOrderStatus(orderId: String) {
        // Show loading before the operation starts
        showLoading(true)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                try {
                    orderViewModel.updateStatusOrder(orderId)

                    orderViewModel.updateOrderStatus
                        .collect { resource ->
                            when (resource) {
                                is Resource.Loading -> {
                                    // Loading is already shown
                                }
                                is Resource.Success -> {
                                    showLoading(false)
                                    resource.data?.let { order ->
                                        Toast.makeText(
                                            requireContext(),
                                            "Order status updated successfully: ${order.id}",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        // Refresh the table data after successful update
                                        floorId?.let {
                                            viewModel.getFloorById(it)
                                        }
                                    }
                                }
                                is Resource.Error -> {
                                    showLoading(false)
                                    val errorMsg = resource.message ?: "An error occurred"
                                    showError(errorMsg)
                                    Log.e("SubTotalFragment", "Error updating order status: $errorMsg")
                                }
                                else -> {
                                    showLoading(false)
                                }
                            }
                        }
                } catch (e: Exception) {
                    if (e is CancellationException) {
                        // Coroutine was cancelled normally, don't show error
                        Log.d("SubTotalFragment", "Update operation was cancelled")
                    } else {
                        showLoading(false)
                        showError("Error: ${e.message}")
                        Log.e("SubTotalFragment", "Exception during update: ${e.message}", e)
                    }
                }
            }
        }
    }

    private fun setupTableRecyclerView() {
        tableAdapter = TableAdapter(listOf()) { table ->
            handleTableSelection(table)
            Log.d("SubTotalFragment", "Table clicked orderId: ${table.orderId}")
        }
        binding.recTable.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recTable.adapter = tableAdapter
    }

    private fun setupFloorSpinner() {
        floorAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            mutableListOf()
        )
        floorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFloors.adapter = floorAdapter

        binding.spinnerFloors.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedFloor = parent?.getItemAtPosition(position).toString()
                val floor = viewModel.floors.value?.data?.find {
                    it.name == selectedFloor
                }
                floor?.let {
                    floorId = it.id
                    showListTable(it.id)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun observeViewModel() {
        viewModel.floors.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    showLoading(false)
                    result.data?.let { floors ->
                        val floorNames = floors.map { it.name }
                        floorAdapter.clear()
                        floorAdapter.addAll(floorNames)

                        if (floors.isNotEmpty()) {
                            // First item will be selected automatically
                            floorId = floors[0].id
                            showListTable(floors[0].id)
                        }
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    showError(result.message ?: "Failed to load floors")
                }
                is Resource.Loading -> {
                    showLoading(true)
                }
            }
        }

        viewModel.floorById.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    showLoading(false)
                    result.data?.let { floor ->
                        tableViewModel.getTablesByFloorId(floor.id)
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    showError(result.message ?: "Failed to load floor details")
                }
                is Resource.Loading -> {
                    showLoading(true)
                }
            }
        }

        tableViewModel.tablesByFloor.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    showLoading(false)
                    result.data?.let { tables ->
                        tableAdapter.updateTables(tables)
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    showError(result.message ?: "Failed to load tables")
                }
                is Resource.Loading -> {
                    showLoading(true)
                }
            }
        }
    }

    private fun showListTable(floorId: String) {
        viewModel.getFloorById(floorId)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.swipeRefreshLayout.isRefreshing = isLoading
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}