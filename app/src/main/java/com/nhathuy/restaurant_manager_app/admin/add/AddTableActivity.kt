package com.nhathuy.restaurant_manager_app.admin.add

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.data.dto.TableDto
import com.nhathuy.restaurant_manager_app.databinding.ActivityAddTableBinding
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.viewmodel.AuthViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.TableViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ViewModelFactory
import javax.inject.Inject
/**
 * Activity for adding a new table to the restaurant.
 * This screen allows the user to add a new table to the restaurant.
 *
 * @return 0.1
 * @since 14-02-2025
 * @author TravisHuy
 */
class AddTableActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTableBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: TableViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as RestaurantMangerApp).getRestaurantComponent().inject(this)

        setupClickListeners()
        observeViewModel()
    }
    private fun setupClickListeners(){
        binding.apply {
            btnAddTable.setOnClickListener {
                val tableNumber = edAddTableNumber.text.toString()
                val tableCapacity = edAddTableCapacity.text.toString()

                if(validateInput(tableNumber,tableCapacity)){
                    viewModel.addTable(TableDto(tableNumber.toInt(),tableCapacity.toInt(),available = true),"1")
                }
            }
        }
    }
    private fun observeViewModel(){
        viewModel.addTableResult.observe(this){
            result ->
            when(result){
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    cleanInput()
                }
                is Resource.Error -> {
                    Toast.makeText(this,result.message, Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }
    private fun cleanInput(){
        binding.edAddTableNumber.text?.clear()
        binding.edAddTableCapacity.text?.clear()
    }
    private fun validateInput(tableNumber:String,tableCapacity:String):Boolean{
        var isValid = true

        if(tableNumber.isEmpty() || tableNumber.isBlank()){
            binding.layoutAddTableNumber.error = "Table number is required"
            isValid = false
        }
        else{
            binding.layoutAddTableNumber.error = null
        }
        if(tableCapacity.isEmpty() || tableCapacity.isBlank()){
            binding.edAddTableCapacity.error = "Table capacity is required"
            isValid = false
        }
        else{
            binding.layoutAddTableCapacity.error = null
        }

        return isValid
    }
}