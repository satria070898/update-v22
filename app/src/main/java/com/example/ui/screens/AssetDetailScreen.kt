package com.example.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.activity.compose.BackHandler
import java.util.Calendar
import java.util.Locale
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.roundToInt
import androidx.compose.ui.graphics.PathEffect
import com.example.ui.components.ChartHistoricalDetailCard
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.window.Dialog
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.example.ui.components.TradingViewChartCard
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AssetItem
import com.example.data.model.AssetRecord
import com.example.ui.dialogs.EditAssetDialog
import com.example.ui.dialogs.getBankBadge
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardSubtle
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ExpenseRedSoft
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.IncomeGreenSoft
import com.example.ui.theme.SegmentAssetGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.FinanceViewModel
import com.example.util.ChartDataHelper
import com.example.util.ChartPeriod
import com.example.util.ChartSeriesData
import com.example.util.CurrencyHelper
import com.example.util.AssetLogoBadge
import com.example.util.ThousandsSeparatorVisualTransformation
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.example.util.ImageUploadHelper
import com.example.util.TransactionMathHelper

@Composable
fun AssetDetailScreen(
    assetId: String,
    viewModel: FinanceViewModel,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    val assets by viewModel.assets.collectAsState()
    val asset = assets.find { it.id == assetId }

    if (asset == null) {
        onBack()
        return
    }

    val context = LocalContext.current
    val assetImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedPath = ImageUploadHelper.saveImageToInternalStorage(context, uri, "asset")
            if (savedPath != null) {
                viewModel.updateAsset(asset.copy(iconUri = "custom:$savedPath"))
                Toast.makeText(context, "Logo aset berhasil diperbarui", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val sortedRecords = remember(asset.records) {
        asset.records.sortedWith { a, b ->
            val dateCmp = b.date.compareTo(a.date)
            if (dateCmp != 0) dateCmp
            else {
                val timeA = TransactionMathHelper.parseTimeToSeconds(a.time)
                val timeB = TransactionMathHelper.parseTimeToSeconds(b.time)
                val cmp = timeB.compareTo(timeA)
                if (cmp != 0) cmp else b.id.compareTo(a.id)
            }
        }
    }

    var showEditAssetDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var recordToEdit by remember { mutableStateOf<AssetRecord?>(null) }
    var showAddRecordModal by remember { mutableStateOf(false) }
    var selectedPeriod by remember { mutableStateOf("1M") }
    var showUpdatePriceDialog by remember { mutableStateOf(false) }

    val isStock = remember(asset) {
        asset.assetType.equals("Saham", ignoreCase = true) || asset.resolvedTicker.isNotBlank()
    }
    var activeChartTab by remember(asset) {
        mutableIntStateOf(if (asset.assetType.equals("Saham", ignoreCase = true)) 1 else 0)
    }

    androidx.compose.runtime.LaunchedEffect(asset.id, asset.resolvedTicker) {
        if (isStock) {
            viewModel.refreshSingleStock(asset.resolvedTicker)
        }
    }

    val chartPeriod = when (selectedPeriod) {
        "7D" -> ChartPeriod.SEVEN_DAYS
        "3M" -> ChartPeriod.THREE_MONTHS
        "1Y" -> ChartPeriod.ONE_YEAR
        "ALL" -> ChartPeriod.ALL
        else -> ChartPeriod.ONE_MONTH
    }
    val chartData = remember(asset, chartPeriod) {
        ChartDataHelper.getAssetValuationTrend(asset, chartPeriod)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
    ) {
        // Top Toolbar matching Image 2
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = DarkSurfaceElevated,
            border = BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("btn_back_asset_detail")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = TextWhite
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Detail Aset",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }

                // Top right: Edit (Pencil) and Delete (Trash)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { showEditAssetDialog = true },
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("btn_edit_asset")
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DarkSurfaceVariant,
                            border = BorderStroke(1.dp, DarkCardBorder),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Aset",
                                    tint = AccentBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { showDeleteConfirmDialog = true },
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("btn_delete_asset")
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DarkSurfaceVariant,
                            border = BorderStroke(1.dp, ExpenseRedSoft),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Hapus Aset",
                                    tint = ExpenseRed,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Header Card with valuation
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .clickable {
                                            assetImagePickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        }
                                ) {
                                    AssetLogoBadge(
                                        assetType = asset.assetType,
                                        assetName = asset.name,
                                        platform = asset.platform,
                                        iconUri = asset.iconUri,
                                        size = 38.dp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = asset.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                    Text(
                                        text = "${asset.assetType} • ${asset.platform}",
                                        fontSize = 12.sp,
                                        color = TextMuted
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = IncomeGreenSoft,
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Text(
                                    text = "+${asset.performancePercent}%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = IncomeGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Nilai Aset Saat Ini",
                            fontSize = 12.sp,
                            color = TextSubtle
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = CurrencyHelper.formatRupiah(asset.realValue),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextWhite
                        )

                        if (isStock && asset.lotCount > 0) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = AccentBlue.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "${String.format(Locale.US, "%.0f", asset.lotCount)} Lot (${String.format(Locale.US, "%,.0f", asset.sharesCount)} lbr)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AccentBlue,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                    )
                                }
                                if (asset.averagePrice > 0) {
                                    val gl = asset.unrealizedGainLoss
                                    val isGain = gl >= 0
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isGain) IncomeGreenSoft else ExpenseRedSoft
                                    ) {
                                        Text(
                                            text = "${if (isGain) "+" else ""}${CurrencyHelper.formatRupiah(gl)} (${String.format(Locale.US, "%+.2f", asset.calculatedPerformancePercent)}%)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isGain) IncomeGreen else ExpenseRed,
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        if (isStock) {
                            Spacer(modifier = Modifier.height(14.dp))
                            // Chart Mode Switcher: TradingView Real-Time vs Grafik Portofolio
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(DarkSurfaceElevated, RoundedCornerShape(12.dp))
                                    .padding(3.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (activeChartTab == 1) AccentBlue else Color.Transparent,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { activeChartTab = 1 }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CandlestickChart,
                                            contentDescription = null,
                                            tint = if (activeChartTab == 1) Color.White else TextMuted,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "TradingView Live",
                                            fontSize = 11.5.sp,
                                            fontWeight = if (activeChartTab == 1) FontWeight.Bold else FontWeight.Medium,
                                            color = if (activeChartTab == 1) Color.White else TextMuted
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (activeChartTab == 0) AccentBlue else Color.Transparent,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { activeChartTab = 0 }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ShowChart,
                                            contentDescription = null,
                                            tint = if (activeChartTab == 0) Color.White else TextMuted,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Grafik Valuasi",
                                            fontSize = 11.5.sp,
                                            fontWeight = if (activeChartTab == 0) FontWeight.Bold else FontWeight.Medium,
                                            color = if (activeChartTab == 0) Color.White else TextMuted
                                        )
                                    }
                                }
                            }
                        }

                        if (isStock && activeChartTab == 1) {
                            Spacer(modifier = Modifier.height(14.dp))
                            TradingViewChartCard(
                                initialSymbol = asset.resolvedTicker,
                                onSymbolChanged = { newSymbol ->
                                    viewModel.updateAsset(asset.copy(tickerSymbol = newSymbol))
                                }
                            )
                        } else {
                            Spacer(modifier = Modifier.height(14.dp))

                            // Period Selector Pills
                            Row(
                                modifier = Modifier
                                    .background(DarkSurfaceElevated, RoundedCornerShape(12.dp))
                                    .padding(2.dp)
                            ) {
                                listOf("7D", "1M", "3M", "1Y", "ALL").forEach { period ->
                                    val isSelected = selectedPeriod == period
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) AccentBlue else Color.Transparent,
                                        modifier = Modifier.clickable { selectedPeriod = period }
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

                            var selectedAssetPointIndex by remember { mutableStateOf<Int?>(null) }

                            // Valuation Timeline Graph with 4x4 grid and tap interactivity
                            AssetValuationGraph(
                                chartData = chartData,
                                selectedIndex = selectedAssetPointIndex,
                                onPointSelected = { idx ->
                                    selectedAssetPointIndex = if (selectedAssetPointIndex == idx) null else idx
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                            )

                            // Interactive historical detail card for Asset Valuation
                            if (selectedAssetPointIndex != null && selectedAssetPointIndex!! in chartData.rawValues.indices) {
                                val idx = selectedAssetPointIndex!!
                                val curVal = chartData.rawValues[idx]
                                val prevVal = if (idx > 0) chartData.rawValues[idx - 1] else null
                                val curLbl = chartData.pointLabels.getOrElse(idx) { "Titik ke-${idx + 1}" }
                                val prevLbl = if (idx > 0) chartData.pointLabels.getOrElse(idx - 1) { "Titik ke-$idx" } else null

                                Spacer(modifier = Modifier.height(10.dp))
                                ChartHistoricalDetailCard(
                                    title = "Valuasi ${asset.name}",
                                    currentLabel = curLbl,
                                    currentValue = curVal,
                                    previousLabel = prevLbl,
                                    previousValue = prevVal,
                                    isReverseGood = false,
                                    onDismiss = { selectedAssetPointIndex = null }
                                )
                            }
                        }
                    }
                }
            }

            // Dedicated Stock Position & Valuation Card
            if (isStock) {
                item {
                    StockPositionDetailCard(
                        asset = asset,
                        onSyncTradingView = { viewModel.refreshSingleStock(asset.resolvedTicker) },
                        onEditPosition = { showEditAssetDialog = true }
                    )
                }
            }

            // Action row: Riwayat Pencatatan + Tambah Catatan
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Riwayat Pencatatan Aset",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = AccentBlue,
                        modifier = Modifier
                            .clickable { showAddRecordModal = true }
                            .testTag("btn_add_asset_record")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Catatan",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Records List
            if (asset.records.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Belum Ada Catatan Aset",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextWhite
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Ketuk tombol '+ Catatan' di atas untuk mencatat pembelian, investasi bertahap, atau penjualan aset.",
                                fontSize = 12.sp,
                                color = TextMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(sortedRecords) { record ->
                    AssetRecordItemRow(
                        record = record,
                        onClick = { recordToEdit = record }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Modal Sheet for Editing Catatan (matching Image 2)
    if (recordToEdit != null) {
        AssetRecordSheet(
            asset = asset,
            initialRecord = recordToEdit,
            viewModel = viewModel,
            onDismiss = { recordToEdit = null },
            onSave = { updatedRecord, updateWallet ->
                viewModel.updateAssetRecord(
                    assetId = asset.id,
                    oldRecord = recordToEdit!!,
                    updatedRecord = updatedRecord,
                    updateWalletBalance = updateWallet
                )
                recordToEdit = null
            },
            onDelete = { recordId, revertWallet ->
                viewModel.deleteAssetRecord(
                    assetId = asset.id,
                    recordId = recordId,
                    revertWalletBalance = revertWallet
                )
                recordToEdit = null
            }
        )
    }

    // Modal Sheet for Adding New Catatan
    if (showAddRecordModal) {
        AssetRecordSheet(
            asset = asset,
            initialRecord = null,
            viewModel = viewModel,
            onDismiss = { showAddRecordModal = false },
            onSave = { newRecord, updateWallet ->
                viewModel.addAssetRecord(
                    assetId = asset.id,
                    record = newRecord,
                    updateWalletBalance = updateWallet
                )
                showAddRecordModal = false
            },
            onDelete = null
        )
    }

    // Edit Asset Dialog
    if (showEditAssetDialog) {
        EditAssetDialog(
            asset = asset,
            viewModel = viewModel,
            onDismiss = { showEditAssetDialog = false }
        )
    }

    // Delete Asset Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            containerColor = DarkSurface,
            title = {
                Text(
                    text = "Hapus Aset?",
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            },
            text = {
                Text(
                    text = "Aset '${asset.name}' beserta seluruh riwayat pencatatannya akan dihapus secara permanen.",
                    color = TextMuted,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        viewModel.deleteAsset(asset.id)
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
                ) {
                    Text("Hapus", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Batal", color = TextSubtle)
                }
            }
        )
    }

    // Quick Update Market Price Modal Dialog
    if (showUpdatePriceDialog) {
        QuickUpdateStockPriceDialog(
            asset = asset,
            onSavePrice = { newPrice ->
                val newRealValue = if (asset.lotCount > 0) asset.lotCount * 100.0 * newPrice else newPrice
                viewModel.updateAsset(
                    asset.copy(
                        currentPrice = newPrice,
                        value = newRealValue
                    )
                )
                showUpdatePriceDialog = false
            },
            onDismiss = { showUpdatePriceDialog = false }
        )
    }
}

@Composable
fun StockPositionDetailCard(
    asset: AssetItem,
    onSyncTradingView: () -> Unit,
    onEditPosition: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, AccentBlue.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = AccentBlue.copy(alpha = 0.15f),
                        modifier = Modifier.size(36.dp)
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
                            text = "Posisi & Valuasi Saham",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Emiten: ${asset.resolvedTicker.ifBlank { asset.name }}",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "TradingView Live",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF34D399)
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AccentBlue,
                        modifier = Modifier.clickable(onClick = onSyncTradingView)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Perbarui Live",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DarkSurfaceElevated,
                        border = BorderStroke(1.dp, DarkCardBorder),
                        modifier = Modifier.clickable(onClick = onEditPosition)
                    ) {
                        Text(
                            text = "Ubah Lot",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextMuted,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (asset.lotCount <= 0.0 && asset.averagePrice <= 0.0) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurfaceElevated,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Belum Ada Data Posisi Lot & Average",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Lengkapi jumlah lot dan nilai beli rata-rata untuk melacak keuntungan saham secara real-time.",
                            fontSize = 11.sp,
                            color = TextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onEditPosition,
                            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text("Atur Lot & Average Beli", fontSize = 11.5.sp)
                        }
                    }
                }
            } else {
                // 4 Grid Metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Metric 1: Jumlah Lot
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = DarkSurfaceElevated,
                        border = BorderStroke(0.8.dp, DarkCardBorder),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "Jumlah Lot", fontSize = 11.sp, color = TextMuted)
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "${String.format(Locale.US, "%.0f", asset.lotCount)} Lot",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = "${String.format(Locale.US, "%,.0f", asset.sharesCount)} lembar",
                                fontSize = 10.sp,
                                color = AccentBlue
                            )
                        }
                    }

                    // Metric 2: Nilai Average
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = DarkSurfaceElevated,
                        border = BorderStroke(0.8.dp, DarkCardBorder),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "Nilai Average", fontSize = 11.sp, color = TextMuted)
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = CurrencyHelper.formatRupiah(asset.averagePrice),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = "Harga beli / lbr",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Metric 3: Harga Real Pasar TradingView
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = DarkSurfaceElevated,
                        border = BorderStroke(0.8.dp, DarkCardBorder),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Harga Real Pasar", fontSize = 11.sp, color = TextMuted)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF10B981))
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "TradingView",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF34D399)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = CurrencyHelper.formatRupiah(if (asset.currentPrice > 0) asset.currentPrice else asset.averagePrice),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentBlue
                            )
                            if (asset.dailyPriceChangePercent != 0.0 || asset.dailyPriceChange != 0.0) {
                                val isDayGain = asset.dailyPriceChangePercent >= 0
                                Text(
                                    text = "${if (isDayGain) "+" else ""}${CurrencyHelper.formatRupiah(asset.dailyPriceChange)} (${String.format(Locale.US, "%+.2f", asset.dailyPriceChangePercent)}%)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDayGain) IncomeGreen else ExpenseRed
                                )
                            } else {
                                Text(
                                    text = "Per lembar pasar",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }

                    // Metric 4: Nilai Real Portofolio
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = DarkSurfaceElevated,
                        border = BorderStroke(0.8.dp, DarkCardBorder),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "Nilai Real Portofolio", fontSize = 11.sp, color = TextMuted)
                            Spacer(modifier = Modifier.height(3.dp))
                            val isGain = asset.unrealizedGainLoss >= 0
                            Text(
                                text = CurrencyHelper.formatRupiah(asset.realValue),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (asset.averagePrice > 0 && asset.currentPrice > 0) {
                                    if (isGain) IncomeGreen else ExpenseRed
                                } else TextWhite
                            )
                            Text(
                                text = "${String.format(Locale.US, "%.0f", asset.lotCount)} Lot × 100 × Real",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Profit & Loss Bar
                val gl = asset.unrealizedGainLoss
                val glPercent = asset.calculatedPerformancePercent
                val isGain = gl >= 0
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isGain) IncomeGreenSoft.copy(alpha = 0.5f) else ExpenseRedSoft.copy(alpha = 0.5f),
                    border = BorderStroke(0.8.dp, if (isGain) IncomeGreen.copy(alpha = 0.4f) else ExpenseRed.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Modal Beli", fontSize = 10.5.sp, color = TextMuted)
                            Text(
                                text = CurrencyHelper.formatRupiah(asset.costBasis),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Potensi Untung / Rugi (P/L)", fontSize = 10.5.sp, color = TextMuted)
                            Text(
                                text = "${if (isGain) "+" else ""}${CurrencyHelper.formatRupiah(gl)} (${String.format(Locale.US, "%+.2f", glPercent)}%)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isGain) IncomeGreen else ExpenseRed
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Nilai real portofolio bergerak otomatis mengikuti harga pasar real-time dari TradingView.",
                        fontSize = 10.5.sp,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

@Composable
fun QuickUpdateStockPriceDialog(
    asset: AssetItem,
    onSavePrice: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    var priceText by remember {
        mutableStateOf(
            if (asset.currentPrice > 0) String.format(Locale.US, "%.0f", asset.currentPrice)
            else if (asset.averagePrice > 0) String.format(Locale.US, "%.0f", asset.averagePrice)
            else ""
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = DarkSurface,
            border = BorderStroke(1.dp, DarkCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Perbarui Harga Real",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "${asset.name} (${asset.resolvedTicker.ifBlank { "Saham" }})",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Current info
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurfaceElevated, RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Jumlah Lot", fontSize = 10.5.sp, color = TextMuted)
                        Text("${String.format(Locale.US, "%.0f", asset.lotCount)} Lot", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    }
                    Column {
                        Text("Nilai Avg Beli", fontSize = 10.5.sp, color = TextMuted)
                        Text(CurrencyHelper.formatRupiah(asset.averagePrice), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Total Modal", fontSize = 10.5.sp, color = TextMuted)
                        Text(CurrencyHelper.formatRupiah(asset.costBasis), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Harga Pasar Saat Ini (Rp / lembar):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSubtle
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it.filter { ch -> ch.isDigit() } },
                    leadingIcon = {
                        Text("Rp", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite, modifier = Modifier.padding(start = 12.dp))
                    },
                    visualTransformation = ThousandsSeparatorVisualTransformation('.'),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("Contoh: 5250", color = TextSubtle) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = DarkSurfaceElevated,
                        unfocusedContainerColor = DarkSurfaceElevated,
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = DarkCardBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Quick nudge buttons: -50, -25, +25, +50
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(-50, -25, 25, 50).forEach { delta ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DarkSurfaceElevated,
                            border = BorderStroke(0.8.dp, DarkCardBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    val current = priceText.toDoubleOrNull() ?: 0.0
                                    val next = (current + delta).coerceAtLeast(0.0)
                                    priceText = String.format(Locale.US, "%.0f", next)
                                }
                        ) {
                            Text(
                                text = if (delta > 0) "+$delta" else "$delta",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (delta > 0) IncomeGreen else ExpenseRed,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }

                val enteredPrice = priceText.toDoubleOrNull() ?: 0.0
                if (enteredPrice > 0 && asset.lotCount > 0) {
                    val newRealVal = asset.lotCount * 100.0 * enteredPrice
                    val newCostBasis = asset.costBasis
                    val newGL = newRealVal - newCostBasis
                    val newGLPercent = if (newCostBasis > 0) (newGL / newCostBasis) * 100.0 else 0.0
                    val isGain = newGL >= 0

                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceElevated,
                        border = BorderStroke(0.8.dp, if (isGain) IncomeGreen.copy(alpha = 0.4f) else ExpenseRed.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Estimasi Nilai Real:", fontSize = 10.5.sp, color = TextMuted)
                                Text(CurrencyHelper.formatRupiah(newRealVal), fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Estimasi P/L:", fontSize = 10.5.sp, color = TextMuted)
                                Text(
                                    text = "${if (isGain) "+" else ""}${CurrencyHelper.formatRupiah(newGL)} (${String.format(Locale.US, "%+.2f", newGLPercent)}%)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isGain) IncomeGreen else ExpenseRed
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, DarkCardBorder)
                    ) {
                        Text("Batal", color = TextMuted)
                    }

                    Button(
                        onClick = {
                            val newPrice = priceText.toDoubleOrNull() ?: 0.0
                            if (newPrice > 0) {
                                onSavePrice(newPrice)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                    ) {
                        Text("Simpan", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AssetRecordItemRow(
    record: AssetRecord,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("record_item_${record.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, DarkCardBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (record.isIncrease) IncomeGreenSoft else ExpenseRedSoft,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (record.isIncrease) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                            contentDescription = null,
                            tint = if (record.isIncrease) IncomeGreen else ExpenseRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = when (record.tag) {
                                "Buy" -> AccentBlue
                                "Invest more" -> IncomeGreen
                                "Sold" -> Color(0xFFF59E0B)
                                else -> DarkSurfaceElevated
                            }
                        ) {
                            Text(
                                text = record.tag,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        if (record.sourceWalletName.isNotBlank()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = DarkSurfaceElevated
                            ) {
                                Text(
                                    text = record.sourceWalletName,
                                    fontSize = 10.sp,
                                    color = TextMuted,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (record.note.isNotBlank()) record.note else (if (record.isIncrease) "Penambahan Aset" else "Pengurangan Aset"),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextWhite
                    )

                    Text(
                        text = "${record.date} • ${record.time}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (record.isIncrease) "+" else "-"}${CurrencyHelper.formatRupiah(record.amount)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (record.isIncrease) IncomeGreen else ExpenseRed
                )
                if (record.lotCount > 0) {
                    val lotLabel = "${String.format(java.util.Locale.US, "%.0f", record.lotCount)} Lot" +
                            if (record.pricePerUnit > 0) " @ ${CurrencyHelper.formatRupiah(record.pricePerUnit)}" else ""
                    Text(
                        text = lotLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF60A5FA)
                    )
                }
                Text(
                    text = "Ketuk untuk edit",
                    fontSize = 10.sp,
                    color = TextSubtle
                )
            }
        }
    }
}

@Composable
fun AssetValuationGraph(
    chartData: ChartSeriesData,
    selectedIndex: Int? = null,
    onPointSelected: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val effectivePoints = if (chartData.points.size >= 2) chartData.points else listOf(0.35f, 0.45f, 0.55f, 0.65f, 0.8f)

    val tapModifier = if (onPointSelected != null) {
        Modifier.pointerInput(effectivePoints) {
            detectTapGestures { offset ->
                val w = size.width
                val stepX = w / (effectivePoints.size - 1).coerceAtLeast(1)
                val idx = (offset.x / stepX).roundToInt().coerceIn(0, effectivePoints.size - 1)
                onPointSelected(idx)
            }
        }
    } else Modifier

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .then(tapModifier)
        ) {
            val w = size.width
            val h = size.height

            // 1. Grid garis putus-putus dalam kotak grafik (3x3 Grid)
            with(ChartDataHelper) {
                drawDashedChartGrid(
                    horizontalLines = 3,
                    verticalLines = 3,
                    gridColor = Color(0xFF334155).copy(alpha = 0.5f),
                    drawFrame = true
                )
            }

            val trendColor = ChartDataHelper.getTrendColor(chartData.trendDirection)
            val stepX = w / (effectivePoints.size - 1).coerceAtLeast(1)

            val path = Path()
            val fillPath = Path()
            fillPath.moveTo(0f, h)

            effectivePoints.forEachIndexed { i, value ->
                val x = i * stepX
                val y = h - (value * h * 0.78f).coerceAtMost(h * 0.92f).coerceAtLeast(h * 0.08f)
                if (i == 0) {
                    path.moveTo(x, y)
                    fillPath.lineTo(x, y)
                } else {
                    path.lineTo(x, y)
                    fillPath.lineTo(x, y)
                }
            }

            fillPath.lineTo(w, h)
            fillPath.close()

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        trendColor.copy(alpha = 0.30f),
                        trendColor.copy(alpha = 0.02f)
                    )
                )
            )

            // Line stroke
            drawPath(
                path = path,
                color = trendColor,
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // Current dot at end if no point is selected
            if (selectedIndex == null) {
                val lastX = (effectivePoints.size - 1) * stepX
                val lastY = h - (effectivePoints.last() * h * 0.78f).coerceAtMost(h * 0.92f).coerceAtLeast(h * 0.08f)
                drawCircle(
                    color = Color.White,
                    radius = 5.dp.toPx(),
                    center = Offset(lastX, lastY)
                )
                drawCircle(
                    color = trendColor,
                    radius = 3.dp.toPx(),
                    center = Offset(lastX, lastY)
                )
            }

            // Highlight selected point on tap
            if (selectedIndex != null && selectedIndex in effectivePoints.indices) {
                val selX = selectedIndex * stepX
                val selY = h - (effectivePoints[selectedIndex] * h * 0.78f).coerceAtMost(h * 0.92f).coerceAtLeast(h * 0.08f)

                // Vertical indicator line
                drawLine(
                    color = Color(0xFF38BDF8).copy(alpha = 0.7f),
                    start = Offset(selX, 0f),
                    end = Offset(selX, h),
                    strokeWidth = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
                )

                // Outer halo
                drawCircle(
                    color = Color(0xFF38BDF8).copy(alpha = 0.30f),
                    radius = 9.dp.toPx(),
                    center = Offset(selX, selY)
                )
                // Center ring
                drawCircle(
                    color = Color(0xFF38BDF8),
                    radius = 5.dp.toPx(),
                    center = Offset(selX, selY)
                )
                // Inner white dot
                drawCircle(
                    color = Color.White,
                    radius = 2.dp.toPx(),
                    center = Offset(selX, selY)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = chartData.startLabel,
                fontSize = 11.sp,
                color = TextSubtle
            )
            Text(
                text = chartData.midLabel,
                fontSize = 11.sp,
                color = TextSubtle
            )
            Text(
                text = chartData.endLabel,
                fontSize = 11.sp,
                color = TextSubtle
            )
        }
    }
}

/**
 * Bottom Sheet replicating Image 2: "Edit Catatan"
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetRecordSheet(
    asset: AssetItem,
    initialRecord: AssetRecord?,
    viewModel: FinanceViewModel,
    onDismiss: () -> Unit,
    onSave: (AssetRecord, Boolean) -> Unit,
    onDelete: ((String, Boolean) -> Unit)?
) {
    val isEdit = initialRecord != null

    var selectedTag by remember { mutableStateOf(initialRecord?.tag ?: "Buy") }
    var isIncrease by remember { mutableStateOf(initialRecord?.isIncrease ?: true) }
    var amountText by remember {
        mutableStateOf(
            if (initialRecord != null) {
                String.format(java.util.Locale.US, "%.0f", initialRecord.amount)
            } else ""
        )
    }
    val wallets by viewModel.wallets.collectAsState()
    var selectedWalletName by remember {
        mutableStateOf(initialRecord?.sourceWalletName ?: (wallets.firstOrNull()?.name ?: ""))
    }
    var dateText by remember { mutableStateOf(initialRecord?.date ?: "7 Okt 2026") }
    var timeText by remember { mutableStateOf(initialRecord?.time ?: "10.09") }
    var noteText by remember { mutableStateOf(initialRecord?.note ?: "") }

    val isStock = remember(asset) {
        asset.assetType.equals("Saham", ignoreCase = true) || asset.resolvedTicker.isNotBlank()
    }
    var lotText by remember {
        mutableStateOf(
            if (initialRecord != null && initialRecord.lotCount > 0) {
                String.format(java.util.Locale.US, "%.0f", initialRecord.lotCount)
            } else ""
        )
    }
    var pricePerUnitText by remember {
        mutableStateOf(
            if (initialRecord != null && initialRecord.pricePerUnit > 0) {
                String.format(java.util.Locale.US, "%.0f", initialRecord.pricePerUnit)
            } else if (asset.currentPrice > 0) {
                String.format(java.util.Locale.US, "%.0f", asset.currentPrice)
            } else if (asset.averagePrice > 0) {
                String.format(java.util.Locale.US, "%.0f", asset.averagePrice)
            } else ""
        )
    }

    val context = LocalContext.current
    val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")
    val cal = Calendar.getInstance()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface,
        dragHandle = {
            Surface(
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(2.dp)
            ) {
                Box(modifier = Modifier.size(width = 36.dp, height = 4.dp))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header matching Image 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0x332563EB),
                        modifier = Modifier.size(44.dp)
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
                            text = if (isEdit) "Edit Catatan" else "Tambah Catatan",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = if (isEdit) "Perbarui catatan ${asset.name}" else "Catat mutasi nilai ${asset.name}",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("btn_close_record_sheet")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint = TextWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tag Section matching Image 2
            Text(
                text = "Tag",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val tags = listOf(
                    Triple("Buy", AccentBlue, "🔵"),
                    Triple("Invest more", IncomeGreen, "🟢"),
                    Triple("Sold", Color(0xFFF59E0B), "🟡")
                )
                tags.forEach { (tag, color, dot) ->
                    val isSelected = selectedTag == tag
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) color.copy(alpha = 0.25f) else DarkSurfaceElevated,
                        border = BorderStroke(1.dp, if (isSelected) color else DarkCardBorder),
                        modifier = Modifier.clickable {
                            selectedTag = tag
                            if (tag == "Sold") isIncrease = false
                            if (tag == "Buy" || tag == "Invest more") isIncrease = true
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = dot, fontSize = 10.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = tag,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isSelected) Color.White else TextMuted
                            )
                        }
                    }
                }

                // Edit tags button
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = DarkSurfaceElevated,
                    border = BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Edit tags",
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Jenis Catatan matching Image 2 (Increase / Decrease)
            Text(
                text = "Jenis Catatan",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Increase
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isIncrease) IncomeGreen.copy(alpha = 0.18f) else DarkSurfaceElevated,
                    border = BorderStroke(1.5.dp, if (isIncrease) IncomeGreen else DarkCardBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { isIncrease = true }
                        .testTag("record_type_increase")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = if (isIncrease) IncomeGreen else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Increase",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isIncrease) IncomeGreen else TextMuted
                        )
                    }
                }

                // Decrease
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (!isIncrease) ExpenseRed.copy(alpha = 0.18f) else DarkSurfaceElevated,
                    border = BorderStroke(1.5.dp, if (!isIncrease) ExpenseRed else DarkCardBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { isIncrease = false }
                        .testTag("record_type_decrease")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                            contentDescription = null,
                            tint = if (!isIncrease) ExpenseRed else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Decrease",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (!isIncrease) ExpenseRed else TextMuted
                        )
                    }
                }
            }

            if (isStock) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DarkSurfaceElevated,
                    border = BorderStroke(1.dp, Color(0xFF2563EB).copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Pencatatan Lot & Harga Beli",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF60A5FA)
                            )
                            if (asset.tickerSymbol.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF2563EB).copy(alpha = 0.25f)
                                ) {
                                    Text(
                                        text = asset.tickerSymbol,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF93C5FD),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Jumlah Lot
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Jumlah Lot", fontSize = 11.sp, color = TextMuted)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = lotText,
                                    onValueChange = {
                                        val digits = it.filter { ch -> ch.isDigit() || ch == '.' }
                                        lotText = digits
                                        val l = lotText.toDoubleOrNull() ?: 0.0
                                        val p = pricePerUnitText.toDoubleOrNull() ?: 0.0
                                        if (l > 0 && p > 0) {
                                            amountText = String.format(java.util.Locale.US, "%.0f", l * 100.0 * p)
                                        }
                                    },
                                    placeholder = { Text("0", color = TextSubtle) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    trailingIcon = { Text("Lot", fontSize = 11.sp, color = TextMuted, modifier = Modifier.padding(end = 8.dp)) },
                                    modifier = Modifier.fillMaxWidth().testTag("input_record_lot"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextWhite,
                                        unfocusedTextColor = TextWhite,
                                        focusedContainerColor = DarkSurface,
                                        unfocusedContainerColor = DarkSurface,
                                        focusedBorderColor = AccentBlue,
                                        unfocusedBorderColor = DarkCardBorder
                                    )
                                )
                            }
                            // Harga per Lembar
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Harga/Lembar", fontSize = 11.sp, color = TextMuted)
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = pricePerUnitText,
                                    onValueChange = {
                                        val digits = it.filter { ch -> ch.isDigit() }
                                        pricePerUnitText = digits
                                        val l = lotText.toDoubleOrNull() ?: 0.0
                                        val p = pricePerUnitText.toDoubleOrNull() ?: 0.0
                                        if (l > 0 && p > 0) {
                                            amountText = String.format(java.util.Locale.US, "%.0f", l * 100.0 * p)
                                        }
                                    },
                                    placeholder = { Text("0", color = TextSubtle) },
                                    visualTransformation = ThousandsSeparatorVisualTransformation('.'),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    leadingIcon = { Text("Rp", fontSize = 11.sp, color = TextMuted, modifier = Modifier.padding(start = 6.dp)) },
                                    modifier = Modifier.fillMaxWidth().testTag("input_record_price_per_share"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextWhite,
                                        unfocusedTextColor = TextWhite,
                                        focusedContainerColor = DarkSurface,
                                        unfocusedContainerColor = DarkSurface,
                                        focusedBorderColor = AccentBlue,
                                        unfocusedBorderColor = DarkCardBorder
                                    )
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Jumlah Section matching Image 2
            Text(
                text = if (isStock) "Total Nilai Transaksi (Rp)" else "Jumlah",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = {
                    val digits = it.filter { ch -> ch.isDigit() }
                    if (digits.length <= 11) {
                        amountText = digits
                        val total = amountText.toDoubleOrNull() ?: 0.0
                        val l = lotText.toDoubleOrNull() ?: 0.0
                        if (isStock && l > 0 && total > 0) {
                            pricePerUnitText = String.format(java.util.Locale.US, "%.0f", total / (l * 100.0))
                        }
                    }
                },
                leadingIcon = {
                    Text(
                        text = "Rp",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        modifier = Modifier.padding(start = 14.dp, end = 4.dp)
                    )
                },
                placeholder = { Text("0", color = TextSubtle) },
                visualTransformation = ThousandsSeparatorVisualTransformation('.'),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_record_amount"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedContainerColor = DarkSurfaceElevated,
                    unfocusedContainerColor = DarkSurfaceElevated,
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = DarkCardBorder
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Danai dari Dompet (Opsional) matching Image 2
            Text(
                text = "Danai dari Dompet (Opsional)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextWhite
            )
            Text(
                text = "Kaitkan transaksi pengeluaran dari sebuah dompet",
                fontSize = 11.sp,
                color = TextMuted
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Option: No wallet
                val isNoneSelected = selectedWalletName.isBlank()
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isNoneSelected) AccentBlue.copy(alpha = 0.2f) else DarkSurfaceElevated,
                    border = BorderStroke(1.dp, if (isNoneSelected) AccentBlue else DarkCardBorder),
                    modifier = Modifier.clickable { selectedWalletName = "" }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isNoneSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = AccentBlue,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = "Tanpa Dompet",
                            fontSize = 12.sp,
                            color = if (isNoneSelected) Color.White else TextMuted
                        )
                    }
                }

                // Available Wallets
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
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = AccentBlue,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Surface(
                                shape = RoundedCornerShape(3.dp),
                                color = AccentBlue,
                                modifier = Modifier.padding(end = 6.dp)
                            ) {
                                Text(
                                    text = getBankBadge(w.name),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                                )
                            }
                            Text(
                                text = w.name,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) Color.White else TextMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tanggal Section matching Image 2
            Text(
                text = "Tanggal",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Date box (Calendar Picker)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurfaceElevated,
                    border = BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier
                        .weight(1.5f)
                        .clickable {
                            val parts = dateText.split(" ").filter { it.isNotBlank() }
                            val initDay = parts.getOrNull(0)?.filter { it.isDigit() }?.toIntOrNull() ?: cal.get(Calendar.DAY_OF_MONTH)
                            val mIdx = monthNames.indexOfFirst { it.equals(parts.getOrNull(1), ignoreCase = true) }
                            val initMonth = if (mIdx >= 0) mIdx else cal.get(Calendar.MONTH)
                            val initYear = parts.getOrNull(2)?.filter { it.isDigit() }?.toIntOrNull() ?: cal.get(Calendar.YEAR)

                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    dateText = "$day ${monthNames.getOrElse(month) { "Okt" }} $year"
                                },
                                initYear, initMonth, initDay
                            ).show()
                        }
                ) {
                    Text(
                        text = dateText,
                        fontSize = 14.sp,
                        color = TextWhite,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                    )
                }

                // Time box (Clock/Time Picker)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurfaceElevated,
                    border = BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            val timeParts = timeText.replace(':', '.').split('.').filter { it.isNotBlank() }
                            val initHour = timeParts.getOrNull(0)?.toIntOrNull() ?: cal.get(Calendar.HOUR_OF_DAY)
                            val initMin = timeParts.getOrNull(1)?.toIntOrNull() ?: cal.get(Calendar.MINUTE)

                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    timeText = String.format(Locale.getDefault(), "%02d.%02d", hour, minute)
                                },
                                initHour, initMin, true
                            ).show()
                        }
                ) {
                    Text(
                        text = timeText,
                        fontSize = 14.sp,
                        color = TextWhite,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Catatan (Opsional) matching Image 2
            Text(
                text = "Catatan (Opsional)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                placeholder = { Text("misal: tabungan ayah", color = TextSubtle) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_record_note"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedContainerColor = DarkSurfaceElevated,
                    unfocusedContainerColor = DarkSurfaceElevated,
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = DarkCardBorder
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button matching Image 2 ("⇄ Transfer dari BRI Utama" or "Simpan Catatan")
            val amountVal = amountText.toDoubleOrNull() ?: 0.0
            val buttonText = if (selectedWalletName.isNotBlank()) {
                "⇄ Transfer dari $selectedWalletName"
            } else {
                if (isEdit) "Simpan Perubahan" else "Simpan Catatan"
            }

            Button(
                onClick = {
                    if (amountVal > 0) {
                        val parsedLots = lotText.toDoubleOrNull() ?: 0.0
                        val parsedPrice = pricePerUnitText.toDoubleOrNull() ?: (if (parsedLots > 0) amountVal / (parsedLots * 100.0) else 0.0)
                        val record = AssetRecord(
                            id = initialRecord?.id ?: java.util.UUID.randomUUID().toString(),
                            assetId = asset.id,
                            tag = selectedTag,
                            isIncrease = isIncrease,
                            amount = amountVal,
                            sourceWalletName = selectedWalletName,
                            date = dateText,
                            time = timeText,
                            note = noteText,
                            lotCount = parsedLots,
                            pricePerUnit = parsedPrice
                        )
                        onSave(record, selectedWalletName.isNotBlank())
                    }
                },
                enabled = amountVal > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_save_asset_record"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (selectedWalletName.isNotBlank()) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = buttonText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Delete record button if editing
            if (onDelete != null && initialRecord != null) {
                Spacer(modifier = Modifier.height(10.dp))
                TextButton(
                    onClick = { onDelete(initialRecord.id, initialRecord.sourceWalletName.isNotBlank()) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Hapus Catatan Ini",
                        color = ExpenseRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
