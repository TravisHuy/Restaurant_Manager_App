package com.nhathuy.restaurant_manager_app.resource

/**
 * A generic sealed class to represent different states of a resource in a network or database operation.
 *
 * @param T The type of data that the resource holds.
 * @property data The data associated with the resource, if available.
 * @property message An optional error message in case of failure.
 *
 * @version 0.1
 * @since 07-02-2025
 * @author TravisHuy
 */
sealed class Resource<T>(

    val data: T? =null,
    val message: String? =null
){
    /**
     * Represents a successful state with the provided data.
     *
     * @param T The type of data.
     * @param data The successfully loaded data.
     */
    class Success<T>(data: T):Resource<T>(data)
    /**
     * Represents an error state with an optional message and data.
     *
     * @param T The type of data.
     * @param message The error message describing the failure.
     * @param data An optional data snapshot available despite the error.
     */
    class Error<T>(message: String,data: T?=null):Resource<T>(data,message)
    /**
     * Represents a loading state while the resource is being fetched.
     *
     * @param T The type of data.
     */
    class Loading<T>: Resource<T>()
}


