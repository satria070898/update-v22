package com.example.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * VisualTransformation that formats digit strings with a thousands separator
 * (default dot '.' for Indonesian Rupiah, e.g. 1000000 -> 1.000.000).
 *
 * Keeps cursor mapping strictly monotonic and in-bounds to prevent any
 * IndexOutOfBoundsException during user typing, cursor movement, or backspacing.
 */
class ThousandsSeparatorVisualTransformation(
    private val separator: Char = '.'
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        val len = originalText.length
        val out = StringBuilder()
        val originalToTransformed = IntArray(len + 1)
        val transformedToOriginalList = ArrayList<Int>(len * 2)

        for (i in 0 until len) {
            // Insert separator every 3 digits from the right, but not before the first digit
            if (i > 0 && (len - i) % 3 == 0) {
                out.append(separator)
                // Separator maps back to original character position i
                transformedToOriginalList.add(i)
            }
            originalToTransformed[i] = out.length
            transformedToOriginalList.add(i)
            out.append(originalText[i])
        }

        originalToTransformed[len] = out.length
        transformedToOriginalList.add(len)

        val transformedString = out.toString()
        val transformedToOriginal = transformedToOriginalList.toIntArray()
        val transformedLen = transformedString.length

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val clamped = offset.coerceIn(0, len)
                return originalToTransformed[clamped].coerceIn(0, transformedLen)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val clamped = offset.coerceIn(0, transformedToOriginal.size - 1)
                return transformedToOriginal[clamped].coerceIn(0, len)
            }
        }

        return TransformedText(AnnotatedString(transformedString), offsetMapping)
    }
}
