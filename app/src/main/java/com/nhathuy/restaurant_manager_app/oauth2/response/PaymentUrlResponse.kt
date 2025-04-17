package com.nhathuy.restaurant_manager_app.oauth2.response

/**
 * response for payment url creation
 *
 * @version 0.1
 * @since 17-04-2025
 * @author TravisHuy
 */
data class PaymentUrlResponse(
    val paymentUrl:String,
    val orderId:String
)