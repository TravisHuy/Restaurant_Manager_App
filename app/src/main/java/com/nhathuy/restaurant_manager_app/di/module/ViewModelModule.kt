package com.nhathuy.restaurant_manager_app.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nhathuy.restaurant_manager_app.di.key.ViewModelKey
import com.nhathuy.restaurant_manager_app.viewmodel.AuthViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.FloorViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.TableViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * This class provides the ViewModelModule module for the application.
 * The ViewModelModule module is used to provide the view model classes for the application.
 *
 * @version 0.1
 * @since 07-02-2025
 * @author TravisHuy
 */
@Module
abstract class ViewModelModule {
    /**
     * Binds the AuthViewModel class to the ViewModel class.
     *
     * @param authViewModel The authentication view model
     * @return The view model
     */
    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(authViewModel: AuthViewModel): ViewModel

    /**
     * Binds the ViewModelFactory class to the ViewModelProvider.Factory class.
     *
     * @param factory The view model factory
     * @return The view model provider factory
     */
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    /**
     * Binds the TableViewModel class to the ViewModel class.
     *
     * @param tableViewModel The table view model
     * @return The view model
     */
    @Binds
    @IntoMap
    @ViewModelKey(TableViewModel::class)
    abstract fun bindTableViewModel(tableViewModel: TableViewModel): ViewModel

    /**
     * Binds the FloorViewModel class to the ViewModel class.
     *
     * @param floorViewModel The floor view model
     * @return The view model
     */
    @Binds
    @IntoMap
    @ViewModelKey(FloorViewModel::class)
    abstract fun bindFloorViewModel(floorViewModel: FloorViewModel): ViewModel
}