package com.nhathuy.restaurant_manager_app.admin.add

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.data.model.Category
import com.nhathuy.restaurant_manager_app.databinding.ActivityAddCategoryBinding
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.viewmodel.CategoryViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.FloorViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ViewModelFactory
import javax.inject.Inject

class AddCategoryActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAddCategoryBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: CategoryViewModel by viewModels { viewModelFactory }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as RestaurantMangerApp).getRestaurantComponent().inject(this)

        setupListeners()
        observeViewModel()
    }
    private fun setupListeners(){
        binding.btnAddCategory.setOnClickListener {
            val categoryName = binding.edAddCategoryName.text.toString()
            if(validateInput(categoryName)){
                viewModel.addCategory(Category("",categoryName))
            }
        }
    }


    private fun observeViewModel(){
        viewModel.addCategory.observe(this) {
            result ->
            when(result){
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    clearInput()
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun validateInput(categoryName: String): Boolean {
        if(categoryName.isEmpty()){
            binding.layoutAddCategoryName.error = "Category name cannot be empty"
            return false
        }
        return true
    }
    private fun clearInput(){
        binding.edAddCategoryName.text?.clear()
    }
}