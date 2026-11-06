package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.graphicsLayer
import com.example.ui.components.TransaksiTwoToneNavIcon
import com.example.ui.components.DasborTwoToneNavIcon
import com.example.ui.components.RingkasanTwoToneNavIcon
import com.example.ui.components.AkunTwoToneNavIcon
import com.example.ui.components.PengaturanTwoToneNavIcon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DebtItem
import com.example.data.model.Transaction
import com.example.ui.dialogs.AddAssetDialog
import com.example.ui.dialogs.AddDebtDialog
import com.example.ui.dialogs.CreateDebtDialog
import com.example.ui.dialogs.AddTransactionDialog
import com.example.ui.dialogs.AddWalletDialog
import com.example.ui.dialogs.BackupRestoreDialog
import com.example.ui.dialogs.BudgetManagementDialog
import com.example.ui.dialogs.CreateWalletDialog
import com.example.ui.dialogs.EditTransactionDialog
import com.example.ui.dialogs.GamificationRoadmapDialog
import com.example.ui.dialogs.PayDebtDialog
import com.example.ui.dialogs.EventManagementSheet
import com.example.ui.dialogs.FinancialCalculatorsDialog
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentBlueBright
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.FinanceViewModel

enum class NavigationTab(val title: String, val icon: ImageVector) {
    TRANSAKSI("Transaksi", Icons.Default.ReceiptLong),
    DASBOR("Dasbor", Icons.Default.GridView),
    RINGKASAN("Ringkasan", Icons.Default.ShowChart),
    AKUN("Akun", Icons.Default.Wallet),
    PENGATURAN("Pengaturan", Icons.Default.Settings)
}

@Composable
fun MainScreen(viewModel: FinanceViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }

    // Dialog state controllers
    var showAddTxDialog by remember { mutableStateOf(false) }
    var showBudgetDialog by remember { mutableStateOf(false) }
    var showPlannerScreen by remember { mutableStateOf(false) }
    var showLevelDialog by remember { mutableStateOf(false) }
    var showAddWalletDialog by remember { mutableStateOf(false) }
    var showAddAssetDialog by remember { mutableStateOf(false) }
    var showAddDebtDialog by remember { mutableStateOf(false) }
    var showBackupRestoreDialog by remember { mutableStateOf(false) }
    var activeDebtToPay by remember { mutableStateOf<DebtItem?>(null) }
    var activeTransactionToEdit by remember { mutableStateOf<Transaction?>(null) }
    var showEventSheet by remember { mutableStateOf(false) }
    var prefillEventLabel by remember { mutableStateOf("") }
    var showCalculatorsDialog by remember { mutableStateOf(false) }
    var initialCalculatorTab by remember { mutableIntStateOf(0) }

    val accountSubTab by viewModel.accountSubTab.collectAsState()
    val selectedWalletIdForDetail by viewModel.selectedWalletIdForDetail.collectAsState()
    val selectedAssetIdForDetail by viewModel.selectedAssetIdForDetail.collectAsState()
    val selectedDebtIdForDetail by viewModel.selectedDebtIdForDetail.collectAsState()
    val isAnyDetailOpen = selectedWalletIdForDetail != null || selectedAssetIdForDetail != null || selectedDebtIdForDetail != null

    if (showPlannerScreen) {
        PlannerScreen(
            viewModel = viewModel,
            onBack = { showPlannerScreen = false }
        )
    } else {
        // Handle back button: return to Transaksi tab if on other tabs, only when no detail view is open
        BackHandler(enabled = !isAnyDetailOpen && selectedTab != 0) {
            selectedTab = 0
        }

        // Determine FAB visibility:
        // Hide FAB completely when viewing account or asset detail.
        // Otherwise visible on Transaksi (tab 0), and on Akun (tab 3) ONLY for Akun (1), Aset (2), Utang (3).
        val isFabVisible = !isAnyDetailOpen && ((selectedTab == 0) || (selectedTab == 3 && accountSubTab != 0))

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = DarkBackground,
            contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
            floatingActionButton = {
                if (isFabVisible) {
                    FloatingActionButton(
                        onClick = {
                            if (selectedTab == 3) {
                                when (accountSubTab) {
                                    1 -> showAddWalletDialog = true
                                    2 -> showAddAssetDialog = true
                                    3 -> showAddDebtDialog = true
                                    else -> showAddWalletDialog = true
                                }
                            } else {
                                showAddTxDialog = true
                            }
                        },
                        containerColor = AccentBlue,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(18.dp),
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 14.dp
                        ),
                        modifier = Modifier
                            .testTag("fab_add_transaction")
                            .size(56.dp)
                            .border(
                                width = 1.dp,
                                brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                    listOf(Color(0x88FFFFFF), Color(0x22FFFFFF))
                                ),
                                shape = RoundedCornerShape(18.dp)
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    androidx.compose.ui.graphics.Brush.linearGradient(
                                        listOf(AccentBlueBright, Color(0xFF4F46E5))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Tambah",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.End,
            bottomBar = {
                // Sembunyikan toolbar bawah utama ketika membuka detil akun
                if (!isAnyDetailOpen) {
                    ModernBottomNavBar(
                        selectedTab = selectedTab,
                        onTabSelected = { tabIndex ->
                            selectedTab = tabIndex
                            if (tabIndex == 3) {
                                viewModel.setAccountSubTab(1) // langsung menuju toolbar akun bukan nilai bersih
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "ScreenTransition"
                ) { targetTab ->
                    when (targetTab) {
                        0 -> TransactionScreen(
                            viewModel = viewModel,
                            onQuickActionClick = { action ->
                                when (action) {
                                    "Anggaran" -> showBudgetDialog = true
                                    "Perencana" -> showPlannerScreen = true
                                    "Acara" -> showEventSheet = true
                                    "Aset" -> {
                                        viewModel.setAccountSubTab(2)
                                        selectedTab = 3 // navigate to Akun -> Aset
                                    }
                                    "Tabungan" -> {
                                        viewModel.setAccountSubTab(2)
                                        selectedTab = 3
                                    }
                                    "Level" -> showLevelDialog = true
                                    "Alat" -> {
                                        initialCalculatorTab = 0
                                        showCalculatorsDialog = true
                                    }
                                }
                            },
                            onTransactionClick = { tx -> activeTransactionToEdit = tx }
                        )
                    1 -> DashboardScreen(
                        viewModel = viewModel,
                        onAddUpcomingClick = { showAddTxDialog = true },
                        onLevelClick = { showLevelDialog = true },
                        onNavigateToSummary = { subTab ->
                            viewModel.setSummaryTab(subTab)
                            selectedTab = 2
                        },
                        onOpenCalculators = {
                            initialCalculatorTab = 0
                            showCalculatorsDialog = true
                        },
                        onOpenSplitBill = {
                            initialCalculatorTab = 4
                            showCalculatorsDialog = true
                        }
                    )
                    2 -> SummaryScreen(
                        viewModel = viewModel,
                        onNavigateToTransactions = { category ->
                            if (!category.isNullOrEmpty()) {
                                viewModel.openSearch(category)
                            }
                            selectedTab = 0
                        },
                        onNavigateToAccount = { walletName ->
                            viewModel.selectWalletFilter(walletName)
                            viewModel.setAccountSubTab(1)
                            selectedTab = 3
                        },
                        onNavigateToAssets = {
                            viewModel.setAccountSubTab(2)
                            selectedTab = 3
                        },
                        onNavigateToDebts = {
                            viewModel.setAccountSubTab(3)
                            selectedTab = 3
                        },
                        onNavigateToPlanner = {
                            showPlannerScreen = true
                        },
                        onNavigateToBudgets = {
                            showBudgetDialog = true
                        },
                        onAddTransaction = { cat ->
                            showAddTxDialog = true
                        },
                        onTransactionClick = { tx ->
                            activeTransactionToEdit = tx
                        }
                    )
                    3 -> AccountScreen(
                        viewModel = viewModel,
                        onAddWalletClick = { showAddWalletDialog = true },
                        onAddAssetClick = { showAddAssetDialog = true },
                        onAddDebtClick = { showAddDebtDialog = true },
                        onPayDebtClick = { activeDebtToPay = it }
                    )
                    4 -> SettingsScreen(
                        viewModel = viewModel,
                        onBackupRestoreClick = { showBackupRestoreDialog = true }
                    )
                }
            }
        }
    }

    // Dialogs
    if (showBackupRestoreDialog) {
        BackupRestoreDialog(
            viewModel = viewModel,
            onDismiss = { showBackupRestoreDialog = false }
        )
    }

    if (showAddTxDialog) {
        AddTransactionDialog(
            viewModel = viewModel,
            initialLabel = prefillEventLabel,
            onDismiss = {
                showAddTxDialog = false
                prefillEventLabel = ""
            }
        )
    }

    if (showEventSheet) {
        EventManagementSheet(
            viewModel = viewModel,
            onDismiss = { showEventSheet = false },
            onNavigateToTransactions = { eventLabel ->
                viewModel.openSearch(eventLabel)
                selectedTab = 0
                showEventSheet = false
            },
            onAddTransactionWithEvent = { eventLabel ->
                prefillEventLabel = eventLabel
                showAddTxDialog = true
                showEventSheet = false
            }
        )
    }

    if (showBudgetDialog) {
        BudgetManagementDialog(
            viewModel = viewModel,
            onDismiss = { showBudgetDialog = false }
        )
    }

    if (showLevelDialog) {
        GamificationRoadmapDialog(
            onDismiss = { showLevelDialog = false }
        )
    }

    if (showAddWalletDialog) {
        CreateWalletDialog(
            viewModel = viewModel,
            onDismiss = { showAddWalletDialog = false }
        )
    }

    if (showAddAssetDialog) {
        AddAssetDialog(
            viewModel = viewModel,
            onDismiss = { showAddAssetDialog = false }
        )
    }

    if (showAddDebtDialog) {
        CreateDebtDialog(
            viewModel = viewModel,
            onDismiss = { showAddDebtDialog = false }
        )
    }

    activeDebtToPay?.let { debt ->
        PayDebtDialog(
            debt = debt,
            viewModel = viewModel,
            onDismiss = { activeDebtToPay = null }
        )
    }

    activeTransactionToEdit?.let { tx ->
        EditTransactionDialog(
            transaction = tx,
            viewModel = viewModel,
            onDismiss = { activeTransactionToEdit = null }
        )
    }

    if (showCalculatorsDialog) {
        val monthlyIncome by viewModel.monthlyIncome.collectAsState()
        val monthlyExpense by viewModel.monthlyExpense.collectAsState()
        val totalBalance by viewModel.totalWalletBalance.collectAsState()
        FinancialCalculatorsDialog(
            currentMonthlyIncome = monthlyIncome,
            currentMonthlyExpense = monthlyExpense,
            totalBalance = totalBalance,
            initialTab = initialCalculatorTab,
            onDismiss = { showCalculatorsDialog = false }
        )
    }
    }
}

@Composable
fun ModernBottomNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = DarkSurfaceElevated.copy(alpha = 0.96f),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        tonalElevation = 12.dp,
        border = BorderStroke(
            1.dp,
            androidx.compose.ui.graphics.Brush.verticalGradient(
                listOf(DarkCardBorder.copy(alpha = 0.8f), Color.Transparent)
            )
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Subtle glowing top accent bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(
                        androidx.compose.ui.graphics.Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                AccentBlue.copy(alpha = 0.6f),
                                Color(0xFF6366F1).copy(alpha = 0.6f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 0: Transaksi
                NavTabItem(
                    title = "Transaksi",
                    isSelected = selectedTab == 0,
                    activeColor = AccentBlue,
                    tag = "nav_tab_transaksi",
                    onClick = { onTabSelected(0) },
                    modifier = Modifier.weight(1f),
                    icon = {
                        TransaksiTwoToneNavIcon(
                            themeColor = AccentBlue,
                            isSelected = selectedTab == 0,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                )

                // Tab 1: Dasbor
                NavTabItem(
                    title = "Dasbor",
                    isSelected = selectedTab == 1,
                    activeColor = AccentBlue,
                    tag = "nav_tab_dasbor",
                    onClick = { onTabSelected(1) },
                    modifier = Modifier.weight(1f),
                    icon = {
                        DasborTwoToneNavIcon(
                            themeColor = AccentBlue,
                            isSelected = selectedTab == 1,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                )

                // Tab 2: Ringkasan
                NavTabItem(
                    title = "Ringkasan",
                    isSelected = selectedTab == 2,
                    activeColor = AccentBlue,
                    tag = "nav_tab_ringkasan",
                    onClick = { onTabSelected(2) },
                    modifier = Modifier.weight(1f),
                    icon = {
                        RingkasanTwoToneNavIcon(
                            themeColor = AccentBlue,
                            isSelected = selectedTab == 2,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                )

                // Tab 3: Akun
                NavTabItem(
                    title = "Akun",
                    isSelected = selectedTab == 3,
                    activeColor = AccentBlue,
                    tag = "nav_tab_akun",
                    onClick = { onTabSelected(3) },
                    modifier = Modifier.weight(1f),
                    icon = {
                        AkunTwoToneNavIcon(
                            themeColor = AccentBlue,
                            isSelected = selectedTab == 3,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                )

                // Tab 4: Pengaturan
                NavTabItem(
                    title = "Pengaturan",
                    isSelected = selectedTab == 4,
                    activeColor = AccentBlue,
                    tag = "nav_tab_pengaturan",
                    onClick = { onTabSelected(4) },
                    modifier = Modifier.weight(1f),
                    icon = {
                        PengaturanTwoToneNavIcon(
                            themeColor = AccentBlue,
                            isSelected = selectedTab == 4,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun NavTabItem(
    title: String,
    isSelected: Boolean,
    activeColor: Color = AccentBlue,
    tag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit
) {
    val pillBgColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (isSelected) activeColor.copy(alpha = 0.18f) else Color.Transparent,
        animationSpec = androidx.compose.animation.core.spring(stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow),
        label = "pillBgColor"
    )

    val contentColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (isSelected) activeColor else Color(0xFF8E9BAE),
        label = "contentColor"
    )

    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1.0f,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessLow
        ),
        label = "scale"
    )

    Box(
        modifier = modifier
            .padding(horizontal = 2.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(pillBgColor)
                .then(
                    if (isSelected) {
                        Modifier.border(
                            width = 1.dp,
                            color = activeColor.copy(alpha = 0.45f),
                            shape = RoundedCornerShape(14.dp)
                        )
                    } else Modifier
                )
                .padding(horizontal = 3.dp, vertical = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    },
                contentAlignment = Alignment.Center
            ) {
                icon()
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor,
                maxLines = 1
            )
        }
    }
}
