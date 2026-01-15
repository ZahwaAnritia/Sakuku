package com.example.comzahwasakuku.ui.screens

import android.app.Activity
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import com.example.comzahwasakuku.data.local.entity.TransactionEntity
import com.example.comzahwasakuku.ui.theme.CyanPrimary
import com.example.comzahwasakuku.ui.viewmodel.AuthViewModel
import com.example.comzahwasakuku.ui.viewmodel.LaporanViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaporanScreen(
    navController: NavController,
    viewModel: LaporanViewModel,
    authViewModel: AuthViewModel
) {
    // 1. Ambil Data
    val userId by authViewModel.userId.collectAsState()
    val transactionList by viewModel.getTransactions(userId).collectAsState(initial = emptyList())

    var selectedFilter by remember { mutableStateOf("Semua") }
    val filters = listOf("Mingguan", "Bulanan", "Semua")

    // PERBAIKAN LOGIKA URUTAN:
    // Menggunakan sortedWith agar jika tanggal sama, item dengan transId lebih besar (input terbaru) ada di atas.
    val filteredList = viewModel.filterTransactions(transactionList, selectedFilter)
        .sortedWith(compareByDescending<TransactionEntity> { it.date }.thenByDescending { it.id })

    val expenseList = filteredList.filter { it.type == "OUT" }
    val totalPengeluaran = expenseList.sumOf { it.amount }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<TransactionEntity?>(null) }

    val chartData = expenseList
        .groupBy { it.category }
        .mapValues { entry -> entry.value.sumOf { it.amount } }

    val chartColors = listOf(
        Color(0xFF00BCD4), Color(0xFFFF7043), Color(0xFF5C6BC0),
        Color(0xFF66BB6A), Color(0xFFAB47BC), Color(0xFFFFA726),
        Color(0xFF26A69A), Color(0xFFEC407A)
    )

    fun formatRupiah(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        format.maximumFractionDigits = 0
        return format.format(amount).replace("Rp", "Rp ")
    }

    val formatDate: (Long) -> String = { timestamp ->
        SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(Date(timestamp))
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color(0xFF00ACC1).toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    if (showDeleteDialog && itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = Color.White,
            title = { Text("Hapus Transaksi?", fontWeight = FontWeight.Bold) },
            text = { Text("Data '${itemToDelete?.category}' akan dihapus permanen.") },
            confirmButton = {
                Button(
                    onClick = {
                        itemToDelete?.let { viewModel.deleteTransaction(it) }
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                ) { Text("Hapus", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal", color = Color.Gray) }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFB))) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF00BCD4), Color(0xFF0097A7))
                    )
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {
            // --- HEADER: Tanpa Tombol Kembali ---
            Row(
                modifier = Modifier
                    .padding(top = 44.dp, bottom = 16.dp, start = 20.dp, end = 20.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Laporan & Riwayat",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Surface(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
            ) {
                Row(modifier = Modifier.padding(4.dp)) {
                    filters.forEach { filter ->
                        val isSelected = selectedFilter == filter
                        val chipBg by animateColorAsState(if (isSelected) Color.White else Color.Transparent)
                        val chipText by animateColorAsState(if (isSelected) CyanPrimary else Color.White)

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(20.dp))
                                .background(chipBg)
                                .clickable { selectedFilter = filter },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(filter, color = chipText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(12.dp, RoundedCornerShape(28.dp), ambientColor = CyanPrimary, spotColor = CyanPrimary),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Total Pengeluaran", fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                            Text(formatRupiah(totalPengeluaran), color = Color(0xFFFF5252), fontWeight = FontWeight.Black, fontSize = 28.sp)

                            Spacer(modifier = Modifier.height(32.dp))

                            Box(contentAlignment = Alignment.Center) {
                                SimplePieChart(data = chartData, colors = chartColors, radius = 75.dp, stroke = 45f)
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Status", fontSize = 10.sp, color = Color.Gray)
                                    Text("Aman", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color(0xFF4CAF50))
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            if (chartData.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    chartData.keys.forEachIndexed { index, category ->
                                        val amount = chartData[category] ?: 0.0
                                        val percentage = if (totalPengeluaran > 0) (amount / totalPengeluaran * 100).toInt() else 0

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .background(chartColors.getOrElse(index % chartColors.size) { Color.Gray }, CircleShape)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(category, modifier = Modifier.weight(1f), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                            Text("$percentage%", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(end = 12.dp))
                                            Text(formatRupiah(amount), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            } else {
                                Text("Belum ada data belanja.", color = Color.Gray, fontSize = 13.sp)
                            }
                        }
                    }
                }

                item {
                    Text("Riwayat Transaksi", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color(0xFF263238), modifier = Modifier.padding(top = 8.dp))
                }

                if (filteredList.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.History, null, tint = Color.LightGray, modifier = Modifier.size(40.dp))
                                Text("Kosong nih...", color = Color.Gray, fontSize = 13.sp)
                            }
                        }
                    }
                }

                items(filteredList) { item ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F0F0))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(46.dp).background(if (item.type == "IN") Color(0xFFE8F5E9) else Color(0xFFFFEBEE), CircleShape), contentAlignment = Alignment.Center) {
                                Text(item.category.take(1).uppercase(), fontWeight = FontWeight.Black, color = if (item.type == "IN") Color(0xFF4CAF50) else Color(0xFFFF5252), fontSize = 18.sp)
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.category, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF263238))
                                Text(formatDate(item.date), fontSize = 11.sp, color = Color.Gray)
                                if (item.note.isNotEmpty()) {
                                    Text(item.note, fontSize = 12.sp, color = Color.Gray.copy(alpha = 0.7f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text((if (item.type == "OUT") "- " else "+ ") + formatRupiah(item.amount), color = if (item.type == "OUT") Color(0xFFFF5252) else Color(0xFF4CAF50), fontWeight = FontWeight.Black, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Icon(Icons.Default.Delete, "Hapus", tint = Color.LightGray, modifier = Modifier.size(18.dp).clickable { itemToDelete = item; showDeleteDialog = true })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimplePieChart(data: Map<String, Double>, colors: List<Color>, radius: Dp, stroke: Float) {
    val total = data.values.sum()
    Canvas(modifier = Modifier.size(radius * 2)) {
        if (total == 0.0) {
            drawCircle(color = Color(0xFFF0F0F0), style = Stroke(width = stroke))
        } else {
            var startAngle = -90f
            data.entries.forEachIndexed { index, entry ->
                val sweepAngle = (entry.value / total * 360).toFloat()
                drawArc(
                    color = colors.getOrElse(index % colors.size) { Color.Gray },
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
                startAngle += sweepAngle
            }
        }
    }
}