package com.nhathuy.restaurant_manager_app.data.repository

import android.net.Uri
import android.util.Log
import com.nhathuy.restaurant_manager_app.data.api.MenuItemService
import com.nhathuy.restaurant_manager_app.data.dto.MenuItemDTO
import com.nhathuy.restaurant_manager_app.data.model.MenuItem
import com.nhathuy.restaurant_manager_app.resource.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import javax.inject.Inject

/**
 * Repository for managing menu item-related operations.
 *
 * @version 0.1
 * @since 19-02-2025
 * @author TravisHuy
 */
class MenuItemRepository @Inject constructor(private val menuItemService: MenuItemService) {

    /**
     * Adds a new menu item to the system.
     *
     * @param menuItemDTO the menu item to add
     * @param imageFile the image file of the menu item
     * @param categoryId the category ID of the menu item
     */
    suspend fun addMenuItem(menuItem: MenuItemDTO, imageFile: MultipartBody.Part?, categoryId: String) :Resource<Unit> {
        return try {

            // Convert DTO fields to RequestBody objects
            val nameRequestBody = menuItem.name.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionRequestBody = menuItem.description.toRequestBody("text/plain".toMediaTypeOrNull())
            val priceRequestBody = menuItem.price.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val availableRequestBody = menuItem.available.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val response = menuItemService.addMenuItem(
                name = nameRequestBody,
                description = descriptionRequestBody,
                price = priceRequestBody,
                available = availableRequestBody,
                image = imageFile,
                categoryId = categoryId
            )


            if(response.isSuccessful){
                Resource.Success(Unit)
            }
            else{
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val json = errorBody?.let { JSONObject(it) }
                    json?.getString("message") ?:"Unknown error occurred"
                }
                catch (e:Exception) {
                    "Failed to add menu item: ${response.message()}"
                }
                Resource.Error(errorMessage)
            }
        }
        catch (e:Exception){
            Resource.Error("Error adding menu item: ${e.message}")
        }
    }

    /**
     * Gets the image of a menu item.
     *
     * @param id the ID of the menu item
     * @return the image of the menu item
     */
    suspend fun getMenuItemImage(id:String) : Resource<ByteArray> {
        return try {
            val response = menuItemService.getMenuItemImage(id)

            if(response.isSuccessful){
                val bytes = response.body()?.bytes()
                if(bytes != null){
                    Resource.Success(bytes)
                }
                else{
                    Resource.Error("Failed to get image")
                }
            }
            else{
                Resource.Error("Failed to get image: ${response.message()}")
            }
        }
        catch (e:Exception){
            Resource.Error("Error getting image: ${e.message}")
        }
    }

    suspend fun getAllMenuItems(): Resource<List<MenuItem>> {
        return try {
            val response = menuItemService.getAllMenuItems()
            if (response.isSuccessful) {
                val menuItems = response.body()
                if (menuItems != null) {
                    Resource.Success(menuItems)
                } else {
                    Resource.Error("Server returned empty response")
                }
            } else {
                // Get more detailed error message from response
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val json = errorBody?.let { JSONObject(it) }
                    json?.getString("message") ?: "Server returned error: ${response.code()}"
                } catch (e: Exception) {
                    "Failed to get menu items: ${response.message() ?: "Unknown error"}"
                }
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Resource.Error("Network error: ${e.localizedMessage ?: e.message ?: "Unknown error occurred"}")
        }
    }
}