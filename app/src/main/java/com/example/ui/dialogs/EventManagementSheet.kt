package com.example.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionType
import com.example.ui.components.AcaraTwoToneIcon
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentBlueBright
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ExpenseRedSoft
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TextWhite
import com.example.ui.theme.TrophyYellow
import com.example.ui.viewmodel.FinanceViewModel
import com.example.util.CurrencyHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventManagementSheet(
    viewModel: FinanceViewModel,
    onDismiss: () -> Unit,
    onNavigateToTransactions: (eventLabel: String) -> Unit,
    onAddTransactionWithEvent: (eventLabel: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val availableLabels by viewModel.labels.collectAsState()
    val allTransactions by viewModel.allTransactions.collectAsState()

    var newEventName by remember { mutableStateOf("") }
    var eventToDelete by remember { mutableStateOf<String?>(null) }

    val presetEvents = listOf(
        "🏖️ Liburan",
        "💍 Pernikahan",
        "🏡 Renovasi",
        "🚗 Mudik Lebaran",
        "🎂 Ulang Tahun",
        "💼 Bisnis",
        "🎉 Hari Raya"
    )

    // Calculate overall event stats
    val allEventTransactions = remember(allTransactions, availableLabels) {
        allTransactions.filter { tx ->
            availableLabels.any { label -> tx.label.contains(label, ignoreCase = true) }
        }
    }
    val totalEventExpense = remember(allEventTransactions) {
        allEventTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = AccentBlue.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, AccentBlue.copy(alpha = 0.35f)),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            AcaraTwoToneIcon(themeColor = AccentBlue, modifier = Modifier.size(24.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Kelola Acara & Proyek",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "Pantau pengeluaran liburan, proyek, dan acara khusus",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Overview Banner
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        border = BorderStroke(1.dp, DarkCardBorder)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            Color(0xFF1E293B),
                                            Color(0xFF0F172A)
                                        )
                                    )
                                )
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Total Pengeluaran Acara",
                                        fontSize = 11.5.sp,
                                        color = TextMuted
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = CurrencyHelper.formatRupiah(totalEventExpense),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = DarkSurfaceElevated,
                                    border = BorderStroke(1.dp, DarkCardBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Label,
                                            contentDescription = null,
                                            tint = TrophyYellow,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${availableLabels.size} Acara Terdaftar",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextWhite
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Add Event Section
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                        border = BorderStroke(1.dp, DarkCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Tambah Acara / Proyek Baru",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextWhite
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            // Preset chips
                            val presetScrollState = rememberScrollState()
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(presetScrollState),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                presetEvents.forEach { preset ->
                                    val cleanName = preset.substringAfter(" ").trim()
                                    val isAlreadyAdded = availableLabels.any { it.equals(cleanName, ignoreCase = true) }
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isAlreadyAdded) AccentBlue.copy(alpha = 0.2f) else DarkSurface,
                                        border = BorderStroke(
                                            0.8.dp,
                                            if (isAlreadyAdded) AccentBlue else DarkCardBorder
                                        ),
                                        modifier = Modifier.clickable {
                                            if (!isAlreadyAdded) {
                                                viewModel.addLabel(cleanName)
                                            }
                                        }
                                    ) {
                                        Text(
                                            text = preset,
                                            fontSize = 11.sp,
                                            color = if (isAlreadyAdded) AccentBlueBright else TextWhite,
                                            fontWeight = if (isAlreadyAdded) FontWeight.Bold else FontWeight.Normal,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = newEventName,
                                    onValueChange = { newEventName = it },
                                    placeholder = {
                                        Text(
                                            "Nama acara (misal: Wisuda, Solo Trip)...",
                                            color = TextSubtle,
                                            fontSize = 12.sp
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextWhite,
                                        unfocusedTextColor = TextWhite,
                                        focusedBorderColor = AccentBlue,
                                        unfocusedBorderColor = DarkCardBorder,
                                        focusedContainerColor = DarkSurface,
                                        unfocusedContainerColor = DarkSurface
                                    ),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (newEventName.isNotBlank()) {
                                            viewModel.addLabel(newEventName.trim())
                                            newEventName = ""
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                                    enabled = newEventName.isNotBlank(),
                                    modifier = Modifier.height(50.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Tambah Acara",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // Event List
                if (availableLabels.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Label,
                                    contentDescription = null,
                                    tint = TextSubtle,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Belum ada acara yang ditambahkan",
                                    color = TextMuted,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                } else {
                    items(availableLabels, key = { it }) { eventName ->
                        val eventTxs = remember(allTransactions, eventName) {
                            allTransactions.filter { it.label.contains(eventName, ignoreCase = true) }
                        }
                        val eventExpense = remember(eventTxs) {
                            eventTxs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                        }
                        val eventIncome = remember(eventTxs) {
                            eventTxs.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                        }

                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                            border = BorderStroke(1.dp, DarkCardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = TrophyYellow.copy(alpha = 0.15f),
                                            border = BorderStroke(0.8.dp, TrophyYellow.copy(alpha = 0.35f)),
                                            modifier = Modifier.size(34.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Label,
                                                    contentDescription = null,
                                                    tint = TrophyYellow,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = eventName,
                                                fontSize = 14.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextWhite
                                            )
                                            Text(
                                                text = "${eventTxs.size} transaksi terkait",
                                                fontSize = 11.sp,
                                                color = TextSubtle
                                            )
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = CurrencyHelper.formatRupiah(eventExpense),
                                            fontSize = 14.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (eventExpense > 0) ExpenseRed else TextMuted
                                        )
                                        if (eventIncome > 0) {
                                            Text(
                                                text = "+${CurrencyHelper.formatRupiah(eventIncome)}",
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = IncomeGreen
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Action Buttons: "Lihat Transaksi" & "+ Catat Biaya"
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = DarkSurface,
                                        border = BorderStroke(1.dp, DarkCardBorder),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                onNavigateToTransactions(eventName)
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(vertical = 7.dp, horizontal = 10.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Receipt,
                                                contentDescription = null,
                                                tint = AccentBlueBright,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Lihat Transaksi",
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = TextWhite
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = AccentBlue.copy(alpha = 0.18f),
                                        border = BorderStroke(1.dp, AccentBlue.copy(alpha = 0.4f)),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                onAddTransactionWithEvent(eventName)
                                            }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(vertical = 7.dp, horizontal = 10.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = null,
                                                tint = AccentBlueBright,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Catat Pengeluaran",
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = AccentBlueBright
                                            )
                                        }
                                    }

                                    // Delete Label button
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = DarkSurface,
                                        border = BorderStroke(1.dp, DarkCardBorder),
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clickable { eventToDelete = eventName }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Hapus Acara",
                                                tint = ExpenseRed.copy(alpha = 0.8f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    // Delete confirmation dialog
    eventToDelete?.let { evName ->
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { eventToDelete = null },
            containerColor = DarkSurface,
            title = {
                Text(
                    text = "Hapus Acara \"$evName\"?",
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    text = "Label acara ini akan dihapus dari daftar. Transaksi yang sudah menggunakan label ini tidak akan terhapus.",
                    color = TextMuted,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteLabel(evName)
                        eventToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
                ) {
                    Text("Hapus", color = Color.White)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { eventToDelete = null }) {
                    Text("Batal", color = TextMuted)
                }
            }
        )
    }
}
