package com.nhathuy.restaurant_manager_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

/**
 * A factory class for creating ViewModel instances using dependency injection with Dagger.
 *
 * This factory allows injecting multiple ViewModels into a map, enabling dynamic ViewModel creation.
 *
 * @property creators A map of ViewModel classes to their providers.
 *
 * @version 0.1
 * @since 07-02-2025
 * @return TravisHuy
 */
class ViewModelFactory @Inject constructor(
    private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {

    // Create a ViewModel instance of the based on the given class type.
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val creator = creators[modelClass] ?: creators.entries.firstOrNull {
            modelClass.isAssignableFrom(it.key)
        }?.value ?: throw IllegalArgumentException("Unknown model class $modelClass")

        @Suppress("UNCHECKED_CAST")
        return creator.get() as T
    }
}