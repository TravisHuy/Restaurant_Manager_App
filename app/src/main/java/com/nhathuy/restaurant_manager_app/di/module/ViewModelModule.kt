package com.nhathuy.restaurant_manager_app.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nhathuy.restaurant_manager_app.di.key.ViewModelKey
import com.nhathuy.restaurant_manager_app.viewmodel.AuthViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.CategoryViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.FloorViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.InvoiceViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.MenuItemViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.OrderItemViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.OrderViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.PaymentViewModel
import com.nhathuy.restaurant_manager_app.viewmodel.ReservationViewModel
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


    /**
     * Binds the CategoryViewModel class to the ViewModel class.
     *
     * @param categoryViewModel The category view model
     * @return The view model
     */
    @Binds
    @IntoMap
    @ViewModelKey(CategoryViewModel::class)
    abstract fun bindCategoryViewModel(categoryViewModel: CategoryViewModel): ViewModel


    /**
     * Binds the MenuItemViewModel class to the ViewModel class.
     *
     * @param menuItemViewModel The floor view model
     * @return The view model
     */
    @Binds
    @IntoMap
    @ViewModelKey(MenuItemViewModel::class)
    abstract fun bindMenuItemViewModel(menuItemViewModel: MenuItemViewModel): ViewModel



    /**
     * Binds the OrderViewModel class to the ViewModel class.
     *
     * @param orderViewModel The floor view model
     * @return The view model
     */
    @Binds
    @IntoMap
    @ViewModelKey(OrderViewModel::class)
    abstract fun bindOrderViewModel(orderViewModel: OrderViewModel): ViewModel


    /**
     * Binds the ReservationViewModel class to the ViewModel class.
     *
     * @param reservationViewModel The reservation view model
     * @return The view model
     */
    @Binds
    @IntoMap
    @ViewModelKey(ReservationViewModel::class)
    abstract fun bindReservationViewModel(reservationViewModel: ReservationViewModel): ViewModel

    /**
     * Binds the OrderItemViewModel class to the ViewModel class.
     *
     * @param orderItemViewModel The reservation view model
     * @return The view model
     */
    @Binds
    @IntoMap
    @ViewModelKey(OrderItemViewModel::class)
    abstract fun bindOrderItemViewModel(orderItemViewModel: OrderItemViewModel): ViewModel


    /**
     * Binds the OrderItemViewModel class to the ViewModel class.
     *
     * @param  invoiceViewModel The reservation view model
     * @return The view model
     */
    @Binds
    @IntoMap
    @ViewModelKey(InvoiceViewModel::class)
    abstract fun bindInvoiceViewModel(invoiceViewModel: InvoiceViewModel): ViewModel

    /**
     * Binds the PaymentViewModel class to the ViewModel class.
     *
     * @param  paymentViewModel The payment view model
     * @return The view model
     */
    @Binds
    @IntoMap
    @ViewModelKey(PaymentViewModel::class)
    abstract fun bindPaymentViewModel(paymentViewModel: PaymentViewModel): ViewModel
}