package com.example.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class Device(
    val id: Int,
    val title: String,
    val price: Double,
    val isUsed: Boolean,
    val image: String
)

@Composable
fun DevicesScreen() {
    val sampleDevices = listOf(
        Device(1, "iPhone 15 Pro", 999.0, false, ""),
        Device(2, "Samsung S22 Ultra", 550.0, true, "")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("devices_screen"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sampleDevices, key = { it.id }) { device ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("device_card_${device.id}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = device.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (device.isUsed) "الحالة: مستعمل" else "الحالة: جديد",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (device.isUsed) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = "${device.price} $",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
