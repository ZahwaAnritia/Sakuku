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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
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
    LaunchedEffect(Unit) {
        viewModel.resetState()
    }

    // --- STATE ---
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // State untuk kontrol Dialog
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val loginState by viewModel.loginState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Sync Status Bar
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color(0xFF00ACC1).toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    // --- MONITOR LOGIN STATE ---
    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                showSuccessDialog = true
            }
            is LoginState.Error -> {
                errorMessage = (loginState as LoginState.Error).message
                showErrorDialog = true
                viewModel.resetState() // Bersihkan state agar tidak loop
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFB))) {
        // Gradient Header
        Box(
            modifier = Modifier.fillMaxWidth().height(340.dp)
                .background(Brush.verticalGradient(listOf(Color(0xFF00BCD4), Color(0xFF0097A7))))
        )

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            // Logo Icon
            Box(
                modifier = Modifier.size(100.dp).shadow(30.dp, CircleShape).clip(CircleShape).background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AccountBalanceWallet, null, tint = Color(0xFF0097A7), modifier = Modifier.size(52.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("SAKUKU", fontSize = 36.sp, fontWeight = FontWeight.Black, color = Color.White, letterSpacing = 4.sp)
            Text("Asisten Finansial Pintar Mahasiswa", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))

            Spacer(modifier = Modifier.height(36.dp))

            // Card Login
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).shadow(20.dp, RoundedCornerShape(32.dp)),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Masuk Ke Akun", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(28.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanPrimary, focusedLabelColor = CyanPrimary)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanPrimary, focusedLabelColor = CyanPrimary),
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = null)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$"
                            if (email.isBlank() || password.isBlank()) {
                                errorMessage = "Email dan Password wajib diisi!"
                                showErrorDialog = true
                            } else if (!email.matches(emailPattern.toRegex())) {
                                errorMessage = "Format email salah!"
                                showErrorDialog = true
                            } else {
                                viewModel.login(email, password)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp).shadow(12.dp, RoundedCornerShape(16.dp)),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                        shape = RoundedCornerShape(16.dp),
                        enabled = loginState !is LoginState.Loading
                    ) {
                        if (loginState is LoginState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Masuk Sekarang", fontSize = 16.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Row(modifier = Modifier.padding(bottom = 48.dp)) {
                Text("Belum punya akun? ", color = Color.Gray)
                Text("Daftar Akun", color = CyanPrimary, fontWeight = FontWeight.Black, modifier = Modifier.clickable { onNavigateToRegister() })
            }
        }

        // --- 1. DIALOG SUKSES (HIJAU) ---
        if (showSuccessDialog) {
            androidx.compose.ui.window.Dialog(onDismissRequest = {
                showSuccessDialog = false
                onLoginSuccess()
            }) {
                Surface(modifier = Modifier.fillMaxWidth(0.75f).wrapContentHeight(), shape = RoundedCornerShape(28.dp), color = Color.White) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.size(64.dp).background(Color(0xFFE8F5E9), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(36.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Halo!", fontSize = 20.sp, fontWeight = FontWeight.Black)
                        Text("Login berhasil.", color = Color.Gray, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = { showSuccessDialog = false; onLoginSuccess() }, modifier = Modifier.fillMaxWidth().height(46.dp), colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary), shape = RoundedCornerShape(12.dp)) {
                            Text("Lanjut", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // --- 2. DIALOG ERROR (MERAH) ---
        if (showErrorDialog) {
            androidx.compose.ui.window.Dialog(onDismissRequest = { showErrorDialog = false }) {
                Surface(modifier = Modifier.fillMaxWidth(0.75f).wrapContentHeight(), shape = RoundedCornerShape(28.dp), color = Color.White) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.size(64.dp).background(Color(0xFFFFEBEE), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Warning, null, tint = Color(0xFFFF5252), modifier = Modifier.size(36.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Gagal", fontSize = 20.sp, fontWeight = FontWeight.Black)
                        Text(errorMessage, color = Color.Gray, fontSize = 14.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = { showErrorDialog = false }, modifier = Modifier.fillMaxWidth().height(46.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)), shape = RoundedCornerShape(12.dp)) {
                            Text("Coba Lagi", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}