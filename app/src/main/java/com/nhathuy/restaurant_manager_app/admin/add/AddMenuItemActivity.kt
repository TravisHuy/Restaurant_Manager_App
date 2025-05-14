package com.nhathuy.restaurant_manager_app.admin.add

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.data.dto.MenuItemDTO
import com.nhathuy.restaurant_manager_app.databinding.ActivityAddMenuItemBinding
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.viewmodel.CategoryViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.MenuItemViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

/**
 * Activity for adding a new menu item.
 * This activity allows the user to add a new menu item to the system.
 *
 * @since 0.1
 * @version 20-02-2025
 * @author TravisHuy

 */
class AddMenuItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMenuItemBinding
    private var selectImageUri: Uri? = null
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val categoryViewModel: CategoryViewModel by viewModels { viewModelFactory }
    private val menuItemViewModel : MenuItemViewModel by viewModels { viewModelFactory }

    /**
     * Activity result launcher for picking an image from the gallery.
     */
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){
        uri : Uri? ->
        uri?.let {
            selectImageUri = it
            binding.menuImage.setImageURI(it)
            binding.addProductTvCount.text= "Image selected"
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMenuItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (application as RestaurantMangerApp).getRestaurantComponent().inject(this)

        setupListeners()
        observeViewModel()
        setupCategoryAdapter()

        menuItemViewModel.resetCreateStatus()
    }


    private fun setupListeners() {
        binding.addProductImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }
        binding.btnAddMenuItem.setOnClickListener {
            if(validateInputs()){
                submitMenuItem()
            }
        }
        binding.addCategory.setOnClickListener {
            startActivity(Intent(this,AddCategoryActivity::class.java))
        }
    }
    private fun submitMenuItem(){
        val name = binding.edAddMenuName.text.toString()
        val price = binding.edAddMenuPrice.text.toString().toDouble()
        val description = binding.edAddMenuDescription.text.toString()
        val category = binding.categoryAutoComplete.text.toString()

        val categoryId = categoryViewModel.allCategories.value?.data?.find { it.name == category }?.id ?: ""

        // Create MenuItemDTO with the proper categoryId
        val menuItem = MenuItemDTO(
            name = name,
            description = description,
            price = price,
            imageData="",
            categoryId = categoryId,
            available = true
        )

        val imageFile = selectImageUri?.let {
            uri ->
            val file = createTempFileFromUri(uri)
            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image", file.name, requestBody)
        }
        menuItemViewModel.addMenuItem(menuItem, imageFile, categoryId)
    }
    /**
     * Validates the user inputs.
     *
     * @return true if the inputs are valid, false otherwise
     */
    private fun validateInputs():Boolean{
        var isValid = true

        // Validate name
        if (binding.edAddMenuName.text.isNullOrBlank()) {
            binding.layoutAddMenuName.error = "Name is required"
            isValid = false
        } else {
            binding.layoutAddMenuName.error = null
        }

        // Validate price
        if (binding.edAddMenuPrice.text.isNullOrBlank()) {
            binding.layoutAddMenuPrice.error = "Price is required"
            isValid = false
        } else {
            try {
                binding.edAddMenuPrice.text.toString().toDouble()
                binding.layoutAddMenuPrice.error = null
            } catch (e: NumberFormatException) {
                binding.layoutAddMenuPrice.error = "Invalid price format"
                isValid = false
            }
        }

        // Validate description
        if (binding.edAddMenuDescription.text.isNullOrBlank()) {
            binding.layoutAddMenuDescription.error = "Description is required"
            isValid = false
        } else {
            binding.layoutAddMenuDescription.error = null
        }

        // Validate category
        if (binding.categoryAutoComplete.text.isNullOrBlank()) {
            binding.layoutCategoryName.error = "Category is required"
            isValid = false
        } else {
            binding.layoutCategoryName.error = null
        }

        return isValid
    }
    /**
     * Creates a temporary file from a URI.
     *
     * @param uri the URI of the file
     * @return the temporary file
     */
    private fun createTempFileFromUri(uri:Uri):File{
        val inputStream = contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("temp_image", null, cacheDir)

        FileOutputStream(tempFile).use { outputStream ->
            inputStream?.copyTo(outputStream)
        }

        return tempFile
    }
    /**
     * Observes the view model.
     */
    private fun observeViewModel() {

        lifecycleScope.launch {
            menuItemViewModel.createMenuItemState.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {
                        showLoading(true)
                    }
                    is Resource.Success -> {
                        showLoading(false)
                        clearInput()
                        Toast.makeText(
                            this@AddMenuItemActivity,
                            "Menu item added successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is Resource.Error -> {
                        showLoading(false)
                        Toast.makeText(
                            this@AddMenuItemActivity,
                            state.message ?: "An error occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {}
                }
            }
        }
    }


    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnAddMenuItem.isEnabled = !show
    }
    /**
     * Sets up the category adapter.
     */
    private fun setupCategoryAdapter() {
        lifecycleScope.launch {
            categoryViewModel.getAllCategories()
            categoryViewModel.allCategories.observe(this@AddMenuItemActivity) {
                    resource ->
                when(resource){
                    is Resource.Success -> {
                        val categories = resource.data?.map { it.name } ?: emptyList()
                        val adapter = ArrayAdapter(
                            this@AddMenuItemActivity,
                            android.R.layout.simple_dropdown_item_1line,
                            categories
                        )
                        binding.categoryAutoComplete.setAdapter(adapter)
                    }
                    is Resource.Error -> {
                        Toast.makeText(this@AddMenuItemActivity, resource.message, Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        // do nothing
                    }
                }
            }
        }
    }

    private fun clearInput(){
        binding.edAddMenuName.text?.clear()
        binding.edAddMenuDescription.text?.clear()
        binding.edAddMenuPrice.text?.clear()
        binding.categoryAutoComplete.text?.clear()
        binding.menuImage.setImageURI(null)
        selectImageUri = null
    }
}