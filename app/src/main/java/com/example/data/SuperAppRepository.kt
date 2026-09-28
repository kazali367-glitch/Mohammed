package com.example.data

import kotlinx.coroutines.flow.Flow

class SuperAppRepository(private val db: SuperAppDatabase) {

    // Devices
    val allDevices: Flow<List<DeviceEntity>> = db.deviceDao().getAllDevices()
    suspend fun insertDevice(device: DeviceEntity): Long = db.deviceDao().insertDevice(device)
    suspend fun deleteDevice(device: DeviceEntity) = db.deviceDao().deleteDevice(device)

    // Taxi
    val allRides: Flow<List<TaxiRideEntity>> = db.taxiRideDao().getAllRides()
    suspend fun insertRide(ride: TaxiRideEntity): Long = db.taxiRideDao().insertRide(ride)
    suspend fun updateRideStatus(id: Long, status: String) = db.taxiRideDao().updateRideStatus(id, status)

    // Currency
    val allCurrencyRequests: Flow<List<CurrencyRequestEntity>> = db.currencyRequestDao().getAllRequests()
    suspend fun insertCurrencyRequest(req: CurrencyRequestEntity): Long = db.currencyRequestDao().insertRequest(req)
    suspend fun deleteCurrencyRequest(req: CurrencyRequestEntity) = db.currencyRequestDao().deleteRequest(req)

    // Flights
    val allFlightTickets: Flow<List<FlightTicketEntity>> = db.flightTicketDao().getAllTickets()
    suspend fun insertFlightTicket(ticket: FlightTicketEntity): Long = db.flightTicketDao().insertTicket(ticket)
    suspend fun deleteFlightTicket(ticket: FlightTicketEntity) = db.flightTicketDao().deleteTicket(ticket)

    // Shops
    val allShops: Flow<List<ShopEntity>> = db.shopDao().getAllShops()
    suspend fun insertShop(shop: ShopEntity): Long = db.shopDao().insertShop(shop)
    suspend fun toggleShopFavorite(id: Long, isFavorite: Boolean) = db.shopDao().toggleFavorite(id, isFavorite)
}
