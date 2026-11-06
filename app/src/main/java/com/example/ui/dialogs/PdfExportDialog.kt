package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TextWhite
import com.example.util.PdfExportResult
import com.example.util.PdfReportHelper

@Composable
fun PdfExportSuccessDialog(
    exportResult: PdfExportResult,
    periodLabel: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("dialog_pdf_export_success"),
            shape = RoundedCornerShape(24.dp),
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Success icon badge
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(IncomeGreen.copy(alpha = 0.15f), CircleShape)
                        .border(1.5.dp, IncomeGreen.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = null,
                        tint = IncomeGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Laporan PDF Berhasil Disimpan",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Laporan keuangan periode $periodLabel telah diekspor dalam format dokumen PDF.",
                    fontSize = 13.sp,
                    color = TextSubtle,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // File Info Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurfaceElevated,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = AccentBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = exportResult.fileName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextWhite,
                                maxLines = 1
                            )
                            Text(
                                text = if (exportResult.savedToDownloads) {
                                    "Tersimpan di Unduhan / FinTrack & Dokumen"
                                } else {
                                    "Tersimpan di Dokumen FinTrack"
                                },
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions: Buka PDF and Bagikan
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            PdfReportHelper.openPdf(context, exportResult.uri)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_open_pdf"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextWhite
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null,
                            tint = AccentBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Buka PDF", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            PdfReportHelper.sharePdf(context, exportResult.uri, periodLabel)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_share_pdf"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentBlue,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Bagikan", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tutup", color = TextSubtle, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun CsvExportSuccessDialog(
    exportResult: com.example.util.CsvExportResult,
    periodLabel: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("dialog_csv_export_success"),
            shape = RoundedCornerShape(24.dp),
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Success icon badge
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(IncomeGreen.copy(alpha = 0.15f), CircleShape)
                        .border(1.5.dp, IncomeGreen.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = IncomeGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "File Excel / CSV Siap!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${exportResult.totalRecords} transaksi periode $periodLabel berhasil diekspor ke format spreadsheet.",
                    fontSize = 13.sp,
                    color = TextSubtle,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurfaceElevated,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = IncomeGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = exportResult.fileName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextWhite,
                                maxLines = 1
                            )
                            Text(
                                text = "Kompatibel Microsoft Excel & Google Sheets",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            com.example.util.CsvExportHelper.openCsv(context, exportResult.uri)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null,
                            tint = AccentBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Buka CSV", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            com.example.util.CsvExportHelper.shareCsv(context, exportResult.uri, exportResult.fileName)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentBlue,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Bagikan", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tutup", color = TextSubtle, fontSize = 13.sp)
                }
            }
        }
    }
}

