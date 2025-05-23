package com.nhathuy.restaurant_manager_app.di.module

import com.nhathuy.restaurant_manager_app.data.api.AdminNotificationService
import com.nhathuy.restaurant_manager_app.data.api.AuthService
import com.nhathuy.restaurant_manager_app.data.api.CategoryService
import com.nhathuy.restaurant_manager_app.data.api.FloorService
import com.nhathuy.restaurant_manager_app.data.api.InvoiceService
import com.nhathuy.restaurant_manager_app.data.api.MenuItemService
import com.nhathuy.restaurant_manager_app.data.api.OrderItemService
import com.nhathuy.restaurant_manager_app.data.api.OrderService
import com.nhathuy.restaurant_manager_app.data.api.PaymentService
import com.nhathuy.restaurant_manager_app.data.api.ReservationService
import com.nhathuy.restaurant_manager_app.data.api.TableService
import com.nhathuy.restaurant_manager_app.data.local.TokenManager
import com.nhathuy.restaurant_manager_app.data.model.Invoice
import com.nhathuy.restaurant_manager_app.data.repository.AdminNotificationRepository
import com.nhathuy.restaurant_manager_app.data.repository.AuthRepository
import com.nhathuy.restaurant_manager_app.data.repository.CategoryRepository
import com.nhathuy.restaurant_manager_app.data.repository.FloorRepository
import com.nhathuy.restaurant_manager_app.data.repository.InvoiceRepository
import com.nhathuy.restaurant_manager_app.data.repository.MenuItemRepository
import com.nhathuy.restaurant_manager_app.data.repository.OrderItemRepository
import com.nhathuy.restaurant_manager_app.data.repository.OrderRepository
import com.nhathuy.restaurant_manager_app.data.repository.PaymentRepository
import com.nhathuy.restaurant_manager_app.data.repository.ReservationRepository
import com.nhathuy.restaurant_manager_app.data.repository.TableRepository
import com.nhathuy.restaurant_manager_app.resource.AdManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * This class provides the repository module for the application.
 * The repository module is used to provide the repository classes for the application.
 *
 * @version 0.1
 * @since 07-02-2025
 * @author TravisHuy
 */
@Module
class RepositoryModule {
    /**
     * Provides the authentication repository for the application.
     *
     * @param authService The authentication service
     * @return The authentication repository
     */
    @Provides
    @Singleton
    fun provideAuthRepository(authService: AuthService,tokenManager: TokenManager):AuthRepository {
        return AuthRepository(authService,tokenManager)
    }
    /**
     * Provides the table repository for the application.
     *
     * @param tableService The table service
     * @return The table repository
     */
    @Provides
    @Singleton
    fun provideTableRepository(tableService: TableService):TableRepository {
        return TableRepository(tableService)
    }
    /**
     * Provides the floor repository for the application.
     *
     * @param floorService The floor service
     * @return The floor repository
     */
    @Provides
    @Singleton
    fun provideFloorRepository(floorService: FloorService): FloorRepository {
        return FloorRepository(floorService)
    }

    /**
     * Provides the category repository for the application.
     *
     * @param categoryService The category service
     * @return The category repository
     */
    @Provides
    @Singleton
    fun provideCategoryRepository(categoryService: CategoryService): CategoryRepository {
        return CategoryRepository(categoryService)
    }
    /**
     * Provides the menu_item repository for the application.
     *
     * @param menuItemService The category service
     * @return The menuItem repository
     */
    @Provides
    @Singleton
    fun provideMenuItemRepository(menuItemService: MenuItemService): MenuItemRepository {
        return MenuItemRepository(menuItemService)
    }

    /**
     * Provides the order repository for the application.
     *
     * @param orderService The category service
     * @return The order repository
     */
    @Provides
    @Singleton
    fun provideOrderRepository(orderService: OrderService): OrderRepository {
        return OrderRepository(orderService)
    }


    /**
     * Provides the reservation repository for the application.
     *
     * @param reservationService The category service
     * @return The reservation repository
     */
    @Provides
    @Singleton
    fun provideReservationRepository(reservationService: ReservationService): ReservationRepository {
        return ReservationRepository(reservationService)
    }

    /**
     * Provides the orderItem repository for the application.
     *
     * @param orderItemService The category service
     * @return The order item repository
     */
    @Provides
    @Singleton
    fun provideOrderItemRepository(orderItemService: OrderItemService): OrderItemRepository {
        return OrderItemRepository(orderItemService)
    }


    /**
     * Provides the orderItem repository for the application.
     *
     * @param invoiceService The category service
     * @return The invoice repository
     */
    @Provides
    @Singleton
    fun provideInvoiceRepository(invoiceService: InvoiceService): InvoiceRepository {
        return InvoiceRepository(invoiceService)
    }

    /**
     * Provides the orderItem repository for the application.
     *
     * @param invoiceService The category service
     * @return The invoice repository
     */
    @Provides
    @Singleton
    fun provideAdManager():AdManager {
        return AdManager()
    }

    /**
     * Provides the payment repository for the application.
     *
     * @param paymentService The payment service
     * @return The payment repository
     */
    @Provides
    @Singleton
    fun providePaymentRepository(paymentService: PaymentService):PaymentRepository {
        return PaymentRepository(paymentService)
    }

    /**
     * Provides the adminNotification repository for the application.
     *
     * @param adminNotificationService The notification service
     * @return The payment repository
     */
    @Provides
    @Singleton
    fun provideNotificationRepository(adminNotificationService: AdminNotificationService):AdminNotificationRepository {
        return AdminNotificationRepository(adminNotificationService)
    }
}