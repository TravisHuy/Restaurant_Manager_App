package com.nhathuy.restaurant_manager_app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nhathuy.restaurant_manager_app.data.model.Category
import com.nhathuy.restaurant_manager_app.data.repository.CategoryRepository
import com.nhathuy.restaurant_manager_app.resource.Resource
import javax.inject.Inject

/**
 * CategoryViewModel for managing category-related operations.
 * This ViewModel handles operations such as adding new categories.
 *
 * @version 0.1
 * @since 18-02-2025
 * @author TravisHuy
 */
class CategoryViewModel @Inject constructor(private val categoryRepository: CategoryRepository) : ViewModel() {

    private val _addCategory = MutableLiveData<Resource<Category>>()
    val addCategory : LiveData<Resource<Category>> = _addCategory


    suspend fun addCategory(category: Category) {
        _addCategory.value = Resource.Loading()
        try {
            val response = categoryRepository.addCategory(category)
            _addCategory.value = Resource.Success(response)
        } catch (e: Exception) {
            _addCategory.value = Resource.Error("Error: ${e.message}")
        }
    }

}