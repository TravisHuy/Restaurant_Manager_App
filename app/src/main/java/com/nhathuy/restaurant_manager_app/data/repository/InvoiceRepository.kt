package com.nhathuy.restaurant_manager_app.data.repository

import com.nhathuy.restaurant_manager_app.data.api.InvoiceService
import com.nhathuy.restaurant_manager_app.data.model.Invoice
import com.nhathuy.restaurant_manager_app.oauth2.request.InvoiceRequest
import com.nhathuy.restaurant_manager_app.resource.Resource
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import java.lang.Exception
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class InvoiceRepository @Inject constructor(private val invoiceService: InvoiceService){

    suspend fun addInvoice(invoiceRequest: InvoiceRequest): Flow<Resource<Invoice>> = flow {
        emit(Resource.Loading())
        try {
            val response = invoiceService.addInvoice(invoiceRequest)
            if (response.isSuccessful) {
                response.body()?.let { invoice ->
                    emit(Resource.Success(invoice))
                } ?: emit(Resource.Error("Response body is null"))
            } else {
                emit(Resource.Error("Failed : ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Network error: ${e.localizedMessage ?: "Unknown error"}"))
        } catch (e: IOException) {
            emit(Resource.Error("Network error: Please check your internet connection"))
        } catch (e: Exception) {
            emit(Resource.Error("Unexpected error: ${e.localizedMessage ?: "Unknown error"}"))
        }
    }

    suspend fun getAllInvoice() : Flow<Resource<List<Invoice>>>  = flow {
        emit(Resource.Loading())
        try {
            val response = invoiceService.getAllInvoices()
            if(response.isSuccessful){
                response.body()?.let {
                    invoices ->
                    Resource.Success(invoices)
                }
            }
        }
        catch (e:HttpException){
            emit(Resource.Error("Network error: ${e.localizedMessage ?: "Unknown error"}"))
        }
    }
}