package com.example.ui.dialogs

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DebtItem
import com.example.data.model.DebtType
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardSubtle
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.FinanceViewModel
import com.example.util.IndonesianInstitutionHelper
import com.example.util.ThousandsSeparatorVisualTransformation
import java.util.Calendar

data class DebtCategoryPreset(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val bgColor: Color,
    val iconColor: Color
)

@Composable
fun CreateDebtDialog(
    existingDebt: DebtItem? = null,
    viewModel: FinanceViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val wallets by viewModel.wallets.collectAsState()

    var debtType by remember { mutableStateOf(existingDebt?.type ?: DebtType.BORROWED) }
    var selectedCategoryIcon by remember { mutableStateOf(existingDebt?.categoryIcon ?: "CreditCard") }
    var selectedInstitutionLogo by remember { mutableStateOf(existingDebt?.institutionLogo ?: "") }
    var showLogoPickerDialog by remember { mutableStateOf(false) }

    var debtTitle by remember { mutableStateOf(existingDebt?.name ?: "") }
    var totalAmountText by remember { mutableStateOf(if (existingDebt != null && existingDebt.totalAmount > 0) existingDebt.totalAmount.toLong().toString() else "") }
    var dueDateText by remember { mutableStateOf(existingDebt?.dueDate ?: "02 Dec 2026") }
    var feesText by remember { mutableStateOf(if (existingDebt != null && existingDebt.extraFees > 0) existingDebt.extraFees.toLong().toString() else "") }

    var tenureMonthsText by remember { mutableStateOf(if (existingDebt != null && existingDebt.tenureMonths > 0) existingDebt.tenureMonths.toString() else "") }
    var firstDueText by remember { mutableStateOf(existingDebt?.startDate ?: "-") }
    var monthlyPaymentText by remember { mutableStateOf(if (existingDebt != null && existingDebt.monthlyPayment > 0) existingDebt.monthlyPayment.toLong().toString() else "") }
    var interestRateText by remember { mutableStateOf(if (existingDebt != null && existingDebt.interestRate > 0) existingDebt.interestRate.toString() else "") }

    var selectedWalletName by remember { mutableStateOf(existingDebt?.linkedWalletName ?: "") }
    var showMoreOptions by remember { mutableStateOf(false) }
    var notesText by remember { mutableStateOf("") }

    val categoryPresets = listOf(
        DebtCategoryPreset("House", "Rumah", Icons.Default.Home, Color(0xFF1E3A8A), Color(0xFF60A5FA)),
        DebtCategoryPreset("Building", "Gedung", Icons.Default.Apartment, Color(0xFF4C1D95), Color(0xFFA78BFA)),
        DebtCategoryPreset("Car", "Mobil", Icons.Default.DirectionsCar, Color(0xFF065F46), Color(0xFF34D399)),
        DebtCategoryPreset("CreditCard", "Kartu Kredit", Icons.Default.CreditCard, Color(0xFF0E7490), Color(0xFF38BDF8)),
        DebtCategoryPreset("Bank", "Bank", Icons.Default.AccountBalance, Color(0xFF1E40AF), Color(0xFF93C5FD)),
        DebtCategoryPreset("Motor", "Motor", Icons.Default.TwoWheeler, Color(0xFF9A3412), Color(0xFFFB923C)),
        DebtCategoryPreset("Tools", "Perkakas", Icons.Default.Build, Color(0xFF831843), Color(0xFFF472B6)),
        DebtCategoryPreset("Cash", "Uang Tunai", Icons.Default.Payments, Color(0xFF134E4A), Color(0xFF2DD4BF))
    )

    fun calculateMonthlyPayment(months: Int) {
        val total = totalAmountText.toDoubleOrNull() ?: 0.0
        val fees = feesText.toDoubleOrNull() ?: 0.0
        val sum = total + fees
        if (months > 0 && sum > 0) {
            val monthly = (sum / months).toLong()
            monthlyPaymentText = monthly.toString()
        }
    }

    val canSubmit = debtTitle.isNotBlank() && (totalAmountText.toDoubleOrNull() ?: 0.0) > 0
    val performSubmit = {
        val totalAmt = totalAmountText.toDoubleOrNull() ?: 0.0
        val fees = feesText.toDoubleOrNull() ?: 0.0
        val monthly = monthlyPaymentText.toDoubleOrNull() ?: 0.0
        val interest = interestRateText.toDoubleOrNull() ?: 0.0
        val months = tenureMonthsText.toIntOrNull() ?: 0

        if (debtTitle.isNotBlank() && totalAmt > 0) {
            val item = DebtItem(
                id = existingDebt?.id ?: java.util.UUID.randomUUID().toString(),
                name = debtTitle,
                type = debtType,
                totalAmount = totalAmt + fees,
                paidAmount = existingDebt?.paidAmount ?: 0.0,
                dueDate = dueDateText,
                startDate = if (firstDueText != "-") firstDueText else "28 Mar 2026",
                monthlyPayment = monthly,
                interestRate = interest,
                extraFees = fees,
                categoryIcon = selectedCategoryIcon,
                institutionLogo = selectedInstitutionLogo,
                tenureMonths = months,
                linkedWalletName = selectedWalletName,
                records = existingDebt?.records ?: emptyList()
            )

            if (existingDebt == null) {
                viewModel.addDebt(item, depositToWalletName = selectedWalletName.ifBlank { null })
            } else {
                viewModel.updateDebt(item)
            }
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .statusBarsPadding()
                .imePadding(),
            containerColor = DarkBackground,
            topBar = {
                // Top App Bar with high-positioned Buat button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("btn_close_create_debt")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = TextWhite,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (existingDebt == null) "Buat Utang" else "Edit Utang",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }

                    // Button Buat right at the top
                    Button(
                        onClick = performSubmit,
                        enabled = canSubmit,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentBlue,
                            disabledContainerColor = AccentBlue.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier
                            .height(38.dp)
                            .testTag("btn_top_submit_create_debt")
                    ) {
                        Text(
                            text = if (existingDebt == null) "Buat" else "Simpan",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            },
            bottomBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = DarkBackground
                ) {
                    Button(
                        onClick = performSubmit,
                        enabled = canSubmit,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentBlue,
                            disabledContainerColor = AccentBlue.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .height(50.dp)
                            .testTag("btn_submit_create_debt")
                    ) {
                        Text(
                            text = if (existingDebt == null) "Buat" else "Simpan Perubahan",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Jenis matching Gambar 3 (Dipinjam / Dipinjamkan)
                item {
                    val isBorrowed = debtType == DebtType.BORROWED
                    Text(
                        text = "Jenis",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSubtle
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Dipinjam (Uang Masuk)
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isBorrowed) Color(0xFF10B981).copy(alpha = 0.2f) else DarkSurfaceElevated,
                            border = BorderStroke(1.dp, if (isBorrowed) Color(0xFF10B981) else DarkCardBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { debtType = DebtType.BORROWED }
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDownward,
                                        contentDescription = null,
                                        tint = if (isBorrowed) Color(0xFF34D399) else TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Dipinjam",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isBorrowed) Color.White else TextMuted
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Uang Masuk ke Akun",
                                    fontSize = 10.sp,
                                    color = if (isBorrowed) Color(0xFF34D399) else TextSubtle,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Dipinjamkan (Uang Keluar)
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (!isBorrowed) Color(0xFFFF5722).copy(alpha = 0.2f) else DarkSurfaceElevated,
                            border = BorderStroke(1.dp, if (!isBorrowed) Color(0xFFFF5722) else DarkCardBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { debtType = DebtType.LENT }
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowUpward,
                                        contentDescription = null,
                                        tint = if (!isBorrowed) Color(0xFFFF7A50) else TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Dipinjamkan",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (!isBorrowed) Color.White else TextMuted
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Uang Keluar dari Akun",
                                    fontSize = 10.sp,
                                    color = if (!isBorrowed) Color(0xFFFF7A50) else TextSubtle,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isBorrowed) {
                            "💡 Utang (Dipinjam): Dana pinjaman dicatat sebagai uang masuk ke akun/dompet Anda dan menjadi kewajiban yang harus dibayar."
                        } else {
                            "💡 Piutang (Dipinjamkan): Dana pinjaman dicatat sebagai uang keluar dari akun/dompet Anda dan menjadi hak yang harus ditagih."
                        },
                        fontSize = 11.sp,
                        color = TextMuted,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                // 2. Kategori matching Gambar 3
                item {
                    Text(
                        text = "Kategori",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSubtle
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // 4x2 Category Grid
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            categoryPresets.take(4).forEach { cat ->
                                val isSelected = selectedCategoryIcon == cat.id
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = cat.bgColor,
                                    border = if (isSelected) BorderStroke(2.dp, Color.White) else null,
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { selectedCategoryIcon = cat.id }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = cat.icon,
                                            contentDescription = cat.name,
                                            tint = cat.iconColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            categoryPresets.drop(4).take(4).forEach { cat ->
                                val isSelected = selectedCategoryIcon == cat.id
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = cat.bgColor,
                                    border = if (isSelected) BorderStroke(2.dp, Color.White) else null,
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { selectedCategoryIcon = cat.id }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = cat.icon,
                                            contentDescription = cat.name,
                                            tint = cat.iconColor,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Button: Pilih dari daftar logo matching Gambar 3
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceElevated,
                        border = BorderStroke(1.dp, DarkCardBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLogoPickerDialog = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (selectedInstitutionLogo.isNotBlank()) "Logo: $selectedInstitutionLogo (Ganti)" else "Pilih dari daftar logo",
                                fontSize = 13.sp,
                                color = if (selectedInstitutionLogo.isNotBlank()) AccentBlue else TextMuted
                            )
                        }
                    }
                }

                // 3. Judul Utang matching Gambar 3
                item {
                    Text(
                        text = "Judul Utang",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSubtle
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = debtTitle,
                        onValueChange = { debtTitle = it },
                        placeholder = { Text("Judul Utang", color = TextMuted, fontSize = 14.sp) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = DarkSurfaceElevated,
                            unfocusedContainerColor = DarkSurfaceElevated
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_debt_title")
                    )
                }

                // 4. Mata Uang & Jumlah Total matching Gambar 3
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(modifier = Modifier.weight(0.9f)) {
                            Text(
                                text = "Mata Uang",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSubtle
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = DarkSurfaceElevated,
                                border = BorderStroke(1.dp, DarkCardBorder),
                                modifier = Modifier.height(56.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🇮🇩", fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("IDR", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                    }
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1.8f)) {
                            Text(
                                text = "Jumlah Total",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSubtle
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = DarkSurfaceElevated,
                                border = BorderStroke(1.dp, DarkCardBorder),
                                modifier = Modifier.height(56.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Rp", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    OutlinedTextField(
                                        value = totalAmountText,
                                        onValueChange = {
                                            totalAmountText = it.filter { ch -> ch.isDigit() }
                                            tenureMonthsText.toIntOrNull()?.let { m -> calculateMonthlyPayment(m) }
                                        },
                                        placeholder = { Text("Jumlah Total", color = TextMuted, fontSize = 14.sp) },
                                        visualTransformation = ThousandsSeparatorVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color.Transparent,
                                            unfocusedBorderColor = Color.Transparent,
                                            focusedTextColor = TextWhite,
                                            unfocusedTextColor = TextWhite
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("input_debt_total_amount")
                                    )
                                }
                            }
                        }
                    }
                }

                // 5. Tanggal Jatuh Tempo matching Gambar 3
                item {
                    Text(
                        text = "Tanggal Jatuh Tempo",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSubtle
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceElevated,
                        border = BorderStroke(1.dp, DarkCardBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                DatePickerDialog(
                                    context,
                                    { _, year, month, day ->
                                        val months = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")
                                        dueDateText = String.format("%02d %s %d", day, months.getOrElse(month) { "Des" }, year)
                                    },
                                    2026, 11, 2
                                ).show()
                            }
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = dueDateText,
                                fontSize = 14.sp,
                                color = TextWhite
                            )
                        }
                    }
                }

                // Quick Action "Buat" Button right after core inputs
                item {
                    Button(
                        onClick = performSubmit,
                        enabled = canSubmit,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentBlue,
                            disabledContainerColor = AccentBlue.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .height(48.dp)
                            .testTag("btn_quick_submit_create_debt")
                    ) {
                        Text(
                            text = if (existingDebt == null) "Buat Utang Sekarang" else "Simpan Perubahan",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // 6. Bunga & biaya (opsional) matching Gambar 3
                item {
                    Text(
                        text = "Bunga & biaya (opsional)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSubtle
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceElevated,
                        border = BorderStroke(1.dp, DarkCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Rp", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = feesText,
                                onValueChange = {
                                    feesText = it.filter { ch -> ch.isDigit() }
                                    tenureMonthsText.toIntOrNull()?.let { m -> calculateMonthlyPayment(m) }
                                },
                                placeholder = { Text("Bunga & biaya", color = TextMuted, fontSize = 14.sp) },
                                visualTransformation = ThousandsSeparatorVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ditambahkan ke jumlah yang Anda utang. Uang yang Anda terima tetap sebesar jumlah di atas.",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                // 7. Pembayaran Kembali (opsional) matching Gambar 3
                item {
                    Text(
                        text = "Pembayaran Kembali (opsional)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSubtle
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Rencana pembayaran dengan tanggal jatuh tempo, atau jumlah pembayaran bulanan Anda — keduanya penting untuk keuangan Anda.",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Pengaturan cepat — ketuk untuk mengisi rencana",
                        fontSize = 11.sp,
                        color = TextSubtle
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick Chips row matching Gambar 3: Mobil · 60 bln, Motor · 36 bln, HP & gadget · 12 bln, Rumah · 240 bln
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "Mobil · 60 bln" to 60,
                            "Motor · 36 bln" to 36,
                            "HP & gadget · 12 bln" to 12,
                            "Rumah · 240 bln" to 240
                        ).forEach { (label, months) ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = DarkSurfaceElevated,
                                border = BorderStroke(1.dp, DarkCardBorder),
                                modifier = Modifier.clickable {
                                    tenureMonthsText = months.toString()
                                    calculateMonthlyPayment(months)
                                }
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Side-by-side inputs: Jumlah bulan & Jatuh tempo pertama matching Gambar 3
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Jumlah bulan
                        OutlinedTextField(
                            value = tenureMonthsText,
                            onValueChange = {
                                tenureMonthsText = it.filter { ch -> ch.isDigit() }
                                it.toIntOrNull()?.let { m -> calculateMonthlyPayment(m) }
                            },
                            placeholder = { Text("Jumlah bulan", color = TextMuted, fontSize = 13.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentBlue,
                                unfocusedBorderColor = DarkCardBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedContainerColor = DarkSurfaceElevated,
                                unfocusedContainerColor = DarkSurfaceElevated
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        // Jatuh tempo pertama
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurfaceElevated,
                            border = BorderStroke(1.dp, DarkCardBorder),
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .clickable {
                                    DatePickerDialog(
                                        context,
                                        { _, year, month, day ->
                                            val months = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")
                                            firstDueText = "$day ${months.getOrElse(month) { "Nov" }} $year"
                                        },
                                        2026, 10, 2
                                    ).show()
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(verticalArrangement = Arrangement.Center) {
                                    Text("Jatuh tempo pertama", fontSize = 10.sp, color = TextMuted)
                                    Text(firstDueText, fontSize = 12.sp, color = TextWhite)
                                }
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Pembayaran Bulanan matching Gambar 3
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceElevated,
                        border = BorderStroke(1.dp, DarkCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Rp", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = monthlyPaymentText,
                                onValueChange = { monthlyPaymentText = it.filter { ch -> ch.isDigit() } },
                                placeholder = { Text("Pembayaran Bulanan", color = TextMuted, fontSize = 14.sp) },
                                visualTransformation = ThousandsSeparatorVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Apa yang Anda bayar setiap bulan, termasuk bunga. Ini dihitung dalam dana cadangan Anda dari hari ini.",
                        fontSize = 11.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Interest rate (optional) % a year matching Gambar 3
                    OutlinedTextField(
                        value = interestRateText,
                        onValueChange = { interestRateText = it },
                        placeholder = { Text("Interest rate (optional)", color = TextMuted, fontSize = 13.sp) },
                        trailingIcon = { Text("% a year", fontSize = 12.sp, color = TextMuted, modifier = Modifier.padding(end = 12.dp)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = DarkSurfaceElevated,
                            unfocusedContainerColor = DarkSurfaceElevated
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "On a card or loan statement it is the APR. Leave it empty if there is no interest. Used by the payoff plan.",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                // 8. Masuk ke akun matching Gambar 3
                item {
                    Text(
                        text = if (debtType == DebtType.BORROWED) "Masuk ke akun (Dompet penerima)" else "Sumber dana (Dompet asal pinjaman)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSubtle
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (debtType == DebtType.BORROWED) {
                            "Opsional — saldo dompet akan bertambah saat pinjaman diterima. Biarkan kosong jika tanpa mengubah saldo."
                        } else {
                            "Opsional — saldo dompet akan berkurang karena meminjamkan dana. Biarkan kosong jika tanpa mengubah saldo."
                        },
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        wallets.forEach { w ->
                            val isSelected = selectedWalletName.equals(w.name, ignoreCase = true)
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) AccentBlue.copy(alpha = 0.2f) else DarkSurfaceElevated,
                                border = BorderStroke(1.dp, if (isSelected) AccentBlue else DarkCardBorder),
                                modifier = Modifier.clickable {
                                    selectedWalletName = if (isSelected) "" else w.name
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = AccentBlue
                                    ) {
                                        Text(
                                            text = getBankBadge(w.name),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = w.name,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) TextWhite else TextSubtle
                                    )
                                }
                            }
                        }
                    }
                }

                // 9. Opsi lainnya accordion matching Gambar 3
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Transparent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showMoreOptions = !showMoreOptions }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = if (showMoreOptions) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Opsi lainnya",
                                fontSize = 13.sp,
                                color = TextMuted,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    AnimatedVisibility(visible = showMoreOptions) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            OutlinedTextField(
                                value = notesText,
                                onValueChange = { notesText = it },
                                placeholder = { Text("Tambahkan catatan atau nomor kontrak...", color = TextMuted, fontSize = 13.sp) },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AccentBlue,
                                    unfocusedBorderColor = DarkCardBorder,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    focusedContainerColor = DarkSurfaceElevated,
                                    unfocusedContainerColor = DarkSurfaceElevated
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    // Logo picker dialog
    if (showLogoPickerDialog) {
        AlertDialog(
            onDismissRequest = { showLogoPickerDialog = false },
            containerColor = DarkSurface,
            title = {
                Text(
                    text = "Pilih Logo Lembaga / Bank",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(IndonesianInstitutionHelper.institutions) { inst ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DarkSurfaceElevated,
                            border = BorderStroke(1.dp, DarkCardBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedInstitutionLogo = inst.shortName
                                    debtTitle = if (debtTitle.isBlank()) "Pinjaman ${inst.name}" else debtTitle
                                    showLogoPickerDialog = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = inst.brandColor,
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = inst.shortName.take(3),
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = inst.name,
                                    fontSize = 13.sp,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showLogoPickerDialog = false }) {
                    Text("Tutup", color = TextMuted)
                }
            }
        )
    }
}
