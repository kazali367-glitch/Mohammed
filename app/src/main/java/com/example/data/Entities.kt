package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String,
    val priceUsd: Double,
    val condition: String, // جديد, شبه جديد, مستعمل بحالة ممتازة, مستعمل
    val specs: String,
    val sellerName: String,
    val phone: String,
    val city: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "taxi_rides")
data class TaxiRideEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pickup: String,
    val dropoff: String,
    val rideType: String,
    val fareUsd: Double,
    val driverName: String,
    val carModel: String,
    val plateNumber: String,
    val status: String, // جاري التوصيل, مكتملة, ملغية
    val bookingTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "currency_requests")
data class CurrencyRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val requestType: String, // شراء, بيع
    val fromCurrency: String,
    val toCurrency: String,
    val amount: Double,
    val targetRate: Double,
    val notes: String,
    val contactPhone: String,
    val city: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "flight_tickets")
data class FlightTicketEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookingRef: String,
    val airline: String,
    val flightNumber: String,
    val fromAirport: String,
    val toAirport: String,
    val departureDate: String,
    val departureTime: String,
    val arrivalTime: String,
    val cabinClass: String,
    val passengerName: String,
    val seatNumber: String,
    val priceUsd: Double,
    val bookingTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "shops")
data class ShopEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String,
    val rating: Float,
    val reviewCount: Int,
    val openingHours: String,
    val phone: String,
    val address: String,
    val city: String,
    val description: String,
    val isFavorite: Boolean = false,
    val isUserAdded: Boolean = false
)
