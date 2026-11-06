package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.Transaction
import com.example.data.model.TransactionType
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CsvExportResult(
    val file: File,
    val uri: Uri,
    val fileName: String,
    val totalRecords: Int
)

object CsvExportHelper {

    fun generateAndSaveCsv(
        context: Context,
        periodLabel: String,
        transactions: List<Transaction>
    ): CsvExportResult? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val cleanPeriod = periodLabel.replace(" ", "_").replace("/", "-")
            val fileName = "MoneyTracker_${cleanPeriod}_$timeStamp.csv"

            val cacheDir = File(context.cacheDir, "exports")
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }

            val file = File(cacheDir, fileName)
            FileOutputStream(file).use { fos ->
                // Write UTF-8 BOM so Excel opens non-ASCII and commas flawlessly
                fos.write(0xEF)
                fos.write(0xBB)
                fos.write(0xBF)

                OutputStreamWriter(fos, StandardCharsets.UTF_8).use { writer ->
                    // CSV Header
                    writer.write("Tanggal,Waktu,Deskripsi,Kategori,Subkategori,Dompet/Akun,Tipe Transaksi,Nominal (IDR),Catatan,Label,Tujuan Transfer,Biaya Transfer (IDR)\n")

                    transactions.forEach { tx ->
                        val dateFormatted = String.format(Locale.US, "%04d-%02d-%02d", tx.year, tx.monthIndex, tx.dayOfMonth)
                        val typeLabel = when (tx.type) {
                            TransactionType.INCOME -> "Pemasukan"
                            TransactionType.EXPENSE -> "Pengeluaran"
                            TransactionType.TRANSFER -> "Transfer"
                        }
                        val escapedTitle = escapeCsv(tx.title)
                        val escapedCategory = escapeCsv(tx.category)
                        val escapedSubcategory = escapeCsv(tx.subcategory)
                        val escapedAccount = escapeCsv(tx.accountName)
                        val escapedNote = escapeCsv(tx.note)
                        val escapedLabel = escapeCsv(tx.label)
                        val escapedToAccount = escapeCsv(tx.toAccountName)

                        val line = listOf(
                            dateFormatted,
                            tx.time,
                            escapedTitle,
                            escapedCategory,
                            escapedSubcategory,
                            escapedAccount,
                            typeLabel,
                            String.format(Locale.US, "%.2f", tx.amount),
                            escapedNote,
                            escapedLabel,
                            escapedToAccount,
                            String.format(Locale.US, "%.2f", tx.transferFee)
                        ).joinToString(",")

                        writer.write(line + "\n")
                    }
                    writer.flush()
                }
            }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            CsvExportResult(
                file = file,
                uri = uri,
                fileName = fileName,
                totalRecords = transactions.size
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun escapeCsv(text: String): String {
        if (text.isEmpty()) return ""
        val needsQuotes = text.contains(",") || text.contains("\"") || text.contains("\n") || text.contains("\r")
        return if (needsQuotes) {
            "\"" + text.replace("\"", "\"\"") + "\""
        } else {
            text
        }
    }

    fun shareCsv(context: Context, uri: Uri, fileName: String) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Laporan Keuangan CSV: $fileName")
                putExtra(Intent.EXTRA_TEXT, "Berikut adalah file ekspor data transaksi Money Tracker format spreadsheet (CSV).")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(shareIntent, "Bagikan Data Transaksi (Excel/Sheets)")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Gagal membagikan file CSV: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun openCsv(context: Context, uri: Uri) {
        try {
            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "text/csv")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(viewIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Tidak ditemukan aplikasi pembuka spreadsheet (Excel/Sheets)", Toast.LENGTH_SHORT).show()
        }
    }
}
