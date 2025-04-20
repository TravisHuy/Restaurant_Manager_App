package com.nhathuy.restaurant_manager_app.data.repository

import com.google.gson.JsonObject
import com.nhathuy.restaurant_manager_app.data.api.PaymentService
import com.nhathuy.restaurant_manager_app.oauth2.response.PaymentCallbackResponse
import com.nhathuy.restaurant_manager_app.oauth2.response.PaymentUrlResponse
import com.nhathuy.restaurant_manager_app.oauth2.response.QrCodePaymentResponse
import com.nhathuy.restaurant_manager_app.oauth2.response.TestPaymentResponse
import com.nhathuy.restaurant_manager_app.resource.Resource
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject
/**
 * Repository for managing payment-related operations.
 *
 * @since 0.1
 * @version 20-04-2025
 * @author TravisHuy
 *
 * @param paymentService the service for payment-related operations
 */
class PaymentRepository @Inject constructor(private val paymentService: PaymentService) {


    suspend fun createVnPayment(orderId: String) : Resource<PaymentUrlResponse> {
        return try {
            val response = paymentService.createVnPayment(orderId)
            handleResponse(response)
        }
        catch (e:Exception){
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }


    suspend fun createVnPayQrPayment(orderId:String) : Resource<QrCodePaymentResponse> {
        return try {
            val response = paymentService.createVnPayQrPayment(orderId)
            handleResponse(response)
        }
        catch (e:Exception){
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun createVnPayTestPayment(orderId: String) : Resource<TestPaymentResponse> {
        return try {
            val response = paymentService.createVnPayTestPayment(orderId)
            handleResponse(response)
        }
        catch (e:Exception){
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun processVnPayCallback(params: Map<String,String>) : Resource<PaymentCallbackResponse> {
        return try {
            val response = paymentService.processVnPayCallback(params)
            handleResponse(response)
        }
        catch (e:Exception){
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }
    /**
     * Generic function to handle API response and convert them to response object
     *
     * @param response the Retrofit response to handle
     * @return Resource.Success with data if the Response is successful, Resource.Error otherwise
     */
    private fun <T> handleResponse(response: Response<T>): Resource<T> {
        return if(response.isSuccessful && response.body() != null) {
            Resource.Success(response.body()!!)
        }
        else{
            val errorMsg = try {
                val errorBody = response.errorBody()?.string()
                val message = JSONObject(errorBody ?: "").optString("message")
                if (message.isNotEmpty()) message else "Error: ${response.code()} - ${response.message()}"
            } catch (e: Exception) {
                "Error: ${response.code()} - ${response.message()}"
            }
            Resource.Error(errorMsg)
        }
    }
}