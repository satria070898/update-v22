package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.roundToInt
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.graphics.graphicsLayer
import com.example.ui.components.NilaiBersihTwoToneNavIcon
import com.example.ui.components.RingkasanTwoToneNavIcon
import com.example.ui.components.AkunTwoToneNavIcon
import com.example.ui.components.AsetTwoToneNavIcon
import com.example.ui.components.UtangTwoToneNavIcon
import com.example.ui.components.InactiveNavGrey
import com.example.ui.components.ChartHistoricalDetailCard
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WorkspacePremium
import com.example.data.model.DebtType
import com.example.ui.dialogs.CreateDebtDialog
import com.example.ui.dialogs.DeleteDebtConfirmDialog
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccountWallet
import com.example.data.model.AssetItem
import com.example.data.model.DebtItem
import com.example.util.ChartDataHelper
import com.example.util.ChartPeriod
import androidx.compose.ui.text.style.TextOverflow
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardSubtle
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.getTopHeaderGradient
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ExpenseRedSoft
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.IncomeGreenSoft
import com.example.ui.theme.SegmentAccountBlue
import com.example.ui.theme.SegmentAssetGreen
import com.example.ui.theme.SegmentDebtRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TextWhite
import com.example.ui.theme.TrophyYellow
import com.example.ui.theme.TrophyYellowLight
import com.example.ui.viewmodel.FinanceViewModel
import com.example.util.CurrencyHelper
import com.example.util.IndonesianInstitutionHelper
import com.example.util.InstitutionLogoBadge
import com.example.util.AssetLogoBadge
import com.example.util.ThousandsSeparatorVisualTransformation
import com.example.util.AssetIconHelper
import com.example.util.availableAssetCategories

@Composable
fun AccountScreen(
    viewModel: FinanceViewModel,
    onAddWalletClick: () -> Unit,
    onAddAssetClick: () -> Unit,
    onAddDebtClick: () -> Unit,
    onPayDebtClick: (DebtItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedWalletIdForDetail by viewModel.selectedWalletIdForDetail.collectAsState()
    val selectedAssetIdForDetail by viewModel.selectedAssetIdForDetail.collectAsState()
    val selectedDebtIdForDetail by viewModel.selectedDebtIdForDetail.collectAsState()
    var debtToEdit by remember { mutableStateOf<DebtItem?>(null) }
    var debtToDelete by remember { mutableStateOf<DebtItem?>(null) }

    if (selectedWalletIdForDetail != null) {
        AccountDetailScreen(
            walletId = selectedWalletIdForDetail!!,
            viewModel = viewModel,
            onBack = { viewModel.selectWalletForDetail(null) }
        )
        return
    }

    if (selectedAssetIdForDetail != null) {
        AssetDetailScreen(
            assetId = selectedAssetIdForDetail!!,
            viewModel = viewModel,
            onBack = { viewModel.selectAssetForDetail(null) }
        )
        return
    }

    if (selectedDebtIdForDetail != null) {
        DebtDetailScreen(
            debtId = selectedDebtIdForDetail!!,
            viewModel = viewModel,
            onBack = { viewModel.selectDebtForDetail(null) }
        )
        return
    }

    val subTab by viewModel.accountSubTab.collectAsState()
    val totalWalletBalance by viewModel.totalAllWalletsBalance.collectAsState()
    val totalAssets by viewModel.totalAssetsValue.collectAsState()
    val totalDebts by viewModel.totalDebtsValue.collectAsState()
    val totalLent by viewModel.totalLentValue.collectAsState()
    val netWorth by viewModel.netWorthValue.collectAsState()

    val wallets by viewModel.wallets.collectAsState()
    val assets by viewModel.assets.collectAsState()
    val debts by viewModel.debts.collectAsState()
    val transactions by viewModel.allTransactions.collectAsState()
    val isRefreshingStocks by viewModel.isRefreshingStockPrices.collectAsState()
    val isBalanceHidden by viewModel.isBalanceHidden.collectAsState()

    var selectedTimePeriod by remember { mutableStateOf("1M") }
    var selectedAssetCategory by remember { mutableStateOf("all") }

    var showAccountOptionsDialog by remember { mutableStateOf(false) }
    var showRetirementPlannerDialog by remember { mutableStateOf(false) }
    var showPayoffPlanDialog by remember { mutableStateOf(false) }
    var accountSortOrder by remember { mutableStateOf("DEFAULT") } // "DEFAULT", "HIGH", "LOW", "NAME"
    var hideZeroBalanceWallets by remember { mutableStateOf(false) }
    var walletViewMode by remember { mutableStateOf("grid") } // "grid" (kotak-kotak) or "list" (daftar)
    var assetViewMode by remember { mutableStateOf("grid") }  // "grid" (kotak-kotak) or "list" (daftar)
    var debtViewMode by remember { mutableStateOf("grid") }   // "grid" matching Gambar 1 or "list"

    val displayedWallets = remember(wallets, accountSortOrder, hideZeroBalanceWallets) {
        var list = if (hideZeroBalanceWallets) wallets.filter { it.balance > 0 } else wallets
        when (accountSortOrder) {
            "HIGH" -> list.sortedByDescending { it.balance }
            "LOW" -> list.sortedBy { it.balance }
            "NAME" -> list.sortedBy { it.name }
            else -> list
        }
    }

    val displayedAssets = remember(assets, selectedAssetCategory) {
        if (selectedAssetCategory == "all") assets
        else assets.filter {
            val cat = selectedAssetCategory.lowercase()
            val t = it.assetType.lowercase()
            val n = it.name.lowercase()
            val p = it.platform.lowercase()
            when (cat) {
                "saham" -> it.isStock || it.tickerSymbol.isNotBlank() || t.contains("saham") || n.contains("saham") || p.contains("stock") || n.contains("bbca") || n.contains("bbri")
                "emas" -> t.contains("emas") || n.contains("emas") || t.contains("gold") || p.contains("antam") || p.contains("pegadaian")
                "reksadana" -> t.contains("reksa") || n.contains("reksa") || p.contains("bibit") || p.contains("bareksa")
                "kripto" -> t.contains("kripto") || t.contains("crypto") || n.contains("btc") || n.contains("bitcoin") || p.contains("indodax") || p.contains("tokocrypto")
                "obligasi" -> t.contains("obligasi") || n.contains("sbn") || t.contains("bond")
                "deposito" -> t.contains("deposito") || t.contains("deposit")
                "properti" -> t.contains("properti") || n.contains("rumah") || n.contains("tanah")
                "bank" -> it.group == "Tunai & Bank" || t.contains("bank")
                else -> true
            }
        }
    }

    val currentChartPeriod = when (selectedTimePeriod) {
        "7D" -> ChartPeriod.SEVEN_DAYS
        "3M" -> ChartPeriod.THREE_MONTHS
        "YTD" -> ChartPeriod.ONE_YEAR
        "ALL" -> ChartPeriod.ALL
        else -> ChartPeriod.ONE_MONTH
    }

    val chartData = remember(wallets, assets, debts, transactions, subTab, currentChartPeriod) {
        ChartDataHelper.getNetWorthTrend(wallets, assets, debts, transactions, subTab, currentChartPeriod)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(getTopHeaderGradient(AccentBlue))
    ) {
        // Header Bar with status bar safe area for true fullscreen
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Akun",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.toggleBalanceVisibility() },
                    modifier = Modifier.testTag("btn_account_privacy_toggle")
                ) {
                    Icon(
                        imageVector = if (isBalanceHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Sembunyikan Saldo",
                        tint = TextWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = { showAccountOptionsDialog = true },
                    modifier = Modifier.testTag("btn_account_tune")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Pengaturan & Urutan Akun",
                        tint = TextWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Main Top Metric & Chart based on Sub-Tab
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val delta = chartData.deltaAmount
                    val isPositive = chartData.isPositive
                    val sign = if (delta >= 0) "+" else "-"
                    val absDelta = Math.abs(delta)
                    val formattedDelta = "${sign}${CurrencyHelper.formatRupiah(absDelta)}"
                    val formattedPercent = "${sign}${String.format(java.util.Locale.US, "%.1f", Math.abs(chartData.percentChange))}%"
                    val trendColor = if (isPositive) IncomeGreen else ExpenseRed
                    val trendIcon = if (isPositive) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown

                    when (subTab) {
                        0 -> {
                            // Nilai Bersih
                            Text(
                                text = if (isBalanceHidden) "Rp ••••••••" else CurrencyHelper.formatRupiah(netWorth),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = trendIcon,
                                    contentDescription = null,
                                    tint = trendColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$formattedDelta  $formattedPercent  $selectedTimePeriod",
                                    fontSize = 12.sp,
                                    color = trendColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Nilai Bersih", fontSize = 13.sp, color = TextMuted)
                            Text(
                                text = "📈 Proyeksi saldo sesuai transaksi: ${CurrencyHelper.formatRupiah(netWorth + delta)}",
                                fontSize = 11.sp,
                                color = TextSubtle
                            )
                        }
                        1 -> {
                            // Akun / Dompet
                            Text(
                                text = if (isBalanceHidden) "Rp ••••••••" else CurrencyHelper.formatRupiah(totalWalletBalance),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = trendIcon,
                                    contentDescription = null,
                                    tint = trendColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$formattedDelta  $formattedPercent  $selectedTimePeriod",
                                    fontSize = 12.sp,
                                    color = trendColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Total Saldo Tersedia · ${wallets.size} Dompet",
                                fontSize = 13.sp,
                                color = TextMuted
                            )
                        }
                        2 -> {
                            // Aset
                            Text(
                                text = if (isBalanceHidden) "Rp ••••••••" else CurrencyHelper.formatRupiah(totalAssets),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = trendIcon,
                                    contentDescription = null,
                                    tint = trendColor,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$formattedDelta  $formattedPercent  $selectedTimePeriod",
                                    fontSize = 12.sp,
                                    color = trendColor,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Total Nilai Aset", fontSize = 13.sp, color = TextMuted)
                        }
                        3 -> {
                            // Utang Header metrics matching Gambar 1
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFFFF6B4A)))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("DIPINJAM", fontSize = 11.sp, color = TextSubtle, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (isBalanceHidden) "Rp ••••••" else CurrencyHelper.formatRupiah(totalDebts),
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        val isDebtDown = chartData.deltaAmount <= 0
                                        val debtColor = if (isDebtDown) IncomeGreen else Color(0xFFFF6B4A)
                                        val debtArrow = if (isDebtDown) "▼" else "▲"
                                        Text(debtArrow, fontSize = 9.sp, color = debtColor)
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("${String.format(java.util.Locale.US, "%.1f", Math.abs(chartData.percentChange))}%  $selectedTimePeriod", fontSize = 11.sp, color = debtColor, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(IncomeGreen))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("DIPINJAMKAN", fontSize = 11.sp, color = TextSubtle, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (isBalanceHidden) "Rp ••••••" else CurrencyHelper.formatRupiah(totalLent),
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("·", fontSize = 9.sp, color = TextMuted)
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("${debts.count { it.type == DebtType.LENT }} Piutang", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Normal)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Time Period Selector
                    Row(
                        modifier = Modifier
                            .background(DarkSurfaceElevated, RoundedCornerShape(12.dp))
                            .padding(2.dp)
                    ) {
                        listOf("7D", "1M", "3M", "YTD", "ALL").forEach { period ->
                            val isSelected = selectedTimePeriod == period
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) AccentBlue else Color.Transparent,
                                modifier = Modifier.clickable { selectedTimePeriod = period }
                            ) {
                                Text(
                                    text = period,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else TextMuted,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    var selectedNetWorthPointIndex by remember { mutableStateOf<Int?>(null) }

                    // Trend Line Canvas with dynamic trend color, 4x4 dashed grid and interactive click/tap
                    NetWorthTrendCanvas(
                        points = chartData.points,
                        subTab = subTab,
                        trendDirection = chartData.trendDirection,
                        selectedIndex = selectedNetWorthPointIndex,
                        onPointSelected = { idx ->
                            selectedNetWorthPointIndex = if (selectedNetWorthPointIndex == idx) null else idx
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(chartData.startLabel, fontSize = 10.sp, color = TextSubtle)
                        Text(chartData.midLabel, fontSize = 10.sp, color = TextSubtle)
                        Text(chartData.endLabel, fontSize = 10.sp, color = TextSubtle)
                    }

                    // Interactive historical detail card for Net Worth Trend
                    if (selectedNetWorthPointIndex != null && selectedNetWorthPointIndex!! in chartData.rawValues.indices) {
                        val idx = selectedNetWorthPointIndex!!
                        val curVal = chartData.rawValues[idx]
                        val prevVal = if (idx > 0) chartData.rawValues[idx - 1] else null
                        val curLbl = chartData.pointLabels.getOrElse(idx) {
                            when (selectedTimePeriod) {
                                "7D" -> "Hari ke-${idx + 1}"
                                "3M" -> "Bulan ke-${idx + 1}"
                                "YTD", "ALL" -> "Bulan ke-${idx + 1}"
                                else -> "Tanggal ${idx + 1}"
                            }
                        }
                        val prevLbl = if (idx > 0) chartData.pointLabels.getOrElse(idx - 1) { "Titik ke-$idx" } else null
                        val subTabName = when (subTab) {
                            0 -> "Nilai Bersih"
                            1 -> "Saldo Akun"
                            2 -> "Valuasi Aset"
                            else -> "Utang"
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        ChartHistoricalDetailCard(
                            title = "Tren $subTabName",
                            currentLabel = curLbl,
                            currentValue = curVal,
                            previousLabel = prevLbl,
                            previousValue = prevVal,
                            isReverseGood = subTab == 3,
                            onDismiss = { selectedNetWorthPointIndex = null }
                        )
                    }
                }
            }


            // Content based on selected Sub-Tab
            when (subTab) {
                0 -> {
                    // Nilai Bersih View
                    item {
                        // Proportional Balance, Asset, and Debt Chart Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            border = BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.7f))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Three Summary Columns with Proportional Mini Bars
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Total Saldo", fontSize = 11.sp, color = TextSubtle)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            CurrencyHelper.formatRupiah(totalWalletBalance),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SegmentAccountBlue
                                        )
                                    }
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("Total Aset", fontSize = 11.sp, color = TextSubtle)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            CurrencyHelper.formatRupiah(totalAssets),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SegmentAssetGreen
                                        )
                                    }
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.End
                                    ) {
                                        Text("Total Utang", fontSize = 11.sp, color = TextSubtle)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            "-${CurrencyHelper.formatRupiah(totalDebts)}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SegmentDebtRed
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Calculate true mathematical proportions
                                val safeWallet = totalWalletBalance.coerceAtLeast(0.0)
                                val safeAssets = totalAssets.coerceAtLeast(0.0)
                                val safeDebts = totalDebts.coerceAtLeast(0.0)
                                val grossTotal = safeWallet + safeAssets + safeDebts

                                val wRatio = if (grossTotal > 0 && safeWallet > 0) (safeWallet / grossTotal).toFloat() else 0f
                                val aRatio = if (grossTotal > 0 && safeAssets > 0) (safeAssets / grossTotal).toFloat() else 0f
                                val dRatio = if (grossTotal > 0 && safeDebts > 0) (safeDebts / grossTotal).toFloat() else 0f

                                val wPct = if (grossTotal > 0) ((safeWallet / grossTotal) * 100).toInt() else 0
                                val aPct = if (grossTotal > 0) ((safeAssets / grossTotal) * 100).toInt() else 0
                                val dPct = if (grossTotal > 0 && safeDebts > 0) (100 - wPct - aPct).coerceAtLeast(0) else 0

                                // Consolidated Proportional Multi-Segment Bar (Full Width & Proportional)
                                if (grossTotal <= 0.0) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(DarkSurfaceElevated)
                                    )
                                } else {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                    ) {
                                        if (wRatio > 0f) {
                                            Box(
                                                modifier = Modifier
                                                    .weight(wRatio)
                                                    .fillMaxHeight()
                                                    .background(SegmentAccountBlue)
                                            )
                                        }
                                        if (wRatio > 0f && (aRatio > 0f || dRatio > 0f)) {
                                            Spacer(modifier = Modifier.width(2.dp))
                                        }
                                        if (aRatio > 0f) {
                                            Box(
                                                modifier = Modifier
                                                    .weight(aRatio)
                                                    .fillMaxHeight()
                                                    .background(SegmentAssetGreen)
                                            )
                                        }
                                        if (aRatio > 0f && dRatio > 0f) {
                                            Spacer(modifier = Modifier.width(2.dp))
                                        }
                                        if (dRatio > 0f) {
                                            Box(
                                                modifier = Modifier
                                                    .weight(dRatio)
                                                    .fillMaxHeight()
                                                    .background(SegmentDebtRed)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Proportional Legend with indicators spanning full width
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(SegmentAccountBlue))
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text("Saldo $wPct%", fontSize = 11.sp, color = TextSubtle, fontWeight = FontWeight.Medium)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(SegmentAssetGreen))
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text("Aset $aPct%", fontSize = 11.sp, color = TextSubtle, fontWeight = FontWeight.Medium)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(SegmentDebtRed))
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text("Utang $dPct%", fontSize = 11.sp, color = TextSubtle, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                    }

                    // Monthly Net Worth Bar Chart Card - Dynamic and changes according to actual data
                    item {
                        val monthlyBars = remember(wallets, assets, debts, transactions) {
                            ChartDataHelper.getMonthlyNetWorthBars(wallets, assets, debts, transactions)
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            border = BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.7f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "Riwayat Nilai Bersih",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextWhite
                                        )
                                        Text(
                                            text = "Perkembangan saldo 6 bulan terakhir",
                                            fontSize = 11.sp,
                                            color = TextSubtle
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = DarkSurfaceElevated
                                    ) {
                                        Text(
                                            text = "Bulanan",
                                            fontSize = 10.sp,
                                            color = TextMuted,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                var selectedMonthlyBarIndex by remember { mutableStateOf<Int?>(null) }

                                // Monthly bar chart with 4x4 dashed grid lines and click/tap interactivity
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(118.dp)
                                ) {
                                    // Dashed 3x3 grid background
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        with(ChartDataHelper) {
                                            drawDashedChartGrid(
                                                horizontalLines = 3,
                                                verticalLines = 3,
                                                gridColor = Color(0xFF334155).copy(alpha = 0.45f),
                                                drawFrame = true
                                            )
                                        }
                                    }

                                    // Dynamic Interactive Bars
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 8.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        monthlyBars.forEachIndexed { idx, bar ->
                                            val isSelected = selectedMonthlyBarIndex == idx
                                            val barColor = ChartDataHelper.getTrendColor(bar.trendDirection)
                                            val barHeight = (bar.heightRatio * 68).dp.coerceAtLeast(6.dp)

                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .clickable {
                                                        selectedMonthlyBarIndex = if (selectedMonthlyBarIndex == idx) null else idx
                                                    }
                                                    .padding(vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = bar.formattedValue,
                                                    fontSize = 8.sp,
                                                    color = if (isSelected) Color(0xFF38BDF8) else barColor,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1
                                                )
                                                Spacer(modifier = Modifier.height(3.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .width(if (isSelected) 30.dp else 26.dp)
                                                        .height(barHeight)
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(barColor)
                                                        .then(
                                                            if (isSelected) Modifier.border(
                                                                1.5.dp,
                                                                Color(0xFF38BDF8),
                                                                RoundedCornerShape(4.dp)
                                                            ) else Modifier
                                                        )
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = bar.monthName,
                                                    fontSize = 10.sp,
                                                    color = if (isSelected) Color(0xFF38BDF8) else TextMuted,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                }

                                // Interactive Historical Detail Card for Monthly Net Worth Bar
                                if (selectedMonthlyBarIndex != null && selectedMonthlyBarIndex!! in monthlyBars.indices) {
                                    val selIdx = selectedMonthlyBarIndex!!
                                    val selBar = monthlyBars[selIdx]
                                    val prevBar = if (selIdx > 0) monthlyBars[selIdx - 1] else null

                                    Spacer(modifier = Modifier.height(10.dp))
                                    ChartHistoricalDetailCard(
                                        title = "Riwayat Nilai Bersih",
                                        currentLabel = selBar.monthName,
                                        currentValue = selBar.value,
                                        previousLabel = prevBar?.monthName,
                                        previousValue = prevBar?.value,
                                        onDismiss = { selectedMonthlyBarIndex = null }
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                val lastBar = monthlyBars.lastOrNull()
                                val prevBar = if (monthlyBars.size >= 2) monthlyBars[monthlyBars.size - 2] else null
                                val recentDelta = if (lastBar != null && prevBar != null) lastBar.value - prevBar.value else 0.0
                                val isGrowing = recentDelta >= 0
                                val trendSummaryColor = if (isGrowing) IncomeGreen else ExpenseRed

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = DarkSurfaceElevated,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isGrowing) IncomeGreenSoft else ExpenseRedSoft,
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = if (isGrowing) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                                                    contentDescription = null,
                                                    tint = trendSummaryColor,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = if (isGrowing) "Nilai bersih sedang meningkat" else "Nilai bersih mengalami penyesuaian",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = TextWhite
                                            )
                                            val deltaStr = CurrencyHelper.formatRupiah(Math.abs(recentDelta))
                                            Text(
                                                text = if (isGrowing) "+$deltaStr dibanding bulan sebelumnya" else "-$deltaStr dibanding bulan sebelumnya",
                                                fontSize = 11.sp,
                                                color = trendSummaryColor
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Total Utang Ratio Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AccountBalance,
                                            contentDescription = null,
                                            tint = TrophyYellow,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Total Utang",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextWhite
                                        )
                                    }
                                    Text(
                                        text = "49.6%",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TrophyYellowLight
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                LinearProgressIndicator(
                                    progress = { 0.496f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = TrophyYellow,
                                    trackColor = DarkSurfaceElevated
                                )

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Moderate debt ratio — manageable but watch it",
                                    fontSize = 11.sp,
                                    color = TextSubtle
                                )
                            }
                        }
                    }

                    // Combined Breakdown List
                    item {
                        Text(
                            text = "Akun  ${CurrencyHelper.formatRupiah(totalWalletBalance)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    items(displayedWallets) { wallet ->
                        WalletMiniRow(
                            wallet = wallet,
                            onClick = { viewModel.selectWalletForDetail(wallet.id) }
                        )
                    }

                    item {
                        Text(
                            text = "Aset  ${CurrencyHelper.formatRupiah(totalAssets)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    items(assets) { asset ->
                        AssetMiniRow(asset, onClick = { viewModel.selectAssetForDetail(asset.id) })
                    }

                    item {
                        Text(
                            text = "Utang  -${CurrencyHelper.formatRupiah(totalDebts)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    items(debts) { debt ->
                        DebtMiniRow(debt)
                    }

                    // Retirement Planner Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showRetirementPlannerDialog = true }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0x332563EB),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Umbrella,
                                            contentDescription = null,
                                            tint = AccentBlue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Perencana pensiun",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                    Text(
                                        text = "Proyeksikan tabunganmu hingga usia pensiun",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = TextMuted
                                )
                            }
                        }
                    }
                }
                1 -> {
                    // Akun (Wallets) View (Kotak-kotak / Grid vs Daftar / List)
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Dompet (${displayedWallets.size})",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )

                            // View Mode Toggle (Grid / List)
                            Row(
                                modifier = Modifier
                                    .background(DarkSurfaceElevated, RoundedCornerShape(10.dp))
                                    .border(1.dp, DarkCardBorder, RoundedCornerShape(10.dp))
                                    .padding(2.dp),
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (walletViewMode == "grid") AccentBlue else Color.Transparent,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { walletViewMode = "grid" }
                                ) {
                                    Box(
                                        modifier = Modifier.size(32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.GridView,
                                            contentDescription = "Tampilan Kotak",
                                            tint = if (walletViewMode == "grid") Color.White else TextMuted,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (walletViewMode == "list") AccentBlue else Color.Transparent,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { walletViewMode = "list" }
                                ) {
                                    Box(
                                        modifier = Modifier.size(32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ViewList,
                                            contentDescription = "Tampilan Daftar",
                                            tint = if (walletViewMode == "list") Color.White else TextMuted,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (walletViewMode == "grid") {
                        // 2x2 Grid of Wallets with matching compact sizing as Asset grid
                        items(displayedWallets.chunked(2)) { pair ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                pair.forEach { wallet ->
                                    val percent = if (totalWalletBalance > 0) ((wallet.balance / totalWalletBalance) * 100).toInt() else 0
                                    val inst = IndonesianInstitutionHelper.resolveInstitution(wallet.name, wallet.iconName)
                                    val typeLabel = wallet.bankType.split("·").firstOrNull()?.trim() ?: wallet.bankType
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { viewModel.selectWalletForDetail(wallet.id) },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                        border = BorderStroke(1.dp, inst.brandColor.copy(alpha = 0.35f))
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                InstitutionLogoBadge(
                                                    walletName = wallet.name,
                                                    iconName = wallet.iconName,
                                                    size = 34.dp
                                                )
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = DarkSurfaceElevated,
                                                    border = BorderStroke(0.8.dp, DarkCardBorder)
                                                ) {
                                                    Text(
                                                        text = typeLabel,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        color = TextMuted,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                        maxLines = 1
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(6.dp))

                                            Text(
                                                text = wallet.name,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextWhite,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = "$percent% • $typeLabel",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = AccentBlue,
                                                maxLines = 1
                                            )

                                            Spacer(modifier = Modifier.height(2.dp))

                                            Text(
                                                text = CurrencyHelper.formatRupiah(wallet.balance),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextWhite,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                                if (pair.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    } else {
                        // Daftar (List View) of Wallets
                        items(displayedWallets) { wallet ->
                            val percent = if (totalWalletBalance > 0) ((wallet.balance / totalWalletBalance) * 100).toInt() else 0
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.selectWalletForDetail(wallet.id) },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                border = BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        InstitutionLogoBadge(
                                            walletName = wallet.name,
                                            iconName = wallet.iconName,
                                            size = 36.dp
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = wallet.name,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextWhite
                                            )
                                            Text(
                                                text = if (wallet.lastAmount > 0) "${wallet.bankType} · ${wallet.lastCategory}" else wallet.bankType,
                                                fontSize = 11.sp,
                                                color = TextSubtle
                                            )
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = CurrencyHelper.formatRupiah(wallet.balance),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextWhite
                                            )
                                            Text(
                                                text = "$percent% dari total",
                                                fontSize = 10.sp,
                                                color = AccentBlue,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                            contentDescription = null,
                                            tint = TextMuted,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // Aset View (Kotak-kotak / Grid vs Daftar / List)
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Daftar Aset & Investasi",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )

                            // View Mode Toggle (Grid / List)
                            Row(
                                modifier = Modifier
                                    .background(DarkSurfaceElevated, RoundedCornerShape(10.dp))
                                    .border(1.dp, DarkCardBorder, RoundedCornerShape(10.dp))
                                    .padding(2.dp),
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (assetViewMode == "grid") AccentBlue else Color.Transparent,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { assetViewMode = "grid" }
                                ) {
                                    Box(
                                        modifier = Modifier.size(32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.GridView,
                                            contentDescription = "Tampilan Kotak",
                                            tint = if (assetViewMode == "grid") Color.White else TextMuted,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (assetViewMode == "list") AccentBlue else Color.Transparent,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { assetViewMode = "list" }
                                ) {
                                    Box(
                                        modifier = Modifier.size(32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ViewList,
                                            contentDescription = "Tampilan Daftar",
                                            tint = if (assetViewMode == "list") Color.White else TextMuted,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Horizontal Category Selector (Semua, Saham, Emas, Reksadana, Kripto, Obligasi, dll.)
                    item {
                        val assetScrollState = rememberScrollState()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(assetScrollState)
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            availableAssetCategories.forEach { cat ->
                                val isSelected = selectedAssetCategory == cat.id
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isSelected) cat.color else DarkSurfaceElevated,
                                    border = BorderStroke(1.dp, if (isSelected) cat.color else DarkCardBorder),
                                    modifier = Modifier.clickable { selectedAssetCategory = cat.id }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = cat.icon,
                                            contentDescription = cat.name,
                                            tint = if (isSelected) Color.White else cat.color,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = cat.name,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else TextWhite
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        if (selectedAssetCategory == "saham") {
                            // Dedicated Real-Time Stock Portfolio Card
                            val stockAssets = displayedAssets.filter { it.isStock || it.tickerSymbol.isNotBlank() }
                            val totalRealValue = stockAssets.sumOf { it.realValue }
                            val totalCostBasis = stockAssets.sumOf { it.costBasis }
                            val totalGL = totalRealValue - totalCostBasis
                            val totalGLPercent = if (totalCostBasis > 0.0) (totalGL / totalCostBasis) * 100.0 else 0.0
                            val isGain = totalGL >= 0

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                border = BorderStroke(1.dp, AccentBlue.copy(alpha = 0.45f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                shape = RoundedCornerShape(10.dp),
                                                color = AccentBlue.copy(alpha = 0.2f),
                                                modifier = Modifier.size(34.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        imageVector = Icons.Default.CandlestickChart,
                                                        contentDescription = null,
                                                        tint = AccentBlue,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = "Portofolio Saham IHSG",
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextWhite
                                                )
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(6.dp)
                                                            .clip(CircleShape)
                                                            .background(Color(0xFF10B981))
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(
                                                        text = "TradingView Real-Time",
                                                        fontSize = 11.sp,
                                                        color = Color(0xFF34D399),
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            }
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = AccentBlue,
                                            modifier = Modifier.clickable { viewModel.refreshStockPrices() }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Refresh,
                                                    contentDescription = "Sync Live",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = if (isRefreshingStocks) "Sinkron..." else "Perbarui Live",
                                                    fontSize = 11.sp,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Text(
                                        text = "Total Valuasi Portofolio Real-Time",
                                        fontSize = 11.5.sp,
                                        color = TextMuted
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = CurrencyHelper.formatRupiah(totalRealValue),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextWhite
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // 3 Columns: Modal Beli, Unrealized P/L, Posisi
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = DarkSurfaceElevated,
                                            border = BorderStroke(0.8.dp, DarkCardBorder),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Column(modifier = Modifier.padding(9.dp)) {
                                                Text(text = "Modal (Avg)", fontSize = 10.5.sp, color = TextMuted)
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = CurrencyHelper.formatRupiah(totalCostBasis),
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextWhite
                                                )
                                            }
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = DarkSurfaceElevated,
                                            border = BorderStroke(0.8.dp, DarkCardBorder),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Column(modifier = Modifier.padding(9.dp)) {
                                                Text(text = "Untung / Rugi", fontSize = 10.5.sp, color = TextMuted)
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = "${if (isGain) "+" else ""}${CurrencyHelper.formatRupiah(totalGL)}",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isGain) IncomeGreen else ExpenseRed
                                                )
                                                Text(
                                                    text = "${String.format(java.util.Locale.US, "%+.2f", totalGLPercent)}%",
                                                    fontSize = 9.5.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = if (isGain) IncomeGreen else ExpenseRed
                                                )
                                            }
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = DarkSurfaceElevated,
                                            border = BorderStroke(0.8.dp, DarkCardBorder),
                                            modifier = Modifier.weight(0.9f)
                                        ) {
                                            Column(modifier = Modifier.padding(9.dp)) {
                                                Text(text = "Posisi", fontSize = 10.5.sp, color = TextMuted)
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = "${stockAssets.size} Emiten",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = AccentBlue
                                                )
                                                Text(
                                                    text = "${stockAssets.sumOf { it.lotCount.toInt() }} Lot",
                                                    fontSize = 9.5.sp,
                                                    color = TextMuted
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(top = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = TextMuted,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Nilai real portofolio bergerak otomatis mengikuti harga pasar terkini dari TradingView.",
                                            fontSize = 10.5.sp,
                                            color = TextMuted
                                        )
                                    }
                                }
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurfaceElevated,
                                border = BorderStroke(0.8.dp, DarkCardBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(7.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF10B981))
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Harga Saham Live TradingView",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF34D399)
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = AccentBlue.copy(alpha = 0.2f),
                                        border = BorderStroke(0.6.dp, AccentBlue),
                                        modifier = Modifier.clickable { viewModel.refreshStockPrices() }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = "Sync",
                                                tint = AccentBlue,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (isRefreshingStocks) "Sinkron..." else "Perbarui Live",
                                                fontSize = 10.sp,
                                                color = AccentBlue,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (selectedAssetCategory == "all") {
                        val cashAssets = displayedAssets.filter { it.group == "Tunai & Bank" }
                        val investAssets = displayedAssets.filter { it.group == "Investasi" }

                        if (cashAssets.isNotEmpty()) {
                            val sumCash = cashAssets.sumOf { it.realValue }
                            item {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SegmentAccountBlue))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Tunai & Bank (${cashAssets.size})  ${CurrencyHelper.formatRupiah(sumCash)}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextWhite
                                    )
                                }
                            }
                            if (assetViewMode == "grid") {
                                items(cashAssets.chunked(2)) { pair ->
                                    AssetGridRow(pair, totalAssets, onAssetClick = { viewModel.selectAssetForDetail(it.id) })
                                }
                            } else {
                                items(cashAssets) { asset ->
                                    AssetListCard(asset, totalAssets, onAssetClick = { viewModel.selectAssetForDetail(it.id) })
                                }
                            }
                        }

                        if (investAssets.isNotEmpty()) {
                            val sumInvest = investAssets.sumOf { it.realValue }
                            item {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(SegmentAssetGreen))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Investasi (${investAssets.size})  ${CurrencyHelper.formatRupiah(sumInvest)}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextWhite
                                    )
                                }
                            }
                            if (assetViewMode == "grid") {
                                items(investAssets.chunked(2)) { pair ->
                                    AssetGridRow(pair, totalAssets, onAssetClick = { viewModel.selectAssetForDetail(it.id) })
                                }
                            } else {
                                items(investAssets) { asset ->
                                    AssetListCard(asset, totalAssets, onAssetClick = { viewModel.selectAssetForDetail(it.id) })
                                }
                            }
                        }
                    } else {
                        val sumFiltered = displayedAssets.sumOf { it.realValue }
                        val activeCatObj = availableAssetCategories.find { it.id == selectedAssetCategory }
                        item {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(activeCatObj?.color ?: SegmentAssetGreen))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${activeCatObj?.name ?: "Aset"} (${displayedAssets.size})  ${CurrencyHelper.formatRupiah(sumFiltered)}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextWhite
                                )
                            }
                        }
                        if (assetViewMode == "grid") {
                            items(displayedAssets.chunked(2)) { pair ->
                                AssetGridRow(pair, totalAssets, onAssetClick = { viewModel.selectAssetForDetail(it.id) })
                            }
                        } else {
                            items(displayedAssets) { asset ->
                                AssetListCard(asset, totalAssets, onAssetClick = { viewModel.selectAssetForDetail(it.id) })
                            }
                        }
                    }
                }
                3 -> {
                    // Utang Content matching Gambar 1
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp, bottom = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Utang",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Buat Utang button at top of Utang menu
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = AccentBlue,
                                    modifier = Modifier
                                        .clickable { onAddDebtClick() }
                                        .testTag("btn_top_header_add_debt")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Buat",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Payoff plan button matching Gambar 1
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF1E293B),
                                    border = BorderStroke(1.dp, Color(0xFF334155)),
                                    modifier = Modifier
                                        .clickable { showPayoffPlanDialog = true }
                                        .testTag("btn_payoff_plan")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Flag,
                                            contentDescription = null,
                                            tint = Color(0xFF3B82F6),
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = "Payoff plan",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF60A5FA)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // View Mode Toggle (Grid vs List) matching Gambar 1
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFF1E293B),
                                    border = BorderStroke(1.dp, Color(0xFF334155))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (debtViewMode == "grid") Color(0xFF2563EB) else Color.Transparent,
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clickable { debtViewMode = "grid" }
                                                .testTag("btn_toggle_debt_grid")
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.GridView,
                                                    contentDescription = "Tampilan Grid",
                                                    tint = if (debtViewMode == "grid") Color.White else TextMuted,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (debtViewMode == "list") Color(0xFF2563EB) else Color.Transparent,
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clickable { debtViewMode = "list" }
                                                .testTag("btn_toggle_debt_list")
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.ViewList,
                                                    contentDescription = "Tampilan Daftar",
                                                    tint = if (debtViewMode == "list") Color.White else TextMuted,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    val borrowedDebts = debts.filter { it.type == DebtType.BORROWED }
                    val lentDebts = debts.filter { it.type == DebtType.LENT }

                    if (debts.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                                border = BorderStroke(1.dp, DarkCardBorder)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Handshake,
                                        contentDescription = null,
                                        tint = AccentBlue,
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Belum Ada Catatan Utang",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Catat pinjaman, kartu kredit, atau piutang dengan rapi.",
                                        fontSize = 12.sp,
                                        color = TextMuted,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Button(
                                        onClick = onAddDebtClick,
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                                    ) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Buat Utang Sekarang", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Section 1: DIPINJAM
                    if (borrowedDebts.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(14.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(Color(0xFFFF5722))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "DIPINJAM (${borrowedDebts.size})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF5722)
                                )
                            }
                        }

                        if (debtViewMode == "grid") {
                            val chunked = borrowedDebts.chunked(2)
                            items(chunked) { pair ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    pair.forEach { debt ->
                                        DebtGridCard(
                                            debt = debt,
                                            modifier = Modifier.weight(1f),
                                            onClick = { viewModel.selectDebtForDetail(debt.id) }
                                        )
                                    }
                                    if (pair.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        } else {
                            items(borrowedDebts) { debt ->
                                DebtListCard(
                                    debt = debt,
                                    onClick = { viewModel.selectDebtForDetail(debt.id) }
                                )
                            }
                        }
                    }

                    // Section 2: DIPINJAMKAN
                    if (lentDebts.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(14.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(IncomeGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "DIPINJAMKAN (${lentDebts.size})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = IncomeGreen
                                )
                            }
                        }

                        if (debtViewMode == "grid") {
                            val chunked = lentDebts.chunked(2)
                            items(chunked) { pair ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    pair.forEach { debt ->
                                        DebtGridCard(
                                            debt = debt,
                                            modifier = Modifier.weight(1f),
                                            onClick = { viewModel.selectDebtForDetail(debt.id) }
                                        )
                                    }
                                    if (pair.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        } else {
                            items(lentDebts) { debt ->
                                DebtListCard(
                                    debt = debt,
                                    onClick = { viewModel.selectDebtForDetail(debt.id) }
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Sub-Tabs Toolbar: Nilai Bersih, Akun, Aset, Utang (ukuran pendek / floating compact pill)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = Color(0xFF131722),
                border = BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.75f)),
                modifier = Modifier
                    .widthIn(min = 260.dp, max = 300.dp)
                    .padding(horizontal = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tab 0: Nilai Bersih (3 Batang vertikal + panah kuning melengkung naik)
                    AccountSubTabItem(
                        title = "Nilai Bersih",
                        isSelected = subTab == 0,
                        activeColor = AccentBlue,
                        onClick = { viewModel.setAccountSubTab(0) },
                        modifier = Modifier.weight(1f),
                        icon = {
                            NilaiBersihTwoToneNavIcon(
                                themeColor = if (subTab == 0) Color.White else AccentBlue,
                                isSelected = subTab == 0,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )

                    // Tab 1: Akun (Dompet dengan koin emas menonjol di atas)
                    AccountSubTabItem(
                        title = "Akun",
                        isSelected = subTab == 1,
                        activeColor = AccentBlue,
                        onClick = { viewModel.setAccountSubTab(1) },
                        modifier = Modifier.weight(1f),
                        icon = {
                            AkunTwoToneNavIcon(
                                themeColor = if (subTab == 1) Color.White else AccentBlue,
                                isSelected = subTab == 1,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )

                    // Tab 2: Aset (Berlian faceted 3D dengan bintang berkilau 4-titik di kanan atas)
                    AccountSubTabItem(
                        title = "Aset",
                        isSelected = subTab == 2,
                        activeColor = AccentBlue,
                        onClick = { viewModel.setAccountSubTab(2) },
                        modifier = Modifier.weight(1f),
                        icon = {
                            AsetTwoToneNavIcon(
                                themeColor = if (subTab == 2) Color.White else AccentBlue,
                                isSelected = subTab == 2,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )

                    // Tab 3: Utang (Jabat tangan handshake dengan koin bulat di atas tengah)
                    AccountSubTabItem(
                        title = "Utang",
                        isSelected = subTab == 3,
                        activeColor = AccentBlue,
                        onClick = { viewModel.setAccountSubTab(3) },
                        modifier = Modifier.weight(1f),
                        icon = {
                            UtangTwoToneNavIcon(
                                themeColor = if (subTab == 3) Color.White else AccentBlue,
                                isSelected = subTab == 3,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    )
                }
            }
        }

        // Edit Debt Dialog
        debtToEdit?.let { debt ->
            CreateDebtDialog(
                existingDebt = debt,
                viewModel = viewModel,
                onDismiss = { debtToEdit = null }
            )
        }

        // Payoff Plan Dialog
        if (showPayoffPlanDialog) {
            PayoffPlanDialog(
                debts = debts,
                onDismiss = { showPayoffPlanDialog = false }
            )
        }

        // Delete Debt Confirmation Dialog
        debtToDelete?.let { debt ->
            AlertDialog(
                onDismissRequest = { debtToDelete = null },
                containerColor = DarkSurface,
                title = { Text("Hapus Utang?", color = TextWhite, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "Apakah Anda yakin ingin menghapus data utang \"${debt.name}\"?",
                        color = TextMuted
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteDebt(debt.id)
                            debtToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
                    ) {
                        Text("Hapus", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { debtToDelete = null }) {
                        Text("Batal", color = TextSubtle)
                    }
                }
            )
        }

        // Account Options Dialog (Sort & Filter)
        if (showAccountOptionsDialog) {
            AccountOptionsDialog(
                currentSort = accountSortOrder,
                hideZero = hideZeroBalanceWallets,
                onApply = { sort, hideZero ->
                    accountSortOrder = sort
                    hideZeroBalanceWallets = hideZero
                    showAccountOptionsDialog = false
                },
                onDismiss = { showAccountOptionsDialog = false }
            )
        }

        // Retirement Planner Dialog
        if (showRetirementPlannerDialog) {
            RetirementPlannerDialog(
                currentNetWorth = netWorth,
                totalAssets = totalAssets,
                onDismiss = { showRetirementPlannerDialog = false }
            )
        }
    }
}

@Composable
fun AccountOptionsDialog(
    currentSort: String,
    hideZero: Boolean,
    onApply: (String, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedSort by remember { mutableStateOf(currentSort) }
    var hideZeroBalance by remember { mutableStateOf(hideZero) }

    val sortOptions = listOf(
        Pair("DEFAULT", "Default (Bawaan)"),
        Pair("HIGH", "Saldo Tertinggi ke Terendah"),
        Pair("LOW", "Saldo Terendah ke Tertinggi"),
        Pair("NAME", "Nama Akun (A - Z)")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = AccentBlue,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pengaturan Akun & Dompet",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Urutkan Dompet Berdasarkan:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSubtle
                )

                sortOptions.forEach { (key, label) ->
                    val isSelected = selectedSort == key
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) Color(0x332563EB) else DarkSurfaceElevated,
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, AccentBlue) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedSort = key }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                color = if (isSelected) Color.White else TextMuted,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = AccentBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurfaceElevated,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Sembunyikan Saldo Rp0",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextWhite
                            )
                            Text(
                                text = "Hanya tampilkan dompet yang memiliki saldo",
                                fontSize = 10.sp,
                                color = TextSubtle
                            )
                        }
                        Switch(
                            checked = hideZeroBalance,
                            onCheckedChange = { hideZeroBalance = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = AccentBlue
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onApply(selectedSort, hideZeroBalance) },
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                Text("Terapkan", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = TextSubtle)
            }
        }
    )
}

@Composable
fun RetirementPlannerDialog(
    currentNetWorth: Double,
    totalAssets: Double,
    onDismiss: () -> Unit
) {
    var currentAgeText by remember { mutableStateOf("26") }
    var retirementAgeText by remember { mutableStateOf("55") }
    var monthlyExpenseText by remember { mutableStateOf("10000000") }

    val currentAge = currentAgeText.toIntOrNull() ?: 26
    val retirementAge = retirementAgeText.toIntOrNull() ?: 55
    val monthlyExpense = monthlyExpenseText.toDoubleOrNull() ?: 10_000_000.0

    val yearsLeft = (retirementAge - currentAge).coerceAtLeast(1)
    val monthsLeft = yearsLeft * 12
    val targetRetirementFund = monthlyExpense * 12 * 25 // 4% rule (25x annual expenses)
    val totalAvailable = (currentNetWorth + totalAssets).coerceAtLeast(0.0)
    val shortfall = (targetRetirementFund - totalAvailable).coerceAtLeast(0.0)
    val monthlySavingsNeeded = if (monthsLeft > 0) shortfall / monthsLeft else 0.0
    val progressPercent = if (targetRetirementFund > 0) ((totalAvailable / targetRetirementFund) * 100).toInt().coerceIn(0, 100) else 0

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Umbrella,
                    contentDescription = null,
                    tint = AccentBlue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Perencana Pensiun & FIRE",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = currentAgeText,
                        onValueChange = { if (it.length <= 2) currentAgeText = it.filter { c -> c.isDigit() } },
                        label = { Text("Usia Sekarang", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = DarkCardBorder
                        )
                    )
                    OutlinedTextField(
                        value = retirementAgeText,
                        onValueChange = { if (it.length <= 2) retirementAgeText = it.filter { c -> c.isDigit() } },
                        label = { Text("Target Pensiun", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = DarkCardBorder
                        )
                    )
                }

                OutlinedTextField(
                    value = monthlyExpenseText,
                    onValueChange = { if (it.length <= 11) monthlyExpenseText = it.filter { c -> c.isDigit() } },
                    label = { Text("Estimasi Pengeluaran / Bulan", fontSize = 11.sp) },
                    visualTransformation = ThousandsSeparatorVisualTransformation('.'),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = DarkCardBorder
                    )
                )

                // Results Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DarkSurfaceElevated,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Target Dana Pensiun (Aturan 4%)", fontSize = 11.sp, color = TextSubtle)
                            Text(
                                text = CurrencyHelper.formatRupiah(targetRetirementFund),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TrophyYellowLight
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Aset & Tabungan Saat Ini", fontSize = 11.sp, color = TextSubtle)
                            Text(
                                text = CurrencyHelper.formatRupiah(totalAvailable),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = IncomeGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { progressPercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = AccentBlue,
                            trackColor = DarkBackground
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Kesiapan: $progressPercent%", fontSize = 11.sp, color = TextMuted)
                            Text("Waktu: $yearsLeft tahun lagi", fontSize = 11.sp, color = TextMuted)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0x332563EB),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "🎯 Rekomendasi Menabung:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF93C5FD)
                                )
                                Text(
                                    text = "Sisihkan ${CurrencyHelper.formatRupiah(monthlySavingsNeeded)} per bulan untuk mencapai kebebasan finansial tepat waktu.",
                                    fontSize = 11.sp,
                                    color = TextWhite
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                Text("Selesai", color = Color.White)
            }
        }
    )
}

@Composable
fun WalletMiniRow(wallet: AccountWallet, onClick: () -> Unit = {}) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = DarkCardSubtle,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                InstitutionLogoBadge(
                    walletName = wallet.name,
                    iconName = wallet.iconName,
                    size = 24.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(wallet.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
            }
            Text(
                text = CurrencyHelper.formatRupiah(wallet.balance),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
        }
    }
}

@Composable
fun AssetMiniRow(
    asset: AssetItem,
    onClick: () -> Unit = {}
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = DarkCardSubtle,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AssetLogoBadge(
                    assetType = asset.assetType,
                    assetName = asset.name,
                    platform = asset.platform,
                    iconUri = asset.iconUri,
                    size = 28.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(asset.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                    Text(
                        text = if (asset.isStock && asset.lotCount > 0) {
                            "${String.format(java.util.Locale.US, "%.0f", asset.lotCount)} Lot • ${asset.platform}"
                        } else {
                            "${asset.assetType} • ${asset.platform}"
                        },
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }
            Text(
                text = CurrencyHelper.formatRupiah(asset.realValue),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
        }
    }
}

@Composable
fun DebtMiniRow(debt: DebtItem) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = DarkCardSubtle,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ExpenseRedSoft,
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null,
                            tint = ExpenseRed,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(debt.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
            }
            Text(
                text = "-${CurrencyHelper.formatRupiah(debt.totalAmount - debt.paidAmount)}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = ExpenseRed
            )
        }
    }
}

@Composable
fun AssetGridRow(
    pair: List<AssetItem>,
    totalAssets: Double,
    onAssetClick: (AssetItem) -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        pair.forEach { asset ->
            val percent = if (totalAssets > 0) String.format(java.util.Locale.US, "%.1f", (asset.value / totalAssets) * 100) else "0"
            val profile = AssetIconHelper.getAssetProfile(asset.assetType, asset.name, asset.platform)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onAssetClick(asset) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = BorderStroke(1.dp, profile.primaryColor.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AssetLogoBadge(
                            assetType = asset.assetType,
                            assetName = asset.name,
                            platform = asset.platform,
                            iconUri = asset.iconUri,
                            size = 34.dp
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = DarkSurfaceElevated,
                            border = BorderStroke(0.8.dp, DarkCardBorder)
                        ) {
                            Text(
                                text = asset.platform,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextMuted,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = asset.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        maxLines = 1
                    )
                    Text(
                        text = "$percent% • ${asset.assetType}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = profile.primaryColor
                    )

                    if (asset.isStock && asset.lotCount > 0) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = AccentBlue.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "${String.format(java.util.Locale.US, "%.0f", asset.lotCount)} Lot",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentBlue,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Real ${CurrencyHelper.formatRupiah(if (asset.currentPrice > 0) asset.currentPrice else asset.averagePrice)}",
                                fontSize = 9.sp,
                                color = TextMuted,
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            CurrencyHelper.formatRupiah(asset.realValue),
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        if (asset.isStock && asset.averagePrice > 0 && asset.currentPrice > 0) {
                            val glPercent = asset.calculatedPerformancePercent
                            val isGain = glPercent >= 0
                            Text(
                                text = "${if (isGain) "+" else ""}${String.format(java.util.Locale.US, "%.1f", glPercent)}%",
                                fontSize = 10.sp,
                                color = if (isGain) IncomeGreen else ExpenseRed,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
        if (pair.size == 1) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun AssetListCard(
    asset: AssetItem,
    totalAssets: Double,
    onAssetClick: (AssetItem) -> Unit = {}
) {
    val percent = if (totalAssets > 0) String.format(java.util.Locale.US, "%.1f", (asset.realValue / totalAssets) * 100) else "0"
    val profile = AssetIconHelper.getAssetProfile(asset.assetType, asset.name, asset.platform)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAssetClick(asset) },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, profile.primaryColor.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                AssetLogoBadge(
                    assetType = asset.assetType,
                    assetName = asset.name,
                    platform = asset.platform,
                    iconUri = asset.iconUri,
                    size = 38.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = asset.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = DarkSurfaceElevated,
                            border = BorderStroke(0.8.dp, DarkCardBorder)
                        ) {
                            Text(
                                text = asset.platform,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextMuted,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = asset.assetType,
                            fontSize = 11.sp,
                            color = profile.primaryColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    if (asset.isStock && asset.lotCount > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = AccentBlue.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "${String.format(java.util.Locale.US, "%.0f", asset.lotCount)} Lot",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentBlue,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Avg ${CurrencyHelper.formatRupiah(asset.averagePrice)} • Real ${CurrencyHelper.formatRupiah(if (asset.currentPrice > 0) asset.currentPrice else asset.averagePrice)}",
                                fontSize = 9.5.sp,
                                color = TextMuted
                            )
                            if (asset.dailyPriceChangePercent != 0.0) {
                                Spacer(modifier = Modifier.width(4.dp))
                                val isDayGain = asset.dailyPriceChangePercent >= 0
                                Text(
                                    text = "(${if (isDayGain) "+" else ""}${String.format(java.util.Locale.US, "%.2f", asset.dailyPriceChangePercent)}%)",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDayGain) IncomeGreen else ExpenseRed
                                )
                            }
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = CurrencyHelper.formatRupiah(asset.realValue),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    if (asset.isStock && asset.lotCount > 0 && asset.averagePrice > 0 && asset.currentPrice > 0) {
                        val gl = asset.unrealizedGainLoss
                        val glPercent = asset.calculatedPerformancePercent
                        val isGain = gl >= 0
                        Text(
                            text = "${if (isGain) "+" else ""}${CurrencyHelper.formatRupiah(gl)} (${String.format(java.util.Locale.US, "%+.1f", glPercent)}%)",
                            fontSize = 10.sp,
                            color = if (isGain) IncomeGreen else ExpenseRed,
                            fontWeight = FontWeight.SemiBold
                        )
                    } else {
                        Text(
                            text = "$percent% dari aset",
                            fontSize = 10.sp,
                            color = profile.primaryColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun NetWorthTrendCanvas(
    points: List<Float> = emptyList(),
    subTab: Int,
    trendDirection: Int = 0,
    selectedIndex: Int? = null,
    onPointSelected: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val effectivePoints = if (points.size >= 2) points else listOf(0.4f, 0.45f, 0.5f, 0.55f, 0.65f)

    val tapModifier = if (onPointSelected != null) {
        Modifier.pointerInput(effectivePoints) {
            detectTapGestures { offset ->
                val stepX = size.width / (effectivePoints.size - 1).coerceAtLeast(1)
                val idx = (offset.x / stepX).roundToInt().coerceIn(0, effectivePoints.size - 1)
                onPointSelected(idx)
            }
        }
    } else Modifier

    Canvas(modifier = modifier.then(tapModifier)) {
        val width = size.width
        val height = size.height

        // 1. Warna: netral abu-abu, naik hijau, turun merah
        val color = ChartDataHelper.getTrendColor(trendDirection)

        // Grid garis putus-putus dalam kotak grafik (3x3 Grid)
        with(ChartDataHelper) {
            drawDashedChartGrid(
                horizontalLines = 3,
                verticalLines = 3,
                gridColor = Color(0xFF334155).copy(alpha = 0.55f),
                drawFrame = true
            )
        }

        val stepX = width / (effectivePoints.size - 1).coerceAtLeast(1)

        val path = Path()
        val fillPath = Path()
        fillPath.moveTo(0f, height)

        var prevX = 0f
        var prevY = 0f

        effectivePoints.forEachIndexed { i, value ->
            val x = i * stepX
            val y = height - (value * height * 0.78f).coerceAtMost(height * 0.92f).coerceAtLeast(height * 0.08f)
            if (i == 0) {
                path.moveTo(x, y)
                fillPath.lineTo(x, y)
            } else {
                val cx1 = prevX + (x - prevX) / 2f
                path.cubicTo(cx1, prevY, cx1, y, x, y)
                fillPath.cubicTo(cx1, prevY, cx1, y, x, y)
            }
            prevX = x
            prevY = y
        }

        fillPath.lineTo(width, height)
        fillPath.close()

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(color.copy(alpha = 0.28f), color.copy(alpha = 0.05f), Color.Transparent),
                startY = 0f,
                endY = height
            )
        )

        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw endpoint glowing dot if no specific point is selected
        if (selectedIndex == null && effectivePoints.isNotEmpty()) {
            drawCircle(
                color = color.copy(alpha = 0.35f),
                radius = 6.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(prevX, prevY)
            )
            drawCircle(
                color = color,
                radius = 3.5.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(prevX, prevY)
            )
            drawCircle(
                color = Color.White,
                radius = 1.5.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(prevX, prevY)
            )
        }

        // Draw interactive cursor guide line & glowing point when tapped
        if (selectedIndex != null && selectedIndex in effectivePoints.indices) {
            val selX = selectedIndex * stepX
            val selY = height - (effectivePoints[selectedIndex] * height * 0.78f).coerceAtMost(height * 0.92f).coerceAtLeast(height * 0.08f)

            // Vertical guideline (Tailwind sky blue dash)
            drawLine(
                color = Color(0xFF38BDF8).copy(alpha = 0.7f),
                start = androidx.compose.ui.geometry.Offset(selX, 0f),
                end = androidx.compose.ui.geometry.Offset(selX, height),
                strokeWidth = 1.5.dp.toPx(),
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
            )

            // Outer halo
            drawCircle(
                color = Color(0xFF38BDF8).copy(alpha = 0.30f),
                radius = 9.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(selX, selY)
            )
            // Accent circle
            drawCircle(
                color = Color(0xFF38BDF8),
                radius = 5.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(selX, selY)
            )
            // Inner white dot
            drawCircle(
                color = Color.White,
                radius = 2.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(selX, selY)
            )
        }
    }
}

@Composable
fun DebtGridCard(
    debt: DebtItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val percent = if (debt.totalAmount > 0) ((debt.paidAmount / debt.totalAmount) * 100).toInt() else 0
    val activeSegments = when {
        percent >= 75 -> 4
        percent >= 50 -> 3
        percent >= 25 -> 2
        percent > 0 -> 1
        else -> 0
    }
    val isBorrowed = debt.type == DebtType.BORROWED
    val primaryColor = if (isBorrowed) Color(0xFFFF5722) else IncomeGreen
    val primaryColorLight = if (isBorrowed) Color(0xFFFF6B4A) else Color(0xFF34D399)

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("debt_card_${debt.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DebtVisualIcon(
                    name = debt.name,
                    categoryIcon = debt.categoryIcon,
                    isBorrowed = isBorrowed,
                    size = 38.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = debt.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // 4 segmented progress bars matching Gambar 1
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            for (i in 0 until 4) {
                                val isFilled = i < activeSegments
                                Box(
                                    modifier = Modifier
                                        .width(9.dp)
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(
                                            if (isFilled) primaryColor
                                            else Color(0xFF243042)
                                        )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$percent% lunas",
                            fontSize = 10.sp,
                            color = if (percent > 0) primaryColorLight else primaryColorLight.copy(alpha = 0.7f),
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = CurrencyHelper.formatRupiah(debt.remainingAmount),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Tempo: ${debt.dueDate.ifBlank { "Belum diatur" }}",
                fontSize = 11.sp,
                color = TextSubtle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun DebtListCard(
    debt: DebtItem,
    onClick: () -> Unit
) {
    val percent = if (debt.totalAmount > 0) ((debt.paidAmount / debt.totalAmount) * 100).toInt() else 0
    val isBorrowed = debt.type == DebtType.BORROWED
    val primaryColorLight = if (isBorrowed) Color(0xFFFF6B4A) else Color(0xFF34D399)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("debt_list_card_${debt.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                DebtVisualIcon(
                    name = debt.name,
                    categoryIcon = debt.categoryIcon,
                    isBorrowed = isBorrowed,
                    size = 40.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = debt.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Tempo: ${debt.dueDate.ifBlank { "-" }} • $percent% lunas",
                        fontSize = 11.sp,
                        color = if (percent > 0) primaryColorLight else TextSubtle
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = CurrencyHelper.formatRupiah(debt.remainingAmount),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "Awal: ${CurrencyHelper.formatRupiah(debt.totalAmount)}",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun PayoffPlanDialog(
    debts: List<DebtItem>,
    onDismiss: () -> Unit
) {
    var strategy by remember { mutableStateOf("SNOWBALL") } // "SNOWBALL" or "AVALANCHE"

    val borrowedDebts = debts.filter { it.type == DebtType.BORROWED && it.remainingAmount > 0 }
    val sortedDebts = remember(borrowedDebts, strategy) {
        if (strategy == "SNOWBALL") {
            borrowedDebts.sortedBy { it.remainingAmount }
        } else {
            borrowedDebts.sortedByDescending { it.interestRate }
        }
    }

    val totalRemaining = borrowedDebts.sumOf { it.remainingAmount }
    val totalMonthlyPayment = borrowedDebts.sumOf { it.monthlyPayment }
    val estimatedMonths = if (totalMonthlyPayment > 0) {
        kotlin.math.ceil(totalRemaining / totalMonthlyPayment).toInt()
    } else {
        0
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0x333B82F6),
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = null,
                            tint = Color(0xFF60A5FA),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Payoff Plan",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "Rencana Strategi Pelunasan Utang",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Strategy Selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurfaceElevated, RoundedCornerShape(10.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (strategy == "SNOWBALL") AccentBlue else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { strategy = "SNOWBALL" }
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Snowball",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (strategy == "SNOWBALL") Color.White else TextMuted
                            )
                            Text(
                                text = "Saldo Terkecil",
                                fontSize = 9.sp,
                                color = if (strategy == "SNOWBALL") Color.White.copy(alpha = 0.8f) else TextSubtle
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (strategy == "AVALANCHE") AccentBlue else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { strategy = "AVALANCHE" }
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Avalanche",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (strategy == "AVALANCHE") Color.White else TextMuted
                            )
                            Text(
                                text = "Bunga Tertinggi",
                                fontSize = 9.sp,
                                color = if (strategy == "AVALANCHE") Color.White.copy(alpha = 0.8f) else TextSubtle
                            )
                        }
                    }
                }

                // Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                    border = BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Total Sisa Utang", fontSize = 10.sp, color = TextSubtle)
                                Text(
                                    CurrencyHelper.formatRupiah(totalRemaining),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF5722)
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Estimasi Lunas", fontSize = 10.sp, color = TextSubtle)
                                Text(
                                    if (estimatedMonths > 0) "$estimatedMonths Bulan" else "Belum diatur",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF60A5FA)
                                )
                            }
                        }
                    }
                }

                // Priority Order List
                Text(
                    text = "Urutan Prioritas Pelunasan:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextWhite
                )

                if (sortedDebts.isEmpty()) {
                    Text(
                        text = "Tidak ada utang aktif yang perlu dilunasi.",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.height(180.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(sortedDebts) { debt ->
                            val index = sortedDebts.indexOf(debt) + 1
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = DarkSurfaceElevated,
                                border = BorderStroke(1.dp, if (index == 1) AccentBlue.copy(alpha = 0.6f) else DarkCardBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = if (index == 1) AccentBlue else Color(0xFF334155),
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "#$index",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = debt.name,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextWhite,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = if (debt.interestRate > 0) "Bunga: ${debt.interestRate}%/thn" else "Tanpa bunga",
                                                fontSize = 10.sp,
                                                color = TextMuted
                                            )
                                        }
                                    }
                                    Text(
                                        text = CurrencyHelper.formatRupiah(debt.remainingAmount),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                Text("Tutup", color = Color.White)
            }
        }
    )
}

@Composable
private fun AccountSubTabItem(
    title: String,
    isSelected: Boolean,
    activeColor: Color = AccentBlue,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit
) {
    val pillBgColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (isSelected) activeColor else Color.Transparent,
        animationSpec = androidx.compose.animation.core.spring(stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow),
        label = "subTabPillBg"
    )

    val contentColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (isSelected) Color.White else Color(0xFFA0ABBA),
        label = "subTabContentColor"
    )

    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1.0f,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessLow
        ),
        label = "subTabScale"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(pillBgColor)
                .padding(vertical = 4.dp, horizontal = 2.dp)
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

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = title,
                fontSize = 9.5.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                color = contentColor,
                maxLines = 1
            )
        }
    }
}
