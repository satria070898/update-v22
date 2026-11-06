package com.example.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DebtItem
import com.example.data.model.DebtRecord
import com.example.data.model.DebtType
import com.example.ui.dialogs.CreateDebtDialog
import com.example.ui.dialogs.getBankBadge
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
import com.example.ui.viewmodel.FinanceViewModel
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.ui.dialogs.DeleteDebtConfirmDialog
import com.example.util.InstitutionLogoBadge
import com.example.util.CurrencyHelper
import com.example.util.ThousandsSeparatorVisualTransformation
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtDetailScreen(
    debtId: String,
    viewModel: FinanceViewModel,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    val debts by viewModel.debts.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val debt = debts.find { it.id == debtId }

    if (debt == null) {
        onBack()
        return
    }

    val recordIds = remember(debt.records) { debt.records.map { it.id }.toSet() }
    val linkedTransactions = remember(transactions, debt.id, debt.name, recordIds) {
        transactions.filter { tx ->
            tx.targetDebtId == debt.id ||
            (debt.name.isNotBlank() && tx.targetDebtName.equals(debt.name, ignoreCase = true)) ||
            tx.id == "debt_init_${debt.id}" ||
            (tx.id.startsWith("debt_rec_") && recordIds.contains(tx.id.removePrefix("debt_rec_"))) ||
            (tx.id.startsWith("tx_") && recordIds.contains(tx.id.removePrefix("tx_"))) ||
            recordIds.contains(tx.id)
        }
    }
    val linkedTxCount = linkedTransactions.size

    var showEditDebtDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showMarkPaidConfirmDialog by remember { mutableStateOf(false) }
    var showAddRecordSheet by remember { mutableStateOf(false) }
    var recordToEdit by remember { mutableStateOf<DebtRecord?>(null) }
    var recordToDelete by remember { mutableStateOf<DebtRecord?>(null) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding(),
        containerColor = DarkBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddRecordSheet = true },
                containerColor = AccentBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .testTag("fab_add_debt_record")
                    .size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Catatan",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Top Toolbar matching Gambar 2
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
                            modifier = Modifier.testTag("btn_debt_detail_back")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = TextWhite,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Detail Utang",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Mark as Paid button (checkmark in rounded green square)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = IncomeGreenSoft,
                            border = BorderStroke(1.dp, IncomeGreen.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { showMarkPaidConfirmDialog = true }
                                .testTag("btn_mark_debt_paid")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Tandai Lunas",
                                    tint = IncomeGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Edit button (blue square)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AccentBlue.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, AccentBlue.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { showEditDebtDialog = true }
                                .testTag("btn_edit_debt")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Utang",
                                    tint = AccentBlue,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Delete button (red square)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ExpenseRedSoft,
                            border = BorderStroke(1.dp, ExpenseRed.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { showDeleteConfirmDialog = true }
                                .testTag("btn_delete_debt")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Hapus Utang",
                                    tint = ExpenseRed,
                                    modifier = Modifier.size(17.dp)
                                )
                            }
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Card 1: Main Debt Summary matching Gambar 2
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.7f))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // Visual Icon with orange badge
                                    DebtVisualIcon(
                                        name = debt.name,
                                        categoryIcon = debt.categoryIcon,
                                        isBorrowed = debt.type == DebtType.BORROWED,
                                        size = 52.dp
                                    )

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column {
                                        Text(
                                            text = debt.name,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextWhite
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Tanggal Jatuh Tempo: ${debt.dueDate}",
                                            fontSize = 12.sp,
                                            color = TextSubtle
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        val isBorrowed = debt.type == DebtType.BORROWED
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isBorrowed) Color(0x2210B981) else Color(0x22FF5722),
                                            border = BorderStroke(1.dp, if (isBorrowed) Color(0xFF10B981).copy(alpha = 0.4f) else Color(0xFFFF5722).copy(alpha = 0.4f))
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = if (isBorrowed) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                                    contentDescription = null,
                                                    tint = if (isBorrowed) Color(0xFF34D399) else Color(0xFFFF7A50),
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = if (isBorrowed) "Uang Masuk • Dipinjam" else "Uang Keluar • Dipinjamkan",
                                                    fontSize = 10.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isBorrowed) Color(0xFF34D399) else Color(0xFFFF7A50)
                                                )
                                            }
                                        }
                                    }
                                }

                                // Status pill: e.g. "⏱ 6% lunas"
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF241C12),
                                    border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.4f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Schedule,
                                            contentDescription = null,
                                            tint = Color(0xFFF59E0B),
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${debt.progressPercent}% lunas",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFF59E0B)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Text(
                                text = CurrencyHelper.formatRupiah(debt.remainingAmount),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Utang Tersisa",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                // Card 2: Progress Card matching Gambar 2
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.7f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${CurrencyHelper.formatRupiah(debt.paidAmount)} / ${CurrencyHelper.formatRupiah(debt.totalAmount)}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextWhite
                                )
                                Text(
                                    text = "${debt.progressPercent}%",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF59E0B)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            val progressFloat = if (debt.totalAmount > 0) {
                                (debt.paidAmount / debt.totalAmount).toFloat().coerceIn(0f, 1f)
                            } else 0f

                            LinearProgressIndicator(
                                progress = { progressFloat },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = Color(0xFFF59E0B),
                                trackColor = DarkSurfaceElevated
                            )
                        }
                    }
                }

                // Card 3: Financial Details Breakdown matching Gambar 2
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.7f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Awalnya dipinjam
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = if (debt.type == DebtType.BORROWED) "Awalnya dipinjam" else "Awalnya dipinjamkan",
                                        fontSize = 13.sp,
                                        color = TextSubtle
                                    )
                                    Text(
                                        text = debt.startDate,
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }
                                Text(
                                    text = CurrencyHelper.formatRupiah(debt.totalAmount),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                            }

                            // Utang Tersisa
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Utang Tersisa",
                                    fontSize = 13.sp,
                                    color = TextSubtle
                                )
                                Text(
                                    text = CurrencyHelper.formatRupiah(debt.remainingAmount),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF59E0B)
                                )
                            }

                            // Dibayar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Dibayar",
                                    fontSize = 13.sp,
                                    color = TextSubtle
                                )
                                Text(
                                    text = CurrencyHelper.formatRupiah(debt.paidAmount),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = IncomeGreen
                                )
                            }

                            // Pembayaran Bulanan
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Pembayaran Bulanan",
                                    fontSize = 13.sp,
                                    color = TextSubtle
                                )
                                val monthlyAmt = if (debt.monthlyPayment > 0) debt.monthlyPayment else {
                                    if (debt.tenureMonths > 0) (debt.totalAmount / debt.tenureMonths) else 250000.0
                                }
                                Text(
                                    text = CurrencyHelper.formatRupiah(monthlyAmt),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                            }
                        }
                    }
                }

                // Card 4: Status Banner Card matching Gambar 2
                item {
                    val isBorrowed = debt.type == DebtType.BORROWED
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.7f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isBorrowed) Color(0xFF3B1E12) else IncomeGreenSoft,
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isBorrowed) Icons.AutoMirrored.Filled.CallReceived else Icons.Default.ArrowOutward,
                                        contentDescription = null,
                                        tint = if (isBorrowed) Color(0xFFFF6B4A) else IncomeGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = if (isBorrowed) "DIPINJAM" else "DIPINJAMKAN",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isBorrowed) Color(0xFFFF6B4A) else IncomeGreen
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isBorrowed) "Anda berutang ${CurrencyHelper.formatRupiah(debt.remainingAmount)}" else "Seseorang berutang kepada Anda ${CurrencyHelper.formatRupiah(debt.remainingAmount)}",
                                    fontSize = 13.sp,
                                    color = TextWhite
                                )
                            }
                        }
                    }
                }

                // Section 5: "Catatan Utang" Header & List matching Gambar 2
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Catatan Utang",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DarkSurfaceElevated
                        ) {
                            Text(
                                text = "${debt.records.size} entri",
                                fontSize = 11.sp,
                                color = TextMuted,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                if (debt.records.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            border = BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Belum ada catatan pembayaran",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSubtle
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Ketuk tombol + di bawah untuk mencatat pembayaran, pinjaman baru, atau bunga.",
                                    fontSize = 12.sp,
                                    color = TextMuted,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(debt.records) { record ->
                        DebtRecordCard(
                            record = record,
                            onEdit = { recordToEdit = record },
                            onDelete = { recordToDelete = record }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Modal Bottom Sheet: Tambah Catatan matching Gambar 4
    if (showAddRecordSheet) {
        AddDebtRecordBottomSheet(
            debt = debt,
            viewModel = viewModel,
            onDismiss = { showAddRecordSheet = false }
        )
    }

    // Edit Debt Dialog matching Gambar 3
    if (showEditDebtDialog) {
        CreateDebtDialog(
            existingDebt = debt,
            viewModel = viewModel,
            onDismiss = { showEditDebtDialog = false }
        )
    }

    // Edit Record Bottom Sheet (matching Gambar)
    recordToEdit?.let { rec ->
        EditDebtRecordBottomSheet(
            record = rec,
            debt = debt,
            viewModel = viewModel,
            onDismiss = { recordToEdit = null }
        )
    }

    // Delete Record Confirmation
    recordToDelete?.let { rec ->
        AlertDialog(
            onDismissRequest = { recordToDelete = null },
            containerColor = DarkSurface,
            title = { Text("Hapus Catatan?", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Catatan pembayaran sebesar ${CurrencyHelper.formatRupiah(rec.amount)} akan dihapus.",
                    color = TextSubtle,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteDebtRecord(debt.id, rec.id)
                        recordToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
                ) {
                    Text("Hapus", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { recordToDelete = null }) {
                    Text("Batal", color = TextMuted)
                }
            }
        )
    }

    // Mark Paid Confirmation Dialog
    if (showMarkPaidConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showMarkPaidConfirmDialog = false },
            containerColor = DarkSurface,
            title = { Text("Tandai Lunas?", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Apakah Anda yakin ingin menandai utang \"${debt.name}\" sebagai lunas sepenuhnya?",
                    color = TextSubtle,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.markDebtFullyPaid(debt.id)
                        showMarkPaidConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IncomeGreen)
                ) {
                    Text("Tandai Lunas", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showMarkPaidConfirmDialog = false }) {
                    Text("Batal", color = TextMuted)
                }
            }
        )
    }

    // Delete Debt Confirmation Dialog matching user screenshot
    if (showDeleteConfirmDialog) {
        DeleteDebtConfirmDialog(
            debt = debt,
            linkedTxCount = linkedTxCount,
            onConfirm = { deleteLinkedTransactions ->
                viewModel.deleteDebt(debt.id, deleteLinkedTransactions = deleteLinkedTransactions)
                showDeleteConfirmDialog = false
                onBack()
            },
            onDismiss = { showDeleteConfirmDialog = false }
        )
    }
}

@Composable
fun DebtRecordCard(
    record: DebtRecord,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.7f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Circular icon: green with down arrow for payment
                val isPayment = record.type == "Pembayaran"
                val isBorrowMore = record.type == "Pinjam Lagi"
                val iconColor = when {
                    isPayment -> IncomeGreen
                    isBorrowMore -> Color(0xFFFF6B4A)
                    else -> Color(0xFFF59E0B)
                }
                val bgColor = when {
                    isPayment -> IncomeGreenSoft
                    isBorrowMore -> Color(0xFF3B1E12)
                    else -> Color(0xFF2E2412)
                }

                Surface(
                    shape = CircleShape,
                    color = bgColor,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = when {
                                isPayment -> Icons.Default.ArrowDownward
                                isBorrowMore -> Icons.Default.ArrowUpward
                                else -> Icons.Default.Percent
                            },
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    val nominalPrefix = if (isPayment) "-" else "+"
                    Text(
                        text = "$nominalPrefix${CurrencyHelper.formatRupiah(record.amount)}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = iconColor
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${record.date} · ${record.time}",
                        fontSize = 11.sp,
                        color = TextSubtle
                    )
                    if (record.note.isNotBlank() || record.sourceWalletName.isNotBlank()) {
                        val sub = buildString {
                            if (record.note.isNotBlank()) append(record.note)
                            if (record.sourceWalletName.isNotBlank()) {
                                if (isNotEmpty()) append(" · ")
                                append(record.sourceWalletName)
                            }
                        }
                        Text(
                            text = sub,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Catatan",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus Catatan",
                        tint = ExpenseRed,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDebtRecordBottomSheet(
    debt: DebtItem,
    viewModel: FinanceViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val wallets by viewModel.wallets.collectAsState()

    var recordType by remember { mutableStateOf("Pembayaran") } // "Pembayaran", "Pinjam Lagi", "Bunga"
    var amountText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }
    var selectedWalletName by remember { mutableStateOf(wallets.firstOrNull()?.name ?: "") }

    val cal = Calendar.getInstance()
    var selectedDateText by remember { mutableStateOf("2 Nov 2026") }
    var selectedTimeText by remember { mutableStateOf("12.56") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .navigationBarsPadding()
        ) {
            // Header matching Gambar 4
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = AccentBlue.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, AccentBlue.copy(alpha = 0.4f)),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                                contentDescription = null,
                                tint = AccentBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Tambah Catatan",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = debt.name,
                            fontSize = 12.sp,
                            color = TextSubtle
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

            Spacer(modifier = Modifier.height(20.dp))

            // Jenis Catatan matching Gambar 4 (Pembayaran, Pinjam Lagi, Bunga)
            Text(
                text = "Jenis Catatan",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSubtle
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple("Pembayaran", Icons.AutoMirrored.Filled.TrendingDown, IncomeGreen),
                    Triple("Pinjam Lagi", Icons.AutoMirrored.Filled.TrendingUp, Color(0xFFFF6B4A)),
                    Triple("Bunga", Icons.Default.Percent, Color(0xFFF59E0B))
                ).forEach { (type, icon, activeColor) ->
                    val isSelected = recordType == type
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) activeColor.copy(alpha = 0.15f) else DarkSurfaceElevated,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) activeColor else DarkCardBorder
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { recordType = type }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) activeColor else TextMuted,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = type,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextMuted,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Jumlah matching Gambar 4
            Text(
                text = "Jumlah",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSubtle
            )
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = DarkSurfaceElevated,
                border = BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rp",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                        placeholder = { Text("Masukkan jumlah", color = TextMuted, fontSize = 15.sp) },
                        visualTransformation = ThousandsSeparatorVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Pilih tanggal matching Gambar 4
            Text(
                text = "Pilih tanggal",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSubtle
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Date picker
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurfaceElevated,
                    border = BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier
                        .weight(1.4f)
                        .clickable {
                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")
                                    selectedDateText = "$day ${monthNames.getOrElse(month) { "Nov" }} $year"
                                },
                                2026, 10, 2
                            ).show()
                        }
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = selectedDateText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextWhite
                        )
                    }
                }

                // Time picker
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurfaceElevated,
                    border = BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier
                        .weight(0.9f)
                        .clickable {
                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    selectedTimeText = String.format("%02d.%02d", hour, minute)
                                },
                                12, 56, true
                            ).show()
                        }
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = selectedTimeText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextWhite
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Pembayaran dari (Pilihan Akun Dompet)
            val isLentDebt = debt.type == DebtType.LENT
            val walletHeaderTitle = when {
                recordType == "Pembayaran" && isLentDebt -> "Masuk ke saldo akun (Dompet penerima)"
                recordType == "Pembayaran" && !isLentDebt -> "Pembayaran dari saldo akun (Dompet pengirim)"
                recordType == "Pinjam Lagi" && isLentDebt -> "Diambil dari saldo akun (Dompet pengirim)"
                recordType == "Pinjam Lagi" && !isLentDebt -> "Masuk ke saldo akun (Dompet penerima)"
                else -> "Akun Dompet Terkait"
            }
            Text(
                text = walletHeaderTitle,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSubtle
            )

            if (selectedWalletName.isNotBlank()) {
                val isIncomeToWallet = (recordType == "Pembayaran" && isLentDebt) || (recordType == "Pinjam Lagi" && !isLentDebt)
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isIncomeToWallet) IncomeGreenSoft else ExpenseRedSoft,
                    border = BorderStroke(1.dp, if (isIncomeToWallet) IncomeGreen.copy(alpha = 0.4f) else ExpenseRed.copy(alpha = 0.4f)),
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isIncomeToWallet) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = if (isIncomeToWallet) IncomeGreen else ExpenseRed,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isIncomeToWallet) {
                                "Saldo $selectedWalletName akan BERTAMBAH (+)"
                            } else {
                                "Saldo $selectedWalletName akan BERKURANG (-)"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isIncomeToWallet) IncomeGreen else ExpenseRed
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Opsi "Tanpa Akun"
                val isNoWallet = selectedWalletName.isEmpty()
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isNoWallet) AccentBlue.copy(alpha = 0.2f) else DarkSurfaceElevated,
                    border = BorderStroke(1.dp, if (isNoWallet) AccentBlue else DarkCardBorder),
                    modifier = Modifier.clickable { selectedWalletName = "" }
                ) {
                    Text(
                        text = "Tanpa Akun",
                        fontSize = 12.sp,
                        fontWeight = if (isNoWallet) FontWeight.Bold else FontWeight.Normal,
                        color = if (isNoWallet) TextWhite else TextMuted,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp)
                    )
                }

                wallets.forEach { w ->
                    val isSelected = selectedWalletName.equals(w.name, ignoreCase = true)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) AccentBlue.copy(alpha = 0.2f) else DarkSurfaceElevated,
                        border = BorderStroke(1.dp, if (isSelected) AccentBlue else DarkCardBorder),
                        modifier = Modifier.clickable { selectedWalletName = w.name }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = AccentBlue
                            ) {
                                Text(
                                    text = getBankBadge(w.name),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = w.name,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) TextWhite else TextSubtle
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Catatan (Opsional) matching Gambar 4
            Text(
                text = "Catatan (Opsional)",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSubtle
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                placeholder = { Text("Tambahkan catatan...", color = TextMuted, fontSize = 13.sp) },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = DarkCardBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedContainerColor = DarkSurfaceElevated,
                    unfocusedContainerColor = DarkSurfaceElevated
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(22.dp))

            // Button Tambah Catatan matching Gambar 4
            Button(
                onClick = {
                    val rawAmt = amountText.toDoubleOrNull() ?: 0.0
                    if (rawAmt > 0) {
                        viewModel.addDebtRecord(
                            debtId = debt.id,
                            record = DebtRecord(
                                debtId = debt.id,
                                type = recordType,
                                amount = rawAmt,
                                date = selectedDateText,
                                time = selectedTimeText,
                                sourceWalletName = selectedWalletName,
                                note = noteText
                            ),
                            affectWalletName = selectedWalletName.ifBlank { null }
                        )
                        onDismiss()
                    }
                },
                enabled = (amountText.toDoubleOrNull() ?: 0.0) > 0,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentBlue,
                    disabledContainerColor = AccentBlue.copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_confirm_add_debt_record")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Tambah Catatan",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDebtRecordBottomSheet(
    record: DebtRecord,
    debt: DebtItem,
    viewModel: FinanceViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val wallets by viewModel.wallets.collectAsState()

    var recordType by remember { mutableStateOf(if (record.type == "Pinjam Lagi") "Pinjam Lagi" else "Pembayaran") }
    var amountText by remember { mutableStateOf(record.amount.toLong().toString()) }
    var noteText by remember { mutableStateOf(record.note) }
    var selectedWalletName by remember { mutableStateOf(record.sourceWalletName) }
    var selectedDateText by remember { mutableStateOf(record.date.ifBlank { "12 Okt 2026" }) }
    var selectedTimeText by remember { mutableStateOf(record.time.ifBlank { "20.41" }) }

    val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")
    val cal = Calendar.getInstance()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .navigationBarsPadding()
        ) {
            // Header matching Gambar: Rounded blue square with pencil icon + "Edit Catatan" + subtitle + Close 'X'
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0x332563EB),
                        border = BorderStroke(1.dp, Color(0x552563EB)),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = AccentBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Edit Catatan",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = debt.name,
                            fontSize = 12.sp,
                            color = TextSubtle
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

            Spacer(modifier = Modifier.height(18.dp))

            // Jenis Catatan matching Gambar
            Text(
                text = "Jenis Catatan",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSubtle
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Button 1: Pembayaran
                val isPayment = recordType == "Pembayaran"
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isPayment) IncomeGreen.copy(alpha = 0.12f) else DarkSurfaceElevated,
                    border = BorderStroke(1.5.dp, if (isPayment) IncomeGreen else DarkCardBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { recordType = "Pembayaran" }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = if (isPayment) IncomeGreen else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pembayaran",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPayment) IncomeGreen else TextMuted
                        )
                    }
                }

                // Button 2: Pinjam Lagi
                val isBorrowMore = recordType == "Pinjam Lagi"
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isBorrowMore) Color(0xFF3B1E12) else DarkSurfaceElevated,
                    border = BorderStroke(1.5.dp, if (isBorrowMore) Color(0xFFFF6B4A) else DarkCardBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { recordType = "Pinjam Lagi" }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                            contentDescription = null,
                            tint = if (isBorrowMore) Color(0xFFFF6B4A) else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Pinjam Lagi",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isBorrowMore) Color(0xFFFF6B4A) else TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Jumlah matching Gambar
            Text(
                text = "Jumlah",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSubtle
            )
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(14.dp),
                color = DarkSurfaceElevated,
                border = BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rp",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                        placeholder = { Text("0", color = TextMuted, fontSize = 15.sp) },
                        visualTransformation = ThousandsSeparatorVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Pilih tanggal matching Gambar
            Text(
                text = "Pilih tanggal",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSubtle
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Date picker box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurfaceElevated,
                    border = BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier
                        .weight(1.4f)
                        .clickable {
                            val parts = selectedDateText.split(" ").filter { it.isNotBlank() }
                            val initDay = parts.getOrNull(0)?.filter { it.isDigit() }?.toIntOrNull() ?: cal.get(Calendar.DAY_OF_MONTH)
                            val mIdx = monthNames.indexOfFirst { it.equals(parts.getOrNull(1), ignoreCase = true) }
                            val initMonth = if (mIdx >= 0) mIdx else cal.get(Calendar.MONTH)
                            val initYear = parts.getOrNull(2)?.filter { it.isDigit() }?.toIntOrNull() ?: cal.get(Calendar.YEAR)

                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    selectedDateText = "$day ${monthNames.getOrElse(month) { "Okt" }} $year"
                                },
                                initYear, initMonth, initDay
                            ).show()
                        }
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = selectedDateText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextWhite
                        )
                    }
                }

                // Time picker box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurfaceElevated,
                    border = BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier
                        .weight(0.9f)
                        .clickable {
                            val timeParts = selectedTimeText.replace(':', '.').split('.').filter { it.isNotBlank() }
                            val initHour = timeParts.getOrNull(0)?.toIntOrNull() ?: cal.get(Calendar.HOUR_OF_DAY)
                            val initMin = timeParts.getOrNull(1)?.toIntOrNull() ?: cal.get(Calendar.MINUTE)

                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    selectedTimeText = String.format(java.util.Locale.getDefault(), "%02d.%02d", hour, minute)
                                },
                                initHour, initMin, true
                            ).show()
                        }
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = selectedTimeText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextWhite
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Pembayaran dari / Masuk ke akun
            val isLentDebt = debt.type == DebtType.LENT
            val walletHeaderTitle = when {
                recordType == "Pembayaran" && isLentDebt -> "Masuk ke saldo akun (Dompet penerima)"
                recordType == "Pembayaran" && !isLentDebt -> "Pembayaran dari saldo akun (Dompet pengirim)"
                recordType == "Pinjam Lagi" && isLentDebt -> "Diambil dari saldo akun (Dompet pengirim)"
                recordType == "Pinjam Lagi" && !isLentDebt -> "Masuk ke saldo akun (Dompet penerima)"
                else -> "Akun Dompet Terkait"
            }
            Text(
                text = walletHeaderTitle,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSubtle
            )

            if (selectedWalletName.isNotBlank()) {
                val isIncomeToWallet = (recordType == "Pembayaran" && isLentDebt) || (recordType == "Pinjam Lagi" && !isLentDebt)
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isIncomeToWallet) IncomeGreenSoft else ExpenseRedSoft,
                    border = BorderStroke(1.dp, if (isIncomeToWallet) IncomeGreen.copy(alpha = 0.4f) else ExpenseRed.copy(alpha = 0.4f)),
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isIncomeToWallet) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = if (isIncomeToWallet) IncomeGreen else ExpenseRed,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isIncomeToWallet) {
                                "Saldo $selectedWalletName akan BERTAMBAH (+)"
                            } else {
                                "Saldo $selectedWalletName akan BERKURANG (-)"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isIncomeToWallet) IncomeGreen else ExpenseRed
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                wallets.forEach { w ->
                    val isSelected = selectedWalletName.equals(w.name, ignoreCase = true)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) AccentBlue.copy(alpha = 0.25f) else DarkSurfaceElevated,
                        border = BorderStroke(1.dp, if (isSelected) AccentBlue else DarkCardBorder),
                        modifier = Modifier.clickable { selectedWalletName = w.name }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            InstitutionLogoBadge(
                                walletName = w.name,
                                iconName = w.iconName,
                                size = 20.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = w.name,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) TextWhite else TextSubtle
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Catatan (Opsional) matching Gambar
            Text(
                text = "Catatan (Opsional)",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSubtle
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                placeholder = { Text("Tambahkan catatan...", color = TextMuted, fontSize = 13.sp) },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = DarkCardBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedContainerColor = DarkSurfaceElevated,
                    unfocusedContainerColor = DarkSurfaceElevated
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(22.dp))

            // Perbarui Catatan Button matching Gambar
            Button(
                onClick = {
                    val rawAmt = amountText.toDoubleOrNull() ?: record.amount
                    if (rawAmt > 0) {
                        viewModel.updateDebtRecord(
                            debtId = debt.id,
                            record = record.copy(
                                type = recordType,
                                amount = rawAmt,
                                date = selectedDateText,
                                time = selectedTimeText,
                                sourceWalletName = selectedWalletName,
                                note = noteText
                            )
                        )
                        onDismiss()
                    }
                },
                enabled = (amountText.toDoubleOrNull() ?: 0.0) > 0,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentBlue,
                    disabledContainerColor = AccentBlue.copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_confirm_update_debt_record")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = com.example.R.drawable.ic_save_disk),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Perbarui Catatan",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun DebtVisualIcon(
    name: String,
    categoryIcon: String,
    isBorrowed: Boolean,
    size: androidx.compose.ui.unit.Dp = 48.dp
) {
    val (baseColor, iconVec) = when {
        categoryIcon.equals("CreditCard", ignoreCase = true) || name.contains("allegra", ignoreCase = true) ->
            Color(0xFF0284C7) to Icons.Default.CreditCard
        categoryIcon.equals("Bank", ignoreCase = true) || name.contains("bbri", ignoreCase = true) || name.contains("bank", ignoreCase = true) ->
            Color(0xFF0F766E) to Icons.Default.CreditCard
        categoryIcon.equals("Car", ignoreCase = true) || categoryIcon.equals("Mobil", ignoreCase = true) ->
            Color(0xFF16A34A) to Icons.Default.CreditCard
        else ->
            Color(0xFF0284C7) to Icons.Default.CreditCard
    }

    Box(modifier = Modifier.size(size)) {
        // Main container
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = baseColor.copy(alpha = 0.22f),
            border = BorderStroke(1.dp, baseColor.copy(alpha = 0.45f)),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = iconVec,
                    contentDescription = null,
                    tint = baseColor,
                    modifier = Modifier.size(size * 0.5f)
                )
            }
        }

        // Circular badge top right with downward orange arrow (matching Gambar 1 & 2)
        Surface(
            shape = CircleShape,
            color = if (isBorrowed) Color(0xFFFF6B4A) else IncomeGreen,
            modifier = Modifier
                .size(size * 0.36f)
                .align(Alignment.TopEnd)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (isBorrowed) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(size * 0.22f)
                )
            }
        }
    }
}
