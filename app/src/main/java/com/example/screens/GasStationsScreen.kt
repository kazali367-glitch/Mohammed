package com.example.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch

// نموذج بيانات محطات الوقود والمنتجات المتاحة مع الإحداثيات الجغرافية
data class GasStation(
    val id: Int,
    val stationName: String,     // اسم المحطة
    val stationType: String,     // حكومية / أهلية
    val address: String,         // المنطقة / الشارع
    val availableProducts: List<String>, // مثل: ["بنزين محسن", "كاز", "غاز"]
    val workHours: String,       // أوقات العمل لليوم التالي
    val dateNotice: String,      // تاريخ اليوم التالي المخصص للتوزيع
    val latitude: Double = 33.3152,  // خط العرض
    val longitude: Double = 44.3661  // خط الطول
)

enum class GasViewMode {
    MAP,      // عرض الخريطة التفاعلية بالكامل
    LIST,     // عرض القائمة بالبطاقات
    SPLIT     // عرض مزدوج (خريطة علوية وقائمة سفلية)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GasStationsScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedProductFilter by remember { mutableStateOf("الكل") }
    var currentViewMode by remember { mutableStateOf(GasViewMode.MAP) }
    var selectedStation by remember { mutableStateOf<GasStation?>(null) }
    var isSatelliteView by remember { mutableStateOf(false) }

    val productFilters = listOf("الكل", "بنزين عادي", "بنزين محسن", "كاز", "غاز")

    // بيانات المحطات مع مواقع جغرافية حقيقية في بغداد
    val stationsList = remember {
        listOf(
            GasStation(
                id = 1,
                stationName = "محطة الرافدين الحكومية",
                stationType = "حكومية",
                address = "الشارع العام - مقابل المستشفى",
                availableProducts = listOf("بنزين عادي", "بنزين محسن", "كاز"),
                workHours = "من 06:00 صباحاً حتى 12:00 ليلاً",
                dateNotice = "جدول غداً",
                latitude = 33.3152,
                longitude = 44.3661
            ),
            GasStation(
                id = 2,
                stationName = "محطة السلام الأهلية",
                stationType = "أهلية",
                address = "حي المعلمين - قرب التقاطع",
                availableProducts = listOf("بنزين محسن", "غاز"),
                workHours = "على مدار 24 ساعة",
                dateNotice = "جدول غداً",
                latitude = 33.3025,
                longitude = 44.3820
            ),
            GasStation(
                id = 3,
                stationName = "ساحة غاز الجنوب المركزية",
                stationType = "حكومية",
                address = "المنطقة الصناعية",
                availableProducts = listOf("غاز"),
                workHours = "من 07:00 صباحاً حتى 04:00 عصراً",
                dateNotice = "جدول غداً",
                latitude = 33.2680,
                longitude = 44.4015
            ),
            GasStation(
                id = 4,
                stationName = "محطة النور الأهلية",
                stationType = "أهلية",
                address = "طريق السريع",
                availableProducts = listOf("كاز", "بنزين عادي"),
                workHours = "من 06:00 صباحاً حتى 10:00 مساءً",
                dateNotice = "جدول غداً",
                latitude = 33.3410,
                longitude = 44.4250
            ),
            GasStation(
                id = 5,
                stationName = "محطة المنصور النموذجية",
                stationType = "حكومية",
                address = "حي المنصور - شارع 14 رمضان",
                availableProducts = listOf("بنزين عادي", "بنزين محسن", "غاز"),
                workHours = "من 06:00 صباحاً حتى 01:00 بعد منتصف الليل",
                dateNotice = "جدول غداً",
                latitude = 33.3082,
                longitude = 44.3495
            ),
            GasStation(
                id = 6,
                stationName = "محطة الجادرية الحديثة",
                stationType = "أهلية",
                address = "الجادرية - قرب مجمع الجامعات",
                availableProducts = listOf("بنزين محسن", "كاز"),
                workHours = "على مدار 24 ساعة",
                dateNotice = "جدول غداً",
                latitude = 33.2785,
                longitude = 44.3750
            )
        )
    }

    // تصفية القائمة بحسب المادة المختارة
    val filteredStations = remember(selectedProductFilter, stationsList) {
        stationsList.filter { station ->
            if (selectedProductFilter == "الكل") true
            else station.availableProducts.contains(selectedProductFilter)
        }
    }

    // إعداد حالة كاميرا خريطة Google (مركز بغداد)
    val baghdadCenter = LatLng(33.3100, 44.3800)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(baghdadCenter, 12f)
    }

    // إعدادات واجهة وخصائص خريطة Google
    val mapUiSettings = remember {
        MapUiSettings(
            zoomControlsEnabled = false,
            compassEnabled = true,
            mapToolbarEnabled = true,
            myLocationButtonEnabled = false
        )
    }

    val mapProperties = remember(isSatelliteView) {
        MapProperties(
            mapType = if (isSatelliteView) MapType.SATELLITE else MapType.NORMAL,
            isMyLocationEnabled = false
        )
    }

    // دالة فتح الموقع والتوجيه في تطبيق Google Maps الخارجي
    fun openNavigation(context: Context, station: GasStation) {
        try {
            val uri = Uri.parse("google.navigation:q=${station.latitude},${station.longitude}&mode=d")
            val mapIntent = Intent(Intent.ACTION_VIEW, uri)
            mapIntent.setPackage("com.google.android.apps.maps")
            if (mapIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(mapIntent)
            } else {
                val geoUri = Uri.parse("geo:${station.latitude},${station.longitude}?q=${station.latitude},${station.longitude}(${Uri.encode(station.stationName)})")
                context.startActivity(Intent(Intent.ACTION_VIEW, geoUri))
            }
        } catch (_: Exception) {
            val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${station.latitude},${station.longitude}")
            context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("gas_stations_screen")
    ) {
        // بطاقة تنبيه لجدول اليوم التالي
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "📢 جدول توزيع المشتقات النفطية لليوم التالي",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "مواقع المحطات المتاحة جغرافياً على خريطة Google التفاعلية مع خيارات التوجيه المباشر.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // أزرار التبديل بين أنماط العرض (خريطة / قائمة / مزدوج)
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3),
                onClick = { currentViewMode = GasViewMode.MAP },
                selected = currentViewMode == GasViewMode.MAP,
                icon = { Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("view_mode_map")
            ) {
                Text("الخريطة 🗺️", fontSize = 12.sp)
            }
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3),
                onClick = { currentViewMode = GasViewMode.SPLIT },
                selected = currentViewMode == GasViewMode.SPLIT,
                icon = { Icon(Icons.Default.ViewAgenda, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("view_mode_split")
            ) {
                Text("مزدوج 🧭", fontSize = 12.sp)
            }
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3),
                onClick = { currentViewMode = GasViewMode.LIST },
                selected = currentViewMode == GasViewMode.LIST,
                icon = { Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(16.dp)) },
                modifier = Modifier.testTag("view_mode_list")
            ) {
                Text("القائمة 📋", fontSize = 12.sp)
            }
        }

        // شريط فلترة حسب المادة المتوفرة
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            items(productFilters) { filter ->
                FilterChip(
                    selected = (selectedProductFilter == filter),
                    onClick = {
                        selectedProductFilter = filter
                        selectedStation = null
                    },
                    label = { Text(filter) },
                    modifier = Modifier.testTag("gas_prod_filter_$filter")
                )
            }
        }

        // المحتوى الرئيسي حسب نمط العرض
        when (currentViewMode) {
            GasViewMode.MAP -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    // مكون خريطة Google المدمج
                    GoogleMap(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("google_map_view"),
                        cameraPositionState = cameraPositionState,
                        properties = mapProperties,
                        uiSettings = mapUiSettings,
                        onMapClick = { selectedStation = null }
                    ) {
                        filteredStations.forEach { station ->
                            val markerState = rememberMarkerState(
                                key = "station_${station.id}",
                                position = LatLng(station.latitude, station.longitude)
                            )
                            Marker(
                                state = markerState,
                                title = station.stationName,
                                snippet = "${station.stationType} • ${station.availableProducts.joinToString("، ")}",
                                onClick = {
                                    selectedStation = station
                                    coroutineScope.launch {
                                        cameraPositionState.animate(
                                            CameraUpdateFactory.newLatLngZoom(
                                                LatLng(station.latitude, station.longitude),
                                                14.5f
                                            )
                                        )
                                    }
                                    true
                                }
                            )
                        }
                    }

                    // أزرار التحكم الطافية فوق الخريطة
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // زر تبديل طبقة القمر الصناعي / العادي
                        SmallFloatingActionButton(
                            onClick = { isSatelliteView = !isSatelliteView },
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.testTag("toggle_satellite_btn")
                        ) {
                            Icon(Icons.Default.Layers, contentDescription = "تبديل نوع الخريطة")
                        }

                        // زر إعادة التمركز لمركز المدينة وجميع المحطات
                        SmallFloatingActionButton(
                            onClick = {
                                coroutineScope.launch {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(baghdadCenter, 12f)
                                    )
                                }
                            },
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.testTag("recenter_map_btn")
                        ) {
                            Icon(Icons.Default.MyLocation, contentDescription = "إعادة تمركز الخريطة")
                        }
                    }

                    // شارة عدد المحطات المتاحة على الخريطة
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${filteredStations.size} محطات متوفرة",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // بطاقة تفاصيل المحطة المحددة تطفو بالأسفل
                    if (selectedStation != null) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp)
                        ) {
                            selectedStation?.let { station ->
                                StationDetailCard(
                                    station = station,
                                    onClose = { selectedStation = null },
                                    onNavigate = { openNavigation(context, station) }
                                )
                            }
                        }
                    }
                }
            }

            GasViewMode.SPLIT -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    // خريطة علوية بنصف الشاشة
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        GoogleMap(
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("google_map_split_view"),
                            cameraPositionState = cameraPositionState,
                            properties = mapProperties,
                            uiSettings = mapUiSettings,
                            onMapClick = { selectedStation = null }
                        ) {
                            filteredStations.forEach { station ->
                                val markerState = rememberMarkerState(
                                    key = "split_station_${station.id}",
                                    position = LatLng(station.latitude, station.longitude)
                                )
                                Marker(
                                    state = markerState,
                                    title = station.stationName,
                                    snippet = station.stationType,
                                    onClick = {
                                        selectedStation = station
                                        coroutineScope.launch {
                                            cameraPositionState.animate(
                                                CameraUpdateFactory.newLatLngZoom(
                                                    LatLng(station.latitude, station.longitude),
                                                    14.5f
                                                )
                                            )
                                        }
                                        true
                                    }
                                )
                            }
                        }

                        // زر التمركز
                        SmallFloatingActionButton(
                            onClick = {
                                coroutineScope.launch {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(baghdadCenter, 12f)
                                    )
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                        ) {
                            Icon(Icons.Default.MyLocation, contentDescription = "تمركز")
                        }
                    }

                    // قائمة المحطات السفلية
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredStations, key = { it.id }) { station ->
                            StationCardItem(
                                station = station,
                                isSelected = (selectedStation?.id == station.id),
                                onSelect = {
                                    selectedStation = station
                                    coroutineScope.launch {
                                        cameraPositionState.animate(
                                            CameraUpdateFactory.newLatLngZoom(
                                                LatLng(station.latitude, station.longitude),
                                                14.5f
                                            )
                                        )
                                    }
                                },
                                onNavigate = { openNavigation(context, station) },
                                onShowOnMap = {
                                    selectedStation = station
                                    coroutineScope.launch {
                                        cameraPositionState.animate(
                                            CameraUpdateFactory.newLatLngZoom(
                                                LatLng(station.latitude, station.longitude),
                                                14.5f
                                            )
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }

            GasViewMode.LIST -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(filteredStations, key = { it.id }) { station ->
                        StationCardItem(
                            station = station,
                            isSelected = false,
                            onSelect = { },
                            onNavigate = { openNavigation(context, station) },
                            onShowOnMap = {
                                selectedStation = station
                                currentViewMode = GasViewMode.MAP
                                coroutineScope.launch {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(
                                            LatLng(station.latitude, station.longitude),
                                            14.5f
                                        )
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

// بطاقة تفاصيل المحطة المحددة فوق الخريطة
@Composable
private fun StationDetailCard(
    station: GasStation,
    onClose: () -> Unit,
    onNavigate: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("station_detail_floating_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = station.stationName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "📍 ${station.address}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "إغلاق")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SuggestionChip(
                    onClick = {},
                    label = { Text(station.stationType) }
                )
                Text(
                    text = "⏰ ${station.workHours}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // عرض المنتجات المتوفرة
            Text(
                text = "المشتقات المتوفرة غداً:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                station.availableProducts.forEach { product ->
                    AssistChip(
                        onClick = {},
                        label = { Text("⛽ $product", fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // زر التوجيه عبر الخريطة
            Button(
                onClick = onNavigate,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("navigate_to_station_btn"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("التوجيه والملاحة عبر Google Maps 🚗", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// عنصر بطاقة المحطة في القائمة
@Composable
private fun StationCardItem(
    station: GasStation,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onNavigate: () -> Unit,
    onShowOnMap: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .testTag("gas_station_card_${station.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = station.stationName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                SuggestionChip(
                    onClick = { },
                    label = { Text(station.stationType) }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "📍 العنوان: ${station.address}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "⏰ أوقات العمل غداً: ${station.workHours}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "المنتوجات المتاحة غداً:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // عرض شارات المنتوجات المتوفرة
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                station.availableProducts.forEach { product ->
                    AssistChip(
                        onClick = { },
                        label = { Text("⛽ $product") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // أزرار الإجراءات
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onShowOnMap,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("على الخريطة 🗺️", fontSize = 12.sp)
                }

                Button(
                    onClick = onNavigate,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("توجيه 🚗", fontSize = 12.sp)
                }
            }
        }
    }
}
