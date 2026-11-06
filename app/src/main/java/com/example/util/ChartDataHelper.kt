package com.example.util

import com.example.data.model.AccountWallet
import com.example.data.model.AssetItem
import com.example.data.model.DebtItem
import com.example.data.model.DebtType
import com.example.data.model.Transaction
import com.example.data.model.TransactionType
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

enum class ChartPeriod(val label: String, val shortLabel: String) {
    SEVEN_DAYS("7 Hari", "7D"),
    ONE_WEEK("1 Minggu", "1W"),
    ONE_MONTH("1 Bulan", "1M"),
    THREE_MONTHS("3 Bulan", "3M"),
    ONE_YEAR("1 Tahun", "1Y"),
    ALL("Semua", "ALL")
}

data class ChartSeriesData(
    val startLabel: String,
    val midLabel: String,
    val endLabel: String,
    val points: List<Float>, // Normalized 0f..1f for canvas
    val rawValues: List<Double>,
    val total: Double,
    val averagePerDay: Double,
    val peakValue: Double,
    val peakDayLabel: String,
    val topCategory: String,
    val topCategoryAmount: Double,
    val percentChange: Double,
    val isPositive: Boolean,
    val deltaAmount: Double = 0.0,
    val trendDirection: Int = if (deltaAmount > 10.0) 1 else if (deltaAmount < -10.0) -1 else 0, // 1: Naik (Hijau), -1: Turun (Merah), 0: Netral (Abu-abu)
    val pointLabels: List<String> = emptyList()
)

data class MonthlyNetWorthBar(
    val monthName: String,
    val value: Double,
    val formattedValue: String,
    val trendDirection: Int, // 1: Naik (Hijau), -1: Turun (Merah), 0: Netral (Abu-abu)
    val heightRatio: Float
)

data class CumulativeComparisonData(
    val incomePoints: List<Float>,
    val expensePoints: List<Float>,
    val totalIncome: Double,
    val totalExpense: Double,
    val savedAmount: Double,
    val savingsRatePercent: Int,
    val summaryTakeaway: String,
    val isIncomeHigher: Boolean,
    val trendDirection: Int = if (savedAmount > 10.0) 1 else if (savedAmount < -10.0) -1 else 0,
    val rawIncomeList: List<Double> = emptyList(),
    val rawExpenseList: List<Double> = emptyList(),
    val dayLabels: List<String> = emptyList()
)

object ChartDataHelper {

    fun getTrendColor(trendDirection: Int): Color = when {
        trendDirection > 0 -> Color(0xFF10B981) // Hijau jika naik
        trendDirection < 0 -> Color(0xFFEF4444) // Merah jika turun
        else -> Color(0xFF94A3B8) // Abu-abu jika netral
    }

    fun DrawScope.drawDashedChartGrid(
        horizontalLines: Int = 3,
        verticalLines: Int = 3,
        gridColor: Color = Color(0xFF334155).copy(alpha = 0.45f),
        drawFrame: Boolean = true
    ) {
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
        for (i in 1..horizontalLines) {
            val y = size.height * (i.toFloat() / (horizontalLines + 1))
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = dashEffect
            )
        }
        for (j in 1..verticalLines) {
            val x = size.width * (j.toFloat() / (verticalLines + 1))
            drawLine(
                color = gridColor,
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 1.dp.toPx(),
                pathEffect = dashEffect
            )
        }
        if (drawFrame) {
            drawRect(
                color = gridColor.copy(alpha = 0.4f),
                size = size,
                style = Stroke(width = 1.dp.toPx(), pathEffect = dashEffect)
            )
        }
    }

    private val monthNamesShort = listOf(
        "", "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des"
    )

    fun getExpenseTrend(
        transactions: List<Transaction>,
        period: ChartPeriod,
        currentMonth: Int = 10,
        currentYear: Int = 2026
    ): ChartSeriesData {
        val expenseTxs = transactions.filter { it.type == TransactionType.EXPENSE }

        val cal = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.YEAR, currentYear)
            set(java.util.Calendar.MONTH, currentMonth - 1)
        }
        val daysInMonth = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
        val curMonthName = monthNamesShort.getOrElse(currentMonth) { "Bln" }

        return when (period) {
            ChartPeriod.SEVEN_DAYS -> {
                // Last 7 days of the selected month
                val startD = (daysInMonth - 6).coerceAtLeast(1)
                val days = (startD..daysInMonth).toList()
                val dailyTotals = days.map { day ->
                    expenseTxs.filter { it.monthIndex == currentMonth && it.year == currentYear && it.dayOfMonth == day }
                        .sumOf { it.amount }
                }
                val total = dailyTotals.sum()
                val avg = if (days.isNotEmpty()) total / days.size else 0.0
                val maxVal = dailyTotals.maxOrNull() ?: 1.0
                val maxIdx = dailyTotals.indexOf(maxVal)
                val peakDay = if (maxIdx >= 0 && maxVal > 0) "${days[maxIdx]} $curMonthName" else "$startD $curMonthName"

                val catGroups = expenseTxs.filter { it.monthIndex == currentMonth && it.year == currentYear && it.dayOfMonth in days }
                    .groupBy { it.category }
                val topCat = catGroups.maxByOrNull { entry -> entry.value.sumOf { it.amount } }
                val topCatName = topCat?.key ?: "Other"
                val topCatAmount = topCat?.value?.sumOf { it.amount } ?: total

                val normalized = normalizePoints(dailyTotals)

                ChartSeriesData(
                    startLabel = "$startD $curMonthName",
                    midLabel = "${startD + 3} $curMonthName",
                    endLabel = "$daysInMonth $curMonthName",
                    points = normalized,
                    rawValues = dailyTotals,
                    total = total,
                    averagePerDay = avg,
                    peakValue = maxVal,
                    peakDayLabel = peakDay,
                    topCategory = topCatName,
                    topCategoryAmount = topCatAmount,
                    percentChange = 0.0,
                    isPositive = true
                )
            }
            ChartPeriod.ONE_WEEK -> {
                // 7 days window (1..7 or similar)
                val days = (1..7.coerceAtMost(daysInMonth)).toList()
                val dailyTotals = days.map { day ->
                    expenseTxs.filter { it.monthIndex == currentMonth && it.year == currentYear && it.dayOfMonth == day }
                        .sumOf { it.amount }
                }
                val total = dailyTotals.sum()
                val avg = if (days.isNotEmpty()) total / days.size else 0.0
                val maxVal = dailyTotals.maxOrNull() ?: 1.0
                val maxIdx = dailyTotals.indexOf(maxVal)
                val peakDay = if (maxIdx >= 0 && maxVal > 0) "${days[maxIdx]} $curMonthName" else "1 $curMonthName"

                val catGroups = expenseTxs.filter { it.monthIndex == currentMonth && it.year == currentYear && it.dayOfMonth in days }
                    .groupBy { it.category }
                val topCat = catGroups.maxByOrNull { entry -> entry.value.sumOf { it.amount } }

                val normalized = normalizePoints(dailyTotals)

                ChartSeriesData(
                    startLabel = "1 $curMonthName",
                    midLabel = "4 $curMonthName",
                    endLabel = "${days.last()} $curMonthName",
                    points = normalized,
                    rawValues = dailyTotals,
                    total = total,
                    averagePerDay = avg,
                    peakValue = maxVal,
                    peakDayLabel = peakDay,
                    topCategory = topCat?.key ?: "Other",
                    topCategoryAmount = topCat?.value?.sumOf { it.amount } ?: total,
                    percentChange = 0.0,
                    isPositive = true
                )
            }
            ChartPeriod.ONE_MONTH -> {
                // 1..daysInMonth of month
                val days = (1..daysInMonth).toList()
                val dailyTotals = days.map { day ->
                    expenseTxs.filter { it.monthIndex == currentMonth && it.year == currentYear && it.dayOfMonth == day }
                        .sumOf { it.amount }
                }
                val total = dailyTotals.sum()
                val avg = if (total > 0) total / daysInMonth.toDouble() else 0.0
                val maxVal = dailyTotals.maxOrNull() ?: 1.0
                val maxIdx = dailyTotals.indexOf(maxVal)
                val peakDay = if (maxIdx >= 0 && maxVal > 0) "${days[maxIdx]} $curMonthName" else "1 $curMonthName"

                val catGroups = expenseTxs.filter { it.monthIndex == currentMonth && it.year == currentYear }
                    .groupBy { it.category }
                val topCat = catGroups.maxByOrNull { entry -> entry.value.sumOf { it.amount } }

                val normalized = normalizePoints(dailyTotals)

                ChartSeriesData(
                    startLabel = "1 $curMonthName",
                    midLabel = "${daysInMonth / 2} $curMonthName",
                    endLabel = "$daysInMonth $curMonthName",
                    points = normalized,
                    rawValues = dailyTotals,
                    total = total,
                    averagePerDay = avg,
                    peakValue = maxVal,
                    peakDayLabel = peakDay,
                    topCategory = topCat?.key ?: "Other",
                    topCategoryAmount = topCat?.value?.sumOf { it.amount } ?: total,
                    percentChange = 0.0,
                    isPositive = true
                )
            }
            ChartPeriod.THREE_MONTHS -> {
                // Last 3 months: Agu, Sep, Okt
                val months = listOf(8, 9, 10)
                val monthTotals = months.map { m ->
                    expenseTxs.filter { it.monthIndex == m && it.year == currentYear }
                        .sumOf { it.amount }
                }
                val total = monthTotals.sum()
                val avg = total / 90.0
                val maxVal = monthTotals.maxOrNull() ?: 1.0
                val maxIdx = monthTotals.indexOf(maxVal)
                val peakLabel = if (maxIdx >= 0) "${monthNamesShort[months[maxIdx]]} $currentYear" else "Sep"

                val catGroups = expenseTxs.filter { it.monthIndex in months && it.year == currentYear }
                    .groupBy { it.category }
                val topCat = catGroups.maxByOrNull { entry -> entry.value.sumOf { it.amount } }

                val normalized = normalizePoints(monthTotals)

                ChartSeriesData(
                    startLabel = "1 Agu",
                    midLabel = "15 Sep",
                    endLabel = "31 Okt",
                    points = normalized,
                    rawValues = monthTotals,
                    total = total,
                    averagePerDay = avg,
                    peakValue = maxVal,
                    peakDayLabel = peakLabel,
                    topCategory = topCat?.key ?: "Other",
                    topCategoryAmount = topCat?.value?.sumOf { it.amount } ?: total,
                    percentChange = -5.2,
                    isPositive = true
                )
            }
            ChartPeriod.ONE_YEAR, ChartPeriod.ALL -> {
                // 12 months Jan..Des
                val months = (1..12).toList()
                val monthTotals = months.map { m ->
                    expenseTxs.filter { it.monthIndex == m && it.year == currentYear }
                        .sumOf { it.amount }
                }
                val total = monthTotals.sum()
                val avg = if (total > 0) total / 365.0 else 0.0
                val maxVal = monthTotals.maxOrNull() ?: 1.0
                val maxIdx = monthTotals.indexOf(maxVal)
                val peakLabel = if (maxIdx >= 0) monthNamesShort[months[maxIdx]] else "Okt"

                val catGroups = expenseTxs.filter { it.year == currentYear }.groupBy { it.category }
                val topCat = catGroups.maxByOrNull { entry -> entry.value.sumOf { it.amount } }

                val normalized = normalizePoints(monthTotals)

                ChartSeriesData(
                    startLabel = "Jan $currentYear",
                    midLabel = "Jun $currentYear",
                    endLabel = "Des $currentYear",
                    points = normalized,
                    rawValues = monthTotals,
                    total = total,
                    averagePerDay = avg,
                    peakValue = maxVal,
                    peakDayLabel = peakLabel,
                    topCategory = topCat?.key ?: "Other",
                    topCategoryAmount = topCat?.value?.sumOf { it.amount } ?: total,
                    percentChange = -10.4,
                    isPositive = true
                )
            }
        }
    }

    fun parseDateParts(dateStr: String): Triple<Int, Int, Int>? {
        val clean = dateStr.trim()
        if (clean.isBlank()) return null
        val parts = clean.split(" ").filter { it.isNotBlank() }
        if (parts.size >= 3) {
            val d = parts[0].filter { it.isDigit() }.toIntOrNull() ?: return null
            val mIdx = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")
                .indexOfFirst { it.equals(parts[1], ignoreCase = true) }
            val m = if (mIdx >= 0) mIdx + 1 else return null
            val y = parts[2].filter { it.isDigit() }.toIntOrNull() ?: return null
            return Triple(d, m, y)
        }
        if (clean.contains("-")) {
            val dashParts = clean.split("-")
            if (dashParts.size == 3) {
                val y = dashParts[0].filter { it.isDigit() }.toIntOrNull() ?: return null
                val m = dashParts[1].filter { it.isDigit() }.toIntOrNull() ?: return null
                val d = dashParts[2].filter { it.isDigit() }.toIntOrNull() ?: return null
                return Triple(d, m, y)
            }
        }
        return null
    }

    fun getNetWorthTrend(
        wallets: List<AccountWallet>,
        assets: List<AssetItem>,
        debts: List<DebtItem>,
        transactions: List<Transaction>,
        subTab: Int, // 0: Nilai Bersih, 1: Akun, 2: Aset, 3: Utang
        period: ChartPeriod,
        referenceDay: Int = 29,
        referenceMonth: Int = 10,
        referenceYear: Int = 2026
    ): ChartSeriesData {
        val totalWalletBal = wallets.sumOf { it.balance }
        val totalAssetsVal = assets.sumOf { it.value }
        val totalDebtVal = debts.filter { it.type == DebtType.BORROWED }.sumOf { it.totalAmount - it.paidAmount }
        val netWorthVal = totalWalletBal + totalAssetsVal - totalDebtVal

        val targetVal = when (subTab) {
            0 -> netWorthVal
            1 -> totalWalletBal
            2 -> totalAssetsVal
            else -> totalDebtVal
        }

        // Anchor reference date to the latest transaction if available
        val latestTx = transactions.maxByOrNull { it.year * 10000 + it.monthIndex * 100 + it.dayOfMonth }
        val curYear = latestTx?.year ?: Calendar.getInstance().get(Calendar.YEAR)
        val curMonth = latestTx?.monthIndex ?: (Calendar.getInstance().get(Calendar.MONTH) + 1)
        val curDay = latestTx?.dayOfMonth ?: Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        val monthNames = listOf("", "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")

        // Helper to compute daily delta for wallets, assets, and debts
        fun getDailyDelta(d: Int, m: Int, y: Int): Double {
            val walletDelta = transactions.filter { it.year == y && it.monthIndex == m && it.dayOfMonth == d && it.type == TransactionType.INCOME }.sumOf { it.amount } -
                    transactions.filter { it.year == y && it.monthIndex == m && it.dayOfMonth == d && it.type == TransactionType.EXPENSE }.sumOf { it.amount }

            val assetDelta = assets.flatMap { it.records }.filter { rec ->
                val p = parseDateParts(rec.date)
                p != null && p.first == d && p.second == m && p.third == y
            }.sumOf { if (it.isIncrease) it.amount else -it.amount }

            val debtDelta = debts.filter { it.type == DebtType.BORROWED }.flatMap { it.records }.filter { rec ->
                val p = parseDateParts(rec.date)
                p != null && p.first == d && p.second == m && p.third == y
            }.sumOf { if (it.type == "Pinjam Lagi") it.amount else -it.amount }

            return when (subTab) {
                0 -> walletDelta + assetDelta - debtDelta
                1 -> walletDelta
                2 -> assetDelta
                else -> debtDelta
            }
        }

        // Helper to compute monthly delta for wallets, assets, and debts
        fun getMonthlyDelta(m: Int, y: Int): Double {
            val walletDelta = transactions.filter { it.year == y && it.monthIndex == m && it.type == TransactionType.INCOME }.sumOf { it.amount } -
                    transactions.filter { it.year == y && it.monthIndex == m && it.type == TransactionType.EXPENSE }.sumOf { it.amount }

            val assetDelta = assets.flatMap { it.records }.filter { rec ->
                val p = parseDateParts(rec.date)
                p != null && p.second == m && p.third == y
            }.sumOf { if (it.isIncrease) it.amount else -it.amount }

            val debtDelta = debts.filter { it.type == DebtType.BORROWED }.flatMap { it.records }.filter { rec ->
                val p = parseDateParts(rec.date)
                p != null && p.second == m && p.third == y
            }.sumOf { if (it.type == "Pinjam Lagi") it.amount else -it.amount }

            return when (subTab) {
                0 -> walletDelta + assetDelta - debtDelta
                1 -> walletDelta
                2 -> assetDelta
                else -> debtDelta
            }
        }

        val rawList = mutableListOf<Double>()
        val startLbl: String
        val midLbl: String
        val endLbl: String

        when (period) {
            ChartPeriod.SEVEN_DAYS -> {
                val dayList = mutableListOf<Triple<Int, Int, Int>>() // day, month, year
                val c = Calendar.getInstance().apply {
                    set(Calendar.YEAR, curYear)
                    set(Calendar.MONTH, curMonth - 1)
                    set(Calendar.DAY_OF_MONTH, curDay)
                }
                for (offset in 6 downTo 0) {
                    val temp = Calendar.getInstance().apply {
                        timeInMillis = c.timeInMillis
                        add(Calendar.DAY_OF_MONTH, -offset)
                    }
                    dayList.add(Triple(temp.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR)))
                }

                startLbl = "${dayList.first().first} ${monthNames.getOrElse(dayList.first().second) { "" }}"
                midLbl = "${dayList[3].first} ${monthNames.getOrElse(dayList[3].second) { "" }}"
                endLbl = "${dayList.last().first} ${monthNames.getOrElse(dayList.last().second) { "" }}"

                val dailyDeltas = dayList.map { (d, m, y) -> getDailyDelta(d, m, y) }
                for (i in 0 until 7) {
                    val futureDelta = (i + 1 until 7).sumOf { dailyDeltas[it] }
                    rawList.add((targetVal - futureDelta).coerceAtLeast(0.0))
                }
            }
            ChartPeriod.ONE_WEEK -> {
                val dayList = (1..7).map { (curDay - 7 + it).coerceAtLeast(1) }
                startLbl = "${dayList.first()} ${monthNames.getOrElse(curMonth) { "" }}"
                midLbl = "${dayList[3]} ${monthNames.getOrElse(curMonth) { "" }}"
                endLbl = "${dayList.last()} ${monthNames.getOrElse(curMonth) { "" }}"

                val dailyDeltas = dayList.map { d -> getDailyDelta(d, curMonth, curYear) }
                for (i in 0 until 7) {
                    val futureDelta = (i + 1 until 7).sumOf { dailyDeltas[it] }
                    rawList.add((targetVal - futureDelta).coerceAtLeast(0.0))
                }
            }
            ChartPeriod.ONE_MONTH -> {
                val dayList = mutableListOf<Triple<Int, Int, Int>>() // day, month, year
                val c = Calendar.getInstance().apply {
                    set(Calendar.YEAR, curYear)
                    set(Calendar.MONTH, curMonth - 1)
                    set(Calendar.DAY_OF_MONTH, curDay)
                }
                for (offset in 29 downTo 0) {
                    val temp = Calendar.getInstance().apply {
                        timeInMillis = c.timeInMillis
                        add(Calendar.DAY_OF_MONTH, -offset)
                    }
                    dayList.add(Triple(temp.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR)))
                }

                startLbl = "${dayList.first().first} ${monthNames.getOrElse(dayList.first().second) { "" }}"
                midLbl = "${dayList[15].first} ${monthNames.getOrElse(dayList[15].second) { "" }}"
                endLbl = "${dayList.last().first} ${monthNames.getOrElse(dayList.last().second) { "" }}"

                val dailyDeltas = dayList.map { (d, m, y) -> getDailyDelta(d, m, y) }
                for (i in 0 until 30) {
                    val futureDelta = (i + 1 until 30).sumOf { dailyDeltas[it] }
                    rawList.add((targetVal - futureDelta).coerceAtLeast(0.0))
                }
            }
            ChartPeriod.THREE_MONTHS -> {
                val m1 = if (curMonth - 2 <= 0) curMonth - 2 + 12 else curMonth - 2
                val m2 = if (curMonth - 1 <= 0) curMonth - 1 + 12 else curMonth - 1
                val m3 = curMonth
                val months = listOf(m1, m2, m3)

                startLbl = monthNames[m1]
                midLbl = monthNames[m2]
                endLbl = monthNames[m3]

                val monthlyDeltas = months.map { m ->
                    val y = if (m > curMonth) curYear - 1 else curYear
                    getMonthlyDelta(m, y)
                }
                val m1Net = targetVal - monthlyDeltas[1] - monthlyDeltas[2]
                val m2Net = targetVal - monthlyDeltas[2]
                val m3Net = targetVal
                rawList.addAll(listOf(
                    m1Net.coerceAtLeast(0.0),
                    (m1Net + (m2Net - m1Net) * 0.5).coerceAtLeast(0.0),
                    m2Net.coerceAtLeast(0.0),
                    (m2Net + (m3Net - m2Net) * 0.5).coerceAtLeast(0.0),
                    m3Net.coerceAtLeast(0.0)
                ))
            }
            ChartPeriod.ONE_YEAR, ChartPeriod.ALL -> {
                startLbl = "Jan $curYear"
                midLbl = "Jun $curYear"
                endLbl = "Des $curYear"

                val monthlyDeltas = (1..12).map { m -> getMonthlyDelta(m, curYear) }
                for (i in 0 until 12) {
                    val futureDelta = (i + 1 until 12).sumOf { monthlyDeltas[it] }
                    rawList.add((targetVal - futureDelta).coerceAtLeast(0.0))
                }
            }
        }

        val startVal = rawList.firstOrNull() ?: targetVal
        val endVal = rawList.lastOrNull() ?: targetVal
        val calculatedDelta = endVal - startVal
        val percent = if (startVal > 0) {
            (calculatedDelta / startVal) * 100.0
        } else if (endVal > 0) {
            100.0
        } else {
            0.0
        }

        // Trend direction: For debt (subTab == 3), decreasing debt is positive (1), increasing is negative (-1)
        val isPos = if (subTab == 3) calculatedDelta <= 0 else calculatedDelta >= 0
        val trend = if (subTab == 3) {
            if (calculatedDelta < -100.0) 1 else if (calculatedDelta > 100.0) -1 else 0
        } else {
            if (calculatedDelta > 100.0) 1 else if (calculatedDelta < -100.0) -1 else 0
        }

        val normalized = normalizePoints(rawList)

        return ChartSeriesData(
            startLabel = startLbl,
            midLabel = midLbl,
            endLabel = endLbl,
            points = normalized,
            rawValues = rawList,
            total = targetVal,
            averagePerDay = if (rawList.isNotEmpty()) rawList.average() else targetVal,
            peakValue = rawList.maxOrNull() ?: targetVal,
            peakDayLabel = endLbl,
            topCategory = "",
            topCategoryAmount = 0.0,
            percentChange = percent,
            isPositive = isPos,
            deltaAmount = calculatedDelta,
            trendDirection = trend
        )
    }

    fun getMonthlyNetWorthBars(
        wallets: List<AccountWallet>,
        assets: List<AssetItem>,
        debts: List<DebtItem>,
        transactions: List<Transaction>
    ): List<MonthlyNetWorthBar> {
        val totalWalletBal = wallets.sumOf { it.balance }
        val totalAssetsVal = assets.sumOf { it.value }
        val totalDebtVal = debts.filter { it.type == DebtType.BORROWED }.sumOf { it.totalAmount - it.paidAmount }
        val currentNetWorth = totalWalletBal + totalAssetsVal - totalDebtVal

        val latestTx = transactions.maxByOrNull { it.year * 10000 + it.monthIndex * 100 + it.dayOfMonth }
        val endYear = latestTx?.year ?: Calendar.getInstance().get(Calendar.YEAR)
        val endMonth = latestTx?.monthIndex ?: (Calendar.getInstance().get(Calendar.MONTH) + 1)

        val monthNames = listOf("", "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")

        val monthsList = mutableListOf<Pair<Int, Int>>() // Pair(monthIndex, year)
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, endYear)
            set(Calendar.MONTH, endMonth - 1)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        for (i in 5 downTo 0) {
            val c = Calendar.getInstance().apply {
                timeInMillis = cal.timeInMillis
                add(Calendar.MONTH, -i)
            }
            monthsList.add(Pair(c.get(Calendar.MONTH) + 1, c.get(Calendar.YEAR)))
        }

        val monthlyCashflows = monthsList.map { (m, y) ->
            val inc = transactions.filter { it.year == y && it.monthIndex == m && it.type == TransactionType.INCOME }.sumOf { it.amount }
            val exp = transactions.filter { it.year == y && it.monthIndex == m && it.type == TransactionType.EXPENSE }.sumOf { it.amount }
            inc - exp
        }

        val netWorthValues = mutableListOf<Double>()
        for (i in 0 until 6) {
            val futureDelta = (i + 1 until 6).sumOf { monthlyCashflows[it] }
            val nw = currentNetWorth - futureDelta
            netWorthValues.add(nw)
        }

        val maxAbs = netWorthValues.maxOfOrNull { Math.abs(it) }?.coerceAtLeast(1000.0) ?: 1.0

        return netWorthValues.mapIndexed { index, nw ->
            val (m, _) = monthsList[index]
            val prevVal = if (index > 0) netWorthValues[index - 1] else nw
            val diff = nw - prevVal
            val trend = if (diff > 1000.0) 1 else if (diff < -1000.0) -1 else 0

            val formatted = CurrencyHelper.formatCompact(Math.abs(nw))
            val sign = if (nw < 0) "-" else ""
            val ratio = (Math.abs(nw) / maxAbs).toFloat().coerceIn(0.12f, 1.0f)

            MonthlyNetWorthBar(
                monthName = monthNames.getOrElse(m) { "Bln" },
                value = nw,
                formattedValue = "$sign$formatted",
                trendDirection = trend,
                heightRatio = ratio
            )
        }
    }

    fun getAssetValuationTrend(
        asset: AssetItem,
        period: ChartPeriod
    ): ChartSeriesData {
        val (startLbl, midLbl, endLbl, count) = when (period) {
            ChartPeriod.SEVEN_DAYS -> Quadruple("22 Okt", "25 Okt", "29 Okt", 7)
            ChartPeriod.ONE_WEEK -> Quadruple("20 Okt", "24 Okt", "29 Okt", 7)
            ChartPeriod.ONE_MONTH -> Quadruple("30 Sep", "15 Okt", "29 Okt", 30)
            ChartPeriod.THREE_MONTHS -> Quadruple("29 Jul", "10 Sep", "29 Okt", 12)
            ChartPeriod.ONE_YEAR -> Quadruple("Nov 2025", "Mei 2026", "Okt 2026", 12)
            ChartPeriod.ALL -> Quadruple("30 Sep", "15 Okt", "29 Okt", 10)
        }

        val baseVal = asset.value
        val rawList = mutableListOf<Double>()
        for (i in 0 until count) {
            val progress = i.toFloat() / (count - 1).coerceAtLeast(1)
            // Steady upward growth for investments
            val v = baseVal * (0.75 + (0.25 * progress) + (kotlin.math.sin(progress * 4.0) * 0.03))
            rawList.add(v.coerceAtLeast(0.0))
        }

        val normalized = normalizePoints(rawList)
        return ChartSeriesData(
            startLabel = startLbl,
            midLabel = midLbl,
            endLabel = endLbl,
            points = normalized,
            rawValues = rawList,
            total = baseVal,
            averagePerDay = baseVal / count,
            peakValue = baseVal,
            peakDayLabel = endLbl,
            topCategory = asset.assetType,
            topCategoryAmount = baseVal,
            percentChange = asset.performancePercent,
            isPositive = asset.performancePercent >= 0,
            deltaAmount = baseVal * (asset.performancePercent / 100.0),
            trendDirection = if (asset.performancePercent > 0.01) 1 else if (asset.performancePercent < -0.01) -1 else 0
        )
    }

    fun getWalletBalanceTrend(
        wallet: AccountWallet,
        transactions: List<Transaction>,
        period: ChartPeriod
    ): ChartSeriesData {
        val (startLbl, midLbl, endLbl, count) = when (period) {
            ChartPeriod.SEVEN_DAYS -> Quadruple("22 Okt", "25 Okt", "28 Okt", 7)
            ChartPeriod.ONE_WEEK -> Quadruple("20 Okt", "23 Okt", "26 Okt", 7)
            ChartPeriod.ONE_MONTH -> Quadruple("1 Okt", "15 Okt", "31 Okt", 30)
            ChartPeriod.THREE_MONTHS -> Quadruple("Agu", "Sep", "Okt", 12)
            ChartPeriod.ONE_YEAR, ChartPeriod.ALL -> Quadruple("Jan", "Jun", "Okt", 10)
        }

        val curBal = wallet.balance
        val rawList = mutableListOf<Double>()
        for (i in 0 until count) {
            val progress = i.toFloat() / (count - 1).coerceAtLeast(1)
            val v = curBal * (1.2 - (0.2 * progress) + (kotlin.math.sin(progress * 5.0) * 0.08))
            rawList.add(v.coerceAtLeast(0.0))
        }

        val normalized = normalizePoints(rawList)
        return ChartSeriesData(
            startLabel = startLbl,
            midLabel = midLbl,
            endLabel = endLbl,
            points = normalized,
            rawValues = rawList,
            total = curBal,
            averagePerDay = curBal / count,
            peakValue = rawList.maxOrNull() ?: curBal,
            peakDayLabel = startLbl,
            topCategory = wallet.bankType,
            topCategoryAmount = curBal,
            percentChange = -5.4,
            isPositive = false,
            deltaAmount = -curBal * 0.054,
            trendDirection = -1
        )
    }

    private fun normalizePoints(values: List<Double>): List<Float> {
        if (values.isEmpty()) return listOf(0.5f, 0.5f)
        val min = values.minOrNull() ?: 0.0
        val max = values.maxOrNull() ?: 1.0
        val diff = (max - min).coerceAtLeast(1.0)

        return values.map {
            // Keep points comfortably inside 0.15f..0.90f range so lines look elegant
            (0.15f + ((it - min) / diff * 0.75f).toFloat()).coerceIn(0.05f, 0.95f)
        }
    }

    fun getCumulativeComparison(
        transactions: List<Transaction>,
        month: Int,
        year: Int,
        walletFilter: String = "Semua"
    ): CumulativeComparisonData {
        val cal = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.YEAR, year)
            set(java.util.Calendar.MONTH, month - 1)
        }
        val daysInMonth = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
        val curMonthName = monthNamesShort.getOrElse(month) { "" }

        val filtered = transactions.filter {
            it.monthIndex == month && it.year == year &&
            (walletFilter == "Semua" || it.accountName.equals(walletFilter, ignoreCase = true))
        }

        val incomeTxs = filtered.filter { it.type == TransactionType.INCOME }
        val expenseTxs = filtered.filter { it.type == TransactionType.EXPENSE }

        val totalIncome = incomeTxs.sumOf { it.amount }
        val totalExpense = expenseTxs.sumOf { it.amount }
        val saved = totalIncome - totalExpense
        val savingsRate = if (totalIncome > 0) (((totalIncome - totalExpense) / totalIncome) * 100).toInt().coerceIn(0, 100) else 0

        var cumInc = 0.0
        var cumExp = 0.0
        val incProgression = mutableListOf<Double>()
        val expProgression = mutableListOf<Double>()

        for (day in 1..daysInMonth) {
            cumInc += incomeTxs.filter { it.dayOfMonth == day }.sumOf { it.amount }
            cumExp += expenseTxs.filter { it.dayOfMonth == day }.sumOf { it.amount }
            incProgression.add(cumInc)
            expProgression.add(cumExp)
        }

        val maxVal = maxOf(totalIncome, totalExpense).coerceAtLeast(100.0)

        val normalizedInc = incProgression.map {
            (0.1f + (it / maxVal * 0.8f).toFloat()).coerceIn(0.05f, 0.95f)
        }
        val normalizedExp = expProgression.map {
            (0.1f + (it / maxVal * 0.8f).toFloat()).coerceIn(0.05f, 0.95f)
        }

        val isHigher = saved >= 0
        val note = if (isHigher) {
            "Pemasukan tetap unggul sepanjang periode (+Rp${CurrencyHelper.formatCompact(saved)})"
        } else {
            "Pengeluaran melebihi pemasukan (-Rp${CurrencyHelper.formatCompact(-saved)})"
        }

        return CumulativeComparisonData(
            incomePoints = normalizedInc,
            expensePoints = normalizedExp,
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            savedAmount = saved,
            savingsRatePercent = savingsRate,
            summaryTakeaway = note,
            isIncomeHigher = isHigher,
            rawIncomeList = incProgression,
            rawExpenseList = expProgression,
            dayLabels = (1..daysInMonth).map { "$it $curMonthName" }
        )
    }

    private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}
