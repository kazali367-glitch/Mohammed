package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import com.example.screens.Professional

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("التطبيق الشامل", appName)
  }

  @Test
  fun `verify screen titles`() {
    assertEquals("بيع وشراء الأجهزة", getScreenTitle("devices"))
    assertEquals("حجز تكسي", getScreenTitle("taxi"))
    assertEquals("بيع وشراء العملات", getScreenTitle("currency"))
    assertEquals("حجز تذاكر طيران", getScreenTitle("flights"))
    assertEquals("دليل المحلات", getScreenTitle("shops"))
    assertEquals("دليل المهن والخدمات", getScreenTitle("professions"))
    assertEquals("طلب وتوصيل من محل", getScreenTitle("delivery"))
    assertEquals("جدول محطات الوقود والغاز", getScreenTitle("gas_stations"))
    assertEquals("العروض الخاصة والتخفيضات", getScreenTitle("offers"))
    assertEquals("التطبيق الشامل", getScreenTitle("home"))
  }

  @Test
  fun `verify user profile model`() {
    val profile = com.example.screens.UserProfile(
      name = "علي العراقي",
      phoneNumber = "07701234567",
      address = "بغداد - المنصور"
    )
    assertNotNull(profile)
    assertEquals("علي العراقي", profile.name)
    assertEquals("07701234567", profile.phoneNumber)
    assertEquals("07701234567", profile.phone)
  }

  @Test
  fun `verify professional data model`() {
    val pro = Professional(
      id = 1,
      name = "د. علي التميمي",
      profession = "دكتور",
      specialty = "جراحة عظام",
      phone = "07701112233",
      address = "بغداد",
      rating = 4.9
    )
    assertNotNull(pro)
    assertEquals("دكتور", pro.profession)
    assertEquals("جراحة عظام", pro.specialty)
  }

  @Test
  fun `verify gas station data model`() {
    val station = com.example.screens.GasStation(
      id = 1,
      stationName = "محطة وقود المنصور النموذجية",
      stationType = "حكومية",
      address = "المنصور - بغداد",
      availableProducts = listOf("بنزين محسن", "كاز", "غاز"),
      workHours = "24 ساعة",
      dateNotice = "يوم غد"
    )
    assertNotNull(station)
    assertEquals("محطة وقود المنصور النموذجية", station.stationName)
    assertEquals("حكومية", station.stationType)
    assertEquals(3, station.availableProducts.size)
    assertEquals(33.3152, station.latitude, 0.0001)
    assertEquals(44.3661, station.longitude, 0.0001)
  }

  @Test
  fun `verify special offer data model`() {
    val offer = com.example.screens.SpecialOffer(
      id = 1,
      title = "خصم 20% على تذاكر دبي",
      categoryName = "طيران",
      providerName = "الخطوط الجوية العراقية",
      originalPrice = "350,000 د.ع",
      discountedPrice = "280,000 د.ع",
      discountTag = "20% الخصم",
      description = "عرض خاص على الرحلات المباشرة",
      expiryDate = "30 أيلول"
    )
    assertNotNull(offer)
    assertEquals(1, offer.id)
    assertEquals("خصم 20% على تذاكر دبي", offer.title)
    assertEquals("طيران", offer.categoryName)
    assertEquals("الخطوط الجوية العراقية", offer.providerName)
    assertEquals("20% الخصم", offer.discountTag)
  }
}
