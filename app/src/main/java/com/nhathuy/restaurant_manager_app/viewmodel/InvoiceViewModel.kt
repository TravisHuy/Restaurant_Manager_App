package com.nhathuy.restaurant_manager_app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhathuy.restaurant_manager_app.data.model.Invoice
import com.nhathuy.restaurant_manager_app.data.repository.InvoiceRepository
import com.nhathuy.restaurant_manager_app.oauth2.request.InvoiceRequest
import com.nhathuy.restaurant_manager_app.resource.Resource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class InvoiceViewModel @Inject constructor(private val invoiceRepository: InvoiceRepository):ViewModel() {

    private val _addInvoice = MutableLiveData<Resource<Invoice>>()
    val addInvoice : LiveData<Resource<Invoice>>  = _addInvoice

    fun addInvoice(invoiceRequest: InvoiceRequest){
        viewModelScope.launch {
            invoiceRepository.addInvoice(invoiceRequest).collect {
                result ->
                _addInvoice.value = result
            }
        }
    }
}