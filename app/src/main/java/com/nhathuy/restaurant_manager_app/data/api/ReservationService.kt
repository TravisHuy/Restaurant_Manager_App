package com.nhathuy.restaurant_manager_app.data.api

import com.nhathuy.restaurant_manager_app.data.dto.ReservationDTO
import com.nhathuy.restaurant_manager_app.data.model.Reservation
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface ReservationService {
    @POST("api/reservations/add/{tableId}")
    suspend fun addReservation(@Path("tableId") tableId: String,
                               @Body reservation: ReservationDTO):Reservation
}