package com.example.util

import android.content.Context
import android.net.Uri
import android.util.Base64
import java.io.File
import java.io.FileOutputStream

object ImageUploadHelper {

    fun getCustomIconsDir(context: Context): File {
        val dir = File(context.filesDir, "custom_icons")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    /**
     * Copies the content URI image data to app internal storage under custom_icons/
     * Returns the absolute path of the saved file or null on failure.
     */
    fun saveImageToInternalStorage(context: Context, uri: Uri, prefix: String = "icon"): String? {
        return try {
            val dir = getCustomIconsDir(context)
            val file = File(dir, "${prefix}_${System.currentTimeMillis()}.png")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Reads a file and converts its binary content to a Base64 string.
     */
    fun fileToBase64(file: File): String? {
        return try {
            if (file.exists() && file.isFile && file.length() > 0) {
                val bytes = file.readBytes()
                Base64.encodeToString(bytes, Base64.NO_WRAP)
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Restores an image file from a Base64 string into custom_icons/ directory.
     * Returns the File on success or null.
     */
    fun base64ToFile(context: Context, fileName: String, base64Str: String): File? {
        return try {
            val cleanName = File(fileName).name
            if (cleanName.isBlank() || base64Str.isBlank()) return null
            val dir = getCustomIconsDir(context)
            val file = File(dir, cleanName)
            val bytes = Base64.decode(base64Str, Base64.DEFAULT)
            FileOutputStream(file).use { fos ->
                fos.write(bytes)
                fos.flush()
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Extracts the local File from an icon string (e.g. "custom:/path/to/icon.png", "file:/path", or "/path").
     * Returns null if not a custom file path or empty.
     */
    fun extractCustomIconFile(rawIconOrUri: String): File? {
        val trimmed = rawIconOrUri.trim()
        if (trimmed.isBlank()) return null
        return when {
            trimmed.startsWith("custom:") -> File(trimmed.removePrefix("custom:"))
            trimmed.startsWith("file://") -> File(trimmed.removePrefix("file://"))
            trimmed.startsWith("file:") -> File(trimmed.removePrefix("file:"))
            trimmed.startsWith("/") -> File(trimmed)
            else -> null
        }
    }

    /**
     * Extracts the clean filename from a raw custom icon string.
     */
    fun extractFileName(rawIconOrUri: String): String {
        val file = extractCustomIconFile(rawIconOrUri)
        return file?.name ?: File(rawIconOrUri).name
    }

    /**
     * Collects all custom logo files present in custom_icons/ and encodes them to a Map of fileName -> Base64 string.
     * Also checks any additional referenced custom file paths and includes them.
     */
    fun collectAllCustomLogos(
        context: Context,
        additionalReferencedPaths: List<String> = emptyList()
    ): Map<String, String> {
        val result = mutableMapOf<String, String>()
        try {
            val dir = getCustomIconsDir(context)
            if (dir.exists()) {
                dir.listFiles()?.forEach { file ->
                    if (file.isFile && file.length() > 0) {
                        fileToBase64(file)?.let { b64 ->
                            result[file.name] = b64
                        }
                    }
                }
            }

            // Check additional paths (e.g. from wallet iconName or asset iconUri)
            additionalReferencedPaths.forEach { rawPath ->
                val f = extractCustomIconFile(rawPath)
                if (f != null && f.exists() && f.isFile && !result.containsKey(f.name)) {
                    fileToBase64(f)?.let { b64 ->
                        result[f.name] = b64
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    /**
     * Resolves an icon string when restoring.
     * If it's a custom icon:
     * 1. If base64Map contains its data, ensures the file is restored into custom_icons/ and returns "custom:$newPath".
     * 2. If it already exists locally in custom_icons/, returns "custom:$localPath".
     * 3. If file exists at original path, returns "custom:$originalPath".
     * Otherwise returns raw icon (e.g. built-in name like "Bank", "Cash", etc.).
     */
    fun resolveOrRestoreIconPath(
        context: Context,
        rawIconOrUri: String,
        base64Map: Map<String, String>? = null
    ): String {
        val trimmed = rawIconOrUri.trim()
        if (trimmed.isBlank()) return ""
        val isCustom = trimmed.startsWith("custom:") ||
                trimmed.startsWith("/") ||
                trimmed.startsWith("file:") ||
                trimmed.contains("custom_icons")

        if (!isCustom) {
            return trimmed
        }

        val fileName = extractFileName(trimmed)
        val dir = getCustomIconsDir(context)
        val targetFile = File(dir, fileName)

        // 1. If we have base64 data for it, restore it
        val b64 = base64Map?.get(fileName)
        if (!b64.isNullOrBlank()) {
            base64ToFile(context, fileName, b64)
            if (targetFile.exists()) {
                return "custom:${targetFile.absolutePath}"
            }
        }

        // 2. If it already exists in custom_icons
        if (targetFile.exists()) {
            return "custom:${targetFile.absolutePath}"
        }

        // 3. If it exists at the original absolute path
        val originalFile = extractCustomIconFile(trimmed)
        if (originalFile != null && originalFile.exists()) {
            return "custom:${originalFile.absolutePath}"
        }

        // Fallback: return pointing to custom_icons path with prefix
        return "custom:${targetFile.absolutePath}"
    }
}
