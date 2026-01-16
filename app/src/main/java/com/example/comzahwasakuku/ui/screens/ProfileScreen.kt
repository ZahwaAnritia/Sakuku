package com.example.comzahwasakuku.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.comzahwasakuku.ui.viewmodel.AuthViewModel
import com.example.comzahwasakuku.ui.theme.CyanPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: AuthViewModel,
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onCategoryClick: () -> Unit
) {

    val userName by viewModel.userName.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()
    val savedLimit by viewModel.currentLimit.collectAsState()
    val savedPrice by viewModel.currentIndomiePrice.collectAsState()

    var inputLimit by remember { mutableStateOf("") }
    var inputPrice by remember { mutableStateOf("") }

    LaunchedEffect(savedLimit, savedPrice) {
        if (inputLimit.isEmpty()) inputLimit = savedLimit
        if (inputPrice.isEmpty()) inputPrice = savedPrice
    }

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showEditSuccessDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()


    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFB))) {


        Box(
            modifier = Modifier.fillMaxWidth().height(300.dp)
                .background(Brush.verticalGradient(listOf(Color(0xFF00BCD4), Color(0xFF0097A7))))
        )

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
        ) {

            Text(
                text = "Profil Saya",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(top = 44.dp, start = 20.dp, bottom = 16.dp)
            )


            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(110.dp).shadow(15.dp, CircleShape).clip(CircleShape).background(Color.White).padding(4.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(Color(0xFFE0F7FA)), contentAlignment = Alignment.Center) {
                        Text("🎓", fontSize = 54.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = userName, fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.White)
                Text(text = userEmail, fontSize = 14.sp, color = Color.White.copy(alpha = 0.9f))
            }


            Column(
                modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth().clickable { onCategoryClick() }.shadow(8.dp, RoundedCornerShape(24.dp))
                ) {
                    Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(48.dp).background(Color(0xFFE0F7FA), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                            Icon(Icons.AutoMirrored.Filled.List, null, tint = CyanPrimary)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Kelola Kategori", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Atur kategori pemasukan & pengeluaran", fontSize = 12.sp, color = Color.Gray)
                        }
                        Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
                    }
                }


                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(24.dp))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Pengaturan Keuangan", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedTextField(
                            value = inputLimit,
                            onValueChange = { if (it.all { c -> c.isDigit() }) inputLimit = it },
                            label = { Text("Limit Bulanan") },
                            prefix = { Text("Rp ", fontWeight = FontWeight.Bold, color = CyanPrimary) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = inputPrice,
                            onValueChange = { if (it.all { c -> c.isDigit() }) inputPrice = it },
                            label = { Text("Harga Indomie") },
                            prefix = { Text("Rp ", fontWeight = FontWeight.Bold, color = CyanPrimary) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                viewModel.updateProfile(inputLimit, inputPrice)
                                showEditSuccessDialog = true
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Simpan Perubahan", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                // Tombol Logout
                Surface(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFFFEBEE))
                ) {
                    Row(modifier = Modifier.padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        Icon(Icons.Default.Logout, null, tint = Color(0xFFFF5252), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Logout Akun", fontWeight = FontWeight.Bold, color = Color(0xFFFF5252))
                    }
                }
            }
        }


        if (showEditSuccessDialog) {
            androidx.compose.ui.window.Dialog(onDismissRequest = { showEditSuccessDialog = false }) {
                Surface(modifier = Modifier.fillMaxWidth(0.85f).wrapContentHeight(), shape = RoundedCornerShape(28.dp), color = Color.White) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(modifier = Modifier.size(72.dp).background(Color(0xFFE8F5E9), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(44.dp))
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(text = "Berhasil!", fontSize = 20.sp, fontWeight = FontWeight.Black)
                        Text(text = "Profil kamu sudah diperbarui.", fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(28.dp))
                        Button(onClick = { showEditSuccessDialog = false }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary), shape = RoundedCornerShape(14.dp)) {
                            Text("Siap!", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }


        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                containerColor = Color.White,
                title = { Text("Logout?", fontWeight = FontWeight.Bold) },
                text = { Text("Yakin ingin keluar?") },
                confirmButton = {
                    TextButton(onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                        onLogoutClick()
                    }) { Text("Keluar", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold) }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) { Text("Batal", color = Color.Gray) }
                }
            )
        }
    }
}