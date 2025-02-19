package com.nhathuy.restaurant_manager_app.admin.all

import android.app.Dialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginTop
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.adapter.FloorAdapter
import com.nhathuy.restaurant_manager_app.data.dto.TableDto
import com.nhathuy.restaurant_manager_app.databinding.ActivityAllFloorBinding
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.viewmodel.FloorViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.TableViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ViewModelFactory
import javax.inject.Inject
/**
 * Activity to display list of floors.
 *
 * @return 0.1
 * @since 17-02-2025
 * @author TravisHuy
 */
class AllFloorActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAllFloorBinding
    private lateinit var adapter:FloorAdapter

    private var currentFloorId: String? = null
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: FloorViewModel by viewModels { viewModelFactory }
    private val tableViewModel : TableViewModel by viewModels  { viewModelFactory }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllFloorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as RestaurantMangerApp).getRestaurantComponent().inject(this)

        setupRecyclerView()
        setupListeners()
        observeViewModel()
        viewModel.getAllFloors()

    }

    private fun setupRecyclerView() {
        adapter = FloorAdapter { floor ->
            Toast.makeText(this, "Selected: ${floor.name}", Toast.LENGTH_SHORT).show()
            currentFloorId = floor.id
            showDialogAddTable(floor.id)
            binding.circleRecyclerView.toggleVisibility()
        }
        binding.circleRecyclerView.adapter = adapter
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
    }
    private fun observeViewModel() {
        viewModel.floors.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    resource.data?.let {
                        floors -> adapter.updateFloors(floors)

                    }
                }
                is Resource.Error -> {
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
    private fun showDialogAddTable(floorId:String) {
        val dialog =Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_add_table)

        val table_number=dialog.findViewById<TextInputEditText>(R.id.ed_add_table_number)
        val table_capacity=dialog.findViewById<TextInputEditText>(R.id.ed_add_table_capacity)
        val layoutTableNumber = dialog.findViewById<TextInputLayout>(R.id.layout_add_table_number)
        val layoutTableCapacity = dialog.findViewById<TextInputLayout>(R.id.layout_add_table_capacity)
        val btnAddTable = dialog.findViewById<MaterialButton>(R.id.btn_add_table)
        val btnClose = dialog.findViewById<ImageView>(R.id.ic_close)

        btnAddTable.setOnClickListener {
            val tableNumber = table_number.text.toString()
            val tableCapacity = table_capacity.text.toString()

            if(validateInput(tableNumber,tableCapacity,layoutTableNumber, layoutTableCapacity)){
                tableViewModel.addTable(TableDto(tableNumber.toInt(),tableCapacity.toInt()),floorId)


                tableViewModel.addTableResult.observe(this) {
                        result ->
                    when(result) {
                        is Resource.Loading -> {
                        }
                        is Resource.Success -> {
                            Toast.makeText(this, "Table added successfully", Toast.LENGTH_SHORT).show()
                            cleanInput(dialog)
                            currentFloorId?.let {
                                showDialogAddTable(it)
                            }
                        }
                        is Resource.Error -> {
                            Toast.makeText(this,result.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        }
        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.attributes?.windowAnimations=R.style.DialogAnimation;
        dialog.window?.setGravity(Gravity.CENTER)
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
}