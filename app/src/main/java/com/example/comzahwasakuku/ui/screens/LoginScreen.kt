package com.example.comzahwasakuku.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.comzahwasakuku.ui.theme.CyanPrimary
import com.example.comzahwasakuku.ui.viewmodel.AuthViewModel
import com.example.comzahwasakuku.ui.viewmodel.LoginState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    // --- STATE ---
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val loginState by viewModel.loginState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Sync Status Bar (Teal Deep)
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color(0xFF00ACC1).toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                Toast.makeText(context, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                onLoginSuccess()
            }
            is LoginState.Error -> {
                val errorMsg = (loginState as LoginState.Error).message
                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFB))) {
        // 1. DYNAMIC BACKGROUND (Header Gradient yang lebih bertekstur)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF00BCD4), Color(0xFF0097A7))
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            // 2. BRANDING SECTION (Logo dengan Glow Effect)
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .shadow(
                        elevation = 30.dp,
                        shape = CircleShape,
                        ambientColor = Color.White.copy(alpha = 0.5f),
                        spotColor = Color.White.copy(alpha = 0.5f)
                    )
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    tint = Color(0xFF0097A7),
                    modifier = Modifier.size(52.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "SAKUKU",
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 4.sp
            )
            Text(
                text = "Asisten Finansial Pintar Mahasiswa",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(36.dp))

            // 3. LOGIN CARD (Refined floating card)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(32.dp),
                        ambientColor = Color.Black.copy(alpha = 0.1f)
                    ),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F3F4))
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Masuk Ke Akun",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF263238)
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Input Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = Color(0xFFF1F3F4),
                            focusedLabelColor = CyanPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Input Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = Color(0xFFF1F3F4),
                            focusedLabelColor = CyanPrimary
                        ),
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = null, tint = Color.LightGray)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Button Login dengan Bayangan Cyan
                    Button(
                        onClick = {
                            if (email.isNotEmpty() && password.length >= 6) {
                                viewModel.login(email, password)
                            } else {
                                Toast.makeText(context, "Email & Password tidak valid", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(
                                elevation = 12.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = CyanPrimary
                            ),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                        shape = RoundedCornerShape(16.dp),
                        enabled = loginState !is LoginState.Loading
                    ) {
                        if (loginState is LoginState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                "Masuk Sekarang",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 4. FOOTER (Navigasi ke Register)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 48.dp)
            ) {
                Text("Belum punya akun? ", color = Color.Gray, fontSize = 14.sp)
                Text(
                    text = "Daftar Akun",
                    color = CyanPrimary,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }
        }
    }
}