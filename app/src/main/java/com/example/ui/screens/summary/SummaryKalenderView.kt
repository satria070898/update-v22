package com.example.ui.screens.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.Transaction
import com.example.data.model.TransactionType
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentBlueBright
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TextWhite
import com.example.util.CategoryIconHelper
import com.example.util.CurrencyHelper
import java.util.Calendar

@Composable
fun SummaryKalenderView(
    selectedMonth: Int,
    selectedYear: Int,
    monthName: String,
    transactions: List<Transaction>,
    onTransactionClick: (Transaction) -> Unit = {},
    onAddTransactionForDate: (day: Int) -> Unit = {},
    onSelectMonth: (monthIndex: Int) -> Unit = {},
    onNavigateToTransactions: () -> Unit = {}
) {
    var selectedDay by remember { mutableIntStateOf(1) }

    val daysInMonth = remember(selectedMonth, selectedYear) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, selectedYear)
        cal.set(Calendar.MONTH, selectedMonth - 1)
        cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    // First day of week (Monday = 1st column in video: S S R K J S M)
    val firstDayOffset = remember(selectedMonth, selectedYear) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, selectedYear)
        cal.set(Calendar.MONTH, selectedMonth - 1)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val dow = cal.get(Calendar.DAY_OF_WEEK) // 1=Sun, 2=Mon...
        // Convert to Monday=0: Mon=2 -> 0, Tue=3 -> 1, ..., Sun=1 -> 6
        (dow + 5) % 7
    }

    // Group transactions by day of month
    val txByDay = remember(transactions) {
        transactions.groupBy { it.dayOfMonth }
    }

    val totalMonthIncome = remember(transactions) {
        transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    }
    val totalMonthExpense = remember(transactions) {
        transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    }
    val totalMonthNet = totalMonthIncome - totalMonthExpense

    val selectedDayTxs = remember(transactions, selectedDay) {
        transactions.filter { it.dayOfMonth == selectedDay }
    }
    val selectedDayIncome = remember(selectedDayTxs) {
        selectedDayTxs.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    }
    val selectedDayExpense = remember(selectedDayTxs) {
        selectedDayTxs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Calendar Grid Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Header: Month Navigation < Bulan Tahun >
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                val prevM = if (selectedMonth > 1) selectedMonth - 1 else 12
                                onSelectMonth(prevM)
                            },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "Bulan Sebelumnya", tint = TextWhite, modifier = Modifier.size(20.dp))
                        }

                        Text(
                            text = "$monthName $selectedYear",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )

                        IconButton(
                            onClick = {
                                val nextM = if (selectedMonth < 12) selectedMonth + 1 else 1
                                onSelectMonth(nextM)
                            },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Bulan Berikutnya", tint = TextWhite, modifier = Modifier.size(20.dp))
                        }
                    }

                    // Header: S S R K J S M (Senin to Minggu)
                    val daysHeader = listOf("S", "S", "R", "K", "J", "S", "M")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        daysHeader.forEach { d ->
                            Text(
                                text = d,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSubtle,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val totalCells = ((firstDayOffset + daysInMonth + 6) / 7) * 7

                    for (row in 0 until totalCells / 7) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            for (col in 0 until 7) {
                                val cellIdx = row * 7 + col
                                val dayNum = cellIdx - firstDayOffset + 1

                                if (dayNum in 1..daysInMonth) {
                                    val isSelected = dayNum == selectedDay
                                    val dayList = txByDay[dayNum] ?: emptyList()
                                    val dayInc = dayList.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                                    val dayExp = dayList.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                                    val txCount = dayList.size

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(58.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (isSelected) Color(0xFF1E2433)
                                                else Color.Transparent
                                            )
                                            .then(
                                                if (isSelected) {
                                                    Modifier.border(1.dp, AccentBlue, RoundedCornerShape(8.dp))
                                                } else Modifier
                                            )
                                            .clickable { selectedDay = dayNum }
                                            .padding(2.dp),
                                        contentAlignment = Alignment.TopCenter
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Top,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Text(
                                                text = "$dayNum",
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected || txCount > 0) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) AccentBlueBright else TextWhite
                                            )

                                            if (txCount > 0) {
                                                Text(
                                                    text = "$txCount hal",
                                                    fontSize = 7.5.sp,
                                                    color = TextSubtle
                                                )
                                                if (dayInc > 0) {
                                                    Surface(
                                                        shape = RoundedCornerShape(3.dp),
                                                        color = IncomeGreen.copy(alpha = 0.2f),
                                                        modifier = Modifier.padding(top = 1.dp)
                                                    ) {
                                                        Text(
                                                            text = "+${CurrencyHelper.formatCompact(dayInc)}",
                                                            fontSize = 7.5.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = IncomeGreen,
                                                            modifier = Modifier.padding(horizontal = 2.dp)
                                                        )
                                                    }
                                                }
                                                if (dayExp > 0) {
                                                    Surface(
                                                        shape = RoundedCornerShape(3.dp),
                                                        color = ExpenseRed.copy(alpha = 0.2f),
                                                        modifier = Modifier.padding(top = 1.dp)
                                                    ) {
                                                        Text(
                                                            text = "-${CurrencyHelper.formatCompact(dayExp)}",
                                                            fontSize = 7.5.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = ExpenseRed,
                                                            modifier = Modifier.padding(horizontal = 2.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    // Dimmed dates outside month
                                    val outsideNum = if (dayNum <= 0) 31 + dayNum else dayNum - daysInMonth
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(58.dp)
                                            .padding(2.dp),
                                        contentAlignment = Alignment.TopCenter
                                    ) {
                                        Text(
                                            text = "$outsideNum",
                                            fontSize = 11.sp,
                                            color = Color(0xFF334155)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Selected Day Interactive Bar with Quick Add Action
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.8f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "$selectedDay $monthName $selectedYear",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (selectedDayIncome > 0) {
                                Text("+${CurrencyHelper.formatCompact(selectedDayIncome)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = IncomeGreen)
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            if (selectedDayExpense > 0) {
                                Text("-${CurrencyHelper.formatCompact(selectedDayExpense)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ExpenseRed)
                            }
                            if (selectedDayIncome == 0.0 && selectedDayExpense == 0.0) {
                                Text("Tidak ada transaksi tercatat", fontSize = 11.sp, color = TextSubtle)
                            }
                        }
                    }

                    // Button to add transaction for this specific date
                    Button(
                        onClick = { onAddTransactionForDate(selectedDay) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentBlue,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Catat", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Selected Day Transactions List (if any)
        if (selectedDayTxs.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Transaksi Tanggal $selectedDay (${selectedDayTxs.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "Buka di Transaksi →",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AccentBlueBright,
                        modifier = Modifier.clickable { onNavigateToTransactions() }
                    )
                }
            }

            items(selectedDayTxs) { tx ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DarkSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTransactionClick(tx) }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val catIcon = CategoryIconHelper.getCategoryIcon(tx.category)
                            val catColor = CategoryIconHelper.getCategoryColor(tx.category, tx.type)
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(catColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = catIcon, contentDescription = null, tint = catColor, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(tx.title.ifEmpty { tx.category }, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                                Text("${tx.category} · ${tx.accountName}", fontSize = 10.sp, color = TextSubtle)
                            }
                        }

                        Text(
                            text = "${if (tx.type == TransactionType.INCOME) "+" else "-"}Rp${CurrencyHelper.formatCompact(tx.amount)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (tx.type == TransactionType.INCOME) IncomeGreen else ExpenseRed
                        )
                    }
                }
            }
        }

        // Bottom Month Summary Card (Pendapatan, Pengeluaran, Arus Kas) matching video
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Row 1: Pendapatan
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = null, tint = IncomeGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pendapatan", fontSize = 13.sp, color = TextWhite)
                        }
                        Text(CurrencyHelper.formatRupiah(totalMonthIncome), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = IncomeGreen)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Row 2: Pengeluaran
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = null, tint = ExpenseRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pengeluaran", fontSize = 13.sp, color = TextWhite)
                        }
                        Text(CurrencyHelper.formatRupiah(totalMonthExpense), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ExpenseRed)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Row 3: Arus Kas
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = AccentBlueBright, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Arus Kas", fontSize = 13.sp, color = TextWhite)
                        }
                        Text("+${CurrencyHelper.formatRupiah(totalMonthNet)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = IncomeGreen)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
