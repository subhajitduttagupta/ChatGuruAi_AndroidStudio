package com.chatguru.ai.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object ImageUtils {

    /**
     * Compress image to File (your existing method - kept for compatibility)
     */
    fun compressImage(context: Context, uri: Uri, quality: Int = 85): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            val maxSize = 1200
            val scaledBitmap = scaleBitmap(bitmap, maxSize)

            val outputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

            val file = File(context.cacheDir, "compressed_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { fos ->
                fos.write(outputStream.toByteArray())
            }

            if (scaledBitmap != bitmap) scaledBitmap.recycle()
            bitmap.recycle()

            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * NEW: Async compress to Bitmap directly (for Gemini API)
     * This is more efficient for API calls
     */
    suspend fun compressImageToBitmap(
        context: Context,
        uri: Uri,
        maxSizeKB: Int = 500
    ): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (originalBitmap == null) return@withContext null

                // Scale down to max 1024px
                val maxSize = 1024
                val scaledBitmap = scaleBitmap(originalBitmap, maxSize)

                // Compress to target size
                var quality = 90
                val outputStream = ByteArrayOutputStream()

                do {
                    outputStream.reset()
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                    quality -= 10
                } while (outputStream.size() > maxSizeKB * 1024 && quality > 20)

                val compressedData = outputStream.toByteArray()
                outputStream.close()

                // Recycle originals
                if (originalBitmap != scaledBitmap) {
                    originalBitmap.recycle()
                }

                // Return compressed bitmap
                BitmapFactory.decodeByteArray(compressedData, 0, compressedData.size)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * Scale bitmap to max size while maintaining aspect ratio
     */
    private fun scaleBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val scale = minOf(
            maxSize.toFloat() / width,
            maxSize.toFloat() / height,
            1.0f
        )

        return if (scale < 1.0f) {
            Bitmap.createScaledBitmap(
                bitmap,
                (width * scale).toInt(),
                (height * scale).toInt(),
                true
            )
        } else {
            bitmap
        }
    }

    /**
     * Convert URI to Bitmap (your existing method)
     */
    fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * NEW: Async URI to Bitmap conversion
     */
    suspend fun uriToBitmapAsync(context: Context, uri: Uri): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                bitmap
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * NEW: Get optimized bitmap for display (faster than full resolution)
     */
    suspend fun getThumbnail(
        context: Context,
        uri: Uri,
        targetWidth: Int = 400
    ): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                BitmapFactory.decodeStream(inputStream, null, options)
                inputStream?.close()

                // Calculate sample size
                options.inSampleSize = calculateInSampleSize(options, targetWidth)
                options.inJustDecodeBounds = false

                val inputStream2 = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream2, null, options)
                inputStream2?.close()

                bitmap
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * Calculate sample size for efficient loading
     */
    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int
    ): Int {
        val width = options.outWidth
        var inSampleSize = 1

        if (width > reqWidth) {
            val halfWidth = width / 2
            while (halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    /**
     * NEW: Clean up old cached images
     */
    fun cleanCache(context: Context) {
        try {
            val cacheDir = context.cacheDir
            val files = cacheDir.listFiles { file ->
                file.name.startsWith("compressed_") &&
                        System.currentTimeMillis() - file.lastModified() > 24 * 60 * 60 * 1000 // 24 hours
            }
            files?.forEach { it.delete() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
