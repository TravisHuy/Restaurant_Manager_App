package com.nhathuy.restaurant_manager_app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhathuy.restaurant_manager_app.data.dto.ReservationDTO
import com.nhathuy.restaurant_manager_app.data.model.Reservation
import com.nhathuy.restaurant_manager_app.data.repository.ReservationRepository
import com.nhathuy.restaurant_manager_app.resource.Resource
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

class ReservationViewModel @Inject constructor(private val reservationRepository: ReservationRepository) : ViewModel() {

    private val _addReservationResult = MutableLiveData<Resource<Reservation>>()
    val addReservationResult: LiveData<Resource<Reservation>> = _addReservationResult

    fun addReservation(tableId: String, reservationDTO: ReservationDTO) {
        viewModelScope.launch {
            _addReservationResult.value = Resource.Loading()
            try {
                val response = reservationRepository.addReservation(tableId, reservationDTO)
                if(response!=null){
                    _addReservationResult.value = Resource.Success(response)
                }
                else{
                    _addReservationResult.value = Resource.Error("Error: No reservation found")
                }
            }
            catch(e: HttpException){
                val errorBody = e.response()?.errorBody()?.string() ?: "Unknown error"
                _addReservationResult.value = Resource.Error("Error: $errorBody")
            }
            catch (e:Exception){
                _addReservationResult.value = Resource.Error("Error: ${e.message}")
            }
        }
    }
}