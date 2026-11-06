package com.example.ui.screens.summary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.util.CurrencyHelper
import kotlin.math.roundToInt

/**
 * Circular Health Gauge used in Skor Kesehatan Keuangan
 */
@Composable
fun FinancialHealthScoreGauge(
    score: Int,
    label: String,
    modifier: Modifier = Modifier.size(68.dp)
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeW = 6.dp.toPx()
            val radius = (minOf(size.width, size.height) - strokeW) / 2f
            val center = Offset(size.width / 2f, size.height / 2f)

            // Background track
            drawCircle(
                color = Color(0xFF1E293B),
                radius = radius,
                center = center,
                style = Stroke(strokeW)
            )

            // Active progress arc
            val sweep = (score / 100f) * 360f
            drawArc(
                color = if (score >= 70) IncomeGreen else if (score >= 50) Color(0xFFF59E0B) else ExpenseRed,
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2f, radius * 2f),
                style = Stroke(strokeW, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$score",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Text(
                text = "/100",
                fontSize = 9.sp,
                color = TextSubtle
            )
        }
    }
}

/**
 * Interactive Mini Trend Line Chart with touch/scrubber tooltip support
 */
@Composable
fun MiniMonthTrendLineChart(
    values: List<Double>,
    labels: List<String>,
    lineColor: Color,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(110.dp),
    onPointSelected: ((Int, Double) -> Unit)? = null
) {
    if (values.isEmpty()) return

    val maxVal = values.maxOrNull()?.coerceAtLeast(1.0) ?: 1.0
    val minVal = (values.minOrNull() ?: 0.0).coerceAtLeast(0.0)
    val range = (maxVal - minVal).coerceAtLeast(1.0)

    var touchIndex by remember { mutableIntStateOf(-1) }

    Column(modifier = modifier) {
        // Floating interactive tooltip if user touches the chart
        if (touchIndex in values.indices) {
            val selectedVal = values[touchIndex]
            val selectedLabel = labels.getOrElse(touchIndex) { "Poin #${touchIndex + 1}" }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DarkSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, lineColor.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$selectedLabel: ",
                            fontSize = 10.sp,
                            color = TextSubtle
                        )
                        Text(
                            text = CurrencyHelper.formatRupiah(selectedVal),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = lineColor
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .pointerInput(values) {
                    detectTapGestures(
                        onPress = { offset ->
                            val count = values.size
                            if (count > 1) {
                                val stepX = size.width / (count - 1)
                                val idx = (offset.x / stepX).roundToInt().coerceIn(0, count - 1)
                                touchIndex = idx
                                onPointSelected?.invoke(idx, values[idx])
                            }
                        }
                    )
                }
                .pointerInput(values) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { change, _ ->
                            val count = values.size
                            if (count > 1) {
                                val stepX = size.width / (count - 1)
                                val idx = (change.position.x / stepX).roundToInt().coerceIn(0, count - 1)
                                touchIndex = idx
                                onPointSelected?.invoke(idx, values[idx])
                            }
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val count = values.size
                if (count < 2) return@Canvas

                // 3x3 Dashed Grid background
                with(com.example.util.ChartDataHelper) {
                    drawDashedChartGrid(
                        horizontalLines = 3,
                        verticalLines = 3,
                        gridColor = Color(0xFF334155).copy(alpha = 0.35f),
                        drawFrame = true
                    )
                }

                val stepX = w / (count - 1)
                val points = values.mapIndexed { idx, v ->
                    val normY = ((v - minVal) / range).toFloat().coerceIn(0.1f, 0.88f)
                    Offset(idx * stepX, h * (1f - normY))
                }

                val path = Path().apply {
                    moveTo(points[0].x, points[0].y)
                    for (i in 1 until points.size) {
                        val prev = points[i - 1]
                        val cur = points[i]
                        val cx = (prev.x + cur.x) / 2f
                        cubicTo(cx, prev.y, cx, cur.y, cur.x, cur.y)
                    }
                }

                // Fill gradient
                val fillPath = Path().apply {
                    addPath(path)
                    lineTo(points.last().x, h)
                    lineTo(points.first().x, h)
                    close()
                }
                drawPath(
                    fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(lineColor.copy(alpha = 0.28f), Color.Transparent),
                        startY = 0f,
                        endY = h
                    )
                )

                // Line stroke
                drawPath(
                    path,
                    color = lineColor,
                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw Scrubber vertical line if active
                if (touchIndex in points.indices) {
                    val p = points[touchIndex]
                    drawLine(
                        color = Color.White.copy(alpha = 0.4f),
                        start = Offset(p.x, 0f),
                        end = Offset(p.x, h),
                        strokeWidth = 1.dp.toPx()
                    )
                    drawCircle(
                        color = lineColor.copy(alpha = 0.35f),
                        radius = 8.dp.toPx(),
                        center = p
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 4.dp.toPx(),
                        center = p
                    )
                    drawCircle(
                        color = lineColor,
                        radius = 2.5.dp.toPx(),
                        center = p
                    )
                } else {
                    // Regular Dots
                    points.forEachIndexed { i, p ->
                        val isLast = i == points.size - 1
                        drawCircle(
                            color = if (isLast) lineColor else lineColor.copy(alpha = 0.7f),
                            radius = (if (isLast) 4.dp else 2.5.dp).toPx(),
                            center = p
                        )
                        drawCircle(
                            color = Color(0xFF131722),
                            radius = 1.2.dp.toPx(),
                            center = p
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            labels.take(values.size).forEachIndexed { idx, lbl ->
                val isSelected = idx == touchIndex
                Text(
                    text = lbl,
                    fontSize = 9.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) AccentBlueBright else TextSubtle
                )
            }
        }
    }
}

/**
 * Visual Sankey Diagram for Money Flow (Aliran Uang)
 */
@Composable
fun SankeyMoneyFlowView(
    incomeCategories: List<Pair<String, Double>>,
    expenseCategories: List<Pair<String, Double>>,
    savingsAmount: Double,
    onCategoryClick: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(180.dp)
) {
    val totalIncome = incomeCategories.sumOf { it.second }.coerceAtLeast(1.0)
    val totalExpense = expenseCategories.sumOf { it.second }
    val safeSavings = savingsAmount.coerceAtLeast(0.0)

    Row(
        modifier = modifier.padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Column (Inflow)
        Column(
            modifier = Modifier.width(90.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            incomeCategories.take(3).forEach { (name, amount) ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(IncomeGreen.copy(alpha = 0.15f))
                        .clickable { onCategoryClick?.invoke(name) }
                        .padding(horizontal = 6.dp, vertical = 6.dp)
                ) {
                    Column {
                        Text(name, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextWhite, maxLines = 1)
                        Text("Rp${CurrencyHelper.formatCompact(amount)}", fontSize = 9.sp, color = IncomeGreen)
                    }
                }
            }
        }

        // Center Flow Canvas
        Canvas(
            modifier = Modifier
                .weight(1f)
                .height(150.dp)
                .padding(horizontal = 4.dp)
        ) {
            val w = size.width
            val h = size.height

            val leftStartY = h * 0.25f
            val leftHeight = h * 0.5f

            // Connecting ribbons to right items
            val rightTops = listOf(h * 0.15f, h * 0.40f, h * 0.70f)
            val colors = listOf(
                Color(0xFFE11D48).copy(alpha = 0.45f), // Shopping
                Color(0xFFF59E0B).copy(alpha = 0.45f), // Debt
                IncomeGreen.copy(alpha = 0.55f)        // Savings
            )

            rightTops.forEachIndexed { i, targetY ->
                val p = Path().apply {
                    moveTo(0f, leftStartY + (i * leftHeight / 3f))
                    cubicTo(
                        w * 0.5f, leftStartY + (i * leftHeight / 3f),
                        w * 0.5f, targetY,
                        w, targetY
                    )
                    lineTo(w, targetY + 14.dp.toPx())
                    cubicTo(
                        w * 0.5f, targetY + 14.dp.toPx(),
                        w * 0.5f, leftStartY + ((i + 1) * leftHeight / 3f),
                        0f, leftStartY + ((i + 1) * leftHeight / 3f)
                    )
                    close()
                }
                drawPath(p, color = colors.getOrElse(i) { colors[0] })
            }
        }

        // Right Column (Outflow & Remaining)
        Column(
            modifier = Modifier.width(100.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            expenseCategories.take(2).forEach { (name, amount) ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ExpenseRed.copy(alpha = 0.15f))
                        .clickable { onCategoryClick?.invoke(name) }
                        .padding(horizontal = 6.dp, vertical = 6.dp)
                ) {
                    Column {
                        Text(name, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextWhite, maxLines = 1)
                        Text("Rp${CurrencyHelper.formatCompact(amount)}", fontSize = 9.sp, color = ExpenseRed)
                    }
                }
            }

            // Remaining / Tersisa
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(IncomeGreen.copy(alpha = 0.2f))
                    .padding(horizontal = 6.dp, vertical = 6.dp)
            ) {
                Column {
                    Text("Tersisa", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = IncomeGreen)
                    Text("Rp${CurrencyHelper.formatCompact(safeSavings)}", fontSize = 9.sp, color = TextWhite)
                }
            }
        }
    }
}

/**
 * Interactive Bar Chart for Pengeluaran Menurut Hari Kerja (S S R K J S M)
 */
@Composable
fun DayOfWeekSpendingBarChart(
    dayTotals: List<Double>, // 7 values for S S R K J S M
    peakDayIndex: Int = 2, // e.g. Rabu
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    val days = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")
    val shortDays = listOf("S", "S", "R", "K", "J", "S", "M")
    val maxVal = dayTotals.maxOrNull()?.coerceAtLeast(1000.0) ?: 1000.0

    var selectedDayIdx by remember { mutableIntStateOf(peakDayIndex) }

    Column(modifier = modifier) {
        // Interactive highlight banner
        if (selectedDayIdx in days.indices) {
            val dName = days[selectedDayIdx]
            val dAmt = dayTotals.getOrElse(selectedDayIdx) { 0.0 }
            val isPeak = selectedDayIdx == peakDayIndex

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = DarkSurfaceElevated,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isPeak) "🔥 $dName (Puncak):" else "$dName:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPeak) Color(0xFFF43F5E) else TextWhite
                        )
                    }
                    Text(
                        text = CurrencyHelper.formatRupiah(dAmt),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (dAmt > 0) ExpenseRed else TextMuted
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            dayTotals.forEachIndexed { idx, amt ->
                val ratio = (amt / maxVal).toFloat().coerceIn(0.08f, 1f)
                val isPeak = idx == peakDayIndex
                val isSelected = idx == selectedDayIdx

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedDayIdx = idx }
                ) {
                    if (amt > 0) {
                        Text(
                            text = CurrencyHelper.formatCompact(amt),
                            fontSize = 8.sp,
                            fontWeight = if (isPeak || isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isPeak) Color(0xFFF43F5E) else TextSubtle
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }

                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .height((70 * ratio).dp)
                            .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            .background(
                                if (isSelected) AccentBlue
                                else if (isPeak) Color(0xFFE11D48)
                                else if (amt > 0) Color(0xFF1E293B)
                                else Color(0xFF131826)
                            )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = shortDays.getOrElse(idx) { "" },
                        fontSize = 11.sp,
                        fontWeight = if (isSelected || isPeak) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (isSelected) AccentBlueBright else if (isPeak) Color(0xFFF43F5E) else TextMuted
                    )
                }
            }
        }
    }
}

/**
 * Mini Calendar Grid for Catatan Harian tracking streak
 */
@Composable
fun DailyTrackingCalendarGrid(
    daysInMonth: Int = 30,
    trackedDays: Set<Int>,
    currentStreakDays: Int = 4,
    onDayClick: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    val headers = listOf("S", "S", "R", "K", "J", "S", "M")

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            headers.forEach { h ->
                Text(
                    text = h,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSubtle,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Grid of days
        val rows = (daysInMonth + 6) / 7
        for (r in 0 until rows) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                for (c in 0 until 7) {
                    val dayNum = r * 7 + c + 1
                    if (dayNum in 1..daysInMonth) {
                        val isTracked = dayNum in trackedDays
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(26.dp)
                                .padding(horizontal = 2.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isTracked) IncomeGreen.copy(alpha = 0.28f) else Color(0xFF1E2433))
                                .clickable { onDayClick?.invoke(dayNum) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$dayNum",
                                fontSize = 10.sp,
                                fontWeight = if (isTracked) FontWeight.Bold else FontWeight.Normal,
                                color = if (isTracked) IncomeGreen else TextMuted
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
