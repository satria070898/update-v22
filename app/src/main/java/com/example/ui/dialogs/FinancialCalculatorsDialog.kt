package com.example.ui.dialogs

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentBlueBright
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import android.widget.Toast
import com.example.ui.theme.TextWhite
import com.example.ui.theme.TrophyYellow
import com.example.util.CurrencyHelper
import kotlin.math.pow

enum class CalculatorTab(val title: String, val icon: ImageVector) {
    COMPOUND("Investasi & Bunga", Icons.Default.ShowChart),
    FIRE("Pensiun / FIRE", Icons.Default.Savings),
    MORTGAGE("Cicilan Pinjaman", Icons.Default.Home),
    BUDGET_50_30_20("Aturan 50/30/20", Icons.Default.PieChart),
    SPLIT_BILL("Bagi Tagihan", Icons.Default.Group),
    ZAKAT_PAJAK("Zakat & Pajak", Icons.Default.AccountBalance)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialCalculatorsDialog(
    currentMonthlyIncome: Double,
    currentMonthlyExpense: Double,
    totalBalance: Double = 0.0,
    initialTab: Int = 0,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTab by remember { mutableIntStateOf(initialTab) }

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
                .padding(bottom = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(AccentBlue.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = AccentBlueBright,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Alat & Kalkulator Finansial",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "Simulasi cerdas perencanaan keuangan profesional",
                            fontSize = 11.sp,
                            color = TextSubtle
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

            // Tab Selector Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalculatorTab.values().forEachIndexed { index, tab ->
                    val isSelected = selectedTab == index
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) AccentBlue else DarkSurfaceElevated,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) AccentBlueBright else DarkCardBorder
                        ),
                        modifier = Modifier.clickable { selectedTab = index }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = tab.title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tab Content
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "CalculatorTabTransition"
            ) { tabIndex ->
                when (tabIndex) {
                    0 -> CompoundInterestCalculatorView()
                    1 -> FireRetirementCalculatorView(currentMonthlyExpense)
                    2 -> MortgageLoanCalculatorView()
                    3 -> Budget503020RuleView(currentMonthlyIncome, currentMonthlyExpense)
                    4 -> SplitBillCalculatorView()
                    5 -> ZakatPajakCalculatorView(totalBalance, currentMonthlyIncome)
                }
            }
        }
    }
}

@Composable
private fun CompoundInterestCalculatorView() {
    var initialDepositText by remember { mutableStateOf("10000000") } // 10 juta
    var monthlyContributionText by remember { mutableStateOf("1500000") } // 1.5 juta
    var annualReturnRateText by remember { mutableStateOf("10") } // 10%
    var periodYearsText by remember { mutableStateOf("10") } // 10 tahun

    val initialDeposit = initialDepositText.toDoubleOrNull() ?: 0.0
    val monthlyContribution = monthlyContributionText.toDoubleOrNull() ?: 0.0
    val annualRatePct = (annualReturnRateText.toDoubleOrNull() ?: 10.0) / 100.0
    val years = periodYearsText.toIntOrNull() ?: 10

    // Compound interest calculation (monthly compounding)
    val monthlyRate = annualRatePct / 12.0
    val totalMonths = years * 12
    val fvInitial = initialDeposit * (1.0 + monthlyRate).pow(totalMonths.toDouble())
    val fvContributions = if (monthlyRate > 0) {
        monthlyContribution * ((1.0 + monthlyRate).pow(totalMonths.toDouble()) - 1.0) / monthlyRate
    } else {
        monthlyContribution * totalMonths
    }
    val totalFutureValue = fvInitial + fvContributions
    val totalPrincipalInvested = initialDeposit + (monthlyContribution * totalMonths)
    val totalInterestEarned = (totalFutureValue - totalPrincipalInvested).coerceAtLeast(0.0)

    val principalRatio = if (totalFutureValue > 0) (totalPrincipalInvested / totalFutureValue).toFloat().coerceIn(0f, 1f) else 0.5f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Result Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
            border = BorderStroke(1.dp, DarkCardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Estimasi Nilai Akumulasi Portofolio", fontSize = 12.sp, color = TextSubtle)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = CurrencyHelper.formatRupiah(totalFutureValue),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = IncomeGreen
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Progress Bar: Principal vs Interest
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Modal Pokok", fontSize = 10.sp, color = TextMuted)
                    Text("Bunga Berbunga", fontSize = 10.sp, color = IncomeGreen)
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { principalRatio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = AccentBlue,
                    trackColor = IncomeGreen,
                    strokeCap = StrokeCap.Round
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total Setoran Modal", fontSize = 11.sp, color = TextSubtle)
                        Text(
                            text = CurrencyHelper.formatRupiah(totalPrincipalInvested),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextWhite
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Keuntungan Bunga", fontSize = 11.sp, color = TextSubtle)
                        Text(
                            text = "+${CurrencyHelper.formatRupiah(totalInterestEarned)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = IncomeGreen
                        )
                    }
                }
            }
        }

        // Inputs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = initialDepositText,
                onValueChange = { initialDepositText = it.filter { c -> c.isDigit() } },
                label = { Text("Modal Awal (Rp)", fontSize = 11.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                colors = calculatorTextFieldColors()
            )
            OutlinedTextField(
                value = monthlyContributionText,
                onValueChange = { monthlyContributionText = it.filter { c -> c.isDigit() } },
                label = { Text("Setoran Rutin/Bln", fontSize = 11.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                colors = calculatorTextFieldColors()
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = annualReturnRateText,
                onValueChange = { annualReturnRateText = it },
                label = { Text("Return Tahunan (% p.a.)", fontSize = 11.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                colors = calculatorTextFieldColors()
            )
            OutlinedTextField(
                value = periodYearsText,
                onValueChange = { periodYearsText = it.filter { c -> c.isDigit() } },
                label = { Text("Durasi (Tahun)", fontSize = 11.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                colors = calculatorTextFieldColors()
            )
        }
    }
}

@Composable
private fun FireRetirementCalculatorView(currentMonthlyExpense: Double) {
    val defaultExpense = if (currentMonthlyExpense > 0) currentMonthlyExpense.toLong().toString() else "8000000"
    var monthlyExpenseText by remember { mutableStateOf(defaultExpense) }
    var currentAgeText by remember { mutableStateOf("28") }
    var targetRetireAgeText by remember { mutableStateOf("50") }
    var expectedInflationRateText by remember { mutableStateOf("4.5") }

    val monthlyExpense = monthlyExpenseText.toDoubleOrNull() ?: 8000000.0
    val currentAge = currentAgeText.toIntOrNull() ?: 28
    val targetRetireAge = targetRetireAgeText.toIntOrNull() ?: 50
    val inflationRate = (expectedInflationRateText.toDoubleOrNull() ?: 4.5) / 100.0

    val yearsToRetire = (targetRetireAge - currentAge).coerceAtLeast(1)
    // Inflation factor until retirement
    val inflationMultiplier = (1.0 + inflationRate).pow(yearsToRetire.toDouble())
    val monthlyExpenseAtRetire = monthlyExpense * inflationMultiplier
    val annualExpenseAtRetire = monthlyExpenseAtRetire * 12.0
    // FIRE 4% Safe Withdrawal Rule = 25x Annual Expense
    val fireNumber = annualExpenseAtRetire * 25.0

    // Required monthly investment to reach target (assuming 10% expected return on balanced portfolio)
    val assumedReturnRate = 0.10 / 12.0
    val totalMonths = yearsToRetire * 12
    val monthlySavingsNeeded = if (assumedReturnRate > 0) {
        fireNumber * assumedReturnRate / ((1.0 + assumedReturnRate).pow(totalMonths.toDouble()) - 1.0)
    } else {
        fireNumber / totalMonths
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
            border = BorderStroke(1.dp, DarkCardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Target Angka FIRE (Dana Pensiun Mandiri)", fontSize = 12.sp, color = TextSubtle)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = CurrencyHelper.formatRupiah(fireNumber),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TrophyYellow
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Sisa Waktu Menuju Pensiun", fontSize = 10.sp, color = TextSubtle)
                        Text("$yearsToRetire Tahun lagi", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Pengeluaran Bln Pensiun", fontSize = 10.sp, color = TextSubtle)
                        Text(CurrencyHelper.formatRupiah(monthlyExpenseAtRetire), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AccentBlue.copy(alpha = 0.15f))
                        .padding(10.dp)
                ) {
                    Column {
                        Text("Rekomendasi Investasi Bulanan:", fontSize = 11.sp, color = AccentBlueBright, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${CurrencyHelper.formatRupiah(monthlySavingsNeeded)} / bulan",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = currentAgeText,
                onValueChange = { currentAgeText = it.filter { c -> c.isDigit() } },
                label = { Text("Usia Sekarang", fontSize = 11.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                colors = calculatorTextFieldColors()
            )
            OutlinedTextField(
                value = targetRetireAgeText,
                onValueChange = { targetRetireAgeText = it.filter { c -> c.isDigit() } },
                label = { Text("Usia Target Pensiun", fontSize = 11.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                colors = calculatorTextFieldColors()
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = monthlyExpenseText,
                onValueChange = { monthlyExpenseText = it.filter { c -> c.isDigit() } },
                label = { Text("Pengeluaran Bulanan Saat Ini (Rp)", fontSize = 11.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                colors = calculatorTextFieldColors()
            )
            OutlinedTextField(
                value = expectedInflationRateText,
                onValueChange = { expectedInflationRateText = it },
                label = { Text("Inflasi (%/Thn)", fontSize = 11.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(0.6f),
                colors = calculatorTextFieldColors()
            )
        }
    }
}

@Composable
private fun MortgageLoanCalculatorView() {
    var loanAmountText by remember { mutableStateOf("250000000") } // 250 juta
    var annualInterestRateText by remember { mutableStateOf("8.5") } // 8.5%
    var tenorYearsText by remember { mutableStateOf("15") } // 15 tahun

    val loanAmount = loanAmountText.toDoubleOrNull() ?: 250000000.0
    val annualRate = (annualInterestRateText.toDoubleOrNull() ?: 8.5) / 100.0
    val tenorYears = tenorYearsText.toIntOrNull() ?: 15

    val monthlyRate = annualRate / 12.0
    val totalMonths = tenorYears * 12

    // Annuity Formula: PMT = P * i / (1 - (1+i)^-n)
    val monthlyPayment = if (monthlyRate > 0 && totalMonths > 0) {
        loanAmount * (monthlyRate / (1.0 - (1.0 + monthlyRate).pow(-totalMonths.toDouble())))
    } else {
        loanAmount / totalMonths.coerceAtLeast(1)
    }

    val totalPayment = monthlyPayment * totalMonths
    val totalInterest = (totalPayment - loanAmount).coerceAtLeast(0.0)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
            border = BorderStroke(1.dp, DarkCardBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Estimasi Angsuran / Cicilan per Bulan", fontSize = 12.sp, color = TextSubtle)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${CurrencyHelper.formatRupiah(monthlyPayment)} / bln",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentBlueBright
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Pokok Pinjaman", fontSize = 11.sp, color = TextSubtle)
                        Text(
                            text = CurrencyHelper.formatRupiah(loanAmount),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextWhite
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Total Bunga Pinjaman", fontSize = 11.sp, color = TextSubtle)
                        Text(
                            text = CurrencyHelper.formatRupiah(totalInterest),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ExpenseRed
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Pengembalian Dana:", fontSize = 11.sp, color = TextMuted)
                    Text(
                        text = CurrencyHelper.formatRupiah(totalPayment),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }
            }
        }

        OutlinedTextField(
            value = loanAmountText,
            onValueChange = { loanAmountText = it.filter { c -> c.isDigit() } },
            label = { Text("Plafon Pinjaman / KPR (Rp)", fontSize = 11.sp) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = calculatorTextFieldColors()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = annualInterestRateText,
                onValueChange = { annualInterestRateText = it },
                label = { Text("Bunga (% p.a.)", fontSize = 11.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                colors = calculatorTextFieldColors()
            )
            OutlinedTextField(
                value = tenorYearsText,
                onValueChange = { tenorYearsText = it.filter { c -> c.isDigit() } },
                label = { Text("Tenor (Tahun)", fontSize = 11.sp) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                colors = calculatorTextFieldColors()
            )
        }
    }
}

@Composable
private fun Budget503020RuleView(
    currentIncome: Double,
    currentExpense: Double
) {
    val baseIncome = if (currentIncome > 0) currentIncome else 10000000.0
    var incomeInputText by remember { mutableStateOf(baseIncome.toLong().toString()) }
    val incomeVal = incomeInputText.toDoubleOrNull() ?: baseIncome

    val needsBudget = incomeVal * 0.50
    val wantsBudget = incomeVal * 0.30
    val savingsBudget = incomeVal * 0.20

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = incomeInputText,
            onValueChange = { incomeInputText = it.filter { c -> c.isDigit() } },
            label = { Text("Penghasilan Bersih Bulanan (Rp)", fontSize = 11.sp) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = calculatorTextFieldColors()
        )

        // 50% Kebutuhan
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
            border = BorderStroke(1.dp, DarkCardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF3B82F6).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("50%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF60A5FA))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Kebutuhan Pokok (Needs)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        Text("Sewa, tagihan, makanan, cicilan wajib", fontSize = 10.sp, color = TextSubtle)
                    }
                }
                Text(
                    text = CurrencyHelper.formatRupiah(needsBudget),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF60A5FA)
                )
            }
        }

        // 30% Keinginan
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
            border = BorderStroke(1.dp, DarkCardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF59E0B).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("30%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFBBF24))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Keinginan (Wants)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        Text("Hiburan, ngopi, shopping, hobi", fontSize = 10.sp, color = TextSubtle)
                    }
                }
                Text(
                    text = CurrencyHelper.formatRupiah(wantsBudget),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFBBF24)
                )
            }
        }

        // 20% Tabungan / Investasi
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
            border = BorderStroke(1.dp, DarkCardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(IncomeGreen.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("20%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = IncomeGreen)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Tabungan & Investasi (Savings)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        Text("Dana darurat, saham, reksadana, emas", fontSize = 10.sp, color = TextSubtle)
                    }
                }
                Text(
                    text = CurrencyHelper.formatRupiah(savingsBudget),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = IncomeGreen
                )
            }
        }
    }
}

@Composable
private fun calculatorTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AccentBlue,
    unfocusedBorderColor = DarkCardBorder,
    focusedLabelColor = AccentBlueBright,
    unfocusedLabelColor = TextSubtle,
    focusedTextColor = TextWhite,
    unfocusedTextColor = TextWhite,
    cursorColor = AccentBlue
)

@Composable
private fun SplitBillCalculatorView() {
    var subtotalText by remember { mutableStateOf("250000") }
    var taxPercentText by remember { mutableStateOf("10") }
    var servicePercentText by remember { mutableStateOf("5") }
    var discountText by remember { mutableStateOf("0") }
    var peopleCount by remember { mutableIntStateOf(4) }

    val subtotal = subtotalText.toDoubleOrNull() ?: 0.0
    val taxPct = taxPercentText.toDoubleOrNull() ?: 0.0
    val servicePct = servicePercentText.toDoubleOrNull() ?: 0.0
    val discount = discountText.toDoubleOrNull() ?: 0.0

    val taxAmount = subtotal * (taxPct / 100.0)
    val serviceAmount = subtotal * (servicePct / 100.0)
    val grandTotal = (subtotal + taxAmount + serviceAmount - discount).coerceAtLeast(0.0)
    val perPerson = if (peopleCount > 0) grandTotal / peopleCount else grandTotal

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Result Card - Highlighted & Modern
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
            border = BorderStroke(
                1.5.dp,
                Brush.linearGradient(
                    listOf(AccentBlueBright, Color(0xFF6366F1), Color(0x33FFFFFF))
                )
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Patungan per Orang",
                    fontSize = 12.sp,
                    color = TextSubtle,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = CurrencyHelper.formatRupiah(perPerson),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentBlueBright,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "untuk $peopleCount orang (Total: ${CurrencyHelper.formatRupiah(grandTotal)})",
                    fontSize = 11.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Breakdown Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text("Subtotal", fontSize = 10.sp, color = TextSubtle)
                        Text(CurrencyHelper.formatRupiah(subtotal), fontSize = 12.sp, color = TextWhite, fontWeight = FontWeight.SemiBold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Pajak ($taxPercentText%)", fontSize = 10.sp, color = TextSubtle)
                        Text("+${CurrencyHelper.formatRupiah(taxAmount)}", fontSize = 12.sp, color = ExpenseRed, fontWeight = FontWeight.SemiBold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Service ($servicePercentText%)", fontSize = 10.sp, color = TextSubtle)
                        Text("+${CurrencyHelper.formatRupiah(serviceAmount)}", fontSize = 12.sp, color = TrophyYellow, fontWeight = FontWeight.SemiBold)
                    }
                }

                if (discount > 0.0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Diskon / Voucher Promo", fontSize = 11.sp, color = IncomeGreen)
                        Text("-${CurrencyHelper.formatRupiah(discount)}", fontSize = 12.sp, color = IncomeGreen, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Copy to WhatsApp button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AccentBlue.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, AccentBlue.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val summaryText = buildString {
                                appendLine("🧾 *Rincian Bagi Tagihan (Split Bill)*")
                                appendLine("━━━━━━━━━━━━━━━━━━━")
                                appendLine("• Subtotal: ${CurrencyHelper.formatRupiah(subtotal)}")
                                if (taxAmount > 0) appendLine("• Pajak Resto (${taxPercentText}%): +${CurrencyHelper.formatRupiah(taxAmount)}")
                                if (serviceAmount > 0) appendLine("• Service Charge (${servicePercentText}%): +${CurrencyHelper.formatRupiah(serviceAmount)}")
                                if (discount > 0) appendLine("• Diskon / Voucher: -${CurrencyHelper.formatRupiah(discount)}")
                                appendLine("━━━━━━━━━━━━━━━━━━━")
                                appendLine("💰 *Total Tagihan: ${CurrencyHelper.formatRupiah(grandTotal)}*")
                                appendLine("👥 *Patungan per Orang ($peopleCount orang): ${CurrencyHelper.formatRupiah(perPerson)}*")
                                appendLine("━━━━━━━━━━━━━━━━━━━")
                                appendLine("Yuk langsung transfer ya teman-teman! Terima kasih 🙌")
                            }
                            clipboardManager.setText(AnnotatedString(summaryText))
                            Toast.makeText(context, "Rincian berhasil disalin! Siap dikirim ke WhatsApp", Toast.LENGTH_SHORT).show()
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = AccentBlueBright,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Salin Format Rincian ke WhatsApp",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentBlueBright
                        )
                    }
                }
            }
        }

        // Input 1: Subtotal Tagihan
        OutlinedTextField(
            value = subtotalText,
            onValueChange = { subtotalText = it.filter { ch -> ch.isDigit() } },
            label = { Text("Subtotal Makanan & Minuman (Rp)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = calculatorTextFieldColors(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )

        // Input 2: Jumlah Orang dengan Button +/-
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
            border = BorderStroke(1.dp, DarkCardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Jumlah Orang Patungan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    Text("Bagi rata secara adil", fontSize = 10.sp, color = TextSubtle)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = DarkSurface,
                        border = BorderStroke(1.dp, DarkCardBorder),
                        modifier = Modifier
                            .size(38.dp)
                            .clickable { if (peopleCount > 1) peopleCount-- }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Remove, "Kurang", tint = TextWhite, modifier = Modifier.size(18.dp))
                        }
                    }

                    Text(
                        text = "$peopleCount",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Surface(
                        shape = CircleShape,
                        color = AccentBlue,
                        modifier = Modifier
                            .size(38.dp)
                            .clickable { peopleCount++ }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Add, "Tambah", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        // Input 3: Pajak Resto & Quick Chips
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Pajak Restoran / PB1 (%)", fontSize = 12.sp, color = TextSubtle)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("0", "10", "11").forEach { preset ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (taxPercentText == preset) AccentBlue else DarkSurfaceElevated,
                            modifier = Modifier.clickable { taxPercentText = preset }
                        ) {
                            Text(
                                text = "$preset%",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (taxPercentText == preset) Color.White else TextMuted,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = taxPercentText,
                onValueChange = { taxPercentText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = calculatorTextFieldColors(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )
        }

        // Input 4: Service Charge & Quick Chips
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Service Charge Resto (%)", fontSize = 12.sp, color = TextSubtle)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("0", "5", "7").forEach { preset ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (servicePercentText == preset) AccentBlue else DarkSurfaceElevated,
                            modifier = Modifier.clickable { servicePercentText = preset }
                        ) {
                            Text(
                                text = "$preset%",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (servicePercentText == preset) Color.White else TextMuted,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = servicePercentText,
                onValueChange = { servicePercentText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = calculatorTextFieldColors(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )
        }

        // Input 5: Diskon / Potongan Voucher
        OutlinedTextField(
            value = discountText,
            onValueChange = { discountText = it.filter { ch -> ch.isDigit() } },
            label = { Text("Potongan Diskon / Voucher Promo (Rp)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = calculatorTextFieldColors(),
            shape = RoundedCornerShape(14.dp),
            singleLine = true
        )
    }
}

@Composable
private fun ZakatPajakCalculatorView(
    totalBalance: Double,
    monthlyIncome: Double
) {
    var modeIndex by remember { mutableIntStateOf(0) } // 0: Zakat Maal, 1: Zakat Profesi, 2: Simulasi PPh 21

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Mode Sub-Tab
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Zakat Maal", "Zakat Profesi", "PPh 21").forEachIndexed { idx, title ->
                val isSelected = modeIndex == idx
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) AccentBlue else DarkSurfaceElevated,
                    border = BorderStroke(1.dp, if (isSelected) AccentBlueBright else DarkCardBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { modeIndex = idx }
                ) {
                    Text(
                        text = title,
                        fontSize = 11.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else TextMuted,
                        modifier = Modifier.padding(vertical = 8.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        when (modeIndex) {
            0 -> {
                // Zakat Maal (Simpanan / Tabungan / Aset Likuid)
                var assetWealthText by remember { mutableStateOf(if (totalBalance > 0) totalBalance.toLong().toString() else "50000000") }
                val goldPricePerGram = 1400000.0 // standar acuan emas ~Rp 1.400.000 / gram
                val nisabMaal = 85.0 * goldPricePerGram // 85 gram emas = Rp 119.000.000

                val assetWealth = assetWealthText.toDoubleOrNull() ?: 0.0
                val isMeetsNisab = assetWealth >= nisabMaal
                val annualZakat = if (isMeetsNisab) assetWealth * 0.025 else 0.0
                val monthlyZakat = annualZakat / 12.0

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                    border = BorderStroke(1.dp, if (isMeetsNisab) IncomeGreen.copy(alpha = 0.7f) else TrophyYellow.copy(alpha = 0.7f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Kewajiban Zakat Maal (2.5%)", fontSize = 12.sp, color = TextSubtle)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isMeetsNisab) IncomeGreen.copy(alpha = 0.2f) else TrophyYellow.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (isMeetsNisab) "Wajib Zakat (Mencapai Nisab)" else "Belum Mencapai Nisab",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isMeetsNisab) IncomeGreen else TrophyYellow,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isMeetsNisab) CurrencyHelper.formatRupiah(annualZakat) + " / tahun" else "Rp 0 (Belum Wajib)",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isMeetsNisab) IncomeGreen else TextWhite
                        )
                        if (isMeetsNisab) {
                            Text(
                                text = "Atau setara ${CurrencyHelper.formatRupiah(monthlyZakat)} per bulan",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Nisab acuan (85 gram emas @ Rp 1.400.000): ${CurrencyHelper.formatRupiah(nisabMaal)}. Haul kepemilikan: 1 tahun penuh.",
                            fontSize = 10.sp,
                            color = TextSubtle,
                            lineHeight = 14.sp
                        )
                    }
                }

                OutlinedTextField(
                    value = assetWealthText,
                    onValueChange = { assetWealthText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Total Harta / Simpanan Likuid (Rp)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = calculatorTextFieldColors(),
                    shape = RoundedCornerShape(14.dp),
                    trailingIcon = {
                        Text(
                            text = "Isi Saldo",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentBlueBright,
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .clickable {
                                    assetWealthText = totalBalance.toLong().toString()
                                }
                        )
                    }
                )
            }
            1 -> {
                // Zakat Profesi / Pendapatan Bulanan
                var incomeText by remember { mutableStateOf(if (monthlyIncome > 0) monthlyIncome.toLong().toString() else "8500000") }
                val nisabProfesiBulanan = 7000000.0 // Nisab bulanan setara 524 kg beras BAZNAS

                val incomeVal = incomeText.toDoubleOrNull() ?: 0.0
                val isMeetsProfesi = incomeVal >= nisabProfesiBulanan
                val zakatProfesiMonthly = if (isMeetsProfesi) incomeVal * 0.025 else 0.0

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                    border = BorderStroke(1.dp, if (isMeetsProfesi) IncomeGreen.copy(alpha = 0.7f) else DarkCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Zakat Penghasilan Bulanan (2.5%)", fontSize = 12.sp, color = TextSubtle)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isMeetsProfesi) IncomeGreen.copy(alpha = 0.2f) else DarkSurface
                            ) {
                                Text(
                                    text = if (isMeetsProfesi) "Wajib Zakat Profesi" else "Di bawah Nisab BAZNAS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isMeetsProfesi) IncomeGreen else TextMuted,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isMeetsProfesi) CurrencyHelper.formatRupiah(zakatProfesiMonthly) + " / bulan" else "Rp 0 (Sedekah Sukarela)",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isMeetsProfesi) IncomeGreen else TextWhite
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Nisab zakat profesi BAZNAS: Rp 7.000.000 / bulan (setara 524 kg beras pokok).",
                            fontSize = 10.sp,
                            color = TextSubtle
                        )
                    }
                }

                OutlinedTextField(
                    value = incomeText,
                    onValueChange = { incomeText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Pendapatan / Gaji Kotor Bulanan (Rp)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = calculatorTextFieldColors(),
                    shape = RoundedCornerShape(14.dp)
                )
            }
            else -> {
                // Simulasi PPh 21 (Skema Tarif Efektif PP 58/2023)
                var grossSalaryText by remember { mutableStateOf("12000000") }
                val grossSalary = grossSalaryText.toDoubleOrNull() ?: 0.0

                // Estimasi TER (Kategori A: TK/0)
                val estimatedTaxPct = when {
                    grossSalary <= 5400000.0 -> 0.0
                    grossSalary <= 5650000.0 -> 0.25
                    grossSalary <= 5950000.0 -> 0.5
                    grossSalary <= 6300000.0 -> 0.75
                    grossSalary <= 6750000.0 -> 1.0
                    grossSalary <= 7500000.0 -> 1.25
                    grossSalary <= 8550000.0 -> 1.5
                    grossSalary <= 9650000.0 -> 1.75
                    grossSalary <= 10050000.0 -> 2.0
                    grossSalary <= 10350000.0 -> 2.25
                    grossSalary <= 10700000.0 -> 2.5
                    grossSalary <= 11050000.0 -> 3.0
                    grossSalary <= 11600000.0 -> 3.5
                    grossSalary <= 12500000.0 -> 4.0
                    grossSalary <= 13750000.0 -> 5.0
                    grossSalary <= 15100000.0 -> 6.0
                    grossSalary <= 16950000.0 -> 7.0
                    grossSalary <= 19750000.0 -> 8.0
                    grossSalary <= 24150000.0 -> 9.0
                    else -> 10.0
                }
                val monthlyTax = grossSalary * (estimatedTaxPct / 100.0)
                val netSalary = grossSalary - monthlyTax

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                    border = BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Perkiraan Potongan PPh 21 Bulanan (TER)", fontSize = 12.sp, color = TextSubtle)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = CurrencyHelper.formatRupiah(monthlyTax),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = ExpenseRed
                        )
                        Text(
                            text = "Tarif Efektif: $estimatedTaxPct% · Estimasi Gaji Bersih: ${CurrencyHelper.formatRupiah(netSalary)}",
                            fontSize = 11.sp,
                            color = IncomeGreen
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Berdasarkan acuan PP 58/2023 Kategori A (Status TK/0: Tidak Kawin tanpa tanggungan).",
                            fontSize = 10.sp,
                            color = TextSubtle
                        )
                    }
                }

                OutlinedTextField(
                    value = grossSalaryText,
                    onValueChange = { grossSalaryText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Gaji Bruto Bulanan (Rp)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = calculatorTextFieldColors(),
                    shape = RoundedCornerShape(14.dp)
                )
            }
        }
    }
}

