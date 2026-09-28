package com.example.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

// نموذج بيانات العرض الخاص
data class SpecialOffer(
    val id: Int,
    val title: String,            // عنوان العرض (مثال: خصم 20% على تذاكر دبي)
    val categoryName: String,     // اسم القسم المصدر (طيران، أجهزة، محلات، مهنيين...)
    val providerName: String,     // اسم الجهة أو المحل أو الدكتور
    val originalPrice: String,    // السعر الأصلي
    val discountedPrice: String,  // السعر بعد الخصم
    val discountTag: String,      // نسبة الخصم (مثال: "20% الخصم")
    val description: String,      // تفاصيل العرض
    val expiryDate: String        // تاريخ انتهاء العرض
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecialOffersScreen() {
    val context = LocalContext.current
    var selectedCategoryFilter by remember { mutableStateOf("الكل") }

    val categories = listOf("الكل", "طيران", "أجهزة", "محلات", "مهن وخدمات", "توصيل")

    // قائمة بيانات تجريبية للعروض الخاصة المجمعة من جميع الأقسام
    val offersList = listOf(
        SpecialOffer(
            id = 1,
            title = "خصم خاص على رحلات بغداد - دبي",
            categoryName = "طيران",
            providerName = "شركة الخطوط الجوية",
            originalPrice = "350 $",
            discountedPrice = "270 $",
            discountTag = "خصم 23%",
            description = "عرض محدود لحجوزات نهاية الأسبوع شاملة الحقائب.",
            expiryDate = "ينتهي العرض غداً"
        ),
        SpecialOffer(
            id = 2,
            title = "عرض أجهزة iPhone 15 Pro جديدة",
            categoryName = "أجهزة",
            providerName = "معرض المدينة للالكترونيات",
            originalPrice = "1,100 $",
            discountedPrice = "950 $",
            discountTag = "خصم 15%",
            description = "ضمان لمدة سنة كاملة مع كفر وحماية شاشة مجاناً.",
            expiryDate = "صالح لمدة 3 أيام"
        ),
        SpecialOffer(
            id = 3,
            title = "خصم على فحص واستشارة جراحة العظام",
            categoryName = "مهن وخدمات",
            providerName = "د. أحمد علي (طبيب)",
            originalPrice = "35,000 د.ع",
            discountedPrice = "20,000 د.ع",
            discountTag = "خصم 40%",
            description = "خصم خاص للمراجعين عبر التطبيق الشامل.",
            expiryDate = "ينتهي نهاية الشهر"
        ),
        SpecialOffer(
            id = 4,
            title = "توصيل مجاني لطلبات الشراء فوق 30 ألف",
            categoryName = "توصيل",
            providerName = "خدمة التوصيل السريع",
            originalPrice = "5,000 د.ع",
            discountedPrice = "مجاناً",
            discountTag = "توصيل 0$",
            description = "اطلب من أي محل محدد واحصل على توصيل مجاني.",
            expiryDate = "عرض اليوم فقط"
        ),
        SpecialOffer(
            id = 5,
            title = "تخفيضات على ملابس الأطفال الشتوية",
            categoryName = "محلات",
            providerName = "محل الأناقة - المنصور",
            originalPrice = "50,000 د.ع",
            discountedPrice = "30,000 د.ع",
            discountTag = "خصم 40%",
            description = "تخفيض شامل على كافة التشكيلة الجديدة.",
            expiryDate = "حتى نفاد الكمية"
        )
    )

    // تصفية العروض حسب الفئة المختارة
    val filteredOffers = offersList.filter { offer ->
        if (selectedCategoryFilter == "الكل") true
        else offer.categoryName == selectedCategoryFilter
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("special_offers_screen")
    ) {
        // بطاقة ترحيب وتنبيه العروض
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🔥 العروض الحصرية والتخفيضات",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "تصفح أحدث العروض والخصومات المجمعة من كافة المحلات والأقسام.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // شريط تصفية العروض بحسب القسم
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = (selectedCategoryFilter == category),
                    onClick = { selectedCategoryFilter = category },
                    label = { Text(category) },
                    modifier = Modifier.testTag("filter_chip_$category")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // عرض قائمة العروض
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(filteredOffers, key = { it.id }) { offer ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("offer_card_${offer.id}"),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = offer.title,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Badge(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            ) {
                                Text(
                                    text = offer.discountTag,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SuggestionChip(
                                onClick = { },
                                label = { Text("القسم: ${offer.categoryName}") }
                            )
                            SuggestionChip(
                                onClick = { },
                                label = { Text(offer.providerName) }
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = offer.description, style = MaterialTheme.typography.bodyMedium)

                        Spacer(modifier = Modifier.height(10.dp))

                        // عرض الأسعار قبل وبعد الخصم
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = offer.originalPrice,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        textDecoration = TextDecoration.LineThrough
                                    ),
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Text(
                                    text = offer.discountedPrice,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Text(
                                text = "⏳ ${offer.expiryDate}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                Toast.makeText(context, "تم تفعيل العرض: ${offer.title}", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("claim_offer_button_${offer.id}")
                        ) {
                            Text("الاستفادة من العرض الآن")
                        }
                    }
                }
            }
        }
    }
}
