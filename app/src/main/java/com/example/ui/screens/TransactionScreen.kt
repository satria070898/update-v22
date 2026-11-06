package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
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
import com.example.ui.components.AnggaranTwoToneIcon
import com.example.ui.components.PerencanaTwoToneIcon
import com.example.ui.components.AcaraTwoToneIcon
import com.example.ui.components.AsetTwoToneIcon
import com.example.ui.components.TabunganTwoToneIcon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.EventRepeat
import com.example.data.model.Transaction
import com.example.data.model.TransactionType
import com.example.ui.dialogs.BatchLabelDialog
import com.example.ui.dialogs.CategoryPickerSubDialog
import com.example.ui.dialogs.ConfirmDeleteTransactionsDialog
import com.example.ui.dialogs.EditTransactionDialog
import com.example.ui.dialogs.MonthYearPickerDialog
import com.example.ui.dialogs.RecurringTransactionDialog
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentBlueBright
import com.example.ui.theme.AppThemeState
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardSubtle
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.getBalanceCardGradient
import com.example.ui.theme.getTopHeaderGradient
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ExpenseRedSoft
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.IncomeGreenSoft
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TextWhite
import com.example.ui.theme.TrophyYellow
import com.example.ui.viewmodel.FinanceViewModel
import com.example.util.CategoryIconHelper
import com.example.util.CurrencyHelper
import com.example.util.AssetLogoBadge
import com.example.util.InstitutionLogoBadge
import com.example.util.TransactionMathHelper
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    viewModel: FinanceViewModel,
    onQuickActionClick: (String) -> Unit,
    onTransactionClick: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    val transactions by viewModel.transactions.collectAsState()
    val wallets by viewModel.wallets.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val availableLabels by viewModel.labels.collectAsState()
    val selectedWallet by viewModel.selectedWalletFilter.collectAsState()
    val selectedMonth by viewModel.selectedMonthIndex.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()
    val monthOptions by viewModel.monthOptions.collectAsState()
    val isHidden by viewModel.isBalanceHidden.collectAsState()
    val totalBalance by viewModel.totalWalletBalance.collectAsState()
    val monthlyIncome by viewModel.monthlyIncome.collectAsState()
    val monthlyExpense by viewModel.monthlyExpense.collectAsState()
    val monthlyTransfer by viewModel.monthlyTransfer.collectAsState()
    val isSearchOpen by viewModel.isSearchOpen.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val recurringTransactions by viewModel.recurringTransactions.collectAsState()

    var showWalletMenu by remember { mutableStateOf(false) }
    var showMonthYearPicker by remember { mutableStateOf(false) }
    var showRecurringDialog by remember { mutableStateOf(false) }
    var selectedTypeFilter by remember { mutableStateOf("Semua") }

    // Multi-select & Edit States
    var isSelectionMode by remember { mutableStateOf(false) }
    var selectedIds by remember { mutableStateOf(setOf<String>()) }
    var editingTransaction by remember { mutableStateOf<Transaction?>(null) }

    var showBatchCategoryDialog by remember { mutableStateOf(false) }
    var showBatchLabelDialog by remember { mutableStateOf(false) }
    var showBatchDeleteConfirm by remember { mutableStateOf(false) }

    // Intercept back button when in multi-select mode
    BackHandler(enabled = isSelectionMode) {
        isSelectionMode = false
        selectedIds = emptySet()
    }

    val filteredByTypeTransactions = remember(transactions, selectedTypeFilter) {
        when (selectedTypeFilter) {
            "Pengeluaran" -> transactions.filter { it.type == TransactionType.EXPENSE }
            "Pemasukan" -> transactions.filter { it.type == TransactionType.INCOME }
            "Transfer" -> transactions.filter { it.type == TransactionType.TRANSFER }
            else -> transactions
        }
    }

    // Group transactions by day of month, and sort by time descending within each day
    val groupedTransactions = remember(filteredByTypeTransactions) {
        filteredByTypeTransactions
            .groupBy { it.dayOfMonth }
            .toSortedMap(compareByDescending { it })
            .mapValues { (_, txList) ->
                txList.sortedWith { a, b ->
                    val timeA = TransactionMathHelper.parseTimeToSeconds(a.time)
                    val timeB = TransactionMathHelper.parseTimeToSeconds(b.time)
                    val cmp = timeB.compareTo(timeA)
                    if (cmp != 0) cmp else b.id.compareTo(a.id)
                }
            }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(getTopHeaderGradient(AccentBlue))
    ) {
        // Top Header or Multi-Select Contextual Header with status bar padding for edge-to-edge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            if (isSelectionMode) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurfaceElevated)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            isSelectionMode = false
                            selectedIds = emptySet()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Batal",
                                tint = TextWhite
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${selectedIds.size} dipilih",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val allIds = transactions.map { it.id }.toSet()
                        val isAllSelected = selectedIds.size == allIds.size && allIds.isNotEmpty()

                        // Select All / Deselect All
                        IconButton(
                            onClick = {
                                selectedIds = if (isAllSelected) emptySet() else allIds
                                if (selectedIds.isEmpty()) isSelectionMode = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.SelectAll,
                                contentDescription = if (isAllSelected) "Batal Semua" else "Pilih Semua",
                                tint = if (isAllSelected) AccentBlueBright else TextWhite,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Change Category
                        IconButton(
                            onClick = { if (selectedIds.isNotEmpty()) showBatchCategoryDialog = true },
                            enabled = selectedIds.isNotEmpty()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Category,
                                contentDescription = "Ubah Kategori",
                                tint = if (selectedIds.isNotEmpty()) TextWhite else TextSubtle,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Change Label
                        IconButton(
                            onClick = { if (selectedIds.isNotEmpty()) showBatchLabelDialog = true },
                            enabled = selectedIds.isNotEmpty()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Label,
                                contentDescription = "Ubah Label",
                                tint = if (selectedIds.isNotEmpty()) TextWhite else TextSubtle,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Delete
                        IconButton(
                            onClick = { if (selectedIds.isNotEmpty()) showBatchDeleteConfirm = true },
                            enabled = selectedIds.isNotEmpty()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Hapus",
                                tint = if (selectedIds.isNotEmpty()) ExpenseRed else TextSubtle,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                // Contextual action strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurface)
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceElevated,
                        border = BorderStroke(1.dp, DarkCardBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clickable(enabled = selectedIds.isNotEmpty()) {
                                showBatchCategoryDialog = true
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Category, null, tint = AccentBlueBright, modifier = Modifier.size(15.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Kategori", fontSize = 12.sp, color = TextWhite, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceElevated,
                        border = BorderStroke(1.dp, DarkCardBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clickable(enabled = selectedIds.isNotEmpty()) {
                                showBatchLabelDialog = true
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Label, null, tint = TrophyYellow, modifier = Modifier.size(15.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Label", fontSize = 12.sp, color = TextWhite, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = ExpenseRedSoft,
                        modifier = Modifier
                            .weight(1f)
                            .clickable(enabled = selectedIds.isNotEmpty()) {
                                showBatchDeleteConfirm = true
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Delete, null, tint = ExpenseRed, modifier = Modifier.size(15.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Hapus", fontSize = 12.sp, color = ExpenseRed, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // Top Header: "Transaksi" on left corner, actions and account dropdown on right corner (bell removed)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Tulisan "Transaksi" di pojok kiri atas
                Text(
                    text = "Transaksi",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    modifier = Modifier.testTag("title_transaksi")
                )

                // Pojok sebelah kanan: Alat Kalkulator, Search, Fitur Transaksi Berulang, dan Dropdown Pilih Akun
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Fitur Alat & Kalkulator Finansial
                    IconButton(
                        onClick = { onQuickActionClick("Alat") },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_financial_tools")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = "Alat & Kalkulator Finansial",
                            tint = TextWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Search Icon with active filter indicator
                    IconButton(
                        onClick = { viewModel.toggleSearch() },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_search_toggle")
                    ) {
                        val isFilterActive = selectedTypeFilter != "Semua" || searchQuery.isNotBlank()
                        BadgedBox(
                            badge = {
                                if (isFilterActive && !isSearchOpen) {
                                    Badge(
                                        containerColor = AccentBlue,
                                        modifier = Modifier.size(7.dp)
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Cari dan Filter",
                                tint = if (isSearchOpen || isFilterActive) AccentBlueBright else TextWhite,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Fitur Transaksi Berulang (Recurring Transactions)
                    IconButton(
                        onClick = { showRecurringDialog = true },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_recurring_transactions")
                    ) {
                        val activeCount = recurringTransactions.count { it.isActive }
                        BadgedBox(
                            badge = {
                                if (activeCount > 0) {
                                    Badge(
                                        containerColor = AccentBlue,
                                        contentColor = Color.White
                                    ) { Text("$activeCount", fontSize = 9.sp) }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.EventRepeat,
                                contentDescription = "Fitur Transaksi Berulang",
                                tint = AccentBlueBright,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Dropdown pilih akun di pojok sebelah kanan
                    Box {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = DarkSurfaceElevated,
                            modifier = Modifier
                                .clickable { showWalletMenu = true }
                                .testTag("wallet_selector")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (selectedWallet == "Semua") {
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(AccentBlue),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "ALL",
                                            fontSize = 7.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                } else {
                                    val currentWalletIcon = wallets.find { it.name.equals(selectedWallet, ignoreCase = true) }?.iconName ?: ""
                                    InstitutionLogoBadge(
                                        walletName = selectedWallet,
                                        iconName = currentWalletIcon,
                                        size = 18.dp
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = selectedWallet,
                                    fontSize = 12.sp,
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
                            expanded = showWalletMenu,
                            onDismissRequest = { showWalletMenu = false },
                            modifier = Modifier.background(DarkSurfaceElevated)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Semua Dompet", color = TextWhite, fontSize = 13.sp) },
                                onClick = {
                                    showWalletMenu = false
                                    viewModel.selectWalletFilter("Semua")
                                }
                            )
                            wallets.forEach { wallet ->
                                DropdownMenuItem(
                                    leadingIcon = {
                                        InstitutionLogoBadge(
                                            walletName = wallet.name,
                                            iconName = wallet.iconName,
                                            size = 20.dp
                                        )
                                    },
                                    text = { Text(wallet.name, color = TextWhite, fontSize = 13.sp) },
                                    onClick = {
                                        showWalletMenu = false
                                        viewModel.selectWalletFilter(wallet.name)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Search Bar & Filter (Semua, Pengeluaran, Pemasukan, Transfer)
    AnimatedVisibility(visible = isSearchOpen) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Cari catatan, kategori, akun...", color = TextSubtle, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = AccentBlueBright,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Hapus",
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedContainerColor = DarkSurfaceElevated,
                    unfocusedContainerColor = DarkSurfaceElevated,
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = DarkCardBorder
                ),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Filter Type Chips moved inside search button / search area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val expenseCount = transactions.count { it.type == TransactionType.EXPENSE }
                val incomeCount = transactions.count { it.type == TransactionType.INCOME }
                val transferCount = transactions.count { it.type == TransactionType.TRANSFER }

                listOf(
                    Triple("Semua", transactions.size, AccentBlue),
                    Triple("Pengeluaran", expenseCount, ExpenseRed),
                    Triple("Pemasukan", incomeCount, IncomeGreen),
                    Triple("Transfer", transferCount, Color(0xFF0284C7))
                ).forEach { (label, count, activeColor) ->
                    val isSelected = selectedTypeFilter == label
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) activeColor else DarkSurfaceElevated,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) activeColor.copy(alpha = 0.8f) else DarkCardBorder.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedTypeFilter = label }
                            .testTag("filter_$label")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 7.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (count > 0 && label != "Semua") "$label ($count)" else label,
                                fontSize = 10.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextMuted
                            )
                        }
                    }
                }
            }
        }
    }

        // Month Selector: Fixed Calendar Button on the left + Horizontally scrollable Month Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Fixed Calendar Button
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurfaceElevated,
                modifier = Modifier
                    .padding(start = 16.dp, end = 8.dp)
                    .clickable { showMonthYearPicker = true }
                    .testTag("btn_calendar_month_picker")
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

            // Scrollable Month Chips (1 year back ending on current month)
            val monthScrollState = rememberScrollState()

            LaunchedEffect(monthOptions) {
                monthScrollState.scrollTo(monthScrollState.maxValue)
            }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(monthScrollState)
                    .padding(end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                monthOptions.forEach { option ->
                    val isSelected = selectedMonth == option.monthIndex && selectedYear == option.year
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (isSelected) AccentBlue else DarkSurfaceElevated,
                        modifier = Modifier.clickable {
                            viewModel.selectMonthAndYear(option.monthIndex, option.year)
                        }
                    ) {
                        Text(
                            text = option.label,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else TextMuted,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Main Balance Card - Dynamically matches Theme Color & Dark/Light mode (Compact & Dense)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    border = BorderStroke(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            listOf(
                                AccentBlue.copy(alpha = 0.85f),
                                AccentBlueBright.copy(alpha = 0.45f),
                                Color(0x33FFFFFF)
                            )
                        )
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                getBalanceCardGradient(
                                    themeColor = AccentBlue,
                                    isDark = AppThemeState.isDarkMode
                                )
                            )
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Column {
                            // Top Row: Title & Balance on Left, Action Badges on Right
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Total Saldo",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (isHidden) "Rp ••••••••" else CurrencyHelper.formatRupiah(totalBalance),
                                        fontSize = 21.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        letterSpacing = (-0.3).sp
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // Trophy button
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0x28FFFFFF),
                                        border = BorderStroke(0.5.dp, Color(0x33FFFFFF)),
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clickable { onQuickActionClick("Level") }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.WorkspacePremium,
                                                contentDescription = "Level",
                                                tint = TrophyYellow,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    // Receipt badge count
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0x28FFFFFF),
                                        border = BorderStroke(0.5.dp, Color(0x33FFFFFF)),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Receipt,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "${transactions.size}",
                                                fontSize = 11.5.sp,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    // Eye icon
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Color(0x28FFFFFF),
                                        border = BorderStroke(0.5.dp, Color(0x33FFFFFF)),
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clickable { viewModel.toggleBalanceVisibility() }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = if (isHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                contentDescription = "Sembunyikan Saldo",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Income & Expense Sub-Cards (Compact)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Income Pill
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color(0x1F22C55E),
                                    border = BorderStroke(1.dp, Color(0x3322C55E))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(26.dp)
                                                .clip(CircleShape)
                                                .background(IncomeGreenSoft),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                                contentDescription = null,
                                                tint = IncomeGreen,
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "Pemasukan",
                                                fontSize = 10.5.sp,
                                                color = Color(0xFFBACCEB)
                                            )
                                            Text(
                                                text = if (isHidden) "Rp •••" else CurrencyHelper.formatRupiah(monthlyIncome),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }

                                // Expense Pill
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color(0x1FF43F5E),
                                    border = BorderStroke(1.dp, Color(0x33F43F5E))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(26.dp)
                                                .clip(CircleShape)
                                                .background(ExpenseRedSoft),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                                                contentDescription = null,
                                                tint = ExpenseRed,
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "Pengeluaran",
                                                fontSize = 10.5.sp,
                                                color = Color(0xFFBACCEB)
                                            )
                                            Text(
                                                text = if (isHidden) "Rp •••" else CurrencyHelper.formatRupiah(monthlyExpense),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }

                            // Monthly Transfer Indicator if any transfers exist
                            if (monthlyTransfer > 0.0) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0x1F0284C7),
                                    border = BorderStroke(0.5.dp, Color(0x330284C7))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.SwapHoriz,
                                                contentDescription = null,
                                                tint = Color(0xFF38BDF8),
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Transfer & Aset Bulan Ini",
                                                fontSize = 10.5.sp,
                                                color = Color(0xFFBACCEB)
                                            )
                                        }
                                        Text(
                                            text = if (isHidden) "Rp •••" else CurrencyHelper.formatRupiah(monthlyTransfer),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF38BDF8)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Quick Actions Bar
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = DarkSurfaceElevated.copy(alpha = 0.94f),
                    border = BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.7f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp, horizontal = 6.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        QuickActionButton(
                            label = "Anggaran",
                            onClick = { onQuickActionClick("Anggaran") }
                        ) {
                            AnggaranTwoToneIcon(themeColor = AccentBlue, modifier = Modifier.size(28.dp))
                        }
                        QuickActionButton(
                            label = "Perencana",
                            onClick = { onQuickActionClick("Perencana") }
                        ) {
                            PerencanaTwoToneIcon(themeColor = AccentBlue, modifier = Modifier.size(28.dp))
                        }
                        QuickActionButton(
                            label = "Acara",
                            onClick = { onQuickActionClick("Acara") }
                        ) {
                            AcaraTwoToneIcon(themeColor = AccentBlue, modifier = Modifier.size(28.dp))
                        }
                        QuickActionButton(
                            label = "Aset",
                            onClick = { onQuickActionClick("Aset") }
                        ) {
                            AsetTwoToneIcon(themeColor = AccentBlue, modifier = Modifier.size(28.dp))
                        }
                        QuickActionButton(
                            label = "Tabungan",
                            onClick = { onQuickActionClick("Tabungan") }
                        ) {
                            TabunganTwoToneIcon(themeColor = AccentBlue, modifier = Modifier.size(28.dp))
                        }
                    }
                }
            }



            // Transactions Grouped by Day
            if (groupedTransactions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                        border = BorderStroke(1.dp, DarkCardBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(AccentBlue.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Receipt,
                                    contentDescription = null,
                                    tint = AccentBlueBright,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Belum Ada Transaksi",
                                color = TextWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Mulai kelola keuangan Anda dengan mencatat transaksi harian",
                                color = TextMuted,
                                fontSize = 12.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                groupedTransactions.forEach { (day, txList) ->
                    item(key = "day_group_$day") {
                        val dayIncome = txList.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                        val dayExpense = txList.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
                        val dayTransfer = txList.filter { it.type == TransactionType.TRANSFER }.sumOf { it.amount }
                        val firstTx = txList.first()

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp, bottom = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "$day",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = DarkSurfaceElevated
                                    ) {
                                        Text(
                                            text = firstTx.dayOfWeekName,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextMuted,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "$selectedMonth/${firstTx.year.toString().takeLast(2)}",
                                        fontSize = 12.sp,
                                        color = TextSubtle
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    if (dayIncome > 0) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = IncomeGreenSoft
                                        ) {
                                            Text(
                                                text = "+${CurrencyHelper.formatRupiah(dayIncome)}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = IncomeGreen,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                    if (dayExpense > 0) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = ExpenseRedSoft
                                        ) {
                                            Text(
                                                text = "-${CurrencyHelper.formatRupiah(dayExpense)}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = ExpenseRed,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                    if (dayTransfer > 0) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = Color(0x260284C7)
                                        ) {
                                            Text(
                                                text = "⇄ ${CurrencyHelper.formatRupiah(dayTransfer)}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF38BDF8),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Unified Card containing all transactions for this date tightly grouped together
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkCardSubtle),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    txList.forEachIndexed { index, tx ->
                                        val isSelected = selectedIds.contains(tx.id)
                                        val walletIcon = wallets.find { it.name.equals(tx.accountName, ignoreCase = true) }?.iconName ?: ""
                                        val targetIcon = if (tx.toAccountName.isNotBlank()) {
                                            wallets.find { it.name.equals(tx.toAccountName, ignoreCase = true) }?.iconName ?: ""
                                        } else ""
                                        TransactionRowItem(
                                            transaction = tx,
                                            accountIcon = walletIcon,
                                            targetIcon = targetIcon,
                                            isSelectionMode = isSelectionMode,
                                            isSelected = isSelected,
                                            onClick = {
                                                if (isSelectionMode) {
                                                    selectedIds = if (isSelected) {
                                                        val updated = selectedIds - tx.id
                                                        if (updated.isEmpty()) isSelectionMode = false
                                                        updated
                                                    } else {
                                                        selectedIds + tx.id
                                                    }
                                                } else {
                                                    editingTransaction = tx
                                                }
                                            },
                                            onLongClick = {
                                                if (!isSelectionMode) {
                                                    isSelectionMode = true
                                                    selectedIds = setOf(tx.id)
                                                } else {
                                                    selectedIds = if (isSelected) {
                                                        val updated = selectedIds - tx.id
                                                        if (updated.isEmpty()) isSelectionMode = false
                                                        updated
                                                    } else {
                                                        selectedIds + tx.id
                                                    }
                                                }
                                            }
                                        )
                                        if (index < txList.size - 1) {
                                            HorizontalDivider(
                                                color = DarkCardBorder.copy(alpha = 0.35f),
                                                thickness = 0.6.dp,
                                                modifier = Modifier.padding(horizontal = 14.dp)
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

    // Edit Transaction Dialog (single click)
    editingTransaction?.let { tx ->
        EditTransactionDialog(
            transaction = tx,
            viewModel = viewModel,
            onDismiss = { editingTransaction = null }
        )
    }

    // Batch Change Category Dialog
    if (showBatchCategoryDialog) {
        CategoryPickerSubDialog(
            currentCategory = "",
            categories = categories,
            onCategorySelected = { newCat ->
                viewModel.batchUpdateCategory(selectedIds, newCat, "")
                showBatchCategoryDialog = false
                isSelectionMode = false
                selectedIds = emptySet()
            },
            onCategoryAndSubcategorySelected = { cat, subcat ->
                viewModel.batchUpdateCategory(selectedIds, cat, subcat)
                showBatchCategoryDialog = false
                isSelectionMode = false
                selectedIds = emptySet()
            },
            onDismiss = { showBatchCategoryDialog = false }
        )
    }

    // Batch Change Label Dialog
    if (showBatchLabelDialog) {
        BatchLabelDialog(
            selectedCount = selectedIds.size,
            availableLabels = availableLabels,
            onSelectLabel = { newLabel ->
                if (newLabel.isNotBlank()) {
                    viewModel.addLabel(newLabel)
                }
                viewModel.batchUpdateLabel(selectedIds, newLabel)
                showBatchLabelDialog = false
                isSelectionMode = false
                selectedIds = emptySet()
            },
            onDismiss = { showBatchLabelDialog = false }
        )
    }

    // Batch Delete Dialog
    if (showBatchDeleteConfirm) {
        ConfirmDeleteTransactionsDialog(
            count = selectedIds.size,
            onConfirm = {
                viewModel.batchDeleteTransactions(selectedIds)
                showBatchDeleteConfirm = false
                isSelectionMode = false
                selectedIds = emptySet()
            },
            onDismiss = { showBatchDeleteConfirm = false }
        )
    }

    // Fitur Transaksi Berulang Dialog
    if (showRecurringDialog) {
        RecurringTransactionDialog(
            viewModel = viewModel,
            onDismiss = { showRecurringDialog = false }
        )
    }
}

@Composable
fun QuickActionButton(
    label: String,
    onClick: () -> Unit,
    iconContent: @Composable () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = DarkSurfaceElevated,
            border = BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.5f)),
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                iconContent()
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 11.5.sp,
            color = TextWhite,
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionRowItem(
    transaction: Transaction,
    accountIcon: String = "",
    targetIcon: String = "",
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val isExpense = transaction.type == TransactionType.EXPENSE
    val isIncome = transaction.type == TransactionType.INCOME
    val isTransfer = transaction.type == TransactionType.TRANSFER
    val isDebtRelated = transaction.targetDebtId.isNotBlank() ||
        transaction.targetDebtName.isNotBlank() ||
        transaction.category.contains("Utang", ignoreCase = true) ||
        transaction.category.contains("Piutang", ignoreCase = true) ||
        transaction.title.contains("Bayar utang", ignoreCase = true) ||
        transaction.title.contains("Pelunasan piutang", ignoreCase = true)
    val isPiutang = isDebtRelated && (transaction.category.contains("Piutang", ignoreCase = true) || transaction.title.contains("Piutang", ignoreCase = true) || isIncome)
    val isDebtTransfer = isDebtRelated
    val isAssetTransfer = isTransfer && !isDebtTransfer && (transaction.targetAssetName.isNotBlank() || transaction.category.equals("Asset", ignoreCase = true))

    Surface(
        color = if (isSelected) Color(0xFF1E293B) else Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Multi-select Checkbox indicator
                if (isSelectionMode) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) AccentBlue else Color.Transparent)
                            .border(
                                1.5.dp,
                                if (isSelected) AccentBlue else DarkCardBorder,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Terpilih",
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // Category Icon
                val catColor = if (isDebtTransfer) (if (isPiutang) Color(0xFF10B981) else Color(0xFFFF5722)) else CategoryIconHelper.getCategoryColor(transaction.category, transaction.type)
                val catIcon = if (isDebtTransfer) Icons.Default.Handshake else CategoryIconHelper.getCategoryIcon(transaction.category, transaction.subcategory)

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(catColor.copy(alpha = 0.18f))
                        .border(0.8.dp, catColor.copy(alpha = 0.35f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = catIcon,
                        contentDescription = transaction.category,
                        tint = catColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val categoryTitle = when {
                            isDebtTransfer -> if (isPiutang) "Pelunasan Piutang" else "Pembayaran Utang"
                            isAssetTransfer -> "Transfer Aset"
                            isTransfer -> "Transfer Antar Akun"
                            transaction.subcategory.isNotBlank() -> "${transaction.category} · ${transaction.subcategory}"
                            else -> transaction.category
                        }
                        Text(
                            text = categoryTitle,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (isDebtTransfer) {
                            Spacer(modifier = Modifier.width(5.dp))
                            val badgeColor = if (isPiutang) Color(0xFF10B981) else Color(0xFFFF5722)
                            val badgeTextColor = if (isPiutang) Color(0xFF34D399) else Color(0xFFFF7043)
                            val badgeLabel = if (isPiutang) "PIUTANG" else "UTANG"
                            Surface(
                                shape = RoundedCornerShape(5.dp),
                                color = badgeColor.copy(alpha = 0.15f),
                                border = BorderStroke(0.5.dp, badgeColor)
                            ) {
                                Text(
                                    text = badgeLabel,
                                    fontSize = 8.5.sp,
                                    color = badgeTextColor,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.5.dp)
                                )
                            }
                        } else if (isAssetTransfer) {
                            Spacer(modifier = Modifier.width(5.dp))
                            Surface(
                                shape = RoundedCornerShape(5.dp),
                                color = Color(0x2610B981),
                                border = BorderStroke(0.5.dp, Color(0xFF10B981))
                            ) {
                                Text(
                                    text = "ASET",
                                    fontSize = 8.5.sp,
                                    color = Color(0xFF34D399),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.5.dp)
                                )
                            }
                        } else if (isTransfer) {
                            Spacer(modifier = Modifier.width(5.dp))
                            Surface(
                                shape = RoundedCornerShape(5.dp),
                                color = Color(0x260284C7),
                                border = BorderStroke(0.5.dp, Color(0xFF0284C7))
                            ) {
                                Text(
                                    text = "TRANSFER",
                                    fontSize = 8.5.sp,
                                    color = Color(0xFF38BDF8),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.5.dp)
                                )
                            }
                        }
                    }

                    // Label diposisikan di bawah kategori agar menghemat ruang horizontal
                    if (transaction.label.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        val labelList = remember(transaction.label) {
                            transaction.label.split(",").map { it.trim() }.filter { it.isNotBlank() }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            labelList.forEach { singleLabel ->
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = DarkSurfaceElevated,
                                    border = BorderStroke(0.5.dp, DarkCardBorder)
                                ) {
                                    Text(
                                        text = singleLabel,
                                        fontSize = 8.5.sp,
                                        color = Color(0xFFFCD34D),
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    }

                    val subtitleText = when {
                        transaction.title.isNotBlank() -> transaction.title
                        isDebtTransfer && transaction.targetDebtName.isNotBlank() ->
                            if (isPiutang) "Pelunasan piutang: ${transaction.targetDebtName}" else "Bayar utang: ${transaction.targetDebtName}"
                        isAssetTransfer && transaction.targetAssetName.isNotBlank() -> "Transfer ke aset ${transaction.targetAssetName}"
                        isTransfer && transaction.toAccountName.isNotBlank() -> "Transfer ke ${transaction.toAccountName}"
                        else -> "Transaksi"
                    }
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = subtitleText,
                        fontSize = 11.sp,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    InstitutionLogoBadge(
                        walletName = transaction.accountName,
                        iconName = accountIcon,
                        size = 14.dp
                    )
                    if (isAssetTransfer && transaction.targetAssetName.isNotBlank()) {
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("→", fontSize = 9.sp, color = TextSubtle)
                        Spacer(modifier = Modifier.width(2.dp))
                        AssetLogoBadge(
                            assetType = "Asset",
                            assetName = transaction.targetAssetName,
                            size = 14.dp
                        )
                    } else if (isDebtTransfer && transaction.targetDebtName.isNotBlank()) {
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("→", fontSize = 9.sp, color = TextSubtle)
                        Spacer(modifier = Modifier.width(2.dp))
                        InstitutionLogoBadge(
                            walletName = transaction.targetDebtName,
                            iconName = "CreditCard",
                            size = 14.dp
                        )
                    } else if (isTransfer && transaction.toAccountName.isNotBlank()) {
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("→", fontSize = 9.sp, color = TextSubtle)
                        Spacer(modifier = Modifier.width(2.dp))
                        InstitutionLogoBadge(
                            walletName = transaction.toAccountName,
                            iconName = targetIcon,
                            size = 14.dp
                        )
                    }
                    Spacer(modifier = Modifier.width(3.dp))
                    val targetName = when {
                        isDebtTransfer -> transaction.targetDebtName.ifBlank { transaction.toAccountName }
                        isAssetTransfer -> transaction.targetAssetName
                        else -> transaction.toAccountName
                    }
                    val accountDisplay = when {
                        isDebtTransfer && targetName.isNotBlank() ->
                            if (isPiutang && isIncome) "$targetName → ${transaction.accountName}" else "${transaction.accountName} → $targetName"
                        (isAssetTransfer || isTransfer) && targetName.isNotBlank() ->
                            "${transaction.accountName} → $targetName"
                        else -> transaction.accountName
                    }
                    Text(
                        text = accountDisplay,
                        fontSize = 10.sp,
                        color = TextSubtle
                    )
                }
                Spacer(modifier = Modifier.height(1.dp))
                val amountString = when {
                    isDebtTransfer && isIncome -> "+${CurrencyHelper.formatRupiah(transaction.amount)}"
                    isDebtTransfer && isExpense -> "-${CurrencyHelper.formatRupiah(transaction.amount)}"
                    transaction.type == TransactionType.EXPENSE -> "-${CurrencyHelper.formatRupiah(transaction.amount)}"
                    transaction.type == TransactionType.INCOME -> "+${CurrencyHelper.formatRupiah(transaction.amount)}"
                    transaction.type == TransactionType.TRANSFER -> "⇄ ${CurrencyHelper.formatRupiah(transaction.amount)}"
                    else -> CurrencyHelper.formatRupiah(transaction.amount)
                }
                val amountColor = when {
                    isDebtTransfer && isIncome -> IncomeGreen
                    isDebtTransfer && isExpense -> Color(0xFFFF7043)
                    transaction.type == TransactionType.EXPENSE -> ExpenseRed
                    transaction.type == TransactionType.INCOME -> IncomeGreen
                    isDebtTransfer -> Color(0xFFFF7043)
                    else -> Color(0xFF38BDF8)
                }
                Text(
                    text = amountString,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = amountColor
                )
                if (transaction.transferFee > 0) {
                    Spacer(modifier = Modifier.height(1.dp))
                    Text(
                        text = "+ Biaya ${CurrencyHelper.formatRupiah(transaction.transferFee)}",
                        fontSize = 9.sp,
                        color = TextMuted
                    )
                }
            }
        }
    }
}
