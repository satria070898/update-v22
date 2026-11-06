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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.model.Transaction
import com.example.data.model.TransactionType
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentBlueBright
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TextWhite
import com.example.util.CategoryIconHelper
import com.example.util.CurrencyHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryTransactionSheet(
    categoryName: String,
    categoryType: TransactionType,
    periodLabel: String,
    transactions: List<Transaction>,
    onDismiss: () -> Unit,
    onNavigateToTransactions: (category: String) -> Unit,
    onAddTransactionForCategory: (category: String) -> Unit,
    onTransactionClick: (Transaction) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val catIcon = CategoryIconHelper.getCategoryIcon(categoryName)
    val catColor = CategoryIconHelper.getCategoryColor(categoryName, categoryType)

    val categoryTransactions = transactions.filter {
        it.category.equals(categoryName, ignoreCase = true)
    }
    val totalAmount = categoryTransactions.sumOf { it.amount }

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
                .padding(20.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(catColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = catIcon,
                            contentDescription = categoryName,
                            tint = catColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = categoryName,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "$periodLabel · ${categoryTransactions.size} transaksi",
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

            // Total Amount Banner
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = DarkSurfaceElevated,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total di Periode Ini", fontSize = 11.sp, color = TextSubtle)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = CurrencyHelper.formatRupiah(totalAmount),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (categoryType == TransactionType.INCOME) IncomeGreen else ExpenseRed
                        )
                    }

                    if (categoryTransactions.isNotEmpty()) {
                        val avg = totalAmount / categoryTransactions.size
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Rata-rata per transaksi", fontSize = 10.sp, color = TextSubtle)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = CurrencyHelper.formatRupiah(avg),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextWhite
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Daftar Transaksi",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Transaction List
            if (categoryTransactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada transaksi di kategori ini pada periode terpilih.",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categoryTransactions) { tx ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurfaceElevated,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onDismiss()
                                    onTransactionClick(tx)
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = DarkSurface,
                                        modifier = Modifier.padding(end = 10.dp)
                                    ) {
                                        Text(
                                            text = "${tx.dayOfMonth}/${tx.monthIndex}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AccentBlueBright,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = tx.title.ifEmpty { tx.category },
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextWhite,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "${tx.accountName}${if (tx.note.isNotEmpty()) " · ${tx.note}" else ""}",
                                            fontSize = 10.sp,
                                            color = TextMuted,
                                            maxLines = 1
                                        )
                                    }
                                }

                                Text(
                                    text = "${if (tx.type == TransactionType.INCOME) "+" else "-"}Rp${CurrencyHelper.formatCompact(tx.amount)}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (tx.type == TransactionType.INCOME) IncomeGreen else ExpenseRed
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onDismiss()
                        onNavigateToTransactions(categoryName)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentBlue)
                ) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ke Transaksi", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        onDismiss()
                        onAddTransactionForCategory(categoryName)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentBlue,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Catat Baru", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
