package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DeviceDao {
    @Query("SELECT * FROM devices ORDER BY createdAt DESC")
    fun getAllDevices(): Flow<List<DeviceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: DeviceEntity): Long

    @Delete
    suspend fun deleteDevice(device: DeviceEntity)
}

@Dao
interface TaxiRideDao {
    @Query("SELECT * FROM taxi_rides ORDER BY bookingTime DESC")
    fun getAllRides(): Flow<List<TaxiRideEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRide(ride: TaxiRideEntity): Long

    @Query("UPDATE taxi_rides SET status = :status WHERE id = :id")
    suspend fun updateRideStatus(id: Long, status: String)
}

@Dao
interface CurrencyRequestDao {
    @Query("SELECT * FROM currency_requests ORDER BY createdAt DESC")
    fun getAllRequests(): Flow<List<CurrencyRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: CurrencyRequestEntity): Long

    @Delete
    suspend fun deleteRequest(request: CurrencyRequestEntity)
}

@Dao
interface FlightTicketDao {
    @Query("SELECT * FROM flight_tickets ORDER BY bookingTime DESC")
    fun getAllTickets(): Flow<List<FlightTicketEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: FlightTicketEntity): Long

    @Delete
    suspend fun deleteTicket(ticket: FlightTicketEntity)
}

@Dao
interface ShopDao {
    @Query("SELECT * FROM shops ORDER BY rating DESC")
    fun getAllShops(): Flow<List<ShopEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShop(shop: ShopEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllShops(shops: List<ShopEntity>)

    @Query("UPDATE shops SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Long, isFavorite: Boolean)
}
