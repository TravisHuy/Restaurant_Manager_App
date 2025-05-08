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

/**
 * Qualifier for the floor Retrofit client
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FloorRetrofit

/**
 * Qualifier for the category Retrofit client
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CategoryRetrofit

/**
 * Qualifier for the category Retrofit client
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MenuItemRetrofit



/**
 * Qualifier for the order Retrofit client
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OrderRetrofit

/**
 * Qualifier for the reservation Retrofit client
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ReservationRetrofit

/**
 * Qualifier for the reservation Retrofit client
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OrderItemRetrofit

/**
 * Qualifier for the invoice Retrofit client
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class InvoiceRetrofit

/**
 * Qualifier for the payment Retrofit client
 */

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PaymentRetrofit


/**
 * Qualifier for the admin notification Retrofit client
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AdminNotificationRetrofit