package com.nhathuy.restaurant_manager_app.data.api

import javax.inject.Qualifier

/**
 * Qualifier for the authentication Retrofit client
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthRetrofit

/**
 * Qualifier for the table Retrofit client
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TableRetrofit