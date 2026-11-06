package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.summary.FinancialHealthScoreGauge
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentBlueBright
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.IncomeGreenSoft
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialHealthDetailSheet(
    score: Int = 80,
    savingsRatePct: Double = 83.5,
    netSurplusFormatted: String = "+Rp5.039.205",
    onDismiss: () -> Unit,
    onNavigateToBudgets: () -> Unit,
    onNavigateToPlanner: () -> Unit,
    onAddTransaction: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(IncomeGreen.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = IncomeGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Skor Kesehatan Keuangan",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "Evaluasi menyeluruh kebiasaan finansial Anda",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Score Banner
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = DarkSurfaceElevated,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = IncomeGreenSoft
                        ) {
                            Text(
                                text = "Status: Sangat Baik",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = IncomeGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Surplus: $netSurplusFormatted",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "Tingkat tabungan mencapai ${savingsRatePct.toInt()}% dari pemasukan",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    FinancialHealthScoreGauge(
                        score = score,
                        label = "$score/100",
                        modifier = Modifier.size(76.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "5 Pilar Kesehatan Finansial",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 5 Breakdown Items
            val pillars = listOf(
                PillarData("Tingkat Tabungan", "83.5% disimpan (Target min 20%)", 20, 20, IncomeGreen),
                PillarData("Kontrol Pengeluaran", "Hemat -80.4% dibanding bulan lalu", 20, 20, IncomeGreen),
                PillarData("Konsistensi Pelacakan", "2 dari 30 hari aktif mencatat", 4, 20, Color(0xFFF59E0B)),
                PillarData("Diversifikasi Pendapatan", "3 sumber pemasukan aktif", 16, 20, IncomeGreen),
                PillarData("Progres Anggaran", "3 kategori dalam batas aman", 20, 20, IncomeGreen)
            )

            pillars.forEach { pillar ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurfaceElevated,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(pillar.name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                            Text("${pillar.score}/${pillar.maxScore} poin", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = pillar.color)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { pillar.score.toFloat() / pillar.maxScore },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = pillar.color,
                            trackColor = Color(0xFF131826)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(pillar.desc, fontSize = 10.sp, color = TextSubtle)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Actionable Tips Box
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0x1A10B981),
                border = androidx.compose.foundation.BorderStroke(1.dp, IncomeGreen.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                    Text("💡", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Rekomendasi untuk Mencapai Skor 100/100",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = IncomeGreen
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Tingkatkan konsistensi pelacakan dengan mencatat transaksi harian. Rutinitas pencatatan 7 hari berturut-turut akan menambah +16 poin ke skor Anda!",
                            fontSize = 11.sp,
                            color = TextWhite,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Integration Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onNavigateToBudgets()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentBlue)
                ) {
                    Icon(imageVector = Icons.Default.PieChart, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Anggaran", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onNavigateToPlanner()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6366F1).copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF818CF8))
                ) {
                    Icon(imageVector = Icons.Default.EventRepeat, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Perencana", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        onDismiss()
                        onAddTransaction()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue, contentColor = Color.White)
                ) {
                    Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Catat", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private data class PillarData(
    val name: String,
    val desc: String,
    val score: Int,
    val maxScore: Int,
    val color: Color
)
