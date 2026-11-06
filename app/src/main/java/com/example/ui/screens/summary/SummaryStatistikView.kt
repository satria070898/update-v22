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
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccountWallet
import com.example.data.model.RecurringTransaction
import com.example.data.model.Transaction
import com.example.data.model.TransactionType
import com.example.ui.dialogs.CategoryTransactionSheet
import com.example.ui.dialogs.CustomizeStatisticsDialog
import com.example.ui.dialogs.StatCardConfig
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
import com.example.util.CategoryIconHelper
import com.example.util.CategorySlice
import com.example.util.CurrencyHelper
import com.example.util.TransactionMathHelper
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs

@Composable
fun SummaryStatistikView(
    selectedMonth: Int,
    selectedYear: Int,
    allTransactions: List<Transaction>,
    periodTransactions: List<Transaction>,
    recurringTransactions: List<RecurringTransaction>,
    wallets: List<AccountWallet>,
    currentIncome: Double,
    currentExpense: Double,
    expenseSlices: List<CategorySlice>,
    incomeSlices: List<CategorySlice>,
    onNavigateToTransactions: (category: String?) -> Unit = {},
    onNavigateToAccount: (walletName: String) -> Unit = {},
    onNavigateToAssets: () -> Unit = {},
    onNavigateToDebts: () -> Unit = {},
    onNavigateToPlanner: () -> Unit = {},
    onNavigateToBudgets: () -> Unit = {},
    onAddTransaction: (category: String?) -> Unit = {},
    onTransactionClick: (Transaction) -> Unit = {}
) {
    var showCustomizeDialog by remember { mutableStateOf(false) }
    var isSideBySideTrends by remember { mutableStateOf(true) }
    var selectedCategoryForSheet by remember { mutableStateOf<String?>(null) }
    var selectedCategoryTypeForSheet by remember { mutableStateOf(TransactionType.EXPENSE) }

    val monthNamesShort = remember {
        listOf("", "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")
    }
    val curMonthName = monthNamesShort.getOrElse(selectedMonth) { "Bln" }

    val cal = remember(selectedMonth, selectedYear) {
        Calendar.getInstance().apply {
            set(Calendar.YEAR, selectedYear)
            set(Calendar.MONTH, selectedMonth - 1)
        }
    }
    val daysInMonth = remember(cal) { cal.getActualMaximum(Calendar.DAY_OF_MONTH) }

    // Dynamic 6-month sequence ending at selectedMonth, selectedYear
    val past6Months = remember(selectedMonth, selectedYear) {
        (0..5).reversed().map { offset ->
            var m = selectedMonth - offset
            var y = selectedYear
            while (m <= 0) {
                m += 12
                y -= 1
            }
            Triple(m, y, monthNamesShort.getOrElse(m) { "" })
        }
    }

    val past6Income = remember(allTransactions, past6Months) {
        past6Months.map { (m, y, _) ->
            allTransactions.filter { it.monthIndex == m && it.year == y && it.type == TransactionType.INCOME }.sumOf { it.amount }
        }
    }

    val past6Expense = remember(allTransactions, past6Months) {
        past6Months.map { (m, y, _) ->
            allTransactions.filter { it.monthIndex == m && it.year == y && it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        }
    }

    val past6Cashflow = remember(past6Income, past6Expense) {
        past6Income.indices.map { i -> past6Income[i] - past6Expense[i] }
    }

    val avgMonthlyIncome = remember(past6Income, currentIncome) {
        val nonZero = past6Income.filter { it > 0 }
        if (nonZero.isNotEmpty()) nonZero.average() else currentIncome
    }
    val avgMonthlyExpense = remember(past6Expense, currentExpense) {
        val nonZero = past6Expense.filter { it > 0 }
        if (nonZero.isNotEmpty()) nonZero.average() else currentExpense
    }

    val prevIncome = past6Income.getOrElse(4) { 0.0 }
    val prevExpense = past6Expense.getOrElse(4) { 0.0 }

    val incomeChangePercent = if (prevIncome > 0) (((currentIncome - prevIncome) / prevIncome) * 100).toInt() else 0
    val expenseChangePercent = if (prevExpense > 0) (((currentExpense - prevExpense) / prevExpense) * 100).toInt() else 0

    // Dynamic S S R K J S M spending breakdown by Day of Week
    val dayOfWeekSpending = remember(periodTransactions) {
        val expenseTxs = periodTransactions.filter { it.type == TransactionType.EXPENSE }
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
        val list = totals.toList()
        val maxVal = list.maxOrNull() ?: 0.0
        val peakIdx = if (maxVal > 0) list.indexOf(maxVal) else 0
        Pair(list, peakIdx)
    }

    val dayNamesIndo = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")

    // Top 5 largest expenses
    val top5Expenses = remember(periodTransactions) {
        periodTransactions.filter { it.type == TransactionType.EXPENSE }
            .sortedByDescending { it.amount }
            .take(5)
    }

    // Active day averages
    val activeIncomeDays = remember(periodTransactions) {
        periodTransactions.filter { it.type == TransactionType.INCOME }.map { it.dayOfMonth }.distinct().size.coerceAtLeast(1)
    }
    val activeExpenseDays = remember(periodTransactions) {
        periodTransactions.filter { it.type == TransactionType.EXPENSE }.map { it.dayOfMonth }.distinct().size.coerceAtLeast(1)
    }

    // Category changes compared to previous month
    val categoryChanges = remember(allTransactions, selectedMonth, selectedYear, periodTransactions) {
        val (prevM, prevY) = if (selectedMonth > 1) Pair(selectedMonth - 1, selectedYear) else Pair(12, selectedYear - 1)
        val prevTxs = allTransactions.filter { it.monthIndex == prevM && it.year == prevY && it.type == TransactionType.EXPENSE }
        val prevGrouped = prevTxs.groupBy { it.category }.mapValues { entry -> entry.value.sumOf { it.amount } }

        val curTxs = periodTransactions.filter { it.type == TransactionType.EXPENSE }
        val curGrouped = curTxs.groupBy { it.category }.mapValues { entry -> entry.value.sumOf { it.amount } }

        val allCats = (prevGrouped.keys + curGrouped.keys).distinct()
        allCats.map { cat ->
            val curAmt = curGrouped[cat] ?: 0.0
            val prevAmt = prevGrouped[cat] ?: 0.0
            val diff = curAmt - prevAmt
            Triple(cat, diff, curAmt)
        }.sortedByDescending { abs(it.second) }.take(4)
    }

    // Spending per account/wallet
    val accountSpending = remember(periodTransactions, currentExpense) {
        val grouped = periodTransactions.filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.accountName }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
        grouped.toList().sortedByDescending { it.second }.map { (name, amt) ->
            val pct = if (currentExpense > 0) ((amt / currentExpense) * 100).toInt() else 0
            Triple(name, "$pct%", amt)
        }
    }

    // Fixed recurring costs
    val fixedRecurringExpenses = remember(recurringTransactions) {
        recurringTransactions.filter { it.isActive && it.type == TransactionType.EXPENSE }
    }
    val fixedCostsTotal = remember(fixedRecurringExpenses) {
        fixedRecurringExpenses.sumOf { it.amount }
    }

    val defaultCards = remember {
        listOf(
            StatCardConfig("trends", "Tren Pendapatan & Pengeluaran", "📈", true),
            StatCardConfig("expense_donut", "Ikhtisar Pengeluaran", "🍩", true),
            StatCardConfig("label_expense", "Pengeluaran per label", "🏷️", true),
            StatCardConfig("upcoming", "Yang akan datang", "📅", true),
            StatCardConfig("cashflow", "Arus Kas", "📊", true),
            StatCardConfig("income_donut", "Ikhtisar Pendapatan", "💰", true),
            StatCardConfig("income_summary", "Ringkasan Penghasilan", "🎯", true),
            StatCardConfig("savings_rate", "Rasio menabung", "🪙", true),
            StatCardConfig("daily_average", "Rata-rata harian", "⏳", true),
            StatCardConfig("mom_compare", "Periode ini vs lalu", "⚖️", true),
            StatCardConfig("category_change", "Kategori paling berubah", "🔄", true),
            StatCardConfig("daily_pattern", "Pola harian", "📅", true),
            StatCardConfig("top_expenses", "Pengeluaran terbesar", "🔥", true),
            StatCardConfig("upcoming_bills", "Tagihan mendatang", "🗓️", true),
            StatCardConfig("period_highlights", "Sorotan periode", "✨", true),
            StatCardConfig("money_flow", "Aliran uang", "🔀", true),
            StatCardConfig("fixed_costs", "Biaya tetap", "🔄", true),
            StatCardConfig("cashflow_journey", "Perjalanan arus kas", "📈", true),
            StatCardConfig("account_spending", "Pengeluaran per akun", "💳", true)
        )
    }

    var cardConfigs by remember { mutableStateOf(defaultCards) }
    val isCardVisible: (String) -> Boolean = { id -> cardConfigs.find { it.id == id }?.isVisible != false }

    val netCashFlow = currentIncome - currentExpense

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Customize Button Bar on top
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurfaceElevated,
                    modifier = Modifier.clickable { showCustomizeDialog = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sesuaikan", fontSize = 11.sp, color = TextWhite, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // 1. Tren Pendapatan & Pengeluaran (Side-by-side or stacked)
        if (isCardVisible("trends")) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Left: Tren Pemasukan
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Tren Pemasukan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            Text("Rata-rata: ${CurrencyHelper.formatRupiah(avgMonthlyIncome)}/bln", fontSize = 9.sp, color = TextSubtle)
                            Spacer(modifier = Modifier.height(8.dp))
                            MiniMonthTrendLineChart(
                                values = past6Income,
                                labels = past6Months.map { it.third },
                                lineColor = IncomeGreen,
                                modifier = Modifier.fillMaxWidth().height(65.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Bulan Lalu", fontSize = 9.sp, color = TextSubtle)
                            Text(CurrencyHelper.formatRupiah(prevIncome), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Bulan ini", fontSize = 9.sp, color = TextSubtle)
                                    Text(CurrencyHelper.formatRupiah(currentIncome), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = IncomeGreen)
                                }
                                if (prevIncome > 0) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (incomeChangePercent >= 0) IncomeGreenSoft else Color(0x33F43F5E)
                                    ) {
                                        Text(
                                            text = if (incomeChangePercent >= 0) "↑ $incomeChangePercent%" else "↓ ${abs(incomeChangePercent)}%",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (incomeChangePercent >= 0) IncomeGreen else ExpenseRed,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Right: Tren Pengeluaran
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Tren Pengeluaran", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            Text("Rata-rata: ${CurrencyHelper.formatRupiah(avgMonthlyExpense)}/bln", fontSize = 9.sp, color = TextSubtle)
                            Spacer(modifier = Modifier.height(8.dp))
                            MiniMonthTrendLineChart(
                                values = past6Expense,
                                labels = past6Months.map { it.third },
                                lineColor = ExpenseRed,
                                modifier = Modifier.fillMaxWidth().height(65.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Bulan Lalu", fontSize = 9.sp, color = TextSubtle)
                            Text(CurrencyHelper.formatRupiah(prevExpense), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Bulan ini", fontSize = 9.sp, color = TextSubtle)
                                    Text(CurrencyHelper.formatRupiah(currentExpense), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ExpenseRed)
                                }
                                if (prevExpense > 0) {
                                    val isLower = expenseChangePercent <= 0
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isLower) IncomeGreenSoft else Color(0x33F43F5E)
                                    ) {
                                        Text(
                                            text = if (expenseChangePercent <= 0) "↓ ${abs(expenseChangePercent)}%" else "↑ $expenseChangePercent%",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isLower) IncomeGreen else ExpenseRed,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Card "Pengeluaran" (Ikhtisar Pengeluaran by Category)
        if (isCardVisible("expense_donut")) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Pengeluaran", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            Text(CurrencyHelper.formatRupiah(currentExpense), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (expenseSlices.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Belum ada catatan pengeluaran di bulan $curMonthName", fontSize = 12.sp, color = TextSubtle)
                            }
                        } else {
                            val palette = listOf(Color(0xFFF43F5E), Color(0xFFF59E0B), Color(0xFF10B981), Color(0xFF3B82F6), Color(0xFF8B5CF6), Color(0xFF06B6D4))
                            expenseSlices.take(5).forEachIndexed { idx, slice ->
                                val pct = if (currentExpense > 0) String.format(Locale.getDefault(), "%.1f%%", (slice.amount / currentExpense) * 100) else "0%"
                                val badgeColor = palette[idx % palette.size]
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
                                        Surface(shape = RoundedCornerShape(6.dp), color = badgeColor) {
                                            Text(pct, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(slice.category, fontSize = 12.sp, color = TextWhite)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(CurrencyHelper.formatRupiah(slice.amount), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Card "Pengeluaran per label"
        if (isCardVisible("label_expense")) {
            item {
                val labeledTxs = remember(periodTransactions) {
                    periodTransactions.filter { it.type == TransactionType.EXPENSE && it.label.isNotBlank() }
                        .groupBy { it.label }
                        .mapValues { entry -> entry.value.sumOf { it.amount } }
                }
                val totalLabeled = remember(labeledTxs) { labeledTxs.values.sum() }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Pengeluaran per label", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            Text(CurrencyHelper.formatRupiah(totalLabeled), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = if (totalLabeled > 0) TextWhite else TextMuted)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        if (labeledTxs.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Tidak ada transaksi berlabel di periode ini", fontSize = 12.sp, color = TextSubtle)
                            }
                        } else {
                            labeledTxs.forEach { (lbl, amt) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(shape = RoundedCornerShape(6.dp), color = DarkSurfaceElevated) {
                                        Text(lbl, fontSize = 10.sp, color = AccentBlueBright, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                                    }
                                    Text(CurrencyHelper.formatRupiah(amt), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Card "Yang akan datang" (Integrated with Recurring & Planner)
        if (isCardVisible("upcoming")) {
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
                            Text("Yang akan datang", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = DarkSurfaceElevated,
                                modifier = Modifier.clickable { onAddTransaction(null) }
                            ) {
                                Text("+ Tambah Catatan", fontSize = 10.sp, color = AccentBlueBright, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val activeRecurring = recurringTransactions.filter { it.isActive }
                        if (activeRecurring.isEmpty()) {
                            Text("Belum ada jadwal transaksi berulang yang didaftarkan.", fontSize = 11.sp, color = TextMuted)
                        } else {
                            Text(
                                text = "${activeRecurring.size} jadwal transaksi rutin tercatat:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextWhite
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            activeRecurring.take(3).forEach { r ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(r.title, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextWhite)
                                        Text("Setiap tanggal ${r.dayOfMonth} · ${r.accountName}", fontSize = 9.sp, color = TextSubtle)
                                    }
                                    Text(
                                        text = CurrencyHelper.formatRupiah(r.amount),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (r.type == TransactionType.INCOME) IncomeGreen else ExpenseRed
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Buka Perencana & Jadwal Tagihan →",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF818CF8),
                            modifier = Modifier.clickable { onNavigateToPlanner() }
                        )
                    }
                }
            }
        }

        // 5. Card "Arus Kas" (Net Cashflow Trend across 6 months)
        if (isCardVisible("cashflow")) {
            item {
                val avgCashflow = past6Cashflow.average()
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Arus Kas", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                Text("Rata-rata: ${CurrencyHelper.formatRupiah(avgCashflow)}/bln", fontSize = 10.sp, color = TextSubtle)
                            }
                            Text(
                                text = (if (netCashFlow >= 0) "+" else "") + CurrencyHelper.formatRupiah(netCashFlow),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (netCashFlow >= 0) IncomeGreen else ExpenseRed
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        MiniMonthTrendLineChart(
                            values = past6Cashflow,
                            labels = past6Months.map { it.third },
                            lineColor = if (netCashFlow >= 0) IncomeGreen else ExpenseRed,
                            modifier = Modifier.fillMaxWidth().height(80.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(shape = RoundedCornerShape(8.dp), color = DarkSurfaceElevated, modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(if (netCashFlow >= 0) "➕" else "⚠️", fontSize = 11.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (netCashFlow >= 0)
                                        "Pemasukan melebihi pengeluaran pada bulan $curMonthName (Surplus ${CurrencyHelper.formatRupiah(netCashFlow)})"
                                    else
                                        "Pengeluaran melebihi pemasukan pada bulan $curMonthName (Defisit ${CurrencyHelper.formatRupiah(-netCashFlow)})",
                                    fontSize = 10.sp,
                                    color = if (netCashFlow >= 0) IncomeGreen else ExpenseRed
                                )
                            }
                        }
                    }
                }
            }
        }

        // 6. Card "Pemasukan" (Income Donut & List)
        if (isCardVisible("income_donut")) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Pemasukan", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            Text(CurrencyHelper.formatRupiah(currentIncome), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        if (incomeSlices.isEmpty()) {
                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                                Text("Belum ada catatan pemasukan di bulan $curMonthName", fontSize = 12.sp, color = TextSubtle)
                            }
                        } else {
                            incomeSlices.take(5).forEach { slice ->
                                val pct = if (currentIncome > 0) String.format(Locale.getDefault(), "%.1f%%", (slice.amount / currentIncome) * 100) else "0%"
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
                                        Surface(shape = RoundedCornerShape(6.dp), color = IncomeGreen) {
                                            Text(pct, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(slice.category, fontSize = 12.sp, color = TextWhite)
                                    }
                                    Text(CurrencyHelper.formatRupiah(slice.amount), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 7. Card "Ringkasan Penghasilan"
        if (isCardVisible("income_summary")) {
            item {
                val savedPercent = if (currentIncome > 0) (((currentIncome - currentExpense) / currentIncome) * 100).toInt().coerceIn(0, 100) else 0
                val dailyExpAvg = if (currentExpense > 0) currentExpense / daysInMonth.toDouble() else 0.0
                val dailySafeRemaining = if (currentIncome > currentExpense) (currentIncome - currentExpense) / daysInMonth.toDouble() else 0.0

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Ringkasan Penghasilan", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FinancialHealthScoreGauge(score = savedPercent, label = "$savedPercent%", modifier = Modifier.size(64.dp))
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text("Tersisa $savedPercent%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                Text("${CurrencyHelper.formatRupiah(netCashFlow.coerceAtLeast(0.0))} dari ${CurrencyHelper.formatRupiah(currentIncome)}", fontSize = 11.sp, color = TextMuted)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Rata-rata belanja harian", fontSize = 11.sp, color = TextSubtle)
                            Text("${CurrencyHelper.formatRupiah(dailyExpAvg)} / hari", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Batas aman harian yang tersisa", fontSize = 11.sp, color = TextSubtle)
                            Text("${CurrencyHelper.formatRupiah(dailySafeRemaining)} / hari", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = IncomeGreen)
                        }
                    }
                }
            }
        }

        // 8. Card "Rasio menabung"
        if (isCardVisible("savings_rate")) {
            item {
                val savingsRate = if (currentIncome > 0) (((currentIncome - currentExpense) / currentIncome) * 100).coerceAtLeast(0.0) else 0.0
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Rasio menabung", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            Text(String.format(Locale.getDefault(), "%.1f%%", savingsRate), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (savingsRate >= 20.0) IncomeGreen else AccentBlueBright)
                        }
                        Text("Target rekomendasi: 20%", fontSize = 10.sp, color = TextSubtle)
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { (savingsRate / 100.0).toFloat().coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = if (savingsRate >= 20.0) IncomeGreen else AccentBlue,
                            trackColor = DarkSurfaceElevated
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (savingsRate >= 20.0)
                                "Luar biasa! Tingkat tabungan Anda di atas target rekomendasi 20%."
                            else if (savingsRate > 0.0)
                                "Bagus! Anda berhasil menyisihkan tabungan bulan ini."
                            else
                                "Pengeluaran bulan ini melebihi atau menyamai total pemasukan.",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        // 9. Card "Rata-rata harian"
        if (isCardVisible("daily_average")) {
            item {
                val avgIncPerActiveDay = currentIncome / activeIncomeDays.toDouble()
                val avgExpPerActiveDay = currentExpense / activeExpenseDays.toDouble()

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Rata-rata harian", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Pemasukan", fontSize = 10.sp, color = TextSubtle)
                                Text(CurrencyHelper.formatCompact(avgIncPerActiveDay), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = IncomeGreen)
                                Text("per hari aktif ($activeIncomeDays hari)", fontSize = 9.sp, color = TextSubtle)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Pengeluaran", fontSize = 10.sp, color = TextSubtle)
                                Text(CurrencyHelper.formatCompact(avgExpPerActiveDay), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ExpenseRed)
                                Text("per hari aktif ($activeExpenseDays hari)", fontSize = 9.sp, color = TextSubtle)
                            }
                        }
                    }
                }
            }
        }

        // 10. Card "Periode ini vs lalu"
        if (isCardVisible("mom_compare")) {
            item {
                val expenseDiff = prevExpense - currentExpense
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Periode ini vs lalu", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Pengeluaran bulan lalu", fontSize = 11.sp, color = TextMuted)
                            Text(CurrencyHelper.formatRupiah(prevExpense), fontSize = 11.sp, color = TextWhite)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Pengeluaran bulan ini", fontSize = 11.sp, color = TextMuted)
                            Text(CurrencyHelper.formatRupiah(currentExpense), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ExpenseRed)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        if (prevExpense > 0) {
                            val isSaved = expenseDiff >= 0
                            val pct = String.format(Locale.getDefault(), "%.1f%%", (abs(expenseDiff) / prevExpense) * 100)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSaved) IncomeGreenSoft else Color(0x33F43F5E),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (isSaved)
                                        "Hemat ${CurrencyHelper.formatRupiah(expenseDiff)} (-$pct) dibanding bulan lalu"
                                    else
                                        "Meningkat ${CurrencyHelper.formatRupiah(-expenseDiff)} (+$pct) dibanding bulan lalu",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSaved) IncomeGreen else ExpenseRed,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 11. Card "Kategori paling berubah"
        if (isCardVisible("category_change")) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Kategori paling berubah", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        Text("Perubahan pengeluaran dibanding bulan sebelumnya", fontSize = 10.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(10.dp))
                        if (categoryChanges.isEmpty()) {
                            Text("Belum cukup data untuk membandingkan kategori", fontSize = 11.sp, color = TextSubtle)
                        } else {
                            categoryChanges.forEach { (cat, diff, _) ->
                                val isLess = diff <= 0
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable {
                                            selectedCategoryForSheet = cat
                                            selectedCategoryTypeForSheet = TransactionType.EXPENSE
                                        }
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(cat, fontSize = 12.sp, color = TextWhite)
                                    Text(
                                        text = (if (diff > 0) "+" else "") + CurrencyHelper.formatRupiah(diff),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isLess) IncomeGreen else ExpenseRed
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 12. Card "Pola harian"
        if (isCardVisible("daily_pattern")) {
            item {
                val (totals, peakIdx) = dayOfWeekSpending
                val peakTotal = totals.getOrElse(peakIdx) { 0.0 }
                val peakDayName = dayNamesIndo.getOrElse(peakIdx) { "Hari" }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Pola harian", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        Text(
                            text = if (peakTotal > 0) "$peakDayName adalah hari terborosmu (${CurrencyHelper.formatRupiah(peakTotal)})" else "Belum ada pengeluaran harian tercatat",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        DayOfWeekSpendingBarChart(
                            dayTotals = totals,
                            peakDayIndex = peakIdx
                        )
                    }
                }
            }
        }

        // 13. Card "Pengeluaran terbesar"
        if (isCardVisible("top_expenses")) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Pengeluaran terbesar", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        Spacer(modifier = Modifier.height(10.dp))
                        if (top5Expenses.isEmpty()) {
                            Text("Belum ada pengeluaran di periode ini", fontSize = 11.sp, color = TextSubtle)
                        } else {
                            top5Expenses.forEach { tx ->
                                val labelText = tx.title.ifBlank { tx.category }
                                val metaText = "${tx.dayOfMonth} $curMonthName · " +
                                    if (currentExpense > 0) String.format(Locale.getDefault(), "%.1f%%", (tx.amount / currentExpense) * 100) else ""
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { onTransactionClick(tx) }
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(labelText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                                        Text(metaText, fontSize = 9.sp, color = TextSubtle)
                                    }
                                    Text(CurrencyHelper.formatRupiah(tx.amount), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ExpenseRed)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 14. Card "Tagihan mendatang"
        if (isCardVisible("upcoming_bills")) {
            item {
                val activeBills = recurringTransactions.filter { it.isActive && it.type == TransactionType.EXPENSE }
                val totalDue = activeBills.sumOf { it.amount }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Tagihan mendatang", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            Text("${activeBills.size} tagihan rutin", fontSize = 10.sp, color = TextSubtle)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        if (activeBills.isEmpty()) {
                            Text("Tidak ada tagihan rutin yang aktif", fontSize = 11.sp, color = TextSubtle)
                        } else {
                            activeBills.take(4).forEach { b ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onNavigateToPlanner() }
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(shape = RoundedCornerShape(6.dp), color = DarkSurfaceElevated) {
                                            Text("Tgl ${b.dayOfMonth}", fontSize = 9.sp, color = TextSubtle, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(b.title, fontSize = 12.sp, color = TextWhite)
                                    }
                                    Text(CurrencyHelper.formatRupiah(b.amount), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ExpenseRed)
                                }
                            }
                            HorizontalDivider(color = DarkSurfaceElevated, modifier = Modifier.padding(vertical = 6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Total jatuh tempo", fontSize = 11.sp, color = TextMuted)
                                Text(CurrencyHelper.formatRupiah(totalDue), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Kelola Tagihan di Perencana →",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF818CF8),
                            modifier = Modifier.clickable { onNavigateToPlanner() }
                        )
                    }
                }
            }
        }

        // 15. Card "Sorotan periode"
        if (isCardVisible("period_highlights")) {
            item {
                val largestExpense = top5Expenses.firstOrNull()
                val totalSaved = (currentIncome - currentExpense).coerceAtLeast(0.0)
                val rate = if (currentIncome > 0) ((totalSaved / currentIncome) * 100).toInt() else 0

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Sorotan periode", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Pengeluaran Terbesar", fontSize = 9.sp, color = TextSubtle)
                                Text(largestExpense?.category ?: "—", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                Text(if (largestExpense != null) CurrencyHelper.formatRupiah(largestExpense.amount) else "Rp0", fontSize = 9.sp, color = TextSubtle)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Tabungan Tersisa", fontSize = 9.sp, color = TextSubtle)
                                Text("$rate%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = IncomeGreen)
                                Text(CurrencyHelper.formatRupiah(totalSaved), fontSize = 9.sp, color = TextSubtle)
                            }
                        }
                    }
                }
            }
        }

        // 16. Card "Aliran uang" (Sankey visual)
        if (isCardVisible("money_flow")) {
            item {
                val incNodes = incomeSlices.take(3).map { Pair(it.category, it.amount) }
                val expNodes = expenseSlices.take(3).map { Pair(it.category, it.amount) }
                val savedAmt = (currentIncome - currentExpense).coerceAtLeast(0.0)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Aliran uang", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        Spacer(modifier = Modifier.height(10.dp))
                        SankeyMoneyFlowView(
                            incomeCategories = if (incNodes.isNotEmpty()) incNodes else listOf(Pair("Pemasukan", currentIncome.coerceAtLeast(1.0))),
                            expenseCategories = if (expNodes.isNotEmpty()) expNodes else listOf(Pair("Pengeluaran", currentExpense.coerceAtLeast(1.0))),
                            savingsAmount = savedAmt,
                            onCategoryClick = { catName ->
                                selectedCategoryForSheet = catName
                                selectedCategoryTypeForSheet = if (incomeSlices.any { it.category == catName }) TransactionType.INCOME else TransactionType.EXPENSE
                            }
                        )
                    }
                }
            }
        }

        // 17. Card "Biaya tetap"
        if (isCardVisible("fixed_costs")) {
            item {
                val fixedPct = if (currentIncome > 0) ((fixedCostsTotal / currentIncome) * 100).toInt() else 0

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Biaya tetap", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(CurrencyHelper.formatRupiah(fixedCostsTotal), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                Text("$fixedPct% dari pemasukan periode ini", fontSize = 9.sp, color = TextSubtle)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        if (fixedRecurringExpenses.isEmpty()) {
                            Text("Belum ada pengeluaran berulang tetap yang didaftarkan", fontSize = 11.sp, color = TextSubtle)
                        } else {
                            fixedRecurringExpenses.take(3).forEach { r ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(r.title, fontSize = 11.sp, color = TextWhite)
                                    Text("${CurrencyHelper.formatRupiah(r.amount)} / bln", fontSize = 11.sp, color = TextMuted)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 18. Card "Perjalanan arus kas"
        if (isCardVisible("cashflow_journey")) {
            item {
                // Checkpoints: Day 1, Day 10, Day 20, Day end
                val dEnd = daysInMonth
                val checkpoints = listOf(1, (dEnd / 3).coerceAtLeast(1), (dEnd * 2 / 3).coerceAtLeast(2), dEnd)
                val journeyVals = checkpoints.map { day ->
                    val inc = periodTransactions.filter { it.type == TransactionType.INCOME && it.dayOfMonth <= day }.sumOf { it.amount }
                    val exp = periodTransactions.filter { it.type == TransactionType.EXPENSE && it.dayOfMonth <= day }.sumOf { it.amount }
                    inc - exp
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Perjalanan arus kas", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            Text(
                                text = (if (netCashFlow >= 0) "+" else "") + CurrencyHelper.formatRupiah(netCashFlow),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (netCashFlow >= 0) IncomeGreen else ExpenseRed
                            )
                        }
                        Text("Nilai bersih kumulatif selama bulan $curMonthName", fontSize = 10.sp, color = TextSubtle)
                        Spacer(modifier = Modifier.height(10.dp))
                        MiniMonthTrendLineChart(
                            values = journeyVals,
                            labels = checkpoints.map { "$it $curMonthName" },
                            lineColor = if (netCashFlow >= 0) IncomeGreen else ExpenseRed,
                            modifier = Modifier.fillMaxWidth().height(65.dp)
                        )
                    }
                }
            }
        }

        // 19. Card "Pengeluaran per akun"
        if (isCardVisible("account_spending")) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Pengeluaran per akun", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            Text("Ketuk untuk buka akun", fontSize = 10.sp, color = TextSubtle)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        if (accountSpending.isEmpty()) {
                            Text("Belum ada pengeluaran akun di bulan ini", fontSize = 11.sp, color = TextSubtle)
                        } else {
                            accountSpending.forEach { (accName, pct, amt) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { onNavigateToAccount(accName) }
                                        .padding(vertical = 5.dp, horizontal = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF1E293B)) {
                                            Text(accName.take(3).uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AccentBlue, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(accName, fontSize = 12.sp, color = TextWhite)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(shape = RoundedCornerShape(6.dp), color = AccentBlue.copy(alpha = 0.2f)) {
                                            Text(pct, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AccentBlue, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(CurrencyHelper.formatRupiah(amt), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                                    }
                                }
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

    if (showCustomizeDialog) {
        CustomizeStatisticsDialog(
            initialCards = cardConfigs,
            isSideBySideTrends = isSideBySideTrends,
            onDismiss = { showCustomizeDialog = false },
            onSave = { updated, sbs ->
                cardConfigs = updated
                isSideBySideTrends = sbs
            }
        )
    }

    selectedCategoryForSheet?.let { cat ->
        CategoryTransactionSheet(
            categoryName = cat,
            categoryType = selectedCategoryTypeForSheet,
            periodLabel = "$curMonthName $selectedYear",
            transactions = periodTransactions,
            onDismiss = { selectedCategoryForSheet = null },
            onNavigateToTransactions = { c -> onNavigateToTransactions(c) },
            onAddTransactionForCategory = { c -> onAddTransaction(c) },
            onTransactionClick = onTransactionClick
        )
    }
}
