package com.nhathuy.restaurant_manager_app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhathuy.restaurant_manager_app.data.api.OrderItemService
import com.nhathuy.restaurant_manager_app.data.model.OrderItem
import com.nhathuy.restaurant_manager_app.data.repository.OrderItemRepository
import com.nhathuy.restaurant_manager_app.resource.Resource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class OrderItemViewModel @Inject constructor(private val orderItemRepository: OrderItemRepository):ViewModel() {

    private val _orderItem = MutableLiveData<Resource<OrderItem>>()
    val orderItem : LiveData<Resource<OrderItem>> = _orderItem

    private val _allOrderItems = MutableLiveData<Resource<List<OrderItem>>>()
    val allOrderItem : LiveData<Resource<List<OrderItem>>> = _allOrderItems

    fun getOrderItemId(orderItemId:String) = viewModelScope.launch {
        try {
            orderItemRepository.getOrderItemById(orderItemId).collect {
                _orderItem.value = it
            }
        }
        catch(e: HttpException){
            val errorBody = e.response()?.errorBody()?.string() ?: "Unknown error"
            _orderItem.value = Resource.Error("Error: $errorBody")
        }
        catch (e:Exception){
            _orderItem.value = Resource.Error("Network error: ${e.message}")
        }
    }
    fun getListOrderItem(orderItemIds:List<String>) = viewModelScope.launch {
        try {
            orderItemRepository.getListOrderItem(orderItemIds).collect {
                _allOrderItems.value = it
            }
        }
        catch(e: HttpException){
            val errorBody = e.response()?.errorBody()?.string() ?: "Unknown error"
            _orderItem.value = Resource.Error("Error: $errorBody")
        }
        catch (e:Exception){
            _orderItem.value = Resource.Error("Network error: ${e.message}")
        }
    }
}