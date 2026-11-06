package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.Transaction
import com.example.data.model.TransactionType
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PdfExportResult(
    val file: File,
    val uri: Uri,
    val fileName: String,
    val savedToDownloads: Boolean
)

object PdfReportHelper {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 36f
    private const val CONTENT_WIDTH = PAGE_WIDTH - (MARGIN * 2)

    fun generateAndSavePdf(
        context: Context,
        periodLabel: String,
        walletName: String,
        incomeTotal: Double,
        expenseTotal: Double,
        netCashFlow: Double,
        savingsRate: Int,
        expenseBreakdown: List<CategorySlice>,
        incomeBreakdown: List<CategorySlice>,
        transactions: List<Transaction>
    ): PdfExportResult? {
        return try {
            val pdfDocument = PdfDocument()

            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(30, 41, 59)
                textSize = 10f
            }
            val boldPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(15, 23, 42)
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textSize = 10f
            }
            val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.FILL
            }
            val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 1f
                color = Color.rgb(226, 232, 240)
            }

            val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))
            val printDate = sdf.format(Date())

            var pageNumber = 1
            var pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
            var currentPage = pdfDocument.startPage(pageInfo)
            var canvas = currentPage.canvas
            var yPos = MARGIN

        fun drawHeader() {
            // Header bar
            fillPaint.color = Color.rgb(15, 23, 42) // Slate 900
            val headerRect = RectF(MARGIN, yPos, MARGIN + CONTENT_WIDTH, yPos + 68f)
            canvas.drawRoundRect(headerRect, 10f, 10f, fillPaint)

            // Brand title
            boldPaint.color = Color.WHITE
            boldPaint.textSize = 18f
            canvas.drawText("FINTRACK", MARGIN + 16f, yPos + 28f, boldPaint)

            textPaint.color = Color.rgb(203, 213, 225)
            textPaint.textSize = 11f
            canvas.drawText("Laporan Keuangan", MARGIN + 16f, yPos + 46f, textPaint)

            // Right side of header: Period & Filter
            textPaint.textAlign = Paint.Align.RIGHT
            textPaint.color = Color.rgb(148, 163, 184)
            textPaint.textSize = 9f
            canvas.drawText("Periode: $periodLabel", MARGIN + CONTENT_WIDTH - 16f, yPos + 24f, textPaint)
            canvas.drawText("Akun / Dompet: $walletName", MARGIN + CONTENT_WIDTH - 16f, yPos + 38f, textPaint)
            canvas.drawText("Dicetak: $printDate", MARGIN + CONTENT_WIDTH - 16f, yPos + 52f, textPaint)
            textPaint.textAlign = Paint.Align.LEFT

            yPos += 80f
        }

        fun drawFooter(pageNum: Int) {
            strokePaint.color = Color.rgb(226, 232, 240)
            canvas.drawLine(MARGIN, PAGE_HEIGHT - 32f, MARGIN + CONTENT_WIDTH, PAGE_HEIGHT - 32f, strokePaint)

            textPaint.color = Color.rgb(148, 163, 184)
            textPaint.textSize = 8.5f
            canvas.drawText("Aplikasi Keuangan FinTrack • Laporan Resmi", MARGIN, PAGE_HEIGHT - 18f, textPaint)

            textPaint.textAlign = Paint.Align.RIGHT
            canvas.drawText("Halaman $pageNum", MARGIN + CONTENT_WIDTH, PAGE_HEIGHT - 18f, textPaint)
            textPaint.textAlign = Paint.Align.LEFT
        }

        // Draw page 1 header
        drawHeader()

        // 1. Executive Summary Cards (4 columns/boxes)
        val boxWidth = (CONTENT_WIDTH - 18f) / 3f
        val boxHeight = 54f

        // Box 1: Pemasukan
        fillPaint.color = Color.rgb(240, 253, 244) // Green 50
        canvas.drawRoundRect(RectF(MARGIN, yPos, MARGIN + boxWidth, yPos + boxHeight), 8f, 8f, fillPaint)
        strokePaint.color = Color.rgb(187, 247, 208)
        canvas.drawRoundRect(RectF(MARGIN, yPos, MARGIN + boxWidth, yPos + boxHeight), 8f, 8f, strokePaint)

        textPaint.color = Color.rgb(22, 101, 52)
        textPaint.textSize = 8.5f
        canvas.drawText("TOTAL PEMASUKAN", MARGIN + 10f, yPos + 18f, textPaint)
        boldPaint.color = Color.rgb(21, 128, 61)
        boldPaint.textSize = 12f
        canvas.drawText(CurrencyHelper.formatRupiah(incomeTotal), MARGIN + 10f, yPos + 38f, boldPaint)

        // Box 2: Pengeluaran
        val box2X = MARGIN + boxWidth + 9f
        fillPaint.color = Color.rgb(254, 242, 242) // Red 50
        canvas.drawRoundRect(RectF(box2X, yPos, box2X + boxWidth, yPos + boxHeight), 8f, 8f, fillPaint)
        strokePaint.color = Color.rgb(254, 202, 202)
        canvas.drawRoundRect(RectF(box2X, yPos, box2X + boxWidth, yPos + boxHeight), 8f, 8f, strokePaint)

        textPaint.color = Color.rgb(153, 27, 27)
        textPaint.textSize = 8.5f
        canvas.drawText("TOTAL PENGELUARAN", box2X + 10f, yPos + 18f, textPaint)
        boldPaint.color = Color.rgb(185, 28, 28)
        boldPaint.textSize = 12f
        canvas.drawText(CurrencyHelper.formatRupiah(expenseTotal), box2X + 10f, yPos + 38f, boldPaint)

        // Box 3: Arus Kas Bersih & Rasio
        val box3X = box2X + boxWidth + 9f
        fillPaint.color = if (netCashFlow >= 0) Color.rgb(239, 246, 255) else Color.rgb(255, 247, 237)
        canvas.drawRoundRect(RectF(box3X, yPos, box3X + boxWidth, yPos + boxHeight), 8f, 8f, fillPaint)
        strokePaint.color = if (netCashFlow >= 0) Color.rgb(191, 219, 254) else Color.rgb(254, 215, 170)
        canvas.drawRoundRect(RectF(box3X, yPos, box3X + boxWidth, yPos + boxHeight), 8f, 8f, strokePaint)

        textPaint.color = if (netCashFlow >= 0) Color.rgb(30, 64, 175) else Color.rgb(154, 52, 18)
        textPaint.textSize = 8.5f
        canvas.drawText("ARUS KAS BERSIH (${savingsRate}%)", box3X + 10f, yPos + 18f, textPaint)
        boldPaint.color = if (netCashFlow >= 0) Color.rgb(29, 78, 216) else Color.rgb(194, 65, 12)
        boldPaint.textSize = 12f
        canvas.drawText(CurrencyHelper.formatRupiah(netCashFlow), box3X + 10f, yPos + 38f, boldPaint)

        yPos += boxHeight + 18f

        // 2. Kategori Pengeluaran Teratas (Top 5)
        boldPaint.color = Color.rgb(30, 41, 59)
        boldPaint.textSize = 11f
        canvas.drawText("RINCIAN PENGELUARAN TERBESAR", MARGIN, yPos, boldPaint)
        yPos += 14f

        if (expenseBreakdown.isEmpty()) {
            textPaint.color = Color.rgb(148, 163, 184)
            textPaint.textSize = 9.5f
            canvas.drawText("Tidak ada data pengeluaran pada periode ini.", MARGIN, yPos, textPaint)
            yPos += 16f
        } else {
            val topExpenses = expenseBreakdown.take(5)
            topExpenses.forEach { slice ->
                val barMaxWidth = 180f
                val barWidth = (barMaxWidth * (slice.percentage / 100f).coerceIn(0.02f, 1f))

                textPaint.color = Color.rgb(51, 65, 85)
                textPaint.textSize = 9.5f
                val catLabel = slice.category.take(18)
                canvas.drawText(catLabel, MARGIN + 4f, yPos + 10f, textPaint)

                // Progress Bar
                fillPaint.color = Color.rgb(241, 245, 249)
                canvas.drawRoundRect(RectF(MARGIN + 120f, yPos + 2f, MARGIN + 120f + barMaxWidth, yPos + 12f), 4f, 4f, fillPaint)

                fillPaint.color = Color.rgb(239, 68, 68)
                canvas.drawRoundRect(RectF(MARGIN + 120f, yPos + 2f, MARGIN + 120f + barWidth, yPos + 12f), 4f, 4f, fillPaint)

                // Percentage and Amount
                textPaint.textAlign = Paint.Align.RIGHT
                textPaint.color = Color.rgb(71, 85, 105)
                val pctStr = String.format(Locale.US, "%.1f%%", slice.percentage)
                canvas.drawText(pctStr, MARGIN + 120f + barMaxWidth + 45f, yPos + 10f, textPaint)

                boldPaint.color = Color.rgb(15, 23, 42)
                boldPaint.textSize = 9.5f
                canvas.drawText(CurrencyHelper.formatRupiah(slice.amount), MARGIN + CONTENT_WIDTH, yPos + 10f, boldPaint)
                textPaint.textAlign = Paint.Align.LEFT

                yPos += 18f
            }
        }

        yPos += 10f

        // 3. Daftar Transaksi Table Header
        boldPaint.color = Color.rgb(30, 41, 59)
        boldPaint.textSize = 11f
        canvas.drawText("DAFTAR TRANSAKSI (${transactions.size} Transaksi)", MARGIN, yPos, boldPaint)
        yPos += 12f

        fun drawTableHeader() {
            fillPaint.color = Color.rgb(241, 245, 249) // Slate 100
            canvas.drawRect(MARGIN, yPos, MARGIN + CONTENT_WIDTH, yPos + 20f, fillPaint)

            boldPaint.color = Color.rgb(71, 85, 105)
            boldPaint.textSize = 8.5f
            canvas.drawText("TGL", MARGIN + 6f, yPos + 13f, boldPaint)
            canvas.drawText("KATEGORI", MARGIN + 50f, yPos + 13f, boldPaint)
            canvas.drawText("AKUN", MARGIN + 160f, yPos + 13f, boldPaint)
            canvas.drawText("CATATAN", MARGIN + 235f, yPos + 13f, boldPaint)
            canvas.drawText("JENIS", MARGIN + 375f, yPos + 13f, boldPaint)

            boldPaint.textAlign = Paint.Align.RIGHT
            canvas.drawText("NOMINAL", MARGIN + CONTENT_WIDTH - 6f, yPos + 13f, boldPaint)
            boldPaint.textAlign = Paint.Align.LEFT

            yPos += 22f
        }

        drawTableHeader()

        // Rows
        val sortedTransactions = transactions.sortedWith(
            TransactionMathHelper.transactionChronologicalDescendingComparator
        )

        val rowHeight = 18f
        val maxYPos = PAGE_HEIGHT - 45f

        for ((index, tx) in sortedTransactions.withIndex()) {
            // Check if page overflow
            if (yPos + rowHeight > maxYPos) {
                drawFooter(pageNumber)
                pdfDocument.finishPage(currentPage)

                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                currentPage = pdfDocument.startPage(pageInfo)
                canvas = currentPage.canvas
                yPos = MARGIN

                // Draw header on new page
                boldPaint.color = Color.rgb(15, 23, 42)
                boldPaint.textSize = 12f
                canvas.drawText("FINTRACK - LAPORAN TRANSAKSI (Lanjutan)", MARGIN, yPos + 12f, boldPaint)
                textPaint.color = Color.rgb(100, 116, 139)
                textPaint.textSize = 8.5f
                canvas.drawText("Periode: $periodLabel | Akun: $walletName", MARGIN, yPos + 24f, textPaint)
                yPos += 36f

                drawTableHeader()
            }

            // Alternating row background
            if (index % 2 == 1) {
                fillPaint.color = Color.rgb(248, 250, 252)
                canvas.drawRect(MARGIN, yPos, MARGIN + CONTENT_WIDTH, yPos + rowHeight, fillPaint)
            }

            textPaint.textSize = 8.5f
            textPaint.color = Color.rgb(51, 65, 85)

            // Date
            val dateStr = "${tx.dayOfMonth}/${tx.monthIndex}"
            canvas.drawText(dateStr, MARGIN + 6f, yPos + 12f, textPaint)

            // Category
            val catText = tx.category.take(18)
            canvas.drawText(catText, MARGIN + 50f, yPos + 12f, textPaint)

            // Account
            val accText = tx.accountName.take(12)
            canvas.drawText(accText, MARGIN + 160f, yPos + 12f, textPaint)

            // Note / Title
            val noteText = (if (tx.note.isNotBlank()) tx.note else tx.title).take(24)
            textPaint.color = Color.rgb(100, 116, 139)
            canvas.drawText(noteText, MARGIN + 235f, yPos + 12f, textPaint)

            // Type
            val typeStr = when (tx.type) {
                TransactionType.INCOME -> "Masuk"
                TransactionType.EXPENSE -> "Keluar"
                TransactionType.TRANSFER -> "Transfer"
            }
            textPaint.color = when (tx.type) {
                TransactionType.INCOME -> Color.rgb(22, 101, 52)
                TransactionType.EXPENSE -> Color.rgb(153, 27, 27)
                TransactionType.TRANSFER -> Color.rgb(30, 64, 175)
            }
            canvas.drawText(typeStr, MARGIN + 375f, yPos + 12f, textPaint)

            // Amount
            boldPaint.textAlign = Paint.Align.RIGHT
            boldPaint.textSize = 8.5f
            boldPaint.color = when (tx.type) {
                TransactionType.INCOME -> Color.rgb(21, 128, 61)
                TransactionType.EXPENSE -> Color.rgb(185, 28, 28)
                TransactionType.TRANSFER -> Color.rgb(37, 99, 235)
            }
            val prefix = when (tx.type) {
                TransactionType.INCOME -> "+"
                TransactionType.EXPENSE -> "-"
                TransactionType.TRANSFER -> ""
            }
            canvas.drawText("$prefix${CurrencyHelper.formatRupiah(tx.amount)}", MARGIN + CONTENT_WIDTH - 6f, yPos + 12f, boldPaint)
            boldPaint.textAlign = Paint.Align.LEFT

            yPos += rowHeight
        }

        // Draw footer on last page
        drawFooter(pageNumber)
        pdfDocument.finishPage(currentPage)

        // Generate filename
        val cleanPeriod = periodLabel.replace("[^a-zA-Z0-9]".toRegex(), "_")
        val timestamp = System.currentTimeMillis() % 100000
        val fileName = "Laporan_Keuangan_${cleanPeriod}_$timestamp.pdf"

        // 1. Save to app private external/cache documents
        val documentsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.cacheDir
        if (!documentsDir.exists()) documentsDir.mkdirs()
        val file = File(documentsDir, fileName)

        try {
            FileOutputStream(file).use { out ->
                pdfDocument.writeTo(out)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            return null
        } finally {
            pdfDocument.close()
        }

        // 2. Also save a copy to Public MediaStore Downloads if Android 10+ (Q)
        var savedToDownloads = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/FinTrack")
                }
                val downloadUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (downloadUri != null) {
                    resolver.openOutputStream(downloadUri)?.use { out ->
                        file.inputStream().use { input ->
                            input.copyTo(out)
                        }
                    }
                    savedToDownloads = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val contentUri = try {
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } catch (e: Exception) {
            Uri.fromFile(file)
        }
        PdfExportResult(
            file = file,
            uri = contentUri,
            fileName = fileName,
            savedToDownloads = savedToDownloads
        )
    } catch (t: Throwable) {
        t.printStackTrace()
        null
    }
}

    fun openPdf(context: Context, uri: Uri) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Buka Laporan PDF"))
        } catch (e: Exception) {
            Toast.makeText(context, "Tidak ada aplikasi pembaca PDF yang ditemukan", Toast.LENGTH_SHORT).show()
        }
    }

    fun sharePdf(context: Context, uri: Uri, periodLabel: String) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Laporan Keuangan $periodLabel")
                putExtra(Intent.EXTRA_TEXT, "Berikut adalah Laporan Keuangan FinTrack untuk periode $periodLabel.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Bagikan Laporan PDF"))
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal membagikan PDF", Toast.LENGTH_SHORT).show()
        }
    }
}
