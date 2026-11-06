package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TextWhite
import com.example.util.StockTickerItem
import com.example.util.TradingViewHelper

enum class TradingViewChartMode {
    OVERVIEW,
    CANDLESTICK
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun TradingViewWebView(
    symbol: String,
    mode: TradingViewChartMode,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isLoading by remember(symbol, mode) { mutableStateOf(true) }
    var hasRendererCrashed by remember(symbol, mode) { mutableStateOf(false) }
    var reloadTrigger by remember { mutableStateOf(0) }
    var lastLoadedKey by remember { mutableStateOf("") }

    if (hasRendererCrashed) {
        Box(
            modifier = modifier
                .background(Color(0xFF12181F)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ShowChart,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Grafik TradingView dialihkan untuk menjaga stabilitas perangkat",
                    fontSize = 12.sp,
                    color = TextWhite,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Buka langsung di TradingView atau coba muat ulang grafik.",
                    fontSize = 11.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            hasRendererCrashed = false
                            isLoading = true
                            reloadTrigger++
                            lastLoadedKey = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Coba Muat Ulang", fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = {
                            val url = TradingViewHelper.getExternalTradingViewUrl(symbol)
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, AccentBlue)
                    ) {
                        Text("Buka di Browser", fontSize = 11.sp, color = AccentBlue)
                    }
                }
            }
        }
    } else {
        Box(modifier = modifier) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        setBackgroundColor(0xFF12181F.toInt())
                        setLayerType(View.LAYER_TYPE_HARDWARE, null)
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            cacheMode = WebSettings.LOAD_DEFAULT
                            setSupportZoom(false)
                            builtInZoomControls = false
                            displayZoomControls = false
                            mediaPlaybackRequiresUserGesture = true
                        }
                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                if (newProgress >= 70) {
                                    isLoading = false
                                }
                            }
                        }
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                isLoading = false
                            }

                            override fun onReceivedError(
                                view: WebView?,
                                request: WebResourceRequest?,
                                error: WebResourceError?
                            ) {
                                super.onReceivedError(view, request, error)
                                if (request?.isForMainFrame == true) {
                                    isLoading = false
                                }
                            }

                            override fun onRenderProcessGone(
                                view: WebView?,
                                detail: RenderProcessGoneDetail?
                            ): Boolean {
                                hasRendererCrashed = true
                                isLoading = false
                                try {
                                    view?.let {
                                        (it.parent as? ViewGroup)?.removeView(it)
                                        it.destroy()
                                    }
                                } catch (_: Exception) {}
                                return true
                            }
                        }
                    }
                },
                update = { webView ->
                    val currentKey = "$symbol-$mode-$reloadTrigger"
                    if (lastLoadedKey != currentKey) {
                        lastLoadedKey = currentKey
                        isLoading = true
                        val html = if (mode == TradingViewChartMode.CANDLESTICK) {
                            TradingViewHelper.generateAdvancedChartHtml(symbol)
                        } else {
                            TradingViewHelper.generateMiniChartHtml(symbol)
                        }
                        webView.loadDataWithBaseURL("https://s3.tradingview.com", html, "text/html", "UTF-8", null)
                    }
                },
                onRelease = { webView ->
                    try {
                        webView.stopLoading()
                        webView.destroy()
                    } catch (_: Exception) {}
                }
            )

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF12181F)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(28.dp),
                            color = AccentBlue,
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Memuat data real-time TradingView...",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradingViewChartCard(
    initialSymbol: String,
    onSymbolChanged: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentSymbol by remember(initialSymbol) {
        mutableStateOf(if (initialSymbol.isNotBlank()) initialSymbol else "IDX:BBRI")
    }
    var chartMode by remember { mutableStateOf(TradingViewChartMode.CANDLESTICK) }
    var isFullScreen by remember { mutableStateOf(false) }
    var showTickerPicker by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, DarkCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Logo/Title + Mode Switcher + Fullscreen + Open in TradingView
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showTickerPicker = true }
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AccentBlue.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, AccentBlue.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "LIVE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentBlue,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentSymbol,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Ganti Saham",
                                tint = TextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = "TradingView Real-Time Chart",
                            fontSize = 11.sp,
                            color = TextSubtle
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Mode Toggle (Candles vs Area/Overview)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = DarkSurfaceElevated,
                        border = BorderStroke(0.5.dp, DarkCardBorder)
                    ) {
                        Row(modifier = Modifier.padding(2.dp)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (chartMode == TradingViewChartMode.CANDLESTICK) AccentBlue else Color.Transparent,
                                modifier = Modifier.clickable { chartMode = TradingViewChartMode.CANDLESTICK }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CandlestickChart,
                                    contentDescription = "Candlestick",
                                    tint = if (chartMode == TradingViewChartMode.CANDLESTICK) Color.White else TextMuted,
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .size(16.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (chartMode == TradingViewChartMode.OVERVIEW) AccentBlue else Color.Transparent,
                                modifier = Modifier.clickable { chartMode = TradingViewChartMode.OVERVIEW }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ShowChart,
                                    contentDescription = "Overview",
                                    tint = if (chartMode == TradingViewChartMode.OVERVIEW) Color.White else TextMuted,
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Fullscreen Button
                    IconButton(
                        onClick = { isFullScreen = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = "Layar Penuh",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Open external TradingView
                    IconButton(
                        onClick = {
                            val url = TradingViewHelper.getExternalTradingViewUrl(currentSymbol)
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = "Buka di TradingView",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Stock Ticker Chips (Indonesian Blue Chips)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val quickTickers = listOf("BBRI", "BBCA", "BMRI", "BBNI", "TLKM", "ASII", "GOTO", "ANTM", "AMMN", "BRIS")
                quickTickers.forEach { code ->
                    val full = "IDX:$code"
                    val isSelected = currentSymbol.equals(full, ignoreCase = true) || currentSymbol.equals(code, ignoreCase = true)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) AccentBlue.copy(alpha = 0.25f) else DarkSurfaceElevated,
                        border = BorderStroke(1.dp, if (isSelected) AccentBlue else Color.Transparent),
                        modifier = Modifier.clickable {
                            currentSymbol = full
                            onSymbolChanged?.invoke(full)
                        }
                    ) {
                        Text(
                            text = code,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextMuted,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Embedded TradingView Interactive Chart
            TradingViewWebView(
                symbol = currentSymbol,
                mode = chartMode,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(310.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(14.dp))
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Live status footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(IncomeGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Terhubung ke Bursa Efek Indonesia (IDX)",
                        fontSize = 10.sp,
                        color = TextSubtle
                    )
                }

                Text(
                    text = "Powered by TradingView",
                    fontSize = 10.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    // Modal: Fullscreen TradingView Chart
    if (isFullScreen) {
        Dialog(
            onDismissRequest = { isFullScreen = false },
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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkSurface)
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { isFullScreen = false },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Tutup",
                                    tint = TextWhite
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = currentSymbol,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                                Text(
                                    text = "TradingView Advanced Technical Analysis",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                val url = TradingViewHelper.getExternalTradingViewUrl(currentSymbol)
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = "TradingView Web",
                                tint = TextWhite
                            )
                        }
                    }

                    TradingViewWebView(
                        symbol = currentSymbol,
                        mode = chartMode,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    // Modal: Ticker Search / Picker Sheet
    if (showTickerPicker) {
        StockTickerPickerSheet(
            selectedSymbol = currentSymbol,
            onSymbolSelected = { newSymbol ->
                currentSymbol = newSymbol
                onSymbolChanged?.invoke(newSymbol)
                showTickerPicker = false
            },
            onDismiss = { showTickerPicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockTickerPickerSheet(
    selectedSymbol: String,
    onSymbolSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val filteredList = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            TradingViewHelper.popularStocks
        } else {
            val q = searchQuery.trim().lowercase()
            TradingViewHelper.popularStocks.filter {
                it.code.lowercase().contains(q) ||
                        it.name.lowercase().contains(q) ||
                        it.sector.lowercase().contains(q)
            }
        }
    }

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
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pilih Emiten Saham IHSG",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Cari kode emiten / nama perusahaan...", color = TextMuted) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextMuted)
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = DarkCardBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedContainerColor = DarkSurfaceElevated,
                    unfocusedContainerColor = DarkSurfaceElevated
                )
            )

            // Direct custom ticker submit if typed something custom
            if (searchQuery.isNotBlank() && filteredList.none { it.code.equals(searchQuery.trim(), ignoreCase = true) }) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AccentBlue.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, AccentBlue.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val custom = TradingViewHelper.formatSymbol(searchQuery)
                            onSymbolSelected(custom)
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ShowChart, contentDescription = null, tint = AccentBlue)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Gunakan Simbol Kustom: ${searchQuery.uppercase()}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = "Memuat grafik TradingView untuk ${TradingViewHelper.formatSymbol(searchQuery)}",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Daftar Saham Terpopuler (IDX / IHSG)",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSubtle
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(filteredList, key = { it.code }) { item ->
                    val isSelected = selectedSymbol.equals(item.fullSymbol, ignoreCase = true) ||
                            selectedSymbol.equals(item.code, ignoreCase = true)
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) AccentBlue.copy(alpha = 0.2f) else DarkSurfaceElevated,
                        border = BorderStroke(1.dp, if (isSelected) AccentBlue else DarkCardBorder.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSymbolSelected(item.fullSymbol) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) AccentBlue else DarkBackground,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = item.code.take(2),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isSelected) Color.White else AccentBlue
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = item.code,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextWhite
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = DarkBackground
                                        ) {
                                            Text(
                                                text = item.sector,
                                                fontSize = 9.sp,
                                                color = TextMuted,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = item.name,
                                        fontSize = 11.sp,
                                        color = TextMuted,
                                        maxLines = 1
                                    )
                                }
                            }

                            Text(
                                text = "IDX",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) AccentBlue else TextSubtle
                            )
                        }
                    }
                }
            }
        }
    }
}
