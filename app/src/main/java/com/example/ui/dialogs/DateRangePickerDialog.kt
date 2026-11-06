package com.example.ui.dialogs

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TextWhite
import java.util.Calendar

data class CustomDateRange(
    val startDay: Int,
    val startMonth: Int,
    val startYear: Int,
    val endDay: Int,
    val endMonth: Int,
    val endYear: Int
) {
    val label: String
        get() {
            val months = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")
            val sm = months.getOrElse(startMonth - 1) { "Nov" }
            val em = months.getOrElse(endMonth - 1) { "Nov" }
            return if (startMonth == endMonth && startYear == endYear) {
                "$startDay $sm - $endDay $em $startYear"
            } else {
                "$startDay $sm $startYear - $endDay $em $endYear"
            }
        }
}

@Composable
fun DateRangePickerDialog(
    currentYear: Int = 2026,
    initialMonth: Int = 11,
    initialRange: CustomDateRange? = null,
    onDismiss: () -> Unit,
    onConfirm: (CustomDateRange) -> Unit
) {
    var startSelected by remember {
        mutableStateOf(
            initialRange?.let { Triple(it.startDay, it.startMonth, it.startYear) }
                ?: Triple(2, 11, currentYear)
        )
    }
    var endSelected by remember {
        mutableStateOf<Triple<Int, Int, Int>?>(
            initialRange?.let { Triple(it.endDay, it.endMonth, it.endYear) }
                ?: Triple(5, 11, currentYear)
        )
    }

    val months = listOf(
        Pair("November $currentYear", Pair(11, currentYear)),
        Pair("Desember $currentYear", Pair(12, currentYear)),
        Pair("Januari ${currentYear + 1}", Pair(1, currentYear + 1))
    )

    val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")

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
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = TextWhite)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Pilih rentang",
                            fontSize = 12.sp,
                            color = TextSubtle
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val rangeText = if (endSelected != null) {
                                val sm = monthNames.getOrElse(startSelected.second - 1) { "" }
                                val em = monthNames.getOrElse(endSelected!!.second - 1) { "" }
                                "${startSelected.first} $sm – ${endSelected!!.first} $em"
                            } else {
                                val sm = monthNames.getOrElse(startSelected.second - 1) { "" }
                                "${startSelected.first} $sm – Tanggal Akhir"
                            }
                            Text(
                                text = rangeText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val end = endSelected ?: startSelected
                            // Ensure start <= end
                            val calStart = Calendar.getInstance().apply {
                                set(startSelected.third, startSelected.second - 1, startSelected.first)
                            }
                            val calEnd = Calendar.getInstance().apply {
                                set(end.third, end.second - 1, end.first)
                            }
                            val (finalStart, finalEnd) = if (calStart.after(calEnd)) {
                                Pair(end, startSelected)
                            } else {
                                Pair(startSelected, end)
                            }
                            onConfirm(
                                CustomDateRange(
                                    startDay = finalStart.first,
                                    startMonth = finalStart.second,
                                    startYear = finalStart.third,
                                    endDay = finalEnd.first,
                                    endMonth = finalEnd.second,
                                    endYear = finalEnd.third
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Simpan", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                // Days of week header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    listOf("M", "S", "S", "R", "K", "J", "S").forEach { day ->
                        Text(
                            text = day,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSubtle,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Month Calendars List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    months.forEach { (title, monthYear) ->
                        val (m, y) = monthYear
                        item {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )

                                val cal = Calendar.getInstance()
                                cal.set(Calendar.YEAR, y)
                                cal.set(Calendar.MONTH, m - 1)
                                cal.set(Calendar.DAY_OF_MONTH, 1)
                                val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1=Sun, 2=Mon...
                                val offset = firstDayOfWeek - 1
                                val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

                                val totalCells = ((offset + daysInMonth + 6) / 7) * 7

                                for (row in 0 until totalCells / 7) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp),
                                        horizontalArrangement = Arrangement.SpaceAround
                                    ) {
                                        for (col in 0 until 7) {
                                            val cellIndex = row * 7 + col
                                            val dayNumber = cellIndex - offset + 1

                                            if (dayNumber in 1..daysInMonth) {
                                                val cellDate = Triple(dayNumber, m, y)
                                                val isStart = cellDate == startSelected
                                                val isEnd = cellDate == endSelected
                                                val inRange = isDateInRange(cellDate, startSelected, endSelected)

                                                val cellBg = when {
                                                    isStart || isEnd -> AccentBlue
                                                    inRange -> AccentBlue.copy(alpha = 0.35f)
                                                    else -> Color.Transparent
                                                }

                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(42.dp)
                                                        .clip(
                                                            when {
                                                                isStart && (endSelected == null || isEnd) -> CircleShape
                                                                isStart -> RoundedCornerShape(topStart = 21.dp, bottomStart = 21.dp)
                                                                isEnd -> RoundedCornerShape(topEnd = 21.dp, bottomEnd = 21.dp)
                                                                inRange -> RoundedCornerShape(0.dp)
                                                                else -> CircleShape
                                                            }
                                                        )
                                                        .background(cellBg)
                                                        .clickable {
                                                            if (endSelected == null) {
                                                                endSelected = cellDate
                                                            } else {
                                                                startSelected = cellDate
                                                                endSelected = null
                                                            }
                                                        },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = "$dayNumber",
                                                        fontSize = 13.sp,
                                                        fontWeight = if (isStart || isEnd) FontWeight.Bold else FontWeight.Normal,
                                                        color = if (isStart || isEnd) Color.White else if (inRange) Color.White else TextWhite
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
                    }
                    item {
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }
        }
    }
}

private fun isDateInRange(
    date: Triple<Int, Int, Int>,
    start: Triple<Int, Int, Int>,
    end: Triple<Int, Int, Int>?
): Boolean {
    if (end == null) return false
    val calDate = Calendar.getInstance().apply { set(date.third, date.second - 1, date.first, 0, 0, 0) }
    val calStart = Calendar.getInstance().apply { set(start.third, start.second - 1, start.first, 0, 0, 0) }
    val calEnd = Calendar.getInstance().apply { set(end.third, end.second - 1, end.first, 0, 0, 0) }
    val (s, e) = if (calStart.after(calEnd)) Pair(calEnd, calStart) else Pair(calStart, calEnd)
    return !calDate.before(s) && !calDate.after(e)
}
