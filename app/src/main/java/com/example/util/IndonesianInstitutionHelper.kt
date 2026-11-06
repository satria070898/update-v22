package com.example.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import java.io.File
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class InstitutionType {
    BANK,
    E_WALLET,
    CASH_OTHER
}

data class FinancialInstitution(
    val id: String,
    val name: String,
    val shortName: String,
    val brandColor: Color,
    val textColor: Color,
    val type: InstitutionType,
    val icon: ImageVector? = null,
    val subtitle: String = ""
)

object IndonesianInstitutionHelper {

    val institutions: List<FinancialInstitution> = listOf(
        // Bank-Bank di Indonesia
        FinancialInstitution(
            id = "bca",
            name = "Bank BCA",
            shortName = "BCA",
            brandColor = Color(0xFF00529C),
            textColor = Color.White,
            type = InstitutionType.BANK,
            subtitle = "Bank Central Asia"
        ),
        FinancialInstitution(
            id = "mandiri",
            name = "Bank Mandiri",
            shortName = "MANDIRI",
            brandColor = Color(0xFF002D62),
            textColor = Color(0xFFFDB913),
            type = InstitutionType.BANK,
            subtitle = "Bank Mandiri Persero"
        ),
        FinancialInstitution(
            id = "bri",
            name = "Bank BRI",
            shortName = "BRI",
            brandColor = Color(0xFF00529C),
            textColor = Color.White,
            type = InstitutionType.BANK,
            subtitle = "Bank Rakyat Indonesia"
        ),
        FinancialInstitution(
            id = "bni",
            name = "Bank BNI",
            shortName = "BNI",
            brandColor = Color(0xFF005E6A),
            textColor = Color.White,
            type = InstitutionType.BANK,
            subtitle = "Bank Negara Indonesia"
        ),
        FinancialInstitution(
            id = "bsi",
            name = "Bank Syariah Indonesia",
            shortName = "BSI",
            brandColor = Color(0xFF00A39D),
            textColor = Color.White,
            type = InstitutionType.BANK,
            subtitle = "Bank Syariah Indonesia"
        ),
        FinancialInstitution(
            id = "jago",
            name = "Bank Jago",
            shortName = "Jago",
            brandColor = Color(0xFFFF6600),
            textColor = Color.White,
            type = InstitutionType.BANK,
            subtitle = "PT Bank Jago Tbk"
        ),
        FinancialInstitution(
            id = "seabank",
            name = "SeaBank",
            shortName = "Sea",
            brandColor = Color(0xFFFE5D26),
            textColor = Color.White,
            type = InstitutionType.BANK,
            subtitle = "PT Bank Seabank Indonesia"
        ),
        FinancialInstitution(
            id = "blu",
            name = "Blu by BCA Digital",
            shortName = "blu",
            brandColor = Color(0xFF00A3E0),
            textColor = Color.White,
            type = InstitutionType.BANK,
            subtitle = "BCA Digital"
        ),
        FinancialInstitution(
            id = "jenius",
            name = "Jenius",
            shortName = "jenius",
            brandColor = Color(0xFF00ACEC),
            textColor = Color.White,
            type = InstitutionType.BANK,
            subtitle = "SMBC BTPN"
        ),
        FinancialInstitution(
            id = "cimb",
            name = "CIMB Niaga",
            shortName = "CIMB",
            brandColor = Color(0xFFEE1C25),
            textColor = Color.White,
            type = InstitutionType.BANK,
            subtitle = "PT Bank CIMB Niaga Tbk"
        ),
        FinancialInstitution(
            id = "permata",
            name = "Permata Bank",
            shortName = "Permata",
            brandColor = Color(0xFF76B82A),
            textColor = Color.White,
            type = InstitutionType.BANK,
            subtitle = "PT Bank Permata Tbk"
        ),
        FinancialInstitution(
            id = "danamon",
            name = "Bank Danamon",
            shortName = "Danamon",
            brandColor = Color(0xFF003B70),
            textColor = Color(0xFFFF8300),
            type = InstitutionType.BANK,
            subtitle = "PT Bank Danamon Indonesia"
        ),
        FinancialInstitution(
            id = "neo",
            name = "Bank Neo Commerce",
            shortName = "NEO",
            brandColor = Color(0xFFFDD835),
            textColor = Color(0xFF1E293B),
            type = InstitutionType.BANK,
            subtitle = "PT Bank Neo Commerce Tbk"
        ),
        FinancialInstitution(
            id = "btn",
            name = "Bank BTN",
            shortName = "BTN",
            brandColor = Color(0xFF004B87),
            textColor = Color.White,
            type = InstitutionType.BANK,
            subtitle = "Bank Tabungan Negara"
        ),

        // E-Wallet Indonesia
        FinancialInstitution(
            id = "gopay",
            name = "GoPay",
            shortName = "GoPay",
            brandColor = Color(0xFF00AED6),
            textColor = Color.White,
            type = InstitutionType.E_WALLET,
            subtitle = "Gojek Financial"
        ),
        FinancialInstitution(
            id = "ovo",
            name = "OVO",
            shortName = "OVO",
            brandColor = Color(0xFF4C3494),
            textColor = Color.White,
            type = InstitutionType.E_WALLET,
            subtitle = "OVO Cash"
        ),
        FinancialInstitution(
            id = "dana",
            name = "DANA",
            shortName = "DANA",
            brandColor = Color(0xFF118EEA),
            textColor = Color.White,
            type = InstitutionType.E_WALLET,
            subtitle = "Dompet Digital Indonesia"
        ),
        FinancialInstitution(
            id = "shopeepay",
            name = "ShopeePay",
            shortName = "Shopee",
            brandColor = Color(0xFFEE4D2D),
            textColor = Color.White,
            type = InstitutionType.E_WALLET,
            subtitle = "ShopeePay E-Money"
        ),
        FinancialInstitution(
            id = "linkaja",
            name = "LinkAja",
            shortName = "LinkAja",
            brandColor = Color(0xFFE1251B),
            textColor = Color.White,
            type = InstitutionType.E_WALLET,
            subtitle = "PT Fintek Karya Nusantara"
        ),
        FinancialInstitution(
            id = "astrapay",
            name = "AstraPay",
            shortName = "Astra",
            brandColor = Color(0xFF0055A5),
            textColor = Color.White,
            type = InstitutionType.E_WALLET,
            subtitle = "PT Astra Digital Arta"
        ),
        FinancialInstitution(
            id = "isaku",
            name = "i.saku",
            shortName = "i.saku",
            brandColor = Color(0xFF1976D2),
            textColor = Color.White,
            type = InstitutionType.E_WALLET,
            subtitle = "Indomaret E-Wallet"
        ),

        // Tunai & Lainnya
        FinancialInstitution(
            id = "cash",
            name = "Uang Tunai / Cash",
            shortName = "CASH",
            brandColor = Color(0xFF10B981),
            textColor = Color.White,
            type = InstitutionType.CASH_OTHER,
            icon = Icons.Default.Payments,
            subtitle = "Dompet Fisik"
        ),
        FinancialInstitution(
            id = "savings",
            name = "Tabungan / Celengan",
            shortName = "SAVE",
            brandColor = Color(0xFFF59E0B),
            textColor = Color.White,
            type = InstitutionType.CASH_OTHER,
            icon = Icons.Default.Savings,
            subtitle = "Dana Darurat / Tabungan"
        ),
        FinancialInstitution(
            id = "credit_card",
            name = "Kartu Kredit",
            shortName = "CC",
            brandColor = Color(0xFF6366F1),
            textColor = Color.White,
            type = InstitutionType.CASH_OTHER,
            icon = Icons.Default.CreditCard,
            subtitle = "Limit Kartu Kredit"
        ),
        FinancialInstitution(
            id = "invest",
            name = "Investasi / Sekuritas",
            shortName = "INVEST",
            brandColor = Color(0xFF8B5CF6),
            textColor = Color.White,
            type = InstitutionType.CASH_OTHER,
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            subtitle = "Reksadana, Saham & Kripto"
        )
    )

    private val idMap = institutions.associateBy { it.id.lowercase() }

    // Observable version tracker so all Composables automatically recompose when custom logos change
    var logoRegistryVersion by mutableIntStateOf(0)
        internal set

    // Registry of custom uploaded logos for wallets and assets
    private val customLogoRegistry = java.util.concurrent.ConcurrentHashMap<String, String>()

    fun registerCustomLogo(name: String, iconOrPath: String) {
        val trimmedName = name.trim().lowercase()
        val trimmedPath = iconOrPath.trim()
        if (trimmedName.isNotBlank() && trimmedPath.isNotBlank()) {
            customLogoRegistry[trimmedName] = trimmedPath
            val noSpaces = trimmedName.replace(" ", "")
            if (noSpaces.isNotBlank()) {
                customLogoRegistry[noSpaces] = trimmedPath
            }
            logoRegistryVersion++
        }
    }

    fun getRegisteredLogo(name: String): String? {
        @Suppress("UNUSED_VARIABLE")
        val v = logoRegistryVersion // Register Compose dependency
        val trimmed = name.trim().lowercase()
        if (trimmed.isBlank()) return null
        return customLogoRegistry[trimmed] ?: customLogoRegistry[trimmed.replace(" ", "")]
    }

    fun syncWithWalletsAndAssets(
        wallets: List<com.example.data.model.AccountWallet>,
        assets: List<com.example.data.model.AssetItem>
    ) {
        var changed = false
        wallets.forEach { w ->
            if (w.iconName.isNotBlank()) {
                val nameKey = w.name.trim().lowercase()
                val iconVal = w.iconName.trim()
                if (customLogoRegistry[nameKey] != iconVal) {
                    customLogoRegistry[nameKey] = iconVal
                    customLogoRegistry[nameKey.replace(" ", "")] = iconVal
                    customLogoRegistry[w.id.lowercase()] = iconVal
                    changed = true
                }
            }
        }
        assets.forEach { a ->
            val icon = if (a.iconUri.isNotBlank()) a.iconUri.trim() else a.platform.trim()
            if (icon.isNotBlank()) {
                val nameKey = a.name.trim().lowercase()
                if (customLogoRegistry[nameKey] != icon) {
                    customLogoRegistry[nameKey] = icon
                    customLogoRegistry[nameKey.replace(" ", "")] = icon
                    customLogoRegistry[a.id.lowercase()] = icon
                    if (a.platform.isNotBlank()) {
                        customLogoRegistry[a.platform.trim().lowercase()] = icon
                        customLogoRegistry[a.platform.trim().lowercase().replace(" ", "")] = icon
                    }
                    changed = true
                }
            }
        }
        if (changed) {
            logoRegistryVersion++
        }
    }

    fun resolveInstitution(walletName: String, iconName: String = ""): FinancialInstitution {
        val effectiveIcon = if (iconName.isNotBlank()) iconName else getRegisteredLogo(walletName).orEmpty()
        val iconKey = effectiveIcon.lowercase().trim()
        if (iconKey.isNotBlank()) {
            idMap[iconKey]?.let { return it }
        }

        val name = walletName.lowercase().trim()
        return when {
            // Specific Banks
            name.contains("bca digital") || name.contains("blu") -> idMap["blu"]!!
            name.contains("bca") -> idMap["bca"]!!
            name.contains("mandiri") || name.contains("livin") || name.contains("bmri") -> idMap["mandiri"]!!
            name.contains("bri") || name.contains("brimo") -> idMap["bri"]!!
            name.contains("bni") || name.contains("wondr") -> idMap["bni"]!!
            name.contains("bsi") || name.contains("syariah indonesia") -> idMap["bsi"]!!
            name.contains("jago") -> idMap["jago"]!!
            name.contains("seabank") || name.contains("sea bank") -> idMap["seabank"]!!
            name.contains("jenius") || name.contains("btpn") -> idMap["jenius"]!!
            name.contains("cimb") || name.contains("niaga") || name.contains("octo") -> idMap["cimb"]!!
            name.contains("permata") -> idMap["permata"]!!
            name.contains("danamon") -> idMap["danamon"]!!
            name.contains("neo") || name.contains("bnc") -> idMap["neo"]!!
            name.contains("btn") -> idMap["btn"]!!

            // E-Wallets
            name.contains("gopay") || name.contains("go-pay") || name.contains("gojek") -> idMap["gopay"]!!
            name.contains("ovo") -> idMap["ovo"]!!
            name.contains("dana") -> idMap["dana"]!!
            name.contains("shopee") || name.contains("spay") -> idMap["shopeepay"]!!
            name.contains("linkaja") || name.contains("link aja") || name.contains("tcash") -> idMap["linkaja"]!!
            name.contains("astrapay") || name.contains("astra") -> idMap["astrapay"]!!
            name.contains("isaku") || name.contains("i.saku") || name.contains("indomaret") -> idMap["isaku"]!!

            // Cash & Other
            name.contains("tunai") || name.contains("cash") || name.contains("dompet fisik") -> idMap["cash"]!!
            name.contains("tabungan") || name.contains("celengan") || name.contains("darurat") -> idMap["savings"]!!
            name.contains("kartu kredit") || name.contains("credit") || name.contains("visa") || name.contains("mastercard") -> idMap["credit_card"]!!
            name.contains("invest") || name.contains("saham") || name.contains("bibit") || name.contains("ajaib") || name.contains("crypto") -> idMap["invest"]!!

            // Generic Bank / Digital
            name.contains("bank") -> FinancialInstitution(
                id = "generic_bank",
                name = walletName,
                shortName = "BANK",
                brandColor = Color(0xFF1E3A8A),
                textColor = Color.White,
                type = InstitutionType.BANK,
                icon = Icons.Default.AccountBalance
            )
            name.contains("digital") || name.contains("wallet") || name.contains("dompet") -> FinancialInstitution(
                id = "generic_wallet",
                name = walletName,
                shortName = "PAY",
                brandColor = Color(0xFF0284C7),
                textColor = Color.White,
                type = InstitutionType.E_WALLET,
                icon = Icons.Default.PhoneAndroid
            )

            else -> FinancialInstitution(
                id = "generic_account",
                name = walletName,
                shortName = if (walletName.length <= 4) walletName.uppercase() else walletName.take(3).uppercase(),
                brandColor = Color(0xFF334155),
                textColor = Color.White,
                type = InstitutionType.CASH_OTHER,
                icon = Icons.Default.AccountBalance
            )
        }
    }
}

@Composable
fun InstitutionLogoBadge(
    walletName: String,
    iconName: String = "",
    size: Dp = 28.dp,
    isCircle: Boolean = false,
    modifier: Modifier = Modifier
) {
    val shape = if (isCircle) CircleShape else RoundedCornerShape((size.value * 0.28f).dp)
    val registryVersion = IndonesianInstitutionHelper.logoRegistryVersion

    val effectiveIcon = remember(iconName, walletName, registryVersion) {
        // Direct custom file/path parameter has priority
        if (iconName.isNotBlank() && (iconName.startsWith("custom:") || iconName.startsWith("/") || iconName.startsWith("file:"))) {
            iconName
        } else {
            // Check registered uploaded logos by walletName
            val registered = IndonesianInstitutionHelper.getRegisteredLogo(walletName)
            if (!registered.isNullOrBlank()) {
                registered
            } else if (iconName.isNotBlank()) {
                val registeredFromIcon = IndonesianInstitutionHelper.getRegisteredLogo(iconName)
                if (!registeredFromIcon.isNullOrBlank()) {
                    registeredFromIcon
                } else {
                    iconName
                }
            } else {
                ""
            }
        }
    }

    val customFile = remember(effectiveIcon, registryVersion) {
        if (effectiveIcon.startsWith("custom:")) {
            val path = effectiveIcon.removePrefix("custom:")
            File(path)
        } else if (effectiveIcon.startsWith("/") || effectiveIcon.startsWith("file:")) {
            File(effectiveIcon.removePrefix("file://").removePrefix("file:"))
        } else null
    }

    if (customFile != null && customFile.exists()) {
        Box(
            modifier = modifier
                .size(size)
                .clip(shape)
                .background(Color(0xFF1E293B))
                .border(0.8.dp, Color.White.copy(alpha = 0.25f), shape),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = customFile,
                contentDescription = walletName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    } else {
        val institution = IndonesianInstitutionHelper.resolveInstitution(walletName, effectiveIcon)

        Box(
            modifier = modifier
                .size(size)
                .clip(shape)
                .background(institution.brandColor)
                .border(0.8.dp, Color.White.copy(alpha = 0.15f), shape),
            contentAlignment = Alignment.Center
        ) {
            if (institution.icon != null) {
                Icon(
                    imageVector = institution.icon,
                    contentDescription = institution.name,
                    tint = institution.textColor,
                    modifier = Modifier.size((size.value * 0.58f).dp)
                )
            } else {
                val textLen = institution.shortName.length
                val fontSize = when {
                    textLen <= 3 -> (size.value * 0.38f).sp
                    textLen <= 5 -> (size.value * 0.30f).sp
                    textLen <= 6 -> (size.value * 0.25f).sp
                    else -> (size.value * 0.21f).sp
                }
                Text(
                    text = institution.shortName,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Black,
                    color = institution.textColor,
                    maxLines = 1
                )
            }
        }
    }
}
