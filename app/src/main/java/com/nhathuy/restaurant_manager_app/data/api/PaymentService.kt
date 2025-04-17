package com.nhathuy.restaurant_manager_app.data.api

import com.nhathuy.restaurant_manager_app.oauth2.response.PaymentCallbackResponse
import com.nhathuy.restaurant_manager_app.oauth2.response.PaymentUrlResponse
import com.nhathuy.restaurant_manager_app.oauth2.response.QrCodePaymentResponse
import com.nhathuy.restaurant_manager_app.oauth2.response.TestPaymentResponse
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

/**
 * Restaurant Api service for payment-related endpoints
 *
 * Interface to communicate with the restaurant backend payment api
 * using retrofit for http requests.
 */
interface PaymentService {

    /**
     * create a standard VNP payment URL for an order
     */
    @POST("api/payments/vnpay/create/{orderId}")
    suspend fun createVnPayment(@Path("orderId") orderId:String):Response<PaymentUrlResponse>

    /**
     * create a qr code payment VNPay
     *
     * @param orderId Id to the order to the paid
     * @return Response containing QR code image and payment details.
     */
    @POST("api/payments/vnpay/qr/{orderId}")
    suspend fun createVnPayQrPayment(@Path("orderId") orderId: String) : Response<QrCodePaymentResponse>

    /**
     * create a test payment with VNPAY (for testing enviroment)
     *
     * @param orderId ID of the order to be paid
     * @return Response containing payment URL and test card information
     */
    suspend fun createVnPayTestPayment(@Path("orderId") orderId:String) : Response<TestPaymentResponse>

    /**
     * process VNPay callback with payment result
     *
     * @param params Map of query parameters from VNPay callback
     * @return Response containing payment result status
     */
    suspend fun processVnPayCallback(@QueryMap params: Map<String,String>): Response<PaymentCallbackResponse>
}