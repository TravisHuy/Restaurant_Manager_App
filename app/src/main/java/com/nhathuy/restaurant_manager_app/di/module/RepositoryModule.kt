package com.nhathuy.restaurant_manager_app.di.module

import com.nhathuy.restaurant_manager_app.data.api.AuthService
import com.nhathuy.restaurant_manager_app.data.repository.AuthRepository
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
    fun provideAuthRepository(authService: AuthService):AuthRepository {
        return AuthRepository(authService)
    }
}