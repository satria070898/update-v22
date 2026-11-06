package com.example.ui.screens.summary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccountWallet
import com.example.data.model.AssetItem
import com.example.data.model.RecurringTransaction
import com.example.data.model.Transaction
import com.example.data.model.TransactionType
import com.example.ui.dialogs.CategoryTransactionSheet
import com.example.ui.dialogs.CustomDateRange
import com.example.ui.dialogs.DateRangePickerDialog
import com.example.ui.dialogs.FinancialHealthDetailSheet
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentBlueBright
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.IncomeGreenSoft
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.MonthOption
import com.example.util.CategorySlice
import com.example.util.CumulativeComparisonData
import com.example.util.CurrencyHelper
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs

@Composable
fun SummaryLaporanView(
    selectedMonth: Int,
    selectedYear: Int,
    monthOptions: List<MonthOption>,
    summaryPeriod: Int, // 0: Bulanan, 1: Tahunan, 2: Kustom
    allTransactions: List<Transaction>,
    periodTransactions: List<Transaction>,
    recurringTransactions: List<RecurringTransaction>,
    wallets: List<AccountWallet>,
    assets: List<AssetItem>,
    currentIncome: Double,
    currentExpense: Double,
    netCashFlow: Double,
    chartData: com.example.util.ChartSeriesData,
    cumulativeData: CumulativeComparisonData,
    expenseSlices: List<CategorySlice>,
    incomeSlices: List<CategorySlice>,
    onSummaryPeriodChange: (Int) -> Unit,
    onExportPdf: () -> Unit,
    onQuickFilterMonth: () -> Unit,
    onQuickFilter7Days: () -> Unit,
    onNavigateToTransactions: (category: String?) -> Unit = {},
    onNavigateToAccount: (walletName: String) -> Unit = {},
    onNavigateToAssets: () -> Unit = {},
    onNavigateToDebts: () -> Unit = {},
    onNavigateToPlanner: () -> Unit = {},
    onNavigateToBudgets: () -> Unit = {},
    onAddTransaction: (category: String?) -> Unit = {},
    onTransactionClick: (Transaction) -> Unit = {},
    onSelectMonth: (monthIndex: Int) -> Unit = {},
    onSelectYear: (year: Int) -> Unit = {}
) {
    val monthName = monthOptions.find { it.first == selectedMonth }?.second ?: "Nov"
    var isPlannedActive by remember { mutableStateOf(true) }
    var showCustomDateDialog by remember { mutableStateOf(false) }
    var customRange by remember { mutableStateOf<CustomDateRange?>(CustomDateRange(1, selectedMonth, selectedYear, 28, selectedMonth, selectedYear)) }

    // Interactive Sheets State
    var showHealthScoreSheet by remember { mutableStateOf(false) }
    var selectedCategoryForSheet by remember { mutableStateOf<String?>(null) }
    var selectedCategoryTypeForSheet by remember { mutableStateOf(TransactionType.EXPENSE) }

    val monthNamesShort = remember {
        listOf("", "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")
    }

    val cal = remember(selectedMonth, selectedYear) {
        Calendar.getInstance().apply {
            set(Calendar.YEAR, selectedYear)
            set(Calendar.MONTH, selectedMonth - 1)
        }
    }
    val daysInMonth = remember(cal) { cal.getActualMaximum(Calendar.DAY_OF_MONTH) }

    // If custom range active, filter transactions
    val effectiveTransactions = remember(summaryPeriod, customRange, periodTransactions, allTransactions) {
        if (summaryPeriod == 2 && customRange != null) {
            val r = customRange!!
            allTransactions.filter { tx ->
                val txYear = tx.year
                val txMonth = tx.monthIndex
                val txDay = tx.dayOfMonth
                val isAfterStart = txYear > r.startYear || (txYear == r.startYear && (txMonth > r.startMonth || (txMonth == r.startMonth && txDay >= r.startDay)))
                val isBeforeEnd = txYear < r.endYear || (txYear == r.endYear && (txMonth < r.endMonth || (txMonth == r.endMonth && txDay <= r.endDay)))
                isAfterStart && isBeforeEnd
            }
        } else {
            periodTransactions
        }
    }

    val (effIncome, effExpense) = remember(effectiveTransactions) {
        val inc = effectiveTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val exp = effectiveTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        Pair(inc, exp)
    }

    val effNet = effIncome - effExpense
    val periodTitle = when (summaryPeriod) {
        1 -> "$selectedYear"
        2 -> customRange?.label ?: "Kustom"
        else -> "$monthName $selectedYear"
    }

    // Previous month comparison
    val (prevM, prevY) = if (selectedMonth > 1) Pair(selectedMonth - 1, selectedYear) else Pair(12, selectedYear - 1)
    val prevMonthExpense = remember(allTransactions, prevM, prevY) {
        allTransactions.filter { it.monthIndex == prevM && it.year == prevY && it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    }
    val prevMonthName = monthNamesShort.getOrElse(prevM) { "Lalu" }

    // Annual computations for Tahunan view
    val yearTransactions = remember(allTransactions, selectedYear) {
        allTransactions.filter { it.year == selectedYear }
    }
    val yearIncome = remember(yearTransactions) {
        yearTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    }
    val yearExpense = remember(yearTransactions) {
        yearTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    }
    val yearNet = yearIncome - yearExpense

    val totalWalletBalance = remember(wallets) { wallets.sumOf { it.balance } }
    val totalAssetValue = remember(assets) { assets.sumOf { it.value } }
    val totalWealth = totalWalletBalance + totalAssetValue
    val walletPct = if (totalWealth > 0) ((totalWalletBalance / totalWealth) * 100).toInt() else 0
    val assetPct = if (totalWealth > 0) ((totalAssetValue / totalWealth) * 100).toInt() else 0

    // Monthly data across all 12 months of selectedYear
    val yearMonthlyData = remember(yearTransactions, selectedYear) {
        (1..12).map { m ->
            val inc = yearTransactions.filter { it.monthIndex == m && it.type == TransactionType.INCOME }.sumOf { it.amount }
            val exp = yearTransactions.filter { it.monthIndex == m && it.type == TransactionType.EXPENSE }.sumOf { it.amount }
            val net = inc - exp
            Triple(m, inc, exp)
        }
    }
    val activeYearMonths = remember(yearMonthlyData) {
        yearMonthlyData.filter { it.second > 0 || it.third > 0 }
    }
    val bestMonthIdx = remember(activeYearMonths) {
        activeYearMonths.maxByOrNull { it.second - it.third }?.first ?: 1
    }
    val worstMonthIdx = remember(activeYearMonths) {
        activeYearMonths.minByOrNull { it.second - it.third }?.first ?: 1
    }

    val avgYearlyInc = if (activeYearMonths.isNotEmpty()) activeYearMonths.map { it.second }.average() else 0.0
    val avgYearlyExp = if (activeYearMonths.isNotEmpty()) activeYearMonths.map { it.third }.average() else 0.0
    val avgYearlyNet = avgYearlyInc - avgYearlyExp
    val yearlySavingsRate = if (yearIncome > 0) (((yearIncome - yearExpense) / yearIncome) * 100).toInt() else 0

    // Daily tracking set
    val trackedDaysSet = remember(effectiveTransactions) {
        effectiveTransactions.map { it.dayOfMonth }.toSet()
    }

    // Health Score calculation
    val savingsPct = if (effIncome > 0) (((effIncome - effExpense) / effIncome) * 100).toInt().coerceIn(0, 100) else 0
    val healthScore = remember(savingsPct, effNet, trackedDaysSet) {
        ((savingsPct * 0.5) + (if (effNet >= 0) 30 else 10) + (trackedDaysSet.size.coerceAtMost(5) * 4)).toInt().coerceIn(20, 100)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Banner: Buku harian uang Anda
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
                                        text = "Melihat kembali dengan tenang ke mana arus uang Anda bergerak",
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
                                color = if (summaryPeriod == 0) AccentBlue else Color(0x33FFFFFF),
                                modifier = Modifier.clickable {
                                    onSummaryPeriodChange(0)
                                    onQuickFilterMonth()
                                }
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
                                color = Color(0x33FFFFFF),
                                modifier = Modifier.clickable {
                                    onQuickFilter7Days()
                                }
                            ) {
                                Text(
                                    text = "⭐ 7 hari lalu",
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

        // Period Selector: Sekilas [Bulanan] [Tahunan] [Kustom]
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
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    val periodOptions = listOf(
                        Triple(0, "Bulanan", Icons.Default.CalendarMonth),
                        Triple(1, "Tahunan", Icons.Default.BarChart),
                        Triple(2, "Kustom", Icons.Default.FilterList)
                    )

                    periodOptions.forEach { (index, title, icon) ->
                        val isSelected = summaryPeriod == index
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) AccentBlue else Color.Transparent,
                            modifier = Modifier.clickable {
                                onSummaryPeriodChange(index)
                                if (index == 2) {
                                    showCustomDateDialog = true
                                }
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else TextMuted,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // Custom Date Range Active Chip
        if (summaryPeriod == 2 && customRange != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurface,
                    border = BorderStroke(1.dp, AccentBlue.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { showCustomDateDialog = true }
                        ) {
                            Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = customRange!!.label,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        }

                        IconButton(
                            onClick = {
                                onSummaryPeriodChange(0)
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Reset", tint = TextMuted, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // TAHUNAN VIEW OR BULANAN/KUSTOM VIEW
        if (summaryPeriod == 1) {
            // ==================== TAHUNAN REPORT ====================
            // 1. Ringkasan Tahunan Header Card with interactive year changer
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
                                Text("Ringkasan Tahunan", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                Text("Total akumulasi transaksi tahun $selectedYear", fontSize = 11.sp, color = TextMuted)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { onSelectYear(selectedYear - 1) },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Tahun Lalu", tint = TextWhite, modifier = Modifier.size(18.dp))
                                }
                                Text("$selectedYear", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                IconButton(
                                    onClick = { onSelectYear(selectedYear + 1) },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Tahun Depan", tint = TextWhite, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(2.dp))
                                IconButton(onClick = onExportPdf, modifier = Modifier.size(28.dp)) {
                                    Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = "PDF", tint = AccentBlue, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 3 Top Cards (Pendapatan, Pengeluaran, Bersih)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = DarkSurfaceElevated,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Total Pendapatan", fontSize = 10.sp, color = TextSubtle)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(CurrencyHelper.formatCompact(yearIncome), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = IncomeGreen)
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = DarkSurfaceElevated,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Total Pengeluaran", fontSize = 10.sp, color = TextSubtle)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(CurrencyHelper.formatCompact(yearExpense), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ExpenseRed)
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = DarkSurfaceElevated,
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Bersih", fontSize = 10.sp, color = TextSubtle)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text((if (yearNet >= 0) "+" else "") + CurrencyHelper.formatCompact(yearNet), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (yearNet >= 0) AccentBlueBright else ExpenseRed)
                                }
                            }
                        }
                    }
                }
            }

            // 2. Pertumbuhan Aset Card
            item {
                var selectedAssetChip by remember { mutableIntStateOf(0) }
                val yearlyValues = remember(selectedAssetChip, yearMonthlyData) {
                    when (selectedAssetChip) {
                        1 -> yearMonthlyData.map { it.second }
                        2 -> yearMonthlyData.map { it.third }
                        else -> yearMonthlyData.map { (it.second - it.third).coerceAtLeast(0.0) }
                    }
                }

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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🌱", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Total Kekayaan & Aset", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                            }
                            Text(CurrencyHelper.formatRupiah(totalWealth), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = IncomeGreen)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Alokasi Kekayaan", fontSize = 10.sp, color = TextSubtle)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToAccount("Semua") },
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("● Saldo Dompet & Bank: ${CurrencyHelper.formatRupiah(totalWalletBalance)}", fontSize = 11.sp, color = TextWhite)
                            Text("$walletPct%", fontSize = 11.sp, color = TextMuted)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToAssets() },
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("● Portofolio Investasi: ${CurrencyHelper.formatRupiah(totalAssetValue)}", fontSize = 11.sp, color = TextWhite)
                            Text("$assetPct%", fontSize = 11.sp, color = TextMuted)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Surplus Bersih", "Pendapatan", "Pengeluaran").forEachIndexed { idx, chip ->
                                val isSel = selectedAssetChip == idx
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSel) AccentBlue else DarkSurfaceElevated,
                                    modifier = Modifier.clickable { selectedAssetChip = idx }
                                ) {
                                    Text(chip, fontSize = 10.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal, color = if (isSel) Color.White else TextMuted, modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        MiniMonthTrendLineChart(
                            values = yearlyValues,
                            labels = listOf("Jan", "Mar", "Mei", "Jul", "Sep", "Nov", "Des"),
                            lineColor = if (selectedAssetChip == 2) ExpenseRed else AccentBlueBright,
                            modifier = Modifier.fillMaxWidth().height(90.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Kelola Aset di menu Akun →",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentBlueBright,
                                modifier = Modifier.clickable { onNavigateToAssets() }
                            )
                        }
                    }
                }
            }

            // 3. Rincian Bulanan (Dynamic Table of months)
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
                            Text("Rincian Bulanan", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            Text("Ketuk bulan untuk melihat rincian", fontSize = 10.sp, color = TextSubtle)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        // Table header
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Bulan", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSubtle, modifier = Modifier.weight(1.2f))
                            Text("Pemasukan", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSubtle, modifier = Modifier.weight(1.5f), textAlign = TextAlign.End)
                            Text("Pengeluaran", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSubtle, modifier = Modifier.weight(1.5f), textAlign = TextAlign.End)
                            Text("Bersih", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSubtle, modifier = Modifier.weight(1.5f), textAlign = TextAlign.End)
                        }

                        HorizontalDivider(color = DarkSurfaceElevated, modifier = Modifier.padding(vertical = 6.dp))

                        val displayMonths = (1..12).filter { m ->
                            val item = yearMonthlyData[m - 1]
                            item.second > 0 || item.third > 0 || m == selectedMonth
                        }

                        if (displayMonths.isEmpty()) {
                            Text("Belum ada transaksi tercatat di tahun $selectedYear", fontSize = 11.sp, color = TextSubtle, modifier = Modifier.padding(vertical = 8.dp))
                        } else {
                            displayMonths.forEach { m ->
                                val item = yearMonthlyData[m - 1]
                                val inc = item.second
                                val exp = item.third
                                val net = inc - exp
                                val isBest = m == bestMonthIdx && activeYearMonths.isNotEmpty()
                                val mName = monthNamesShort.getOrElse(m) { "" } + (if (isBest) " ★" else "")

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isBest) Color(0x2210B981) else Color.Transparent)
                                        .clickable {
                                            onSelectMonth(m)
                                            onSummaryPeriodChange(0)
                                        }
                                        .padding(vertical = 6.dp, horizontal = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(mName, fontSize = 11.sp, fontWeight = if (isBest) FontWeight.Bold else FontWeight.Normal, color = if (isBest) IncomeGreen else TextWhite, modifier = Modifier.weight(1.2f))
                                    Text(CurrencyHelper.formatCompact(inc), fontSize = 10.sp, color = TextMuted, modifier = Modifier.weight(1.5f), textAlign = TextAlign.End)
                                    Text(CurrencyHelper.formatCompact(exp), fontSize = 10.sp, color = TextMuted, modifier = Modifier.weight(1.5f), textAlign = TextAlign.End)
                                    Text((if (net >= 0) "+" else "") + CurrencyHelper.formatCompact(net), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (net >= 0) IncomeGreen else ExpenseRed, modifier = Modifier.weight(1.5f), textAlign = TextAlign.End)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 6 Summary Grid
                        val stats = listOf(
                            Pair("Pemasukan Rata-rata", CurrencyHelper.formatRupiah(avgYearlyInc)),
                            Pair("Pengeluaran Rata-rata", CurrencyHelper.formatRupiah(avgYearlyExp)),
                            Pair("Rata-rata Bersih", (if (avgYearlyNet >= 0) "+" else "") + CurrencyHelper.formatRupiah(avgYearlyNet)),
                            Pair("Tingkat Tabungan", "$yearlySavingsRate%"),
                            Pair("Bulan Terbaik", "${monthNamesShort.getOrElse(bestMonthIdx) { "Jan" }} $selectedYear 🏆"),
                            Pair("Bulan Tersulit", "${monthNamesShort.getOrElse(worstMonthIdx) { "Jan" }} $selectedYear ⏳")
                        )

                        for (r in 0 until 3) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val s1 = stats[r * 2]
                                val s2 = stats[r * 2 + 1]
                                Surface(shape = RoundedCornerShape(10.dp), color = DarkSurfaceElevated, modifier = Modifier.weight(1f)) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(s1.first, fontSize = 10.sp, color = TextSubtle)
                                        Text(s1.second, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                    }
                                }
                                Surface(shape = RoundedCornerShape(10.dp), color = DarkSurfaceElevated, modifier = Modifier.weight(1f)) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(s2.first, fontSize = 10.sp, color = TextSubtle)
                                        Text(s2.second, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } else {
            // ==================== BULANAN & KUSTOM REPORT ====================
            // 1. Card "AKTIVITAS PENGELUARAN"
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
                                    text = periodTitle,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                            }

                            // PDF Button
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurfaceElevated,
                                modifier = Modifier.clickable { onExportPdf() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PictureAsPdf,
                                        contentDescription = null,
                                        tint = AccentBlue,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AccentBlue)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = CurrencyHelper.formatRupiah(effExpense),
                                fontSize = 22.sp,
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
                            if (prevMonthExpense > 0) {
                                val expenseDiff = prevMonthExpense - effExpense
                                val isSaved = expenseDiff >= 0
                                val pct = (abs(expenseDiff) / prevMonthExpense * 100).toInt()
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSaved) IncomeGreenSoft else Color(0x33F43F5E)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (isSaved) Icons.AutoMirrored.Filled.TrendingDown else Icons.AutoMirrored.Filled.TrendingUp,
                                            contentDescription = null,
                                            tint = if (isSaved) IncomeGreen else ExpenseRed,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = if (isSaved) "$pct% vs $prevMonthName" else "+$pct% vs $prevMonthName",
                                            fontSize = 10.sp,
                                            color = if (isSaved) IncomeGreen else ExpenseRed,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Daily Trend Curve
                        val trendPts = chartData.rawValues.ifEmpty { listOf(0.0, effExpense) }
                        MiniMonthTrendLineChart(
                            values = trendPts,
                            labels = listOf(chartData.startLabel, chartData.midLabel, chartData.endLabel),
                            lineColor = ExpenseRed,
                            modifier = Modifier.fillMaxWidth().height(110.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // 3 Summary Metrics
                        val dailyAvg = if (effExpense > 0) effExpense / daysInMonth.toDouble() else 0.0
                        val peakDayEntry = effectiveTransactions.filter { it.type == TransactionType.EXPENSE }
                            .groupBy { it.dayOfMonth }
                            .maxByOrNull { entry -> entry.value.sumOf { it.amount } }
                        val peakAmt = peakDayEntry?.value?.sumOf { it.amount } ?: 0.0
                        val peakDayNum = peakDayEntry?.key ?: 1
                        val topCat = expenseSlices.firstOrNull()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Rata-rata per hari", fontSize = 10.sp, color = TextSubtle)
                                Text(CurrencyHelper.formatCompact(dailyAvg), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            }
                            Column {
                                Text("Hari terboros", fontSize = 10.sp, color = TextSubtle)
                                Text(CurrencyHelper.formatCompact(peakAmt), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                Text("$peakDayNum $monthName", fontSize = 9.sp, color = TextSubtle)
                            }
                            Column(modifier = Modifier.clickable {
                                topCat?.category?.let {
                                    selectedCategoryForSheet = it
                                    selectedCategoryTypeForSheet = TransactionType.EXPENSE
                                }
                            }) {
                                Text("Kategori teratas 👆", fontSize = 10.sp, color = AccentBlueBright)
                                Text(topCat?.category ?: "—", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                Text(if (topCat != null) CurrencyHelper.formatCompact(topCat.amount) else "Rp0", fontSize = 9.sp, color = TextSubtle)
                            }
                        }
                    }
                }
            }

            // 2. Card "Ringkasan: Arus Kas Bulanan"
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Ringkasan", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextSubtle)
                        Text("Arus Kas Bulanan", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextWhite)

                        Spacer(modifier = Modifier.height(14.dp))

                        // Income bar
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(IncomeGreen))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(CurrencyHelper.formatRupiah(effIncome), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pemasukan", fontSize = 11.sp, color = TextMuted)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { 1f },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = IncomeGreen,
                            trackColor = DarkSurfaceElevated
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Expense bar
                        val expRatio = if (effIncome > 0) (effExpense / effIncome).toFloat().coerceIn(0f, 1f) else 1f
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ExpenseRed))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(CurrencyHelper.formatRupiah(effExpense), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pengeluaran", fontSize = 11.sp, color = TextMuted)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { expRatio },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = ExpenseRed,
                            trackColor = DarkSurfaceElevated
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Surface(shape = RoundedCornerShape(12.dp), color = DarkSurfaceElevated, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = if (effNet >= 0)
                                    "Pada $periodTitle, arus kas Anda surplus positif. Pemasukan Anda +${CurrencyHelper.formatRupiah(effNet)} lebih besar dari total pengeluaran."
                                else
                                    "Pada $periodTitle, pengeluaran Anda lebih besar ${CurrencyHelper.formatRupiah(-effNet)} dibanding pemasukan.",
                                fontSize = 11.sp,
                                color = TextMuted,
                                lineHeight = 16.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }

            // 3. Card "Terencana: Masih direncanakan"
            item {
                val plannedIncList = recurringTransactions.filter { it.isActive && it.type == TransactionType.INCOME }
                val plannedExpList = recurringTransactions.filter { it.isActive && it.type == TransactionType.EXPENSE }
                val plannedIncTotal = plannedIncList.sumOf { it.amount }
                val plannedExpTotal = plannedExpList.sumOf { it.amount }

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
                                Text("Terencana", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextSubtle)
                                Text("Masih direncanakan", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            }
                            Switch(
                                checked = isPlannedActive,
                                onCheckedChange = { isPlannedActive = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = AccentBlue,
                                    uncheckedThumbColor = TextMuted,
                                    uncheckedTrackColor = DarkSurfaceElevated
                                )
                            )
                        }

                        Text(
                            text = "Tagihan dan pemasukan dari daftar transaksi berulang yang aktif di periode ini.",
                            fontSize = 11.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Pemasukan terencana - ${plannedIncList.size} item", fontSize = 11.sp, color = TextWhite)
                            Text("+${CurrencyHelper.formatRupiah(plannedIncTotal)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = IncomeGreen)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToPlanner() },
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Pengeluaran terencana - ${plannedExpList.size} item", fontSize = 11.sp, color = TextWhite)
                            Text("-${CurrencyHelper.formatRupiah(plannedExpTotal)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ExpenseRed)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(shape = RoundedCornerShape(12.dp), color = DarkSurfaceElevated, modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Jika semua rencana berjalan", fontSize = 10.sp, color = TextSubtle)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Proyeksi Pemasukan", fontSize = 11.sp, color = TextWhite)
                                    Text(CurrencyHelper.formatRupiah(effIncome + plannedIncTotal), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Proyeksi Pengeluaran", fontSize = 11.sp, color = TextWhite)
                                    Text(CurrencyHelper.formatRupiah(effExpense + plannedExpTotal), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                }
                                val projNet = (effIncome + plannedIncTotal) - (effExpense + plannedExpTotal)
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Bersih akhir periode", fontSize = 11.sp, color = TextWhite)
                                    Text((if (projNet >= 0) "+" else "") + CurrencyHelper.formatRupiah(projNet), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (projNet >= 0) IncomeGreen else ExpenseRed)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Buka Perencana & Jadwal Tagihan →",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF818CF8),
                                modifier = Modifier.clickable { onNavigateToPlanner() }
                            )
                            Text(
                                text = "Lihat Utang",
                                fontSize = 11.sp,
                                color = TextSubtle,
                                modifier = Modifier.clickable { onNavigateToDebts() }
                            )
                        }
                    }
                }
            }

            // 4. Card "Pendapatan vs Pengeluaran" (Kumulatif)
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
                            Text("Pendapatan vs Pengeluaran", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            Surface(shape = RoundedCornerShape(8.dp), color = DarkSurfaceElevated) {
                                Text("Kumulatif", fontSize = 9.sp, color = TextSubtle, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text((if (effNet >= 0) "+" else "") + CurrencyHelper.formatCompact(effNet), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = if (effNet >= 0) IncomeGreen else ExpenseRed)
                                Text("Dihitung periode ini", fontSize = 10.sp, color = TextMuted)
                            }
                            val remPct = if (effIncome > 0) (((effIncome - effExpense) / effIncome) * 100).toInt() else 0
                            Surface(shape = RoundedCornerShape(10.dp), color = if (remPct >= 0) IncomeGreenSoft else Color(0x33F43F5E)) {
                                Text(
                                    text = if (remPct >= 0) "$remPct% pemasukan tersisa" else "Defisit ${abs(remPct)}%",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (remPct >= 0) IncomeGreen else ExpenseRed,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(10.dp, 3.dp).background(IncomeGreen))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pemasukan ${CurrencyHelper.formatCompact(effIncome)}", fontSize = 10.sp, color = TextMuted)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(10.dp, 3.dp).background(ExpenseRed))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pengeluaran ${CurrencyHelper.formatCompact(effExpense)}", fontSize = 10.sp, color = TextMuted)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val incPoints = cumulativeData.incomePoints.ifEmpty { listOf(0.1f, 0.9f) }
                        MiniMonthTrendLineChart(
                            values = cumulativeData.rawIncomeList.ifEmpty { listOf(0.0, effIncome) },
                            labels = listOf("1", "${daysInMonth / 2}", "$daysInMonth"),
                            lineColor = IncomeGreen,
                            modifier = Modifier.fillMaxWidth().height(110.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(cumulativeData.summaryTakeaway, fontSize = 10.sp, color = if (effNet >= 0) IncomeGreen else ExpenseRed)
                    }
                }
            }

            // 5. Card "Wawasan mendalam" -> "Skor Kesehatan Keuangan"
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showHealthScoreSheet = true },
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
                                Text("WAWASAN MENDALAM", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextSubtle)
                                Text("Skor Kesehatan Keuangan", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(shape = RoundedCornerShape(8.dp), color = IncomeGreenSoft) {
                                    Text("Status: ${if (healthScore >= 75) "Sangat Baik" else if (healthScore >= 50) "Cukup Baik" else "Perlu Perhatian"} · Ketuk ℹ️", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = IncomeGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }

                            FinancialHealthScoreGauge(score = healthScore, label = "$healthScore/100")
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Surplus Bersih", fontSize = 10.sp, color = TextSubtle)
                                Text(CurrencyHelper.formatRupiah(effNet), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = if (effNet >= 0) IncomeGreen else ExpenseRed)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Tingkat Tabungan", fontSize = 10.sp, color = TextSubtle)
                                Text("$savingsPct%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = IncomeGreen)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        val metrics = listOf(
                            Triple("Tingkat Tabungan", "$savingsPct%", (savingsPct * 0.2).toInt().coerceAtMost(20)),
                            Triple("Surplus Arus Kas", if (effNet >= 0) "Positif" else "Defisit", if (effNet >= 0) 20 else 5),
                            Triple("Konsistensi Pencatatan", "${trackedDaysSet.size}/$daysInMonth hari terlacak", (trackedDaysSet.size * 2).coerceAtMost(20)),
                            Triple("Diversifikasi Dompet", "${wallets.size} dompet aktif", (wallets.size * 5).coerceAtMost(20)),
                            Triple("Pengeluaran Rutin", "${recurringTransactions.filter { it.isActive }.size} tagihan terkendali", 20)
                        )

                        metrics.forEach { (name, desc, pts) ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🛡️", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(name, fontSize = 11.sp, color = TextWhite)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(desc, fontSize = 11.sp, color = TextMuted)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("$pts", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                }
                            }
                        }
                    }
                }
            }

            // 6. Card "Rincian Pendapatan & Pengeluaran"
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
                                Text("Rincian Pendapatan & Pengeluaran", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                Text("Ketuk kategori untuk melihat rincian transaksi", fontSize = 10.sp, color = TextSubtle)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Section Pemasukan
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(IncomeGreen))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pemasukan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            }
                            Text(CurrencyHelper.formatRupiah(effIncome), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = IncomeGreen)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        if (incomeSlices.isEmpty()) {
                            Text("Belum ada catatan pemasukan di periode ini", fontSize = 11.sp, color = TextSubtle, modifier = Modifier.padding(vertical = 4.dp))
                        } else {
                            incomeSlices.forEach { slice ->
                                val pct = if (effIncome > 0) String.format(Locale.getDefault(), "%.1f%%", (slice.amount / effIncome) * 100) else "0%"
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            selectedCategoryForSheet = slice.category
                                            selectedCategoryTypeForSheet = TransactionType.INCOME
                                        }
                                        .padding(vertical = 5.dp, horizontal = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("💵", fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(slice.category, fontSize = 11.sp, color = TextWhite)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(pct, fontSize = 10.sp, color = TextSubtle)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(CurrencyHelper.formatRupiah(slice.amount), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = DarkSurfaceElevated, modifier = Modifier.padding(vertical = 10.dp))

                        // Section Pengeluaran
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ExpenseRed))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pengeluaran", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            }
                            Text(CurrencyHelper.formatRupiah(effExpense), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ExpenseRed)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        if (expenseSlices.isEmpty()) {
                            Text("Belum ada catatan pengeluaran di periode ini", fontSize = 11.sp, color = TextSubtle, modifier = Modifier.padding(vertical = 4.dp))
                        } else {
                            expenseSlices.forEach { slice ->
                                val pct = if (effExpense > 0) String.format(Locale.getDefault(), "%.1f%%", (slice.amount / effExpense) * 100) else "0%"
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            selectedCategoryForSheet = slice.category
                                            selectedCategoryTypeForSheet = TransactionType.EXPENSE
                                        }
                                        .padding(vertical = 5.dp, horizontal = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🛒", fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(slice.category, fontSize = 11.sp, color = TextWhite)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(pct, fontSize = 10.sp, color = TextSubtle)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(CurrencyHelper.formatRupiah(slice.amount), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 7. Card "Pengeluaran Menurut Hari Kerja"
            item {
                val expenseTxs = remember(effectiveTransactions) {
                    effectiveTransactions.filter { it.type == TransactionType.EXPENSE }
                }
                val dayTotals = remember(expenseTxs) {
                    val totals = DoubleArray(7) { 0.0 }
                    val tempCal = Calendar.getInstance()
                    for (tx in expenseTxs) {
                        tempCal.set(tx.year, tx.monthIndex - 1, tx.dayOfMonth)
                        val dow = tempCal.get(Calendar.DAY_OF_WEEK)
                        val idx = if (dow == Calendar.SUNDAY) 6 else dow - 2
                        if (idx in 0..6) {
                            totals[idx] += tx.amount
                        }
                    }
                    totals.toList()
                }
                val peakIdx = remember(dayTotals) {
                    val max = dayTotals.maxOrNull() ?: 0.0
                    if (max > 0) dayTotals.indexOf(max) else 0
                }
                val weekdayAvg = dayTotals.take(5).sum() / 5.0
                val weekendAvg = dayTotals.takeLast(2).sum() / 2.0

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Pengeluaran Menurut Hari Kerja", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        Text("Pola belanja hari kerja vs akhir pekan", fontSize = 10.sp, color = TextMuted)

                        Spacer(modifier = Modifier.height(14.dp))

                        DayOfWeekSpendingBarChart(
                            dayTotals = dayTotals,
                            peakDayIndex = peakIdx
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Hari kerja (Sen-Jum)", fontSize = 10.sp, color = TextSubtle)
                                Text(CurrencyHelper.formatCompact(weekdayAvg), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                Text("rata-rata per hari", fontSize = 9.sp, color = TextSubtle)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Akhir pekan (Sab-Min)", fontSize = 10.sp, color = TextSubtle)
                                Text(CurrencyHelper.formatCompact(weekendAvg), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                Text("rata-rata per hari", fontSize = 9.sp, color = TextSubtle)
                            }
                        }
                    }
                }
            }

            // 8. Card "Catatan Harian"
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Catatan Harian", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            Text("${trackedDaysSet.size}/$daysInMonth hari terlacak", fontSize = 10.sp, color = TextSubtle)
                        }
                        Text("Disiplin pencatatan keuangan Anda di bulan $monthName", fontSize = 11.sp, color = TextMuted)

                        Spacer(modifier = Modifier.height(12.dp))

                        DailyTrackingCalendarGrid(
                            daysInMonth = daysInMonth,
                            trackedDays = trackedDaysSet,
                            onDayClick = { day ->
                                onAddTransaction(null)
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(IncomeGreen))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ada catatan", fontSize = 10.sp, color = TextMuted)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF1E2433)))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Tidak ada catatan", fontSize = 10.sp, color = TextMuted)
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showCustomDateDialog) {
        DateRangePickerDialog(
            currentYear = selectedYear,
            initialMonth = selectedMonth,
            initialRange = customRange,
            onDismiss = { showCustomDateDialog = false },
            onConfirm = { range ->
                customRange = range
                onSummaryPeriodChange(2)
                showCustomDateDialog = false
            }
        )
    }

    if (showHealthScoreSheet) {
        FinancialHealthDetailSheet(
            score = healthScore,
            savingsRatePct = savingsPct.toDouble(),
            netSurplusFormatted = CurrencyHelper.formatRupiah(effNet),
            onDismiss = { showHealthScoreSheet = false },
            onNavigateToBudgets = onNavigateToBudgets,
            onNavigateToPlanner = onNavigateToPlanner,
            onAddTransaction = { onAddTransaction(null) }
        )
    }

    selectedCategoryForSheet?.let { cat ->
        CategoryTransactionSheet(
            categoryName = cat,
            categoryType = selectedCategoryTypeForSheet,
            periodLabel = periodTitle,
            transactions = effectiveTransactions,
            onDismiss = { selectedCategoryForSheet = null },
            onNavigateToTransactions = { c ->
                onNavigateToTransactions(c)
            },
            onAddTransactionForCategory = { c ->
                onAddTransaction(c)
            },
            onTransactionClick = onTransactionClick
        )
    }
}
