package com.nhathuy.restaurant_manager_app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhathuy.restaurant_manager_app.data.model.Order
import com.nhathuy.restaurant_manager_app.data.repository.OrderRepository
import com.nhathuy.restaurant_manager_app.oauth2.request.OrderRequest
import com.nhathuy.restaurant_manager_app.oauth2.response.OrderResponse
import com.nhathuy.restaurant_manager_app.resource.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import javax.inject.Inject

/**
 * OrderViewModel for managing order-related operations.
 *
 * @return 0.1
 * @since 25-02-2025
 * @author TravisHuy
 */
class OrderViewModel @Inject constructor(private val repository: OrderRepository):ViewModel(){
    //stateflow for adding item to order
    private val _addItemResult = MutableStateFlow<Resource<String>>(Resource.Loading())
    val addItemResult: StateFlow<Resource<String>> = _addItemResult.asStateFlow()

    // stateflow for removing item from order
    private val _removeItemResult = MutableStateFlow<Resource<String>>(Resource.Loading())
    val removeItemResult: StateFlow<Resource<String>> = _removeItemResult.asStateFlow()

    // stateflow for confirming order
    private val _confirmOrderResult = MutableStateFlow<Resource<Order>>(Resource.Loading())
    val confirmOrderResult: StateFlow<Resource<Order>> = _confirmOrderResult.asStateFlow()

    // stateflow for getting temp items
    private val _tempOrderResult = MutableStateFlow<Resource<List<Order>>>(Resource.Loading())
    val tempOrderResult: StateFlow<Resource<List<Order>>> = _tempOrderResult.asStateFlow()


    // StateFlow for create order
    private val _orderState = MutableStateFlow<Resource<OrderResponse>?>(null)
    val orderState: StateFlow<Resource<OrderResponse>?> = _orderState.asStateFlow()

    //stateflow for getting customer name
    private val _customerName = MutableStateFlow<Resource<String>>(Resource.Loading())
    val customerName: StateFlow<Resource<String>> = _customerName.asStateFlow()
    /**
     * Creates an order.
     *
     * @param orderRequest the order request
     */
    fun createOrder(orderRequest: OrderRequest) = viewModelScope.launch {
        repository.createOrder(orderRequest).collect { resource ->
            _orderState.value = resource
        }
    }

    /**
     * Gets the customer name.
     *
     * @param tableId the id of the table
     */
    fun getCustomerName(tableId: String) = viewModelScope.launch {
        repository.getCustomerName(tableId).collect {
            _customerName.value = it
        }
    }
    /**
     * Adds an item to the order.
     *
     * @param tableId the id of the table
     * @param menuItemId the id of the menu item
     * @param quantity the quantity of the menu item
     * @param price the price of the menu item
     */
    fun addItemOrder(tableId:String ,menuItemId:String, quantity:Int, price: Double){
        viewModelScope.launch {
            repository.addOrder(tableId, menuItemId, quantity, price).collect {
                _addItemResult.value = it
            }
        }
    }

    /**
     * Removes an item from the order.
     *
     * @param tableId the id of the table
     * @param menuItemId the id of the menu item
     */
    fun removeItemOrder(tableId: String, menuItemId: String) {
        viewModelScope.launch {
            repository.removeItem(tableId, menuItemId).collect {
                _removeItemResult.value = it
            }
        }
    }

    /**
     * Confirms the order.
     *
     * @param tableId the id of the table
     * @param customerName the name of the customer
     */
    fun confirmOrder(tableId: String, customerName: String) {
        viewModelScope.launch {
            repository.confirmOrder(tableId, customerName).collect {
                _confirmOrderResult.value = it
            }
        }
    }

    /**
     * Gets the temporary items.
     *
     * @param tableId the id of the table
     */
    fun getTempItems(tableId: String) {
        viewModelScope.launch {
            repository.getTempItems(tableId).collect {
                _tempOrderResult.value = it
            }
        }
    }
}