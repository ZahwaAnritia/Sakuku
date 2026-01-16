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
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.resetState()
    }

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var showSuccessDialog by remember { mutableStateOf(false) }

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val registerState by viewModel.loginState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()


    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color(0xFF00ACC1).toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }


    LaunchedEffect(registerState) {
        when (registerState) {
            is LoginState.Success -> {
                showSuccessDialog = true
            }
            is LoginState.Error -> {
                val errorMsg = (registerState as LoginState.Error).message
                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFB))) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
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
            Spacer(modifier = Modifier.height(50.dp))


            Box(
                modifier = Modifier
                    .size(80.dp)
                    .shadow(15.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    tint = CyanPrimary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Buat Akun",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 1.sp
            )
            Text(
                text = "Mulai kelola uang sakumu sekarang!",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(30.dp))

            // 3. REGISTER
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .shadow(25.dp, RoundedCornerShape(32.dp), ambientColor = Color.Black.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Daftar Baru",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF263238)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Input Nama Lengkap
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Nama Lengkap") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = Color(0xFFEEEEEE)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

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
                            unfocusedBorderColor = Color(0xFFEEEEEE)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

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
                            unfocusedBorderColor = Color(0xFFEEEEEE)
                        ),
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = null)
                            }
                        },
                        supportingText = {
                            if (password.isNotEmpty() && password.length < 6) {
                                Text("Minimal 6 karakter", color = Color.Red, fontSize = 11.sp)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Input Konfirmasi Password
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Konfirmasi Password") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = Color(0xFFEEEEEE)
                        ),
                        trailingIcon = {
                            val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(imageVector = image, contentDescription = null)
                            }
                        },
                        supportingText = {
                            if (confirmPassword.isNotEmpty() && password != confirmPassword) {
                                Text("Password tidak cocok", color = Color.Red, fontSize = 11.sp)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Button Daftar
                    // Button Daftar
                    Button(
                        onClick = {
                            val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$"

                            if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
                                errorMessage = "Semua data wajib diisi!"
                                showErrorDialog = true
                            } else if (!email.matches(emailPattern.toRegex())) {
                                errorMessage = "Format email salah!"
                                showErrorDialog = true
                            } else if (password.length < 6) {
                                errorMessage = "Password minimal 6 karakter!"
                                showErrorDialog = true
                            } else if (password != confirmPassword) {
                                errorMessage = "Password tidak cocok!"
                                showErrorDialog = true
                            } else {
                                viewModel.register(fullName, email, password)
                            }
                        },
                        // ... sisa properti button tetap sama ...
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = CyanPrimary),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                        shape = RoundedCornerShape(16.dp),
                        enabled = registerState !is LoginState.Loading
                    ) {
                        if (registerState is LoginState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Daftar Sekarang", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. NAVIGATION TO LOGIN
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 40.dp)
            ) {
                Text("Sudah punya akun? ", color = Color.Gray)
                Text(
                    text = "Masuk di sini",
                    color = CyanPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }

        }
        if (showSuccessDialog) {
            androidx.compose.ui.window.Dialog(onDismissRequest = {
                showSuccessDialog = false
                onNavigateToLogin() // Pindah ke Login
            }) {
                Surface(
                    modifier = Modifier.fillMaxWidth(0.85f).wrapContentHeight(),
                    shape = RoundedCornerShape(28.dp),
                    color = Color.White
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.size(72.dp).background(Color(0xFFE0F7FA), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.CheckCircle, null, tint = CyanPrimary, modifier = Modifier.size(44.dp))
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Text("Berhasil!", fontSize = 20.sp, fontWeight = FontWeight.Black)
                        Text("Akun kamu sudah aktif.", fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(28.dp))
                        Button(
                            onClick = {
                                showSuccessDialog = false
                                onNavigateToLogin() // Pindah ke Login
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Masuk Sekarang", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
        // --- POP-UP ERROR (DI TENGAH) ---
        if (showErrorDialog) {
            androidx.compose.ui.window.Dialog(onDismissRequest = { showErrorDialog = false }) {
                Surface(
                    modifier = Modifier.fillMaxWidth(0.75f).wrapContentHeight(),
                    shape = RoundedCornerShape(28.dp),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Ikon Peringatan Merah
                        Box(
                            modifier = Modifier.size(60.dp).background(Color(0xFFFFEBEE), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Warning, null, tint = Color(0xFFFF5252), modifier = Modifier.size(32.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Oops!", fontSize = 18.sp, fontWeight = FontWeight.Black)
                        Text(errorMessage, fontSize = 14.sp, color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { showErrorDialog = false },
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Perbaiki", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}