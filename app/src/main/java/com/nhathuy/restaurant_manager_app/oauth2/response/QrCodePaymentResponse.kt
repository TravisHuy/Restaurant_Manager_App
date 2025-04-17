package com.nhathuy.restaurant_manager_app.oauth2.response

/**
 * The class response for qr code payment
 *
 * @version 0.1
 * @since 17-04-2025
 * @author TravisHuy
 */
data class QrCodePaymentResponse(
    val qrCodeImage:String,
    val paymentUrl:String,
    val orderId:String,
    val amount:Double,
    val customerName:String,
    val instructions:String
)
