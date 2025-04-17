package com.nhathuy.restaurant_manager_app.oauth2.response

/**
 * The class is response for test payment info
 *
 * @version 0.1
 * @since 17-04-2025
 * @author TravisHuy
 */
data class TestPaymentResponse(
    val paymentUrl:String,
    val orderId:String,
    val testCardInfo:Map<String,String>
)
