package com.example.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

data class UserProfile(
    val name: String,
    val phoneNumber: String,
    val address: String = ""
) {
    val phone: String get() = phoneNumber
}

@Composable
fun LoginScreen(onLoginSuccess: (UserProfile) -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag("login_screen"),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("مرحباً بك في التطبيق الشامل", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("يرجى التسجيل للمتابعة", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("الاسم الكامل") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("login_name_input"),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("رقم الهاتف (مثال: 07700000000)") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("login_phone_input"),
            singleLine = true
        )

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (name.trim().isEmpty() || phone.trim().isEmpty()) {
                    errorMessage = "يرجى إدخال الاسم ورقم الهاتف بشكل صحيح"
                } else {
                    onLoginSuccess(UserProfile(name = name.trim(), phoneNumber = phone.trim()))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("login_submit_button")
        ) {
            Text("تسجيل الدخول / البدء")
        }
    }
}
