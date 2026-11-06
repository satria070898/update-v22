package com.example.ui.dialogs

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.runtime.mutableFloatStateOf

import androidx.compose.foundation.layout.Box
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.AddPhotoAlternate
import com.example.util.ImageUploadHelper
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.filled.CandlestickChart
import com.example.ui.components.TradingViewChartCard
import com.example.ui.components.StockTickerPickerSheet
import com.example.util.TradingViewHelper
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.ui.window.PopupProperties
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.example.data.model.AssetItem
import com.example.data.model.DebtItem
import com.example.data.model.DebtType
import com.example.data.model.MainCategory
import com.example.data.model.SubcategoryItem
import com.example.data.model.Transaction
import com.example.data.model.TransactionType
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ExpenseRedSoft
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.IncomeGreenSoft
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TextWhite
import com.example.ui.theme.TrophyYellow
import com.example.ui.theme.TrophyYellowLight
import com.example.ui.viewmodel.FinanceViewModel
import com.example.util.CategoryIconHelper
import com.example.util.CurrencyHelper
import com.example.util.ThousandsSeparatorVisualTransformation
import com.example.util.IndonesianInstitutionHelper
import com.example.util.InstitutionLogoBadge
import com.example.util.AssetLogoBadge
import com.example.util.AssetIconHelper
import com.example.util.TransactionMathHelper

fun getCategoryIcon(category: String): ImageVector {
    return CategoryIconHelper.getCategoryIcon(category)
}

fun getBankBadge(accountName: String): String {
    return IndonesianInstitutionHelper.resolveInstitution(accountName).shortName
}

@Composable
fun AddTransactionDialog(
    viewModel: FinanceViewModel,
    initialCategory: String = "Other",
    initialWallet: String? = null,
    initialLabel: String = "",
    onDismiss: () -> Unit
) {
    val wallets by viewModel.wallets.collectAsState()
    val assets by viewModel.assets.collectAsState()
    val debts by viewModel.debts.collectAsState()
    val availableLabels by viewModel.labels.collectAsState()
    val allCategories by viewModel.categories.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()
    val selectedWalletFilter by viewModel.selectedWalletFilter.collectAsState()

    val currentLocalTime = remember {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }
    val currentCal = remember { Calendar.getInstance() }
    val currentRealDay = remember { currentCal.get(Calendar.DAY_OF_MONTH) }
    val currentRealMonth = remember { currentCal.get(Calendar.MONTH) + 1 }
    val currentRealYear = remember { currentCal.get(Calendar.YEAR) }
    val currentDayNames = listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")
    val currentRealDayOfWeek = remember {
        val idx = (currentCal.get(Calendar.DAY_OF_WEEK) - 1 + 7) % 7
        currentDayNames[idx]
    }

    var amountText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var selectedSubcategory by remember { mutableStateOf("") }
    var isCategoryChosen by remember { mutableStateOf(initialCategory != "Other") }
    var showSubcategoryDropdown by remember { mutableStateOf(false) }

    // Otomatis arahkan akun default ke akun yang dipilih di pojok kanan atas
    val defaultWalletName = remember(wallets, initialWallet, selectedWalletFilter) {
        val explicitMatch = initialWallet?.let { name -> wallets.firstOrNull { it.name.equals(name, ignoreCase = true) }?.name }
        if (explicitMatch != null) {
            explicitMatch
        } else if (selectedWalletFilter != "Semua" && selectedWalletFilter != "Semua Dompet" && selectedWalletFilter.isNotBlank()) {
            val filterMatch = wallets.firstOrNull { it.name.equals(selectedWalletFilter, ignoreCase = true) }?.name
            filterMatch ?: (wallets.firstOrNull()?.name ?: "BRI Utama")
        } else {
            wallets.firstOrNull()?.name ?: "BRI Utama"
        }
    }

    var selectedWallet by remember(defaultWalletName) { mutableStateOf(defaultWalletName) }
    var selectedTargetWallet by remember(wallets, selectedWallet) {
        val dest = wallets.firstOrNull { it.name != selectedWallet }?.name ?: wallets.getOrNull(1)?.name ?: "Bank Jago"
        mutableStateOf(dest)
    }
    var hasTransferFee by remember { mutableStateOf(false) }
    var transferFeeText by remember { mutableStateOf("") }
    var selectedAsset by remember(assets) { mutableStateOf(assets.firstOrNull()) }
    var isAssetDropdownExpanded by remember { mutableStateOf(false) }
    var selectedDebt by remember(debts) { mutableStateOf(debts.firstOrNull { it.type == DebtType.BORROWED } ?: debts.firstOrNull()) }
    var showDebtPicker by remember { mutableStateOf(false) }

    var selectedCurrency by remember { mutableStateOf("IDR") }
    var selectedDayOfMonth by remember { mutableIntStateOf(currentRealDay) }
    var selectedMonthIndex by remember { mutableIntStateOf(currentRealMonth) }
    var selectedYear by remember { mutableIntStateOf(currentRealYear) }
    var selectedDayOfWeek by remember { mutableStateOf(currentRealDayOfWeek) }
    var selectedDayLabel by remember { mutableStateOf("Hari Ini") }
    var selectedTime by remember { mutableStateOf(currentLocalTime) }
    var selectedRecurrence by remember { mutableStateOf("Tidak pernah") }
    var noteText by remember { mutableStateOf("") }
    var isNoteDropdownExpanded by remember { mutableStateOf(false) }
    var selectedLabels by remember { mutableStateOf(if (initialLabel.isNotBlank()) setOf(initialLabel) else setOf<String>()) }

    var showCategoryPicker by remember { mutableStateOf(false) }
    var showSourceAccountPicker by remember { mutableStateOf(false) }
    var showTargetAccountPicker by remember { mutableStateOf(false) }
    var showAssetPicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showRecurrencePicker by remember { mutableStateOf(false) }
    var showManageLabelsDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val isDebtPayment = selectedCategory.equals("Pembayaran Utang", ignoreCase = true) ||
            selectedCategory.equals("Bayar Utang", ignoreCase = true)

    val isTransferType = selectedCategory.equals("Transfer", ignoreCase = true) ||
            selectedCategory.equals("Transfer Antar Bank", ignoreCase = true) ||
            selectedCategory.equals("Transfer Aset", ignoreCase = true) ||
            selectedCategory.equals("Asset", ignoreCase = true) ||
            selectedCategory.equals("Aset", ignoreCase = true) ||
            isDebtPayment

    val isAssetTransfer = selectedCategory.equals("Asset", ignoreCase = true) ||
            selectedCategory.equals("Aset", ignoreCase = true) ||
            selectedCategory.equals("Transfer Aset", ignoreCase = true)

    val executeSave = {
        if (!isCategoryChosen && !isTransferType) {
            showCategoryPicker = true
        } else {
            val rawAmt = amountText.toDoubleOrNull() ?: 0.0
            val amt = TransactionMathHelper.sanitizeAmount(rawAmt)
            if (amt <= 0.0) {
                Toast.makeText(context, "Masukkan nominal transaksi terlebih dahulu", Toast.LENGTH_SHORT).show()
            } else {
                val rawFee = if (hasTransferFee) (transferFeeText.toDoubleOrNull() ?: 0.0) else 0.0
                val fee = TransactionMathHelper.sanitizeAmount(rawFee)
                val txType = when {
                    isTransferType -> TransactionType.TRANSFER
                    allCategories.any { it.type == TransactionType.INCOME && it.name.equals(selectedCategory, ignoreCase = true) } -> TransactionType.INCOME
                    allCategories.any { it.type == TransactionType.EXPENSE && it.name.equals(selectedCategory, ignoreCase = true) } -> TransactionType.EXPENSE
                    selectedCategory in listOf("Salary", "Extra Income", "Bonus", "Investasi", "Pemberian", "Gaji", "Pendapatan", "Dividen", "Hadiah") -> TransactionType.INCOME
                    else -> TransactionType.EXPENSE
                }
                val defaultTitle = when {
                    isDebtPayment -> "Bayar Utang ${selectedDebt?.name ?: ""}"
                    isAssetTransfer -> "Transfer ke ${selectedAsset?.name ?: "Aset"}"
                    isTransferType -> "Transfer ke $selectedTargetWallet"
                    else -> selectedCategory
                }
                viewModel.addTransaction(
                    Transaction(
                        title = if (noteText.isNotBlank()) noteText else defaultTitle,
                        category = if (isDebtPayment) "Pembayaran Utang" else if (isAssetTransfer) "Asset" else if (isTransferType) "Transfer" else selectedCategory,
                        subcategory = selectedSubcategory,
                        amount = amt,
                        type = txType,
                        accountName = selectedWallet,
                        dayOfMonth = selectedDayOfMonth,
                        monthIndex = selectedMonthIndex,
                        year = selectedYear,
                        dayOfWeekName = selectedDayOfWeek,
                        note = noteText,
                        label = selectedLabels.joinToString(", "),
                        recurrence = selectedRecurrence,
                        time = selectedTime,
                        toAccountName = if (isDebtPayment) (selectedDebt?.name ?: "") else if (isTransferType && !isAssetTransfer) selectedTargetWallet else "",
                        transferFee = fee,
                        targetAssetId = if (isAssetTransfer) (selectedAsset?.id ?: "") else "",
                        targetAssetName = if (isAssetTransfer) (selectedAsset?.name ?: "") else "",
                        targetDebtId = if (isDebtPayment) (selectedDebt?.id ?: "") else "",
                        targetDebtName = if (isDebtPayment) (selectedDebt?.name ?: "") else ""
                    )
                )
                onDismiss()
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Scaffold(
            containerColor = DarkBackground,
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            bottomBar = {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    color = DarkSurfaceElevated,
                    border = BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.7f)),
                    shadowElevation = 16.dp
                ) {
                    val bottomButtonText = when {
                        !isCategoryChosen && !isTransferType -> "Pilih Kategori"
                        isAssetTransfer -> "Simpan Transfer Aset"
                        isTransferType -> "Simpan Transfer"
                        else -> "Tambah Transaksi"
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 64.dp)
                    ) {
                        Button(
                            onClick = { executeSave() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("btn_save_transaction_bottom"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when {
                                    !isCategoryChosen && !isTransferType -> AccentBlue
                                    isAssetTransfer -> Color(0xFF059669)
                                    isTransferType -> Color(0xFF0284C7)
                                    else -> Color(0xFF10B981)
                                }
                            )
                        ) {
                            Icon(
                                imageVector = if (isTransferType) Icons.Default.SwapHoriz else Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = bottomButtonText,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                // 1. Top Header (Teal/Emerald or Blue for transfer)
                val headerColor = when {
                    isTransferType -> Color(0xFF0284C7)
                    selectedCategory in listOf("Salary", "Extra Income", "Bonus", "Investasi", "Pemberian") -> Color(0xFF059669)
                    else -> Color(0xFF009688)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(headerColor)
                        .statusBarsPadding()
                        .padding(bottom = 18.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Header bar: [X] Tambah transaksi / Transfer & [✓ Simpan]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = onDismiss) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Tutup",
                                        tint = Color.White,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = when {
                                        isDebtPayment -> "Bayar Utang"
                                        isAssetTransfer -> "Transfer Aset"
                                        isTransferType -> "Transfer Antar Akun"
                                        else -> "Tambah transaksi"
                                    },
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Category Circle + Amount Input + Currency Tag
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Category dashed circular button
                            Box(
                                modifier = Modifier
                                    .size(58.dp)
                                    .clickable { showCategoryPicker = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawCircle(
                                        color = Color.White.copy(alpha = 0.85f),
                                        style = Stroke(
                                            width = 2.dp.toPx(),
                                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                                        )
                                    )
                                }
                                Icon(
                                    imageVector = getCategoryIcon(selectedCategory),
                                    contentDescription = "Ganti Kategori",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Large amount field
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                BasicTextField(
                                    value = amountText,
                                    onValueChange = { input ->
                                        val digits = input.filter { it.isDigit() }
                                        if (digits.length <= 11) {
                                            amountText = digits
                                        }
                                    },
                                    visualTransformation = ThousandsSeparatorVisualTransformation('.'),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    textStyle = TextStyle(
                                        color = Color.White,
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    cursorBrush = SolidColor(Color.White),
                                    singleLine = true,
                                    decorationBox = { innerTextField ->
                                        if (amountText.isEmpty()) {
                                            Text(
                                                text = "0",
                                                fontSize = 36.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White.copy(alpha = 0.95f)
                                            )
                                        }
                                        innerTextField()
                                    }
                                )
                            }

                            // Currency selector pill
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color(0x33000000),
                                modifier = Modifier.clickable {
                                    selectedCurrency = if (selectedCurrency == "IDR") "USD" else "IDR"
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = selectedCurrency,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Transfer Switcher (Transfer, Asset)
                if (isTransferType) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                            .background(DarkSurfaceElevated, RoundedCornerShape(14.dp))
                            .border(1.dp, DarkCardBorder, RoundedCornerShape(14.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val transferOptions = listOf(
                            Triple("Transfer", Icons.Default.SwapHoriz, "Transfer"),
                            Triple("Transfer Aset", Icons.AutoMirrored.Filled.TrendingUp, "Asset"),
                            Triple("Bayar Utang", Icons.Default.Handshake, "Pembayaran Utang")
                        )
                        transferOptions.forEach { (label, icon, catValue) ->
                            val isCurrent = when (catValue) {
                                "Pembayaran Utang" -> isDebtPayment
                                "Asset" -> isAssetTransfer
                                else -> !isAssetTransfer && !isDebtPayment
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isCurrent) AccentBlue else Color.Transparent,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedCategory = catValue
                                        isCategoryChosen = true
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (isCurrent) Color.White else TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isCurrent) Color.White else TextMuted,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Form Body
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    if (isDebtPayment) {
                        // === MODE BAYAR UTANG ===
                        // Row 1: Dari akun
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showSourceAccountPicker = true }
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.CallMade,
                                    contentDescription = null,
                                    tint = ExpenseRed,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Dari akun",
                                    fontSize = 15.sp,
                                    color = TextWhite
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurfaceElevated,
                                border = BorderStroke(1.dp, DarkCardBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = AccentBlue,
                                        modifier = Modifier.padding(end = 6.dp)
                                    ) {
                                        Text(
                                            text = getBankBadge(selectedWallet),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                    Text(
                                        text = selectedWallet,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextWhite
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = DarkCardBorder.copy(alpha = 0.5f), thickness = 0.5.dp)

                        // Row 2: Ke Utang
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDebtPicker = true }
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Handshake,
                                    contentDescription = null,
                                    tint = Color(0xFFFF5722),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = "Ke utang",
                                        fontSize = 15.sp,
                                        color = TextWhite
                                    )
                                    selectedDebt?.let { d ->
                                        Text(
                                            text = "Sisa: ${CurrencyHelper.formatRupiah(d.remainingAmount)}",
                                            fontSize = 11.sp,
                                            color = TextMuted
                                        )
                                    }
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurfaceElevated,
                                border = BorderStroke(1.dp, Color(0xFFFF5722).copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = selectedDebt?.name ?: "Pilih Utang",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFFFF6B4A)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = DarkCardBorder.copy(alpha = 0.5f), thickness = 0.5.dp)

                        // Row 3: Biaya transfer (admin)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Payments,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Biaya admin / transfer",
                                    fontSize = 15.sp,
                                    color = TextWhite
                                )
                            }

                            Switch(
                                checked = hasTransferFee,
                                onCheckedChange = { hasTransferFee = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = AccentBlue,
                                    uncheckedThumbColor = TextMuted,
                                    uncheckedTrackColor = DarkSurfaceElevated
                                )
                            )
                        }

                        if (hasTransferFee) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = DarkSurfaceElevated,
                                border = BorderStroke(1.dp, DarkCardBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 4.dp)
                            ) {
                                BasicTextField(
                                    value = transferFeeText,
                                    onValueChange = { transferFeeText = it.filter { ch -> ch.isDigit() } },
                                    textStyle = TextStyle(
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    cursorBrush = SolidColor(AccentBlue),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    decorationBox = { inner ->
                                        if (transferFeeText.isEmpty()) {
                                            Text(
                                                text = "0 (Nominal biaya transfer)",
                                                fontSize = 14.sp,
                                                color = TextSubtle
                                            )
                                        }
                                        inner()
                                    }
                                )
                            }
                        }
                    } else if (isTransferType && !isAssetTransfer) {
                        // === MODE TRANSFER / TOP UP ===
                        // Row 1: Dari akun
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showSourceAccountPicker = true }
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.CallMade,
                                    contentDescription = null,
                                    tint = ExpenseRed,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Dari akun",
                                    fontSize = 15.sp,
                                    color = TextWhite
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurfaceElevated,
                                border = BorderStroke(1.dp, DarkCardBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = AccentBlue,
                                        modifier = Modifier.padding(end = 6.dp)
                                    ) {
                                        Text(
                                            text = getBankBadge(selectedWallet),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                    Text(
                                        text = selectedWallet,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextWhite
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        // Row 2: Ke akun
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showTargetAccountPicker = true }
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.CallReceived,
                                    contentDescription = null,
                                    tint = IncomeGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Ke akun",
                                    fontSize = 15.sp,
                                    color = TextWhite
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurfaceElevated,
                                border = BorderStroke(1.dp, DarkCardBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = IncomeGreen,
                                        modifier = Modifier.padding(end = 6.dp)
                                    ) {
                                        Text(
                                            text = getBankBadge(selectedTargetWallet),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                    Text(
                                        text = selectedTargetWallet,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextWhite
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        // Row 3: Biaya transfer switch
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = "Biaya transfer",
                                        fontSize = 15.sp,
                                        color = TextWhite
                                    )
                                    Text(
                                        text = "Tambahkan biaya admin transfer",
                                        fontSize = 11.sp,
                                        color = TextSubtle
                                    )
                                }
                            }

                            Switch(
                                checked = hasTransferFee,
                                onCheckedChange = { hasTransferFee = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = AccentBlue,
                                    uncheckedThumbColor = TextMuted,
                                    uncheckedTrackColor = DarkSurfaceElevated
                                )
                            )
                        }

                        // Row 3b: Nominal biaya (appears when switch is ON)
                        if (hasTransferFee) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 6.dp)
                                    .background(DarkSurfaceElevated, RoundedCornerShape(14.dp))
                                    .border(1.dp, DarkCardBorder, RoundedCornerShape(14.dp))
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Rp",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentBlue
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                BasicTextField(
                                    value = transferFeeText,
                                    onValueChange = { input ->
                                        val digits = input.filter { it.isDigit() }
                                        if (digits.length <= 9) transferFeeText = digits
                                    },
                                    visualTransformation = ThousandsSeparatorVisualTransformation('.'),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    textStyle = TextStyle(
                                        color = TextWhite,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    cursorBrush = SolidColor(AccentBlue),
                                    modifier = Modifier.fillMaxWidth(),
                                    decorationBox = { inner ->
                                        if (transferFeeText.isEmpty()) {
                                            Text(
                                                text = "0 (Nominal biaya transfer)",
                                                fontSize = 14.sp,
                                                color = TextSubtle
                                            )
                                        }
                                        inner()
                                    }
                                )
                            }
                        }
                    } else if (isAssetTransfer) {
                        // === MODE ASSET ===
                        // Row 1: Dari akun
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showSourceAccountPicker = true }
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.CallMade,
                                    contentDescription = null,
                                    tint = ExpenseRed,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Dari akun",
                                    fontSize = 15.sp,
                                    color = TextWhite
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurfaceElevated,
                                border = BorderStroke(1.dp, DarkCardBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = AccentBlue,
                                        modifier = Modifier.padding(end = 6.dp)
                                    ) {
                                        Text(
                                            text = getBankBadge(selectedWallet),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                    Text(
                                        text = selectedWallet,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextWhite
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        // Row 2: Dropdown Aset yang sudah ditambahkan (bisa diklik lagi untuk ganti aset)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isAssetDropdownExpanded = true }
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = null,
                                    tint = IncomeGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = "Aset",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextWhite
                                    )
                                    Text(
                                        text = if (selectedAsset != null) "${selectedAsset?.group} · ${selectedAsset?.platform}" else "Pilih aset dari daftar",
                                        fontSize = 11.sp,
                                        color = TextSubtle
                                    )
                                }
                            }

                            Box {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = DarkSurfaceElevated,
                                    border = BorderStroke(1.dp, if (selectedAsset != null) AccentBlue else DarkCardBorder),
                                    modifier = Modifier.clickable { isAssetDropdownExpanded = true }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (selectedAsset != null) {
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = IncomeGreenSoft,
                                                modifier = Modifier.padding(end = 6.dp)
                                            ) {
                                                Text(
                                                    text = selectedAsset?.platform ?: "Aset",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = IncomeGreen,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                            Text(
                                                text = selectedAsset?.name ?: "Pilih Aset",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = TextWhite
                                            )
                                        } else {
                                            Text(
                                                text = "Pilih Aset",
                                                fontSize = 13.sp,
                                                color = TextMuted
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = "Ganti Aset",
                                            tint = AccentBlue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = isAssetDropdownExpanded,
                                    onDismissRequest = { isAssetDropdownExpanded = false },
                                    modifier = Modifier
                                        .background(DarkSurfaceElevated)
                                        .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                                        .heightIn(max = 280.dp)
                                ) {
                                    if (assets.isEmpty()) {
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = "Belum ada aset ditambahkan",
                                                    fontSize = 13.sp,
                                                    color = TextMuted
                                                )
                                            },
                                            onClick = {
                                                isAssetDropdownExpanded = false
                                                showAssetPicker = true
                                            }
                                        )
                                    } else {
                                        assets.forEach { asset ->
                                            val isSelected = selectedAsset?.id == asset.id
                                            DropdownMenuItem(
                                                text = {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(vertical = 4.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            modifier = Modifier.weight(1f)
                                                        ) {
                                                            Surface(
                                                                shape = RoundedCornerShape(4.dp),
                                                                color = if (isSelected) AccentBlue else DarkSurface,
                                                                border = BorderStroke(1.dp, if (isSelected) AccentBlue else DarkCardBorder),
                                                                modifier = Modifier.padding(end = 8.dp)
                                                            ) {
                                                                Text(
                                                                    text = asset.platform,
                                                                    fontSize = 9.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = Color.White,
                                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                                )
                                                            }
                                                            Column {
                                                                Text(
                                                                    text = asset.name,
                                                                    fontSize = 13.sp,
                                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                                    color = TextWhite
                                                                )
                                                                Text(
                                                                    text = "${asset.group} · ${CurrencyHelper.formatRupiah(asset.value)}",
                                                                    fontSize = 11.sp,
                                                                    color = TextMuted
                                                                )
                                                            }
                                                        }
                                                        if (isSelected) {
                                                            Spacer(modifier = Modifier.width(8.dp))
                                                            Icon(
                                                                imageVector = Icons.Default.Check,
                                                                contentDescription = "Terpilih",
                                                                tint = AccentBlue,
                                                                modifier = Modifier.size(18.dp)
                                                            )
                                                        }
                                                    }
                                                },
                                                onClick = {
                                                    selectedAsset = asset
                                                    isAssetDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Row 3: Biaya transfer switch
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = "Biaya transfer / admin",
                                        fontSize = 15.sp,
                                        color = TextWhite
                                    )
                                    Text(
                                        text = "Tambahkan biaya administrasi",
                                        fontSize = 11.sp,
                                        color = TextSubtle
                                    )
                                }
                            }

                            Switch(
                                checked = hasTransferFee,
                                onCheckedChange = { hasTransferFee = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = AccentBlue,
                                    uncheckedThumbColor = TextMuted,
                                    uncheckedTrackColor = DarkSurfaceElevated
                                )
                            )
                        }

                        // Row 3b: Nominal biaya
                        if (hasTransferFee) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 6.dp)
                                    .background(DarkSurfaceElevated, RoundedCornerShape(14.dp))
                                    .border(1.dp, DarkCardBorder, RoundedCornerShape(14.dp))
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Rp",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentBlue
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                BasicTextField(
                                    value = transferFeeText,
                                    onValueChange = { input ->
                                        val digits = input.filter { it.isDigit() }
                                        if (digits.length <= 9) transferFeeText = digits
                                    },
                                    visualTransformation = ThousandsSeparatorVisualTransformation('.'),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    textStyle = TextStyle(
                                        color = TextWhite,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    cursorBrush = SolidColor(AccentBlue),
                                    modifier = Modifier.fillMaxWidth(),
                                    decorationBox = { inner ->
                                        if (transferFeeText.isEmpty()) {
                                            Text(
                                                text = "0 (Nominal biaya transfer)",
                                                fontSize = 14.sp,
                                                color = TextSubtle
                                            )
                                        }
                                        inner()
                                    }
                                )
                            }
                        }
                    } else {
                        // === MODE BIASA (PENGELUARAN / PEMASUKAN) ===
                        // Row 1: Akun
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showSourceAccountPicker = true }
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Akun",
                                    fontSize = 15.sp,
                                    color = TextWhite
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurfaceElevated,
                                border = BorderStroke(1.dp, DarkCardBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = AccentBlue,
                                        modifier = Modifier.padding(end = 6.dp)
                                    ) {
                                        Text(
                                            text = getBankBadge(selectedWallet),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                    Text(
                                        text = selectedWallet,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextWhite
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        // Row 2: Kategori
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showCategoryPicker = true }
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Category,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Kategori",
                                    fontSize = 15.sp,
                                    color = TextWhite
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurfaceElevated,
                                border = BorderStroke(1.dp, DarkCardBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = getCategoryIcon(selectedCategory),
                                        contentDescription = null,
                                        tint = AccentBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = selectedCategory,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextWhite
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Row: Hari Ini (Date & Time)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true }
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = selectedDayLabel,
                                fontSize = 15.sp,
                                color = TextWhite
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurfaceElevated,
                                border = BorderStroke(1.dp, DarkCardBorder),
                                modifier = Modifier.clickable { showTimePicker = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = "Pilih Jam",
                                        tint = AccentBlue,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = selectedTime,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextWhite
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurfaceElevated,
                                border = BorderStroke(1.dp, DarkCardBorder),
                                modifier = Modifier.clickable {
                                    val yCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
                                    selectedDayOfMonth = yCal.get(Calendar.DAY_OF_MONTH)
                                    selectedMonthIndex = yCal.get(Calendar.MONTH) + 1
                                    selectedYear = yCal.get(Calendar.YEAR)
                                    val dayIdx = (yCal.get(Calendar.DAY_OF_WEEK) - 1 + 7) % 7
                                    selectedDayOfWeek = currentDayNames[dayIdx]
                                    selectedDayLabel = "Kemarin"
                                }
                            ) {
                                Text(
                                    text = "Yesterday ?",
                                    fontSize = 12.sp,
                                    color = TextMuted,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    // Row: Ulangi
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showRecurrencePicker = true }
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Repeat,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Ulangi",
                                fontSize = 15.sp,
                                color = TextWhite
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = selectedRecurrence,
                                fontSize = 13.sp,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Autocomplete Note matching logic
                    val previousNotes = remember(allTransactions) {
                        val list = allTransactions
                            .mapNotNull { it.note.trim().takeIf { n -> n.isNotEmpty() } }
                            .distinct()
                        if (list.isNotEmpty()) list else listOf(
                            "Gaji bulanan", "Makan siang", "Beli bensin", "Belanja dapur", "Ngopi santai", "Token listrik", "Makan malam", "Bayar tagihan", "Servis motor", "Pulsa & kuota"
                        )
                    }

                    val noteSuggestions = remember(previousNotes, noteText) {
                        val query = noteText.trim().lowercase()
                        if (query.isEmpty()) {
                            emptyList()
                        } else {
                            val lastWord = query.split(" ").lastOrNull()?.trim() ?: ""
                            previousNotes.filter { candidate ->
                                val candLower = candidate.lowercase()
                                if (candLower == query) {
                                    false
                                } else if (candLower.startsWith(query) || candLower.contains(query)) {
                                    true
                                } else if (lastWord.isNotEmpty() && candLower.split(" ", "-", "_").any { it.startsWith(lastWord) || (lastWord.length >= 2 && it.contains(lastWord)) }) {
                                    true
                                } else {
                                    false
                                }
                            }.sortedWith(
                                compareBy<String> { !it.lowercase().startsWith(query) }
                                    .thenBy { !it.lowercase().contains(query) }
                            ).take(5)
                        }
                    }

                    // Row: Tulis catatan with Autocomplete Hint Dropdown
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = if (noteText.isNotEmpty()) AccentBlue else TextMuted,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            BasicTextField(
                                value = noteText,
                                onValueChange = { input ->
                                    noteText = input
                                    isNoteDropdownExpanded = input.trim().isNotEmpty()
                                },
                                textStyle = TextStyle(
                                    color = TextWhite,
                                    fontSize = 15.sp
                                ),
                                cursorBrush = SolidColor(AccentBlue),
                                modifier = Modifier.weight(1f),
                                decorationBox = { innerTextField ->
                                    if (noteText.isEmpty()) {
                                        Text(
                                            text = "Tulis catatan",
                                            fontSize = 15.sp,
                                            color = TextSubtle
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                            if (noteText.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        noteText = ""
                                        isNoteDropdownExpanded = false
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Hapus catatan",
                                        tint = TextSubtle,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        // Real-time Autocomplete Hint Dropdown
                        DropdownMenu(
                            expanded = isNoteDropdownExpanded && noteSuggestions.isNotEmpty() && noteText.trim().isNotEmpty(),
                            onDismissRequest = { isNoteDropdownExpanded = false },
                            properties = PopupProperties(focusable = false),
                            modifier = Modifier
                                .fillMaxWidth(0.88f)
                                .background(DarkSurfaceElevated)
                                .border(1.dp, AccentBlue.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = AccentBlue,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Rekomendasi Catatan Terdahulu",
                                    fontSize = 11.sp,
                                    color = AccentBlue,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            HorizontalDivider(color = DarkCardBorder, thickness = 0.5.dp)

                            noteSuggestions.forEach { suggestion ->
                                DropdownMenuItem(
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.History,
                                            contentDescription = null,
                                            tint = TextMuted,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    text = {
                                        Text(
                                            text = suggestion,
                                            fontSize = 14.sp,
                                            color = TextWhite,
                                            fontWeight = FontWeight.Medium
                                        )
                                    },
                                    onClick = {
                                        noteText = suggestion
                                        isNoteDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Row: Label
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showManageLabelsDialog = true }
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Label,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Label",
                                    fontSize = 15.sp,
                                    color = TextWhite
                                )
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Label pills from Settings
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(availableLabels) { labelName ->
                                val isSelected = selectedLabels.contains(labelName)
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) AccentBlue else DarkSurfaceElevated,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) AccentBlue else DarkCardBorder
                                    ),
                                    modifier = Modifier.clickable {
                                        selectedLabels = if (isSelected) {
                                            selectedLabels - labelName
                                        } else {
                                            selectedLabels + labelName
                                        }
                                    }
                                ) {
                                    Text(
                                        text = labelName,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else TextMuted,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Sub-dialogs
    if (showCategoryPicker) {
        CategoryPickerSubDialog(
            currentCategory = selectedCategory,
            currentSubcategory = selectedSubcategory,
            categories = allCategories,
            onCategorySelected = {
                selectedCategory = it
                selectedSubcategory = ""
                isCategoryChosen = true
                showCategoryPicker = false
            },
            onCategoryAndSubcategorySelected = { cat, subcat ->
                selectedCategory = cat
                selectedSubcategory = subcat
                isCategoryChosen = true
                showCategoryPicker = false
            },
            onDismiss = { showCategoryPicker = false }
        )
    }

    if (showSourceAccountPicker) {
        AccountPickerSubDialog(
            wallets = wallets.map { it.name },
            currentAccount = selectedWallet,
            title = "Pilih Akun Asal",
            onAccountSelected = {
                selectedWallet = it
                if (selectedTargetWallet == it) {
                    selectedTargetWallet = wallets.firstOrNull { w -> w.name != it }?.name ?: ""
                }
                showSourceAccountPicker = false
            },
            onDismiss = { showSourceAccountPicker = false }
        )
    }

    if (showTargetAccountPicker) {
        AccountPickerSubDialog(
            wallets = wallets.map { it.name },
            currentAccount = selectedTargetWallet,
            title = "Pilih Akun Tujuan",
            onAccountSelected = {
                selectedTargetWallet = it
                showTargetAccountPicker = false
            },
            onDismiss = { showTargetAccountPicker = false }
        )
    }

    if (showAssetPicker) {
        AssetPickerSubDialog(
            assets = assets,
            selectedAsset = selectedAsset,
            onAssetSelected = {
                selectedAsset = it
                showAssetPicker = false
            },
            onDismiss = { showAssetPicker = false }
        )
    }

    if (showDatePicker) {
        DatePickerSubDialog(
            initialDay = selectedDayOfMonth,
            initialMonth = selectedMonthIndex,
            initialYear = selectedYear,
            onDateSelected = { day, month, year, dayName, label ->
                selectedDayOfMonth = day
                selectedMonthIndex = month
                selectedYear = year
                selectedDayOfWeek = dayName
                selectedDayLabel = label
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    if (showTimePicker) {
        TimePickerSubDialog(
            currentTime = selectedTime,
            onTimeSelected = {
                selectedTime = it
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }

    if (showRecurrencePicker) {
        RecurrencePickerSubDialog(
            currentRecurrence = selectedRecurrence,
            onRecurrenceSelected = {
                selectedRecurrence = it
                showRecurrencePicker = false
            },
            onDismiss = { showRecurrencePicker = false }
        )
    }

    if (showManageLabelsDialog) {
        ManageLabelsDialog(
            viewModel = viewModel,
            onDismiss = { showManageLabelsDialog = false }
        )
    }

    if (showDebtPicker) {
        AlertDialog(
            onDismissRequest = { showDebtPicker = false },
            containerColor = DarkSurface,
            title = {
                Text(
                    text = "Pilih Utang untuk Dibayar",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.height(280.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val activeDebts = debts.filter { it.remainingAmount > 0 }
                    if (activeDebts.isEmpty()) {
                        item {
                            Text(
                                text = "Tidak ada utang aktif yang ditemukan.",
                                color = TextMuted,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        items(activeDebts) { d ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (selectedDebt?.id == d.id) AccentBlue.copy(alpha = 0.2f) else DarkSurfaceElevated,
                                border = BorderStroke(1.dp, if (selectedDebt?.id == d.id) AccentBlue else DarkCardBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedDebt = d
                                        showDebtPicker = false
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = d.name,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextWhite
                                        )
                                        Text(
                                            text = "Tempo: ${d.dueDate.ifBlank { "-" }}",
                                            fontSize = 11.sp,
                                            color = TextMuted
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = CurrencyHelper.formatRupiah(d.remainingAmount),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFF6B4A)
                                        )
                                        Text(
                                            text = "Sisa Utang",
                                            fontSize = 10.sp,
                                            color = TextSubtle
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showDebtPicker = false }) {
                    Text("Tutup", color = TextMuted)
                }
            }
        )
    }
}
}

@Composable
fun EditTransactionDialog(
    transaction: Transaction,
    viewModel: FinanceViewModel,
    onDismiss: () -> Unit
) {
    val wallets by viewModel.wallets.collectAsState()
    val assets by viewModel.assets.collectAsState()
    val availableLabels by viewModel.labels.collectAsState()
    val allCategories by viewModel.categories.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()

    val formattedInitialAmount = remember(transaction) {
        if (transaction.amount % 1.0 == 0.0) {
            transaction.amount.toLong().toString()
        } else {
            transaction.amount.toString()
        }
    }

    var amountText by remember { mutableStateOf(formattedInitialAmount) }
    var selectedCategory by remember { mutableStateOf(transaction.category) }
    var selectedSubcategory by remember { mutableStateOf(transaction.subcategory) }
    var showSubcategoryDropdown by remember { mutableStateOf(false) }
    var selectedWallet by remember { mutableStateOf(transaction.accountName) }
    var selectedTargetWallet by remember(transaction, wallets) {
        val dest = transaction.toAccountName.ifBlank {
            wallets.firstOrNull { it.name != transaction.accountName }?.name ?: wallets.getOrNull(1)?.name ?: "Bank Jago"
        }
        mutableStateOf(dest)
    }
    var hasTransferFee by remember { mutableStateOf(transaction.transferFee > 0.0) }
    var transferFeeText by remember {
        mutableStateOf(if (transaction.transferFee > 0.0) transaction.transferFee.toLong().toString() else "")
    }
    var selectedAsset by remember(transaction, assets) {
        val found = assets.firstOrNull { it.id == transaction.targetAssetId || it.name.equals(transaction.targetAssetName, ignoreCase = true) } ?: assets.firstOrNull()
        mutableStateOf(found)
    }
    var isAssetDropdownExpanded by remember { mutableStateOf(false) }

    val debts by viewModel.debts.collectAsState()
    var selectedDebt by remember(transaction, debts) {
        val found = debts.firstOrNull { it.id == transaction.targetDebtId || it.name.equals(transaction.targetDebtName, ignoreCase = true) }
            ?: debts.firstOrNull { it.name.equals(transaction.toAccountName, ignoreCase = true) }
            ?: debts.firstOrNull { it.type == DebtType.BORROWED }
            ?: debts.firstOrNull()
        mutableStateOf(found)
    }
    var showDebtPicker by remember { mutableStateOf(false) }

    var selectedDayOfMonth by remember { mutableIntStateOf(transaction.dayOfMonth) }
    var selectedMonthIndex by remember { mutableIntStateOf(transaction.monthIndex) }
    var selectedYear by remember { mutableIntStateOf(transaction.year) }
    var selectedDayOfWeek by remember { mutableStateOf(transaction.dayOfWeekName) }
    var selectedDayLabel by remember {
        mutableStateOf("${transaction.dayOfWeekName}, ${transaction.dayOfMonth}/${transaction.monthIndex}")
    }
    var selectedTime by remember { mutableStateOf(transaction.time) }
    var selectedRecurrence by remember { mutableStateOf(transaction.recurrence) }
    var noteText by remember { mutableStateOf(transaction.note.ifBlank { transaction.title }) }
    var isNoteDropdownExpanded by remember { mutableStateOf(false) }
    var selectedLabels by remember {
        mutableStateOf(
            transaction.label.split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .toSet()
        )
    }

    var showCategoryPicker by remember { mutableStateOf(false) }
    var showSourceAccountPicker by remember { mutableStateOf(false) }
    var showTargetAccountPicker by remember { mutableStateOf(false) }
    var showAssetPicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showRecurrencePicker by remember { mutableStateOf(false) }
    var showManageLabelsDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val isDebtPayment = selectedCategory.equals("Pembayaran Utang", ignoreCase = true) ||
            selectedCategory.equals("Bayar Utang", ignoreCase = true) ||
            transaction.targetDebtId.isNotBlank() ||
            transaction.targetDebtName.isNotBlank()

    val isTransferType = selectedCategory.equals("Transfer", ignoreCase = true) ||
            selectedCategory.equals("Transfer Antar Bank", ignoreCase = true) ||
            selectedCategory.equals("Transfer Aset", ignoreCase = true) ||
            selectedCategory.equals("Asset", ignoreCase = true) ||
            selectedCategory.equals("Aset", ignoreCase = true) ||
            isDebtPayment

    val isAssetTransfer = selectedCategory.equals("Asset", ignoreCase = true) ||
            selectedCategory.equals("Aset", ignoreCase = true) ||
            selectedCategory.equals("Transfer Aset", ignoreCase = true)

    val executeSaveEdit = {
        val rawAmt = amountText.toDoubleOrNull() ?: 0.0
        val amt = TransactionMathHelper.sanitizeAmount(rawAmt)
        if (amt <= 0.0) {
            Toast.makeText(context, "Masukkan nominal transaksi terlebih dahulu", Toast.LENGTH_SHORT).show()
        } else {
            val rawFee = if (hasTransferFee) (transferFeeText.toDoubleOrNull() ?: 0.0) else 0.0
            val fee = TransactionMathHelper.sanitizeAmount(rawFee)
            val txType = when {
                isTransferType -> TransactionType.TRANSFER
                allCategories.any { it.type == TransactionType.INCOME && it.name.equals(selectedCategory, ignoreCase = true) } -> TransactionType.INCOME
                allCategories.any { it.type == TransactionType.EXPENSE && it.name.equals(selectedCategory, ignoreCase = true) } -> TransactionType.EXPENSE
                selectedCategory in listOf("Salary", "Extra Income", "Bonus", "Investasi", "Pemberian", "Gaji", "Pendapatan", "Dividen", "Hadiah") -> TransactionType.INCOME
                selectedCategory in listOf("Other", "Bills & Fees", "Market", "Food & Dining", "Shopping", "Transport", "Kesehatan", "Pendidikan", "Hiburan") -> TransactionType.EXPENSE
                transaction.category.equals(selectedCategory, ignoreCase = true) -> transaction.type
                else -> TransactionType.EXPENSE
            }
            val defaultTitle = when {
                isDebtPayment -> "Bayar Utang ${selectedDebt?.name ?: ""}"
                isAssetTransfer -> "Transfer ke ${selectedAsset?.name ?: "Aset"}"
                isTransferType -> "Transfer ke $selectedTargetWallet"
                else -> selectedCategory
            }
            viewModel.updateTransaction(
                transaction.copy(
                    title = if (noteText.isNotBlank()) noteText else defaultTitle,
                    category = if (isDebtPayment) "Pembayaran Utang" else if (isAssetTransfer) "Asset" else if (isTransferType) "Transfer" else selectedCategory,
                    subcategory = selectedSubcategory,
                    amount = amt,
                    type = txType,
                    accountName = selectedWallet,
                    dayOfMonth = selectedDayOfMonth,
                    monthIndex = selectedMonthIndex,
                    year = selectedYear,
                    dayOfWeekName = selectedDayOfWeek,
                    note = noteText,
                    label = selectedLabels.joinToString(", "),
                    recurrence = selectedRecurrence,
                    time = selectedTime,
                    toAccountName = if (isDebtPayment) (selectedDebt?.name ?: "") else if (isTransferType && !isAssetTransfer) selectedTargetWallet else "",
                    transferFee = fee,
                    targetAssetId = if (isAssetTransfer) (selectedAsset?.id ?: "") else "",
                    targetAssetName = if (isAssetTransfer) (selectedAsset?.name ?: "") else "",
                    targetDebtId = if (isDebtPayment) (selectedDebt?.id ?: "") else "",
                    targetDebtName = if (isDebtPayment) (selectedDebt?.name ?: "") else ""
                )
            )
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Scaffold(
            containerColor = DarkBackground,
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
            bottomBar = {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    color = DarkSurfaceElevated,
                    border = BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.7f)),
                    shadowElevation = 16.dp
                ) {
                    val bottomButtonText = when {
                        isAssetTransfer -> "Simpan Transfer Aset"
                        isTransferType -> "Simpan Transfer"
                        else -> "Simpan Perubahan"
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 64.dp)
                    ) {
                        Button(
                            onClick = { executeSaveEdit() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("btn_save_edit_transaction_bottom"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when {
                                    isAssetTransfer -> Color(0xFF059669)
                                    isTransferType -> Color(0xFF0284C7)
                                    else -> Color(0xFF10B981)
                                }
                            )
                        ) {
                            Icon(
                                imageVector = if (isTransferType) Icons.Default.SwapHoriz else Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = bottomButtonText,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header (Teal/Emerald or Blue for transfer)
                val headerColor = when {
                    isTransferType -> Color(0xFF0284C7)
                    selectedCategory in listOf("Salary", "Extra Income", "Bonus", "Investasi", "Pemberian") -> Color(0xFF059669)
                    else -> Color(0xFF0D9488)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(headerColor)
                        .padding(horizontal = 16.dp, vertical = 18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = onDismiss,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Tutup",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = when {
                                        isAssetTransfer -> "Edit Transfer Aset"
                                        isTransferType -> "Edit Transfer"
                                        else -> "Edit transaksi"
                                    },
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            IconButton(
                                onClick = { showDeleteConfirmation = true },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Hapus Transaksi",
                                    tint = Color.White.copy(alpha = 0.9f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Category Icon & Amount Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clickable { showCategoryPicker = true },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val strokeWidth = 2.dp.toPx()
                                        drawCircle(
                                            color = Color.White.copy(alpha = 0.85f),
                                            radius = size.minDimension / 2 - strokeWidth / 2,
                                            style = Stroke(
                                                width = strokeWidth,
                                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                                            )
                                        )
                                    }
                                    Icon(
                                        imageVector = getCategoryIcon(selectedCategory),
                                        contentDescription = "Pilih Kategori",
                                        tint = Color.White,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                // Large Amount Input
                                BasicTextField(
                                    value = amountText,
                                    onValueChange = { input ->
                                        val digits = input.filter { it.isDigit() }
                                        if (digits.length <= 11) {
                                            amountText = digits
                                        }
                                    },
                                    visualTransformation = ThousandsSeparatorVisualTransformation('.'),
                                    textStyle = TextStyle(
                                        color = Color.White,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    cursorBrush = SolidColor(Color.White),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    decorationBox = { innerTextField ->
                                        if (amountText.isEmpty()) {
                                            Text(
                                                text = "0",
                                                fontSize = 32.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White.copy(alpha = 0.5f)
                                            )
                                        }
                                        innerTextField()
                                    }
                                )
                            }

                            // IDR Pill
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "IDR",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Transfer Switcher (Transfer, Asset, Bayar Utang)
                if (isTransferType) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                            .background(DarkSurfaceElevated, RoundedCornerShape(14.dp))
                            .border(1.dp, DarkCardBorder, RoundedCornerShape(14.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val transferOptions = listOf(
                            Triple("Transfer", Icons.Default.SwapHoriz, "Transfer"),
                            Triple("Transfer Aset", Icons.AutoMirrored.Filled.TrendingUp, "Asset"),
                            Triple("Bayar Utang", Icons.Default.Handshake, "Pembayaran Utang")
                        )
                        transferOptions.forEach { (label, icon, catValue) ->
                            val isCurrent = when (catValue) {
                                "Pembayaran Utang" -> isDebtPayment
                                "Asset" -> isAssetTransfer
                                else -> !isAssetTransfer && !isDebtPayment
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isCurrent) AccentBlue else Color.Transparent,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedCategory = catValue
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (isCurrent) Color.White else TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isCurrent) Color.White else TextMuted,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                // Form List Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    if (isDebtPayment) {
                        // Bayar Utang: Dari Akun, Ke Utang, Biaya Transfer
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showSourceAccountPicker = true }
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.CallMade,
                                    contentDescription = null,
                                    tint = ExpenseRed,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Dari akun",
                                    fontSize = 15.sp,
                                    color = TextWhite
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurfaceElevated,
                                border = BorderStroke(1.dp, DarkCardBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = AccentBlue,
                                        modifier = Modifier.padding(end = 6.dp)
                                    ) {
                                        Text(
                                            text = getBankBadge(selectedWallet),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                    Text(
                                        text = selectedWallet,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextWhite
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDebtPicker = true }
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Handshake,
                                    contentDescription = null,
                                    tint = Color(0xFFFF5722),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = "Ke utang",
                                        fontSize = 15.sp,
                                        color = TextWhite
                                    )
                                    selectedDebt?.let { d ->
                                        Text(
                                            text = "Sisa: ${CurrencyHelper.formatRupiah(d.remainingAmount)}",
                                            fontSize = 11.sp,
                                            color = TextMuted
                                        )
                                    }
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurfaceElevated,
                                border = BorderStroke(1.dp, Color(0xFFFF5722).copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = selectedDebt?.name ?: "Pilih Utang",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFFFF6B4A)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        // Fee toggle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Payments,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Biaya admin / transfer",
                                    fontSize = 15.sp,
                                    color = TextWhite
                                )
                            }
                            Switch(
                                checked = hasTransferFee,
                                onCheckedChange = { hasTransferFee = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = AccentBlue)
                            )
                        }

                        if (hasTransferFee) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Nominal Biaya Admin: Rp ", fontSize = 13.sp, color = TextMuted)
                                OutlinedTextField(
                                    value = transferFeeText,
                                    onValueChange = { input -> transferFeeText = input.filter { ch -> ch.isDigit() } },
                                    placeholder = { Text("0", color = TextSubtle) },
                                    modifier = Modifier.width(140.dp),
                                    textStyle = TextStyle(color = TextWhite, fontSize = 14.sp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AccentBlue,
                                        unfocusedBorderColor = DarkCardBorder,
                                        focusedTextColor = TextWhite,
                                        unfocusedTextColor = TextWhite
                                    )
                                )
                            }
                        }
                    } else if (isTransferType && !isAssetTransfer) {
                        // Transfer / Top Up: Dari Akun, Ke Akun, Biaya Transfer
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showSourceAccountPicker = true }
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.CallMade,
                                    contentDescription = null,
                                    tint = ExpenseRed,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Dari akun",
                                    fontSize = 15.sp,
                                    color = TextWhite
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurfaceElevated,
                                border = BorderStroke(1.dp, DarkCardBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = AccentBlue,
                                        modifier = Modifier.padding(end = 6.dp)
                                    ) {
                                        Text(
                                            text = getBankBadge(selectedWallet),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                    Text(
                                        text = selectedWallet,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextWhite
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showTargetAccountPicker = true }
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.CallReceived,
                                    contentDescription = null,
                                    tint = IncomeGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Ke akun",
                                    fontSize = 15.sp,
                                    color = TextWhite
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurfaceElevated,
                                border = BorderStroke(1.dp, DarkCardBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = IncomeGreen,
                                        modifier = Modifier.padding(end = 6.dp)
                                    ) {
                                        Text(
                                            text = getBankBadge(selectedTargetWallet),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                    Text(
                                        text = selectedTargetWallet,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextWhite
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = "Biaya transfer",
                                        fontSize = 15.sp,
                                        color = TextWhite
                                    )
                                    Text(
                                        text = "Tambahkan biaya admin transfer",
                                        fontSize = 11.sp,
                                        color = TextSubtle
                                    )
                                }
                            }
                            Switch(
                                checked = hasTransferFee,
                                onCheckedChange = { hasTransferFee = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = AccentBlue,
                                    uncheckedThumbColor = TextMuted,
                                    uncheckedTrackColor = DarkSurfaceElevated
                                )
                            )
                        }

                        if (hasTransferFee) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 6.dp)
                                    .background(DarkSurfaceElevated, RoundedCornerShape(14.dp))
                                    .border(1.dp, DarkCardBorder, RoundedCornerShape(14.dp))
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Rp",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentBlue
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                BasicTextField(
                                    value = transferFeeText,
                                    onValueChange = { input ->
                                        val digits = input.filter { it.isDigit() }
                                        if (digits.length <= 9) transferFeeText = digits
                                    },
                                    visualTransformation = ThousandsSeparatorVisualTransformation('.'),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    textStyle = TextStyle(
                                        color = TextWhite,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    cursorBrush = SolidColor(AccentBlue),
                                    modifier = Modifier.fillMaxWidth(),
                                    decorationBox = { inner ->
                                        if (transferFeeText.isEmpty()) {
                                            Text(
                                                text = "0 (Nominal biaya transfer)",
                                                fontSize = 14.sp,
                                                color = TextSubtle
                                            )
                                        }
                                        inner()
                                    }
                                )
                            }
                        }
                    } else if (isAssetTransfer) {
                        // Asset: Dari Akun, Dropdown Aset, Biaya Transfer
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showSourceAccountPicker = true }
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.CallMade,
                                    contentDescription = null,
                                    tint = ExpenseRed,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Dari akun",
                                    fontSize = 15.sp,
                                    color = TextWhite
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurfaceElevated,
                                border = BorderStroke(1.dp, DarkCardBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = AccentBlue,
                                        modifier = Modifier.padding(end = 6.dp)
                                    ) {
                                        Text(
                                            text = getBankBadge(selectedWallet),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                    Text(
                                        text = selectedWallet,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextWhite
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        // Dropdown Aset (bisa diklik lagi untuk ganti aset)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isAssetDropdownExpanded = true }
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = null,
                                    tint = IncomeGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = "Aset",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextWhite
                                    )
                                    Text(
                                        text = if (selectedAsset != null) "${selectedAsset?.group} · ${selectedAsset?.platform}" else "Pilih aset dari daftar",
                                        fontSize = 11.sp,
                                        color = TextSubtle
                                    )
                                }
                            }

                            Box {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = DarkSurfaceElevated,
                                    border = BorderStroke(1.dp, if (selectedAsset != null) AccentBlue else DarkCardBorder),
                                    modifier = Modifier.clickable { isAssetDropdownExpanded = true }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (selectedAsset != null) {
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = IncomeGreenSoft,
                                                modifier = Modifier.padding(end = 6.dp)
                                            ) {
                                                Text(
                                                    text = selectedAsset?.platform ?: "Aset",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = IncomeGreen,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                            Text(
                                                text = selectedAsset?.name ?: "Pilih Aset",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = TextWhite
                                            )
                                        } else {
                                            Text(
                                                text = "Pilih Aset",
                                                fontSize = 13.sp,
                                                color = TextMuted
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = "Ganti Aset",
                                            tint = AccentBlue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = isAssetDropdownExpanded,
                                    onDismissRequest = { isAssetDropdownExpanded = false },
                                    modifier = Modifier
                                        .background(DarkSurfaceElevated)
                                        .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                                        .heightIn(max = 280.dp)
                                ) {
                                    if (assets.isEmpty()) {
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = "Belum ada aset ditambahkan",
                                                    fontSize = 13.sp,
                                                    color = TextMuted
                                                )
                                            },
                                            onClick = {
                                                isAssetDropdownExpanded = false
                                                showAssetPicker = true
                                            }
                                        )
                                    } else {
                                        assets.forEach { asset ->
                                            val isSelected = selectedAsset?.id == asset.id
                                            DropdownMenuItem(
                                                text = {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(vertical = 4.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            modifier = Modifier.weight(1f)
                                                        ) {
                                                            Surface(
                                                                shape = RoundedCornerShape(4.dp),
                                                                color = if (isSelected) AccentBlue else DarkSurface,
                                                                border = BorderStroke(1.dp, if (isSelected) AccentBlue else DarkCardBorder),
                                                                modifier = Modifier.padding(end = 8.dp)
                                                            ) {
                                                                Text(
                                                                    text = asset.platform,
                                                                    fontSize = 9.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = Color.White,
                                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                                )
                                                            }
                                                            Column {
                                                                Text(
                                                                    text = asset.name,
                                                                    fontSize = 13.sp,
                                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                                    color = TextWhite
                                                                )
                                                                Text(
                                                                    text = "${asset.group} · ${CurrencyHelper.formatRupiah(asset.value)}",
                                                                    fontSize = 11.sp,
                                                                    color = TextMuted
                                                                )
                                                            }
                                                        }
                                                        if (isSelected) {
                                                            Spacer(modifier = Modifier.width(8.dp))
                                                            Icon(
                                                                imageVector = Icons.Default.Check,
                                                                contentDescription = "Terpilih",
                                                                tint = AccentBlue,
                                                                modifier = Modifier.size(18.dp)
                                                            )
                                                        }
                                                    }
                                                },
                                                onClick = {
                                                    selectedAsset = asset
                                                    isAssetDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = "Biaya transfer / admin",
                                        fontSize = 15.sp,
                                        color = TextWhite
                                    )
                                    Text(
                                        text = "Tambahkan biaya administrasi",
                                        fontSize = 11.sp,
                                        color = TextSubtle
                                    )
                                }
                            }
                            Switch(
                                checked = hasTransferFee,
                                onCheckedChange = { hasTransferFee = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = AccentBlue,
                                    uncheckedThumbColor = TextMuted,
                                    uncheckedTrackColor = DarkSurfaceElevated
                                )
                            )
                        }

                        if (hasTransferFee) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 6.dp)
                                    .background(DarkSurfaceElevated, RoundedCornerShape(14.dp))
                                    .border(1.dp, DarkCardBorder, RoundedCornerShape(14.dp))
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Rp",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentBlue
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                BasicTextField(
                                    value = transferFeeText,
                                    onValueChange = { input ->
                                        val digits = input.filter { it.isDigit() }
                                        if (digits.length <= 9) transferFeeText = digits
                                    },
                                    visualTransformation = ThousandsSeparatorVisualTransformation('.'),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    textStyle = TextStyle(
                                        color = TextWhite,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    cursorBrush = SolidColor(AccentBlue),
                                    modifier = Modifier.fillMaxWidth(),
                                    decorationBox = { inner ->
                                        if (transferFeeText.isEmpty()) {
                                            Text(
                                                text = "0 (Nominal biaya transfer)",
                                                fontSize = 14.sp,
                                                color = TextSubtle
                                            )
                                        }
                                        inner()
                                    }
                                )
                            }
                        }
                    } else {
                        // Mode Biasa
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showSourceAccountPicker = true }
                                .padding(horizontal = 20.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Akun",
                                    fontSize = 15.sp,
                                    color = TextWhite
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = AccentBlue,
                                    modifier = Modifier.padding(end = 6.dp)
                                ) {
                                    Text(
                                        text = getBankBadge(selectedWallet),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                                Text(
                                    text = selectedWallet,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextWhite
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Row 2: Kategori
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showCategoryPicker = true }
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Category,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Kategori",
                                    fontSize = 15.sp,
                                    color = TextWhite
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurfaceElevated,
                                border = BorderStroke(1.dp, DarkCardBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = getCategoryIcon(selectedCategory),
                                        contentDescription = null,
                                        tint = AccentBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = selectedCategory,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextWhite
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Row: Hari Ini (Date & Time)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true }
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = selectedDayLabel,
                                fontSize = 15.sp,
                                color = TextWhite
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurfaceElevated,
                                border = BorderStroke(1.dp, DarkCardBorder),
                                modifier = Modifier.clickable { showTimePicker = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = "Pilih Jam",
                                        tint = AccentBlue,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = selectedTime,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextWhite
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurfaceElevated,
                                border = BorderStroke(1.dp, DarkCardBorder),
                                modifier = Modifier.clickable {
                                    val cal = Calendar.getInstance()
                                    cal.add(Calendar.DAY_OF_YEAR, -1)
                                    selectedDayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
                                    selectedMonthIndex = cal.get(Calendar.MONTH) + 1
                                    selectedYear = cal.get(Calendar.YEAR)
                                    val dayNames = listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")
                                    val dayName = dayNames[(cal.get(Calendar.DAY_OF_WEEK) - 1 + 7) % 7]
                                    selectedDayOfWeek = dayName
                                    selectedDayLabel = "Kemarin, $selectedDayOfMonth/$selectedMonthIndex"
                                }
                            ) {
                                Text(
                                    text = "Yesterday ?",
                                    fontSize = 12.sp,
                                    color = TextMuted,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    // Row: Ulangi (Recurrence)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showRecurrencePicker = true }
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Repeat,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Ulangi",
                                fontSize = 15.sp,
                                color = TextWhite
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = selectedRecurrence,
                                fontSize = 13.sp,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Autocomplete Note matching logic for Edit
                    val previousNotes = remember(allTransactions) {
                        val list = allTransactions
                            .mapNotNull { it.note.trim().takeIf { n -> n.isNotEmpty() } }
                            .distinct()
                        if (list.isNotEmpty()) list else listOf(
                            "Gaji bulanan", "Makan siang", "Beli bensin", "Belanja dapur", "Ngopi santai", "Token listrik", "Makan malam", "Bayar tagihan", "Servis motor", "Pulsa & kuota"
                        )
                    }

                    val noteSuggestions = remember(previousNotes, noteText) {
                        val query = noteText.trim().lowercase()
                        if (query.isEmpty()) {
                            emptyList()
                        } else {
                            val lastWord = query.split(" ").lastOrNull()?.trim() ?: ""
                            previousNotes.filter { candidate ->
                                val candLower = candidate.lowercase()
                                if (candLower == query) {
                                    false
                                } else if (candLower.startsWith(query) || candLower.contains(query)) {
                                    true
                                } else if (lastWord.isNotEmpty() && candLower.split(" ", "-", "_").any { it.startsWith(lastWord) || (lastWord.length >= 2 && it.contains(lastWord)) }) {
                                    true
                                } else {
                                    false
                                }
                            }.sortedWith(
                                compareBy<String> { !it.lowercase().startsWith(query) }
                                    .thenBy { !it.lowercase().contains(query) }
                            ).take(5)
                        }
                    }

                    // Row: Tulis catatan with Autocomplete Hint Dropdown
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = if (noteText.isNotEmpty()) AccentBlue else TextMuted,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            BasicTextField(
                                value = noteText,
                                onValueChange = { input ->
                                    noteText = input
                                    isNoteDropdownExpanded = input.trim().isNotEmpty()
                                },
                                textStyle = TextStyle(
                                    color = TextWhite,
                                    fontSize = 15.sp
                                ),
                                cursorBrush = SolidColor(AccentBlue),
                                modifier = Modifier.weight(1f),
                                decorationBox = { innerTextField ->
                                    if (noteText.isEmpty()) {
                                        Text(
                                            text = "Tulis catatan",
                                            fontSize = 15.sp,
                                            color = TextSubtle
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                            if (noteText.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        noteText = ""
                                        isNoteDropdownExpanded = false
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Hapus catatan",
                                        tint = TextSubtle,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        // Real-time Autocomplete Hint Dropdown
                        DropdownMenu(
                            expanded = isNoteDropdownExpanded && noteSuggestions.isNotEmpty() && noteText.trim().isNotEmpty(),
                            onDismissRequest = { isNoteDropdownExpanded = false },
                            properties = PopupProperties(focusable = false),
                            modifier = Modifier
                                .fillMaxWidth(0.88f)
                                .background(DarkSurfaceElevated)
                                .border(1.dp, AccentBlue.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = AccentBlue,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Rekomendasi Catatan Terdahulu",
                                    fontSize = 11.sp,
                                    color = AccentBlue,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            HorizontalDivider(color = DarkCardBorder, thickness = 0.5.dp)

                            noteSuggestions.forEach { suggestion ->
                                DropdownMenuItem(
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.History,
                                            contentDescription = null,
                                            tint = TextMuted,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    text = {
                                        Text(
                                            text = suggestion,
                                            fontSize = 14.sp,
                                            color = TextWhite,
                                            fontWeight = FontWeight.Medium
                                        )
                                    },
                                    onClick = {
                                        noteText = suggestion
                                        isNoteDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Row: Label
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showManageLabelsDialog = true }
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Label,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Label",
                                    fontSize = 15.sp,
                                    color = TextWhite
                                )
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Label pills
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(availableLabels) { labelName ->
                                val isSelected = selectedLabels.contains(labelName)
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) AccentBlue else DarkSurfaceElevated,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) AccentBlue else DarkCardBorder
                                    ),
                                    modifier = Modifier.clickable {
                                        selectedLabels = if (isSelected) {
                                            selectedLabels - labelName
                                        } else {
                                            selectedLabels + labelName
                                        }
                                    }
                                ) {
                                    Text(
                                        text = labelName,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else TextMuted,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // In-form Delete Button
                        Button(
                            onClick = { showDeleteConfirmation = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ExpenseRed.copy(alpha = 0.15f)
                            ),
                            border = BorderStroke(1.dp, ExpenseRed.copy(alpha = 0.5f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = ExpenseRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Hapus Transaksi Ini",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = ExpenseRed
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = {
                Text(
                    text = "Hapus Transaksi?",
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin menghapus transaksi ini? Saldo rekening akan disesuaikan secara otomatis.",
                    color = TextMuted,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmation = false
                        viewModel.deleteTransaction(transaction.id)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
                ) {
                    Text("Hapus", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Batal", color = TextMuted)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }

    // Sub-dialogs for Edit
    if (showCategoryPicker) {
        CategoryPickerSubDialog(
            currentCategory = selectedCategory,
            currentSubcategory = selectedSubcategory,
            categories = allCategories,
            onCategorySelected = {
                selectedCategory = it
                selectedSubcategory = ""
                showCategoryPicker = false
            },
            onCategoryAndSubcategorySelected = { cat, subcat ->
                selectedCategory = cat
                selectedSubcategory = subcat
                showCategoryPicker = false
            },
            onDismiss = { showCategoryPicker = false }
        )
    }

    if (showSourceAccountPicker) {
        AccountPickerSubDialog(
            wallets = wallets.map { it.name },
            currentAccount = selectedWallet,
            title = "Pilih Akun Asal",
            onAccountSelected = {
                selectedWallet = it
                if (selectedTargetWallet == it) {
                    selectedTargetWallet = wallets.firstOrNull { w -> w.name != it }?.name ?: ""
                }
                showSourceAccountPicker = false
            },
            onDismiss = { showSourceAccountPicker = false }
        )
    }

    if (showTargetAccountPicker) {
        AccountPickerSubDialog(
            wallets = wallets.map { it.name },
            currentAccount = selectedTargetWallet,
            title = "Pilih Akun Tujuan",
            onAccountSelected = {
                selectedTargetWallet = it
                showTargetAccountPicker = false
            },
            onDismiss = { showTargetAccountPicker = false }
        )
    }

    if (showAssetPicker) {
        AssetPickerSubDialog(
            assets = assets,
            selectedAsset = selectedAsset,
            onAssetSelected = {
                selectedAsset = it
                showAssetPicker = false
            },
            onDismiss = { showAssetPicker = false }
        )
    }

    if (showDatePicker) {
        DatePickerSubDialog(
            initialDay = selectedDayOfMonth,
            initialMonth = selectedMonthIndex,
            initialYear = selectedYear,
            onDateSelected = { day, month, year, dayName, label ->
                selectedDayOfMonth = day
                selectedMonthIndex = month
                selectedYear = year
                selectedDayOfWeek = dayName
                selectedDayLabel = label
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    if (showTimePicker) {
        TimePickerSubDialog(
            currentTime = selectedTime,
            onTimeSelected = {
                selectedTime = it
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }

    if (showRecurrencePicker) {
        RecurrencePickerSubDialog(
            currentRecurrence = selectedRecurrence,
            onRecurrenceSelected = {
                selectedRecurrence = it
                showRecurrencePicker = false
            },
            onDismiss = { showRecurrencePicker = false }
        )
    }

    if (showManageLabelsDialog) {
        ManageLabelsDialog(
            viewModel = viewModel,
            onDismiss = { showManageLabelsDialog = false }
        )
    }

    if (showDebtPicker) {
        AlertDialog(
            onDismissRequest = { showDebtPicker = false },
            containerColor = DarkSurface,
            title = {
                Text(
                    text = "Pilih Utang untuk Dibayar",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.height(280.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val activeDebts = debts.filter { it.remainingAmount > 0 }
                    if (activeDebts.isEmpty()) {
                        item {
                            Text(
                                text = "Tidak ada utang aktif yang ditemukan.",
                                color = TextMuted,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        items(activeDebts) { d ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (selectedDebt?.id == d.id) AccentBlue.copy(alpha = 0.2f) else DarkSurfaceElevated,
                                border = BorderStroke(1.dp, if (selectedDebt?.id == d.id) AccentBlue else DarkCardBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedDebt = d
                                        showDebtPicker = false
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = d.name,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextWhite
                                        )
                                        Text(
                                            text = "Tempo: ${d.dueDate.ifBlank { "-" }}",
                                            fontSize = 11.sp,
                                            color = TextMuted
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = CurrencyHelper.formatRupiah(d.remainingAmount),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFF6B4A)
                                        )
                                        Text(
                                            text = "Sisa Utang",
                                            fontSize = 10.sp,
                                            color = TextSubtle
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showDebtPicker = false }) {
                    Text("Tutup", color = TextMuted)
                }
            }
        )
    }
}
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun BatchLabelDialog(
    selectedCount: Int,
    availableLabels: List<String>,
    onDismiss: () -> Unit,
    onSelectLabel: (String) -> Unit
) {
    var customLabelText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.dp, DarkCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Ubah Label",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "$selectedCount transaksi dipilih",
                            fontSize = 12.sp,
                            color = AccentBlue
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Pilih label yang sudah ada:",
                    fontSize = 13.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (availableLabels.isEmpty()) {
                    Text(
                        text = "Belum ada label tersimpan. Buat label baru di bawah.",
                        fontSize = 12.sp,
                        color = TextSubtle,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                } else {
                    androidx.compose.foundation.layout.FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        availableLabels.forEach { labelName ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = DarkSurfaceElevated,
                                border = BorderStroke(1.dp, DarkCardBorder),
                                modifier = Modifier.clickable {
                                    onSelectLabel(labelName)
                                    onDismiss()
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Label,
                                        contentDescription = null,
                                        tint = TrophyYellow,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = labelName,
                                        fontSize = 12.5.sp,
                                        color = TextWhite,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Custom label input
                Text(
                    text = "Atau tulis label baru:",
                    fontSize = 13.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = customLabelText,
                    onValueChange = { customLabelText = it },
                    placeholder = { Text("Nama label baru", color = TextSubtle) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = DarkCardBorder
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Option to remove label
                    Button(
                        onClick = {
                            onSelectLabel("")
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Tanpa Label", color = TextMuted, fontSize = 13.sp)
                    }

                    // Apply custom button
                    Button(
                        onClick = {
                            if (customLabelText.isNotBlank()) {
                                onSelectLabel(customLabelText.trim())
                                onDismiss()
                            }
                        },
                        enabled = customLabelText.isNotBlank(),
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Terapkan", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ConfirmDeleteTransactionsDialog(
    count: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = BorderStroke(1.dp, DarkCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(ExpenseRedSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = ExpenseRed,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Hapus Transaksi",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Apakah Anda yakin ingin menghapus $count transaksi yang dipilih? Saldo dompet terkait akan disesuaikan secara otomatis.",
                    fontSize = 13.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Batal", color = TextWhite)
                    }

                    Button(
                        onClick = {
                            onConfirm()
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Hapus", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryPickerSubDialog(
    currentCategory: String,
    currentSubcategory: String = "",
    categories: List<MainCategory> = emptyList(),
    onCategorySelected: (String) -> Unit = {},
    onCategoryAndSubcategorySelected: (String, String) -> Unit = { cat, _ -> onCategorySelected(cat) },
    onDismiss: () -> Unit
) {
    val fallbackExpense = listOf(
        "Other", "Bills & Fees", "Market", "Food & Dining",
        "Shopping", "Transport", "Kesehatan", "Pendidikan", "Hiburan"
    )
    val fallbackIncome = listOf(
        "Salary", "Extra Income", "Bonus", "Investasi", "Pemberian"
    )
    val transferCategories = listOf(
        "Transfer", "Asset", "Pembayaran Utang"
    )

    val initialTab = when {
        currentCategory in transferCategories || currentCategory == "Aset" || currentCategory == "Transfer Antar Bank" || currentCategory.equals("Bayar Utang", ignoreCase = true) || currentCategory.equals("Pembayaran Utang", ignoreCase = true) -> 2
        categories.any { it.type == TransactionType.INCOME && it.name.equals(currentCategory, ignoreCase = true) } || fallbackIncome.contains(currentCategory) -> 1
        else -> 0
    }
    var activeTab by remember { mutableIntStateOf(initialTab) }
    var expandedCategoryIds by remember { mutableStateOf(setOf<String>()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pilih Kategori",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Switcher
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurfaceElevated, RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("Pengeluaran", "Pemasukan", "Transfer").forEachIndexed { idx, title ->
                        val isSelected = activeTab == idx
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) {
                                when (idx) {
                                    0 -> ExpenseRed
                                    1 -> IncomeGreen
                                    else -> AccentBlue
                                }
                            } else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { activeTab = idx }
                        ) {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else TextMuted,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val customCatsForTab = when (activeTab) {
                    0 -> categories.filter { it.type == TransactionType.EXPENSE && it.isVisible }
                    1 -> categories.filter { it.type == TransactionType.INCOME && it.isVisible }
                    else -> emptyList()
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (activeTab == 2) {
                        // Transfer
                        items(transferCategories) { cat ->
                            val isSelected = currentCategory.equals(cat, ignoreCase = true) ||
                                (cat == "Transfer" && currentCategory.equals("Transfer Antar Bank", ignoreCase = true)) ||
                                (cat == "Asset" && (currentCategory.equals("Aset", ignoreCase = true) || currentCategory.equals("Transfer Aset", ignoreCase = true))) ||
                                (cat == "Pembayaran Utang" && currentCategory.equals("Bayar Utang", ignoreCase = true))

                            val displayCatName = when (cat) {
                                "Asset" -> "Transfer Aset"
                                "Pembayaran Utang" -> "Pembayaran Utang"
                                else -> "Transfer Antar Akun"
                            }
                            val displaySubtext = when (cat) {
                                "Asset" -> "Investasi, emas, saham, deposito"
                                "Pembayaran Utang" -> "Cicilan, pinjaman, pelunasan utang"
                                else -> "Kirim uang antar dompet / bank"
                            }
                            val catIcon = when (cat) {
                                "Asset" -> Icons.AutoMirrored.Filled.TrendingUp
                                "Pembayaran Utang" -> Icons.Default.Handshake
                                else -> Icons.Default.SwapHoriz
                            }
                            val catColor = when (cat) {
                                "Asset" -> Color(0xFF10B981)
                                "Pembayaran Utang" -> Color(0xFFFF5722)
                                else -> AccentBlue
                            }

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) catColor.copy(alpha = 0.2f) else DarkSurfaceElevated,
                                border = BorderStroke(1.dp, if (isSelected) catColor else DarkCardBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onCategorySelected(cat)
                                        onCategoryAndSubcategorySelected(cat, "")
                                        onDismiss()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(if (isSelected) catColor else catColor.copy(alpha = 0.2f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = catIcon,
                                                contentDescription = null,
                                                tint = if (isSelected) Color.White else catColor,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = displayCatName,
                                                fontSize = 14.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                                color = TextWhite
                                            )
                                            Text(
                                                text = displaySubtext,
                                                fontSize = 11.sp,
                                                color = TextMuted
                                            )
                                        }
                                    }
                                    if (isSelected) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = catColor, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    } else if (customCatsForTab.isNotEmpty()) {
                        // Display hierarchical categories with collapsible subcategories
                        items(customCatsForTab) { cat ->
                            val isParentSelected = currentCategory.equals(cat.name, ignoreCase = true)
                            val visibleSubs = cat.subcategories.filter { it.isVisible }
                            val hasSubcategories = visibleSubs.isNotEmpty()
                            val isExpanded = expandedCategoryIds.contains(cat.id)

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(DarkSurfaceElevated, RoundedCornerShape(14.dp))
                                    .border(1.dp, if (isParentSelected) AccentBlue.copy(alpha = 0.6f) else DarkCardBorder, RoundedCornerShape(14.dp))
                                    .padding(8.dp)
                            ) {
                                // Parent category header row
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onCategorySelected(cat.name)
                                            onCategoryAndSubcategorySelected(cat.name, "")
                                            onDismiss()
                                        }
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val itemCatColor = CategoryIconHelper.getCategoryColor(cat.name, cat.type)
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .background(if (isParentSelected) itemCatColor else itemCatColor.copy(alpha = 0.2f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = CategoryIconHelper.getCategoryIcon(cat.name, cat.iconName),
                                                contentDescription = null,
                                                tint = if (isParentSelected) Color.White else itemCatColor,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = cat.name,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextWhite
                                            )
                                            if (hasSubcategories) {
                                                Text(
                                                    text = "${visibleSubs.size} subkategori",
                                                    fontSize = 10.sp,
                                                    color = TextSubtle
                                                )
                                            }
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (isParentSelected && currentSubcategory.isBlank()) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = AccentBlue,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                        }

                                        // Dropdown toggle to expand/hide subcategories so they don't consume space
                                        if (hasSubcategories) {
                                            Surface(
                                                shape = CircleShape,
                                                color = if (isExpanded) DarkCardBorder else Color.Transparent,
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .clickable {
                                                        expandedCategoryIds = if (isExpanded) {
                                                            expandedCategoryIds - cat.id
                                                        } else {
                                                            expandedCategoryIds + cat.id
                                                        }
                                                    }
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                                        contentDescription = if (isExpanded) "Sembunyikan subkategori" else "Tampilkan subkategori",
                                                        tint = TextMuted,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // Visible Subcategories: Hidden by default, only shown if isExpanded is true
                                if (hasSubcategories && isExpanded) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 12.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        visibleSubs.forEach { sub ->
                                            val isSubSelected = isParentSelected && currentSubcategory.equals(sub.name, ignoreCase = true)
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = if (isSubSelected) AccentBlue.copy(alpha = 0.25f) else Color(0x15FFFFFF),
                                                border = BorderStroke(1.dp, if (isSubSelected) AccentBlue else Color.Transparent),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        onCategorySelected(cat.name)
                                                        onCategoryAndSubcategorySelected(cat.name, sub.name)
                                                        onDismiss()
                                                    }
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        val subColor = CategoryIconHelper.getSubcategoryColor(cat.name, sub.name, cat.type)
                                                        val subIcon = CategoryIconHelper.getCategoryIcon(sub.name, sub.iconName)
                                                        Box(
                                                            modifier = Modifier
                                                                .size(22.dp)
                                                                .clip(RoundedCornerShape(6.dp))
                                                                .background(subColor.copy(alpha = 0.18f)),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Icon(
                                                                 imageVector = subIcon,
                                                                 contentDescription = sub.name,
                                                                 tint = subColor,
                                                                 modifier = Modifier.size(13.dp)
                                                            )
                                                        }
                                                        Spacer(modifier = Modifier.width(10.dp))
                                                        Text(
                                                            text = sub.name,
                                                            fontSize = 12.sp,
                                                            fontWeight = if (isSubSelected) FontWeight.Bold else FontWeight.Normal,
                                                            color = if (isSubSelected) AccentBlue else TextWhite
                                                        )
                                                    }
                                                    if (isSubSelected) {
                                                        Icon(
                                                            imageVector = Icons.Default.Check,
                                                            contentDescription = null,
                                                            tint = AccentBlue,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Fallback list
                        val fallbackList = if (activeTab == 0) fallbackExpense else fallbackIncome
                        items(fallbackList) { cat ->
                            val isSelected = currentCategory == cat
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) Color(0x332563EB) else DarkSurfaceElevated,
                                border = BorderStroke(1.dp, if (isSelected) AccentBlue else DarkCardBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onCategorySelected(cat)
                                        onCategoryAndSubcategorySelected(cat, "")
                                        onDismiss()
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(if (isSelected) AccentBlue else Color(0x22FFFFFF), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = getCategoryIcon(cat),
                                                contentDescription = null,
                                                tint = if (isSelected) Color.White else TextWhite,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(text = cat, fontSize = 14.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = TextWhite)
                                    }
                                    if (isSelected) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(18.dp))
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

@Composable
fun AccountPickerSubDialog(
    wallets: List<String>,
    currentAccount: String,
    onAccountSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    title: String = "Pilih Akun / Dompet"
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(wallets) { walletName ->
                        val isSelected = currentAccount == walletName
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) Color(0x332563EB) else DarkSurfaceElevated,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) AccentBlue else DarkCardBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onAccountSelected(walletName) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    InstitutionLogoBadge(
                                        walletName = walletName,
                                        size = 26.dp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = walletName,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = TextWhite
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = AccentBlue,
                                        modifier = Modifier.size(18.dp)
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

@Composable
fun AssetPickerSubDialog(
    assets: List<AssetItem>,
    selectedAsset: AssetItem?,
    onAssetSelected: (AssetItem) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pilih Aset",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (assets.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada aset yang ditambahkan.",
                            fontSize = 14.sp,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 340.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(assets) { asset ->
                            val isSelected = selectedAsset?.id == asset.id
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) Color(0x332563EB) else DarkSurfaceElevated,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) AccentBlue else DarkCardBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onAssetSelected(asset) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .background(
                                                    if (isSelected) AccentBlue else Color(0x22FFFFFF),
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                                contentDescription = null,
                                                tint = if (isSelected) Color.White else IncomeGreen,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = asset.name,
                                                fontSize = 14.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = TextWhite
                                            )
                                            Text(
                                                text = "${asset.group} · ${asset.platform}",
                                                fontSize = 11.sp,
                                                color = TextMuted
                                            )
                                        }
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = CurrencyHelper.formatRupiah(asset.value),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = IncomeGreen
                                        )
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = AccentBlue,
                                                modifier = Modifier.size(16.dp)
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
}

@Composable
fun DatePickerSubDialog(
    initialDay: Int,
    initialMonth: Int,
    initialYear: Int,
    onDateSelected: (Int, Int, Int, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var displayYear by remember { mutableIntStateOf(initialYear) }
    var displayMonth by remember { mutableIntStateOf(initialMonth) }
    var pickedDay by remember { mutableIntStateOf(initialDay) }
    var dragOffsetX by remember { mutableFloatStateOf(0f) }

    val monthNames = listOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )
    val dayNames = listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")

    fun goToPrevMonth() {
        if (displayMonth == 1) {
            displayMonth = 12
            displayYear -= 1
        } else {
            displayMonth -= 1
        }
    }

    fun goToNextMonth() {
        if (displayMonth == 12) {
            displayMonth = 1
            displayYear += 1
        } else {
            displayMonth += 1
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(displayMonth, displayYear) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (dragOffsetX < -40f) {
                                goToNextMonth()
                            } else if (dragOffsetX > 40f) {
                                goToPrevMonth()
                            }
                            dragOffsetX = 0f
                        },
                        onDragCancel = { dragOffsetX = 0f },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            dragOffsetX += dragAmount
                        }
                    )
                }
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Top Header: Month, Year, and Navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Pilih Tanggal",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "${monthNames[displayMonth - 1]} $displayYear",
                            fontSize = 14.sp,
                            color = AccentBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { goToPrevMonth() },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Bulan Sebelumnya",
                                tint = TextWhite
                            )
                        }

                        IconButton(
                            onClick = { goToNextMonth() },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Bulan Berikutnya",
                                tint = TextWhite
                            )
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(34.dp)) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Tutup",
                                tint = TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Days of week row (Min, Sen, Sel, Rab, Kam, Jum, Sab)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    dayNames.forEach { dayName ->
                        Text(
                            text = dayName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Animated Calendar Grid (Slides left/right when month changes)
                AnimatedContent(
                    targetState = displayMonth to displayYear,
                    transitionSpec = {
                        val (targetM, targetY) = targetState
                        val (initialM, initialY) = initialState
                        val isNext = (targetY > initialY) || (targetY == initialY && targetM > initialM) || (initialM == 12 && targetM == 1)
                        if (isNext) {
                            slideInHorizontally { width -> width / 3 } + fadeIn() togetherWith
                                    slideOutHorizontally { width -> -width / 3 } + fadeOut()
                        } else {
                            slideInHorizontally { width -> -width / 3 } + fadeIn() togetherWith
                                    slideOutHorizontally { width -> width / 3 } + fadeOut()
                        }
                    },
                    label = "calendar_days_slide"
                ) { (curMonth, curYear) ->
                    val cal = remember(curYear, curMonth) {
                        Calendar.getInstance().apply {
                            set(Calendar.YEAR, curYear)
                            set(Calendar.MONTH, curMonth - 1)
                            set(Calendar.DAY_OF_MONTH, 1)
                        }
                    }
                    val firstDay = cal.get(Calendar.DAY_OF_WEEK)
                    val daysCount = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                    val totalCells = (firstDay - 1) + daysCount
                    val totalRows = (totalCells + 6) / 7

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (row in 0 until totalRows) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                for (col in 0..6) {
                                    val cellIndex = row * 7 + col
                                    val dayNum = cellIndex - (firstDay - 1) + 1

                                    if (dayNum in 1..daysCount) {
                                        val isSelected = (pickedDay == dayNum &&
                                                curMonth == initialMonth &&
                                                curYear == initialYear)

                                        val todayCal = Calendar.getInstance()
                                        val isToday = (dayNum == todayCal.get(Calendar.DAY_OF_MONTH) &&
                                                curMonth == todayCal.get(Calendar.MONTH) + 1 &&
                                                curYear == todayCal.get(Calendar.YEAR))

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isSelected) AccentBlue
                                                    else if (isToday) Color(0x332563EB)
                                                    else Color.Transparent
                                                )
                                                .clickable {
                                                    pickedDay = dayNum
                                                    displayMonth = curMonth
                                                    displayYear = curYear
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "$dayNum",
                                                fontSize = 13.sp,
                                                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) Color.White else if (isToday) AccentBlue else TextWhite
                                            )
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.weight(1f).size(38.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Swipe Gesture Hint
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = TextSubtle,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Geser kalender untuk ganti bulan",
                        fontSize = 11.sp,
                        color = TextSubtle,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = TextSubtle,
                        modifier = Modifier.size(13.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val todayCal = Calendar.getInstance()
                            val tDay = todayCal.get(Calendar.DAY_OF_MONTH)
                            val tMonth = todayCal.get(Calendar.MONTH) + 1
                            val tYear = todayCal.get(Calendar.YEAR)
                            val dayIdx = (todayCal.get(Calendar.DAY_OF_WEEK) - 1 + 7) % 7
                            onDateSelected(tDay, tMonth, tYear, dayNames[dayIdx], "Hari Ini")
                        },
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated)
                    ) {
                        Text("Hari Ini", fontSize = 13.sp, color = TextWhite)
                    }

                    Button(
                        onClick = {
                            val targetCal = Calendar.getInstance().apply {
                                set(Calendar.YEAR, displayYear)
                                set(Calendar.MONTH, displayMonth - 1)
                                set(Calendar.DAY_OF_MONTH, pickedDay)
                            }
                            val dayIdx = (targetCal.get(Calendar.DAY_OF_WEEK) - 1 + 7) % 7
                            val dayName = dayNames[dayIdx]
                            val todayCal = Calendar.getInstance()
                            val label = if (pickedDay == todayCal.get(Calendar.DAY_OF_MONTH) &&
                                displayMonth == todayCal.get(Calendar.MONTH) + 1 &&
                                displayYear == todayCal.get(Calendar.YEAR)
                            ) {
                                "Hari Ini"
                            } else {
                                "$pickedDay ${monthNames[displayMonth - 1].take(3)} $displayYear"
                            }
                            onDateSelected(pickedDay, displayMonth, displayYear, dayName, label)
                        },
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                    ) {
                        Text("Pilih Tanggal", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun TimePickerSubDialog(
    currentTime: String,
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val parts = currentTime.split(":")
    var hour by remember { mutableIntStateOf(parts.getOrNull(0)?.toIntOrNull() ?: 18) }
    var minute by remember { mutableIntStateOf(parts.getOrNull(1)?.toIntOrNull() ?: 51) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Pilih Waktu", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hour Box
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = DarkSurfaceElevated,
                        border = BorderStroke(1.dp, AccentBlue),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            IconButton(onClick = { hour = (hour + 1) % 24 }, modifier = Modifier.size(30.dp)) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "+", tint = TextWhite)
                            }
                            Text(
                                text = String.format(Locale.getDefault(), "%02d", hour),
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            IconButton(onClick = { hour = (hour - 1 + 24) % 24 }, modifier = Modifier.size(30.dp)) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "-", tint = TextWhite)
                            }
                            Text("Jam", fontSize = 11.sp, color = TextMuted)
                        }
                    }

                    Text(":", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = TextWhite)

                    // Minute Box
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = DarkSurfaceElevated,
                        border = BorderStroke(1.dp, AccentBlue),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            IconButton(onClick = { minute = (minute + 1) % 60 }, modifier = Modifier.size(30.dp)) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "+", tint = TextWhite)
                            }
                            Text(
                                text = String.format(Locale.getDefault(), "%02d", minute),
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            IconButton(onClick = { minute = (minute - 1 + 60) % 60 }, modifier = Modifier.size(30.dp)) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "-", tint = TextWhite)
                            }
                            Text("Menit", fontSize = 11.sp, color = TextMuted)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                            onTimeSelected(sdf.format(Date()))
                        },
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated)
                    ) {
                        Text("Jam Sekarang", fontSize = 12.sp, color = TextWhite)
                    }

                    Button(
                        onClick = {
                            onTimeSelected(String.format(Locale.getDefault(), "%02d:%02d", hour, minute))
                        },
                        modifier = Modifier.weight(1f).height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                    ) {
                        Text("Simpan", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun RecurrencePickerSubDialog(
    currentRecurrence: String,
    onRecurrenceSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val options = listOf(
        "Tidak pernah",
        "Setiap hari",
        "Setiap 2 minggu",
        "Setiap bulan",
        "Setiap tahun"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pengulangan",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                options.forEach { opt ->
                    val isSelected = currentRecurrence.equals(opt, ignoreCase = true)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0x332563EB) else DarkSurfaceElevated,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) AccentBlue else DarkCardBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onRecurrenceSelected(opt) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = opt,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = TextWhite
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = AccentBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ManageLabelsDialog(
    viewModel: FinanceViewModel,
    onDismiss: () -> Unit
) {
    val labels by viewModel.labels.collectAsState()
    var newLabelText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Kelola Label Transaksi",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = TextMuted)
                    }
                }

                Text(
                    text = "Label akan muncul di menu Tambah Transaksi",
                    fontSize = 12.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Add new label row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newLabelText,
                        onValueChange = { newLabelText = it },
                        placeholder = { Text("Nama label baru...", color = TextSubtle, fontSize = 13.sp) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedContainerColor = DarkSurfaceElevated,
                            unfocusedContainerColor = DarkSurfaceElevated
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newLabelText.isNotBlank()) {
                                viewModel.addLabel(newLabelText.trim())
                                newLabelText = ""
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                        modifier = Modifier.height(52.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Label", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(labels) { labelName ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurfaceElevated,
                            border = BorderStroke(1.dp, DarkCardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Label,
                                        contentDescription = null,
                                        tint = AccentBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = labelName,
                                        fontSize = 14.sp,
                                        color = TextWhite
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.deleteLabel(labelName) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Hapus",
                                        tint = ExpenseRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                ) {
                    Text("Selesai", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun GamificationRoadmapDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Target & Roadmap Finansial", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                val levels = listOf(
                    Triple("Level 1: Dana Darurat Pemula", "Target Rp5.000.000", true),
                    Triple("Level 2: Dana cadangan pertama", "Target Rp17.673.149 (88% selesai)", true),
                    Triple("Level 3: Bebas Utang Konsumtif", "Target Pelunasan Rp8.050.000", false),
                    Triple("Level 4: Investor Mandiri", "Target Investasi Rp50.000.000", false)
                )

                levels.forEachIndexed { i, (name, target, isDone) ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (i == 1) Color(0x2EF59E0B) else DarkSurfaceElevated,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (isDone) TrophyYellow else TextSubtle),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WorkspacePremium,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                Text(target, fontSize = 11.sp, color = if (i == 1) TrophyYellowLight else TextMuted)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetManagementDialog(
    viewModel: FinanceViewModel,
    onDismiss: () -> Unit
) {
    val budgets by viewModel.budgets.collectAsState()
    var newCat by remember { mutableStateOf("") }
    var newAlloc by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Kelola Anggaran", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(modifier = Modifier.height(220.dp)) {
                    items(budgets) { b ->
                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(b.category, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                                Text(
                                    "${CurrencyHelper.formatRupiah(b.spent)} / ${CurrencyHelper.formatRupiah(b.allocated)}",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { b.percentage },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = if (b.percentage > 0.85f) ExpenseRed else AccentBlue,
                                trackColor = DarkSurfaceElevated
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Add quick budget
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newCat,
                        onValueChange = { newCat = it },
                        placeholder = { Text("Kategori", color = TextSubtle, fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = DarkSurfaceElevated,
                            unfocusedContainerColor = DarkSurfaceElevated
                        )
                    )
                    OutlinedTextField(
                        value = newAlloc,
                        onValueChange = { newAlloc = it.filter { ch -> ch.isDigit() } },
                        placeholder = { Text("Nominal", color = TextSubtle, fontSize = 11.sp) },
                        visualTransformation = ThousandsSeparatorVisualTransformation('.'),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = DarkSurfaceElevated,
                            unfocusedContainerColor = DarkSurfaceElevated
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val alloc = newAlloc.toDoubleOrNull() ?: 0.0
                        if (newCat.isNotBlank() && alloc > 0) {
                            viewModel.addBudget(newCat, alloc)
                            newCat = ""
                            newAlloc = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                ) {
                    Text("+ Tambah Anggaran")
                }
            }
        }
    }
}

@Composable
fun PayDebtDialog(
    debt: DebtItem,
    viewModel: FinanceViewModel,
    onDismiss: () -> Unit
) {
    val wallets by viewModel.wallets.collectAsState()
    var amountText by remember { mutableStateOf("") }
    var selectedWalletName by remember {
        mutableStateOf(
            if (debt.linkedWalletName.isNotBlank() && wallets.any { it.name == debt.linkedWalletName }) {
                debt.linkedWalletName
            } else {
                wallets.firstOrNull()?.name ?: ""
            }
        )
    }
    var noteText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (debt.type == DebtType.BORROWED) "Bayar Cicilan / Utang" else "Terima Pembayaran Piutang",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(debt.name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                Text(
                    "Sisa Tagihan: ${CurrencyHelper.formatRupiah(debt.totalAmount - debt.paidAmount)}",
                    fontSize = 12.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Jumlah Pembayaran (Rp)", color = TextSubtle) },
                    visualTransformation = ThousandsSeparatorVisualTransformation('.'),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = DarkCardBorder
                    )
                )

                if (wallets.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        if (debt.type == DebtType.BORROWED) "Sumber Dana (Dompet / Akun)" else "Setor ke Dompet / Akun",
                        fontSize = 11.5.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        wallets.forEach { wallet ->
                            val isSelected = wallet.name == selectedWalletName
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) AccentBlue.copy(alpha = 0.2f) else DarkSurfaceElevated,
                                border = BorderStroke(1.dp, if (isSelected) AccentBlue else DarkCardBorder),
                                modifier = Modifier.clickable { selectedWalletName = wallet.name }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    InstitutionLogoBadge(walletName = wallet.name, iconName = wallet.iconName, size = 14.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = wallet.name,
                                        fontSize = 11.5.sp,
                                        color = if (isSelected) AccentBlue else TextWhite,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Catatan (Opsional)", color = TextSubtle) },
                    placeholder = { Text("Contoh: Cicilan ke-1", color = TextSubtle.copy(alpha = 0.5f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = DarkCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        val amt = amountText.toDoubleOrNull() ?: 0.0
                        if (amt > 0) {
                            viewModel.payDebt(
                                debtId = debt.id,
                                amount = amt,
                                walletName = selectedWalletName,
                                note = noteText.ifBlank {
                                    if (debt.type == DebtType.BORROWED) "Pembayaran utang: ${debt.name}" else "Pelunasan piutang: ${debt.name}"
                                }
                            )
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IncomeGreen)
                ) {
                    Text("Konfirmasi Pembayaran", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AddWalletDialog(
    viewModel: FinanceViewModel,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Bank · IDR") }
    var initialBalance by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Tambah Dompet Baru", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Dompet / Bank", color = TextSubtle) },
                    placeholder = { Text("misal: BCA, Mandiri, GoPay", color = TextSubtle) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = initialBalance,
                    onValueChange = { initialBalance = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Saldo Awal (Rp)", color = TextSubtle) },
                    visualTransformation = ThousandsSeparatorVisualTransformation('.'),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        val bal = initialBalance.toDoubleOrNull() ?: 0.0
                        if (name.isNotBlank()) {
                            viewModel.addWallet(name, type, bal)
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                ) {
                    Text("Simpan Dompet", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

data class AssetTypeOption(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val defaultGroup: String
)

val availableAssetTypes = listOf(
    AssetTypeOption("Bank Account", "Savings or deposit balances", Icons.Default.AccountBalance, "Tunai & Bank"),
    AssetTypeOption("Emas", "Investasi emas fisik & tabungan emas", Icons.Default.WorkspacePremium, "Investasi"),
    AssetTypeOption("Saham", "Portofolio saham pasar modal", Icons.Default.ShowChart, "Investasi"),
    AssetTypeOption("Reksadana", "Pasar uang, obligasi & saham", Icons.Default.PieChart, "Investasi"),
    AssetTypeOption("Kripto", "Bitcoin, Ethereum & altcoin", Icons.Default.CurrencyBitcoin, "Investasi"),
    AssetTypeOption("Deposito", "Simpanan berjangka perbankan", Icons.Default.Lock, "Tunai & Bank"),
    AssetTypeOption("Properti", "Rumah, apartemen, tanah & ruko", Icons.Default.HomeWork, "Investasi"),
    AssetTypeOption("Kendaraan", "Mobil, motor & kendaraan bermotor", Icons.Default.DirectionsCar, "Investasi"),
    AssetTypeOption("Bisnis", "Kepemilikan modal usaha & franchise", Icons.Default.Storefront, "Investasi"),
    AssetTypeOption("Aset Lainnya", "Barang antik, koleksi & piutang", Icons.Default.Category, "Investasi")
)

val availableLogos = listOf(
    Pair("Bibit", "Aplikasi Reksa Dana & Obligasi"),
    Pair("Antam", "Logam Mulia PT Aneka Tambang"),
    Pair("Stockbit", "Aplikasi Saham & Analisis"),
    Pair("Pluang", "Multi-Asset Gold & Global Stocks"),
    Pair("Indodax", "Indonesia Bitcoin & Crypto Exchange"),
    Pair("Tokocrypto", "Platform Jual Beli Aset Kripto"),
    Pair("Pegadaian", "Tabungan Emas Pegadaian"),
    Pair("BCA", "Bank Central Asia"),
    Pair("Mandiri", "Livin' by Mandiri"),
    Pair("BRI", "Bank Rakyat Indonesia"),
    Pair("Bank Jago", "Bank Digital Jago"),
    Pair("SeaBank", "SeaBank Indonesia"),
    Pair("Bareksa", "Supermarket Investasi Bareksa")
)

@Composable
fun StockPortfolioInputCard(
    lotText: String,
    onLotChange: (String) -> Unit,
    averagePriceText: String,
    onAveragePriceChange: (String) -> Unit,
    currentPriceText: String,
    onCurrentPriceChange: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = DarkSurfaceElevated,
        border = BorderStroke(1.dp, AccentBlue.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CandlestickChart,
                        contentDescription = null,
                        tint = AccentBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Data Posisi Saham",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = AccentBlue.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "1 Lot = 100 Lembar",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = AccentBlue,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Input Jumlah Lot
            Text(
                text = "Jumlah Lot Dimiliki",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextSubtle
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = lotText,
                onValueChange = onLotChange,
                placeholder = { Text("Contoh: 15 lot", color = TextSubtle, fontSize = 12.sp) },
                trailingIcon = {
                    val l = lotText.toDoubleOrNull() ?: 0.0
                    Text(
                        text = if (l > 0) "${String.format(java.util.Locale.US, "%.0f", l * 100)} lbr" else "Lot",
                        fontSize = 11.sp,
                        color = AccentBlue,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface,
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = DarkCardBorder
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 2 Kolom: Nilai Average & Harga Real Pasar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Nilai Average (Beli)",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSubtle
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = averagePriceText,
                        onValueChange = onAveragePriceChange,
                        leadingIcon = {
                            Text("Rp", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, modifier = Modifier.padding(start = 10.dp))
                        },
                        placeholder = { Text("Avg Beli", color = TextSubtle, fontSize = 11.sp) },
                        visualTransformation = ThousandsSeparatorVisualTransformation('.'),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface,
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = DarkCardBorder
                        )
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Harga Real (Pasar)",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSubtle
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = currentPriceText,
                        onValueChange = onCurrentPriceChange,
                        leadingIcon = {
                            Text("Rp", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, modifier = Modifier.padding(start = 10.dp))
                        },
                        placeholder = { Text("Harga Pasar", color = TextSubtle, fontSize = 11.sp) },
                        visualTransformation = ThousandsSeparatorVisualTransformation('.'),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface,
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = DarkCardBorder
                        )
                    )
                }
            }

            // Live Calculation Card
            val pLots = lotText.toDoubleOrNull() ?: 0.0
            val pAvg = averagePriceText.toDoubleOrNull() ?: 0.0
            val pCur = currentPriceText.toDoubleOrNull() ?: pAvg
            if (pLots > 0 && (pAvg > 0 || pCur > 0)) {
                val costBasis = pLots * 100.0 * pAvg
                val realVal = pLots * 100.0 * pCur
                val gl = realVal - costBasis
                val glPercent = if (costBasis > 0) (gl / costBasis) * 100.0 else 0.0
                val isGain = gl >= 0

                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurface,
                    border = BorderStroke(1.dp, if (isGain) IncomeGreen.copy(alpha = 0.35f) else ExpenseRed.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Total Modal (Avg):", fontSize = 11.sp, color = TextMuted)
                                Text(CurrencyHelper.formatRupiah(costBasis), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Nilai Real Portofolio:", fontSize = 11.sp, color = TextMuted)
                                Text(CurrencyHelper.formatRupiah(realVal), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = if (isGain) IncomeGreen else ExpenseRed)
                            }
                        }
                        if (pAvg > 0 && pCur > 0 && pAvg != pCur) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Estimasi P/L:", fontSize = 11.sp, color = TextMuted)
                                Text(
                                    text = "${if (isGain) "+" else ""}${CurrencyHelper.formatRupiah(gl)} (${String.format(java.util.Locale.US, "%+.2f", glPercent)}%)",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isGain) IncomeGreen else ExpenseRed
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddAssetDialog(
    viewModel: FinanceViewModel,
    onDismiss: () -> Unit
) {
    var selectedType by remember { mutableStateOf(availableAssetTypes[0]) } // Bank Account
    var selectedPlatform by remember { mutableStateOf("Bibit") }
    var name by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf("IDR") }
    var amountText by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf("29 Okt 2026") }
    var timeText by remember { mutableStateOf("00.15") }
    var valuationMethod by remember { mutableStateOf("Manual") }

    var lotText by remember { mutableStateOf("") }
    var averagePriceText by remember { mutableStateOf("") }
    var currentPriceText by remember { mutableStateOf("") }

    var showTypePicker by remember { mutableStateOf(false) }
    var showLogoPicker by remember { mutableStateOf(false) }
    var showCurrencyDropdown by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showValuationPicker by remember { mutableStateOf(false) }
    var stockTicker by remember { mutableStateOf("IDX:BBRI") }
    var showStockTickerPicker by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var customIconUri by remember { mutableStateOf("") }
    val assetImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedPath = ImageUploadHelper.saveImageToInternalStorage(context, uri, "asset")
            if (savedPath != null) {
                customIconUri = "custom:$savedPath"
                showLogoPicker = false
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = DarkBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                // Top Header matching Image 1: Back arrow + "Buat Aset"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = TextWhite
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Buat Aset",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }

                // Scrollable Form content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                ) {
                    // Section 1: Jenis Aset
                    Text(
                        text = "Jenis Aset",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Card Jenis Aset matching Image 1
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = DarkSurface,
                        border = BorderStroke(1.dp, DarkCardBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showTypePicker = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                AssetLogoBadge(
                                    assetType = selectedType.title,
                                    assetName = name,
                                    platform = selectedPlatform,
                                    iconUri = customIconUri,
                                    size = 42.dp
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = selectedType.title,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                    Text(
                                        text = selectedType.subtitle,
                                        fontSize = 12.sp,
                                        color = TextMuted
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Perubahan",
                                    fontSize = 13.sp,
                                    color = AccentBlue,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = AccentBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = DarkSurface,
                            border = BorderStroke(1.dp, DarkCardBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showLogoPicker = true }
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
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (selectedPlatform.isNotBlank()) "Logo: $selectedPlatform" else "Pilih Logo",
                                    fontSize = 12.5.sp,
                                    color = if (selectedPlatform.isNotBlank()) TextWhite else TextMuted,
                                    maxLines = 1
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (customIconUri.isNotBlank()) AccentBlue.copy(alpha = 0.2f) else DarkSurface,
                            border = BorderStroke(1.dp, if (customIconUri.isNotBlank()) AccentBlue else DarkCardBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    assetImagePickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = null,
                                    tint = if (customIconUri.isNotBlank()) AccentBlue else TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (customIconUri.isNotBlank()) "Gambar Diunggah" else "Unggah Gambar",
                                    fontSize = 12.5.sp,
                                    color = if (customIconUri.isNotBlank()) TextWhite else TextMuted,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (selectedType.title.equals("Saham", ignoreCase = true)) {
                        // Banner Integrasi TradingView
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = AccentBlue.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, AccentBlue.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showStockTickerPicker = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = AccentBlue,
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.CandlestickChart,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = if (stockTicker.isNotBlank()) "Emiten: $stockTicker" else "Pilih Emiten Saham (IHSG)",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextWhite
                                        )
                                        Text(
                                            text = "Integrasi Real-Time TradingView",
                                            fontSize = 11.5.sp,
                                            color = TextMuted
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = AccentBlue
                                ) {
                                    Text(
                                        text = if (stockTicker.isNotBlank()) "Ganti" else "Pilih",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Chips: Saham Unggulan IHSG
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val presetCodes = listOf("BBRI", "BBCA", "BMRI", "BBNI", "TLKM", "ASII", "GOTO", "AMMN", "ANTM", "BRIS")
                            presetCodes.forEach { code ->
                                val full = "IDX:$code"
                                val isSelected = stockTicker.equals(full, ignoreCase = true) || stockTicker.equals(code, ignoreCase = true)
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) AccentBlue else DarkSurface,
                                    border = BorderStroke(1.dp, if (isSelected) AccentBlue else DarkCardBorder),
                                    modifier = Modifier.clickable {
                                        stockTicker = full
                                        val found = TradingViewHelper.popularStocks.find { it.code.equals(code, ignoreCase = true) }
                                        name = "Saham $code" + (if (found != null) " - ${found.name.take(20)}" else "")
                                        selectedPlatform = "Stockbit"
                                    }
                                ) {
                                    Text(
                                        text = code,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else TextMuted,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }

                        // Live TradingView Real-time Widget Preview
                        if (stockTicker.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            TradingViewChartCard(
                                initialSymbol = stockTicker,
                                onSymbolChanged = { newSym ->
                                    stockTicker = newSym
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        StockPortfolioInputCard(
                            lotText = lotText,
                            onLotChange = { input ->
                                lotText = input.filter { it.isDigit() || it == '.' }
                                val l = lotText.toDoubleOrNull() ?: 0.0
                                val c = currentPriceText.toDoubleOrNull() ?: averagePriceText.toDoubleOrNull() ?: 0.0
                                if (l > 0 && c > 0) {
                                    amountText = String.format(java.util.Locale.US, "%.0f", l * 100.0 * c)
                                }
                            },
                            averagePriceText = averagePriceText,
                            onAveragePriceChange = { input ->
                                averagePriceText = input.filter { it.isDigit() }
                                if (currentPriceText.isBlank()) {
                                    currentPriceText = averagePriceText
                                }
                                val l = lotText.toDoubleOrNull() ?: 0.0
                                val c = currentPriceText.toDoubleOrNull() ?: averagePriceText.toDoubleOrNull() ?: 0.0
                                if (l > 0 && c > 0) {
                                    amountText = String.format(java.util.Locale.US, "%.0f", l * 100.0 * c)
                                }
                            },
                            currentPriceText = currentPriceText,
                            onCurrentPriceChange = { input ->
                                currentPriceText = input.filter { it.isDigit() }
                                val l = lotText.toDoubleOrNull() ?: 0.0
                                val c = currentPriceText.toDoubleOrNull() ?: averagePriceText.toDoubleOrNull() ?: 0.0
                                if (l > 0 && c > 0) {
                                    amountText = String.format(java.util.Locale.US, "%.0f", l * 100.0 * c)
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Section 2: Nama Aset
                    Text(
                        text = "Nama Aset",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("Nama Aset", color = TextSubtle) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface,
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = DarkCardBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Section 3: Mata Uang & Jumlah matching Image 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(0.42f)) {
                            Text(
                                text = "Mata Uang",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextWhite
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Box {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = DarkSurface,
                                    border = BorderStroke(1.dp, DarkCardBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .clickable { showCurrencyDropdown = true }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = when (selectedCurrency) {
                                                    "IDR" -> "🇮🇩"
                                                    "USD" -> "🇺🇸"
                                                    "SGD" -> "🇸🇬"
                                                    else -> "🌐"
                                                },
                                                fontSize = 16.sp
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = selectedCurrency,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextWhite
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = null,
                                            tint = TextMuted
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = showCurrencyDropdown,
                                    onDismissRequest = { showCurrencyDropdown = false }
                                ) {
                                    listOf("IDR" to "🇮🇩 IDR - Rupiah", "USD" to "🇺🇸 USD - Dollar", "SGD" to "🇸🇬 SGD - Dolar SG").forEach { (curr, label) ->
                                        DropdownMenuItem(
                                            text = { Text(label) },
                                            onClick = {
                                                selectedCurrency = curr
                                                showCurrencyDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(0.58f)) {
                            Text(
                                text = "Jumlah",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextWhite
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = amountText,
                                onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                                leadingIcon = {
                                    Text(
                                        text = "Rp",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite,
                                        modifier = Modifier.padding(start = 12.dp, end = 2.dp)
                                    )
                                },
                                placeholder = { Text("Masukkan jumlah", color = TextSubtle, fontSize = 13.sp) },
                                visualTransformation = ThousandsSeparatorVisualTransformation('.'),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    focusedContainerColor = DarkSurface,
                                    unfocusedContainerColor = DarkSurface,
                                    focusedBorderColor = AccentBlue,
                                    unfocusedBorderColor = DarkCardBorder
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Section 4: Tanggal Dibuat matching Image 1
                    Text(
                        text = "Tanggal Dibuat",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = DarkSurface,
                            border = BorderStroke(1.dp, DarkCardBorder),
                            modifier = Modifier
                                .weight(1.4f)
                                .height(54.dp)
                                .clickable { showDatePicker = true }
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = dateText,
                                    fontSize = 14.sp,
                                    color = TextWhite
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = DarkSurface,
                            border = BorderStroke(1.dp, DarkCardBorder),
                            modifier = Modifier
                                .weight(0.9f)
                                .height(54.dp)
                                .clickable { showTimePicker = true }
                        ) {
                            Box(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = timeText,
                                    fontSize = 14.sp,
                                    color = TextWhite
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Section 5: Tambah Metode Penilaian button matching Image 1
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurface,
                        border = BorderStroke(1.dp, DarkCardBorder),
                        modifier = Modifier.clickable { showValuationPicker = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = null,
                                tint = AccentBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (valuationMethod == "Manual") "Tambah Metode Penilaian" else "Metode: $valuationMethod",
                                fontSize = 13.sp,
                                color = AccentBlue,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Fixed Bottom Action Bar: "Buat Aset"
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = DarkBackground,
                    border = BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.5f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 56.dp)
                    ) {
                        val amountVal = amountText.toDoubleOrNull() ?: 0.0
                        Button(
                            onClick = {
                                val finalName = if (name.isNotBlank()) name else selectedType.title
                                val parsedLots = lotText.toDoubleOrNull() ?: 0.0
                                val parsedAvgPrice = averagePriceText.toDoubleOrNull() ?: 0.0
                                val parsedCurrentPrice = currentPriceText.toDoubleOrNull() ?: parsedAvgPrice
                                val finalRealVal = if (selectedType.title.equals("Saham", ignoreCase = true) && parsedLots > 0 && parsedCurrentPrice > 0) {
                                    parsedLots * 100.0 * parsedCurrentPrice
                                } else amountVal

                                val newAsset = AssetItem(
                                    name = finalName,
                                    group = selectedType.defaultGroup,
                                    assetType = selectedType.title,
                                    value = finalRealVal,
                                    platform = selectedPlatform,
                                    currency = selectedCurrency,
                                    createdAtDate = dateText,
                                    createdAtTime = timeText,
                                    valuationMethod = valuationMethod,
                                    iconUri = customIconUri,
                                    tickerSymbol = if (selectedType.title.equals("Saham", ignoreCase = true)) stockTicker else "",
                                    lotCount = parsedLots,
                                    averagePrice = parsedAvgPrice,
                                    currentPrice = parsedCurrentPrice
                                )
                                viewModel.addAsset(newAsset)
                                onDismiss()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                        ) {
                            Text(
                                text = "Buat Aset",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal: Pilih Jenis Aset (Emas, Saham, Reksadana, Kripto, dll.)
    if (showTypePicker) {
        AlertDialog(
            onDismissRequest = { showTypePicker = false },
            containerColor = DarkSurface,
            title = {
                Text(
                    text = "Pilih Jenis Aset",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableAssetTypes) { type ->
                        val isSelected = selectedType.title == type.title
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) AccentBlue.copy(alpha = 0.2f) else DarkSurfaceElevated,
                            border = BorderStroke(1.dp, if (isSelected) AccentBlue else DarkCardBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedType = type
                                    if (type.title.equals("Saham", ignoreCase = true)) {
                                        if (stockTicker.isBlank()) stockTicker = "IDX:BBRI"
                                        if (name.isBlank() || name == "Bank Account") name = "Saham BBRI"
                                        selectedPlatform = "Stockbit"
                                    }
                                    showTypePicker = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AssetLogoBadge(
                                    assetType = type.title,
                                    size = 36.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = type.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                    Text(
                                        text = type.subtitle,
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTypePicker = false }) {
                    Text("Tutup", color = AccentBlue)
                }
            }
        )
    }

    // Modal: Choose from Logo List
    if (showLogoPicker) {
        AlertDialog(
            onDismissRequest = { showLogoPicker = false },
            containerColor = DarkSurface,
            title = {
                Text(
                    text = "Pilih Logo Platform",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AccentBlue.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, AccentBlue.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    assetImagePickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = null,
                                    tint = AccentBlue,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Unggah Gambar dari Galeri",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                    Text(
                                        text = "Gunakan logo atau gambar kustom untuk aset",
                                        fontSize = 10.5.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }
                    items(availableLogos) { (brand, desc) ->
                        val isSelected = selectedPlatform.equals(brand, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) AccentBlue.copy(alpha = 0.2f) else DarkSurfaceElevated,
                            border = BorderStroke(1.dp, if (isSelected) AccentBlue else DarkCardBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedPlatform = brand
                                    if (name.isBlank()) {
                                        name = when (brand) {
                                            "Antam", "Pegadaian" -> "Emas $brand"
                                            "Bibit", "Bareksa" -> "Tabungan $brand"
                                            "Stockbit" -> "Portofolio $brand"
                                            "Indodax", "Tokocrypto" -> "Kripto $brand"
                                            else -> "Aset $brand"
                                        }
                                    }
                                    showLogoPicker = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = AccentBlue,
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = brand.take(2).uppercase(),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = brand,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                    Text(
                                        text = desc,
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLogoPicker = false }) {
                    Text("Tutup", color = AccentBlue)
                }
            }
        )
    }

    // Date Picker Selector
    if (showDatePicker) {
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            containerColor = DarkSurface,
            title = { Text("Pilih Tanggal Dibuat", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("29 Okt 2026", "28 Okt 2026", "20 Okt 2026", "15 Okt 2026", "1 Okt 2026", "30 Sep 2026").forEach { d ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (dateText == d) AccentBlue else DarkSurfaceElevated,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    dateText = d
                                    showDatePicker = false
                                }
                        ) {
                            Text(
                                text = d,
                                color = TextWhite,
                                modifier = Modifier.padding(12.dp),
                                fontWeight = if (dateText == d) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Tutup", color = AccentBlue)
                }
            }
        )
    }

    // Time Picker Selector
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            containerColor = DarkSurface,
            title = { Text("Pilih Waktu", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("00.15", "08.30", "10.09", "12.16", "15.00", "19.30").forEach { t ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (timeText == t) AccentBlue else DarkSurfaceElevated,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    timeText = t
                                    showTimePicker = false
                                }
                        ) {
                            Text(
                                text = t,
                                color = TextWhite,
                                modifier = Modifier.padding(12.dp),
                                fontWeight = if (timeText == t) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Tutup", color = AccentBlue)
                }
            }
        )
    }

    // Valuation Method Selector
    if (showValuationPicker) {
        AlertDialog(
            onDismissRequest = { showValuationPicker = false },
            containerColor = DarkSurface,
            title = { Text("Metode Penilaian Aset", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "Manual" to "Input mandiri nilai aset berkala",
                        "Harga Pasar Real-time" to "Mengikuti update harga pasar/bursa",
                        "Biaya Perolehan" to "Berdasarkan modal pembelian awal (Cost Basis)",
                        "Nilai Buku" to "Nilai tercatat setelah penyusutan / akumulasi"
                    ).forEach { (m, desc) ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (valuationMethod == m) AccentBlue else DarkSurfaceElevated,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    valuationMethod = m
                                    showValuationPicker = false
                                }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = m,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = desc,
                                    color = if (valuationMethod == m) Color.White.copy(alpha = 0.8f) else TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showValuationPicker = false }) {
                    Text("Tutup", color = AccentBlue)
                }
            }
        )
    }

    if (showStockTickerPicker) {
        StockTickerPickerSheet(
            selectedSymbol = stockTicker,
            onSymbolSelected = { sym ->
                stockTicker = sym
                val code = sym.substringAfter(":")
                val found = TradingViewHelper.popularStocks.find { it.code.equals(code, ignoreCase = true) }
                name = "Saham $code" + (if (found != null) " - ${found.name.take(20)}" else "")
                selectedPlatform = "Stockbit"
                val q = TradingViewHelper.getCachedQuote(sym)
                if (q != null && q.closePrice > 0.0) {
                    currentPriceText = String.format(java.util.Locale.US, "%.0f", q.closePrice)
                    if (averagePriceText.isBlank()) {
                        averagePriceText = currentPriceText
                    }
                }
                showStockTickerPicker = false
            },
            onDismiss = { showStockTickerPicker = false }
        )
    }
}

@Composable
fun EditAssetDialog(
    asset: AssetItem,
    viewModel: FinanceViewModel,
    onDismiss: () -> Unit
) {
    var selectedType by remember {
        mutableStateOf(availableAssetTypes.find { it.title.equals(asset.assetType, ignoreCase = true) } ?: availableAssetTypes[0])
    }
    var selectedPlatform by remember { mutableStateOf(asset.platform) }
    var name by remember { mutableStateOf(asset.name) }
    var selectedCurrency by remember { mutableStateOf(asset.currency) }
    var amountText by remember { mutableStateOf(String.format(java.util.Locale.US, "%.0f", asset.value)) }
    var dateText by remember { mutableStateOf(asset.createdAtDate) }
    var timeText by remember { mutableStateOf(asset.createdAtTime) }
    var valuationMethod by remember { mutableStateOf(asset.valuationMethod) }

    var showTypePicker by remember { mutableStateOf(false) }
    var showLogoPicker by remember { mutableStateOf(false) }
    var showCurrencyDropdown by remember { mutableStateOf(false) }
    var stockTicker by remember { mutableStateOf(asset.tickerSymbol.ifBlank { asset.resolvedTicker }) }
    var showStockTickerPicker by remember { mutableStateOf(false) }

    var lotText by remember {
        mutableStateOf(if (asset.lotCount > 0) String.format(java.util.Locale.US, "%.0f", asset.lotCount) else "")
    }
    var averagePriceText by remember {
        mutableStateOf(if (asset.averagePrice > 0) String.format(java.util.Locale.US, "%.0f", asset.averagePrice) else "")
    }
    var currentPriceText by remember {
        mutableStateOf(if (asset.currentPrice > 0) String.format(java.util.Locale.US, "%.0f", asset.currentPrice) else if (asset.averagePrice > 0) String.format(java.util.Locale.US, "%.0f", asset.averagePrice) else "")
    }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(stockTicker) {
        if (selectedType.title.equals("Saham", ignoreCase = true) && stockTicker.isNotBlank()) {
            val q = TradingViewHelper.getCachedQuote(stockTicker)
            if (q != null && q.closePrice > 0.0) {
                if (currentPriceText.isBlank() || currentPriceText == "0") {
                    currentPriceText = String.format(java.util.Locale.US, "%.0f", q.closePrice)
                    val l = lotText.toDoubleOrNull() ?: 0.0
                    if (l > 0) {
                        amountText = String.format(java.util.Locale.US, "%.0f", l * 100.0 * q.closePrice)
                    }
                }
            } else {
                val fetched = withContext(Dispatchers.IO) {
                    TradingViewHelper.fetchSingleQuote(stockTicker)
                }
                if (fetched != null && fetched.closePrice > 0.0) {
                    if (currentPriceText.isBlank() || currentPriceText == "0") {
                        currentPriceText = String.format(java.util.Locale.US, "%.0f", fetched.closePrice)
                        val l = lotText.toDoubleOrNull() ?: 0.0
                        if (l > 0) {
                            amountText = String.format(java.util.Locale.US, "%.0f", l * 100.0 * fetched.closePrice)
                        }
                    }
                }
            }
        }
    }

    val context = LocalContext.current
    var customIconUri by remember { mutableStateOf(asset.iconUri) }
    val assetImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedPath = ImageUploadHelper.saveImageToInternalStorage(context, uri, "asset")
            if (savedPath != null) {
                customIconUri = "custom:$savedPath"
                showLogoPicker = false
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = DarkBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = TextWhite
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Edit Aset",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                ) {
                    Text(
                        text = "Jenis Aset",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = DarkSurface,
                        border = BorderStroke(1.dp, DarkCardBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showTypePicker = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                AssetLogoBadge(
                                    assetType = selectedType.title,
                                    assetName = name,
                                    platform = selectedPlatform,
                                    iconUri = customIconUri,
                                    size = 42.dp
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = selectedType.title,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                    Text(
                                        text = selectedType.subtitle,
                                        fontSize = 12.sp,
                                        color = TextMuted
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Perubahan",
                                    fontSize = 13.sp,
                                    color = AccentBlue,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = AccentBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = DarkSurface,
                            border = BorderStroke(1.dp, DarkCardBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showLogoPicker = true }
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
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (selectedPlatform.isNotBlank()) "Logo: $selectedPlatform" else "Pilih Logo",
                                    fontSize = 12.5.sp,
                                    color = if (selectedPlatform.isNotBlank()) TextWhite else TextMuted,
                                    maxLines = 1
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (customIconUri.isNotBlank()) AccentBlue.copy(alpha = 0.2f) else DarkSurface,
                            border = BorderStroke(1.dp, if (customIconUri.isNotBlank()) AccentBlue else DarkCardBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    assetImagePickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = null,
                                    tint = if (customIconUri.isNotBlank()) AccentBlue else TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (customIconUri.isNotBlank()) "Gambar Diunggah" else "Unggah Gambar",
                                    fontSize = 12.5.sp,
                                    color = if (customIconUri.isNotBlank()) TextWhite else TextMuted,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (selectedType.title.equals("Saham", ignoreCase = true)) {
                        // Banner Integrasi TradingView
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = AccentBlue.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, AccentBlue.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showStockTickerPicker = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = AccentBlue,
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.CandlestickChart,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = if (stockTicker.isNotBlank()) "Emiten: $stockTicker" else "Pilih Emiten Saham (IHSG)",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextWhite
                                        )
                                        Text(
                                            text = "Integrasi Real-Time TradingView",
                                            fontSize = 11.5.sp,
                                            color = TextMuted
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = AccentBlue
                                ) {
                                    Text(
                                        text = if (stockTicker.isNotBlank()) "Ganti" else "Pilih",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Chips: Saham Unggulan IHSG
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val presetCodes = listOf("BBRI", "BBCA", "BMRI", "BBNI", "TLKM", "ASII", "GOTO", "AMMN", "ANTM", "BRIS")
                            presetCodes.forEach { code ->
                                val full = "IDX:$code"
                                val isSelected = stockTicker.equals(full, ignoreCase = true) || stockTicker.equals(code, ignoreCase = true)
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) AccentBlue else DarkSurface,
                                    border = BorderStroke(1.dp, if (isSelected) AccentBlue else DarkCardBorder),
                                    modifier = Modifier.clickable {
                                        stockTicker = full
                                        val found = TradingViewHelper.popularStocks.find { it.code.equals(code, ignoreCase = true) }
                                        name = "Saham $code" + (if (found != null) " - ${found.name.take(20)}" else "")
                                        selectedPlatform = "Stockbit"
                                        coroutineScope.launch {
                                            val q = TradingViewHelper.getCachedQuote(full) ?: withContext(Dispatchers.IO) {
                                                TradingViewHelper.fetchSingleQuote(full)
                                            }
                                            if (q != null && q.closePrice > 0.0) {
                                                currentPriceText = String.format(java.util.Locale.US, "%.0f", q.closePrice)
                                                if (averagePriceText.isBlank()) {
                                                    averagePriceText = currentPriceText
                                                }
                                                val l = lotText.toDoubleOrNull() ?: 0.0
                                                if (l > 0) {
                                                    amountText = String.format(java.util.Locale.US, "%.0f", l * 100.0 * q.closePrice)
                                                }
                                            }
                                        }
                                    }
                                ) {
                                    Text(
                                        text = code,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else TextMuted,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }

                        // Live TradingView Real-time Widget Preview
                        if (stockTicker.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            TradingViewChartCard(
                                initialSymbol = stockTicker,
                                onSymbolChanged = { newSym ->
                                    stockTicker = newSym
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        StockPortfolioInputCard(
                            lotText = lotText,
                            onLotChange = { input ->
                                lotText = input.filter { it.isDigit() || it == '.' }
                                val l = lotText.toDoubleOrNull() ?: 0.0
                                val c = currentPriceText.toDoubleOrNull() ?: averagePriceText.toDoubleOrNull() ?: 0.0
                                if (l > 0 && c > 0) {
                                    amountText = String.format(java.util.Locale.US, "%.0f", l * 100.0 * c)
                                }
                            },
                            averagePriceText = averagePriceText,
                            onAveragePriceChange = { input ->
                                averagePriceText = input.filter { it.isDigit() }
                                if (currentPriceText.isBlank()) {
                                    currentPriceText = averagePriceText
                                }
                                val l = lotText.toDoubleOrNull() ?: 0.0
                                val c = currentPriceText.toDoubleOrNull() ?: averagePriceText.toDoubleOrNull() ?: 0.0
                                if (l > 0 && c > 0) {
                                    amountText = String.format(java.util.Locale.US, "%.0f", l * 100.0 * c)
                                }
                            },
                            currentPriceText = currentPriceText,
                            onCurrentPriceChange = { input ->
                                currentPriceText = input.filter { it.isDigit() }
                                val l = lotText.toDoubleOrNull() ?: 0.0
                                val c = currentPriceText.toDoubleOrNull() ?: averagePriceText.toDoubleOrNull() ?: 0.0
                                if (l > 0 && c > 0) {
                                    amountText = String.format(java.util.Locale.US, "%.0f", l * 100.0 * c)
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    Text(
                        text = "Nama Aset",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("Nama Aset", color = TextSubtle) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface,
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = DarkCardBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Nilai Aset (Rp)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                        leadingIcon = {
                            Text(
                                text = "Rp",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite,
                                modifier = Modifier.padding(start = 12.dp, end = 2.dp)
                            )
                        },
                        visualTransformation = ThousandsSeparatorVisualTransformation('.'),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface,
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = DarkCardBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Fixed Bottom Action Bar: "Simpan Perubahan"
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = DarkBackground,
                    border = BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.5f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 56.dp)
                    ) {
                        val amountVal = amountText.toDoubleOrNull() ?: asset.value
                        Button(
                            onClick = {
                                val parsedLots = lotText.toDoubleOrNull() ?: 0.0
                                val parsedAvgPrice = averagePriceText.toDoubleOrNull() ?: 0.0
                                val parsedCurrentPrice = currentPriceText.toDoubleOrNull() ?: parsedAvgPrice
                                val finalRealVal = if (selectedType.title.equals("Saham", ignoreCase = true) && parsedLots > 0 && parsedCurrentPrice > 0) {
                                    parsedLots * 100.0 * parsedCurrentPrice
                                } else amountVal

                                val updated = asset.copy(
                                    name = name.ifBlank { asset.name },
                                    group = selectedType.defaultGroup,
                                    assetType = selectedType.title,
                                    value = finalRealVal,
                                    platform = selectedPlatform,
                                    currency = selectedCurrency,
                                    valuationMethod = valuationMethod,
                                    iconUri = customIconUri,
                                    tickerSymbol = if (selectedType.title.equals("Saham", ignoreCase = true)) stockTicker else asset.tickerSymbol,
                                    lotCount = parsedLots,
                                    averagePrice = parsedAvgPrice,
                                    currentPrice = parsedCurrentPrice
                                )
                                viewModel.updateAsset(updated)
                                onDismiss()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                        ) {
                            Text(
                                text = "Simpan Perubahan",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    if (showTypePicker) {
        AlertDialog(
            onDismissRequest = { showTypePicker = false },
            containerColor = DarkSurface,
            title = {
                Text(
                    text = "Pilih Jenis Aset",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableAssetTypes) { type ->
                        val isSelected = selectedType.title == type.title
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) AccentBlue.copy(alpha = 0.2f) else DarkSurfaceElevated,
                            border = BorderStroke(1.dp, if (isSelected) AccentBlue else DarkCardBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedType = type
                                    if (type.title.equals("Saham", ignoreCase = true) && stockTicker.isBlank()) {
                                        stockTicker = "IDX:BBRI"
                                    }
                                    showTypePicker = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AssetLogoBadge(
                                    assetType = type.title,
                                    size = 36.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = type.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                    Text(
                                        text = type.subtitle,
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTypePicker = false }) {
                    Text("Tutup", color = AccentBlue)
                }
            }
        )
    }

    if (showLogoPicker) {
        AlertDialog(
            onDismissRequest = { showLogoPicker = false },
            containerColor = DarkSurface,
            title = {
                Text(
                    text = "Pilih Logo Platform",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AccentBlue.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, AccentBlue.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    assetImagePickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = null,
                                    tint = AccentBlue,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Unggah Gambar dari Galeri",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                    Text(
                                        text = "Gunakan logo atau gambar kustom untuk aset",
                                        fontSize = 10.5.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }
                    items(availableLogos) { (brand, desc) ->
                        val isSelected = selectedPlatform.equals(brand, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) AccentBlue.copy(alpha = 0.2f) else DarkSurfaceElevated,
                            border = BorderStroke(1.dp, if (isSelected) AccentBlue else DarkCardBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedPlatform = brand
                                    showLogoPicker = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = AccentBlue,
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = brand.take(2).uppercase(),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = brand,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                    Text(
                                        text = desc,
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLogoPicker = false }) {
                    Text("Tutup", color = AccentBlue)
                }
            }
        )
    }

    if (showStockTickerPicker) {
        StockTickerPickerSheet(
            selectedSymbol = stockTicker,
            onSymbolSelected = { sym ->
                stockTicker = sym
                val code = sym.substringAfter(":")
                val found = TradingViewHelper.popularStocks.find { it.code.equals(code, ignoreCase = true) }
                name = "Saham $code" + (if (found != null) " - ${found.name.take(20)}" else "")
                selectedPlatform = "Stockbit"
                coroutineScope.launch {
                    val q = TradingViewHelper.getCachedQuote(sym) ?: withContext(Dispatchers.IO) {
                        TradingViewHelper.fetchSingleQuote(sym)
                    }
                    if (q != null && q.closePrice > 0.0) {
                        currentPriceText = String.format(java.util.Locale.US, "%.0f", q.closePrice)
                        if (averagePriceText.isBlank()) {
                            averagePriceText = currentPriceText
                        }
                        val l = lotText.toDoubleOrNull() ?: 0.0
                        if (l > 0) {
                            amountText = String.format(java.util.Locale.US, "%.0f", l * 100.0 * q.closePrice)
                        }
                    }
                }
                showStockTickerPicker = false
            },
            onDismiss = { showStockTickerPicker = false }
        )
    }
}

@Composable
fun AddDebtDialog(
    viewModel: FinanceViewModel,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("1 Des 2027") }
    var debtType by remember { mutableStateOf(DebtType.BORROWED) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tambah Pinjaman / Utang", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = TextMuted)
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))

                // Type selector: Utang vs Piutang
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurfaceElevated, RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val isBorrowed = debtType == DebtType.BORROWED
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isBorrowed) ExpenseRed else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { debtType = DebtType.BORROWED }
                    ) {
                        Text(
                            text = "Utang (Dipinjam)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isBorrowed) Color.White else TextMuted,
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (!isBorrowed) IncomeGreen else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { debtType = DebtType.LENT }
                    ) {
                        Text(
                            text = "Piutang (Dipinjamkan)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!isBorrowed) Color.White else TextMuted,
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Pinjaman / Cicilan", color = TextSubtle) },
                    placeholder = { Text("misal: KPR, Cicilan Laptop", color = TextSubtle) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Total Nominal (Rp)", color = TextSubtle) },
                    visualTransformation = ThousandsSeparatorVisualTransformation('.'),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Jatuh Tempo", color = TextSubtle) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        val a = amountText.toDoubleOrNull() ?: 0.0
                        if (name.isNotBlank() && a > 0) {
                            viewModel.addDebt(name, a, dueDate, debtType)
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
                ) {
                    Text("Simpan Utang", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun EditDebtDialog(
    debt: DebtItem,
    viewModel: FinanceViewModel,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    var name by remember { mutableStateOf(debt.name) }
    var amountText by remember { mutableStateOf(debt.totalAmount.toLong().toString()) }
    var paidText by remember { mutableStateOf(debt.paidAmount.toLong().toString()) }
    var dueDate by remember { mutableStateOf(debt.dueDate) }
    var debtType by remember { mutableStateOf(debt.type) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit Pinjaman / Utang",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Type selector: Utang (Dipinjam) vs Piutang (Dipinjamkan)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurfaceElevated, RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val isBorrowed = debtType == DebtType.BORROWED
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isBorrowed) ExpenseRed else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { debtType = DebtType.BORROWED }
                    ) {
                        Text(
                            text = "Utang (Dipinjam)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isBorrowed) Color.White else TextMuted,
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (!isBorrowed) IncomeGreen else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { debtType = DebtType.LENT }
                    ) {
                        Text(
                            text = "Piutang (Dipinjamkan)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!isBorrowed) Color.White else TextMuted,
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Pinjaman / Cicilan", color = TextSubtle) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Total Nominal (Rp)", color = TextSubtle) },
                    visualTransformation = ThousandsSeparatorVisualTransformation('.'),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = paidText,
                    onValueChange = { paidText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Nominal Sudah Dibayar (Rp)", color = TextSubtle) },
                    visualTransformation = ThousandsSeparatorVisualTransformation('.'),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Jatuh Tempo", color = TextSubtle) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        val total = amountText.toDoubleOrNull() ?: 0.0
                        val paid = paidText.toDoubleOrNull() ?: 0.0
                        if (name.isNotBlank() && total > 0) {
                            viewModel.updateDebt(
                                debt.copy(
                                    name = name,
                                    type = debtType,
                                    totalAmount = total,
                                    paidAmount = paid.coerceAtMost(total),
                                    dueDate = dueDate
                                )
                            )
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                ) {
                    Text("Simpan Perubahan", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = ExpenseRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Hapus Utang Ini", color = ExpenseRed, fontWeight = FontWeight.Medium)
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = DarkSurface,
            title = { Text("Hapus Utang?", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Apakah Anda yakin ingin menghapus data utang \"${debt.name}\"?",
                    color = TextMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        viewModel.deleteDebt(debt.id)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
                ) {
                    Text("Hapus", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Batal", color = TextSubtle)
                }
            }
        )
    }
}

@Composable
fun MonthYearPickerDialog(
    currentMonth: Int,
    currentYear: Int,
    onDismiss: () -> Unit,
    onSelectMonthYear: (month: Int, year: Int) -> Unit
) {
    var selectedMonth by remember { mutableStateOf(currentMonth) }
    var selectedYear by remember { mutableStateOf(currentYear) }
    var dragOffsetX by remember { mutableFloatStateOf(0f) }

    val months = listOf(
        Pair(1, "Januari"),
        Pair(2, "Februari"),
        Pair(3, "Maret"),
        Pair(4, "April"),
        Pair(5, "Mei"),
        Pair(6, "Juni"),
        Pair(7, "Juli"),
        Pair(8, "Agustus"),
        Pair(9, "September"),
        Pair(10, "Oktober"),
        Pair(11, "November"),
        Pair(12, "Desember")
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(selectedMonth, selectedYear) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (dragOffsetX < -40f) {
                                // Swipe left: next month
                                if (selectedMonth < 12) {
                                    selectedMonth += 1
                                } else {
                                    selectedMonth = 1
                                    selectedYear += 1
                                }
                            } else if (dragOffsetX > 40f) {
                                // Swipe right: previous month
                                if (selectedMonth > 1) {
                                    selectedMonth -= 1
                                } else {
                                    selectedMonth = 12
                                    selectedYear -= 1
                                }
                            }
                            dragOffsetX = 0f
                        },
                        onDragCancel = { dragOffsetX = 0f },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            dragOffsetX += dragAmount
                        }
                    )
                }
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Pilih Periode",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "Bulan & Tahun Transaksi",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Year selector stepper
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DarkSurfaceElevated,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { selectedYear -= 1 }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Tahun Sebelumnya",
                                tint = TextWhite
                            )
                        }

                        Text(
                            text = "$selectedYear",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )

                        IconButton(onClick = { selectedYear += 1 }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Tahun Berikutnya",
                                tint = TextWhite
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Year Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val quickYears = listOf(2023, 2024, 2025, 2026, 2027, 2028)
                    items(quickYears) { y ->
                        val isYearSelected = selectedYear == y
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isYearSelected) AccentBlue else DarkSurfaceElevated,
                            modifier = Modifier.clickable { selectedYear = y }
                        ) {
                            Text(
                                text = "$y",
                                fontSize = 11.sp,
                                fontWeight = if (isYearSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isYearSelected) Color.White else TextMuted,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Months Grid (4 rows x 3 columns)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    months.chunked(3).forEach { rowMonths ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowMonths.forEach { (mIndex, mName) ->
                                val isSelected = selectedMonth == mIndex
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) AccentBlue else DarkSurfaceElevated,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { selectedMonth = mIndex }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = mName.take(3),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else TextWhite
                                        )
                                        Text(
                                            text = mName,
                                            fontSize = 9.sp,
                                            color = if (isSelected) Color(0xFFDCE8FE) else TextSubtle
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Swipe Gesture Hint
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = TextSubtle,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Geser untuk ganti bulan / tahun",
                        fontSize = 11.sp,
                        color = TextSubtle,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = TextSubtle,
                        modifier = Modifier.size(13.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Actions: Reset to current ("Bulan Ini") and "Terapkan"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceElevated,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                selectedMonth = 10
                                selectedYear = 2026
                            }
                    ) {
                        Text(
                            text = "Okt 2026",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextMuted,
                            modifier = Modifier.padding(vertical = 12.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }

                    Button(
                        onClick = {
                            onSelectMonthYear(selectedMonth, selectedYear)
                            onDismiss()
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(44.dp)
                    ) {
                        Text("Terapkan", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

