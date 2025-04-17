package com.nhathuy.restaurant_manager_app.oauth2.response

/**
 * The class is response for payment callback
 *
 * @version 0.1
 * @since 17-04-2025
 * @author TravisHuy
 */
data class PaymentCallbackResponse(
    val status:String,
    val message:String,
    val invoiceId:String,
    val amount:Double,
    val paymentTime: String
)
