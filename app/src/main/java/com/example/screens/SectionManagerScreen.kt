package com.example.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class SectionData(
    val id: String,
    val title: String,
    val items: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectionManagerScreen() {
    // حالة الأقسام وقائمة العناصر التابعة لكل قسم (Sections State)
    var sections by remember {
        mutableStateOf(
            listOf(
                SectionData(
                    id = "sec_1",
                    title = "قسم الأخبار",
                    items = listOf("الخبر الأول: إطلاق النسخة الجديدة", "الخبر الثاني: تحديث أسعار الصرف")
                ),
                SectionData(
                    id = "sec_2",
                    title = "قسم المنتجات",
                    items = listOf("منتج أ: هاتف ذكي حديث", "منتج ب: شاحن سريع 65W")
                ),
                SectionData(
                    id = "sec_3",
                    title = "قسم الخدمات السريعة",
                    items = listOf("خدمة التوصيل السريع", "حجز التكسي الفوري")
                )
            )
        )
    }

    // حالة لتتبع نص الإدخال الخاص بكل قسم بشكل مستقل (inputValues)
    var inputValues by remember { mutableStateOf(mapOf<String, String>()) }

    // حوار لإضافة قسم رئيسي جديد
    var showAddSectionDialog by remember { mutableStateOf(false) }
    var newSectionTitle by remember { mutableStateOf("") }

    // دالة إضافة عنصر جديد إلى القسم المستهدف
    val handleAddItem: (String) -> Unit = { sectionId ->
        val text = inputValues[sectionId]?.trim() ?: ""
        if (text.isNotEmpty()) {
            sections = sections.map { section ->
                if (section.id == sectionId) {
                    section.copy(items = section.items + text)
                } else {
                    section
                }
            }
            // إعادة تفريغ حقل الإدخال الخاص بهذا القسم فقط
            inputValues = inputValues + (sectionId to "")
        }
    }

    // دالة حذف عنصر من قسم
    val handleDeleteItem: (String, Int) -> Unit = { sectionId, itemIndex ->
        sections = sections.map { section ->
            if (section.id == sectionId) {
                val updatedItems = section.items.toMutableList()
                if (itemIndex in updatedItems.indices) {
                    updatedItems.removeAt(itemIndex)
                }
                section.copy(items = updatedItems)
            } else {
                section
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddSectionDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "إضافة قسم جديد") },
                text = { Text("إضافة قسم جديد", fontWeight = FontWeight.Bold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_new_section_fab")
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .testTag("section_manager_list"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ترويسة الشاشة
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "📑 إدارة أقسام التطبيق والعناصر",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "يمكنك إدارة الأقسام وإضافة عناصر جديدة لكل قسم وحذفها في الوقت الفعلي.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // بطاقات الأقسام
            items(sections, key = { it.id }) { section ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("section_card_${section.id}")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // عنوان القسم وعدد العناصر
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = section.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(
                                    text = "${section.items.size} عناصر",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // قائمة عناصر القسم
                        if (section.items.isEmpty()) {
                            Text(
                                text = "لا توجد عناصر في هذا القسم حتى الآن.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                section.items.forEachIndexed { index, itemText ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = "•",
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                            Text(
                                                text = itemText,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        IconButton(
                                            onClick = { handleDeleteItem(section.id, index) },
                                            modifier = Modifier
                                                .size(32.dp)
                                                .testTag("delete_item_${section.id}_$index")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "حذف العنصر",
                                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // حقل إدخال إضافة عنصر وزر الإضافة
                        val currentText = inputValues[section.id] ?: ""
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = currentText,
                                onValueChange = { newText ->
                                    inputValues = inputValues + (section.id to newText)
                                },
                                placeholder = {
                                    Text("أدخل اسم العنصر الجديد...", fontSize = 13.sp)
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_field_${section.id}")
                            )

                            Button(
                                onClick = { handleAddItem(section.id) },
                                shape = RoundedCornerShape(10.dp),
                                enabled = currentText.isNotBlank(),
                                modifier = Modifier
                                    .height(52.dp)
                                    .testTag("add_item_button_${section.id}")
                            ) {
                                Text("إضافة", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // مساحة إضافية في الأسفل لزر الـ FAB
            item {
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }

    // نافذة منبثقة لإضافة قسم جديد
    if (showAddSectionDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddSectionDialog = false
                newSectionTitle = ""
            },
            title = { Text("إضافة قسم جديد") },
            text = {
                Column {
                    Text(
                        "أدخل عنوان القسم الجديد ليتم إدراجه في قائمة أقسام التطبيق:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newSectionTitle,
                        onValueChange = { newSectionTitle = it },
                        label = { Text("عنوان القسم") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmed = newSectionTitle.trim()
                        if (trimmed.isNotEmpty()) {
                            val newId = "sec_${System.currentTimeMillis()}"
                            sections = sections + SectionData(id = newId, title = trimmed, items = emptyList())
                            showAddSectionDialog = false
                            newSectionTitle = ""
                        }
                    },
                    enabled = newSectionTitle.isNotBlank()
                ) {
                    Text("إضافة القسم")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAddSectionDialog = false
                        newSectionTitle = ""
                    }
                ) {
                    Text("إلغاء")
                }
            }
        )
    }
}
