package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TextWhite

data class StatCardConfig(
    val id: String,
    val title: String,
    val icon: String = "📊",
    val isVisible: Boolean = true
)

@Composable
fun CustomizeStatisticsDialog(
    initialCards: List<StatCardConfig>,
    isSideBySideTrends: Boolean,
    onDismiss: () -> Unit,
    onSave: (List<StatCardConfig>, Boolean) -> Unit
) {
    var cards by remember { mutableStateOf(initialCards) }
    var sideBySide by remember { mutableStateOf(isSideBySideTrends) }

    val visibleCount = cards.count { it.isVisible }

    Dialog(
        onDismissRequest = onDismiss,
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
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = TextWhite)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Sesuaikan Statistik",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "$visibleCount dari ${cards.size} kartu ditampilkan",
                            fontSize = 11.sp,
                            color = TextSubtle
                        )
                    }

                    Text(
                        text = "Atur Ulang",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AccentBlue,
                        modifier = Modifier
                            .clickable {
                                cards = cards.map { it.copy(isVisible = true) }
                                sideBySide = true
                            }
                            .padding(8.dp)
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Toggle: Tren berdampingan
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = DarkSurface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(AccentBlue.copy(alpha = 0.2f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("📊", fontSize = 16.sp)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Tren berdampingan",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextWhite
                                        )
                                        Text(
                                            text = "Pengeluaran dan Tren Pendapatan terbagi satu baris",
                                            fontSize = 11.sp,
                                            color = TextMuted
                                        )
                                    }
                                }

                                Switch(
                                    checked = sideBySide,
                                    onCheckedChange = { sideBySide = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = AccentBlue,
                                        uncheckedThumbColor = TextMuted,
                                        uncheckedTrackColor = DarkSurfaceElevated
                                    )
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    // Card List
                    items(cards, key = { it.id }) { card ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = DarkSurface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.DragHandle,
                                        contentDescription = null,
                                        tint = TextSubtle,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = card.icon,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = card.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (card.isVisible) TextWhite else TextMuted
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        cards = cards.map {
                                            if (it.id == card.id) it.copy(isVisible = !it.isVisible)
                                            else it
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (card.isVisible) Icons.Default.RemoveCircle else Icons.Default.AddCircle,
                                        contentDescription = null,
                                        tint = if (card.isVisible) ExpenseRed.copy(alpha = 0.8f) else IncomeGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }

                // Bottom Save Button
                Button(
                    onClick = {
                        onSave(cards, sideBySide)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                ) {
                    Text("Terapkan Pengaturan", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
