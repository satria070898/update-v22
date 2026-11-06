package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.PlanSectionType
import com.example.data.model.PlannedItem
import com.example.ui.dialogs.MonthYearPickerDialog
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
import com.example.util.CategoryIconHelper
import com.example.util.ThousandsSeparatorVisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerScreen(
    viewModel: FinanceViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val planTitle by viewModel.planTitle.collectAsState()
    val plannedItems by viewModel.plannedItems.collectAsState()
    val plannedIncomeTotal by viewModel.plannedIncomeTotal.collectAsState()
    val plannedExpenseTotal by viewModel.plannedExpenseTotal.collectAsState()
    val plannedSavingsTotal by viewModel.plannedSavingsTotal.collectAsState()
    val unallocatedAmount by viewModel.unallocatedAmount.collectAsState()

    val selectedMonth by viewModel.selectedMonthIndex.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()
    val monthOptions by viewModel.monthOptions.collectAsState()

    // Dialog states
    var showMonthPicker by remember { mutableStateOf(false) }
    var showEditPlanTitleDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<PlannedItem?>(null) }
    var addingSection by remember { mutableStateOf<PlanSectionType?>(null) }

    val incomeItems = remember(plannedItems) { plannedItems.filter { it.section == PlanSectionType.INCOME } }
    val expenseItems = remember(plannedItems) { plannedItems.filter { it.section == PlanSectionType.EXPENSE } }
    val savingsItems = remember(plannedItems) { plannedItems.filter { it.section == PlanSectionType.SAVINGS } }

    val savingsRate = remember(plannedIncomeTotal, plannedSavingsTotal) {
        if (plannedIncomeTotal > 0) (plannedSavingsTotal / plannedIncomeTotal) * 100 else 0.0
    }
    val expenseRatio = remember(plannedIncomeTotal, plannedExpenseTotal) {
        if (plannedIncomeTotal > 0) (plannedExpenseTotal / plannedIncomeTotal) * 100 else 0.0
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        containerColor = DarkBackground,
        bottomBar = {
            // Sticky Bottom Bar "BELUM DIALOKASIKAN"
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = DarkSurface,
                tonalElevation = 8.dp,
                border = BorderStroke(0.5.dp, DarkCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BELUM DIALOKASIKAN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextMuted,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = CurrencyHelper.formatRupiah(unallocatedAmount),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        modifier = Modifier.testTag("bottom_unallocated_amount")
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Top App Bar: [←] Perencana [Share]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("btn_planner_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = TextWhite,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Perencana",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }

                IconButton(
                    onClick = { /* share / export plan */ },
                    modifier = Modifier.testTag("btn_planner_share")
                ) {
                    Icon(
                        imageVector = Icons.Default.IosShare,
                        contentDescription = "Bagikan",
                        tint = TextWhite,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Month Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Calendar button with dropdown arrow
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurfaceElevated,
                    modifier = Modifier
                        .padding(start = 16.dp, end = 8.dp)
                        .clickable { showMonthPicker = true }
                        .testTag("btn_planner_month_picker")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Pilih Bulan",
                            tint = AccentBlueBright,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Scrollable Month Chips
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState())
                        .padding(end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    monthOptions.takeLast(4).forEach { (index, label) ->
                        val isSelected = selectedMonth == index
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = if (isSelected) AccentBlue else DarkSurfaceElevated,
                            modifier = Modifier.clickable { viewModel.selectMonth(index) }
                        ) {
                            Text(
                                text = label,
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
                // Plan Subtitle & Info Icon
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { showEditPlanTitleDialog = true }
                                .testTag("btn_edit_plan_title")
                        ) {
                            Text(
                                text = planTitle,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Ubah Nama Rencana",
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        IconButton(
                            onClick = { showHelpDialog = true },
                            modifier = Modifier.size(28.dp).testTag("btn_planner_help")
                        ) {
                            Icon(
                                imageVector = Icons.Default.HelpOutline,
                                contentDescription = "Bantuan Perencanaan",
                                tint = TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Summary Card: "BELUM DIALOKASIKAN" & Progress Bar
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = BorderStroke(1.dp, DarkCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "BELUM DIALOKASIKAN",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSubtle,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = CurrencyHelper.formatRupiah(unallocatedAmount),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Belum dialokasikan. Rencana yang seimbang menempatkan setiap jumlah di tempatnya — targetkan nol.",
                                fontSize = 12.sp,
                                color = TextMuted,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            // Segmented Multi-Color Progress Bar
                            SegmentedPlannerProgressBar(
                                expenseTotal = plannedExpenseTotal,
                                savingsTotal = plannedSavingsTotal,
                                unallocatedTotal = unallocatedAmount.coerceAtLeast(0.0),
                                incomeTotal = plannedIncomeTotal
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Legend Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PlannerLegendItem(color = Color(0xFFEF4444), label = "Pengeluaran")
                                PlannerLegendItem(color = Color(0xFF10B981), label = "Tabungan")
                                PlannerLegendItem(color = Color(0xFF334155), label = "Ruang gerak")
                            }

                            Spacer(modifier = Modifier.height(18.dp))
                            HorizontalDivider(color = DarkCardBorder, thickness = 0.8.dp)
                            Spacer(modifier = Modifier.height(14.dp))

                            // Metric Columns: Tingkat Tabungan & Rasio Pengeluaran
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "TINGKAT TABUNGAN",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextSubtle,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = String.format(java.util.Locale.US, "%.1f%%", savingsRate),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "RASIO PENGELUARAN",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextSubtle,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = String.format(java.util.Locale.US, "%.1f%%", expenseRatio),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 1: PEMASUKAN
                item {
                    PlannerSectionHeader(
                        title = "PEMASUKAN",
                        badgeColor = Color(0xFF10B981),
                        totalAmount = plannedIncomeTotal,
                        amountColor = IncomeGreen
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = BorderStroke(1.dp, DarkCardBorder)
                    ) {
                        Column {
                            incomeItems.forEachIndexed { index, item ->
                                PlannedItemRow(
                                    item = item,
                                    amountColor = IncomeGreen,
                                    icon = Icons.Default.AttachMoney,
                                    iconBgColor = Color(0x2E10B981),
                                    iconTint = IncomeGreen,
                                    onEditClick = { editingItem = item }
                                )
                                if (index < incomeItems.size - 1) {
                                    HorizontalDivider(color = DarkCardBorder, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                                }
                            }

                            // "+ Tambah pemasukan" row button
                            AddPlannedItemRowButton(
                                label = "Tambah pemasukan",
                                onClick = { addingSection = PlanSectionType.INCOME }
                            )
                        }
                    }
                }

                // Section 2: PENGELUARAN
                item {
                    PlannerSectionHeader(
                        title = "PENGELUARAN",
                        badgeColor = Color(0xFFEF4444),
                        totalAmount = plannedExpenseTotal,
                        amountColor = Color(0xFFEF4444)
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = BorderStroke(1.dp, DarkCardBorder)
                    ) {
                        Column {
                            expenseItems.forEachIndexed { index, item ->
                                val (icon, bgCol, tintCol) = getCategoryIconAndColor(item.category, item.name)
                                PlannedItemRow(
                                    item = item,
                                    amountColor = Color(0xFFEF4444),
                                    icon = icon,
                                    iconBgColor = bgCol,
                                    iconTint = tintCol,
                                    onEditClick = { editingItem = item }
                                )
                                if (index < expenseItems.size - 1) {
                                    HorizontalDivider(color = DarkCardBorder, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                                }
                            }

                            // "+ Tambah pengeluaran" row button
                            AddPlannedItemRowButton(
                                label = "Tambah pengeluaran",
                                onClick = { addingSection = PlanSectionType.EXPENSE }
                            )
                        }
                    }
                }

                // Section 3: RENCANA TABUNGAN
                item {
                    PlannerSectionHeader(
                        title = "RENCANA TABUNGAN",
                        badgeColor = Color(0xFF10B981),
                        totalAmount = plannedSavingsTotal,
                        amountColor = IncomeGreen
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = BorderStroke(1.dp, DarkCardBorder)
                    ) {
                        Column {
                            savingsItems.forEachIndexed { index, item ->
                                PlannedItemRow(
                                    item = item,
                                    amountColor = IncomeGreen,
                                    icon = null, // Savings section in screenshot doesn't have category circle
                                    iconBgColor = Color.Transparent,
                                    iconTint = Color.Transparent,
                                    onEditClick = { editingItem = item }
                                )
                                if (index < savingsItems.size - 1) {
                                    HorizontalDivider(color = DarkCardBorder, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                                }
                            }

                            // "+ Tambah tabungan" row button
                            AddPlannedItemRowButton(
                                label = "Tambah tabungan",
                                onClick = { addingSection = PlanSectionType.SAVINGS }
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    // Month Picker Dialog
    if (showMonthPicker) {
        MonthYearPickerDialog(
            currentMonth = selectedMonth,
            currentYear = selectedYear,
            onDismiss = { showMonthPicker = false },
            onSelectMonthYear = { m, y ->
                viewModel.selectMonthAndYear(m, y)
                showMonthPicker = false
            }
        )
    }

    // Edit Planned Item Dialog (pencil click)
    editingItem?.let { item ->
        EditPlannedItemDialog(
            item = item,
            onSave = { updated ->
                viewModel.updatePlannedItem(updated)
                editingItem = null
            },
            onDelete = {
                viewModel.deletePlannedItem(item.id)
                editingItem = null
            },
            onDismiss = { editingItem = null }
        )
    }

    // Add Planned Item Dialog (+ click)
    addingSection?.let { section ->
        AddPlannedItemDialog(
            section = section,
            onSave = { newItem ->
                viewModel.addPlannedItem(newItem)
                addingSection = null
            },
            onDismiss = { addingSection = null }
        )
    }

    // Edit Plan Title Dialog
    if (showEditPlanTitleDialog) {
        EditPlanTitleDialog(
            currentTitle = planTitle,
            onSave = { newTitle ->
                viewModel.updatePlanTitle(newTitle)
                showEditPlanTitleDialog = false
            },
            onDismiss = { showEditPlanTitleDialog = false }
        )
    }

    // Help Dialog
    if (showHelpDialog) {
        PlannerHelpDialog(onDismiss = { showHelpDialog = false })
    }
}

@Composable
fun PlannerSectionHeader(
    title: String,
    badgeColor: Color,
    totalAmount: Double,
    amountColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(badgeColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                letterSpacing = 0.5.sp
            )
        }

        Text(
            text = CurrencyHelper.formatRupiah(totalAmount),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = amountColor
        )
    }
}

@Composable
fun PlannedItemRow(
    item: PlannedItem,
    amountColor: Color,
    icon: ImageVector?,
    iconBgColor: Color,
    iconTint: Color,
    onEditClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEditClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("planned_item_${item.id}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column {
                Text(
                    text = item.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextWhite
                )
                if (item.category.isNotBlank()) {
                    Text(
                        text = item.category,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }
        }

        // Nominal + Pencil edit icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = CurrencyHelper.formatRupiah(item.amount),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onEditClick,
                modifier = Modifier
                    .size(32.dp)
                    .testTag("btn_edit_planned_${item.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit nominal ${item.name}",
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun AddPlannedItemRowButton(
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag("btn_add_planned_row_$label"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color(0x1AFFFFFF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextMuted
        )
    }
}

@Composable
fun SegmentedPlannerProgressBar(
    expenseTotal: Double,
    savingsTotal: Double,
    unallocatedTotal: Double,
    incomeTotal: Double
) {
    val total = if (incomeTotal > 0) incomeTotal else (expenseTotal + savingsTotal + unallocatedTotal).coerceAtLeast(1.0)
    val expenseWeight = (expenseTotal / total).toFloat().coerceIn(0f, 1f)
    val savingsWeight = (savingsTotal / total).toFloat().coerceIn(0f, 1f)
    val unallocatedWeight = ((total - expenseTotal - savingsTotal).coerceAtLeast(0.0) / total).toFloat().coerceIn(0f, 1f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(DarkSurfaceElevated)
    ) {
        if (expenseWeight > 0f) {
            Box(
                modifier = Modifier
                    .weight(expenseWeight)
                    .height(10.dp)
                    .background(Color(0xFFEF4444))
            )
        }
        if (savingsWeight > 0f) {
            Box(
                modifier = Modifier
                    .weight(savingsWeight)
                    .height(10.dp)
                    .background(Color(0xFF10B981))
            )
        }
        if (unallocatedWeight > 0f) {
            Box(
                modifier = Modifier
                    .weight(unallocatedWeight)
                    .height(10.dp)
                    .background(Color(0xFF334155))
            )
        }
    }
}

@Composable
fun PlannerLegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextMuted
        )
    }
}

fun getCategoryIconAndColor(category: String, name: String): Triple<ImageVector, Color, Color> {
    val icon = CategoryIconHelper.getCategoryIcon(if (name.isNotBlank()) name else category)
    val color = CategoryIconHelper.getCategoryColor(if (name.isNotBlank()) name else category)
    return Triple(icon, color.copy(alpha = 0.2f), color)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPlannedItemDialog(
    item: PlannedItem,
    onSave: (PlannedItem) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(item.name) }
    var category by remember { mutableStateOf(item.category) }
    var amountText by remember { mutableStateOf(item.amount.toLong().toString()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = DarkSurfaceElevated,
            border = BorderStroke(1.dp, DarkCardBorder),
            modifier = Modifier.fillMaxWidth(0.92f)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit Rencana",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Nama", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_edit_planned_name"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = DarkSurface,
                        unfocusedContainerColor = DarkSurface,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = DarkCardBorder
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                if (item.section != PlanSectionType.SAVINGS) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Kategori", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_edit_planned_category"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface,
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = DarkCardBorder
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("Nominal (Rp)", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input ->
                        val digits = input.filter { it.isDigit() }
                        if (digits.length <= 11) amountText = digits
                    },
                    visualTransformation = ThousandsSeparatorVisualTransformation('.'),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_edit_planned_amount"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = DarkSurface,
                        unfocusedContainerColor = DarkSurface,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = DarkCardBorder
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = it, color = ExpenseRed, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onDelete,
                        colors = ButtonDefaults.buttonColors(containerColor = ExpenseRedSoft),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = ExpenseRed, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Hapus", color = ExpenseRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull() ?: 0.0
                            if (name.isBlank()) {
                                errorMessage = "Nama tidak boleh kosong."
                                return@Button
                            }
                            if (amt <= 0.0) {
                                errorMessage = "Nominal harus lebih dari 0."
                                return@Button
                            }
                            onSave(item.copy(name = name.trim(), category = category.trim(), amount = amt))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("btn_save_edit_planned")
                    ) {
                        Text("Simpan", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlannedItemDialog(
    section: PlanSectionType,
    onSave: (PlannedItem) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember {
        mutableStateOf(
            when (section) {
                PlanSectionType.INCOME -> "Salary"
                PlanSectionType.EXPENSE -> "Food & Drink"
                PlanSectionType.SAVINGS -> ""
            }
        )
    }
    var amountText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val sectionTitle = when (section) {
        PlanSectionType.INCOME -> "Tambah Pemasukan"
        PlanSectionType.EXPENSE -> "Tambah Pengeluaran"
        PlanSectionType.SAVINGS -> "Tambah Tabungan"
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = DarkSurfaceElevated,
            border = BorderStroke(1.dp, DarkCardBorder),
            modifier = Modifier.fillMaxWidth(0.92f)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = sectionTitle,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Nama", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Contoh: Bonus, Belanja...", color = TextSubtle) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_add_planned_name"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = DarkSurface,
                        unfocusedContainerColor = DarkSurface,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = DarkCardBorder
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                if (section != PlanSectionType.SAVINGS) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Kategori", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_add_planned_category"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface,
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = DarkCardBorder
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("Nominal (Rp)", fontSize = 12.sp, color = TextMuted, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input ->
                        val digits = input.filter { it.isDigit() }
                        if (digits.length <= 11) amountText = digits
                    },
                    visualTransformation = ThousandsSeparatorVisualTransformation('.'),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("0", color = TextSubtle) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_add_planned_amount"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = DarkSurface,
                        unfocusedContainerColor = DarkSurface,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = DarkCardBorder
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = it, color = ExpenseRed, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val amt = amountText.toDoubleOrNull() ?: 0.0
                        if (name.isBlank()) {
                            errorMessage = "Nama tidak boleh kosong."
                            return@Button
                        }
                        if (amt <= 0.0) {
                            errorMessage = "Nominal harus lebih dari 0."
                            return@Button
                        }
                        onSave(PlannedItem(name = name.trim(), category = category.trim(), amount = amt, section = section))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_save_add_planned")
                ) {
                    Text("Tambah ke Rencana", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPlanTitleDialog(
    currentTitle: String,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(currentTitle) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = DarkSurfaceElevated,
            border = BorderStroke(1.dp, DarkCardBorder),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Ubah Nama Rencana", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = DarkSurface,
                        unfocusedContainerColor = DarkSurface,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = DarkCardBorder
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(18.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Surface(shape = RoundedCornerShape(10.dp), color = Color.Transparent, modifier = Modifier.clickable { onDismiss() }) {
                        Text("Batal", color = TextMuted, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { if (title.isNotBlank()) onSave(title.trim()) },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Simpan", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun PlannerHelpDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = DarkSurfaceElevated,
            border = BorderStroke(1.dp, DarkCardBorder),
            modifier = Modifier.fillMaxWidth(0.92f)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tentang Perencanaan", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = TextMuted)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Prinsip Zero-Based Budgeting:\n\n" +
                            "1. Masukkan semua sumber pemasukan yang diperkirakan.\n" +
                            "2. Rencanakan pos pengeluaran bulanan Anda.\n" +
                            "3. Alokasikan sisa dana ke tabungan dan investasi.\n\n" +
                            "Targetkan dana 'Belum Dialokasikan' mendekati nol agar setiap rupiah memiliki tujuan yang terencana.",
                    fontSize = 13.sp,
                    color = TextMuted,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(18.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Mengerti", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
