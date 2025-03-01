package com.nhathuy.restaurant_manager_app.fragment

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.adapter.FloorAdapter
import com.nhathuy.restaurant_manager_app.adapter.TableAdapter
import com.nhathuy.restaurant_manager_app.data.dto.ReservationDTO
import com.nhathuy.restaurant_manager_app.databinding.DialogAddCustomerNameBinding
import com.nhathuy.restaurant_manager_app.databinding.FragmentMapBinding
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.ui.MenuItemActivity
import com.nhathuy.restaurant_manager_app.util.Constants
import com.nhathuy.restaurant_manager_app.viewmodel.FloorViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ReservationViewModel
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
    private val reservationViewModel: ReservationViewModel by viewModels { viewModelFactory }


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
        tableAdapter= TableAdapter(listOf()){
            table ->
            showEnterCustomerName(table.id)
        }
        binding.recTable.layoutManager = GridLayoutManager(requireContext(),3)
        binding.recTable.adapter = tableAdapter
    }

    private fun showEnterCustomerName(tableId:String) {
        val dialog = Dialog(requireContext())
        val dialogBinding = DialogAddCustomerNameBinding.inflate(LayoutInflater.from(requireContext()))

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.btnCancelCustomer.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnConfirmCustomer.setOnClickListener {
            val customerName = dialogBinding.etCustomerName.text.toString().trim()
            val numberOfPeople = dialogBinding.etCustomerNumberPerson.text.toString().trim()
            var isValid = true

            if (numberOfPeople.isEmpty()) {
                dialogBinding.layoutCustomerNumberPerson.error = "Please enter number of people"
                isValid = false
            } else {
                dialogBinding.layoutCustomerNumberPerson.error = null // Xóa lỗi nếu nhập đúng
            }

            if (customerName.isEmpty()) {
                dialogBinding.layoutCustomerName.error = "Please enter customer name"
                isValid = false
            } else {
                dialogBinding.layoutCustomerName.error = null
            }

            if (!isValid) return@setOnClickListener

            reservationViewModel.addReservation(tableId, ReservationDTO(numberOfPeople.toInt(), customerName))

            reservationViewModel.addReservationResult.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Resource.Success -> {
                        Toast.makeText(requireContext(), "Reservation successful!", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()

                        // Chuyển màn hình sau khi reservation đã được tạo xong
                        val intent = Intent(requireContext(), MenuItemActivity::class.java).apply {
                            putExtra("CUSTOMER_NAME", customerName)
                            putExtra("TABLE_ID", tableId)
                        }
                        startActivityForResult(intent, Constants.REQUEST_CODE_CREATE_ORDER)
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        // Có thể hiển thị ProgressBar nếu muốn
                    }
                }
            }
        }

        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.CENTER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == Constants.REQUEST_CODE_CREATE_ORDER && resultCode == AppCompatActivity.RESULT_OK){
            floorId?.let {
                viewModel.getFloorById(it)
            }
        }
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