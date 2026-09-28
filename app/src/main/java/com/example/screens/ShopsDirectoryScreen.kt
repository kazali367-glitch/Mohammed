package com.example.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Shop(
    val id: Int,
    val name: String,
    val category: String,
    val address: String,
    val phone: String
)

@Composable
fun ShopsDirectoryScreen() {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    val sampleShops = remember {
        listOf(
            Shop(1, "المركز الفني", "صيانة هواتف", "شارع فلسطين - بغداد", "07700000000"),
            Shop(2, "صرافة الرافدين", "بيع وشراء عملات", "المنصور - بغداد", "07800000000"),
            Shop(3, "معرض المدينة", "بيع أجهزة إلكترونية", "الكرادة - بغداد", "07900000000"),
            Shop(4, "مكتبة ومطبعة النور", "قرطاسية وخدمات طباعة", "باب المعظم - بغداد", "07712345678"),
            Shop(5, "سوبرماركت البركة", "مواد غذائية وتسوق منزلي", "حي الجامعة - بغداد", "07812345678"),
            Shop(6, "صيدلية الشفاء", "أدوية ومستلزمات طبية", "زيونة - بغداد", "07501234567")
        )
    }

    val filteredShops = remember(searchQuery, sampleShops) {
        if (searchQuery.isBlank()) {
            sampleShops
        } else {
            val query = searchQuery.trim().lowercase()
            sampleShops.filter { shop ->
                shop.name.lowercase().contains(query) ||
                shop.category.lowercase().contains(query) ||
                shop.address.lowercase().contains(query)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("shops_directory_screen")
    ) {
        Text(
            text = "دليل المحلات حسب الاختصاص",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        // شريط البحث في أعلى الشاشة
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("ابحث عن محل بالاسم أو الاختصاص...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "بحث"
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { searchQuery = "" },
                        modifier = Modifier.testTag("clear_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "مسح البحث"
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("shops_search_bar")
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (filteredShops.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("shops_empty_state"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔍", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "لا توجد نتائج مطابقة لـ \"$searchQuery\"",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "جرب البحث باسم آخر أو اختصاص مختلف",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            Text(
                text = "النتائج (${filteredShops.size}):",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredShops, key = { it.id }) { shop ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${shop.phone}")
                                }
                                context.startActivity(dialIntent)
                            }
                            .testTag("shop_card_${shop.id}"),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = shop.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = shop.category,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "📍 العنوان: ${shop.address}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "📞 الهاتف: ${shop.phone}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
