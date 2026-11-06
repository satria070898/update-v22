package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

// Fixed Yellow Color specified by the user (Warna Kuning FIX yang tidak berubah)
val FixedYellow = Color(0xFFFBBF24)
val FixedYellowDark = Color(0xFFF59E0B)
val FixedYellowLight = Color(0xFFFDE68A)

// Warna abu-abu untuk item navigasi yang sedang tidak dipilih (Unselected/Inactive)
val InactiveNavGrey = Color(0xFF8E9BAE)
val InactiveNavGreyLight = Color(0xFFA8B4C4)

// =========================================================================
// 1. TOOLBAR FITUR CEPAT (Anggaran, Perencana, Acara, Aset, Tabungan)
// =========================================================================

/**
 * Anggaran: Pie Chart dengan potongan utama mengikuti themeColor dan potongan terpisah warna kuning fix.
 */
@Composable
fun AnggaranTwoToneIcon(
    themeColor: Color,
    modifier: Modifier = Modifier.size(26.dp)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val diameter = minOf(w, h) * 0.86f
        val radius = diameter / 2f
        val center = Offset(w / 2f, h / 2f)

        // 1. Potongan utama pie chart (Theme Color) ~260 derajat
        drawArc(
            color = themeColor,
            startAngle = -175f,
            sweepAngle = 260f,
            useCenter = true,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(diameter, diameter)
        )

        // 2. Potongan terpisah menonjol (Fixed Yellow) ~70 derajat di pojok kanan atas
        val shiftX = radius * 0.18f
        val shiftY = -radius * 0.18f
        drawArc(
            color = FixedYellow,
            startAngle = -80f,
            sweepAngle = 70f,
            useCenter = true,
            topLeft = Offset(center.x - radius + shiftX, center.y - radius + shiftY),
            size = Size(diameter, diameter)
        )
    }
}

/**
 * Perencana: Papan Clipboard dengan papan lembar checklist themeColor dan penjepit (clip) kuning fix.
 */
@Composable
fun PerencanaTwoToneIcon(
    themeColor: Color,
    modifier: Modifier = Modifier.size(26.dp)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 1. Papan Lembar Checklist (Theme Color)
        val boardW = w * 0.68f
        val boardH = h * 0.78f
        val boardX = (w - boardW) / 2f
        val boardY = h * 0.16f

        drawRoundRect(
            color = themeColor,
            topLeft = Offset(boardX, boardY),
            size = Size(boardW, boardH),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
        )

        // 2. Tanda centang & baris checklist
        val checkY = listOf(boardY + boardH * 0.32f, boardY + boardH * 0.52f, boardY + boardH * 0.72f)
        val checkColor = Color.White.copy(alpha = 0.9f)
        val strokeW = 1.6.dp.toPx()

        checkY.forEach { y ->
            // Centang kuning fix
            val tickPath = Path().apply {
                moveTo(boardX + boardW * 0.18f, y)
                lineTo(boardX + boardW * 0.28f, y + 2.2.dp.toPx())
                lineTo(boardX + boardW * 0.42f, y - 2.8.dp.toPx())
            }
            drawPath(
                path = tickPath,
                color = FixedYellow,
                style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Garis checklist putih
            drawLine(
                color = checkColor,
                start = Offset(boardX + boardW * 0.50f, y),
                end = Offset(boardX + boardW * 0.82f, y),
                strokeWidth = strokeW,
                cap = StrokeCap.Round
            )
        }

        // 3. Penjepit Atas / Clip (Fixed Yellow)
        val clipW = w * 0.38f
        val clipH = h * 0.15f
        val clipX = (w - clipW) / 2f
        val clipY = h * 0.08f

        drawRoundRect(
            color = FixedYellow,
            topLeft = Offset(clipX, clipY),
            size = Size(clipW, clipH),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
        )
        // Lubang kecil clip
        drawCircle(
            color = Color(0xFF131822),
            radius = 1.8.dp.toPx(),
            center = Offset(w / 2f, clipY + clipH * 0.45f)
        )
    }
}

/**
 * Acara: Kalender dengan lembar themeColor, ring pengait atas dan bintang sudut kuning fix.
 */
@Composable
fun AcaraTwoToneIcon(
    themeColor: Color,
    modifier: Modifier = Modifier.size(26.dp)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val calW = w * 0.74f
        val calH = h * 0.72f
        val calX = (w - calW) / 2f
        val calY = h * 0.20f

        // 1. Badan Kalender (Theme Color)
        drawRoundRect(
            color = themeColor,
            topLeft = Offset(calX, calY),
            size = Size(calW, calH),
            cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx())
        )

        // 2. Ring Pengait Kalender (Fixed Yellow)
        val ringW = 2.8.dp.toPx()
        val ringH = 5.dp.toPx()
        val ring1X = calX + calW * 0.24f
        val ring2X = calX + calW * 0.76f - ringW
        val ringY = calY - ringH * 0.55f

        drawRoundRect(
            color = FixedYellow,
            topLeft = Offset(ring1X, ringY),
            size = Size(ringW, ringH),
            cornerRadius = CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx())
        )
        drawRoundRect(
            color = FixedYellow,
            topLeft = Offset(ring2X, ringY),
            size = Size(ringW, ringH),
            cornerRadius = CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx())
        )

        // 3. Titik Grid Kalender
        val dotRadius = 1.3.dp.toPx()
        val dotColor = Color.White.copy(alpha = 0.85f)
        val rows = 2
        val cols = 3
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val dx = calX + calW * (0.24f + c * 0.22f)
                val dy = calY + calH * (0.38f + r * 0.24f)
                drawCircle(color = dotColor, radius = dotRadius, center = Offset(dx, dy))
            }
        }

        // 4. Bintang Emas di Sudut Kanan Bawah (Fixed Yellow)
        val starCenter = Offset(calX + calW * 0.75f, calY + calH * 0.74f)
        val starR = calW * 0.20f
        val starPath = Path()
        for (i in 0 until 10) {
            val angle = Math.toRadians((i * 36.0) - 90.0)
            val rCurrent = if (i % 2 == 0) starR else starR * 0.45f
            val px = starCenter.x + (rCurrent * cos(angle)).toFloat()
            val py = starCenter.y + (rCurrent * sin(angle)).toFloat()
            if (i == 0) starPath.moveTo(px, py) else starPath.lineTo(px, py)
        }
        starPath.close()

        drawPath(path = starPath, color = FixedYellow)
    }
}

/**
 * Aset: Berlian faceted dengan badan utama themeColor dan kilauan mahkota kuning fix.
 */
@Composable
fun AsetTwoToneIcon(
    themeColor: Color,
    modifier: Modifier = Modifier.size(26.dp)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val topY = h * 0.24f
        val midY = h * 0.45f
        val botY = h * 0.86f

        val leftX = w * 0.12f
        val rightX = w * 0.88f
        val midX = w * 0.50f
        val topL = w * 0.30f
        val topR = w * 0.70f

        // 1. Bagian Bawah Berlian (Pavilion) dalam Theme Color
        val lowerFacet = Path().apply {
            moveTo(leftX, midY)
            lineTo(rightX, midY)
            lineTo(midX, botY)
            close()
        }
        drawPath(path = lowerFacet, color = themeColor)

        // Garis faset pembagi
        val facetLine1 = Path().apply {
            moveTo(topL, midY)
            lineTo(midX, botY)
        }
        val facetLine2 = Path().apply {
            moveTo(topR, midY)
            lineTo(midX, botY)
        }
        drawPath(path = facetLine1, color = Color.White.copy(alpha = 0.35f), style = Stroke(1.dp.toPx()))
        drawPath(path = facetLine2, color = Color.White.copy(alpha = 0.35f), style = Stroke(1.dp.toPx()))

        // 2. Faset Samping Atas (Crown sides)
        val crownLeft = Path().apply {
            moveTo(leftX, midY)
            lineTo(topL, topY)
            lineTo(topL, midY)
            close()
        }
        val crownRight = Path().apply {
            moveTo(rightX, midY)
            lineTo(topR, topY)
            lineTo(topR, midY)
            close()
        }
        drawPath(path = crownLeft, color = themeColor.copy(alpha = 0.85f))
        drawPath(path = crownRight, color = themeColor.copy(alpha = 0.85f))

        // 3. Faset Meja Tengah (Table) dalam Fixed Yellow
        val tablePath = Path().apply {
            moveTo(topL, topY)
            lineTo(topR, topY)
            lineTo(midX, midY)
            close()
        }
        drawPath(path = tablePath, color = FixedYellow)

        // Kilauan bintang 4 sudut kecil di pojok kanan atas
        val sparkleCenter = Offset(w * 0.78f, h * 0.22f)
        val spR = w * 0.09f
        val sparklePath = Path().apply {
            moveTo(sparkleCenter.x, sparkleCenter.y - spR)
            lineTo(sparkleCenter.x + spR * 0.3f, sparkleCenter.y)
            lineTo(sparkleCenter.x + spR, sparkleCenter.y)
            lineTo(sparkleCenter.x + spR * 0.3f, sparkleCenter.y + spR * 0.3f)
            lineTo(sparkleCenter.x, sparkleCenter.y + spR)
            lineTo(sparkleCenter.x - spR * 0.3f, sparkleCenter.y + spR * 0.3f)
            lineTo(sparkleCenter.x - spR, sparkleCenter.y)
            lineTo(sparkleCenter.x - spR * 0.3f, sparkleCenter.y)
            close()
        }
        drawPath(path = sparklePath, color = FixedYellowLight)
    }
}

/**
 * Tabungan: Celengan Babi dengan badan themeColor dan koin emas kuning fix masuk ke slot.
 */
@Composable
fun TabunganTwoToneIcon(
    themeColor: Color,
    modifier: Modifier = Modifier.size(26.dp)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 1. Kaki Celengan (Theme Color)
        drawRoundRect(
            color = themeColor,
            topLeft = Offset(w * 0.30f, h * 0.78f),
            size = Size(w * 0.12f, h * 0.14f),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )
        drawRoundRect(
            color = themeColor,
            topLeft = Offset(w * 0.62f, h * 0.78f),
            size = Size(w * 0.12f, h * 0.14f),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )

        // 2. Badan Celengan Bulat (Theme Color)
        drawOval(
            color = themeColor,
            topLeft = Offset(w * 0.18f, h * 0.34f),
            size = Size(w * 0.68f, h * 0.50f)
        )

        // 3. Moncong (Snout)
        drawRoundRect(
            color = themeColor,
            topLeft = Offset(w * 0.11f, h * 0.48f),
            size = Size(w * 0.14f, h * 0.22f),
            cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx())
        )

        // 4. Telinga (Ear)
        val earPath = Path().apply {
            moveTo(w * 0.32f, h * 0.36f)
            lineTo(w * 0.28f, h * 0.22f)
            lineTo(w * 0.42f, h * 0.34f)
            close()
        }
        drawPath(path = earPath, color = themeColor)

        // 5. Mata (Eye)
        drawCircle(
            color = Color.White,
            radius = 1.4.dp.toPx(),
            center = Offset(w * 0.30f, h * 0.47f)
        )

        // 6. Koin Emas (Fixed Yellow) jatuh ke dalam celengan
        val coin1Center = Offset(w * 0.54f, h * 0.24f)
        val coin1R = w * 0.14f
        drawCircle(color = FixedYellow, radius = coin1R, center = coin1Center)
        drawCircle(color = FixedYellowDark, radius = coin1R * 0.72f, center = coin1Center, style = Stroke(1.dp.toPx()))

        // Koin kecil kedua di atas
        val coin2Center = Offset(w * 0.72f, h * 0.15f)
        val coin2R = w * 0.085f
        drawCircle(color = FixedYellow, radius = coin2R, center = coin2Center)
    }
}

// =========================================================================
// 2. TOOLBAR BAWAH / NAV TABS (Transaksi, Dasbor, Ringkasan, Akun, Pengaturan)
// =========================================================================

/**
 * Transaksi: Dokumen lembaran dengan badan themeColor dan pita/bookmark kuning fix.
 * Ketika tidak dipilih, berubah warna menjadi abu-abu.
 */
@Composable
fun TransaksiTwoToneNavIcon(
    themeColor: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier.size(22.dp)
) {
    val effTheme by animateColorAsState(
        targetValue = if (isSelected) themeColor else InactiveNavGrey,
        label = "transaksiTheme"
    )
    val effYellow by animateColorAsState(
        targetValue = if (isSelected) FixedYellow else InactiveNavGreyLight,
        label = "transaksiYellow"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 1. Dokumen / Catatan Transaksi
        val docW = w * 0.66f
        val docH = h * 0.78f
        val docX = (w - docW) / 2f
        val docY = h * 0.11f

        drawRoundRect(
            color = effTheme,
            topLeft = Offset(docX, docY),
            size = Size(docW, docH),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
        )

        // 2. Garis Teks Transaksi (Putih saat aktif, semi-transparan abu-abu saat tidak aktif)
        val lineX1 = docX + docW * 0.20f
        val lineX2 = docX + docW * 0.80f
        val lineY1 = docY + docH * 0.42f
        val lineY2 = docY + docH * 0.60f
        val lineY3 = docY + docH * 0.78f
        val strokeW = 1.6.dp.toPx()
        val lineColor = if (isSelected) Color.White.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.4f)

        drawLine(color = lineColor, start = Offset(lineX1, lineY1), end = Offset(lineX2, lineY1), strokeWidth = strokeW, cap = StrokeCap.Round)
        drawLine(color = lineColor, start = Offset(lineX1, lineY2), end = Offset(lineX2, lineY2), strokeWidth = strokeW, cap = StrokeCap.Round)
        drawLine(color = lineColor, start = Offset(lineX1, lineY3), end = Offset(docX + docW * 0.58f, lineY3), strokeWidth = strokeW, cap = StrokeCap.Round)

        // 3. Pita / Bookmark di pojok kanan atas dokumen (Fixed Yellow saat aktif, abu-abu saat tidak aktif)
        val ribW = docW * 0.32f
        val ribH = docH * 0.34f
        val ribX = docX + docW * 0.58f
        val ribY = docY

        val ribbonPath = Path().apply {
            moveTo(ribX, ribY)
            lineTo(ribX + ribW, ribY)
            lineTo(ribX + ribW, ribY + ribH)
            lineTo(ribX + ribW / 2f, ribY + ribH * 0.72f)
            lineTo(ribX, ribY + ribH)
            close()
        }
        drawPath(path = ribbonPath, color = effYellow)
    }
}

/**
 * Dasbor: 4 ubin kotak (2x2 grid) dengan 3 ubin themeColor dan 1 ubin pojok kanan atas kuning fix.
 * Ketika tidak dipilih, berubah warna menjadi abu-abu.
 */
@Composable
fun DasborTwoToneNavIcon(
    themeColor: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier.size(22.dp)
) {
    val effTheme by animateColorAsState(
        targetValue = if (isSelected) themeColor else InactiveNavGrey,
        label = "dasborTheme"
    )
    val effYellow by animateColorAsState(
        targetValue = if (isSelected) FixedYellow else InactiveNavGreyLight,
        label = "dasborYellow"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val tileSize = w * 0.36f
        val gap = w * 0.12f
        val startX = (w - (tileSize * 2 + gap)) / 2f
        val startY = (h - (tileSize * 2 + gap)) / 2f
        val radius = CornerRadius(3.5.dp.toPx(), 3.5.dp.toPx())

        // 1. Top-Left Tile
        drawRoundRect(
            color = effTheme,
            topLeft = Offset(startX, startY),
            size = Size(tileSize, tileSize),
            cornerRadius = radius
        )

        // 2. Top-Right Tile
        drawRoundRect(
            color = effYellow,
            topLeft = Offset(startX + tileSize + gap, startY),
            size = Size(tileSize, tileSize),
            cornerRadius = radius
        )

        // 3. Bottom-Left Tile
        drawRoundRect(
            color = effTheme,
            topLeft = Offset(startX, startY + tileSize + gap),
            size = Size(tileSize, tileSize),
            cornerRadius = radius
        )

        // 4. Bottom-Right Tile
        drawRoundRect(
            color = effTheme,
            topLeft = Offset(startX + tileSize + gap, startY + tileSize + gap),
            size = Size(tileSize, tileSize),
            cornerRadius = radius
        )
    }
}

/**
 * Ringkasan: Garis grafik naik (trend) themeColor dengan titik puncak kuning fix.
 * Ketika tidak dipilih, berubah warna menjadi abu-abu.
 */
@Composable
fun RingkasanTwoToneNavIcon(
    themeColor: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier.size(22.dp)
) {
    val effTheme by animateColorAsState(
        targetValue = if (isSelected) themeColor else InactiveNavGrey,
        label = "ringkasanTheme"
    )
    val effYellow by animateColorAsState(
        targetValue = if (isSelected) FixedYellow else InactiveNavGreyLight,
        label = "ringkasanYellow"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Titik-titik garis tren
        val p1 = Offset(w * 0.14f, h * 0.76f)
        val p2 = Offset(w * 0.38f, h * 0.52f)
        val p3 = Offset(w * 0.55f, h * 0.62f)
        val p4 = Offset(w * 0.84f, h * 0.24f)

        // 1. Line stroke
        val linePath = Path().apply {
            moveTo(p1.x, p1.y)
            lineTo(p2.x, p2.y)
            lineTo(p3.x, p3.y)
            lineTo(p4.x, p4.y)
        }

        drawPath(
            path = linePath,
            color = effTheme,
            style = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Titik-titik perantara
        drawCircle(color = effTheme, radius = 2.2.dp.toPx(), center = p1)
        drawCircle(color = effTheme, radius = 2.2.dp.toPx(), center = p2)
        drawCircle(color = effTheme, radius = 2.2.dp.toPx(), center = p3)

        // 2. Titik Puncak Tertinggi (Fixed Yellow saat aktif, abu-abu saat tidak aktif)
        drawCircle(color = effYellow, radius = 4.5.dp.toPx(), center = p4)
        val centerDotColor = if (isSelected) Color.White else Color(0xFFD1D5DB)
        drawCircle(color = centerDotColor, radius = 1.8.dp.toPx(), center = p4)
    }
}

/**
 * Nilai Bersih: 3 batang grafik vertikal naik (themeColor) dengan panah melengkung kuning fix
 * mengarah ke atas-kanan persis seperti di gambar referensi.
 */
@Composable
fun NilaiBersihTwoToneNavIcon(
    themeColor: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier.size(22.dp)
) {
    val effTheme by animateColorAsState(
        targetValue = if (isSelected) themeColor else InactiveNavGrey,
        label = "nilaiBersihTheme"
    )
    val effYellow by animateColorAsState(
        targetValue = if (isSelected) FixedYellow else InactiveNavGreyLight,
        label = "nilaiBersihYellow"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 1. 3 Batang Vertikal Naik (Theme Color)
        val barW = w * 0.20f
        val gap = w * 0.08f
        val startX = (w - (barW * 3 + gap * 2)) / 2f
        val botY = h * 0.94f
        val radius = CornerRadius(2.8.dp.toPx(), 2.8.dp.toPx())

        // Batang 1 (Kiri - Terpendek)
        val h1 = h * 0.32f
        drawRoundRect(
            color = effTheme.copy(alpha = 0.88f),
            topLeft = Offset(startX, botY - h1),
            size = Size(barW, h1),
            cornerRadius = radius
        )

        // Batang 2 (Tengah - Sedang)
        val h2 = h * 0.52f
        drawRoundRect(
            color = effTheme,
            topLeft = Offset(startX + barW + gap, botY - h2),
            size = Size(barW, h2),
            cornerRadius = radius
        )

        // Batang 3 (Kanan - Tertinggi)
        val h3 = h * 0.72f
        drawRoundRect(
            color = effTheme,
            topLeft = Offset(startX + (barW + gap) * 2, botY - h3),
            size = Size(barW, h3),
            cornerRadius = radius
        )

        // 2. Panah Melengkung Kuning Naik ke Atas Kanan (Fixed Yellow)
        val arrowPath = Path().apply {
            moveTo(startX - w * 0.02f, botY - h1 * 0.45f)
            quadraticTo(
                startX + barW + gap * 0.5f,
                botY - h2 * 0.96f,
                w * 0.80f,
                h * 0.24f
            )
        }
        drawPath(
            path = arrowPath,
            color = effYellow,
            style = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round)
        )

        // Kepala Panah (Arrowhead di ujung kanan atas, mengarah 45 derajat)
        val tipX = w * 0.88f
        val tipY = h * 0.16f
        val headPath = Path().apply {
            moveTo(tipX, tipY)
            lineTo(tipX - w * 0.22f, tipY + h * 0.04f)
            lineTo(tipX - w * 0.12f, tipY + h * 0.14f)
            lineTo(tipX - w * 0.04f, tipY + h * 0.22f)
            close()
        }
        drawPath(path = headPath, color = effYellow)
    }
}

/**
 * Akun: Dompet tema dengan koin emas yang menonjol keluar di bagian atas
 * persis seperti di gambar referensi.
 */
@Composable
fun AkunTwoToneNavIcon(
    themeColor: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier.size(22.dp)
) {
    val effTheme by animateColorAsState(
        targetValue = if (isSelected) themeColor else InactiveNavGrey,
        label = "akunTheme"
    )
    val effYellow by animateColorAsState(
        targetValue = if (isSelected) FixedYellow else InactiveNavGreyLight,
        label = "akunYellow"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 1. Koin Emas Menonjol di Atas Dompet (Fixed Yellow)
        val coinCenter = Offset(w * 0.50f, h * 0.32f)
        val coinR = w * 0.18f
        drawCircle(color = effYellow, radius = coinR, center = coinCenter)
        drawCircle(
            color = if (isSelected) FixedYellowDark else InactiveNavGrey,
            radius = coinR * 0.75f,
            center = coinCenter,
            style = Stroke(1.dp.toPx())
        )

        // 2. Badan Dompet (Theme Color)
        val wallW = w * 0.76f
        val wallH = h * 0.56f
        val wallX = (w - wallW) / 2f
        val wallY = h * 0.36f
        val wallRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx())

        drawRoundRect(
            color = effTheme,
            topLeft = Offset(wallX, wallY),
            size = Size(wallW, wallH),
            cornerRadius = wallRadius
        )

        // Lekukan kantong dompet di tengah atas
        val foldPath = Path().apply {
            moveTo(wallX, wallY + wallH * 0.26f)
            lineTo(wallX + wallW * 0.36f, wallY + wallH * 0.26f)
            lineTo(wallX + wallW * 0.50f, wallY + wallH * 0.40f)
            lineTo(wallX + wallW * 0.64f, wallY + wallH * 0.26f)
            lineTo(wallX + wallW, wallY + wallH * 0.26f)
        }
        val foldColor = if (isSelected) Color.White.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.2f)
        drawPath(path = foldPath, color = foldColor, style = Stroke(1.2.dp.toPx(), cap = StrokeCap.Round))
    }
}

/**
 * Pengaturan: Roda gigi gerigi tema dengan pusat/poros tengah kuning fix.
 * Ketika tidak dipilih, berubah warna menjadi abu-abu.
 */
@Composable
fun PengaturanTwoToneNavIcon(
    themeColor: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier.size(22.dp)
) {
    val effTheme by animateColorAsState(
        targetValue = if (isSelected) themeColor else InactiveNavGrey,
        label = "pengaturanTheme"
    )
    val effYellow by animateColorAsState(
        targetValue = if (isSelected) FixedYellow else InactiveNavGreyLight,
        label = "pengaturanYellow"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val center = Offset(w / 2f, h / 2f)

        // 1. Roda Gigi Luar
        val outerR = minOf(w, h) * 0.34f
        val toothLength = minOf(w, h) * 0.11f
        val toothWidth = 2.8.dp.toPx()

        // 6 Gerigi di sekeliling
        for (i in 0 until 6) {
            val angle = Math.toRadians(i * 60.0)
            val start = Offset(
                center.x + (outerR * cos(angle)).toFloat(),
                center.y + (outerR * sin(angle)).toFloat()
            )
            val end = Offset(
                center.x + ((outerR + toothLength) * cos(angle)).toFloat(),
                center.y + ((outerR + toothLength) * sin(angle)).toFloat()
            )
            drawLine(
                color = effTheme,
                start = start,
                end = end,
                strokeWidth = toothWidth,
                cap = StrokeCap.Round
            )
        }

        // Lingkaran luar gerigi
        drawCircle(
            color = effTheme,
            radius = outerR,
            center = center,
            style = Stroke(width = 3.2.dp.toPx())
        )

        // 2. Poros Tengah (Fixed Yellow saat aktif, abu-abu saat tidak aktif)
        val innerR = outerR * 0.54f
        drawCircle(
            color = effYellow,
            radius = innerR,
            center = center
        )
        // Lubang kecil tengah
        drawCircle(
            color = Color(0xFF131822),
            radius = innerR * 0.42f,
            center = center
        )
    }
}

/**
 * Aset (Nav/Tab): Berlian faceted dengan badan utama themeColor dan kilauan mahkota kuning fix.
 * Ketika tidak dipilih, berubah warna menjadi abu-abu.
 */
@Composable
fun AsetTwoToneNavIcon(
    themeColor: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier.size(22.dp)
) {
    val effTheme by animateColorAsState(
        targetValue = if (isSelected) themeColor else InactiveNavGrey,
        label = "asetTheme"
    )
    val effYellow by animateColorAsState(
        targetValue = if (isSelected) FixedYellow else InactiveNavGreyLight,
        label = "asetYellow"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val topY = h * 0.26f
        val midY = h * 0.50f
        val botY = h * 0.90f

        val leftX = w * 0.14f
        val rightX = w * 0.86f
        val midX = w * 0.50f
        val topL = w * 0.32f
        val topR = w * 0.68f

        // 1. Lower Pavilion (Faceted Bottom Triangle)
        val lowerFacet = Path().apply {
            moveTo(leftX, midY)
            lineTo(rightX, midY)
            lineTo(midX, botY)
            close()
        }
        drawPath(path = lowerFacet, color = effTheme)

        // Center lower facet dividing lines
        val facetLine1 = Path().apply {
            moveTo(topL, midY)
            lineTo(midX, botY)
        }
        val facetLine2 = Path().apply {
            moveTo(topR, midY)
            lineTo(midX, botY)
        }
        val strokeColor = if (isSelected) Color.White.copy(alpha = 0.40f) else Color.White.copy(alpha = 0.22f)
        drawPath(path = facetLine1, color = strokeColor, style = Stroke(1.1.dp.toPx()))
        drawPath(path = facetLine2, color = strokeColor, style = Stroke(1.1.dp.toPx()))

        // 2. Upper Crown Facets
        val crownLeft = Path().apply {
            moveTo(leftX, midY)
            lineTo(topL, topY)
            lineTo(topL, midY)
            close()
        }
        val crownRight = Path().apply {
            moveTo(rightX, midY)
            lineTo(topR, topY)
            lineTo(topR, midY)
            close()
        }
        drawPath(path = crownLeft, color = effTheme.copy(alpha = 0.88f))
        drawPath(path = crownRight, color = effTheme.copy(alpha = 0.88f))

        // Center Table facet
        val tablePath = Path().apply {
            moveTo(topL, topY)
            lineTo(topR, topY)
            lineTo(midX, midY)
            close()
        }
        drawPath(path = tablePath, color = effTheme.copy(alpha = 0.95f))

        // Outline table top
        drawLine(
            color = strokeColor,
            start = Offset(topL, topY),
            end = Offset(topR, topY),
            strokeWidth = 1.1.dp.toPx()
        )

        // 3. Sparkle 4-pointed Star at Top-Right (Fixed Yellow saat aktif, abu-abu saat tidak aktif)
        val starCx = w * 0.82f
        val starCy = h * 0.20f
        val starR = w * 0.16f
        val starInnerR = starR * 0.32f
        val starPath = Path().apply {
            moveTo(starCx, starCy - starR)
            lineTo(starCx + starInnerR, starCy - starInnerR)
            lineTo(starCx + starR, starCy)
            lineTo(starCx + starInnerR, starCy + starInnerR)
            lineTo(starCx, starCy + starR)
            lineTo(starCx - starInnerR, starCy + starInnerR)
            lineTo(starCx - starR, starCy)
            lineTo(starCx - starInnerR, starCy - starInnerR)
            close()
        }
        drawPath(path = starPath, color = effYellow)
    }
}

/**
 * Utang (Nav/Tab): Handshake / Perjanjian dengan tema themeColor dan aksen emas kuning fix.
 * Ketika tidak dipilih, berubah warna menjadi abu-abu.
 */
@Composable
fun UtangTwoToneNavIcon(
    themeColor: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier.size(22.dp)
) {
    val effTheme by animateColorAsState(
        targetValue = if (isSelected) themeColor else InactiveNavGrey,
        label = "utangTheme"
    )
    val effYellow by animateColorAsState(
        targetValue = if (isSelected) FixedYellow else InactiveNavGreyLight,
        label = "utangYellow"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // 1. Tangan Kiri (Lengan bawah kiri ke tengah)
        val handLeft = Path().apply {
            moveTo(w * 0.10f, h * 0.62f)
            lineTo(w * 0.34f, h * 0.40f)
            lineTo(w * 0.52f, h * 0.52f)
            lineTo(w * 0.38f, h * 0.76f)
            lineTo(w * 0.16f, h * 0.78f)
            close()
        }
        drawPath(path = handLeft, color = effTheme)

        // 2. Tangan Kanan (Lengan atas kanan ke tengah saling mengunci)
        val handRight = Path().apply {
            moveTo(w * 0.90f, h * 0.42f)
            lineTo(w * 0.66f, h * 0.64f)
            lineTo(w * 0.48f, h * 0.52f)
            lineTo(w * 0.62f, h * 0.28f)
            lineTo(w * 0.84f, h * 0.26f)
            close()
        }
        drawPath(path = handRight, color = effTheme.copy(alpha = 0.90f))

        // Garis batas jabat tangan
        drawLine(
            color = if (isSelected) Color.White.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.25f),
            start = Offset(w * 0.44f, h * 0.46f),
            end = Offset(w * 0.56f, h * 0.58f),
            strokeWidth = 1.3.dp.toPx(),
            cap = StrokeCap.Round
        )

        // 3. Koin Emas / Segel Perjanjian di atas jabat tangan (Fixed Yellow saat aktif, abu-abu saat tidak aktif)
        val coinCenter = Offset(w * 0.50f, h * 0.24f)
        val coinR = w * 0.15f
        drawCircle(color = effYellow, radius = coinR, center = coinCenter)
        drawCircle(
            color = if (isSelected) FixedYellowDark else InactiveNavGrey,
            radius = coinR * 0.70f,
            center = coinCenter,
            style = Stroke(1.dp.toPx())
        )
    }
}
