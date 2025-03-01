package com.nhathuy.restaurant_manager_app.viewmodel

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhathuy.restaurant_manager_app.data.api.MenuItemService
import com.nhathuy.restaurant_manager_app.data.dto.MenuItemDTO
import com.nhathuy.restaurant_manager_app.data.model.MenuItem
import com.nhathuy.restaurant_manager_app.data.repository.MenuItemRepository
import com.nhathuy.restaurant_manager_app.resource.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import javax.inject.Inject

/**
 * MenuItemViewModel for managing menu item-related operations.
 *
 * @since 0.1
 * @version 19-02-2025
 * @author TravisHuy
 *
 * @param menuItemRepository the repository for menu item-related operations
 *
 */
class MenuItemViewModel @Inject constructor(private val repository: MenuItemRepository):ViewModel(){

    private val _menuItemsState = MutableStateFlow<Resource<List<MenuItem>>>(Resource.Success(emptyList()))
    val menuItemsState: StateFlow<Resource<List<MenuItem>>> = _menuItemsState.asStateFlow()

    private val _createMenuItemState = MutableStateFlow<Resource<MenuItem>?>(null)
    val createMenuItemState: StateFlow<Resource<MenuItem>?> = _createMenuItemState.asStateFlow()

    private val _menuItemImageState = MutableStateFlow<Resource<Uri>?>(null)
    val menuItemImageState: StateFlow<Resource<Uri>?> = _menuItemImageState.asStateFlow()

    private val _addNoteMenuItemState = MutableStateFlow<Resource<MenuItem>?>(null)
    val addNoteMenuItemState: StateFlow<Resource<MenuItem>?> = _addNoteMenuItemState.asStateFlow()

    fun getAllMenuItems() {
        viewModelScope.launch {
            repository.getAllMenuItems()
                .catch { e -> _menuItemsState.value = Resource.Error(e.message ?: "Unknown error") }
                .collect { _menuItemsState.value = it }
        }
    }

    fun addMenuItem(menuItemDTO: MenuItemDTO, imageFile: MultipartBody.Part?, categoryId: String) {
        viewModelScope.launch {
            repository.addMenuItem(menuItemDTO, imageFile, categoryId)
                .catch { e ->
                    val errorMessage = when(e) {
                        is HttpException -> "Error: ${e.response()?.errorBody()?.string()}"
                        else -> "Error: ${e.message}"
                    }
                    _createMenuItemState.value = Resource.Error(errorMessage) }
                .collect { _createMenuItemState.value = it }
        }
    }

    fun getMenuItemImage(menuItemId: String) {
        viewModelScope.launch {
            repository.getMenuItemImage(menuItemId)
                .catch { e -> _menuItemImageState.value = Resource.Error(e.message ?: "Unknown error") }
                .collect { _menuItemImageState.value = it }
        }
    }

    fun addNoteMenuItem(id:String , note:String){
        viewModelScope.launch {
            repository.addNoteMenuItem(id,note)
                .catch { e -> _addNoteMenuItemState.value = Resource.Error(e.message ?: "Unknown error") }
                .collect { _addNoteMenuItemState.value = it }
        }

    }

    // Reset status flows
    fun resetCreateStatus() {
        _createMenuItemState.value = null
        _menuItemImageState.value = null
    }
}