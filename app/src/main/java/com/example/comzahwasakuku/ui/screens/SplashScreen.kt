package com.example.comzahwasakuku.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
// Import warna tema
import com.example.comzahwasakuku.ui.theme.CyanPrimary

@Composable
fun SplashScreen(onTimeout: () -> Unit) {

    // Timer 2 detik sebelum pindah (Logika tetap sama)
    LaunchedEffect(true) {
        delay(2000)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                // Menggunakan Gradasi yang sama dengan Dashboard agar transisi mulus
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF00BCD4), Color(0xFF0097A7))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // --- BAGIAN LOGO PREMIUM ---
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .shadow(20.dp, CircleShape) // Tambah bayangan agar logo "pop out"
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                // Ikon Dompet dengan warna CyanPrimary
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = "Logo Sakuku",
                    tint = Color(0xFF0097A7),
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- NAMA APLIKASI ---
            Text(
                text = "SAKUKU",
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Black, // Lebih tebal agar terlihat bold/premium
                letterSpacing = 4.sp // Jarak antar huruf diperlebar
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Cerdas Kelola Keuangan Mahasiswa",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // --- FOOTER ---
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Smart Financial Assistant",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "v1.0.0 by Zahwa",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 11.sp
            )
        }
    }
}