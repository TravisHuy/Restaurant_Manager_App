package com.nhathuy.restaurant_manager_app.data.model

import java.time.LocalDateTime

data class Reservation(
    val id:String,
    val tableId:String,
    val numberOfPeople:Int,
    val customerName:String,
    val reservationTime: String
)
