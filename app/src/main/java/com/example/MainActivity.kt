package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.screens.*
import com.example.ui.theme.SuperAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SuperAppTheme {
                MainApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    var userProfile by remember {
        mutableStateOf<UserProfile?>(UserProfile(name = "زائر", phoneNumber = "07700000000", address = "بغداد"))
    }
    var currentScreen by remember { mutableStateOf("home") }
    var showProfileDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = userProfile != null && currentScreen != "home") {
        currentScreen = "home"
    }

    // في حال عدم التسجيل، تظهر شاشة الدخول أولاً
    if (userProfile == null) {
        LoginScreen(onLoginSuccess = { profile -> userProfile = profile })
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (currentScreen == "home") "المنصة الشاملة" else getScreenTitle(currentScreen),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    navigationIcon = {
                        if (currentScreen != "home") {
                            TextButton(
                                onClick = { currentScreen = "home" },
                                modifier = Modifier.testTag("top_bar_back_button")
                            ) {
                                Text("رجوع", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                        }
                    },
                    actions = {
                        Surface(
                            onClick = { showProfileDialog = true },
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .testTag("user_badge")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("✨", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = userProfile?.name ?: "زائر",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                        navigationIconContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag("bottom_navigation_bar")
                ) {
                    NavigationBarItem(
                        selected = currentScreen == "home",
                        onClick = { currentScreen = "home" },
                        icon = { Icon(Icons.Default.Home, contentDescription = "الرئيسية") },
                        label = { Text("الرئيسية", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen == "offers",
                        onClick = { currentScreen = "offers" },
                        icon = { Text("🔥", fontSize = 18.sp) },
                        label = { Text("العروض", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFE63946),
                            selectedTextColor = Color(0xFFE63946),
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen == "delivery",
                        onClick = { currentScreen = "delivery" },
                        icon = { Text("📦", fontSize = 18.sp) },
                        label = { Text("توصيل", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                    NavigationBarItem(
                        selected = currentScreen == "flights",
                        onClick = { currentScreen = "flights" },
                        icon = { Text("✈️", fontSize = 18.sp) },
                        label = { Text("طيران", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentScreen) {
                    "home" -> HomeScreen(onNavigate = { route -> currentScreen = route })
                    "delivery" -> DeliveryOrderScreen(userProfile = userProfile!!)
                    "gas_stations" -> GasStationsScreen()
                    "devices" -> DevicesScreen()
                    "taxi" -> TaxiBookingScreen()
                    "currency" -> CurrencyExchangeScreen()
                    "flights" -> FlightBookingScreen()
                    "shops" -> ShopsDirectoryScreen()
                    "professions" -> ProfessionsSearchScreen()
                    "offers" -> SpecialOffersScreen()
                }
            }
        }
    }

    if (showProfileDialog) {
        var editName by remember { mutableStateOf(userProfile?.name ?: "") }
        var editPhone by remember { mutableStateOf(userProfile?.phoneNumber ?: "") }
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = { Text("الملف الشخصي / حساب المستخدم") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("يمكنك تحديث بياناتك أو التبديل إلى حساب آخر:", style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("الاسم") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("رقم الهاتف") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalName = editName.ifBlank { "زائر" }
                        val finalPhone = editPhone.ifBlank { "07700000000" }
                        userProfile = UserProfile(name = finalName, phoneNumber = finalPhone)
                        showProfileDialog = false
                    }
                ) {
                    Text("حفظ التغييرات")
                }
            },
            dismissButton = {
                TextButton(onClick = { showProfileDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

fun getScreenTitle(screen: String): String {
    return when (screen) {
        "devices" -> "بيع وشراء الأجهزة"
        "taxi" -> "حجز تكسي"
        "currency" -> "بيع وشراء العملات"
        "flights" -> "حجز تذاكر طيران"
        "shops" -> "دليل المحلات"
        "professions" -> "دليل المهن والخدمات"
        "delivery" -> "طلب وتوصيل من محل"
        "gas_stations" -> "جدول محطات الوقود والغاز"
        "offers" -> "العروض الخاصة والتخفيضات"
        else -> "التطبيق الشامل"
    }
}
