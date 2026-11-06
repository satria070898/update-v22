package com.example.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import com.example.util.ImageUploadHelper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AccountWallet
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSubtle
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.FinanceViewModel
import com.example.util.FinancialInstitution
import com.example.util.IndonesianInstitutionHelper
import com.example.util.InstitutionLogoBadge
import com.example.util.InstitutionType
import com.example.util.ThousandsSeparatorVisualTransformation

fun getWalletIconVector(iconName: String): ImageVector {
    val inst = IndonesianInstitutionHelper.resolveInstitution("", iconName)
    if (inst.icon != null) return inst.icon
    return when (iconName.lowercase()) {
        "tunai", "cash" -> Icons.Default.Payments
        "bank" -> Icons.Default.AccountBalance
        "dompet digital", "digital" -> Icons.Default.PhoneAndroid
        "kartu kredit", "credit card" -> Icons.Default.CreditCard
        "tabungan", "savings" -> Icons.Default.Savings
        "investasi", "invest" -> Icons.Default.TrendingUp
        "kerja", "work" -> Icons.Default.Work
        "belanja", "shop" -> Icons.Default.ShoppingBag
        "rumah", "home" -> Icons.Default.Home
        "kendaraan", "car" -> Icons.Default.DirectionsCar
        else -> Icons.Default.AccountBalanceWallet
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateWalletDialog(
    viewModel: FinanceViewModel,
    walletToEdit: AccountWallet? = null,
    onDismiss: () -> Unit
) {
    var walletName by remember { mutableStateOf(walletToEdit?.name ?: "") }
    var selectedAccountType by remember { mutableStateOf(walletToEdit?.bankType ?: "Bank") }
    var selectedCurrency by remember { mutableStateOf(walletToEdit?.currency ?: "IDR") }
    var selectedIconName by remember { mutableStateOf(walletToEdit?.iconName ?: "Bank") }

    val initialBalanceStr = remember(walletToEdit) {
        if (walletToEdit != null) {
            if (walletToEdit.balance % 1.0 == 0.0) {
                walletToEdit.balance.toLong().toString()
            } else {
                walletToEdit.balance.toString()
            }
        } else ""
    }
    var balanceText by remember { mutableStateOf(initialBalanceStr) }
    var excludeFromNetWorth by remember { mutableStateOf(walletToEdit?.excludeFromNetWorth ?: false) }
    var excludeFromBudget by remember { mutableStateOf(walletToEdit?.excludeFromBudget ?: false) }

    var showCurrencyDropdown by remember { mutableStateOf(false) }
    var showIconPicker by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val walletImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedPath = ImageUploadHelper.saveImageToInternalStorage(context, uri, "wallet")
            if (savedPath != null) {
                selectedIconName = "custom:$savedPath"
                showIconPicker = false
            }
        }
    }

    val accountTypes = listOf(
        Pair("Tunai", Icons.Default.Payments),
        Pair("Bank", Icons.Default.AccountBalance),
        Pair("Dompet digital", Icons.Default.PhoneAndroid),
        Pair("Kartu Kredit", Icons.Default.CreditCard),
        Pair("Tabungan", Icons.Default.Savings)
    )

    val currencies = listOf(
        "Indonesian Rupiah (IDR)",
        "US Dollar (USD)",
        "Euro (EUR)",
        "Singapore Dollar (SGD)",
        "Malaysian Ringgit (MYR)",
        "Japanese Yen (JPY)"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header Bar: [X]  Buat Akun Baru
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_close_create_wallet")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = TextWhite,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (walletToEdit == null) "Buat Akun Baru" else "Edit Akun",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }

                    // Spacer or Delete button if editing
                    if (walletToEdit != null) {
                        IconButton(
                            onClick = { showDeleteConfirm = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Hapus Akun",
                                tint = ExpenseRed,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(36.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Form Body (Scrollable)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    // 1. Nama Dompet
                    Column {
                        Text(
                            text = "Nama Dompet",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurfaceElevated)
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                        ) {
                            BasicTextField(
                                value = walletName,
                                onValueChange = { walletName = it },
                                textStyle = TextStyle(
                                    color = TextWhite,
                                    fontSize = 14.sp
                                ),
                                cursorBrush = SolidColor(AccentBlue),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_wallet_name"),
                                decorationBox = { innerTextField ->
                                    if (walletName.isEmpty()) {
                                        Text(
                                            text = "Nama Dompet",
                                            fontSize = 14.sp,
                                            color = TextSubtle
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                        }
                    }

                    // 2. Jenis Akun
                    Column {
                        Text(
                            text = "Jenis Akun",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            accountTypes.forEach { (typeName, iconVector) ->
                                val isSelected = selectedAccountType.contains(typeName, ignoreCase = true)
                                val borderColor = if (isSelected) AccentBlue else DarkCardBorder
                                val bgTint = if (isSelected) Color(0x2B2563EB) else DarkSurfaceElevated

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(bgTint)
                                        .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(10.dp))
                                        .clickable {
                                            selectedAccountType = typeName
                                            selectedIconName = typeName
                                        }
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                        .testTag("pill_type_$typeName")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = iconVector,
                                            contentDescription = null,
                                            tint = if (isSelected) AccentBlue else TextMuted,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = typeName,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else TextMuted
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 3. Pilih Mata Uang
                    Column {
                        Text(
                            text = "Pilih Mata Uang",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurfaceElevated)
                                .clickable { showCurrencyDropdown = true }
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                                .testTag("select_currency_button")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val currentCurrencyLabel = currencies.firstOrNull { it.contains(selectedCurrency) }
                                    ?: "Indonesian Rupiah (IDR)"
                                Text(
                                    text = currentCurrencyLabel,
                                    fontSize = 14.sp,
                                    color = TextWhite
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showCurrencyDropdown,
                                onDismissRequest = { showCurrencyDropdown = false },
                                modifier = Modifier.background(DarkSurface)
                            ) {
                                currencies.forEach { currItem ->
                                    DropdownMenuItem(
                                        text = { Text(currItem, color = TextWhite, fontSize = 13.sp) },
                                        onClick = {
                                            selectedCurrency = if (currItem.contains("IDR")) "IDR"
                                            else if (currItem.contains("USD")) "USD"
                                            else if (currItem.contains("EUR")) "EUR"
                                            else if (currItem.contains("SGD")) "SGD"
                                            else if (currItem.contains("MYR")) "MYR"
                                            else "JPY"
                                            showCurrencyDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // 4. Ikon / Logo Lembaga Keuangan
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Logo Bank / E-Wallet",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextWhite
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Unggah",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AccentBlue,
                                    modifier = Modifier
                                        .clickable {
                                            walletImagePickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        }
                                        .padding(vertical = 4.dp, horizontal = 4.dp)
                                )
                                Text(
                                    text = "•",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                                Text(
                                    text = "Lihat Semua",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AccentBlue,
                                    modifier = Modifier
                                        .clickable { showIconPicker = true }
                                        .padding(vertical = 4.dp, horizontal = 4.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        val resolvedInst = IndonesianInstitutionHelper.resolveInstitution(walletName, selectedIconName)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurfaceElevated)
                                .clickable { showIconPicker = true }
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                                .testTag("select_icon_button")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    InstitutionLogoBadge(
                                        walletName = walletName,
                                        iconName = selectedIconName,
                                        size = 36.dp
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = resolvedInst.name,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextWhite
                                        )
                                        if (resolvedInst.subtitle.isNotBlank()) {
                                            Text(
                                                text = resolvedInst.subtitle,
                                                fontSize = 11.sp,
                                                color = TextMuted
                                            )
                                        }
                                    }
                                }

                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        // Quick selection chips for top Indonesian banks and e-wallets
                        Text(
                            text = "Pilihan Populer:",
                            fontSize = 11.sp,
                            color = TextSubtle
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        val quickPicks = listOf("bca", "mandiri", "bri", "bni", "bsi", "jago", "gopay", "ovo", "dana", "shopeepay", "cash")
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            quickPicks.forEach { id ->
                                val inst = IndonesianInstitutionHelper.resolveInstitution("", id)
                                val isSelected = selectedIconName.equals(id, ignoreCase = true)
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = if (isSelected) inst.brandColor.copy(alpha = 0.25f) else DarkSurfaceElevated,
                                    border = BorderStroke(1.dp, if (isSelected) inst.brandColor else DarkCardBorder),
                                    modifier = Modifier.clickable {
                                        selectedIconName = inst.id
                                        if (walletName.isBlank() || quickPicks.any { quickId ->
                                                val q = IndonesianInstitutionHelper.resolveInstitution("", quickId)
                                                walletName.equals(q.name, ignoreCase = true) || walletName.equals(q.shortName, ignoreCase = true)
                                            }) {
                                            walletName = inst.name
                                        }
                                        selectedAccountType = when (inst.type) {
                                            InstitutionType.BANK -> "Bank"
                                            InstitutionType.E_WALLET -> "Dompet digital"
                                            InstitutionType.CASH_OTHER -> if (inst.id == "credit_card") "Kartu Kredit" else if (inst.id == "savings") "Tabungan" else "Tunai"
                                        }
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        InstitutionLogoBadge(
                                            walletName = "",
                                            iconName = inst.id,
                                            size = 20.dp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = inst.shortName,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) Color.White else TextWhite
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 5. Saldo
                    Column {
                        Text(
                            text = "Saldo",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurfaceElevated)
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Rp",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                BasicTextField(
                                    value = balanceText,
                                    onValueChange = { input ->
                                        val digits = input.filter { it.isDigit() }
                                        if (digits.length <= 11) {
                                            balanceText = digits
                                        }
                                    },
                                    visualTransformation = ThousandsSeparatorVisualTransformation('.'),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    textStyle = TextStyle(
                                        color = TextWhite,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    cursorBrush = SolidColor(AccentBlue),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("input_wallet_balance"),
                                    decorationBox = { innerTextField ->
                                        if (balanceText.isEmpty()) {
                                            Text(
                                                text = "Saldo",
                                                fontSize = 14.sp,
                                                color = TextSubtle
                                            )
                                        }
                                        innerTextField()
                                    }
                                )

                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF232D3F)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "+",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                    )
                                }
                            }
                        }
                    }

                    // 6. Bagian dari Perhitungan
                    Column(
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = "Bagian dari Perhitungan",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextWhite
                        )

                        // Toggle 1: Keluarkan dari kekayaan bersih
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Keluarkan dari kekayaan bersih",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextWhite
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Saldonya masih ditampilkan di sini; hanya tidak ditambahkan ke total Anda.",
                                    fontSize = 11.sp,
                                    color = TextSubtle,
                                    lineHeight = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Switch(
                                checked = excludeFromNetWorth,
                                onCheckedChange = { excludeFromNetWorth = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = AccentBlue,
                                    uncheckedThumbColor = Color.Gray,
                                    uncheckedTrackColor = DarkSurfaceElevated
                                ),
                                modifier = Modifier.testTag("switch_exclude_net_worth")
                            )
                        }

                        // Toggle 2: Keluarkan dari anggaran
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Keluarkan dari anggaran",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextWhite
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Pengeluaran di sini tidak akan dihitung terhadap anggaran yang mencakup semua akun.",
                                    fontSize = 11.sp,
                                    color = TextSubtle,
                                    lineHeight = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Switch(
                                checked = excludeFromBudget,
                                onCheckedChange = { excludeFromBudget = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = AccentBlue,
                                    uncheckedThumbColor = Color.Gray,
                                    uncheckedTrackColor = DarkSurfaceElevated
                                ),
                                modifier = Modifier.testTag("switch_exclude_budget")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Fixed Bottom Action Bar: "Buat Akun" / "Simpan Perubahan"
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = DarkBackground,
                    border = BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.5f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 56.dp)
                    ) {
                        Button(
                            onClick = {
                                if (walletName.isNotBlank()) {
                                    val bal = balanceText.toDoubleOrNull() ?: 0.0
                                    val typeLabel = "$selectedAccountType · $selectedCurrency"
                                    if (walletToEdit == null) {
                                        viewModel.addWallet(
                                            AccountWallet(
                                                name = walletName.trim(),
                                                bankType = typeLabel,
                                                balance = bal,
                                                currency = selectedCurrency,
                                                iconName = selectedIconName,
                                                excludeFromNetWorth = excludeFromNetWorth,
                                                excludeFromBudget = excludeFromBudget
                                            )
                                        )
                                    } else {
                                        viewModel.updateWallet(
                                            walletToEdit.copy(
                                                name = walletName.trim(),
                                                bankType = typeLabel,
                                                balance = bal,
                                                currency = selectedCurrency,
                                                iconName = selectedIconName,
                                                excludeFromNetWorth = excludeFromNetWorth,
                                                excludeFromBudget = excludeFromBudget
                                            )
                                        )
                                    }
                                    onDismiss()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("btn_save_wallet"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentBlue
                            )
                        ) {
                            Text(
                                text = if (walletToEdit == null) "Buat Akun" else "Simpan Perubahan",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    // Sub-dialog: Bank & E-Wallet Logo Picker
    if (showIconPicker) {
        var activeTab by remember { androidx.compose.runtime.mutableIntStateOf(0) } // 0: Semua, 1: Bank, 2: E-Wallet, 3: Tunai & Lain
        val allInstitutions = IndonesianInstitutionHelper.institutions
        val filteredList = when (activeTab) {
            1 -> allInstitutions.filter { it.type == InstitutionType.BANK }
            2 -> allInstitutions.filter { it.type == InstitutionType.E_WALLET }
            3 -> allInstitutions.filter { it.type == InstitutionType.CASH_OTHER }
            else -> allInstitutions
        }

        Dialog(onDismissRequest = { showIconPicker = false }) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pilih Bank / E-Wallet",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        IconButton(
                            onClick = { showIconPicker = false },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Tutup",
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = AccentBlue.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, AccentBlue.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                walletImagePickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = AccentBlue,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Unggah Gambar dari Galeri",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                                Text(
                                    text = "Gunakan logo atau foto kustom sendiri",
                                    fontSize = 10.5.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tab selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkSurfaceElevated, RoundedCornerShape(10.dp))
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Semua", "Bank", "E-Wallet", "Lainnya").forEachIndexed { idx, label ->
                            val isSel = activeTab == idx
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) AccentBlue else Color.Transparent,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { activeTab = idx }
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) Color.White else TextMuted,
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.height(290.dp)
                    ) {
                        items(filteredList) { inst ->
                            val isSel = selectedIconName.equals(inst.id, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isSel) inst.brandColor.copy(alpha = 0.25f) else DarkSurfaceElevated)
                                    .border(
                                        BorderStroke(1.2.dp, if (isSel) inst.brandColor else DarkCardBorder),
                                        RoundedCornerShape(14.dp)
                                    )
                                    .clickable {
                                        selectedIconName = inst.id
                                        if (walletName.isBlank()) {
                                            walletName = inst.name
                                        }
                                        selectedAccountType = when (inst.type) {
                                            InstitutionType.BANK -> "Bank"
                                            InstitutionType.E_WALLET -> "Dompet digital"
                                            InstitutionType.CASH_OTHER -> if (inst.id == "credit_card") "Kartu Kredit" else if (inst.id == "savings") "Tabungan" else "Tunai"
                                        }
                                        showIconPicker = false
                                    }
                                    .padding(vertical = 12.dp, horizontal = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    InstitutionLogoBadge(
                                        walletName = "",
                                        iconName = inst.id,
                                        size = 32.dp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = inst.shortName,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.White else TextWhite,
                                        maxLines = 1,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                    if (inst.subtitle.isNotBlank()) {
                                        Text(
                                            text = inst.subtitle,
                                            fontSize = 8.5.sp,
                                            color = TextMuted,
                                            maxLines = 1,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { showIconPicker = false },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated)
                    ) {
                        Text("Tutup", color = TextWhite, fontSize = 13.sp)
                    }
                }
            }
        }
    }

    // Confirmation dialog for deleting wallet
    if (showDeleteConfirm && walletToEdit != null) {
        Dialog(onDismissRequest = { showDeleteConfirm = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Hapus Akun Dompet?",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Apakah Anda yakin ingin menghapus akun '${walletToEdit.name}'? Data akun ini akan dihapus.",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = { showDeleteConfirm = false },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Batal", color = TextWhite)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(
                            onClick = {
                                viewModel.deleteWallet(walletToEdit.id)
                                showDeleteConfirm = false
                                onDismiss()
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
                        ) {
                            Text("Hapus", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
