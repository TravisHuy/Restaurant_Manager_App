package com.nhathuy.restaurant_manager_app.oauth2.request

import com.nhathuy.restaurant_manager_app.data.model.PaymentMethod

data class InvoiceRequest(
    val orderId:String,
    val totalAmount:Double,
    val paymentMethod:PaymentMethod
)