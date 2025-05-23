package com.nhathuy.restaurant_manager_app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhathuy.restaurant_manager_app.data.model.Order
import com.nhathuy.restaurant_manager_app.data.repository.OrderRepository
import com.nhathuy.restaurant_manager_app.oauth2.request.OrderItemRequest
import com.nhathuy.restaurant_manager_app.oauth2.request.OrderRequest
import com.nhathuy.restaurant_manager_app.oauth2.response.OrderResponse
import com.nhathuy.restaurant_manager_app.resource.Resource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
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

    private val _addOrderItemResult = MutableStateFlow<Resource<OrderResponse>?>(null)
    val addOrderItemResult: StateFlow<Resource<OrderResponse>?> = _addOrderItemResult.asStateFlow()

    private val _orderItems = MutableLiveData<Resource<List<Order>>>()
    val orderItems: LiveData<Resource<List<Order>>> = _orderItems

    private val _orderId = MutableLiveData<Resource<Order>>()
    val orderId: LiveData<Resource<Order>> = _orderId

    private val _updateOrderStatus = MutableStateFlow<Resource<OrderResponse>?>(null)
    val updateOrderStatus : StateFlow<Resource<OrderResponse>?> = _updateOrderStatus.asStateFlow()


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
     * Adds an item to the order.
     * @param orderId the id of the order
     * @param newItems the list of new items
     */

    fun addItemsOrder(orderId:String,newItems:List<OrderItemRequest>) = viewModelScope.launch {
        try{
            repository.addItemToOrder(orderId,newItems).collect { resource ->
                _addOrderItemResult.value = resource
            }
        }
        catch(e: HttpException){
            val errorBody = e.response()?.errorBody()?.string() ?: "Unknown error"
            _addOrderItemResult.value = Resource.Error("Error: $errorBody")
        }
        catch (e:Exception){
            _addOrderItemResult.value = Resource.Error("Network error: ${e.message}")
        }
    }

    fun getAllOrders() = viewModelScope.launch {
        try {
            repository.getAllOrders().collect {
                _orderItems.value = it
            }
        }
        catch (e:HttpException) {
            val errorBody = e.response()?.errorBody()?.string() ?: "Unknown error"
            _orderItems.value = Resource.Error("Error: $errorBody")
        }
        catch (e:Exception){
            _orderItems.value = Resource.Error("Network error: ${e.message}")
        }
    }
    fun getOrderById(orderId: String) = viewModelScope.launch {
        try {
            repository.getOrderById(orderId).collect {
                _orderId.value = it
            }
        }
        catch (e:HttpException) {
            val errorBody = e.response()?.errorBody()?.string() ?: "Unknown error"
            _orderId.value = Resource.Error("Error: $errorBody")
        }
        catch (e:Exception){
            _orderId.value = Resource.Error("Network error: ${e.message}")
        }
    }

    fun updateStatusOrder(orderId: String) = viewModelScope.launch {
        try {
            repository.updateOrderStatus(orderId)
                .catch { e ->
                    when (e) {
                        is CancellationException -> throw e // Propagate cancellation
                        is HttpException -> {
                            val errorBody = e.response()?.errorBody()?.string() ?: "Unknown error"
                            _updateOrderStatus.value = Resource.Error("Error: $errorBody")
                        }
                        else -> _updateOrderStatus.value = Resource.Error("Network error: ${e.message}")
                    }
                }
                .collect { resource ->
                    _updateOrderStatus.value = resource
                }
        } catch (e: CancellationException) {
            // Propagate cancellation
            throw e
        } catch (e: Exception) {
            _updateOrderStatus.value = Resource.Error("Network error: ${e.message}")
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