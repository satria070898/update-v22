package com.example.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Toll
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import java.io.File
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class AssetVisualProfile(
    val icon: ImageVector,
    val primaryColor: Color,
    val containerColor: Color,
    val monogram: String,
    val label: String
)

object AssetIconHelper {

    fun getAssetProfile(
        assetType: String,
        name: String = "",
        platform: String = ""
    ): AssetVisualProfile {
        val lowerType = assetType.lowercase()
        val lowerName = name.lowercase()
        val lowerPlatform = platform.lowercase()

        // 1. Emas & Logam Mulia
        if (lowerType.contains("emas") || lowerName.contains("emas") || lowerName.contains("gold") ||
            lowerPlatform.contains("antam") || lowerPlatform.contains("pegadaian") || lowerName.contains("logam mulia")
        ) {
            return AssetVisualProfile(
                icon = Icons.Default.WorkspacePremium,
                primaryColor = Color(0xFFF59E0B), // Gold Amber
                containerColor = Color(0x33F59E0B),
                monogram = "AU",
                label = "Emas"
            )
        }

        // 2. Saham (Stocks)
        if (lowerType.contains("saham") || lowerName.contains("saham") || lowerName.contains("stock") ||
            lowerName.contains("bbca") || lowerName.contains("bbri") || lowerName.contains("bmri") ||
            lowerName.contains("tlkm") || lowerName.contains("goto") || lowerPlatform.contains("stockbit") ||
            lowerPlatform.contains("ajaib") || lowerPlatform.contains("mirae") || lowerPlatform.contains("ipot")
        ) {
            return AssetVisualProfile(
                icon = Icons.AutoMirrored.Filled.ShowChart,
                primaryColor = Color(0xFF3B82F6), // Royal Blue
                containerColor = Color(0x333B82F6),
                monogram = "IDX",
                label = "Saham"
            )
        }

        // 3. Reksadana (Mutual Funds)
        if (lowerType.contains("reksa") || lowerName.contains("reksa") || lowerName.contains("fund") ||
            lowerPlatform.contains("bibit") || lowerPlatform.contains("bareksa") || lowerName.contains("pasar uang") ||
            lowerName.contains("pendapatan tetap") || lowerName.contains("campuran")
        ) {
            return AssetVisualProfile(
                icon = Icons.Default.PieChart,
                primaryColor = Color(0xFF8B5CF6), // Purple / Violet
                containerColor = Color(0x338B5CF6),
                monogram = "RD",
                label = "Reksadana"
            )
        }

        // 4. Kripto (Crypto)
        if (lowerType.contains("kripto") || lowerType.contains("crypto") || lowerName.contains("kripto") ||
            lowerName.contains("bitcoin") || lowerName.contains("btc") || lowerName.contains("eth") ||
            lowerName.contains("usdt") || lowerPlatform.contains("indodax") || lowerPlatform.contains("tokocrypto") ||
            lowerPlatform.contains("pintu") || lowerPlatform.contains("binance")
        ) {
            return AssetVisualProfile(
                icon = Icons.Default.CurrencyBitcoin,
                primaryColor = Color(0xFFF97316), // Crypto Orange
                containerColor = Color(0x33F97316),
                monogram = "BTC",
                label = "Kripto"
            )
        }

        // 5. Obligasi / SBN / Sukuk
        if (lowerType.contains("obligasi") || lowerName.contains("obligasi") || lowerName.contains("sbn") ||
            lowerName.contains("ori") || lowerName.contains("sukuk") || lowerName.contains("bond")
        ) {
            return AssetVisualProfile(
                icon = Icons.Default.Description,
                primaryColor = Color(0xFF0284C7), // Sky Blue
                containerColor = Color(0x330284C7),
                monogram = "SBN",
                label = "Obligasi"
            )
        }

        // 6. Deposito
        if (lowerType.contains("deposito") || lowerName.contains("deposito") || lowerName.contains("deposit")) {
            return AssetVisualProfile(
                icon = Icons.Default.Lock,
                primaryColor = Color(0xFF14B8A6), // Teal
                containerColor = Color(0x3314B8A6),
                monogram = "DEP",
                label = "Deposito"
            )
        }

        // 7. Properti & Tanah
        if (lowerType.contains("properti") || lowerName.contains("properti") || lowerName.contains("rumah") ||
            lowerName.contains("tanah") || lowerName.contains("apartemen") || lowerName.contains("ruko") ||
            lowerName.contains("property")
        ) {
            return AssetVisualProfile(
                icon = Icons.Default.HomeWork,
                primaryColor = Color(0xFF10B981), // Emerald Green
                containerColor = Color(0x3310B981),
                monogram = "PRP",
                label = "Properti"
            )
        }

        // 8. Kendaraan
        if (lowerType.contains("kendaraan") || lowerName.contains("mobil") || lowerName.contains("motor") ||
            lowerName.contains("vehicle") || lowerName.contains("vespa")
        ) {
            return AssetVisualProfile(
                icon = Icons.Default.DirectionsCar,
                primaryColor = Color(0xFF06B6D4), // Cyan
                containerColor = Color(0x3306B6D4),
                monogram = "KND",
                label = "Kendaraan"
            )
        }

        // 9. Bisnis / Usaha / Modal
        if (lowerType.contains("bisnis") || lowerName.contains("bisnis") || lowerName.contains("usaha") ||
            lowerName.contains("toko") || lowerName.contains("franchise") || lowerName.contains("modal")
        ) {
            return AssetVisualProfile(
                icon = Icons.Default.Storefront,
                primaryColor = Color(0xFFEC4899), // Pink Rose
                containerColor = Color(0x33EC4899),
                monogram = "BSN",
                label = "Bisnis"
            )
        }

        // 10. Tunai & Bank
        if (lowerType.contains("bank") || lowerType.contains("tunai") || lowerType.contains("cash")) {
            return AssetVisualProfile(
                icon = Icons.Default.AccountBalance,
                primaryColor = Color(0xFF38BDF8),
                containerColor = Color(0x3338BDF8),
                monogram = "BNK",
                label = "Bank"
            )
        }

        // Default / Koleksi / Aset Lainnya
        return AssetVisualProfile(
            icon = Icons.Default.Diamond,
            primaryColor = Color(0xFF34D399), // Mint Green
            containerColor = Color(0x3334D399),
            monogram = "AST",
            label = "Aset"
        )
    }

    /**
     * Platform badge color (for Bibit, Stockbit, Antam, Indodax, Ajaib, etc.)
     */
    fun getPlatformColor(platform: String): Color {
        val p = platform.lowercase()
        return when {
            p.contains("bibit") -> Color(0xFF00AA13) // Bibit Green
            p.contains("stockbit") -> Color(0xFF059669) // Stockbit Emerald
            p.contains("ajaib") -> Color(0xFF0284C7) // Ajaib Blue
            p.contains("pluang") -> Color(0xFFF59E0B) // Pluang Yellow
            p.contains("antam") -> Color(0xFFD97706) // Antam Gold
            p.contains("pegadaian") -> Color(0xFF008037) // Pegadaian Green
            p.contains("indodax") -> Color(0xFF0284C7) // Indodax Cyan-Blue
            p.contains("tokocrypto") -> Color(0xFFEAB308) // Tokocrypto Amber
            p.contains("pintu") -> Color(0xFF0A68F4) // Pintu Blue
            p.contains("bareksa") -> Color(0xFF16A34A) // Bareksa Green
            p.contains("bca") -> Color(0xFF0060AF)
            p.contains("mandiri") -> Color(0xFF003876)
            p.contains("bri") -> Color(0xFF00529C)
            p.contains("jago") -> Color(0xFFFF6900)
            p.contains("seabank") -> Color(0xFFF05A28)
            else -> Color(0xFF64748B)
        }
    }
}

data class AssetClassCategory(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val color: Color
)

val availableAssetCategories = listOf(
    AssetClassCategory("all", "Semua", Icons.Default.Category, Color(0xFF3B82F6)),
    AssetClassCategory("saham", "Saham", Icons.AutoMirrored.Filled.ShowChart, Color(0xFF3B82F6)),
    AssetClassCategory("emas", "Emas", Icons.Default.WorkspacePremium, Color(0xFFF59E0B)),
    AssetClassCategory("reksadana", "Reksadana", Icons.Default.PieChart, Color(0xFF8B5CF6)),
    AssetClassCategory("kripto", "Kripto", Icons.Default.CurrencyBitcoin, Color(0xFFF97316)),
    AssetClassCategory("obligasi", "Obligasi", Icons.Default.Description, Color(0xFF0284C7)),
    AssetClassCategory("deposito", "Deposito", Icons.Default.Lock, Color(0xFF14B8A6)),
    AssetClassCategory("properti", "Properti", Icons.Default.HomeWork, Color(0xFF10B981)),
    AssetClassCategory("bank", "Tunai & Bank", Icons.Default.AccountBalance, Color(0xFF38BDF8))
)

/**
 * Visual badge for assets with recognizable icons, platform brand hints, and high-contrast styling.
 */
@Composable
fun AssetLogoBadge(
    assetType: String,
    assetName: String = "",
    platform: String = "",
    iconUri: String = "",
    size: Dp = 32.dp,
    modifier: Modifier = Modifier
) {
    val cornerRadius = (size * 0.28f).coerceAtLeast(6.dp)
    val shape = RoundedCornerShape(cornerRadius)
    val registryVersion = IndonesianInstitutionHelper.logoRegistryVersion

    val effectiveTarget = remember(iconUri, assetName, platform, registryVersion) {
        if (iconUri.isNotBlank() && (iconUri.startsWith("custom:") || iconUri.startsWith("/") || iconUri.startsWith("file:"))) {
            iconUri
        } else {
            val registered = IndonesianInstitutionHelper.getRegisteredLogo(assetName)
                ?: (if (platform.isNotBlank()) IndonesianInstitutionHelper.getRegisteredLogo(platform) else null)
                ?: (if (iconUri.isNotBlank()) IndonesianInstitutionHelper.getRegisteredLogo(iconUri) else null)

            if (!registered.isNullOrBlank()) {
                registered
            } else if (iconUri.isNotBlank()) {
                iconUri
            } else {
                platform
            }
        }
    }

    val customFile = remember(effectiveTarget, registryVersion) {
        if (effectiveTarget.startsWith("custom:")) {
            File(effectiveTarget.removePrefix("custom:"))
        } else if (effectiveTarget.startsWith("/") || effectiveTarget.startsWith("file:")) {
            File(effectiveTarget.removePrefix("file://").removePrefix("file:"))
        } else null
    }

    if (customFile != null && customFile.exists()) {
        Box(
            modifier = modifier
                .size(size)
                .clip(shape)
                .background(Color(0xFF1E293B))
                .border(1.dp, Color.White.copy(alpha = 0.25f), shape),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = customFile,
                contentDescription = assetName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    } else {
        val profile = AssetIconHelper.getAssetProfile(assetType, assetName, platform)
        val iconSize = (size * 0.58f).coerceAtLeast(14.dp)

        Box(
            modifier = modifier
                .size(size)
                .clip(shape)
                .background(profile.containerColor)
                .border(1.dp, profile.primaryColor.copy(alpha = 0.35f), shape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = profile.icon,
                contentDescription = profile.label,
                tint = profile.primaryColor,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

