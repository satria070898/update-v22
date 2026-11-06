package com.example.ui.dialogs

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DebtItem

@Composable
fun DeleteDebtConfirmDialog(
    debt: DebtItem,
    linkedTxCount: Int,
    onConfirm: (deleteLinkedTransactions: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var deleteLinkedTransactions by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 380.dp),
                shape = RoundedCornerShape(26.dp),
                color = Color(0xFF191B20),
                border = BorderStroke(1.dp, Color(0xFF2D3039).copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp, vertical = 26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top circular icon container with dark burgundy circle & red trash icon
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF2E1C1F),
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = Color(0xFFEB5757),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Title
                    Text(
                        text = "Hapus Utang",
                        fontSize = 18.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Description text exactly as shown in screenshot
                    Text(
                        text = "Utang ini beserta catatannya akan dihapus. Transaksi yang dibuatnya di akun Anda tetap ada, kecuali Anda mencentang kotak di bawah.",
                        fontSize = 13.sp,
                        lineHeight = 18.5.sp,
                        color = Color(0xFFB5BAC7),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Checkbox card matching screenshot
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF262831),
                        border = BorderStroke(1.dp, Color(0xFF333742)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { deleteLinkedTransactions = !deleteLinkedTransactions }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Checkbox visual
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (deleteLinkedTransactions) Color(0xFF3B82F6) else Color.Transparent)
                                    .border(
                                        width = 1.8.dp,
                                        color = if (deleteLinkedTransactions) Color(0xFF3B82F6) else Color(0xFF7E8494),
                                        shape = RoundedCornerShape(4.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (deleteLinkedTransactions) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                val txLabel = if (linkedTxCount > 0) {
                                    "Hapus juga $linkedTxCount transaksi terkait"
                                } else {
                                    "Hapus juga transaksi terkait"
                                }
                                Text(
                                    text = txLabel,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "Saldo akun Anda berubah sebesar jumlah yang dipindahkan transaksi tersebut.",
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    color = Color(0xFF8E95A5)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Buttons side-by-side: Batal (dark) and Hapus (coral/red)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Batal button
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF13151A),
                            border = BorderStroke(1.dp, Color(0xFF292C36)),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onDismiss() }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "Batal",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // Hapus button (coral/red)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEB5757),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onConfirm(deleteLinkedTransactions)
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "Hapus",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
