package com.nhathuy.restaurant_manager_app.di.component

import com.nhathuy.restaurant_manager_app.admin.add.AddCategoryActivity
import com.nhathuy.restaurant_manager_app.admin.add.AddFloorActivity
import com.nhathuy.restaurant_manager_app.admin.add.AddMenuItemActivity
import com.nhathuy.restaurant_manager_app.admin.add.AddTableActivity
import com.nhathuy.restaurant_manager_app.admin.all.AllFloorActivity
import com.nhathuy.restaurant_manager_app.admin.fragment.AdminNotificationFragment
import com.nhathuy.restaurant_manager_app.admin.fragment.DashBoardFragment
import com.nhathuy.restaurant_manager_app.admin.fragment.FloorTableAdminFragment
import com.nhathuy.restaurant_manager_app.admin.fragment.InvoicesAdminFragment
import com.nhathuy.restaurant_manager_app.admin.fragment.MenuItemsAdminFragment
import com.nhathuy.restaurant_manager_app.admin.fragment.OrderAdminFragment
import com.nhathuy.restaurant_manager_app.admin.fragment.StaffManagementAdminFragment
import com.nhathuy.restaurant_manager_app.admin.login.LoginAdminActivity
import com.nhathuy.restaurant_manager_app.admin.register.RegisterAdminActivity
import com.nhathuy.restaurant_manager_app.data.api.RetrofitClient
import com.nhathuy.restaurant_manager_app.di.module.NotificationModule
import com.nhathuy.restaurant_manager_app.di.module.RepositoryModule
import com.nhathuy.restaurant_manager_app.di.module.RestaurantManagerModule
import com.nhathuy.restaurant_manager_app.di.module.ViewModelModule
import com.nhathuy.restaurant_manager_app.fragment.MapFragment
import com.nhathuy.restaurant_manager_app.fragment.MoreFragment
import com.nhathuy.restaurant_manager_app.fragment.OrderFragment
import com.nhathuy.restaurant_manager_app.fragment.ProvisionalBillFragment
import com.nhathuy.restaurant_manager_app.fragment.SubTotalFragment
import com.nhathuy.restaurant_manager_app.service.WebSocketService
import com.nhathuy.restaurant_manager_app.ui.MenuItemActivity
import com.nhathuy.restaurant_manager_app.ui.LoginActivity
import com.nhathuy.restaurant_manager_app.ui.MainActivity
import com.nhathuy.restaurant_manager_app.ui.MainActivity2
import com.nhathuy.restaurant_manager_app.ui.OrderItemActivity
import com.nhathuy.restaurant_manager_app.ui.OrderPaymentActivity
import com.nhathuy.restaurant_manager_app.ui.RegisterActivity
import com.nhathuy.restaurant_manager_app.ui.SplashActivity
import com.nhathuy.restaurant_manager_app.ui.TestActivity
import dagger.Component
import javax.inject.Singleton

/**
 * This interface is used  to create the RestaurantManagerComponent class
 * This class is used to inject the dependencies into the activities.
 *
 * @version 0.1
 * @since 07-02-2025
 * @author TravisHuy
 */
@Singleton
@Component(modules = [
    RestaurantManagerModule::class,
    RetrofitClient::class,
    RepositoryModule::class,
    ViewModelModule::class,
    NotificationModule::class
])
interface RestaurantMangerComponent {
    fun inject(loginActivity: LoginActivity)
    fun inject(registerActivity: RegisterActivity)
    fun inject(mainActivity: MainActivity)
    fun inject(addTableActivity: AddTableActivity)
    fun inject(addFloorActivity: AddFloorActivity)
    fun inject(mapFragment: MapFragment)
//    fun inject(orderFragment: OrderAdminFragment)
//    fun inject(subTotalFragment: SubTotalFragment)
    fun inject(moreFragment: MoreFragment)

    fun inject(mainActivity2: MainActivity2)
    fun inject(allFloorActivity: AllFloorActivity)
    fun inject(addCategoryActivity: AddCategoryActivity)
    fun inject(addMenuItemActivity: AddMenuItemActivity)
    fun inject(menuItemActivity: MenuItemActivity)
    fun inject(orderFragment: OrderFragment)
    fun inject(orderItemActivity: OrderItemActivity)
    fun inject(orderPaymentActivity: OrderPaymentActivity)
    fun inject(subTotalFragment: SubTotalFragment)
    fun inject(splashActivity: SplashActivity)
    fun inject(registerAdminActivity: RegisterAdminActivity)
    fun inject(loginAdminActivity: LoginAdminActivity)
    fun inject(dashBoardFragment: DashBoardFragment)
    fun inject(floorTableAdminFragment: FloorTableAdminFragment)
    fun inject(menuItemsAdminFragment: MenuItemsAdminFragment)
    fun inject(orderAdminFragment: OrderAdminFragment)
    fun inject(invoicesAdminFragment: InvoicesAdminFragment)
    fun inject(testActivity: TestActivity)
    fun inject(staffManagementAdminFragment: StaffManagementAdminFragment)
    fun inject(provisionalBillFragment: ProvisionalBillFragment)
    fun inject(adminNotificationFragment: AdminNotificationFragment)
    fun inject(webSocketService: WebSocketService)
}