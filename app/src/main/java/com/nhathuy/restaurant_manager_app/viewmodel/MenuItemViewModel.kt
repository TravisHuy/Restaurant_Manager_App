package com.nhathuy.restaurant_manager_app.viewmodel

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhathuy.restaurant_manager_app.data.api.MenuItemService
import com.nhathuy.restaurant_manager_app.data.dto.MenuItemDTO
import com.nhathuy.restaurant_manager_app.data.repository.MenuItemRepository
import com.nhathuy.restaurant_manager_app.resource.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
class MenuItemViewModel @Inject constructor(private val menuItemRepository: MenuItemRepository):ViewModel(){

    /**
     * The state of the add menu item operation
     */
    private val _addMenuItemState = MutableStateFlow<Resource<Unit>>(Resource.Success(Unit))
    val addMenuItemState: StateFlow<Resource<Unit>> = _addMenuItemState

    /**
     * The state of the get menu item image operation
     */
    private val _menuItemImageState = MutableStateFlow<Resource<ByteArray>>(Resource.Success(
        ByteArray(0)
    ))
    val menuItemImageState: StateFlow<Resource<ByteArray>> = _menuItemImageState

    /**
     * Adds a new menu item to the system.
     *
     * @param menuItem the menu item to add
     * @param imageFile the image file of the menu item
     * @param categoryId the category ID of the menu item
     */
    fun addMenuItem(menuItem: MenuItemDTO, imageFile:MultipartBody.Part?, categoryId:String){
        viewModelScope.launch {
            _addMenuItemState.value = Resource.Loading()
            menuItemRepository.addMenuItem(menuItem, imageFile, categoryId).let {
                _addMenuItemState.value = it
            }
        }
    }

    /**
     * Gets the image of a menu item.
     *
     * @param id the ID of the menu item
     */
    fun getMenuItemImage(id:String){
        viewModelScope.launch {
            _menuItemImageState.value = Resource.Loading()
            menuItemRepository.getMenuItemImage(id).let {
                _menuItemImageState.value = it
            }
        }
    }

    /** reset the state of the add menu item operation */
    fun resetAddMenuItemState(){
        _addMenuItemState.value = Resource.Success(Unit)
    }
    /** reset the state of the get menu item image operation */
    fun resetMenuItemImageState(){
        _menuItemImageState.value = Resource.Success(ByteArray(0))
    }

}