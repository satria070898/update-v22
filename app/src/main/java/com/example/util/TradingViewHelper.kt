package com.example.util

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

data class StockTickerItem(
    val code: String,
    val name: String,
    val sector: String,
    val fullSymbol: String = "IDX:$code"
)

data class TradingViewQuote(
    val symbol: String, // e.g. "IDX:BBRI"
    val tickerCode: String, // e.g. "BBRI"
    val closePrice: Double, // Real-time market price
    val changePercent: Double, // e.g. +0.74%
    val changeAbsolute: Double, // e.g. +20.0
    val volume: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)

object TradingViewHelper {

    private val quoteCache = ConcurrentHashMap<String, TradingViewQuote>()

    val popularStocks = listOf(
        StockTickerItem("BBRI", "Bank Rakyat Indonesia (Persero) Tbk", "Perbankan"),
        StockTickerItem("BBCA", "Bank Central Asia Tbk", "Perbankan"),
        StockTickerItem("BMRI", "Bank Mandiri (Persero) Tbk", "Perbankan"),
        StockTickerItem("BBNI", "Bank Negara Indonesia (Persero) Tbk", "Perbankan"),
        StockTickerItem("BRIS", "Bank Syariah Indonesia Tbk", "Perbankan"),
        StockTickerItem("TLKM", "Telkom Indonesia (Persero) Tbk", "Telekomunikasi"),
        StockTickerItem("ASII", "Astra International Tbk", "Otomotif & Industri"),
        StockTickerItem("GOTO", "GoTo Gojek Tokopedia Tbk", "Teknologi"),
        StockTickerItem("AMMN", "Amman Mineral Internasional Tbk", "Pertambangan"),
        StockTickerItem("ANTM", "Aneka Tambang Tbk", "Pertambangan"),
        StockTickerItem("ICBP", "Indofood CBP Sukses Makmur Tbk", "Konsumsi"),
        StockTickerItem("INDF", "Indofood Sukses Makmur Tbk", "Konsumsi"),
        StockTickerItem("UNVR", "Unilever Indonesia Tbk", "Konsumsi"),
        StockTickerItem("ADRO", "Adaro Energy Indonesia Tbk", "Energi"),
        StockTickerItem("PGAS", "Perusahaan Gas Negara Tbk", "Energi"),
        StockTickerItem("KLBF", "Kalbe Farma Tbk", "Kesehatan"),
        StockTickerItem("MDKA", "Merdeka Copper Gold Tbk", "Pertambangan"),
        StockTickerItem("ACES", "Aspirasi Hidup Indonesia Tbk", "Ritel"),
        StockTickerItem("CPIN", "Charoen Pokphand Indonesia Tbk", "Agrikultur"),
        StockTickerItem("SMGR", "Semen Indonesia (Persero) Tbk", "Semen"),
        StockTickerItem("INCO", "Vale Indonesia Tbk", "Nikel"),
        StockTickerItem("PTBA", "Bukit Asam Tbk", "Batubara"),
        StockTickerItem("MEDC", "Medco Energi Internasional Tbk", "Minyak & Gas"),
        StockTickerItem("BUKA", "Bukalapak.com Tbk", "Teknologi"),
        StockTickerItem("ARTO", "Bank Jago Tbk", "Bank Digital"),
        StockTickerItem("TPIA", "Chandra Asri Pacific Tbk", "Petrokimia"),
        StockTickerItem("BREN", "Barito Renewables Tbk", "Energi Terbarukan"),
        StockTickerItem("CUAN", "Petrindo Jaya Kreasi Tbk", "Pertambangan"),
        StockTickerItem("GGRM", "Gudang Garam Tbk", "Konsumsi"),
        StockTickerItem("HMSP", "HM Sampoerna Tbk", "Konsumsi"),
        StockTickerItem("MYOR", "Mayora Indah Tbk", "Konsumsi")
    )

    init {
        // Pre-populate baseline popular Indonesian stock quotes
        val baseline = mapOf(
            "BBRI" to 2710.0,
            "BBCA" to 5125.0,
            "BMRI" to 3610.0,
            "BBNI" to 3320.0,
            "BRIS" to 1130.0,
            "TLKM" to 1990.0,
            "ASII" to 4930.0,
            "GOTO" to 24.0,
            "ANTM" to 2720.0
        )
        baseline.forEach { (code, price) ->
            val sym = "IDX:$code"
            val q = TradingViewQuote(sym, code, price, 0.0, 0.0, 0.0, System.currentTimeMillis())
            quoteCache[sym] = q
            quoteCache[code] = q
        }
    }

    fun extractTickerCode(input: String): String {
        val clean = input.trim().uppercase()
        if (clean.contains(":")) {
            return clean.substringAfter(":")
        }
        // Direct company name keywords
        if (clean.contains("MANDIRI")) return "BMRI"
        if (clean.contains("CENTRAL ASIA")) return "BBCA"
        if (clean.contains("RAKYAT INDONESIA")) return "BBRI"
        if (clean.contains("NEGARA INDONESIA")) return "BBNI"
        if (clean.contains("SYARIAH INDONESIA")) return "BRIS"
        if (clean.contains("TELKOM")) return "TLKM"
        if (clean.contains("ASTRA")) return "ASII"
        if (clean.contains("GOJEK") || clean.contains("TOKOPEDIA")) return "GOTO"
        if (clean.contains("TAMBANG") || clean.contains("ANTAM")) return "ANTM"
        if (clean.contains("INDOFOOD")) return "ICBP"
        if (clean.contains("UNILEVER")) return "UNVR"
        if (clean.contains("GAS NEGARA")) return "PGAS"
        if (clean.contains("KALBE")) return "KLBF"
        if (clean.contains("BUKIT ASAM")) return "PTBA"

        for (item in popularStocks) {
            if (clean.contains(item.code)) return item.code
        }
        val words = clean.split(" ", "_", "-", "/")
        for (w in words) {
            val wordOnly = w.filter { it.isLetter() }
            if (wordOnly.length in 4..5 && !wordOnly.equals("SAHAM", ignoreCase = true)) return wordOnly
        }
        return clean.filter { it.isLetterOrDigit() }.ifBlank { "BBRI" }
    }

    fun formatSymbol(input: String): String {
        val trimmed = input.trim().uppercase()
        if (trimmed.isEmpty()) return "IDX:BBRI"
        if (trimmed.contains(":")) return trimmed
        if (trimmed.contains("BTC") || trimmed.contains("BITCOIN")) return "BINANCE:BTCUSDT"
        if (trimmed.contains("ETH") || trimmed.contains("ETHEREUM")) return "BINANCE:ETHUSDT"
        if (trimmed.contains("GOLD") || trimmed.contains("EMAS")) return "TVC:GOLD"
        val code = extractTickerCode(trimmed)
        return "IDX:$code"
    }

    fun getDisplayName(symbol: String): String {
        val clean = symbol.substringAfter(":")
        val found = popularStocks.firstOrNull { it.code.equals(clean, ignoreCase = true) }
        return if (found != null) "${found.code} - ${found.name}" else symbol
    }

    fun getCachedQuote(symbol: String): TradingViewQuote? {
        val formatted = formatSymbol(symbol)
        val code = extractTickerCode(symbol)
        return quoteCache[formatted] ?: quoteCache[code]
    }

    fun getAllCachedQuotes(): Map<String, TradingViewQuote> = quoteCache.toMap()

    suspend fun fetchSingleQuote(symbol: String): TradingViewQuote? = withContext(Dispatchers.IO) {
        val formatted = formatSymbol(symbol)
        val quotes = fetchLiveQuotes(listOf(formatted))
        quotes[formatted] ?: quotes[extractTickerCode(symbol)] ?: quoteCache[formatted] ?: quoteCache[extractTickerCode(symbol)]
    }

    suspend fun fetchLiveQuotes(symbols: List<String>): Map<String, TradingViewQuote> = withContext(Dispatchers.IO) {
        if (symbols.isEmpty()) return@withContext quoteCache.toMap()
        val formattedSymbols = symbols.map { formatSymbol(it) }.distinct()
        val result = mutableMapOf<String, TradingViewQuote>()

        val idxSymbols = formattedSymbols.filter { it.startsWith("IDX:") }
        val otherSymbols = formattedSymbols.filter { !it.startsWith("IDX:") }

        if (idxSymbols.isNotEmpty()) {
            try {
                val quotes = executeScanRequest("https://scanner.tradingview.com/indonesia/scan", idxSymbols)
                result.putAll(quotes)
            } catch (e: Exception) {
                Log.e("TradingViewHelper", "Error scanning Indonesia symbols: ${e.message}")
            }
        }

        if (otherSymbols.isNotEmpty()) {
            try {
                val quotes = executeScanRequest("https://scanner.tradingview.com/global/scan", otherSymbols)
                result.putAll(quotes)
            } catch (e: Exception) {
                Log.e("TradingViewHelper", "Error scanning Global symbols: ${e.message}")
            }
        }

        result.forEach { (sym, quote) ->
            quoteCache[sym] = quote
            quoteCache[quote.tickerCode] = quote
        }

        quoteCache.toMap()
    }

    private fun executeScanRequest(endpoint: String, symbols: List<String>): Map<String, TradingViewQuote> {
        val quotesMap = mutableMapOf<String, TradingViewQuote>()
        var conn: HttpURLConnection? = null
        try {
            val url = URL(endpoint)
            conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36")
            conn.connectTimeout = 5000
            conn.readTimeout = 7000
            conn.doOutput = true
            conn.doInput = true

            val jsonBody = JSONObject().apply {
                val symObj = JSONObject().apply {
                    val tickersArr = JSONArray()
                    symbols.forEach { tickersArr.put(it) }
                    put("tickers", tickersArr)
                }
                put("symbols", symObj)
                val colsArr = JSONArray().apply {
                    put("close")
                    put("change")
                    put("change_abs")
                    put("volume")
                }
                put("columns", colsArr)
            }

            conn.outputStream.use { os ->
                os.write(jsonBody.toString().toByteArray(Charsets.UTF_8))
                os.flush()
            }

            val responseCode = conn.responseCode
            if (responseCode in 200..299) {
                val responseText = conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                val root = JSONObject(responseText)
                val dataArr = root.optJSONArray("data")
                if (dataArr != null) {
                    for (i in 0 until dataArr.length()) {
                        val item = dataArr.getJSONObject(i)
                        val sym = item.getString("s")
                        val dArr = item.getJSONArray("d")
                        val close = dArr.optDouble(0, 0.0)
                        val changePercent = dArr.optDouble(1, 0.0)
                        val changeAbs = dArr.optDouble(2, 0.0)
                        val vol = dArr.optDouble(3, 0.0)
                        val tickerCode = sym.substringAfter(":")
                        val quote = TradingViewQuote(
                            symbol = sym,
                            tickerCode = tickerCode,
                            closePrice = close,
                            changePercent = changePercent,
                            changeAbsolute = changeAbs,
                            volume = vol,
                            timestamp = System.currentTimeMillis()
                        )
                        quotesMap[sym] = quote
                        quotesMap[tickerCode] = quote
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("TradingViewHelper", "Exception in executeScanRequest: ${e.message}")
        } finally {
            conn?.disconnect()
        }
        return quotesMap
    }

    fun generateAdvancedChartHtml(symbol: String, interval: String = "D"): String {
        val safeSymbol = formatSymbol(symbol)
        return """
            <!DOCTYPE html>
            <html>
            <head>
              <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
              <style>
                * { box-sizing: border-box; }
                html, body {
                  margin: 0;
                  padding: 0;
                  width: 100%;
                  height: 100%;
                  background-color: #12181F;
                  overflow: hidden;
                }
                .tradingview-widget-container {
                  width: 100%;
                  height: 100%;
                }
                #tradingview_chart {
                  width: 100%;
                  height: 100%;
                }
              </style>
            </head>
            <body>
              <div class="tradingview-widget-container">
                <div id="tradingview_chart"></div>
                <script type="text/javascript">
                  window.onerror = function(msg, url, line) {
                    console.log("TV log: " + msg);
                    return true;
                  };
                </script>
                <script type="text/javascript" src="https://s3.tradingview.com/tv.js"></script>
                <script type="text/javascript">
                  function initTV() {
                    try {
                      if (typeof TradingView !== 'undefined' && TradingView.widget) {
                        new TradingView.widget({
                          "autosize": true,
                          "symbol": "$safeSymbol",
                          "interval": "$interval",
                          "timezone": "Asia/Jakarta",
                          "theme": "dark",
                          "style": "1",
                          "locale": "id",
                          "toolbar_bg": "#12181F",
                          "enable_publishing": false,
                          "allow_symbol_change": true,
                          "container_id": "tradingview_chart",
                          "hide_side_toolbar": false,
                          "withdateranges": true,
                          "details": true,
                          "hotlist": false,
                          "calendar": false,
                          "studies": [
                            "MASimple@tv-basicstudies",
                            "RSI@tv-basicstudies"
                          ]
                        });
                      }
                    } catch (e) {
                      console.log("Error initializing widget: " + e);
                    }
                  }
                  if (document.readyState === 'loading') {
                    document.addEventListener('DOMContentLoaded', initTV);
                  } else {
                    initTV();
                  }
                </script>
              </div>
            </body>
            </html>
        """.trimIndent()
    }

    fun generateMiniChartHtml(symbol: String): String {
        val safeSymbol = formatSymbol(symbol)
        return """
            <!DOCTYPE html>
            <html>
            <head>
              <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
              <style>
                * { box-sizing: border-box; }
                html, body {
                  margin: 0;
                  padding: 0;
                  width: 100%;
                  height: 100%;
                  background-color: #12181F;
                  overflow: hidden;
                }
                .tradingview-widget-container {
                  width: 100%;
                  height: 100%;
                }
                .tradingview-widget-container__widget {
                  width: 100%;
                  height: 100%;
                }
              </style>
            </head>
            <body>
              <div class="tradingview-widget-container">
                <div class="tradingview-widget-container__widget"></div>
                <script type="text/javascript" src="https://s3.tradingview.com/external-embedding/embed-widget-symbol-overview.js" async>
                {
                  "symbols": [
                    [
                      "$safeSymbol|1D"
                    ]
                  ],
                  "chartOnly": false,
                  "width": "100%",
                  "height": "100%",
                  "locale": "id",
                  "colorTheme": "dark",
                  "autosize": true,
                  "showVolume": true,
                  "showMA": true,
                  "hideDateRanges": false,
                  "hideMarketStatus": false,
                  "hideSymbolLogo": false,
                  "scalePosition": "right",
                  "scaleMode": "Normal",
                  "fontFamily": "-apple-system, BlinkMacSystemFont, Trebuchet MS, Roboto, Ubuntu, sans-serif",
                  "fontSize": "10",
                  "noTimeScale": false,
                  "valuesTracking": "1",
                  "changeMode": "price-and-percent",
                  "chartType": "area",
                  "maLineColor": "#3B82F6",
                  "maLineWidth": 1,
                  "maLength": 9,
                  "lineWidth": 2,
                  "lineType": 0,
                  "dateRanges": [
                    "1d|1",
                    "1m|30",
                    "3m|60",
                    "12m|1D",
                    "all|1M"
                  ]
                }
                </script>
              </div>
            </body>
            </html>
        """.trimIndent()
    }

    fun getExternalTradingViewUrl(symbol: String): String {
        val safeSymbol = formatSymbol(symbol).replace(":", "-")
        return "https://id.tradingview.com/symbols/$safeSymbol/"
    }
}
