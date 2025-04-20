package com.nhathuy.restaurant_manager_app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhathuy.restaurant_manager_app.data.repository.PaymentRepository
import com.nhathuy.restaurant_manager_app.oauth2.response.PaymentCallbackResponse
import com.nhathuy.restaurant_manager_app.oauth2.response.PaymentUrlResponse
import com.nhathuy.restaurant_manager_app.oauth2.response.QrCodePaymentResponse
import com.nhathuy.restaurant_manager_app.oauth2.response.TestPaymentResponse
import com.nhathuy.restaurant_manager_app.resource.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject
/**
 * PaymentViewModel for managing payment-related operations.
 *
 * @return 0.1
 * @since 20-04-2025
 * @author TravisHuy
 */
class PaymentViewModel @Inject constructor(private val paymentRepository: PaymentRepository) : ViewModel() {

    private val _paymentUrlResponse = MutableLiveData<Resource<PaymentUrlResponse>>()
    val paymentUrlResponse: LiveData<Resource<PaymentUrlResponse>> = _paymentUrlResponse

    private val _qrCodePaymentResponse = MutableLiveData<Resource<QrCodePaymentResponse>>()
    val qrCodePaymentResponse : LiveData<Resource<QrCodePaymentResponse>> = _qrCodePaymentResponse

    private val _testPaymentResponse = MutableLiveData<Resource<TestPaymentResponse>>()
    val testPaymentResponse: LiveData<Resource<TestPaymentResponse>> = _testPaymentResponse

    private val _paymentCallbackResponse = MutableLiveData<Resource<PaymentCallbackResponse>>()
    val paymentCallbackResponse : LiveData<Resource<PaymentCallbackResponse>> = _paymentCallbackResponse


    /**
     * creates a standard VNPay payment Url for an order
     *
     * @param orderId The Id of the order to be paid
     */
    fun createVnPayment(orderId:String){
        _paymentUrlResponse.value = Resource.Loading()
        viewModelScope.launch {
            val result = paymentRepository.createVnPayment(orderId)
            _paymentUrlResponse.value = result
        }
    }

    /**
     * creates a qr code payment for VNPay
     *
     * @param orderId The Id of the order to be paid
     */
    fun createVnPayQrPayment(orderId:String){
        _qrCodePaymentResponse.value = Resource.Loading()
        viewModelScope.launch {
            val result = paymentRepository.createVnPayQrPayment(orderId)
            _qrCodePaymentResponse.value = result
        }
    }

    /**
     * creates a test payment with VNPay(for testing payment)
     *
     *  @param orderId The Id of the order to be paid
     */
    fun createVnPayTestPayment(orderId: String) {
        _testPaymentResponse.value = Resource.Loading()
        viewModelScope.launch {
            val result = paymentRepository.createVnPayTestPayment(orderId)
            _testPaymentResponse.value = result
        }
    }

    /**
     *  process VnPay callback with payment result
     *
     * @param params Map of query parameters from VNPay callback
     */
    fun processVnPayCallback(params:Map<String,String>){
        _paymentCallbackResponse.value = Resource.Loading()
        viewModelScope.launch {
            val result = paymentRepository.processVnPayCallback(params)
            _paymentCallbackResponse.value = result
        }
    }
}