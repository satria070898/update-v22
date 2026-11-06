package com.example.ui.screens

import com.example.ui.screens.summary.SummaryLaporanView
import com.example.ui.screens.summary.SummaryStatistikView
import com.example.ui.screens.summary.SummaryKalenderView
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.roundToInt
import com.example.ui.components.ChartHistoricalDetailCard
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import com.example.ui.dialogs.PdfExportSuccessDialog
import com.example.util.PdfExportResult
import com.example.util.PdfReportHelper
import com.example.ui.theme.DarkCardBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import com.example.data.model.Transaction
import com.example.data.model.TransactionType
import com.example.ui.dialogs.MonthYearPickerDialog
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentBlueBright
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.getTopHeaderGradient
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.IncomeGreenSoft
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.FinanceViewModel
import com.example.util.CategorySlice
import com.example.util.ChartDataHelper
import com.example.util.ChartPeriod
import com.example.util.CurrencyHelper
import com.example.util.TransactionMathHelper
import com.example.util.CategoryIconHelper

@Composable
fun SummaryScreen(
    viewModel: FinanceViewModel,
    modifier: Modifier = Modifier,
    onNavigateToTransactions: (category: String?) -> Unit = {},
    onNavigateToAccount: (walletName: String) -> Unit = {},
    onNavigateToAssets: () -> Unit = {},
    onNavigateToDebts: () -> Unit = {},
    onNavigateToPlanner: () -> Unit = {},
    onNavigateToBudgets: () -> Unit = {},
    onAddTransaction: (category: String?) -> Unit = {},
    onTransactionClick: (Transaction) -> Unit = {}
) {
    val context = LocalContext.current
    val selectedMonth by viewModel.selectedMonthIndex.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()
    val monthOptions by viewModel.monthOptions.collectAsState()
    val selectedWallet by viewModel.selectedWalletFilter.collectAsState()
    val wallets by viewModel.wallets.collectAsState()
    val summaryTab by viewModel.summaryTab.collectAsState()
    val summaryPeriod by viewModel.summaryPeriod.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()
    val recurringTransactions by viewModel.recurringTransactions.collectAsState()
    val assets by viewModel.assets.collectAsState()

    var showWalletDropdown by remember { mutableStateOf(false) }
    var isPlannedActive by remember { mutableStateOf(true) }
    var showMonthYearPicker by remember { mutableStateOf(false) }
    var chartPeriod by remember { mutableStateOf(ChartPeriod.ONE_MONTH) }
    var selectedCalendarDay by remember { mutableIntStateOf(1) }

    // Filter transactions based on selected period and wallet
    val periodTransactions = remember(allTransactions, selectedMonth, selectedYear, selectedWallet, summaryPeriod) {
        allTransactions.filter { tx ->
            val matchPeriod = when (summaryPeriod) {
                0 -> tx.monthIndex == selectedMonth && tx.year == selectedYear // Bulanan
                1 -> tx.year == selectedYear // Tahunan
                else -> true // Kustom / Semua
            }
            val matchWallet = selectedWallet == "Semua" || tx.accountName.equals(selectedWallet, ignoreCase = true)
            matchPeriod && matchWallet
        }
    }

    val (computedIncomeTotal, incomeBreakdown) = remember(periodTransactions) {
        TransactionMathHelper.buildCategoryBreakdown(periodTransactions, TransactionType.INCOME)
    }

    val (computedExpenseTotal, expenseBreakdown) = remember(periodTransactions) {
        TransactionMathHelper.buildCategoryBreakdown(periodTransactions, TransactionType.EXPENSE)
    }

    val currentIncome = computedIncomeTotal
    val currentExpense = computedExpenseTotal
    val netCashFlow = currentIncome - currentExpense

    val chartData = remember(periodTransactions, chartPeriod, selectedMonth, selectedYear) {
        ChartDataHelper.getExpenseTrend(periodTransactions, chartPeriod, selectedMonth, selectedYear)
    }

    val cumulativeData = remember(allTransactions, selectedMonth, selectedYear, selectedWallet) {
        ChartDataHelper.getCumulativeComparison(allTransactions, selectedMonth, selectedYear, selectedWallet)
    }

    // Category breakdown for existing views (StatistikView)
    val categoryBreakdown: List<Triple<String, Double, Float>> = remember(expenseBreakdown, currentExpense) {
        expenseBreakdown.map { slice: CategorySlice ->
            val ratio = if (currentExpense > 0) (slice.amount / currentExpense).toFloat() else 0f
            Triple(slice.category, slice.amount, ratio)
        }
    }

    val selectedMonthLabel = "${monthOptions.find { it.first == selectedMonth }?.second ?: "Okt"} $selectedYear"
    val savingsRate = if (currentIncome > 0) (((currentIncome - currentExpense) / currentIncome) * 100).toInt() else 0
    var pdfExportResult by remember { mutableStateOf<PdfExportResult?>(null) }

    val handleExportPdf: () -> Unit = {
        val result = PdfReportHelper.generateAndSavePdf(
            context = context,
            periodLabel = selectedMonthLabel,
            walletName = selectedWallet,
            incomeTotal = currentIncome,
            expenseTotal = currentExpense,
            netCashFlow = netCashFlow,
            savingsRate = savingsRate,
            expenseBreakdown = expenseBreakdown,
            incomeBreakdown = incomeBreakdown,
            transactions = periodTransactions
        )
        if (result != null) {
            pdfExportResult = result
            Toast.makeText(context, "Laporan PDF berhasil disimpan!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Gagal membuat laporan PDF", Toast.LENGTH_SHORT).show()
        }
    }

    var csvExportResult by remember { mutableStateOf<com.example.util.CsvExportResult?>(null) }

    val handleExportCsv: () -> Unit = {
        val result = com.example.util.CsvExportHelper.generateAndSaveCsv(
            context = context,
            periodLabel = selectedMonthLabel,
            transactions = periodTransactions
        )
        if (result != null) {
            csvExportResult = result
            Toast.makeText(context, "Data Excel/CSV berhasil diekspor!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Gagal membuat file CSV", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(getTopHeaderGradient(AccentBlue))
    ) {
        // Header with status bar padding for edge-to-edge fullscreen
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Ringkasan",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Quick PDF Export Button
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurfaceElevated,
                    border = BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .clickable { handleExportPdf() }
                        .padding(end = 6.dp)
                        .testTag("btn_export_pdf_header")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "Simpan Laporan PDF",
                            tint = AccentBlueBright,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "PDF",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentBlueBright
                        )
                    }
                }

                // Quick CSV/Excel Export Button
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurfaceElevated,
                    border = BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .clickable { handleExportCsv() }
                        .padding(end = 8.dp)
                        .testTag("btn_export_csv_header")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "Ekspor Spreadsheet Excel/CSV",
                            tint = IncomeGreen,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Excel",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = IncomeGreen
                        )
                    }
                }

                // Wallet Filter
                Box {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = DarkSurfaceElevated,
                        modifier = Modifier.clickable { showWalletDropdown = true }
                    ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedWallet,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextWhite
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = showWalletDropdown,
                    onDismissRequest = { showWalletDropdown = false },
                    modifier = Modifier.background(DarkSurfaceElevated)
                ) {
                    DropdownMenuItem(
                        text = { Text("Semua", color = TextWhite) },
                        onClick = {
                            viewModel.selectWalletFilter("Semua")
                            showWalletDropdown = false
                        }
                    )
                    wallets.forEach { wallet ->
                        DropdownMenuItem(
                            text = { Text(wallet.name, color = TextWhite) },
                            onClick = {
                                viewModel.selectWalletFilter(wallet.name)
                                showWalletDropdown = false
                            }
                        )
                    }
                }
            }
        }
    }

        // Month Selector: Fixed Calendar Button on the left + Horizontally scrollable Month Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Fixed Calendar Button
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurfaceElevated,
                modifier = Modifier
                    .padding(start = 16.dp, end = 8.dp)
                    .clickable { showMonthYearPicker = true }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Pilih Bulan dan Tahun",
                        tint = AccentBlueBright,
                        modifier = Modifier.size(16.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Scrollable Month Chips
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState())
                    .padding(end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                monthOptions.forEach { (index, label) ->
                    val isSelected = selectedMonth == index
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (isSelected) AccentBlue else DarkSurfaceElevated,
                        modifier = Modifier.clickable { viewModel.selectMonth(index) }
                    ) {
                        Text(
                            text = label,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else TextMuted,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Sub-tabs row: Statistik, Laporan, Kalender
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val tabs = listOf(
                Triple(0, "Statistik", Icons.Default.BarChart),
                Triple(1, "Laporan", Icons.Default.Description),
                Triple(2, "Kalender", Icons.Default.CalendarMonth)
            )

            tabs.forEach { (index, title, icon) ->
                val isSelected = summaryTab == index
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) AccentBlue else DarkSurfaceElevated,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.setSummaryTab(index) }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) Color.White else TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextMuted
                        )
                    }
                }
            }
        }

        // TAB CONTENT
        when (summaryTab) {
            0 -> {
                // STATISTIK TAB (20 analytical cards, side-by-side trends, Sankey money flow, etc.)
                SummaryStatistikView(
                    selectedMonth = selectedMonth,
                    selectedYear = selectedYear,
                    allTransactions = allTransactions,
                    periodTransactions = periodTransactions,
                    recurringTransactions = recurringTransactions,
                    wallets = wallets,
                    currentIncome = currentIncome,
                    currentExpense = currentExpense,
                    expenseSlices = expenseBreakdown,
                    incomeSlices = incomeBreakdown,
                    onNavigateToTransactions = onNavigateToTransactions,
                    onNavigateToAccount = onNavigateToAccount,
                    onNavigateToAssets = onNavigateToAssets,
                    onNavigateToDebts = onNavigateToDebts,
                    onNavigateToPlanner = onNavigateToPlanner,
                    onNavigateToBudgets = onNavigateToBudgets,
                    onAddTransaction = onAddTransaction,
                    onTransactionClick = onTransactionClick
                )
            }
            1 -> {
                // LAPORAN TAB (Buku harian, Bulanan with 8 cards, Tahunan, Kustom Range Picker)
                SummaryLaporanView(
                    selectedMonth = selectedMonth,
                    selectedYear = selectedYear,
                    monthOptions = monthOptions,
                    summaryPeriod = summaryPeriod,
                    allTransactions = allTransactions,
                    periodTransactions = periodTransactions,
                    recurringTransactions = recurringTransactions,
                    wallets = wallets,
                    assets = assets,
                    currentIncome = currentIncome,
                    currentExpense = currentExpense,
                    netCashFlow = netCashFlow,
                    chartData = chartData,
                    cumulativeData = cumulativeData,
                    expenseSlices = expenseBreakdown,
                    incomeSlices = incomeBreakdown,
                    onSummaryPeriodChange = { viewModel.setSummaryPeriod(it) },
                    onExportPdf = handleExportPdf,
                    onQuickFilterMonth = {
                        chartPeriod = ChartPeriod.ONE_MONTH
                        viewModel.setSummaryPeriod(0)
                    },
                    onQuickFilter7Days = {
                        chartPeriod = ChartPeriod.SEVEN_DAYS
                    },
                    onNavigateToTransactions = onNavigateToTransactions,
                    onNavigateToAccount = onNavigateToAccount,
                    onNavigateToAssets = onNavigateToAssets,
                    onNavigateToDebts = onNavigateToDebts,
                    onNavigateToPlanner = onNavigateToPlanner,
                    onNavigateToBudgets = onNavigateToBudgets,
                    onAddTransaction = onAddTransaction,
                    onTransactionClick = onTransactionClick,
                    onSelectMonth = { viewModel.selectMonth(it) },
                    onSelectYear = { viewModel.selectYear(it) }
                )
            }
            else -> {
                // KALENDER TAB (Month grid S S R K J S M, badges, bottom totals)
                SummaryKalenderView(
                    selectedMonth = selectedMonth,
                    selectedYear = selectedYear,
                    monthName = monthOptions.find { it.first == selectedMonth }?.second ?: "Nov",
                    transactions = periodTransactions,
                    onTransactionClick = onTransactionClick,
                    onAddTransactionForDate = { day -> onAddTransaction(null) },
                    onSelectMonth = { viewModel.selectMonth(it) },
                    onNavigateToTransactions = { onNavigateToTransactions(null) }
                )
            }
        }
    }

    if (showMonthYearPicker) {
        MonthYearPickerDialog(
            currentMonth = selectedMonth,
            currentYear = selectedYear,
            onDismiss = { showMonthYearPicker = false },
            onSelectMonthYear = { m, y ->
                viewModel.selectMonthAndYear(m, y)
            }
        )
    }

    pdfExportResult?.let { result ->
        PdfExportSuccessDialog(
            exportResult = result,
            periodLabel = selectedMonthLabel,
            onDismiss = { pdfExportResult = null }
        )
    }

    csvExportResult?.let { result ->
        com.example.ui.dialogs.CsvExportSuccessDialog(
            exportResult = result,
            periodLabel = selectedMonthLabel,
            onDismiss = { csvExportResult = null }
        )
    }
}

// -------------------------------------------------------------
// 1. STATISTIK VIEW
// -------------------------------------------------------------

@Composable
fun StatistikView(
    selectedMonth: Int,
    selectedYear: Int,
    monthOptions: List<com.example.ui.viewmodel.MonthOption>,
    summaryPeriod: Int,
    chartPeriod: ChartPeriod,
    chartData: com.example.util.ChartSeriesData,
    cumulativeData: com.example.util.CumulativeComparisonData,
    currentIncome: Double,
    currentExpense: Double,
    isPlannedActive: Boolean,
    recurringTransactions: List<com.example.data.model.RecurringTransaction>,
    categoryBreakdown: List<Triple<String, Double, Float>>,
    allTransactions: List<Transaction> = emptyList(),
    periodTransactions: List<Transaction> = emptyList(),
    onSummaryPeriodChange: (Int) -> Unit,
    onChartPeriodChange: (ChartPeriod) -> Unit,
    onPlannedActiveToggle: (Boolean) -> Unit,
    onQuickFilterMonth: () -> Unit,
    onQuickFilter7Days: () -> Unit
) {
    val monthName = monthOptions.find { it.first == selectedMonth }?.second ?: "Okt"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // "Buku harian uang Anda" Banner Card with functional filter buttons
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF1B2238),
                                    Color(0xFF151928),
                                    Color(0xFF111420)
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0x33F59E0B),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.MenuBook,
                                            contentDescription = null,
                                            tint = Color(0xFFFBBF24),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Buku harian uang Anda",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                    Text(
                                        text = "Melihat kembali dengan cermat arus keuangan Anda",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                            Text(text = "🌙", fontSize = 24.sp)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (chartPeriod == ChartPeriod.ONE_MONTH) AccentBlue else Color(0x33FFFFFF),
                                modifier = Modifier.clickable(onClick = onQuickFilterMonth)
                            ) {
                                Text(
                                    text = "🌙 $monthName $selectedYear",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextWhite,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (chartPeriod == ChartPeriod.SEVEN_DAYS) AccentBlue else Color(0x33FFFFFF),
                                modifier = Modifier.clickable(onClick = onQuickFilter7Days)
                            ) {
                                Text(
                                    text = "⭐ 7 hari terakhir",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextWhite,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Period Selector (Bulanan / Tahunan / Kustom)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sekilas",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextWhite
                )

                Row(
                    modifier = Modifier
                        .background(DarkSurfaceElevated, RoundedCornerShape(14.dp))
                        .padding(2.dp)
                ) {
                    listOf("Bulanan", "Tahunan", "Semua").forEachIndexed { index, title ->
                        val isSelected = summaryPeriod == index
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) AccentBlue else Color.Transparent,
                            modifier = Modifier.clickable { onSummaryPeriodChange(index) }
                        ) {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else TextMuted,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Card "AKTIVITAS PENGELUARAN" (Daily Trend Chart)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "AKTIVITAS PENGELUARAN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSubtle
                            )
                            Text(
                                text = "$monthName $selectedYear",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        }

                        // Period selector chips inside card
                        Row(
                            modifier = Modifier
                                .background(DarkSurfaceElevated, RoundedCornerShape(12.dp))
                                .padding(2.dp)
                        ) {
                            listOf(
                                ChartPeriod.SEVEN_DAYS,
                                ChartPeriod.ONE_WEEK,
                                ChartPeriod.ONE_MONTH,
                                ChartPeriod.ONE_YEAR
                            ).forEach { p ->
                                val isSelected = chartPeriod == p
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) AccentBlue else Color.Transparent,
                                    modifier = Modifier.clickable { onChartPeriodChange(p) }
                                ) {
                                    Text(
                                        text = p.shortLabel,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else TextMuted,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Rp${CurrencyHelper.formatCompact(chartData.total)}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Pengeluaran",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (chartData.percentChange <= 0) IncomeGreenSoft else Color(0x33F43F5E)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (chartData.percentChange <= 0) Icons.AutoMirrored.Filled.TrendingDown else Icons.Default.BarChart,
                                    contentDescription = null,
                                    tint = if (chartData.percentChange <= 0) IncomeGreen else ExpenseRed,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${if (chartData.percentChange > 0) "+" else ""}${chartData.percentChange}% vs periode lalu",
                                    fontSize = 10.sp,
                                    color = if (chartData.percentChange <= 0) IncomeGreen else ExpenseRed,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    var selectedExpensePointIndex by remember { mutableStateOf<Int?>(null) }

                    DailyExpenseLineChart(
                        points = chartData.points,
                        peakIndex = chartData.rawValues.indexOf(chartData.peakValue),
                        selectedIndex = selectedExpensePointIndex,
                        onPointSelected = { idx ->
                            selectedExpensePointIndex = if (selectedExpensePointIndex == idx) null else idx
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(125.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(chartData.startLabel, fontSize = 10.sp, color = TextSubtle)
                        Text(chartData.midLabel, fontSize = 10.sp, color = TextSubtle)
                        Text(chartData.endLabel, fontSize = 10.sp, color = TextSubtle)
                    }

                    // Interactive historical detail card for Daily Expense
                    if (selectedExpensePointIndex != null && selectedExpensePointIndex!! in chartData.rawValues.indices) {
                        val idx = selectedExpensePointIndex!!
                        val curVal = chartData.rawValues[idx]
                        val prevVal = if (idx > 0) chartData.rawValues[idx - 1] else null
                        val curLbl = chartData.pointLabels.getOrElse(idx) { "Hari ke-${idx + 1}" }
                        val prevLbl = if (idx > 0) chartData.pointLabels.getOrElse(idx - 1) { "Hari ke-$idx" } else null

                        Spacer(modifier = Modifier.height(10.dp))
                        ChartHistoricalDetailCard(
                            title = "Pengeluaran Harian",
                            currentLabel = curLbl,
                            currentValue = curVal,
                            previousLabel = prevLbl,
                            previousValue = prevVal,
                            isReverseGood = true,
                            onDismiss = { selectedExpensePointIndex = null }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3 Dynamic summary metrics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Per hari", fontSize = 11.sp, color = TextSubtle)
                            Text("Rp${CurrencyHelper.formatCompact(chartData.averagePerDay)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                        Column {
                            Text("Hari terboros", fontSize = 11.sp, color = TextSubtle)
                            Text("Rp${CurrencyHelper.formatCompact(chartData.peakValue)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            Text(chartData.peakDayLabel, fontSize = 10.sp, color = TextSubtle)
                        }
                        Column {
                            Text("Kategori teratas", fontSize = 11.sp, color = TextSubtle)
                            Text(chartData.topCategory, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            Text("Rp${CurrencyHelper.formatCompact(chartData.topCategoryAmount)}", fontSize = 10.sp, color = TextSubtle)
                        }
                    }
                }
            }
        }

        // Card "Ringkasan: Arus Kas"
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Ringkasan",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSubtle
                    )
                    Text(
                        text = "Arus Kas Periode Ini",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Income Bar
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(IncomeGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = CurrencyHelper.formatRupiah(currentIncome),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Pemasukan",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { 1.0f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = IncomeGreen,
                        trackColor = DarkSurfaceElevated
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Expense Bar
                    val expenseRatio = if (currentIncome > 0) (currentExpense / currentIncome).toFloat().coerceIn(0f, 1f) else 1f
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(ExpenseRed)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = CurrencyHelper.formatRupiah(currentExpense),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Pengeluaran",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { expenseRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = ExpenseRed,
                        trackColor = DarkSurfaceElevated
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Text analysis card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceElevated,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val net = currentIncome - currentExpense
                        val status = if (net >= 0) "positif" else "negatif"
                        val diffText = if (net >= 0) "+${CurrencyHelper.formatRupiah(net)} lebih besar dari pengeluaran" else "${CurrencyHelper.formatRupiah(-net)} lebih besar dari pemasukan"
                        Text(
                            text = "Pada $monthName $selectedYear, arus kas Anda $status. Pemasukan Anda $diffText.",
                            fontSize = 12.sp,
                            color = TextMuted,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }

        // Card "Terencana" (Functional with actual recurring transactions)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Terencana",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSubtle
                            )
                            Text(
                                text = "Transaksi Berulang Terjadwal",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        }
                        Switch(
                            checked = isPlannedActive,
                            onCheckedChange = onPlannedActiveToggle,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = AccentBlue,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = DarkSurfaceElevated
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (isPlannedActive && recurringTransactions.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            recurringTransactions.take(3).forEach { rec ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = DarkSurfaceElevated,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(rec.title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextWhite)
                                            Text("Setiap tgl ${rec.dayOfMonth} · ${rec.accountName}", fontSize = 11.sp, color = TextMuted)
                                        }
                                        Text(
                                            text = "${if (rec.type == TransactionType.INCOME) "+" else "-"}Rp${CurrencyHelper.formatCompact(rec.amount)}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (rec.type == TransactionType.INCOME) IncomeGreen else ExpenseRed
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Text(
                            text = if (isPlannedActive) "Belum ada transaksi berulang aktif untuk periode ini." else "Tampilan transaksi berulang dinonaktifkan.",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        // Card "Pendapatan vs Pengeluaran" (Cumulative) - FULLY DYNAMIC
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pendapatan vs Pengeluaran",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "Kumulatif Bulanan",
                            fontSize = 11.sp,
                            color = TextSubtle
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            val savedSign = if (cumulativeData.savedAmount >= 0) "+" else "-"
                            Text(
                                text = "${savedSign}Rp${CurrencyHelper.formatCompact(kotlin.math.abs(cumulativeData.savedAmount))}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (cumulativeData.savedAmount >= 0) IncomeGreen else ExpenseRed
                            )
                            Text(
                                text = if (cumulativeData.savedAmount >= 0) "Ditabung periode ini" else "Defisit periode ini",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurfaceElevated
                        ) {
                            Text(
                                text = "${cumulativeData.savingsRatePercent}% pemasukan tersisa",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (cumulativeData.isIncomeHigher) IncomeGreen else ExpenseRed,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp, 3.dp).background(IncomeGreen))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pemasukan Rp${CurrencyHelper.formatCompact(cumulativeData.totalIncome)}", fontSize = 11.sp, color = TextMuted)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp, 3.dp).background(ExpenseRed))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pengeluaran Rp${CurrencyHelper.formatCompact(cumulativeData.totalExpense)}", fontSize = 11.sp, color = TextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    var selectedCumulativePointIndex by remember { mutableStateOf<Int?>(null) }

                    // Dual Line Cumulative Chart with 4x4 grid and tap interactivity
                    CumulativeComparisonChart(
                        incomePoints = cumulativeData.incomePoints,
                        expensePoints = cumulativeData.expensePoints,
                        selectedIndex = selectedCumulativePointIndex,
                        onPointSelected = { idx ->
                            selectedCumulativePointIndex = if (selectedCumulativePointIndex == idx) null else idx
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(125.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("1", "8", "15", "22", "31").forEach {
                            Text(text = it, fontSize = 10.sp, color = TextSubtle)
                        }
                    }

                    // Interactive historical detail card for Cumulative Cash Flow
                    if (selectedCumulativePointIndex != null) {
                        val idx = selectedCumulativePointIndex!!
                        val curInc = cumulativeData.rawIncomeList.getOrElse(idx) { 0.0 }
                        val curExp = cumulativeData.rawExpenseList.getOrElse(idx) { 0.0 }
                        val curNet = curInc - curExp
                        val prevNet = if (idx > 0) {
                            cumulativeData.rawIncomeList.getOrElse(idx - 1) { 0.0 } - cumulativeData.rawExpenseList.getOrElse(idx - 1) { 0.0 }
                        } else null
                        val curLbl = cumulativeData.dayLabels.getOrElse(idx) { "Hari ke-${idx + 1}" }
                        val prevLbl = if (idx > 0) cumulativeData.dayLabels.getOrElse(idx - 1) { "Hari ke-$idx" } else null

                        Spacer(modifier = Modifier.height(10.dp))
                        ChartHistoricalDetailCard(
                            title = "Kas Bersih Kumulatif",
                            currentLabel = curLbl,
                            currentValue = curNet,
                            previousLabel = prevLbl,
                            previousValue = prevNet,
                            isReverseGood = false,
                            onDismiss = { selectedCumulativePointIndex = null }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = if (cumulativeData.isIncomeHigher) "📈" else "📉", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = cumulativeData.summaryTakeaway,
                            fontSize = 11.sp,
                            color = if (cumulativeData.isIncomeHigher) IncomeGreen else ExpenseRed
                        )
                    }
                }
            }
        }

        // Card "Evaluasi Aturan 50 / 30 / 20"
        item {
            val (needsVal, wantsVal, savingsVal) = remember(periodTransactions, currentIncome, currentExpense) {
                var needs = 0.0
                var wants = 0.0
                val wantKeywords = listOf("hiburan", "kafe", "kopi", "nongkrong", "liburan", "jalan", "hobi", "hadiah", "online", "game", "nonton", "bioskop", "fashion", "baju", "gadget", "belanja online")
                periodTransactions.filter { it.type == TransactionType.EXPENSE }.forEach { tx ->
                    val catLower = tx.category.lowercase()
                    if (wantKeywords.any { catLower.contains(it) }) {
                        wants += tx.amount
                    } else {
                        needs += tx.amount
                    }
                }
                val savings = (currentIncome - currentExpense).coerceAtLeast(0.0)
                Triple(needs, wants, savings)
            }
            val totalRef = (needsVal + wantsVal + savingsVal).coerceAtLeast(1.0)
            val needsPct = ((needsVal / totalRef) * 100).toInt()
            val wantsPct = ((wantsVal / totalRef) * 100).toInt()
            val savingsPct = ((savingsVal / totalRef) * 100).toInt()

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ALOKASI KEUANGAN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSubtle
                            )
                            Text(
                                text = "Aturan Emas 50 / 30 / 20",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        }
                        val statusLabel = when {
                            savingsPct >= 20 && needsPct <= 55 -> "Sangat Sehat 🌟"
                            wantsPct > 35 -> "Keinginan Tinggi ⚠️"
                            needsPct > 60 -> "Kebutuhan Dominan ℹ️"
                            else -> "Cukup Baik 👍"
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DarkSurfaceElevated
                        ) {
                            Text(
                                text = statusLabel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextWhite,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Needs bar
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Kebutuhan (Target ≤ 50%)", fontSize = 12.sp, color = TextWhite)
                            Text("$needsPct% • Rp${CurrencyHelper.formatCompact(needsVal)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { (needsPct / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = if (needsPct <= 50) IncomeGreen else Color(0xFFF59E0B),
                            trackColor = DarkSurfaceElevated
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Wants bar
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Keinginan (Target ≤ 30%)", fontSize = 12.sp, color = TextWhite)
                            Text("$wantsPct% • Rp${CurrencyHelper.formatCompact(wantsVal)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { (wantsPct / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = if (wantsPct <= 30) AccentBlue else ExpenseRed,
                            trackColor = DarkSurfaceElevated
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Savings bar
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Tabungan & Investasi (Target ≥ 20%)", fontSize = 12.sp, color = TextWhite)
                            Text("$savingsPct% • Rp${CurrencyHelper.formatCompact(savingsVal)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { (savingsPct / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = if (savingsPct >= 20) IncomeGreen else Color(0xFF94A3B8),
                            trackColor = DarkSurfaceElevated
                        )
                    }
                }
            }
        }

        // Card "Komparasi vs Bulan Lalu" (Month-over-Month Comparison)
        item {
            val (prevExp, prevInc, expDeltaPct, incDeltaPct) = remember(allTransactions, selectedMonth, selectedYear, currentExpense, currentIncome) {
                val prevMonth = if (selectedMonth > 1) selectedMonth - 1 else 12
                val prevYear = if (selectedMonth > 1) selectedYear else selectedYear - 1
                var pExp = 0.0
                var pInc = 0.0
                allTransactions.forEach { tx ->
                    if (tx.monthIndex == prevMonth && tx.year == prevYear) {
                        if (tx.type == TransactionType.EXPENSE) pExp += tx.amount
                        else if (tx.type == TransactionType.INCOME) pInc += tx.amount
                    }
                }
                val eDelta = if (pExp > 0) (((currentExpense - pExp) / pExp) * 100).toInt() else if (currentExpense > 0) 100 else 0
                val iDelta = if (pInc > 0) (((currentIncome - pInc) / pInc) * 100).toInt() else if (currentIncome > 0) 100 else 0
                listOf(pExp, pInc, eDelta, iDelta)
            }

            val expTrendDir = when {
                expDeltaPct.toInt() > 0 -> 1
                expDeltaPct.toInt() < 0 -> -1
                else -> 0
            }
            val incTrendDir = when {
                incDeltaPct.toInt() > 0 -> 1
                incDeltaPct.toInt() < 0 -> -1
                else -> 0
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PERBANDINGAN BULANAN",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSubtle
                    )
                    Text(
                        text = "Performa vs Bulan Lalu",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Expense box
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurfaceElevated,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Pengeluaran", fontSize = 11.sp, color = TextMuted)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Rp${CurrencyHelper.formatCompact(currentExpense)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val expColor = ChartDataHelper.getTrendColor(expTrendDir)
                                    val expSign = if (expDeltaPct.toInt() > 0) "+" else ""
                                    Text(
                                        text = "$expSign$expDeltaPct% vs lalu",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = expColor
                                    )
                                }
                            }
                        }

                        // Income box
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurfaceElevated,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Pemasukan", fontSize = 11.sp, color = TextMuted)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Rp${CurrencyHelper.formatCompact(currentIncome)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val incColor = ChartDataHelper.getTrendColor(incTrendDir)
                                    val incSign = if (incDeltaPct.toInt() > 0) "+" else ""
                                    Text(
                                        text = "$incSign$incDeltaPct% vs lalu",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = incColor
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Card "Pengeluaran Berdasarkan Kategori"
        if (categoryBreakdown.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Kategori Pengeluaran Terbesar", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        Spacer(modifier = Modifier.height(12.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            categoryBreakdown.take(5).forEach { (catName, sum, ratio) ->
                                val catIcon = CategoryIconHelper.getCategoryIcon(catName)
                                val catColor = CategoryIconHelper.getCategoryColor(catName, TransactionType.EXPENSE)
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(catColor.copy(alpha = 0.15f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = catIcon,
                                                    contentDescription = null,
                                                    tint = catColor,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                            }
                                            Text(catName, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                        Text(
                                            CurrencyHelper.formatRupiah(sum),
                                            color = TextWhite,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = { ratio },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(5.dp)
                                            .clip(RoundedCornerShape(2.5.dp)),
                                        color = AccentBlue,
                                        trackColor = DarkSurfaceElevated
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// -------------------------------------------------------------
// DONUT CHART FOR SUMMARY
// -------------------------------------------------------------

@Composable
fun SummaryDonutChart(
    slices: List<CategorySlice>,
    totalText: String,
    labelText: String,
    modifier: Modifier = Modifier,
    strokeWidth: androidx.compose.ui.unit.Dp = 22.dp
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val total = slices.sumOf { it.percentage.toDouble() }.toFloat().coerceAtLeast(1f)
            var currentAngle = -90f
            val stroke = strokeWidth.toPx()
            val diameter = size.minDimension - stroke
            val topLeft = Offset(
                (size.width - diameter) / 2f,
                (size.height - diameter) / 2f
            )
            val arcSize = Size(diameter, diameter)

            if (slices.isEmpty()) {
                drawArc(
                    color = Color(0x33FFFFFF),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )
            } else {
                slices.forEach { slice ->
                    val sweepAngle = (slice.percentage / total) * 360f
                    if (sweepAngle > 0f) {
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
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = labelText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = totalText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                textAlign = TextAlign.Center
            )
        }
    }
}

// -------------------------------------------------------------
// 2. LAPORAN VIEW
// -------------------------------------------------------------

@Composable
fun LaporanView(
    selectedMonthLabel: String,
    selectedWallet: String,
    currentIncome: Double,
    currentExpense: Double,
    netCashFlow: Double,
    transactions: List<Transaction>,
    expenseSlices: List<CategorySlice>,
    incomeSlices: List<CategorySlice>,
    onExportPdf: () -> Unit = {}
) {
    val context = LocalContext.current
    val savingsRate = if (currentIncome > 0) (((currentIncome - currentExpense) / currentIncome) * 100).toInt() else 0
    var reportSubTab by remember { mutableIntStateOf(0) } // 0: Pengeluaran, 1: Pemasukan, 2: Arus Kas

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Banner: Export PDF Laporan Keuangan
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExportPdf() }
                    .testTag("card_export_pdf_banner")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(AccentBlue.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = AccentBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Simpan Laporan PDF",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = "Ekspor rekapitulasi $selectedMonthLabel (.pdf)",
                                fontSize = 11.sp,
                                color = TextSubtle
                            )
                        }
                    }

                    Button(
                        onClick = onExportPdf,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentBlue,
                            contentColor = Color.White
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp).testTag("btn_export_pdf_laporan")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Simpan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Sub-Navigation Tabs inside Laporan: Pengeluaran, Pemasukan, Arus Kas
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceElevated, RoundedCornerShape(14.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    Triple(0, "Pengeluaran", ExpenseRed),
                    Triple(1, "Pemasukan", IncomeGreen),
                    Triple(2, "Arus Kas", AccentBlue)
                ).forEach { (idx, title, tabColor) ->
                    val isSelected = reportSubTab == idx
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) tabColor else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { reportSubTab = idx }
                    ) {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextMuted,
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        when (reportSubTab) {
            0 -> {
                // TAB 1: PENGELUARAN (Total Pengeluaran = Sum of Categories, Donut Chart, Category list)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "RINGKASAN PENGELUARAN",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSubtle
                                    )
                                    Text(
                                        text = selectedMonthLabel,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0x33F43F5E)
                                ) {
                                    Text(
                                        text = selectedWallet,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ExpenseRed,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            SummaryDonutChart(
                                slices = expenseSlices,
                                totalText = CurrencyHelper.formatRupiah(currentExpense),
                                labelText = "Total Pengeluaran",
                                modifier = Modifier.size(190.dp)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Total: ${CurrencyHelper.formatRupiah(currentExpense)}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ExpenseRed
                            )
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Rincian per Kategori Pengeluaran",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                                Text(
                                    text = "${expenseSlices.size} Kategori",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            if (expenseSlices.isEmpty()) {
                                Text(
                                    text = "Belum ada pengeluaran pada periode ini.",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                    expenseSlices.forEach { slice ->
                                        val catIcon = CategoryIconHelper.getCategoryIcon(slice.category)
                                        Column {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(24.dp)
                                                            .clip(CircleShape)
                                                            .background(slice.color.copy(alpha = 0.18f)),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = catIcon,
                                                            contentDescription = null,
                                                            tint = slice.color,
                                                            modifier = Modifier.size(13.dp)
                                                        )
                                                    }
                                                    Text(
                                                        text = slice.category,
                                                        color = TextWhite,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                }
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Text(
                                                        text = CurrencyHelper.formatRupiah(slice.amount),
                                                        color = TextWhite,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = "(${String.format(Locale.getDefault(), "%.1f%%", slice.percentage)})",
                                                        color = TextMuted,
                                                        fontSize = 11.sp
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(5.dp))
                                            LinearProgressIndicator(
                                                progress = { (slice.percentage / 100f).coerceIn(0f, 1f) },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(6.dp)
                                                    .clip(RoundedCornerShape(3.dp)),
                                                color = slice.color,
                                                trackColor = DarkSurfaceElevated
                                            )
                                        }
                                    }

                                    HorizontalDivider(
                                        color = DarkSurfaceElevated,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Total Penjumlahan Kategori",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = TextMuted
                                        )
                                        Text(
                                            text = CurrencyHelper.formatRupiah(expenseSlices.sumOf { it.amount }),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ExpenseRed
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // TAB 2: PEMASUKAN (Total Pemasukan = Sum of Categories, Donut Chart, Category list)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "RINGKASAN PEMASUKAN",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSubtle
                                    )
                                    Text(
                                        text = selectedMonthLabel,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = IncomeGreenSoft
                                ) {
                                    Text(
                                        text = selectedWallet,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = IncomeGreen,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            SummaryDonutChart(
                                slices = incomeSlices,
                                totalText = CurrencyHelper.formatRupiah(currentIncome),
                                labelText = "Total Pemasukan",
                                modifier = Modifier.size(190.dp)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Total: ${CurrencyHelper.formatRupiah(currentIncome)}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = IncomeGreen
                            )
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Rincian per Kategori Pemasukan",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                                Text(
                                    text = "${incomeSlices.size} Kategori",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            if (incomeSlices.isEmpty()) {
                                Text(
                                    text = "Belum ada pemasukan pada periode ini.",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                    incomeSlices.forEach { slice ->
                                        val catIcon = CategoryIconHelper.getCategoryIcon(slice.category)
                                        Column {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(24.dp)
                                                            .clip(CircleShape)
                                                            .background(slice.color.copy(alpha = 0.18f)),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = catIcon,
                                                            contentDescription = null,
                                                            tint = slice.color,
                                                            modifier = Modifier.size(13.dp)
                                                        )
                                                    }
                                                    Text(
                                                        text = slice.category,
                                                        color = TextWhite,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                }
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Text(
                                                        text = CurrencyHelper.formatRupiah(slice.amount),
                                                        color = TextWhite,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = "(${String.format(Locale.getDefault(), "%.1f%%", slice.percentage)})",
                                                        color = TextMuted,
                                                        fontSize = 11.sp
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(5.dp))
                                            LinearProgressIndicator(
                                                progress = { (slice.percentage / 100f).coerceIn(0f, 1f) },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(6.dp)
                                                    .clip(RoundedCornerShape(3.dp)),
                                                color = slice.color,
                                                trackColor = DarkSurfaceElevated
                                            )
                                        }
                                    }

                                    HorizontalDivider(
                                        color = DarkSurfaceElevated,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Total Penjumlahan Kategori",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = TextMuted
                                        )
                                        Text(
                                            text = CurrencyHelper.formatRupiah(incomeSlices.sumOf { it.amount }),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = IncomeGreen
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // TAB 3: ARUS KAS (Executive Summary + Transactions)
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("LAPORAN ARUS KAS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSubtle)
                                    Text(selectedMonthLabel, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    IconButton(
                                        onClick = onExportPdf,
                                        modifier = Modifier.testTag("btn_export_pdf_card")
                                    ) {
                                        Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = "Simpan Laporan PDF", tint = AccentBlue)
                                    }
                                    IconButton(
                                        onClick = {
                                            val reportText = buildString {
                                                appendLine("📊 LAPORAN KEUANGAN FINTRACK")
                                                appendLine("Periode: $selectedMonthLabel")
                                                appendLine("Akun: $selectedWallet")
                                                appendLine("---------------------------")
                                                appendLine("💰 Total Pemasukan: ${CurrencyHelper.formatRupiah(currentIncome)}")
                                                appendLine("💸 Total Pengeluaran: ${CurrencyHelper.formatRupiah(currentExpense)}")
                                                appendLine("📈 Arus Kas Bersih: ${CurrencyHelper.formatRupiah(netCashFlow)}")
                                                appendLine("🎯 Rasio Tabungan: $savingsRate%")
                                                appendLine("---------------------------")
                                                appendLine("Rincian Pengeluaran Terbesar:")
                                                expenseSlices.take(5).forEach { slice ->
                                                    appendLine("- ${slice.category}: ${CurrencyHelper.formatRupiah(slice.amount)} (${String.format(Locale.getDefault(), "%.1f%%", slice.percentage)})")
                                                }
                                            }
                                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(Intent.EXTRA_SUBJECT, "Laporan Keuangan $selectedMonthLabel")
                                                putExtra(Intent.EXTRA_TEXT, reportText)
                                            }
                                            context.startActivity(Intent.createChooser(shareIntent, "Bagikan Laporan Keuangan"))
                                        }
                                    ) {
                                        Icon(imageVector = Icons.Default.Share, contentDescription = "Bagikan", tint = TextMuted)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = DarkSurfaceElevated,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Pemasukan", fontSize = 11.sp, color = TextMuted)
                                        Text(CurrencyHelper.formatRupiah(currentIncome), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = IncomeGreen)
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = DarkSurfaceElevated,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Pengeluaran", fontSize = 11.sp, color = TextMuted)
                                        Text(CurrencyHelper.formatRupiah(currentExpense), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ExpenseRed)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = DarkSurfaceElevated,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Saldo Bersih Arus Kas", fontSize = 11.sp, color = TextMuted)
                                        Text(
                                            CurrencyHelper.formatRupiah(netCashFlow),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (netCashFlow >= 0) IncomeGreen else ExpenseRed
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (savingsRate >= 20) IncomeGreenSoft else Color(0x33F43F5E)
                                    ) {
                                        Text(
                                            text = "Tabungan $savingsRate%",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (savingsRate >= 20) IncomeGreen else ExpenseRed,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Daftar Transaksi Periode Ini
                item {
                    Text("Semua Transaksi (${transactions.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                }

                if (transactions.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Tidak ada transaksi pada periode ini.", color = TextMuted, fontSize = 12.sp, modifier = Modifier.padding(16.dp))
                        }
                    }
                } else {
                    items(transactions.take(25)) { tx ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(tx.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                                    Text("${tx.dayOfMonth} · ${tx.category} · ${tx.accountName}", fontSize = 10.sp, color = TextMuted)
                                }
                                Text(
                                    text = "${if (tx.type == TransactionType.INCOME) "+" else if (tx.type == TransactionType.TRANSFER) "⇄ " else "-"}Rp${CurrencyHelper.formatCompact(tx.amount)}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (tx.type) {
                                        TransactionType.INCOME -> IncomeGreen
                                        TransactionType.TRANSFER -> AccentBlue
                                        TransactionType.EXPENSE -> ExpenseRed
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// -------------------------------------------------------------
// 3. KALENDER VIEW
// -------------------------------------------------------------

@Composable
fun KalenderView(
    selectedMonth: Int,
    selectedYear: Int,
    monthName: String,
    selectedDay: Int,
    transactions: List<Transaction>,
    onSelectDay: (Int) -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    var dragOffsetX by remember { mutableFloatStateOf(0f) }
    val swipeMonthModifier = Modifier.pointerInput(selectedMonth, selectedYear) {
        detectHorizontalDragGestures(
            onDragEnd = {
                if (dragOffsetX < -40f) {
                    onNextMonth()
                } else if (dragOffsetX > 40f) {
                    onPrevMonth()
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

    // Days in current month
    val daysInMonth = remember(selectedMonth, selectedYear) {
        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.YEAR, selectedYear)
        cal.set(java.util.Calendar.MONTH, selectedMonth - 1)
        cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
    }

    val dayTransactions = remember(transactions, selectedDay) {
        transactions.filter { it.dayOfMonth == selectedDay }.sortedWith { a, b ->
            val timeA = TransactionMathHelper.parseTimeToSeconds(a.time)
            val timeB = TransactionMathHelper.parseTimeToSeconds(b.time)
            val cmp = timeB.compareTo(timeA)
            if (cmp != 0) cmp else b.id.compareTo(a.id)
        }
    }

    val dayIncome = remember(dayTransactions) {
        dayTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    }
    val dayExpense = remember(dayTransactions) {
        dayTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Month Navigation Bar with Horizontal Swipe Gesture
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(swipeMonthModifier),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onPrevMonth) {
                        Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Bulan Sebelumnya", tint = TextWhite)
                    }
                    AnimatedContent(
                        targetState = "$monthName $selectedYear",
                        label = "calendar_header_slide"
                    ) { label ->
                        Text(
                            text = label,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextWhite
                        )
                    }
                    IconButton(onClick = onNextMonth) {
                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Bulan Berikutnya", tint = TextWhite)
                    }
                }
            }
        }

        // Calendar Grid with Horizontal Swipe Gesture
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(swipeMonthModifier),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Day of week headers
                    val dayHeaders = listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab")
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        dayHeaders.forEach { h ->
                            Text(
                                text = h,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSubtle,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Animated grid when month changes
                    AnimatedContent(
                        targetState = selectedMonth to selectedYear,
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
                        label = "summary_calendar_grid_slide"
                    ) { (curM, curY) ->
                        val curDaysInMonth = remember(curM, curY) {
                            val cal = java.util.Calendar.getInstance()
                            cal.set(java.util.Calendar.YEAR, curY)
                            cal.set(java.util.Calendar.MONTH, curM - 1)
                            cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            val weeks = (1..curDaysInMonth).chunked(7)
                            weeks.forEach { week ->
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    week.forEach { day ->
                                        val isSelected = day == selectedDay
                                        val hasIncome = transactions.any { it.dayOfMonth == day && it.type == TransactionType.INCOME }
                                        val hasExpense = transactions.any { it.dayOfMonth == day && it.type == TransactionType.EXPENSE }

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(42.dp)
                                                .padding(2.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSelected) AccentBlue else Color.Transparent)
                                                .clickable { onSelectDay(day) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = "$day",
                                                    fontSize = 12.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) Color.White else TextWhite
                                                )
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    if (hasIncome) {
                                                        Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(IncomeGreen))
                                                    }
                                                    if (hasExpense) {
                                                        Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(ExpenseRed))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    // Fill remaining spaces in the last week
                                    val remaining = 7 - week.size
                                    if (remaining > 0) {
                                        repeat(remaining) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Swipe hint
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
                }
            }
        }

        // Transactions on Selected Day
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tanggal $selectedDay $monthName",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            if (dayIncome > 0) {
                                Text("+Rp${CurrencyHelper.formatCompact(dayIncome)}", color = IncomeGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            if (dayExpense > 0) {
                                Text("-Rp${CurrencyHelper.formatCompact(dayExpense)}", color = ExpenseRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (dayTransactions.isEmpty()) {
                        Text(
                            text = "Tidak ada transaksi pada tanggal ini.",
                            color = TextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            dayTransactions.forEach { tx ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = DarkSurfaceElevated,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(tx.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                                            Text("${tx.category} · ${tx.accountName}", fontSize = 10.sp, color = TextMuted)
                                        }
                                        Text(
                                            text = "${if (tx.type == TransactionType.INCOME) "+" else "-"}Rp${CurrencyHelper.formatCompact(tx.amount)}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (tx.type == TransactionType.INCOME) IncomeGreen else ExpenseRed
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// -------------------------------------------------------------
// Interactive Charts
// -------------------------------------------------------------

@Composable
fun DailyExpenseLineChart(
    points: List<Float> = emptyList(),
    peakIndex: Int = -1,
    selectedIndex: Int? = null,
    onPointSelected: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val effectivePoints = if (points.size >= 2) points else listOf(0.2f, 0.35f, 0.25f, 0.4f, 0.3f)

    val tapModifier = if (onPointSelected != null) {
        Modifier.pointerInput(effectivePoints) {
            detectTapGestures { offset ->
                val stepX = size.width / (effectivePoints.size - 1).coerceAtLeast(1)
                val idx = (offset.x / stepX).roundToInt().coerceIn(0, effectivePoints.size - 1)
                onPointSelected(idx)
            }
        }
    } else Modifier

    Canvas(modifier = modifier.then(tapModifier)) {
        val width = size.width
        val height = size.height
        val stepX = width / (effectivePoints.size - 1).coerceAtLeast(1)

        // 1. Grid garis putus-putus dalam kotak grafik (3x3 Grid)
        with(ChartDataHelper) {
            drawDashedChartGrid(
                horizontalLines = 3,
                verticalLines = 3,
                gridColor = Color(0xFF334155).copy(alpha = 0.5f),
                drawFrame = true
            )
        }

        // Tentukan warna sesuai ketentuan: kalau netral abu-abu, kalau naik hijau, kalau turun merah
        val delta = if (effectivePoints.size >= 2) effectivePoints.last() - effectivePoints.first() else 0f
        val trendColor = if (delta > 0.02f) IncomeGreen else if (delta < -0.02f) ExpenseRed else Color(0xFF94A3B8)

        // Draw baseline previous period dotted line
        val prevPath = Path()
        effectivePoints.forEachIndexed { i, value ->
            val x = i * stepX
            val prevVal = (value * 0.85f + 0.05f).coerceIn(0.05f, 0.95f)
            val y = height - (prevVal * height * 0.82f)
            if (i == 0) prevPath.moveTo(x, y) else prevPath.lineTo(x, y)
        }
        drawPath(
            path = prevPath,
            color = Color(0xFF64748B),
            style = Stroke(
                width = 1.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
            )
        )

        // Draw current period filled gradient wave & solid line
        val path = Path()
        val fillPath = Path()
        fillPath.moveTo(0f, height)

        effectivePoints.forEachIndexed { i, value ->
            val x = i * stepX
            val y = height - (value * height * 0.80f).coerceAtMost(height * 0.92f).coerceAtLeast(height * 0.08f)
            if (i == 0) {
                path.moveTo(x, y)
                fillPath.lineTo(x, y)
            } else {
                path.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }

        fillPath.lineTo(width, height)
        fillPath.close()

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(trendColor.copy(alpha = 0.28f), trendColor.copy(alpha = 0.02f))
            )
        )

        drawPath(
            path = path,
            color = trendColor,
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        )

        // Highlight the peak point if no specific point is selected
        if (selectedIndex == null) {
            val validPeakIndex = if (peakIndex in effectivePoints.indices) {
                peakIndex
            } else {
                effectivePoints.indices.maxByOrNull { effectivePoints[it] } ?: (effectivePoints.size / 2)
            }
            val peakX = validPeakIndex * stepX
            val peakY = height - (effectivePoints[validPeakIndex] * height * 0.80f).coerceAtMost(height * 0.92f).coerceAtLeast(height * 0.08f)

            drawCircle(
                color = Color.White,
                radius = 5.dp.toPx(),
                center = Offset(peakX, peakY)
            )
            drawCircle(
                color = trendColor,
                radius = 3.dp.toPx(),
                center = Offset(peakX, peakY)
            )
        }

        // Highlight selected point on tap with vertical indicator guideline & halo
        if (selectedIndex != null && selectedIndex in effectivePoints.indices) {
            val selX = selectedIndex * stepX
            val selY = height - (effectivePoints[selectedIndex] * height * 0.80f).coerceAtMost(height * 0.92f).coerceAtLeast(height * 0.08f)

            // Vertical guideline (Tailwind sky blue dash)
            drawLine(
                color = Color(0xFF38BDF8).copy(alpha = 0.7f),
                start = Offset(selX, 0f),
                end = Offset(selX, height),
                strokeWidth = 1.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
            )

            // Outer halo
            drawCircle(
                color = Color(0xFF38BDF8).copy(alpha = 0.30f),
                radius = 9.dp.toPx(),
                center = Offset(selX, selY)
            )
            // Center ring
            drawCircle(
                color = Color(0xFF38BDF8),
                radius = 5.dp.toPx(),
                center = Offset(selX, selY)
            )
            // Inner white dot
            drawCircle(
                color = Color.White,
                radius = 2.dp.toPx(),
                center = Offset(selX, selY)
            )
        }
    }
}

@Composable
fun CumulativeComparisonChart(
    incomePoints: List<Float> = emptyList(),
    expensePoints: List<Float> = emptyList(),
    selectedIndex: Int? = null,
    onPointSelected: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val effIncome = if (incomePoints.size >= 2) incomePoints else listOf(0.1f, 0.4f, 0.7f, 0.85f)
    val effExpense = if (expensePoints.size >= 2) expensePoints else listOf(0.1f, 0.3f, 0.5f, 0.65f)
    val maxCount = maxOf(effIncome.size, effExpense.size)

    val tapModifier = if (onPointSelected != null) {
        Modifier.pointerInput(effIncome, effExpense) {
            detectTapGestures { offset ->
                val stepX = size.width / (maxCount - 1).coerceAtLeast(1)
                val idx = (offset.x / stepX).roundToInt().coerceIn(0, maxCount - 1)
                onPointSelected(idx)
            }
        }
    } else Modifier

    Canvas(modifier = modifier.then(tapModifier)) {
        val width = size.width
        val height = size.height

        // 1. Grid garis putus-putus dalam kotak grafik (3x3 Grid)
        with(ChartDataHelper) {
            drawDashedChartGrid(
                horizontalLines = 3,
                verticalLines = 3,
                gridColor = Color(0xFF334155).copy(alpha = 0.5f),
                drawFrame = true
            )
        }

        // Draw Income curve
        val stepXInc = width / (effIncome.size - 1).coerceAtLeast(1)
        val incomePath = Path()
        effIncome.forEachIndexed { index, normVal ->
            val x = index * stepXInc
            val y = height - (normVal * height * 0.80f).coerceIn(6f, height - 6f)
            if (index == 0) incomePath.moveTo(x, y) else incomePath.lineTo(x, y)
        }
        drawPath(
            path = incomePath,
            color = IncomeGreen,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw Expense curve
        val stepXExp = width / (effExpense.size - 1).coerceAtLeast(1)
        val expensePath = Path()
        effExpense.forEachIndexed { index, normVal ->
            val x = index * stepXExp
            val y = height - (normVal * height * 0.80f).coerceIn(6f, height - 6f)
            if (index == 0) expensePath.moveTo(x, y) else expensePath.lineTo(x, y)
        }
        drawPath(
            path = expensePath,
            color = ExpenseRed,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        // Highlight selected point on tap
        if (selectedIndex != null && selectedIndex in 0 until maxCount) {
            val selX = selectedIndex * (width / (maxCount - 1).coerceAtLeast(1))

            // Vertical indicator
            drawLine(
                color = Color(0xFF38BDF8).copy(alpha = 0.7f),
                start = Offset(selX, 0f),
                end = Offset(selX, height),
                strokeWidth = 1.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
            )

            // Income point dot
            if (selectedIndex in effIncome.indices) {
                val incY = height - (effIncome[selectedIndex] * height * 0.80f).coerceIn(6f, height - 6f)
                drawCircle(color = IncomeGreen, radius = 5.dp.toPx(), center = Offset(selX, incY))
                drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = Offset(selX, incY))
            }

            // Expense point dot
            if (selectedIndex in effExpense.indices) {
                val expY = height - (effExpense[selectedIndex] * height * 0.80f).coerceIn(6f, height - 6f)
                drawCircle(color = ExpenseRed, radius = 5.dp.toPx(), center = Offset(selX, expY))
                drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = Offset(selX, expY))
            }
        }
    }
}
