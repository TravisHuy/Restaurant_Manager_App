package com.nhathuy.restaurant_manager_app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhathuy.restaurant_manager_app.data.dto.ReservationDTO
import com.nhathuy.restaurant_manager_app.data.model.Reservation
import com.nhathuy.restaurant_manager_app.data.repository.ReservationRepository
import com.nhathuy.restaurant_manager_app.resource.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

class ReservationViewModel @Inject constructor(private val reservationRepository: ReservationRepository) : ViewModel() {

    private val _addReservationResult = MutableLiveData<Resource<Reservation>>()
    val addReservationResult: LiveData<Resource<Reservation>> = _addReservationResult

    private val _checkTableReservationResult = MutableLiveData<Resource<Boolean>>()
    val checkTableReservationResult: LiveData<Resource<Boolean>> = _checkTableReservationResult

    private val _getReservationResult = MutableStateFlow<Resource<Reservation>?>(null)
    val getReservationResult : StateFlow<Resource<Reservation>?> = _getReservationResult.asStateFlow()



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

    fun checkTableReservation(tableId: String) {
        viewModelScope.launch {
            _checkTableReservationResult.value = Resource.Loading()
            try {
                val response = reservationRepository.checkTableReservation(tableId)
                _checkTableReservationResult.value = Resource.Success(response)
            }
            catch (e:HttpException){
                val errorBody = e.response()?.errorBody()?.string() ?: "Unknown error"
                _checkTableReservationResult.value = Resource.Error("Error: $errorBody")

            }
            catch (e: Exception) {
                _checkTableReservationResult.value = Resource.Error("Error: ${e.message}")
            }
        }
    }
    fun getReservation(reservationId:String) = viewModelScope.launch {
        try {
            reservationRepository.getReservation(reservationId).collect {
                _getReservationResult.value = it
            }
        }
        catch (e:HttpException) {
            val errorBody = e.response()?.errorBody()?.string() ?: "Unknown error"
            _getReservationResult.value = Resource.Error("Error: $errorBody")
        }
        catch (e:Exception){
            _getReservationResult.value = Resource.Error("Network error: ${e.message}")
        }
    }
}