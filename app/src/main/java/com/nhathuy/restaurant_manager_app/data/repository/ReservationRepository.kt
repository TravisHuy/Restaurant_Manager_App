package com.nhathuy.restaurant_manager_app.data.repository

import com.nhathuy.restaurant_manager_app.data.api.ReservationService
import com.nhathuy.restaurant_manager_app.data.dto.ReservationDTO
import com.nhathuy.restaurant_manager_app.data.model.Reservation
import com.nhathuy.restaurant_manager_app.resource.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ReservationRepository @Inject constructor(private val reservationService: ReservationService) {
    suspend fun addReservation(tableId: String, reservation: ReservationDTO) = reservationService.addReservation(tableId, reservation)
    suspend fun checkTableReservation(tableId: String) = reservationService.checkTableReservation(tableId)
    suspend fun getReservation(reservationId:String) : Flow<Resource<Reservation>> = flow {
        emit(Resource.Loading())
        try {
            val response = reservationService.getReservation(reservationId)
            if(response.isSuccessful){
                response.body()?.let {
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error("empty response body server"))
            }
            else{
                emit(Resource.Error("Failed to get reservation by id: ${response.message()}"))
            }
        }
        catch (e:Exception){
            emit(Resource.Error("Network error: ${e.message}"))
        }
    }
}