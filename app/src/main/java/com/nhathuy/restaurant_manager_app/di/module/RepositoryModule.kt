package com.nhathuy.restaurant_manager_app.di.module

import com.nhathuy.restaurant_manager_app.data.api.AuthService
import com.nhathuy.restaurant_manager_app.data.repository.AuthRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Provides
    @Singleton
    fun provideAuthRepository(authService: AuthService):AuthRepository {
        return AuthRepository(authService)
    }
}