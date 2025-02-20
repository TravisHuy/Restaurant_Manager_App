package com.nhathuy.restaurant_manager_app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhathuy.restaurant_manager_app.data.model.Category
import com.nhathuy.restaurant_manager_app.data.repository.CategoryRepository
import com.nhathuy.restaurant_manager_app.resource.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * CategoryViewModel for managing category-related operations.
 * This ViewModel handles operations such as adding new categories.
 *
 * @version 0.1
 * @since 19-02-2025
 * @author TravisHuy
 */
class CategoryViewModel @Inject constructor(private val categoryRepository: CategoryRepository) : ViewModel() {

    private val _addCategory = MutableLiveData<Resource<Category>>()
    val addCategory : LiveData<Resource<Category>> = _addCategory

    private val _allCategories = MutableLiveData<Resource<List<Category>>>()
    val allCategories : LiveData<Resource<List<Category>>> = _allCategories

    private val _categoryById = MutableLiveData<Resource<Category>>()
    val categoryById : LiveData<Resource<Category>> = _categoryById


   fun addCategory(category: Category) {
        viewModelScope.launch {
            _addCategory.value = Resource.Loading()
            try {
                val response = categoryRepository.addCategory(category)
                _addCategory.value = Resource.Success(response)
            } catch (e: Exception) {
                _addCategory.value = Resource.Error("Error: ${e.message}")
            }
        }
    }

    fun getAllCategories() {
        viewModelScope.launch {
            _allCategories.value = Resource.Loading()
            try {
                val response = categoryRepository.getAllCategories()
                if(response.isEmpty()){
                    _allCategories.value = Resource.Error("No categories found")
                } else {
                    _allCategories.value = Resource.Success(response)
                }
            } catch (e: Exception) {
                _allCategories.value = Resource.Error("Error: ${e.message}")
            }
        }
    }

    fun getCategoryById(id: String) {
        viewModelScope.launch {
            _categoryById.value = Resource.Loading()
            try {
                val response = categoryRepository.getCategoryById(id)
                if (response  != null) {
                    _categoryById.value = Resource.Success(response)
                } else {
                    _categoryById.value = Resource.Error("No floor found")
                }
            } catch (e: Exception) {
                _categoryById.value = Resource.Error("Error: ${e.message}")
            }
        }
    }
}