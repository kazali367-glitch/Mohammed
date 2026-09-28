package com.example.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TaxiBookingScreen() {
    val context = LocalContext.current
    var pickupLocation by remember { mutableStateOf("") }
    var dropoffLocation by remember { mutableStateOf("") }
    var isBooked by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("taxi_booking_screen")
    ) {
        Text("حجز تكسي", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = pickupLocation,
            onValueChange = { 
                pickupLocation = it
                if (isBooked) isBooked = false
            },
            label = { Text("موقع الانطلاق") },
            placeholder = { Text("مثال: شارع الرشيد، بغداد") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("taxi_pickup_input")
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = dropoffLocation,
            onValueChange = { 
                dropoffLocation = it
                if (isBooked) isBooked = false
            },
            label = { Text("وجهة الوصول") },
            placeholder = { Text("مثال: مطار بغداد الدولي") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("taxi_dropoff_input")
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (pickupLocation.isBlank() || dropoffLocation.isBlank()) {
                    Toast.makeText(context, "يرجى إدخال موقع الانطلاق ووجهة الوصول", Toast.LENGTH_SHORT).show()
                } else {
                    isBooked = true
                    Toast.makeText(context, "تم تأكيد طلب التكسي بنجاح! الكابتن في الطريق إليك", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("taxi_confirm_button")
        ) {
            Text("تأكيد طلب التكسي", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        if (isBooked) {
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("taxi_booking_confirmation_card"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "تم التأكيد",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "تم تثبيت حجز التكسي بنجاح",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "🚕 الكابتن: أحمد العراقي (تويوتا كورولا 2023 - بغداد)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        "⏱ وقت الوصول المتوقع: 5 دقائق",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        "📍 من: $pickupLocation إلى: $dropoffLocation",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        "💵 الأجرة التقديرية: 8,000 دينار عراقي",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
