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
    suspend fun addMenuItem(menuItemDTO: MenuItemDTO, imageFile: MultipartBody.Part?, categoryId: String): Flow<Resource<MenuItem>> = flow {
        emit(Resource.Loading())
        try {
            // Convert menuItemDto to json
            val menuItemJson = JSONObject().apply {
                put("name", menuItemDTO.name)
                put("description", menuItemDTO.description)
                put("price", menuItemDTO.price)
                put("categoryId", categoryId)
                put("available", menuItemDTO.available)
            }

            // Create image body parts
            val menuItemPart = menuItemJson.toString().toRequestBody("application/json".toMediaTypeOrNull())

            // Create image part if image exists
            val imagePart = imageFile?.let {
                MultipartBody.Part.createFormData(
                    "image",
                    it.body.contentType()?.let { contentType ->
                        "image.${contentType.subtype}" // Generate a filename based on content type
                    } ?: "image.jpg", // Default filename if content type is null
                    it.body
                )
            }

            val response = menuItemService.createMenuItem(menuItemPart, imagePart, categoryId)

            // Check if the response is successful and extract the body
            if (response.isSuccessful) {
                val menuItem = response.body()
                if (menuItem != null) {
                    emit(Resource.Success(menuItem))
                } else {
                    emit(Resource.Error("Response body is null"))
                }
            } else {
                emit(Resource.Error("Error adding menu item: ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error adding menu item: ${e.message}"))
        }
    }

    /**
     * Gets the image of a menu item.
     *
     * @param id the ID of the menu item
     * @return the image of the menu item
     */

    suspend fun getMenuItemImage(id: String): Flow<Resource<Uri>> = flow {
        emit(Resource.Loading())
        try {
            val response = menuItemService.getMenuItemImage(id)
            if (response.isSuccessful) {
                response.body()?.let { responseBody ->
                    try {
                        // Create temporary file
                        val tempFile = File.createTempFile("menu_item_", ".jpg")
                        tempFile.outputStream().use { outputStream ->
                            responseBody.byteStream().use { inputStream ->
                                inputStream.copyTo(outputStream)
                            }
                        }
                        // Convert File to Uri
                        val uri = Uri.fromFile(tempFile)
                        emit(Resource.Success(uri))
                    } catch (e: Exception) {
                        emit(Resource.Error("Error processing image: ${e.message}"))
                    }
                } ?: emit(Resource.Error("Response body is null"))
            } else {
                val errorMessage = when (response.code()) {
                    404 -> "Image not found"
                    else -> "Error fetching image: ${response.message()}"
                }
                emit(Resource.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Network error: ${e.message}"))
        }
    }

    suspend fun getAllMenuItems(): Flow<Resource<List<MenuItem>>> = flow {
        emit(Resource.Loading())
        try {
            val response = menuItemService.getAllMenuItems()
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error("Empty response"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    JSONObject(errorBody ?: "").getString("message")
                } catch (e: Exception) {
                    response.message() ?: "Unknown error"
                }
                emit(Resource.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Network error: ${e.message}"))
        }
    }

    suspend fun addNoteMenuItem(id:String , note:String) : Flow<Resource<MenuItem>> = flow {
        emit(Resource.Loading())
        try {
            val notePart = note.toRequestBody("text/plain".toMediaTypeOrNull())
            val response = menuItemService.addNoteToMenuItem(id,notePart)

            if(response.isSuccessful){
                response.body()?.let {
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error("Empty response"))
            }
            else {
                val errorBody  = response.errorBody()?.string()
                val errorMessage = try{
                    JSONObject(errorBody ?: "").getString("message")
                } catch (e:Exception){
                    response.message() ?: "Unknown error"
                }
                emit(Resource.Error(errorMessage))
            }
        }
        catch (e:Exception){
            emit(Resource.Error("Network error: ${e.message}"))
        }
    }
}