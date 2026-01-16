package com.example.comzahwasakuku.ui.screens

import android.app.Activity
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.comzahwasakuku.data.local.entity.CategoryEntity
import com.example.comzahwasakuku.ui.theme.CyanPrimary
import com.example.comzahwasakuku.ui.viewmodel.AuthViewModel
import com.example.comzahwasakuku.ui.viewmodel.CategoryViewModel


val categoryIcons = mapOf(
    "Makan" to Icons.Default.Restaurant,
    "Belanja" to Icons.Default.ShoppingCart,
    "Transport" to Icons.Default.DirectionsCar,
    "Tagihan" to Icons.Default.Receipt,
    "Uang" to Icons.Default.AttachMoney,
    "Kampus" to Icons.Default.School,
    "Kesehatan" to Icons.Default.LocalHospital,
    "Hiburan" to Icons.Default.Movie,
    "Lainnya" to Icons.Default.Category
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    viewModel: CategoryViewModel,
    authViewModel: AuthViewModel,
    onBackClick: () -> Unit
) {

    val userId by authViewModel.userId.collectAsState()

    LaunchedEffect(userId) {
        if (userId != -1) {
            viewModel.setUserId(userId)
        }
    }

    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // --- STATE UNTUK POP-UP VALIDASI ---
    var showDeleteDialog by remember { mutableStateOf(false) }
    var categoryToDelete by remember { mutableStateOf<CategoryEntity?>(null) }
    var showAddSuccessDialog by remember { mutableStateOf(false) }

    var newCategoryName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("OUT") }
    var selectedIconName by remember { mutableStateOf("Lainnya") }


    val isInputValid = newCategoryName.isNotBlank()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color(0xFF00ACC1).toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

// POP-UP HAPUS ULTRA SLIM
    if (showDeleteDialog && categoryToDelete != null) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showDeleteDialog = false }) {
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

                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color(0xFFFFEBEE), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = null,
                            tint = Color(0xFFFF5252),
                            modifier = Modifier.size(32.dp) // Ikon lebih proporsional
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. Judul Ramping
                    Text(
                        text = "Hapus Kategori",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF263238)
                    )

                    Spacer(modifier = Modifier.height(6.dp))


                    Text(
                        text = "Hapus '${categoryToDelete?.name}'?",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))


                    Button(
                        onClick = {
                            categoryToDelete?.let { viewModel.deleteCategory(it) }
                            showDeleteDialog = false
                            categoryToDelete = null
                        },
                        modifier = Modifier.fillMaxWidth().height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Ya, Hapus", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    TextButton(
                        onClick = { showDeleteDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Batal", color = Color.LightGray, fontSize = 13.sp)
                    }
                }
            }
        }
    }
    // --- POP-UP SUKSES TAMBAH KATEGORI (MODERN & RAMPING) ---
    if (showAddSuccessDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showAddSuccessDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.75f) // Ramping (75% lebar layar)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(32.dp),
                color = Color.White,
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Ikon Centang Hijau
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color(0xFFE8F5E9), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Berhasil!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF263238)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Kategori baru telah ditambahkan.",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { showAddSuccessDialog = false },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Sip!", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
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
                    .padding(top = 44.dp, bottom = 16.dp, start = 8.dp, end = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                }
                Text(
                    "Kelola Kategori",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Surface(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                color = Color.White.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
            ) {
                Row(modifier = Modifier.padding(4.dp)) {
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
                                .clickable { selectedType = type },
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

            // FORM INPUT
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .shadow(
                        elevation = 15.dp,
                        shape = RoundedCornerShape(28.dp),
                        ambientColor = CyanPrimary.copy(alpha = 0.4f),
                        spotColor = CyanPrimary.copy(alpha = 0.4f)
                    ),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Tambah Kategori Kustom",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF263238)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Pilih Visual Ikon:", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    LazyRow(
                        modifier = Modifier.padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(start = 0.dp, top = 0.dp, end = 12.dp, bottom = 0.dp)
                    ) {
                        items(categoryIcons.toList()) { (name, iconVector) ->
                            val isSelected = selectedIconName == name
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) CyanPrimary else Color(0xFFF5F7F9))
                                    .clickable { selectedIconName = name }
                            ) {
                                Icon(
                                    imageVector = iconVector,
                                    contentDescription = name,
                                    tint = if (isSelected) Color.White else Color.Gray,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = newCategoryName,
                            onValueChange = { newCategoryName = it },
                            placeholder = {
                                Text(
                                    "Nama kategori baru...",
                                    color = Color.LightGray
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanPrimary,
                                unfocusedBorderColor = Color(0xFFEEEEEE)
                            )
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = {
                                if (newCategoryName.isNotEmpty() && userId != -1) {
                                    viewModel.addCategory(
                                        userId = userId,
                                        name = newCategoryName,
                                        type = selectedType,
                                        icon = selectedIconName
                                    )
                                    newCategoryName = ""
                                    selectedIconName = "Lainnya"
                                    showAddSuccessDialog = true
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            // Warna berubah jadi abu-abu kalau belum ngetik
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isInputValid) CyanPrimary else Color.LightGray,
                                disabledContainerColor = Color.LightGray.copy(alpha = 0.5f)
                            ),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(56.dp),
                            // Tombol mati ga bisa diklik kalau belum ngetik
                            enabled = !isLoading && isInputValid
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = if (isInputValid) Color.White else Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            val filteredList = categories.filter { it.type == selectedType }

            Text(
                "Daftar Kategori ${if (selectedType == "OUT") "Pengeluaran" else "Pemasukan"}",
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 28.dp, bottom = 16.dp),
                fontWeight = FontWeight.Black,
                color = Color(0xFF263238),
                fontSize = 18.sp
            )

            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, top = 0.dp, end = 20.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (filteredList.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Category, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Kategori belum tersedia", color = Color.Gray, fontSize = 14.sp)
                            }
                        }
                    }
                } else {
                    items(filteredList) { cat ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F0F0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape(20.dp),
                                    ambientColor = Color.Black.copy(alpha = 0.05f),
                                    spotColor = Color.Black.copy(alpha = 0.05f)
                                )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val iconVector = categoryIcons[cat.icon] ?: Icons.Default.Category
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .background(
                                            if (cat.type == "OUT") Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = iconVector,
                                        contentDescription = null,
                                        tint = if (cat.type == "OUT") Color(0xFFFF5252) else Color(0xFF4CAF50),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Text(
                                    text = cat.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF263238),
                                    modifier = Modifier.weight(1f)
                                )


                                if (cat.userId != 0) {

                                    IconButton(onClick = {
                                        categoryToDelete = cat
                                        showDeleteDialog = true
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Hapus",
                                            tint = Color(0xFFFF5252),
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                } else {
                                    Surface(
                                        color = Color(0xFFF5F7F9),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            "DEFAULT",
                                            fontSize = 10.sp,
                                            color = Color.LightGray,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}