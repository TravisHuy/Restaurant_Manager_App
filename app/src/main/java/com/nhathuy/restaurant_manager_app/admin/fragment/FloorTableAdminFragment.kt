package com.nhathuy.restaurant_manager_app.admin.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.adapter.FloorAdapter
import com.nhathuy.restaurant_manager_app.adapter.TableAdapter
import com.nhathuy.restaurant_manager_app.admin.add.AddFloorActivity
import com.nhathuy.restaurant_manager_app.data.dto.TableDto
import com.nhathuy.restaurant_manager_app.data.model.Floor
import com.nhathuy.restaurant_manager_app.databinding.FragmentFloorTableAdminBinding
import com.nhathuy.restaurant_manager_app.resource.AdManager
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.util.Constants
import com.nhathuy.restaurant_manager_app.viewmodel.FloorViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.TableViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ViewModelFactory
import javax.inject.Inject

/**
 * FloorTableAdminFragment
 *
 * This fragment is responsible for displaying and managing the floor table
 * layout in the admin panel.
 *
 * @version 0.1
 * @author TravisHuy
 * @since 28.3.2025
 */

class FloorTableAdminFragment : Fragment() {
    private lateinit var binding : FragmentFloorTableAdminBinding
    private lateinit var adapter: FloorAdapter
    private lateinit var tableAdapter: TableAdapter

    private var currentFloorId: String? = null
    private var dialog: Dialog? = null

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: FloorViewModel by viewModels { viewModelFactory }
    private val tableViewModel : TableViewModel by viewModels  { viewModelFactory }

    @Inject
    lateinit var adManager: AdManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as RestaurantMangerApp).getRestaurantComponent().inject(this)

    }

    /**
     * Called to create and return the view hierarchy associated with the fragment.
     *
     * @param inflater LayoutInflater to inflate the fragment's view
     * @param container Parent view that this fragment's UI should be attached to
     * @param savedInstanceState Bundle containing the saved state of the fragment
     * @return The root view of the fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment and return the view
        binding = FragmentFloorTableAdminBinding.inflate(LayoutInflater.from(context),container,false)


        adManager.loadInterstitialAd(requireContext())

        setupRecyclerView()
        setupTableRecyclerView()
        setupListeners()
        setupAddFloorListeners()
        setupRefreshSwipeLayout()
        observeViewModel()
        viewModel.getAllFloors()


        return binding.root
    }
    private fun setupRecyclerView() {
        adapter = FloorAdapter { floor ->
            Toast.makeText(requireContext(), "Selected: ${floor.name}", Toast.LENGTH_SHORT).show()
            currentFloorId = floor.id
            showDialogAddTable(floor.id)
            binding.circleRecyclerView.toggleVisibility()
        }
        binding.circleRecyclerView.adapter = adapter
        binding.btnFloors.setCircleRecyclerView(binding.circleRecyclerView)
    }
    private fun setupTableRecyclerView(){
        tableAdapter = TableAdapter(listOf()){

        }
        binding.recTable.layoutManager = GridLayoutManager(requireContext(),3)
        binding.recTable.adapter = tableAdapter
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
    }
    private fun setupRefreshSwipeLayout(){
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getAllFloors()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }
    private fun setupAddFloorListeners(){
        binding.btnAddFloor.setOnClickListener {
            try {
                val intent = Intent(requireContext(), AddFloorActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.ADD_FLOOR_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            viewModel.getAllFloors()
        }
    }

    private fun showListTable(floorId: String) {
        viewModel.getFloorById(floorId)
    }
    private fun observeViewModel() {
        viewModel.floors.observe(requireActivity()) { resource ->
            when (resource) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    resource.data?.let {
                            floors ->
                        setupFloorChips(floors)
                        adapter.updateFloors(floors)

                        if (currentFloorId == null && floors.isNotEmpty()) {
                            currentFloorId = floors[0].id
                            showListTable(floors[0].id)

                            // Make sure the first chip is selected
                            if (binding.chipGroupFloors.childCount > 0) {
                                (binding.chipGroupFloors.getChildAt(0) as? Chip)?.isChecked = true
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
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
                        tableViewModel.getTablesByFloorId(floor.id)
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
        tableViewModel.tables.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Success -> {
                    showLoading(false)
                    result.data?.let { tables ->
                        tableAdapter.updateTables(tables)
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    showLoading(true)
                }
            }
        }
        tableViewModel.tablesByFloor.observe(requireActivity()) { result ->
            when (result) {
                is Resource.Success -> {
                    showLoading(false)
                    result.data?.let { tables ->
                        tableAdapter.updateTables(tables)
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    showLoading(true)
                }
            }
        }

        tableViewModel.addTableResult.observe(viewLifecycleOwner) { result ->
            when(result) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), "Table added successfully", Toast.LENGTH_SHORT).show()

                    dialog?.dismiss()

                    currentFloorId?.let { floorId ->
                        tableViewModel.getTablesByFloorId(floorId)
                    }

                    adManager.showInterstitialAdWithUX(requireActivity()) {}
                }
                is Resource.Error -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun setupFloorChips(floors: List<Floor>) {
        binding.chipGroupFloors.removeAllViews()

        floors.forEach { floor ->
            val chip = Chip(requireContext()).apply {
                text = floor.name
                isClickable = true
                isCheckable = true
                setOnClickListener {

                    currentFloorId = floor.id
                    showListTable(floor.id)
                }
            }
            binding.chipGroupFloors.addView(chip)

            if (floor.id == currentFloorId) {
                chip.isChecked = true
            }
        }
    }
    private fun showDialogAddTable(floorId:String) {
        dialog = Dialog(requireContext())
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.dialog_add_table)

        val table_number=dialog?.findViewById<TextInputEditText>(R.id.ed_add_table_number)
        val table_capacity=dialog?.findViewById<TextInputEditText>(R.id.ed_add_table_capacity)
        val layoutTableNumber = dialog?.findViewById<TextInputLayout>(R.id.layout_add_table_number)
        val layoutTableCapacity = dialog?.findViewById<TextInputLayout>(R.id.layout_add_table_capacity)
        val btnAddTable = dialog?.findViewById<MaterialButton>(R.id.btn_add_table)
        val btnClose = dialog?.findViewById<ImageView>(R.id.ic_close)

        btnAddTable?.setOnClickListener {
            val tableNumber = table_number?.text.toString()
            val tableCapacity = table_capacity?.text.toString()

            if(validateInput(tableNumber,tableCapacity,layoutTableNumber!!, layoutTableCapacity!!)) {
                tableViewModel.addTable(
                    TableDto(tableNumber.toInt(), tableCapacity.toInt(), true),
                    floorId
                )
            }

        }
        btnClose?.setOnClickListener {
            dialog?.dismiss()
        }

        dialog?.show()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.attributes?.windowAnimations=R.style.DialogAnimation;
        dialog?.window?.setGravity(Gravity.CENTER)
    }
    private fun cleanInput(dialog: Dialog){
        dialog.findViewById<TextInputEditText>(R.id.ed_add_table_number).text?.clear()
        dialog.findViewById<TextInputEditText>(R.id.ed_add_table_capacity).text?.clear()
    }
    private fun validateInput(
        tableNumber: String,
        tableCapacity: String,
        layoutTableNumber: TextInputLayout,
        layoutTableCapacity: TextInputLayout
    ): Boolean {
        var isValid = true

        if (tableNumber.isEmpty() || tableNumber.isBlank()) {
            layoutTableNumber.error = "Table number is required"
            isValid = false
        } else {
            layoutTableNumber.error = null
        }

        if (tableCapacity.isEmpty() || tableCapacity.isBlank()) {
            layoutTableCapacity.error = "Table capacity is required"
            isValid = false
        } else {
            layoutTableCapacity.error = null
        }

        return isValid
    }

    private fun showLoading(isLoading:Boolean){
        binding.swipeRefreshLayout.isRefreshing = if(isLoading) true else false
    }
}
