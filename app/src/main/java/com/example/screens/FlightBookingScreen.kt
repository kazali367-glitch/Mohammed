package com.example.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class AvailableFlight(
    val flightNumber: String,
    val airline: String,
    val fromCity: String,
    val toCity: String,
    val departureTime: String,
    val price: String
)

@Composable
fun FlightBookingScreen() {
    val context = LocalContext.current
    var origin by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var searchPerformed by remember { mutableStateOf(false) }

    val sampleFlights = listOf(
        AvailableFlight("IA-101", "الخطوط الجوية العراقية", "مطار بغداد الدولي (BGW)", "إسطنبول (IST)", "08:30 ص", "250 $"),
        AvailableFlight("IA-204", "الخطوط الجوية العراقية", "مطار بغداد الدولي (BGW)", "دبي (DXB)", "11:15 ص", "310 $"),
        AvailableFlight("EK-902", "طيران الإمارات", "مطار بغداد الدولي (BGW)", "دبي (DXB)", "03:45 م", "420 $"),
        AvailableFlight("TK-803", "الخطوط التركية", "مطار بغداد الدولي (BGW)", "إسطنبول (IST)", "06:00 م", "340 $")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("flight_booking_screen")
    ) {
        Text("حجز تذاكر طيران", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = origin,
            onValueChange = { 
                origin = it
                searchPerformed = false
            },
            label = { Text("من (المطار/المدينة)") },
            placeholder = { Text("مثال: بغداد (BGW)") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("flight_origin_input")
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = destination,
            onValueChange = { 
                destination = it
                searchPerformed = false
            },
            label = { Text("إلى (المطار/المدينة)") },
            placeholder = { Text("مثال: إسطنبول (IST)") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("flight_destination_input")
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                searchPerformed = true
                Toast.makeText(context, "جاري استعراض الرحلات المتوفرة...", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("flight_search_button")
        ) {
            Text("البحث عن الرحلات", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        if (searchPerformed) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "الرحلات المتوفرة:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(sampleFlights, key = { it.flightNumber }) { flight ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("flight_card_${flight.flightNumber}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Flight,
                                        contentDescription = "طائرة",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = flight.airline,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = flight.flightNumber,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${flight.fromCity} ➔ ${flight.toCity}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "وقت الإقلاع: ${flight.departureTime}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = flight.price,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    Toast.makeText(context, "تم اختيار الرحلة ${flight.flightNumber} بنجاح", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("حجز هذه الرحلة")
                            }
                        }
                    }
                }
            }
        }
    }
}
