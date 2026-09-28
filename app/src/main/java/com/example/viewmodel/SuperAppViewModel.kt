package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CurrencyRate(
    val code: String,
    val nameAr: String,
    val flag: String,
    val buyPrice: Double, // in local currency (e.g. IQD per 1 USD)
    val sellPrice: Double,
    val change24h: Double, // percentage e.g. +0.25%
    val isPositive: Boolean
)

data class FlightSearchResult(
    val flightId: String,
    val airline: String,
    val airlineCode: String,
    val flightNumber: String,
    val fromCity: String,
    val fromAirport: String,
    val toCity: String,
    val toAirport: String,
    val departureTime: String,
    val arrivalTime: String,
    val duration: String,
    val isDirect: Boolean,
    val stopsCount: Int,
    val baggage: String,
    val priceUsd: Double
)

class SuperAppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SuperAppRepository

    init {
        val db = SuperAppDatabase.getDatabase(application, viewModelScope)
        repository = SuperAppRepository(db)
    }

    // ==========================================
    // 1. DEVICES MARKETPLACE STATE
    // ==========================================
    val devices: StateFlow<List<DeviceEntity>> = repository.allDevices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _deviceCategoryFilter = MutableStateFlow("الكل")
    val deviceCategoryFilter: StateFlow<String> = _deviceCategoryFilter.asStateFlow()

    private val _deviceSearchQuery = MutableStateFlow("")
    val deviceSearchQuery: StateFlow<String> = _deviceSearchQuery.asStateFlow()

    fun setDeviceCategoryFilter(category: String) {
        _deviceCategoryFilter.value = category
    }

    fun setDeviceSearchQuery(query: String) {
        _deviceSearchQuery.value = query
    }

    fun postDevice(device: DeviceEntity) {
        viewModelScope.launch {
            repository.insertDevice(device)
        }
    }

    fun deleteDevice(device: DeviceEntity) {
        viewModelScope.launch {
            repository.deleteDevice(device)
        }
    }

    // ==========================================
    // 2. TAXI BOOKING STATE
    // ==========================================
    val taxiRidesHistory: StateFlow<List<TaxiRideEntity>> = repository.allRides
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _pickupLocation = MutableStateFlow("حي المنصور - شارع 14 رمضان")
    val pickupLocation: StateFlow<String> = _pickupLocation.asStateFlow()

    private val _dropoffLocation = MutableStateFlow("مطار بغداد الدولي - صالة بابل")
    val dropoffLocation: StateFlow<String> = _dropoffLocation.asStateFlow()

    private val _selectedTaxiType = MutableStateFlow("اقتصادي")
    val selectedTaxiType: StateFlow<String> = _selectedTaxiType.asStateFlow()

    private val _taxiBookingStatus = MutableStateFlow("IDLE") // IDLE, SEARCHING, CONFIRMED, COMPLETED
    val taxiBookingStatus: StateFlow<String> = _taxiBookingStatus.asStateFlow()

    private val _activeDriverInfo = MutableStateFlow<TaxiRideEntity?>(null)
    val activeDriverInfo: StateFlow<TaxiRideEntity?> = _activeDriverInfo.asStateFlow()

    fun setPickupLocation(location: String) {
        _pickupLocation.value = location
    }

    fun setDropoffLocation(location: String) {
        _dropoffLocation.value = location
    }

    fun selectTaxiType(type: String) {
        _selectedTaxiType.value = type
    }

    fun getTaxiFare(): Double {
        val base = when (_selectedTaxiType.value) {
            "اقتصادي" -> 10.0
            "مريح" -> 16.0
            "عائلي" -> 22.0
            "فاخر VIP" -> 35.0
            else -> 12.0
        }
        return base
    }

    fun requestTaxiRide() {
        viewModelScope.launch {
            _taxiBookingStatus.value = "SEARCHING"
            delay(2000) // simulation delay for driver match

            val drivers = listOf(
                Triple("كابتن فراس الجبوري", "كيا k5 بيضاء 2024", "بغداد - 92140"),
                Triple("كابتن حيدر العبيدي", "تويوتا كورولا 2023 رمادي", "بغداد - 34182"),
                Triple("كابتن مصطفى الربيعي", "هيونداي سوناتا 2024 كحلي", "بغداد - 71923")
            )
            val driver = drivers.random()
            val newRide = TaxiRideEntity(
                pickup = _pickupLocation.value,
                dropoff = _dropoffLocation.value,
                rideType = _selectedTaxiType.value,
                fareUsd = getTaxiFare(),
                driverName = driver.first,
                carModel = driver.second,
                plateNumber = driver.third,
                status = "جاري التوصيل"
            )
            val rideId = repository.insertRide(newRide)
            _activeDriverInfo.value = newRide.copy(id = rideId)
            _taxiBookingStatus.value = "CONFIRMED"
        }
    }

    fun completeTaxiRide() {
        viewModelScope.launch {
            val current = _activeDriverInfo.value
            if (current != null) {
                repository.updateRideStatus(current.id, "مكتملة")
            }
            _taxiBookingStatus.value = "IDLE"
            _activeDriverInfo.value = null
        }
    }

    fun cancelTaxiRide() {
        viewModelScope.launch {
            val current = _activeDriverInfo.value
            if (current != null) {
                repository.updateRideStatus(current.id, "ملغية")
            }
            _taxiBookingStatus.value = "IDLE"
            _activeDriverInfo.value = null
        }
    }

    // ==========================================
    // 3. CURRENCY EXCHANGE STATE
    // ==========================================
    val currencyRequests: StateFlow<List<CurrencyRequestEntity>> = repository.allCurrencyRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val liveCurrencyRates: StateFlow<List<CurrencyRate>> = MutableStateFlow(
        listOf(
            CurrencyRate("USD", "دولار أمريكي / دينار عراقي", "🇺🇸", 1528.0, 1533.0, 0.15, true),
            CurrencyRate("EUR", "يورو أوروبي / دولار", "🇪🇺", 1.085, 1.092, -0.20, false),
            CurrencyRate("TRY", "ليرة تركية / دولار", "🇹🇷", 34.10, 34.35, -0.45, false),
            CurrencyRate("SAR", "ريال سعودي / دولار", "🇸🇦", 3.75, 3.76, 0.01, true),
            CurrencyRate("AED", "درهم إماراتي / دولار", "🇦🇪", 3.67, 3.68, 0.00, true),
            CurrencyRate("KWD", "دينار كويتي / دولار", "🇰🇼", 0.306, 0.308, 0.05, true),
            CurrencyRate("EGP", "جنيه مصري / دولار", "🇪🇬", 48.50, 48.80, 0.30, true),
            CurrencyRate("GBP", "جنيه إسترليني / دولار", "🇬🇧", 1.285, 1.295, 0.12, true),
            CurrencyRate("JOD", "دينار أردني / دولار", "🇯🇴", 0.708, 0.712, 0.00, true)
        )
    ).asStateFlow()

    private val _converterAmount = MutableStateFlow("100")
    val converterAmount: StateFlow<String> = _converterAmount.asStateFlow()

    private val _converterFrom = MutableStateFlow("USD")
    val converterFrom: StateFlow<String> = _converterFrom.asStateFlow()

    private val _converterTo = MutableStateFlow("IQD")
    val converterTo: StateFlow<String> = _converterTo.asStateFlow()

    fun setConverterAmount(amount: String) {
        _converterAmount.value = amount
    }

    fun setConverterFrom(from: String) {
        _converterFrom.value = from
    }

    fun setConverterTo(to: String) {
        _converterTo.value = to
    }

    fun swapCurrencies() {
        val temp = _converterFrom.value
        _converterFrom.value = _converterTo.value
        _converterTo.value = temp
    }

    fun getConvertedResult(): String {
        val amount = _converterAmount.value.toDoubleOrNull() ?: 0.0
        val from = _converterFrom.value
        val to = _converterTo.value
        if (amount <= 0.0) return "0.00"

        // Base rate in USD
        val usdValue = when (from) {
            "USD" -> amount
            "IQD" -> amount / 1530.0
            "EUR" -> amount * 1.09
            "TRY" -> amount / 34.2
            "SAR" -> amount / 3.75
            "AED" -> amount / 3.67
            "KWD" -> amount / 0.307
            "EGP" -> amount / 48.65
            "GBP" -> amount * 1.29
            "JOD" -> amount / 0.71
            else -> amount
        }

        val result = when (to) {
            "USD" -> usdValue
            "IQD" -> usdValue * 1530.0
            "EUR" -> usdValue / 1.09
            "TRY" -> usdValue * 34.2
            "SAR" -> usdValue * 3.75
            "AED" -> usdValue * 3.67
            "KWD" -> usdValue * 0.307
            "EGP" -> usdValue * 48.65
            "GBP" -> usdValue / 1.29
            "JOD" -> usdValue * 0.71
            else -> usdValue
        }

        return String.format(java.util.Locale.US, "%,.2f", result)
    }

    fun postCurrencyRequest(req: CurrencyRequestEntity) {
        viewModelScope.launch {
            repository.insertCurrencyRequest(req)
        }
    }

    fun deleteCurrencyRequest(req: CurrencyRequestEntity) {
        viewModelScope.launch {
            repository.deleteCurrencyRequest(req)
        }
    }

    // ==========================================
    // 4. FLIGHTS BOOKING STATE
    // ==========================================
    val flightTickets: StateFlow<List<FlightTicketEntity>> = repository.allFlightTickets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _flightOrigin = MutableStateFlow("بغداد (BGW)")
    val flightOrigin: StateFlow<String> = _flightOrigin.asStateFlow()

    private val _flightDestination = MutableStateFlow("إسطنبول (IST)")
    val flightDestination: StateFlow<String> = _flightDestination.asStateFlow()

    private val _flightDate = MutableStateFlow("2026-10-15")
    val flightDate: StateFlow<String> = _flightDate.asStateFlow()

    private val _flightCabinClass = MutableStateFlow("الدرجة السياحية")
    val flightCabinClass: StateFlow<String> = _flightCabinClass.asStateFlow()

    private val _flightPassengersCount = MutableStateFlow(1)
    val flightPassengersCount: StateFlow<Int> = _flightPassengersCount.asStateFlow()

    private val _isRoundTrip = MutableStateFlow(false)
    val isRoundTrip: StateFlow<Boolean> = _isRoundTrip.asStateFlow()

    fun setFlightOrigin(origin: String) { _flightOrigin.value = origin }
    fun setFlightDestination(dest: String) { _flightDestination.value = dest }
    fun setFlightDate(date: String) { _flightDate.value = date }
    fun setFlightCabinClass(cabin: String) { _flightCabinClass.value = cabin }
    fun setFlightPassengers(count: Int) { _flightPassengersCount.value = count }
    fun setRoundTrip(isRound: Boolean) { _isRoundTrip.value = isRound }

    fun getAvailableFlights(): List<FlightSearchResult> {
        val orig = _flightOrigin.value
        val dest = _flightDestination.value
        return listOf(
            FlightSearchResult(
                flightId = "F-01",
                airline = "الخطوط الجوية العراقية",
                airlineCode = "IA",
                flightNumber = "IA-215",
                fromCity = orig,
                fromAirport = orig.substringAfter("(").substringBefore(")"),
                toCity = dest,
                toAirport = dest.substringAfter("(").substringBefore(")"),
                departureTime = "09:30 ص",
                arrivalTime = "12:45 م",
                duration = "3س 15د",
                isDirect = true,
                stopsCount = 0,
                baggage = "30 كغم + 7 كغم يد",
                priceUsd = 280.0
            ),
            FlightSearchResult(
                flightId = "F-02",
                airline = "الخطوط الجوية التركية",
                airlineCode = "TK",
                flightNumber = "TK-803",
                fromCity = orig,
                fromAirport = orig.substringAfter("(").substringBefore(")"),
                toCity = dest,
                toAirport = dest.substringAfter("(").substringBefore(")"),
                departureTime = "02:15 م",
                arrivalTime = "05:40 م",
                duration = "3س 25د",
                isDirect = true,
                stopsCount = 0,
                baggage = "35 كغم + وجبة فاخرة",
                priceUsd = 360.0
            ),
            FlightSearchResult(
                flightId = "F-03",
                airline = "طيران الإمارات",
                airlineCode = "EK",
                flightNumber = "EK-944",
                fromCity = orig,
                fromAirport = orig.substringAfter("(").substringBefore(")"),
                toCity = dest,
                toAirport = dest.substringAfter("(").substringBefore(")"),
                departureTime = "06:00 م",
                arrivalTime = "11:30 م",
                duration = "5س 30د",
                isDirect = false,
                stopsCount = 1,
                baggage = "30 كغم + ترفيه شاشة",
                priceUsd = 410.0
            ),
            FlightSearchResult(
                flightId = "F-04",
                airline = "الخطوط الجوية القطرية",
                airlineCode = "QR",
                flightNumber = "QR-458",
                fromCity = orig,
                fromAirport = orig.substringAfter("(").substringBefore(")"),
                toCity = dest,
                toAirport = dest.substringAfter("(").substringBefore(")"),
                departureTime = "11:00 م",
                arrivalTime = "04:20 ص",
                duration = "5س 20د",
                isDirect = false,
                stopsCount = 1,
                baggage = "30 كغم",
                priceUsd = 390.0
            )
        )
    }

    fun bookFlightTicket(flight: FlightSearchResult, passengerName: String, seat: String) {
        viewModelScope.launch {
            val randomRef = "SHM-" + (1000..9999).random()
            val ticket = FlightTicketEntity(
                bookingRef = randomRef,
                airline = flight.airline,
                flightNumber = flight.flightNumber,
                fromAirport = flight.fromCity,
                toAirport = flight.toCity,
                departureDate = _flightDate.value,
                departureTime = flight.departureTime,
                arrivalTime = flight.arrivalTime,
                cabinClass = _flightCabinClass.value,
                passengerName = passengerName,
                seatNumber = seat,
                priceUsd = flight.priceUsd * _flightPassengersCount.value
            )
            repository.insertFlightTicket(ticket)
        }
    }

    fun deleteTicket(ticket: FlightTicketEntity) {
        viewModelScope.launch {
            repository.deleteFlightTicket(ticket)
        }
    }

    // ==========================================
    // 5. SHOPS & DIRECTORY STATE
    // ==========================================
    val shops: StateFlow<List<ShopEntity>> = repository.allShops
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _shopCategoryFilter = MutableStateFlow("الكل")
    val shopCategoryFilter: StateFlow<String> = _shopCategoryFilter.asStateFlow()

    private val _shopSearchQuery = MutableStateFlow("")
    val shopSearchQuery: StateFlow<String> = _shopSearchQuery.asStateFlow()

    fun setShopCategoryFilter(cat: String) {
        _shopCategoryFilter.value = cat
    }

    fun setShopSearchQuery(query: String) {
        _shopSearchQuery.value = query
    }

    fun toggleShopFavorite(shop: ShopEntity) {
        viewModelScope.launch {
            repository.toggleShopFavorite(shop.id, !shop.isFavorite)
        }
    }

    fun addNewShop(shop: ShopEntity) {
        viewModelScope.launch {
            repository.insertShop(shop.copy(isUserAdded = true))
        }
    }
}
