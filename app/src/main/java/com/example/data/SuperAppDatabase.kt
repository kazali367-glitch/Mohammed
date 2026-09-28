package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        DeviceEntity::class,
        TaxiRideEntity::class,
        CurrencyRequestEntity::class,
        FlightTicketEntity::class,
        ShopEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SuperAppDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
    abstract fun taxiRideDao(): TaxiRideDao
    abstract fun currencyRequestDao(): CurrencyRequestDao
    abstract fun flightTicketDao(): FlightTicketDao
    abstract fun shopDao(): ShopDao

    companion object {
        @Volatile
        private var INSTANCE: SuperAppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): SuperAppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SuperAppDatabase::class.java,
                    "super_app_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }

            private suspend fun populateInitialData(database: SuperAppDatabase) {
                // Initial Device Listings
                val deviceDao = database.deviceDao()
                deviceDao.insertDevice(
                    DeviceEntity(
                        title = "آيفون 15 برو ماكس (iPhone 15 Pro Max)",
                        category = "هواتف ذكية",
                        priceUsd = 1050.0,
                        condition = "شبه جديد",
                        specs = "ذاكرة 256GB - تيتانيوم طبيعي - بطارية 98% - مع العلبة وكافة الملحقات الأصلية",
                        sellerName = "طارق الكرخي",
                        phone = "+9647701234567",
                        city = "بغداد - الكرادة"
                    )
                )
                deviceDao.insertDevice(
                    DeviceEntity(
                        title = "ماك بوك برو M3 برو 16 إنش",
                        category = "حواسيب محمولة",
                        priceUsd = 2100.0,
                        condition = "جديد بالكرتون",
                        specs = "18GB RAM - 512GB SSD - اللون رمادي فلكي - ضمان دولي سنة",
                        sellerName = "شركة آفاق التقنية",
                        phone = "+971501122334",
                        city = "دبي - ديرة"
                    )
                )
                deviceDao.insertDevice(
                    DeviceEntity(
                        title = "سامسونج جالاكسي S24 ألترا",
                        category = "هواتف ذكية",
                        priceUsd = 920.0,
                        condition = "مستعمل بحالة ممتازة",
                        specs = "512GB - رمادي تيتانيوم - قلم S-Pen - بدون أي خدش",
                        sellerName = "مكتب النخبة للاتصالات",
                        phone = "+966551234567",
                        city = "الرياض - العليا"
                    )
                )
                deviceDao.insertDevice(
                    DeviceEntity(
                        title = "سوني بلايستيشن 5 سليم (PS5 Slim)",
                        category = "أجهزة ألعاب",
                        priceUsd = 480.0,
                        condition = "جديد بالكرتون",
                        specs = "نسخة الأقراص 1TB - مع يدتي تحكم ولعبة EA FC 24",
                        sellerName = "عالم الألعاب",
                        phone = "+962791234567",
                        city = "عمان - الجبيهة"
                    )
                )
                deviceDao.insertDevice(
                    DeviceEntity(
                        title = "آيباد إير M2 الجيل الجديد 11 إنش",
                        category = "أجهزة لوحية",
                        priceUsd = 630.0,
                        condition = "شبه جديد",
                        specs = "128GB Wi-Fi - اللون أزرق سماوي - مع قلم Apple Pencil Pro",
                        sellerName = "حسين الشمري",
                        phone = "+9647809876543",
                        city = "البصرة - العشار"
                    )
                )

                // Initial Taxi Ride
                val taxiDao = database.taxiRideDao()
                taxiDao.insertRide(
                    TaxiRideEntity(
                        pickup = "مطار بغداد الدولي - صالة بابل",
                        dropoff = "حي المنصور - شارع 14 رمضان",
                        rideType = "تكسي مريح (Comfort)",
                        fareUsd = 22.5,
                        driverName = "كابتن سجاد التميمي",
                        carModel = "تويوتا كامري 2023 - فضي",
                        plateNumber = "بغداد - 84931",
                        status = "مكتملة"
                    )
                )

                // Initial Currency Exchange Requests
                val currencyDao = database.currencyRequestDao()
                currencyDao.insertRequest(
                    CurrencyRequestEntity(
                        requestType = "شراء",
                        fromCurrency = "USD",
                        toCurrency = "IQD",
                        amount = 1000.0,
                        targetRate = 1530.0,
                        notes = "مطلوب شراء 1000 دولار كاش بسعر السوق مع التسليم المباشر في المنصور",
                        contactPhone = "+9647712398471",
                        city = "بغداد"
                    )
                )
                currencyDao.insertRequest(
                    CurrencyRequestEntity(
                        requestType = "بيع",
                        fromCurrency = "EUR",
                        toCurrency = "USD",
                        amount = 1500.0,
                        targetRate = 1.09,
                        notes = "يوجد 1500 يورو للتبديل بدولار أمريكي فئات نظيفة 100$",
                        contactPhone = "+971558765432",
                        city = "دبي"
                    )
                )

                // Initial Flight Ticket
                val flightDao = database.flightTicketDao()
                flightDao.insertTicket(
                    FlightTicketEntity(
                        bookingRef = "SHM-7842",
                        airline = "الخطوط الجوية العراقية",
                        flightNumber = "IA-215",
                        fromAirport = "مطار بغداد (BGW)",
                        toAirport = "مطار إسطنبول (IST)",
                        departureDate = "2026-10-15",
                        departureTime = "10:30 ص",
                        arrivalTime = "01:45 م",
                        cabinClass = "درجة رجال الأعمال",
                        passengerName = "علي الكاظمي",
                        seatNumber = "2A",
                        priceUsd = 340.0
                    )
                )

                // Initial Shops & Directory
                val shopDao = database.shopDao()
                val initialShops = listOf(
                    ShopEntity(
                        name = "مجمع الشفاء الطبي التخصصي",
                        category = "عيادات وأطباء",
                        rating = 4.9f,
                        reviewCount = 142,
                        openingHours = "09:00 ص - 09:00 م",
                        phone = "+9647705551234",
                        address = "شارع الأميرات - مجاور مستشفى الهلال",
                        city = "بغداد",
                        description = "عيادات باطنية، أسنان، قلبية، وعظام مجهزة بأحدث أجهزة السونار والمختبرات السريعة"
                    ),
                    ShopEntity(
                        name = "صيدلية الدواء المركزية",
                        category = "صيدليات",
                        rating = 4.8f,
                        reviewCount = 98,
                        openingHours = "على مدار 24 ساعة",
                        phone = "+9647801112233",
                        address = "ساحة النسور - مقابل برج المأمون",
                        city = "بغداد",
                        description = "كافة الأدوية المزمنة ومستحضرات التجميل العالمية والعناية بالبشرة وخدمة توصيل فورية"
                    ),
                    ShopEntity(
                        name = "مركز الأمانة لصيانة السيارات الحديثة",
                        category = "كراجات وصيانة سيارات",
                        rating = 4.7f,
                        reviewCount = 87,
                        openingHours = "08:00 ص - 08:00 م",
                        phone = "+9647723344556",
                        address = "المنطقة الصناعية - الشيخ عمر",
                        city = "بغداد",
                        description = "فحص كمبيوتر أوتيل، فحص محركات وكهرباء سيارات، برمجة وتصليح منظومات الهايبرد والكهرباء"
                    ),
                    ShopEntity(
                        name = "مطعم ومشاوي ليالي النرجس",
                        category = "مطاعم ومقاهي",
                        rating = 4.9f,
                        reviewCount = 310,
                        openingHours = "12:00 م - 01:00 ص",
                        phone = "+9647719998877",
                        address = "شارع الرواد - المنصور",
                        city = "بغداد",
                        description = "أشهى المأكولات الشرقية والغربية، كباب عراقي فاخر، قوزي على الفحم، ومقاعد عائلية راقية"
                    ),
                    ShopEntity(
                        name = "آبل فيكس لصيانة الإلكترونيات والشاشات",
                        category = "صيانة إلكترونيات وهواتف",
                        rating = 4.8f,
                        reviewCount = 115,
                        openingHours = "10:00 ص - 10:00 م",
                        phone = "+9647706667788",
                        address = "شارع الربيعي - مجمع النور للطابق الأول",
                        city = "بغداد",
                        description = "صيانة معتمدة لجميع هواتف آيفون وسامسونج، تبديل شاشات أصلية، إصلاح آيسيات اللوحة الأم بضمان"
                    ),
                    ShopEntity(
                        name = "مكتب المستشار للخدمات القانونية والمحاماة",
                        category = "محاماة واستشارات",
                        rating = 4.9f,
                        reviewCount = 64,
                        openingHours = "09:00 ص - 05:00 م",
                        phone = "+9647814455667",
                        address = "شارع فلسطين - قرب ساحة بيروت",
                        city = "بغداد",
                        description = "تأسيس الشركات، العقود التجارية، قضايا العمل والضرائب، استشارات قانونية متكاملة للشركات والأفراد"
                    )
                )
                shopDao.insertAllShops(initialShops)
            }
        }
    }
}
