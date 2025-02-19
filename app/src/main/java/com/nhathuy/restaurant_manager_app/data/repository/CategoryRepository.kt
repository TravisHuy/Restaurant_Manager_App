package com.nhathuy.restaurant_manager_app.data.repository

import com.nhathuy.restaurant_manager_app.data.api.CategoryService
import com.nhathuy.restaurant_manager_app.data.model.Category
import javax.inject.Inject
/**
 * The repository class for the category service.
 * This class is used to interact with the category service to perform CRUD operations on categories.
 *
 * @version 0.1
 * @since 18-02-2025
 * @author TravisHuy
 */
class CategoryRepository @Inject constructor(private val categoryService: CategoryService) {
    suspend fun addCategory(category: Category) = categoryService.addCategory(category)
}