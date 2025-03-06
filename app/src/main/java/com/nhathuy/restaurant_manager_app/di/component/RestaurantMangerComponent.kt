package com.nhathuy.restaurant_manager_app.di.component

import com.nhathuy.restaurant_manager_app.admin.add.AddCategoryActivity
import com.nhathuy.restaurant_manager_app.admin.add.AddFloorActivity
import com.nhathuy.restaurant_manager_app.admin.add.AddMenuItemActivity
import com.nhathuy.restaurant_manager_app.admin.add.AddTableActivity
import com.nhathuy.restaurant_manager_app.admin.all.AllFloorActivity
import com.nhathuy.restaurant_manager_app.data.api.RetrofitClient
import com.nhathuy.restaurant_manager_app.di.module.RepositoryModule
import com.nhathuy.restaurant_manager_app.di.module.RestaurantManagerModule
import com.nhathuy.restaurant_manager_app.di.module.ViewModelModule
import com.nhathuy.restaurant_manager_app.fragment.MapFragment
import com.nhathuy.restaurant_manager_app.fragment.MoreFragment
import com.nhathuy.restaurant_manager_app.fragment.OrderFragment
import com.nhathuy.restaurant_manager_app.ui.MenuItemActivity
import com.nhathuy.restaurant_manager_app.ui.LoginActivity
import com.nhathuy.restaurant_manager_app.ui.MainActivity
import com.nhathuy.restaurant_manager_app.ui.MainActivity2
import com.nhathuy.restaurant_manager_app.ui.RegisterActivity
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
    ViewModelModule::class])
interface RestaurantMangerComponent {
    fun inject(loginActivity: LoginActivity)
    fun inject(registerActivity: RegisterActivity)
    fun inject(mainActivity: MainActivity)
    fun inject(addTableActivity: AddTableActivity)
    fun inject(addFloorActivity: AddFloorActivity)
    fun inject(mapFragment: MapFragment)
//    fun inject(orderFragment: OrderFragment)
//    fun inject(subTotalFragment: SubTotalFragment)
    fun inject(moreFragment: MoreFragment)

    fun inject(mainActivity2: MainActivity2)
    fun inject(allFloorActivity: AllFloorActivity)
    fun inject(addCategoryActivity: AddCategoryActivity)
    fun inject(addMenuItemActivity: AddMenuItemActivity)
    fun inject(menuItemActivity: MenuItemActivity)
    fun inject(orderFragment: OrderFragment)
}