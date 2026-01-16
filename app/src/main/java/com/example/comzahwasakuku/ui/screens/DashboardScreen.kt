package com.example.comzahwasakuku.ui.screens

import android.app.Activity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import com.example.comzahwasakuku.ui.theme.CyanPrimary
import com.example.comzahwasakuku.ui.viewmodel.DashboardViewModel
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun DashboardScreen(
    navController: NavController,

    viewModel: DashboardViewModel
) {
    var showNotifInfo by remember { mutableStateOf(false) }

    // --- 1. DATA DARI VIEWMODEL ---
    val userName by viewModel.userName.collectAsState()
    val limitHarian by viewModel.dailyLimit.collectAsState()
    val expenseHariIni by viewModel.todayExpense.collectAsState()
    val sisaBudget by viewModel.remainingBudget.collectAsState()
    val totalExpenseBulanIni by viewModel.totalExpense.collectAsState()
    val totalIncome by viewModel.totalIncome.collectAsState()
    val indomieCount by viewModel.indomieIndex.collectAsState()

    // --- 2. LOGIKA UI ---
    val calendar = Calendar.getInstance()
    val hariIni = calendar.get(Calendar.DAY_OF_MONTH)
    val maxHariBulanIni = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val sisaHari = maxHariBulanIni - hariIni + 1
    val jatahHarianPintar = if (sisaBudget > 0) sisaBudget / sisaHari else 0.0

    val isOverBudget = limitHarian > 0 && totalExpenseBulanIni > limitHarian
    val isBorosHariIni = expenseHariIni > jatahHarianPintar && !isOverBudget

    val context = androidx.compose.ui.platform.LocalContext.current
    val targetProgress = if (limitHarian > 0) (totalExpenseBulanIni / limitHarian).toFloat().coerceIn(0f, 1f) else 0f
    val progressAnimation by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 1000),
        label = "progress"
    )

    fun formatRupiah(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        format.maximumFractionDigits = 0
        return format.format(amount).replace("Rp", "Rp ")
    }

    // Status Bar Style
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color(0xFF00ACC1).toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFB))) {
        // Gradient Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF00BCD4), Color(0xFF0097A7))
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            // --- HEADER ---
            Row(
                modifier = Modifier
                    .padding(top = 44.dp, bottom = 24.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Halo, $userName!", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text("Sakuku Dashboard", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                }
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(44.dp)
                        .clickable {
                            showNotifInfo = true // Memunculkan popup
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifikasi",
                        tint = Color.White,
                        modifier = Modifier.padding(10.dp)


                    )

                }
            }

            // --- 1. SMART WARNING SECTION (Ramping & Modern) ---
            if (isOverBudget) {
                SmartWarning(
                    title = "Terkena Catch-up Utang!",
                    subtitle = "Limit dikurangi untuk menutupi minus kemarin.",
                    color = Color(0xFFFF5252),
                    bgColor = Color(0xFFFFEBEE)
                )
            } else if (isBorosHariIni) {
                SmartWarning(
                    title = "Waduh, Boros Hari Ini!",
                    subtitle = "Kurangi jajan ya biar budget aman sampai akhir bulan!",
                    color = Color(0xFFFFA000),
                    bgColor = Color(0xFFFFF3E0)
                )
            }

            // --- 2. HERO CARD (SISA BUDGET) ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 15.dp, shape = RoundedCornerShape(32.dp), ambientColor = CyanPrimary, spotColor = CyanPrimary),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(28.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Sisa Budget Bulanan", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Medium)

                        val isKritis = jatahHarianPintar < 20000 && jatahHarianPintar > 0
                        Surface(
                            color = if (isKritis) Color(0xFFFFEBEE) else Color(0xFFE0F7FA),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "Jatah: ${formatRupiah(jatahHarianPintar)} /hr",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isKritis) Color(0xFFFF5252) else CyanPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = formatRupiah(sisaBudget),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = if (sisaBudget <= 0) Color(0xFFFF5252) else Color(0xFF263238),
                        letterSpacing = (-1).sp
                    )

                    Text(
                        text = if (sisaBudget <= 0) "Budget bulan ini sudah habis!" else "Berlaku untuk $sisaHari hari ke depan",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Progress Section
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Progres Pemakaian", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text("${(targetProgress * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Black, color = CyanPrimary)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { progressAnimation },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        color = if (isOverBudget) Color(0xFFFF5252) else CyanPrimary,
                        trackColor = Color(0xFFF1F4F7),
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 3. STATS GRID ---
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SmallStatCard(
                    label = "Hari Ini",
                    value = formatRupiah(expenseHariIni),
                    icon = Icons.Default.TrendingDown,
                    color = Color(0xFFFF5252),
                    modifier = Modifier.weight(1f)
                )
                SmallStatCard(
                    label = "Total Masuk",
                    value = formatRupiah(totalIncome),
                    icon = Icons.Default.TrendingUp,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 4. INDOMIE INDEX ---
// --- 4. INDOMIE INDEX (FIXED & CLEAN) ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp) // Beri tinggi tetap agar stabil
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = Color(0xFFFBC02D).copy(alpha = 0.4f),
                        spotColor = Color(0xFFFBC02D).copy(alpha = 0.4f)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White) // Dasar putih agar aman
            ) {
                // Gunakan Box sebagai container utama di dalam card untuk background gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFFFFDE7), // Kuning sangat muda (kiri)
                                    Color(0xFFFFF9C4)  // Kuning soft (kanan)
                                )
                            )
                        )
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Icon Bulat
                        Surface(
                            modifier = Modifier.size(52.dp),
                            shape = CircleShape,
                            color = Color(0xFFFFD600), // Kuning terang khas Indomie
                            shadowElevation = 4.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🍜", fontSize = 28.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Indomie Index",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = Color(0xFF5D4037) // Cokelat tua (Premium look)
                            )
                            Text(
                                text = "Dompetmu setara $indomieCount bungkus mie!",
                                fontSize = 13.sp,
                                color = Color(0xFF795548).copy(alpha = 0.9f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
        if (showNotifInfo) {
            androidx.compose.ui.window.Dialog(onDismissRequest = { showNotifInfo = false }) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.8f) // Lebih ramping (80% lebar layar)
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(28.dp),
                    color = Color.White,
                    shadowElevation = 10.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 1. Ikon Lonceng Biru
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(CyanPrimary.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = CyanPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // 2. Judul Singkat
                        Text(
                            text = "Segera Hadir",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF263238)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // 3. Pesan Singkat (Micro-copy)
                        Text(
                            text = "Fitur pengingat cerdas sedang dikembangkan.",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // 4. Tombol Oke (Lebar)
                        Button(
                            onClick = { showNotifInfo = false },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Oke", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SmartWarning(title: String, subtitle: String, color: Color, bgColor: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
        color = bgColor,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Warning, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(subtitle, color = color.copy(alpha = 0.8f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun SmallStatCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Surface(
        modifier = modifier.shadow(4.dp, RoundedCornerShape(24.dp), ambientColor = Color.Black.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFF0F2F5))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Box(
                modifier = Modifier.size(32.dp).background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(label, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Black, color = color)
        }

    }
}