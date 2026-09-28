package com.example.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// نموذج بيانات المهنيين والمهن
data class Professional(
    val id: Int,
    val name: String,
    val profession: String, // دكتور، محامي، سباك، نجار، كهربائي...
    val specialty: String,  // التخصص الفرعي (مثل: جراحة عظام، قانون جنائي)
    val phone: String,
    val address: String,
    val rating: Double      // تقييم الخدمة
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionsSearchScreen() {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedProfessionFilter by remember { mutableStateOf("الكل") }

    val professionFilters = listOf("الكل", "دكتور", "محامي", "كهربائي", "سباك", "نجار", "تبريد وتكييف")

    val sampleProfessionals = remember {
        listOf(
            Professional(
                id = 1,
                name = "د. علي التميمي",
                profession = "دكتور",
                specialty = "جراحة عظام ومفاصل",
                phone = "07701112233",
                address = "المنصور، شارع 14 رمضان - بغداد",
                rating = 4.9
            ),
            Professional(
                id = 2,
                name = "أ. فاطمة الموسوي",
                profession = "محامي",
                specialty = "قانون جنائي واستشارات",
                phone = "07802223344",
                address = "الكرادة خارج - بغداد",
                rating = 4.8
            ),
            Professional(
                id = 3,
                name = "أحمد الصالحي",
                profession = "كهربائي",
                specialty = "تمديدات منزلية وصيانة لوحات توزيع",
                phone = "07703334455",
                address = "حي الجامعة - بغداد",
                rating = 4.9
            ),
            Professional(
                id = 4,
                name = "حيدر الكرخي",
                profession = "سباك",
                specialty = "تأسيس شبكات مياه وصرف صحي",
                phone = "07804445566",
                address = "شارع فلسطين - بغداد",
                rating = 4.7
            ),
            Professional(
                id = 5,
                name = "عمر العبيدي",
                profession = "نجار",
                specialty = "تصنيع وصيانة أثاث ومطابخ حديثة",
                phone = "07905556677",
                address = "زيونة - بغداد",
                rating = 4.8
            ),
            Professional(
                id = 6,
                name = "د. زينب الشمري",
                profession = "دكتور",
                specialty = "طب وتجميل الأسنان",
                phone = "07506667788",
                address = "الحارثية، مجمع الأطباء - بغداد",
                rating = 4.9
            ),
            Professional(
                id = 7,
                name = "أ. مصطفى الراوي",
                profession = "محامي",
                specialty = "قضايا تجارية وعقود شركات",
                phone = "07817778899",
                address = "الجادرية - بغداد",
                rating = 4.7
            ),
            Professional(
                id = 8,
                name = "ماجد الجبوري",
                profession = "تبريد وتكييف",
                specialty = "صيانة وتنصيب وحدات تكييف وسبالت",
                phone = "07728889900",
                address = "الدورة - بغداد",
                rating = 4.8
            )
        )
    }

    val filteredList = remember(searchQuery, selectedProfessionFilter, sampleProfessionals) {
        val query = searchQuery.trim().lowercase()
        sampleProfessionals.filter { item ->
            val matchesFilter = selectedProfessionFilter == "الكل" || item.profession == selectedProfessionFilter
            val matchesQuery = query.isEmpty() ||
                    item.name.lowercase().contains(query) ||
                    item.profession.lowercase().contains(query) ||
                    item.specialty.lowercase().contains(query) ||
                    item.address.lowercase().contains(query)
            matchesFilter && matchesQuery
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("professions_screen")
    ) {
        Text(
            text = "دليل المهن والخدمات",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("ابحث بالاسم، المهنة، التخصص أو العنوان...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "أيقونة البحث",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { searchQuery = "" },
                        modifier = Modifier.testTag("clear_professions_search")
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
                .testTag("professions_search_field")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Profession Quick Filters
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(professionFilters) { filter ->
                FilterChip(
                    selected = selectedProfessionFilter == filter,
                    onClick = { selectedProfessionFilter = filter },
                    label = { Text(filter) },
                    modifier = Modifier.testTag("filter_chip_$filter")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔍", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "لا توجد نتائج تطابق بحثك",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredList, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profession_card_${item.id}"),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "التقييم",
                                        tint = MaterialTheme.colorScheme.tertiary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${item.rating}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = item.profession,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(
                                    text = item.specialty,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "📍 العنوان: ${item.address}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "📞 ${item.phone}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL).apply {
                                            data = Uri.parse("tel:${item.phone}")
                                        }
                                        context.startActivity(intent)
                                    },
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("call_button_${item.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Call,
                                        contentDescription = "اتصال",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("اتصال الآن")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
