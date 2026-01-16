package com.example.comzahwasakuku.ui.screens

import android.app.Activity
import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import com.example.comzahwasakuku.ui.theme.CyanPrimary
import com.example.comzahwasakuku.ui.viewmodel.AuthViewModel
import com.example.comzahwasakuku.ui.viewmodel.CategoryViewModel
import com.example.comzahwasakuku.ui.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel,
    categoryViewModel: CategoryViewModel,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val userId by authViewModel.userId.collectAsState()

    LaunchedEffect(userId) {
        if (userId != -1) {
            categoryViewModel.setUserId(userId)
        }
    }


    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("OUT") }
    var selectedDateText by remember { mutableStateOf("") }
    var selectedCategoryName by remember { mutableStateOf("") }
    var expandedCategory by remember { mutableStateOf(false) }


    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val categoryState by categoryViewModel.categories.collectAsState()
    val isLoading by transactionViewModel.isLoading.collectAsState()


    LaunchedEffect(Unit) {
        val c = Calendar.getInstance()
        val y = c.get(Calendar.YEAR)
        val m = (c.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
        val d = c.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
        selectedDateText = "$y-$m-$d"
    }

    val filteredCategories = categoryState.filter { it.type == selectedType }


    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color(0xFF00ACC1).toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFB))) {

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

        Column(modifier = Modifier.fillMaxSize()) {

            Row(
                modifier = Modifier
                    .padding(start = 8.dp, top = 44.dp, end = 16.dp, bottom = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                }
                Text(
                    "Tambah Transaksi",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }


            Surface(
                modifier = Modifier
                    .padding(start = 20.dp, top = 0.dp, end = 20.dp, bottom = 0.dp)
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                color = Color.White.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
            ) {
                Row(modifier = Modifier.padding(all = 4.dp)) {
                    val types = listOf("OUT" to "Pengeluaran", "IN" to "Pemasukan")
                    types.forEach { (type, label) ->
                        val isSelected = selectedType == type
                        val chipBg by animateColorAsState(if (isSelected) Color.White else Color.Transparent)
                        val chipText by animateColorAsState(
                            if (isSelected) (if (type == "OUT") Color(0xFFFF5252) else Color(0xFF4CAF50))
                            else Color.White
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(22.dp))
                                .background(chipBg)
                                .clickable {
                                    selectedType = type
                                    selectedCategoryName = ""
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                label,
                                color = chipText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))


            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 0.dp, end = 20.dp, bottom = 0.dp)
                    .shadow(15.dp, RoundedCornerShape(28.dp), ambientColor = CyanPrimary, spotColor = CyanPrimary),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(all = 24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        "Detail Keuangan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF263238)
                    )

                    Spacer(modifier = Modifier.height(20.dp))


                    OutlinedTextField(
                        value = amount,
                        onValueChange = { if (it.all { c -> c.isDigit() }) amount = it },
                        label = { Text("Nominal Transaksi") },
                        prefix = { Text("Rp ", fontWeight = FontWeight.Bold, color = CyanPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = Color(0xFFEEEEEE)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))


                    ExposedDropdownMenuBox(
                        expanded = expandedCategory,
                        onExpandedChange = { expandedCategory = !expandedCategory }
                    ) {
                        OutlinedTextField(
                            value = if (selectedCategoryName.isEmpty()) "Pilih Kategori" else selectedCategoryName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Kategori") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanPrimary,
                                unfocusedBorderColor = Color(0xFFEEEEEE)
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCategory,
                            onDismissRequest = { expandedCategory = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            if (filteredCategories.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("Belum ada kategori", color = Color.Gray) },
                                    onClick = {}
                                )
                            } else {
                                filteredCategories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat.name, fontWeight = FontWeight.Medium) },
                                        onClick = {
                                            selectedCategoryName = cat.name
                                            expandedCategory = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))


                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedDateText,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tanggal") },
                            trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = CyanPrimary) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanPrimary,
                                unfocusedBorderColor = Color(0xFFEEEEEE)
                            )
                        )
                        Box(modifier = Modifier.matchParentSize().clickable {
                            val c = Calendar.getInstance()
                            DatePickerDialog(context, { _, y, m, d ->
                                val mStr = (m + 1).toString().padStart(2, '0')
                                val dStr = d.toString().padStart(2, '0')
                                selectedDateText = "$y-$mStr-$dStr"
                            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
                        })
                    }

                    Spacer(modifier = Modifier.height(16.dp))


                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Catatan (Opsional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = Color(0xFFEEEEEE)
                        )
                    )

                    Spacer(modifier = Modifier.height(32.dp))


                    Button(
                        onClick = {
                            if (amount.isNotEmpty() && selectedCategoryName.isNotEmpty()) {
                                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                val dateLong = try { sdf.parse(selectedDateText)?.time ?: System.currentTimeMillis() } catch (e: Exception) { System.currentTimeMillis() }

                                transactionViewModel.addTransaction(
                                    userId = userId,
                                    amount = amount.toDouble(),
                                    category = selectedCategoryName,
                                    note = note,
                                    type = selectedType,
                                    date = dateLong,
                                    onSuccess = {
                                        showSuccessDialog = true
                                    },
                                    onError = { msg -> Toast.makeText(context, "Gagal: $msg", Toast.LENGTH_SHORT).show() }
                                )
                            } else {
                                errorMessage = "Harap isi nominal dan pilih kategori terlebih dahulu."
                                showErrorDialog = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = CyanPrimary),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("SIMPAN TRANSAKSI", fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 16.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        // --- POP-UP SUKSES TRANSAKSI (ULTRA SLIM & COMPACT) ---
        if (showSuccessDialog) {
            androidx.compose.ui.window.Dialog(onDismissRequest = {
                showSuccessDialog = false
                navController.popBackStack()
            }) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.75f) // Lebih ramping agar tidak terlihat besar
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(32.dp), // Sudut lebih membulat (modern)
                    color = Color.White,
                    shadowElevation = 12.dp
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp), // Padding yang lebih rapat
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // 1. Ikon Centang Kecil & Elegan
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(Color(0xFFE8F5E9), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 2. Judul Singkat
                        Text(
                            text = "Berhasil!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF263238)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // 3. Pesan Padat
                        Text(
                            text = "Transaksi telah dicatat.",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // 4. Tombol Utama Rapi
                        Button(
                            onClick = {
                                showSuccessDialog = false
                                navController.popBackStack()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Sip!", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
        // --- POP-UP ERROR/VALIDATION (DI TENGAH) ---
        if (showErrorDialog) {
            androidx.compose.ui.window.Dialog(onDismissRequest = { showErrorDialog = false }) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(32.dp),
                    color = Color.White,
                    shadowElevation = 12.dp
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Ikon Peringatan Merah
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(Color(0xFFFFEBEE), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFFF5252),
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Belum Lengkap",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF263238)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = errorMessage,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { showErrorDialog = false },
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Perbaiki", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}