package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccountWallet
import com.example.data.model.Transaction
import com.example.data.model.TransactionType
import com.example.ui.dialogs.AddTransactionDialog
import com.example.ui.dialogs.CreateWalletDialog
import com.example.ui.dialogs.EditTransactionDialog
import com.example.ui.dialogs.getBankBadge
import com.example.ui.dialogs.getCategoryIcon
import com.example.util.InstitutionLogoBadge
import com.example.util.ImageUploadHelper
import com.example.util.TransactionMathHelper
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.CameraAlt
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardSubtle
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
import com.example.ui.viewmodel.FinanceViewModel
import com.example.util.CategorySlice
import com.example.util.CurrencyHelper

@Composable
fun AccountDetailScreen(
    walletId: String,
    viewModel: FinanceViewModel,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    val allWallets by viewModel.wallets.collectAsState()
    val wallet = allWallets.find { it.id == walletId }

    if (wallet == null) {
        onBack()
        return
    }

    val allTransactions by viewModel.allTransactions.collectAsState()
    val selectedMonth by viewModel.selectedMonthIndex.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()
    val monthOptions by viewModel.monthOptions.collectAsState()
    val isBalanceHidden by viewModel.isBalanceHidden.collectAsState()

    var showEditWalletDialog by remember { mutableStateOf(false) }
    var showAddTxDialog by remember { mutableStateOf(false) }
    var activeTxToEdit by remember { mutableStateOf<Transaction?>(null) }
    var activeBottomTab by remember { mutableIntStateOf(0) } // 0: Ringkasan, 1: Transaksi

    val context = LocalContext.current
    val walletImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedPath = ImageUploadHelper.saveImageToInternalStorage(context, uri, "wallet")
            if (savedPath != null) {
                viewModel.updateWallet(wallet.copy(iconName = "custom:$savedPath"))
            }
        }
    }

    // Filter transactions for this specific account and current selected period
    val accountTransactions = remember(allTransactions, wallet.name, selectedMonth, selectedYear) {
        allTransactions.filter { tx ->
            val matchAccount = tx.accountName.equals(wallet.name, ignoreCase = true) ||
                    (tx.type == TransactionType.TRANSFER && tx.toAccountName.equals(wallet.name, ignoreCase = true))
            val matchPeriod = tx.monthIndex == selectedMonth && tx.year == selectedYear
            matchAccount && matchPeriod
        }.sortedWith(TransactionMathHelper.transactionChronologicalDescendingComparator)
    }

    val dynamicIncome = remember(accountTransactions, wallet.name) {
        accountTransactions.filter {
            it.type == TransactionType.INCOME ||
                    (it.type == TransactionType.TRANSFER && it.toAccountName.equals(wallet.name, ignoreCase = true))
        }.sumOf { it.amount }
    }

    val dynamicExpense = remember(accountTransactions, wallet.name) {
        accountTransactions.filter {
            it.type == TransactionType.EXPENSE
        }.sumOf { it.amount }
    }

    val dynamicTransferIn = remember(accountTransactions, wallet.name) {
        accountTransactions.filter {
            it.type == TransactionType.TRANSFER && it.toAccountName.equals(wallet.name, ignoreCase = true)
        }.sumOf { it.amount }
    }

    val dynamicTransferOut = remember(accountTransactions, wallet.name) {
        accountTransactions.filter {
            it.type == TransactionType.TRANSFER && it.accountName.equals(wallet.name, ignoreCase = true)
        }.sumOf { it.amount + it.transferFee }
    }

    val displayBalance = wallet.balance
    val displayIncome = dynamicIncome
    val displayExpense = dynamicExpense
    val displayTransferIn = dynamicTransferIn
    val displayTransferOut = dynamicTransferOut
    val transactionCount = accountTransactions.size

    // Expense Breakdown (Total is exact sum of categories, percentages sum to 100%)
    val (expenseTotal, expenseCategories) = remember(accountTransactions) {
        TransactionMathHelper.buildCategoryBreakdown(
            transactions = accountTransactions,
            type = TransactionType.EXPENSE
        )
    }

    // Income Breakdown (Total is exact sum of categories, percentages sum to 100%)
    val (incomeTotal, incomeCategories) = remember(accountTransactions) {
        TransactionMathHelper.buildCategoryBreakdown(
            transactions = accountTransactions,
            type = TransactionType.INCOME
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTxDialog = true },
                containerColor = AccentBlue,
                contentColor = Color.White,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(6.dp),
                modifier = Modifier
                    .testTag("btn_account_detail_fab")
                    .size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Transaksi",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        bottomBar = {
            AccountDetailBottomBar(
                activeTab = activeBottomTab,
                onTabSelect = { activeBottomTab = it }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            // ---------------------------------------------------------
            // 1. Header Toolbar & Navigasi Atas
            // ---------------------------------------------------------
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = DarkSurfaceElevated,
                border = BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("btn_account_detail_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = TextWhite,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Logo Bank / E-Wallet Badge (Click to replace logo)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                walletImagePickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                    ) {
                        InstitutionLogoBadge(
                            walletName = wallet.name,
                            iconName = wallet.iconName,
                            size = 36.dp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = wallet.name,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "${wallet.bankType} · IDR",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                // Edit Pencil Icon
                IconButton(
                    onClick = { showEditWalletDialog = true },
                    modifier = Modifier.testTag("btn_account_detail_edit")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Akun",
                        tint = TextWhite,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

            // Filter Periode Bulan (Horizontal Chips)
            val monthScrollState = rememberScrollState()
            LaunchedEffect(monthOptions) {
                monthScrollState.scrollTo(monthScrollState.maxValue)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(monthScrollState)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                monthOptions.forEach { option ->
                    val isSelected = selectedMonth == option.monthIndex && selectedYear == option.year
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) AccentBlue else DarkSurfaceElevated,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) AccentBlue else DarkCardBorder
                        ),
                        modifier = Modifier.clickable {
                            viewModel.selectMonthAndYear(option.monthIndex, option.year)
                        }
                    ) {
                        Text(
                            text = option.label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else TextMuted,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            }

            // ---------------------------------------------------------
            // 2. Kartu Saldo Utama (Top Card - Dark Blue Container)
            // ---------------------------------------------------------
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A2138)),
                border = BorderStroke(1.dp, Color(0xFF283556))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF1E294B), Color(0xFF141C31))
                            )
                        )
                        .padding(16.dp)
                ) {
                    // Total Saldo Header & Action Icons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Total Saldo",
                                fontSize = 12.sp,
                                color = Color(0xFF93C5FD),
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isBalanceHidden) "••••••••" else CurrencyHelper.formatRupiah(displayBalance),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Top Right Action Badges
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Trophy / Coin Icon
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF263353),
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.WorkspacePremium,
                                        contentDescription = "Trophy",
                                        tint = TrophyYellow,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            // Transaction Count Badge
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF263353),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Receipt,
                                        contentDescription = "Mutasi",
                                        tint = Color(0xFF93C5FD),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$transactionCount",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            // Eye Toggle Icon (Hide / Show)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF263353),
                                modifier = Modifier
                                    .size(34.dp)
                                    .clickable { viewModel.toggleBalanceVisibility() }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isBalanceHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Sembunyikan Saldo",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Kotak Ringkasan Cepat: Pemasukan & Pengeluaran
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Kotak Pemasukan
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF1E2F48))
                                .border(1.dp, Color(0xFF2E4366), RoundedCornerShape(14.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF0E4438)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                        contentDescription = null,
                                        tint = Color(0xFF2ECC71),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Pemasukan",
                                        fontSize = 11.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                    Text(
                                        text = if (isBalanceHidden) "••••••" else CurrencyHelper.formatRupiah(displayIncome),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        // Kotak Pengeluaran
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF1E2F48))
                                .border(1.dp, Color(0xFF2E4366), RoundedCornerShape(14.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF4A202E)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                                        contentDescription = null,
                                        tint = Color(0xFFF43F5E),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Pengeluaran",
                                        fontSize = 11.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                    Text(
                                        text = if (isBalanceHidden) "••••••" else CurrencyHelper.formatRupiah(displayExpense),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Informasi Transfer
                    Text(
                        text = "Transfer: Masuk +${CurrencyHelper.formatRupiah(displayTransferIn)} · Keluar -${CurrencyHelper.formatRupiah(displayTransferOut)}",
                        fontSize = 11.sp,
                        color = Color(0xFF93C5FD).copy(alpha = 0.85f),
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            // ---------------------------------------------------------
            // 3. Tab Konten: Ringkasan vs Transaksi
            // ---------------------------------------------------------
            AnimatedContent(
                targetState = activeBottomTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "AccountTabTransition"
            ) { targetTab ->
                if (targetTab == 0) {
                    // TAB 1: RINGKASAN (DONUT CHARTS & BREAKDOWN)
                    AccountSummaryTab(
                        expenseTotal = expenseTotal,
                        expenseSlices = expenseCategories,
                        incomeTotal = incomeTotal,
                        incomeSlices = incomeCategories
                    )
                } else {
                    // TAB 2: TRANSAKSI (MUTASI HISTORI)
                    AccountTransactionListTab(
                        accountName = wallet.name,
                        transactions = accountTransactions,
                        onTransactionClick = { activeTxToEdit = it }
                    )
                }
            }
        }
    }

    // Dialogs
    if (showAddTxDialog) {
        AddTransactionDialog(
            viewModel = viewModel,
            initialWallet = wallet.name,
            onDismiss = { showAddTxDialog = false }
        )
    }

    if (showEditWalletDialog) {
        CreateWalletDialog(
            viewModel = viewModel,
            walletToEdit = wallet,
            onDismiss = { showEditWalletDialog = false }
        )
    }

    activeTxToEdit?.let { tx ->
        EditTransactionDialog(
            transaction = tx,
            viewModel = viewModel,
            onDismiss = { activeTxToEdit = null }
        )
    }
}

// -----------------------------------------------------------------------------
// TAB 1: RINGKASAN (ANALITIK PENGELUARAN & PEMASUKAN DENGAN DONUT CHART)
// -----------------------------------------------------------------------------
@Composable
private fun AccountSummaryTab(
    expenseTotal: Double,
    expenseSlices: List<CategorySlice>,
    incomeTotal: Double,
    incomeSlices: List<CategorySlice>
) {
    if (expenseSlices.isEmpty() && incomeSlices.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, DarkCardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 44.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E2638)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tidak Ada Data Analitik",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Belum ada pencatatan transaksi pada periode bulan ini.",
                        fontSize = 13.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(2.dp)) }

            // 1. BAGIAN PENGELUARAN (Donut Chart + List)
            if (expenseSlices.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = BorderStroke(1.dp, DarkCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Pengeluaran Berdasarkan Kategori",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            // Donut Chart Container
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                DonutChart(
                                    slices = expenseSlices,
                                    modifier = Modifier.size(160.dp),
                                    strokeWidth = 26.dp
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Pengeluaran",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                    Text(
                                        text = CurrencyHelper.formatRupiah(expenseTotal),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Breakdown List with Progress Bars
                            expenseSlices.forEach { slice ->
                                CategoryBreakdownItem(slice = slice)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }

            // 2. BAGIAN PEMASUKAN (Donut Chart + List)
            if (incomeSlices.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = BorderStroke(1.dp, DarkCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Pemasukan Berdasarkan Kategori",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            // Donut Chart Container
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                DonutChart(
                                    slices = incomeSlices,
                                    modifier = Modifier.size(160.dp),
                                    strokeWidth = 26.dp
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Pemasukan",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                    Text(
                                        text = CurrencyHelper.formatRupiah(incomeTotal),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Breakdown List with Progress Bars
                            incomeSlices.forEach { slice ->
                                CategoryBreakdownItem(slice = slice)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun CategoryBreakdownItem(slice: CategorySlice) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(slice.color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = slice.category,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextWhite
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${String.format("%.1f", slice.percentage)}%",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            Text(
                text = CurrencyHelper.formatRupiah(slice.amount),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextWhite
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { (slice.percentage / 100f).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = slice.color,
            trackColor = Color(0xFF222838)
        )
    }
}

@Composable
private fun DonutChart(
    slices: List<CategorySlice>,
    modifier: Modifier = Modifier,
    strokeWidth: androidx.compose.ui.unit.Dp = 24.dp
) {
    Canvas(modifier = modifier) {
        val total = slices.sumOf { it.percentage.toDouble() }.toFloat().coerceAtLeast(1f)
        var currentAngle = -90f
        val stroke = strokeWidth.toPx()
        val diameter = size.minDimension - stroke
        val topLeft = Offset(
            (size.width - diameter) / 2f,
            (size.height - diameter) / 2f
        )
        val arcSize = Size(diameter, diameter)

        slices.forEach { slice ->
            val sweepAngle = (slice.percentage / total) * 360f
            drawArc(
                color = slice.color,
                startAngle = currentAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Butt)
            )
            currentAngle += sweepAngle
        }
    }
}

// -----------------------------------------------------------------------------
// TAB 2: TRANSAKSI (MUTASI HISTORI BERDASARKAN TANGGAL)
// -----------------------------------------------------------------------------
@Composable
private fun AccountTransactionListTab(
    accountName: String,
    transactions: List<Transaction>,
    onTransactionClick: (Transaction) -> Unit
) {
    if (transactions.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, DarkCardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 44.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E2638)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Tidak Ada Transaksi",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Belum ada riwayat transaksi pada periode bulan ini.",
                        fontSize = 13.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    } else {
        val grouped = remember(transactions) {
            transactions.groupBy { tx ->
                "${tx.dayOfMonth} ${tx.dayOfWeekName}  ${tx.monthIndex}/${tx.year % 100}"
            }.mapValues { (_, list) ->
                list.sortedWith { a, b ->
                    val timeA = TransactionMathHelper.parseTimeToSeconds(a.time)
                    val timeB = TransactionMathHelper.parseTimeToSeconds(b.time)
                    val cmp = timeB.compareTo(timeA)
                    if (cmp != 0) cmp else b.id.compareTo(a.id)
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { Spacer(modifier = Modifier.height(2.dp)) }

            grouped.forEach { (dateHeader, txsInGroup) ->
                item {
                    val daySumExpense = txsInGroup.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                    val daySumIncome = txsInGroup.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = dateHeader,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (daySumIncome > 0.0) {
                                Text(
                                    text = "+${CurrencyHelper.formatRupiah(daySumIncome)}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF2ECC71)
                                )
                            }
                            if (daySumExpense > 0.0) {
                                Text(
                                    text = "-${CurrencyHelper.formatRupiah(daySumExpense)}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFF43F5E)
                                )
                            }
                        }
                    }
                }

                items(txsInGroup) { tx ->
                    val isIncome = tx.type == TransactionType.INCOME
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTransactionClick(tx) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = BorderStroke(1.dp, DarkCardBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(if (isIncome) Color(0xFF0F3D32) else Color(0xFF242B3D)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = getCategoryIcon(tx.category),
                                        contentDescription = tx.category,
                                        tint = if (isIncome) Color(0xFF2ECC71) else Color(0xFF94A3B8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        text = tx.title.ifBlank { tx.category },
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextWhite
                                    )
                                    Spacer(modifier = Modifier.height(1.dp))
                                    Text(
                                        text = "${tx.category} · $accountName",
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                    if (tx.label.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        val labelList = remember(tx.label) {
                                            tx.label.split(",").map { it.trim() }.filter { it.isNotBlank() }
                                        }
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                                        ) {
                                            labelList.forEach { singleLabel ->
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = Color(0xFF1E2435),
                                                    border = BorderStroke(0.5.dp, Color(0xFF2A344A))
                                                ) {
                                                    Text(
                                                        text = singleLabel,
                                                        fontSize = 8.5.sp,
                                                        color = Color(0xFFFCD34D),
                                                        fontWeight = FontWeight.Medium,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Text(
                                text = "${if (isIncome) "+" else "-"}${CurrencyHelper.formatRupiah(tx.amount)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isIncome) Color(0xFF2ECC71) else Color(0xFFF43F5E)
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

// -----------------------------------------------------------------------------
// BOTTOM NAVIGATION BAR (2 MENU: RINGKASAN & TRANSAKSI) - LEBIH BERPORSI
// -----------------------------------------------------------------------------
@Composable
private fun AccountDetailBottomBar(
    activeTab: Int,
    onTabSelect: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = Color(0xFF111420),
        border = BorderStroke(1.dp, Color(0xFF1E2638)),
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 1: Ringkasan
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onTabSelect(0) }
                    .testTag("tab_account_detail_ringkasan"),
                shape = RoundedCornerShape(14.dp),
                color = if (activeTab == 0) Color(0xFF0284C7) else Color(0xFF181D2C),
                border = BorderStroke(
                    1.dp,
                    if (activeTab == 0) Color(0xFF38BDF8) else Color(0xFF263047)
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PieChart,
                        contentDescription = "Ringkasan",
                        tint = if (activeTab == 0) Color.White else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ringkasan",
                        fontSize = 14.sp,
                        fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (activeTab == 0) Color.White else TextMuted
                    )
                }
            }

            // Tab 2: Transaksi
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onTabSelect(1) }
                    .testTag("tab_account_detail_transaksi"),
                shape = RoundedCornerShape(14.dp),
                color = if (activeTab == 1) Color(0xFF0284C7) else Color(0xFF181D2C),
                border = BorderStroke(
                    1.dp,
                    if (activeTab == 1) Color(0xFF38BDF8) else Color(0xFF263047)
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = "Transaksi",
                        tint = if (activeTab == 1) Color.White else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Transaksi",
                        fontSize = 14.sp,
                        fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (activeTab == 1) Color.White else TextMuted
                    )
                }
            }
        }
    }
}
