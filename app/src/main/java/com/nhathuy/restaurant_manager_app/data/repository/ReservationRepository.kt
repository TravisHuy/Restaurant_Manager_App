package com.nhathuy.restaurant_manager_app.data.repository

import com.nhathuy.restaurant_manager_app.data.api.ReservationService
import com.nhathuy.restaurant_manager_app.data.dto.ReservationDTO
import javax.inject.Inject

class ReservationRepository @Inject constructor(private val reservationService: ReservationService) {
    suspend fun addReservation(tableId: String, reservation: ReservationDTO) = reservationService.addReservation(tableId, reservation)
}