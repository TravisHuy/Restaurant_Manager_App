package com.nhathuy.restaurant_manager_app.data.api

import com.nhathuy.restaurant_manager_app.data.model.Invoice
import com.nhathuy.restaurant_manager_app.oauth2.request.InvoiceRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Service for managing invoice-related operations.
 *
 * @return 0.1
 * @since 11-03-2025
 * @author TravisHuy
 */
interface InvoiceService {

    @POST("api/invoices/add")
    suspend fun addInvoice(@Body invoiceRequest: InvoiceRequest):Response<Invoice>

}