package com.example.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.RecurringTransaction
import com.example.data.model.TransactionType
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentBlueBright
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
import com.example.ui.viewmodel.FinanceViewModel
import com.example.util.CurrencyHelper
import com.example.util.ThousandsSeparatorVisualTransformation
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringTransactionDialog(
    viewModel: FinanceViewModel,
    onDismiss: () -> Unit
) {
    val recurringList by viewModel.recurringTransactions.collectAsState()
    val wallets by viewModel.wallets.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<RecurringTransaction?>(null) }
    var itemToDelete by remember { mutableStateOf<RecurringTransaction?>(null) }
    var filterType by remember { mutableStateOf("Semua") } // "Semua", "Pengeluaran", "Pemasukan"

    val cal = Calendar.getInstance()
    val todayDay = cal.get(Calendar.DAY_OF_MONTH)
    val todayMonth = cal.get(Calendar.MONTH) + 1
    val todayYear = cal.get(Calendar.YEAR)
    val currentMonthYearKey = "$todayMonth/$todayYear"

    val filteredList = remember(recurringList, filterType) {
        when (filterType) {
            "Pengeluaran" -> recurringList.filter { it.type == TransactionType.EXPENSE }
            "Pemasukan" -> recurringList.filter { it.type == TransactionType.INCOME }
            else -> recurringList
        }
    }

    val totalRecurringExpense = remember(recurringList) {
        recurringList.filter { it.isActive && it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    }
    val totalRecurringIncome = remember(recurringList) {
        recurringList.filter { it.isActive && it.type == TransactionType.INCOME }.sumOf { it.amount }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("dialog_recurring_transactions"),
            shape = RoundedCornerShape(24.dp),
            color = DarkBackground,
            border = BorderStroke(1.dp, DarkCardBorder)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top App Bar in Dialog
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0x332563EB)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EventRepeat,
                                contentDescription = null,
                                tint = AccentBlueBright,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Transaksi Berulang",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = "Otomatis berkurang saat tepat tanggalnya",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_recurring_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = TextMuted
                        )
                    }
                }

                // Summary Stats Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(ExpenseRedSoft),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                                        contentDescription = null,
                                        tint = ExpenseRed,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Rutin Keluar", fontSize = 11.sp, color = TextMuted)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = CurrencyHelper.formatRupiah(totalRecurringExpense),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = "per bulan",
                                fontSize = 10.sp,
                                color = TextSubtle
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(IncomeGreenSoft),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                        contentDescription = null,
                                        tint = IncomeGreen,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Rutin Masuk", fontSize = 11.sp, color = TextMuted)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = CurrencyHelper.formatRupiah(totalRecurringIncome),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = "per bulan",
                                fontSize = 10.sp,
                                color = TextSubtle
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Filter Tabs (Semua, Pengeluaran, Pemasukan)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Semua", "Pengeluaran", "Pemasukan").forEach { tab ->
                        val isSelected = filterType == tab
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) AccentBlue else DarkSurfaceElevated,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { filterType = tab }
                                .testTag("tab_filter_$tab")
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tab,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else TextMuted
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Recurring List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (filteredList.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.EventRepeat,
                                        contentDescription = null,
                                        tint = TextSubtle,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Belum ada transaksi berulang",
                                        color = TextMuted,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Tambahkan jadwal rutin bulanan Anda",
                                        color = TextSubtle,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    } else {
                        items(filteredList, key = { it.id }) { item ->
                            val isProcessedThisMonth = item.lastProcessedMonthYear == currentMonthYearKey
                            val isDueToday = item.dayOfMonth == todayDay
                            val isPastDue = item.dayOfMonth < todayDay

                            RecurringItemCard(
                                item = item,
                                isProcessedThisMonth = isProcessedThisMonth,
                                isDueToday = isDueToday,
                                isPastDue = isPastDue,
                                todayDay = todayDay,
                                onToggleActive = { viewModel.toggleRecurringActive(item.id) },
                                onProcessNow = { viewModel.processRecurringTransaction(item.id) },
                                onEdit = { editingItem = item },
                                onDelete = { itemToDelete = item }
                            )
                        }
                    }
                }

                // Bottom Add Button
                Surface(
                    color = DarkSurface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Button(
                            onClick = { showAddDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_add_recurring_transaction")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Tambah Transaksi Rutin Bulanan",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    // Add or Edit Dialog
    if (showAddDialog) {
        AddEditRecurringTransactionDialog(
            viewModel = viewModel,
            onDismiss = { showAddDialog = false }
        )
    }

    editingItem?.let { item ->
        AddEditRecurringTransactionDialog(
            viewModel = viewModel,
            initialItem = item,
            onDismiss = { editingItem = null }
        )
    }

    // Delete Confirmation Dialog
    itemToDelete?.let { item ->
        Dialog(onDismissRequest = { itemToDelete = null }) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = DarkSurfaceElevated,
                border = BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Hapus Transaksi Rutin?",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Apakah Anda yakin ingin menghapus jadwal transaksi rutin '${item.title}'?",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.Transparent,
                            modifier = Modifier.clickable { itemToDelete = null }
                        ) {
                            Text(
                                text = "Batal",
                                color = TextMuted,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.deleteRecurringTransaction(item.id)
                                itemToDelete = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Hapus", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecurringItemCard(
    item: RecurringTransaction,
    isProcessedThisMonth: Boolean,
    isDueToday: Boolean,
    isPastDue: Boolean,
    todayDay: Int,
    onToggleActive: () -> Unit,
    onProcessNow: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("recurring_item_${item.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(
            width = if (isDueToday && !isProcessedThisMonth) 1.5.dp else 1.dp,
            color = if (isDueToday && !isProcessedThisMonth) AccentBlueBright else DarkCardBorder
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Title & Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.category,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                        Text(
                            text = " · ${item.accountName}",
                            fontSize = 11.sp,
                            color = TextSubtle
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    val isIncome = item.type == TransactionType.INCOME
                    Text(
                        text = "${if (isIncome) "+" else "-"}${CurrencyHelper.formatRupiah(item.amount)}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isIncome) IncomeGreen else ExpenseRed
                    )
                    Text(
                        text = item.frequency.lowercase(),
                        fontSize = 10.sp,
                        color = TextSubtle
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Schedule info & Status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = AccentBlueBright,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        val freqText = when (item.frequency) {
                            "Setiap bulan" -> "Setiap tgl ${item.dayOfMonth}"
                            else -> item.frequency
                        }
                        Text(
                            text = freqText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AccentBlueBright
                        )
                    }
                    val dateSpan = buildString {
                        if (item.startDate.isNotBlank()) append("Mulai: ${item.startDate}")
                        if (item.endDate.isNotBlank()) append(" · Sampai: ${item.endDate}")
                        else if (item.startDate.isNotBlank()) append(" · Tanpa batas")
                    }
                    if (dateSpan.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = dateSpan,
                            fontSize = 10.sp,
                            color = TextSubtle
                        )
                    }
                }

                // Status chip
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when {
                        !item.isActive -> DarkSurfaceElevated
                        isProcessedThisMonth -> Color(0x2E10B981)
                        isDueToday -> Color(0x332563EB)
                        isPastDue -> Color(0x33EF4444)
                        else -> DarkSurfaceElevated
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isProcessedThisMonth) {
                            Icon(Icons.Default.Check, null, tint = IncomeGreen, modifier = Modifier.size(11.dp))
                            Spacer(Modifier.width(3.dp))
                        }
                        Text(
                            text = when {
                                !item.isActive -> "Nonaktif"
                                isProcessedThisMonth -> "Sudah diproses bulan ini"
                                isDueToday -> "Jatuh tempo hari ini!"
                                isPastDue -> "Lewat tgl ${item.dayOfMonth}"
                                else -> "Menunggu tgl ${item.dayOfMonth}"
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = when {
                                !item.isActive -> TextSubtle
                                isProcessedThisMonth -> IncomeGreen
                                isDueToday -> AccentBlueBright
                                isPastDue -> ExpenseRed
                                else -> TextMuted
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action row: Active Switch, Process Now, Edit, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = item.isActive,
                        onCheckedChange = { onToggleActive() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AccentBlue,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = DarkSurfaceElevated
                        ),
                        modifier = Modifier.testTag("switch_active_${item.id}")
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (item.isActive) "Aktif" else "Jeda",
                        fontSize = 12.sp,
                        color = if (item.isActive) TextWhite else TextSubtle
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // "Proses Sekarang" button if not yet processed this month
                    if (item.isActive && !isProcessedThisMonth) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0x2E2563EB),
                            modifier = Modifier
                                .clickable { onProcessNow() }
                                .testTag("btn_process_now_${item.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = AccentBlueBright,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Proses Sekarang",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentBlueBright
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    // Edit Button
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp).testTag("btn_edit_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Delete Button
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp).testTag("btn_delete_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = ExpenseRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRecurringTransactionDialog(
    viewModel: FinanceViewModel,
    initialItem: RecurringTransaction? = null,
    onDismiss: () -> Unit
) {
    val wallets by viewModel.wallets.collectAsState()
    val allCategories by viewModel.categories.collectAsState()

    var title by remember { mutableStateOf(initialItem?.title ?: "") }
    var amountText by remember {
        mutableStateOf(if (initialItem != null) initialItem.amount.toLong().toString() else "")
    }
    var selectedType by remember {
        mutableStateOf(initialItem?.type ?: TransactionType.EXPENSE)
    }
    var selectedCategory by remember {
        mutableStateOf(initialItem?.category ?: if (selectedType == TransactionType.INCOME) "Salary" else "Bills & Fees")
    }
    var selectedSubcategory by remember {
        mutableStateOf(initialItem?.subcategory ?: "")
    }
    val selectedWalletFilter by viewModel.selectedWalletFilter.collectAsState()
    val defaultWalletName = remember(wallets, initialItem, selectedWalletFilter) {
        if (initialItem != null) {
            initialItem.accountName
        } else if (selectedWalletFilter != "Semua" && selectedWalletFilter != "Semua Dompet" && selectedWalletFilter.isNotBlank()) {
            val filterMatch = wallets.firstOrNull { it.name.equals(selectedWalletFilter, ignoreCase = true) }?.name
            filterMatch ?: (wallets.firstOrNull()?.name ?: "BRI Utama")
        } else {
            wallets.firstOrNull()?.name ?: "BRI Utama"
        }
    }
    var selectedWallet by remember(defaultWalletName) {
        mutableStateOf(defaultWalletName)
    }
    var selectedDay by remember {
        mutableIntStateOf(initialItem?.dayOfMonth ?: 1)
    }
    var note by remember { mutableStateOf(initialItem?.note ?: "") }
    var autoDeduct by remember { mutableStateOf(initialItem?.autoDeduct ?: true) }

    val now = Calendar.getInstance()
    val todayD = now.get(Calendar.DAY_OF_MONTH)
    val todayM = now.get(Calendar.MONTH) + 1
    val todayY = now.get(Calendar.YEAR)
    val defaultStartDate = String.format(java.util.Locale.getDefault(), "%02d/%02d/%04d", todayD, todayM, todayY)
    val defaultEndDate = String.format(java.util.Locale.getDefault(), "%02d/%02d/%04d", todayD, todayM, todayY + 1)

    var frequency by remember {
        mutableStateOf(initialItem?.frequency ?: "Setiap bulan")
    }
    var startDateText by remember {
        mutableStateOf(initialItem?.startDate?.ifBlank { defaultStartDate } ?: defaultStartDate)
    }
    var hasEndDate by remember {
        mutableStateOf(!initialItem?.endDate.isNullOrBlank())
    }
    var endDateText by remember {
        mutableStateOf(initialItem?.endDate?.ifBlank { defaultEndDate } ?: defaultEndDate)
    }
    var showFrequencyDropdown by remember { mutableStateOf(false) }

    var showWalletDropdown by remember { mutableStateOf(false) }
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var showSubcategoryDropdown by remember { mutableStateOf(false) }
    var showDayDropdown by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val frequencyOptions = listOf(
        "Setiap hari",
        "Setiap 2 minggu",
        "Setiap bulan",
        "Setiap tahun"
    )

    val expenseCategories = listOf("Bills & Fees", "Market", "Food & Dining", "Transport", "Shopping", "Entertainment", "Other")
    val incomeCategories = listOf("Salary", "Extra Income", "Investasi", "Bonus", "Pemberian")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.88f)
                .testTag("dialog_add_edit_recurring"),
            shape = RoundedCornerShape(24.dp),
            color = DarkBackground,
            border = BorderStroke(1.dp, DarkCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initialItem == null) "Tambah Transaksi Rutin" else "Edit Transaksi Rutin",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Type Toggle (Pengeluaran / Pemasukan)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (selectedType == TransactionType.EXPENSE) ExpenseRedSoft else DarkSurfaceElevated,
                        border = BorderStroke(
                            1.dp,
                            if (selectedType == TransactionType.EXPENSE) ExpenseRed else DarkCardBorder
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                selectedType = TransactionType.EXPENSE
                                if (selectedCategory in incomeCategories) selectedCategory = "Bills & Fees"
                            }
                            .testTag("toggle_type_expense")
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Pengeluaran",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedType == TransactionType.EXPENSE) ExpenseRed else TextMuted
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (selectedType == TransactionType.INCOME) IncomeGreenSoft else DarkSurfaceElevated,
                        border = BorderStroke(
                            1.dp,
                            if (selectedType == TransactionType.INCOME) IncomeGreen else DarkCardBorder
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                selectedType = TransactionType.INCOME
                                if (selectedCategory in expenseCategories) selectedCategory = "Salary"
                            }
                            .testTag("toggle_type_income")
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Pemasukan",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedType == TransactionType.INCOME) IncomeGreen else TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Judul Input
                Text("Nama Transaksi Rutin", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Contoh: Langganan Netflix, Uang Kos, WiFi...", color = TextSubtle) },
                    modifier = Modifier.fillMaxWidth().testTag("input_recurring_title"),
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

                Spacer(modifier = Modifier.height(14.dp))

                // Nominal Input
                Text("Nominal (Rp)", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input ->
                        val digits = input.filter { it.isDigit() }
                        if (digits.length <= 11) amountText = digits
                    },
                    placeholder = { Text("0", color = TextSubtle) },
                    visualTransformation = ThousandsSeparatorVisualTransformation('.'),
                    modifier = Modifier.fillMaxWidth().testTag("input_recurring_amount"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

                Spacer(modifier = Modifier.height(14.dp))

                // 1. Pengulangan
                Text("Pengulangan", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Box {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceElevated,
                        border = BorderStroke(1.dp, DarkCardBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showFrequencyDropdown = true }
                            .testTag("dropdown_frequency_picker")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.EventRepeat, null, tint = AccentBlueBright, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = frequency,
                                    fontSize = 14.sp,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text("Ubah", fontSize = 12.sp, color = AccentBlueBright, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    DropdownMenu(
                        expanded = showFrequencyDropdown,
                        onDismissRequest = { showFrequencyDropdown = false },
                        modifier = Modifier.background(DarkSurfaceElevated)
                    ) {
                        frequencyOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = opt,
                                        color = if (frequency == opt) AccentBlue else TextWhite,
                                        fontWeight = if (frequency == opt) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    frequency = opt
                                    showFrequencyDropdown = false
                                }
                            )
                        }
                    }
                }

                // If "Setiap bulan", allow choosing specific day of month
                if (frequency == "Setiap bulan") {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurfaceElevated,
                            border = BorderStroke(1.dp, DarkCardBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDayDropdown = true }
                                .testTag("dropdown_day_picker")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Jatuh tempo: Tanggal $selectedDay setiap bulan",
                                    fontSize = 13.sp,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Medium
                                )
                                Icon(Icons.Default.CalendarToday, null, tint = AccentBlueBright, modifier = Modifier.size(15.dp))
                            }
                        }

                        DropdownMenu(
                            expanded = showDayDropdown,
                            onDismissRequest = { showDayDropdown = false },
                            modifier = Modifier
                                .background(DarkSurfaceElevated)
                                .height(260.dp)
                        ) {
                            (1..31).forEach { day ->
                                DropdownMenuItem(
                                    text = { Text("Tanggal $day setiap bulan", color = TextWhite) },
                                    onClick = {
                                        selectedDay = day
                                        showDayDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Tanggal Mulai
                Text("Tanggal Mulai", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = startDateText,
                    onValueChange = { startDateText = it },
                    placeholder = { Text("DD/MM/YYYY (Contoh: $defaultStartDate)", color = TextSubtle) },
                    leadingIcon = {
                        Icon(Icons.Default.CalendarToday, null, tint = AccentBlueBright, modifier = Modifier.size(16.dp))
                    },
                    modifier = Modifier.fillMaxWidth().testTag("input_start_date"),
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

                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DarkSurfaceElevated,
                        modifier = Modifier.clickable { startDateText = defaultStartDate }
                    ) {
                        Text("Hari Ini", fontSize = 11.sp, color = AccentBlueBright, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DarkSurfaceElevated,
                        modifier = Modifier.clickable {
                            startDateText = String.format(java.util.Locale.getDefault(), "01/%02d/%04d", todayM, todayY)
                        }
                    ) {
                        Text("Awal Bulan", fontSize = 11.sp, color = TextMuted, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DarkSurfaceElevated,
                        modifier = Modifier.clickable {
                            val nextM = if (todayM < 12) todayM + 1 else 1
                            val nextY = if (todayM < 12) todayY else todayY + 1
                            startDateText = String.format(java.util.Locale.getDefault(), "01/%02d/%04d", nextM, nextY)
                        }
                    ) {
                        Text("Bulan Depan", fontSize = 11.sp, color = TextMuted, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Tanggal Akhir
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DarkSurfaceElevated,
                    border = BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Batas Akhir Transaksi",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                                Text(
                                    text = if (hasEndDate) "Transaksi akan berhenti setelah tanggal akhir" else "Tanpa batas akhir (Berulang terus menerus)",
                                    fontSize = 11.sp,
                                    color = if (hasEndDate) AccentBlueBright else TextSubtle
                                )
                            }
                            Switch(
                                checked = hasEndDate,
                                onCheckedChange = { hasEndDate = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = AccentBlue
                                ),
                                modifier = Modifier.testTag("switch_has_end_date")
                            )
                        }

                        if (hasEndDate) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Tanggal Akhir", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = endDateText,
                                onValueChange = { endDateText = it },
                                placeholder = { Text("DD/MM/YYYY (Contoh: $defaultEndDate)", color = TextSubtle) },
                                leadingIcon = {
                                    Icon(Icons.Default.CalendarToday, null, tint = ExpenseRed, modifier = Modifier.size(16.dp))
                                },
                                modifier = Modifier.fillMaxWidth().testTag("input_end_date"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    focusedContainerColor = DarkBackground,
                                    unfocusedContainerColor = DarkBackground,
                                    focusedBorderColor = AccentBlue,
                                    unfocusedBorderColor = DarkCardBorder
                                ),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = DarkBackground,
                                    modifier = Modifier.clickable {
                                        val calM = Calendar.getInstance().apply { add(Calendar.MONTH, 1) }
                                        endDateText = String.format(java.util.Locale.getDefault(), "%02d/%02d/%04d", calM.get(Calendar.DAY_OF_MONTH), calM.get(Calendar.MONTH) + 1, calM.get(Calendar.YEAR))
                                    }
                                ) {
                                    Text("+1 Bulan", fontSize = 11.sp, color = AccentBlueBright, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = DarkBackground,
                                    modifier = Modifier.clickable {
                                        val calM = Calendar.getInstance().apply { add(Calendar.MONTH, 6) }
                                        endDateText = String.format(java.util.Locale.getDefault(), "%02d/%02d/%04d", calM.get(Calendar.DAY_OF_MONTH), calM.get(Calendar.MONTH) + 1, calM.get(Calendar.YEAR))
                                    }
                                ) {
                                    Text("+6 Bulan", fontSize = 11.sp, color = TextMuted, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = DarkBackground,
                                    modifier = Modifier.clickable {
                                        val calM = Calendar.getInstance().apply { add(Calendar.YEAR, 1) }
                                        endDateText = String.format(java.util.Locale.getDefault(), "%02d/%02d/%04d", calM.get(Calendar.DAY_OF_MONTH), calM.get(Calendar.MONTH) + 1, calM.get(Calendar.YEAR))
                                    }
                                ) {
                                    Text("+1 Tahun", fontSize = 11.sp, color = TextMuted, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Rekening / Dompet Dropdown
                Text("Rekening / Dompet", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Box {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceElevated,
                        border = BorderStroke(1.dp, DarkCardBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showWalletDropdown = true }
                            .testTag("dropdown_wallet_picker")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = selectedWallet, fontSize = 14.sp, color = TextWhite, fontWeight = FontWeight.Medium)
                            Text("Pilih", fontSize = 12.sp, color = AccentBlueBright)
                        }
                    }

                    DropdownMenu(
                        expanded = showWalletDropdown,
                        onDismissRequest = { showWalletDropdown = false },
                        modifier = Modifier.background(DarkSurfaceElevated)
                    ) {
                        wallets.forEach { wallet ->
                            DropdownMenuItem(
                                text = { Text(wallet.name, color = TextWhite) },
                                onClick = {
                                    selectedWallet = wallet.name
                                    showWalletDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Kategori Dropdown
                Text("Kategori", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Box {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceElevated,
                        border = BorderStroke(1.dp, DarkCardBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCategoryDropdown = true }
                            .testTag("dropdown_category_picker")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = selectedCategory, fontSize = 14.sp, color = TextWhite, fontWeight = FontWeight.Medium)
                            Text("Pilih", fontSize = 12.sp, color = AccentBlueBright)
                        }
                    }

                    val categories = if (selectedType == TransactionType.INCOME) incomeCategories else expenseCategories
                    DropdownMenu(
                        expanded = showCategoryDropdown,
                        onDismissRequest = { showCategoryDropdown = false },
                        modifier = Modifier.background(DarkSurfaceElevated)
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat, color = TextWhite) },
                                onClick = {
                                    selectedCategory = cat
                                    selectedSubcategory = ""
                                    showCategoryDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Subkategori Dropdown
                Text("Subkategori (Opsional)", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Box {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceElevated,
                        border = BorderStroke(1.dp, if (selectedSubcategory.isNotBlank()) AccentBlue else DarkCardBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showSubcategoryDropdown = true }
                            .testTag("dropdown_recurring_subcategory")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (selectedSubcategory.isNotBlank()) selectedSubcategory else "Pilih Subkategori",
                                fontSize = 14.sp,
                                color = if (selectedSubcategory.isNotBlank()) TextWhite else TextMuted,
                                fontWeight = if (selectedSubcategory.isNotBlank()) FontWeight.Medium else FontWeight.Normal
                            )
                            Text("Pilih", fontSize = 12.sp, color = AccentBlueBright)
                        }
                    }

                    val availableSubs = allCategories
                        .find { it.name.equals(selectedCategory, ignoreCase = true) }
                        ?.subcategories
                        ?.filter { it.isVisible }
                        ?.map { it.name }
                        ?: emptyList()

                    DropdownMenu(
                        expanded = showSubcategoryDropdown,
                        onDismissRequest = { showSubcategoryDropdown = false },
                        modifier = Modifier.background(DarkSurfaceElevated)
                    ) {
                        DropdownMenuItem(
                            text = { Text("(Tanpa Subkategori)", color = TextMuted) },
                            onClick = {
                                selectedSubcategory = ""
                                showSubcategoryDropdown = false
                            }
                        )
                        availableSubs.forEach { sub ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = sub,
                                        color = if (selectedSubcategory == sub) AccentBlue else TextWhite,
                                        fontWeight = if (selectedSubcategory == sub) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    selectedSubcategory = sub
                                    showSubcategoryDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Catatan
                Text("Catatan (Opsional)", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    placeholder = { Text("Keterangan tambahan...", color = TextSubtle) },
                    modifier = Modifier.fillMaxWidth().testTag("input_recurring_note"),
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

                Spacer(modifier = Modifier.height(16.dp))

                // Toggle Auto-deduct
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DarkSurfaceElevated,
                    border = BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Otomatis potong saldo",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = "Saldo otomatis berkurang tepat pada tanggal yang ditentukan",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                        Switch(
                            checked = autoDeduct,
                            onCheckedChange = { autoDeduct = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = AccentBlue
                            )
                        )
                    }
                }

                errorMessage?.let { msg ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = msg, color = ExpenseRed, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Save button
                Button(
                    onClick = {
                        val amount = amountText.toDoubleOrNull() ?: 0.0
                        if (title.isBlank()) {
                            errorMessage = "Nama transaksi tidak boleh kosong."
                            return@Button
                        }
                        if (amount <= 0.0) {
                            errorMessage = "Nominal harus lebih dari 0."
                            return@Button
                        }

                        val finalEndDate = if (hasEndDate) endDateText.trim() else ""
                        if (initialItem == null) {
                            val newItem = RecurringTransaction(
                                title = title.trim(),
                                amount = amount,
                                type = selectedType,
                                category = selectedCategory,
                                subcategory = selectedSubcategory,
                                accountName = selectedWallet,
                                dayOfMonth = selectedDay,
                                frequency = frequency,
                                startDate = startDateText.trim(),
                                endDate = finalEndDate,
                                note = note.trim(),
                                autoDeduct = autoDeduct,
                                isActive = true
                            )
                            viewModel.addRecurringTransaction(newItem)
                        } else {
                            val updatedItem = initialItem.copy(
                                title = title.trim(),
                                amount = amount,
                                type = selectedType,
                                category = selectedCategory,
                                subcategory = selectedSubcategory,
                                accountName = selectedWallet,
                                dayOfMonth = selectedDay,
                                frequency = frequency,
                                startDate = startDateText.trim(),
                                endDate = finalEndDate,
                                note = note.trim(),
                                autoDeduct = autoDeduct
                            )
                            viewModel.updateRecurringTransaction(updatedItem)
                        }
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_save_recurring")
                ) {
                    Text("Simpan Transaksi Rutin", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
