package com.nhathuy.restaurant_manager_app.di.component

import com.nhathuy.restaurant_manager_app.data.api.RetrofitClient
import com.nhathuy.restaurant_manager_app.di.module.RepositoryModule
import com.nhathuy.restaurant_manager_app.di.module.RestaurantManagerModule
import com.nhathuy.restaurant_manager_app.di.module.ViewModelModule
import com.nhathuy.restaurant_manager_app.ui.LoginActivity
import com.nhathuy.restaurant_manager_app.ui.MainActivity
import com.nhathuy.restaurant_manager_app.ui.RegisterActivity
import dagger.Component
import javax.inject.Singleton

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
}