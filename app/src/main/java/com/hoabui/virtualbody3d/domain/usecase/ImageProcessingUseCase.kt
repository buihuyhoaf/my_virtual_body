package com.hoabui.virtualbody3d.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject
import kotlin.math.max

/**
 * Unified image processing pipeline: decode safely with inSampleSize,
 * downscale to max long edge 1080px, JPEG 75%, strip EXIF (orientation applied to pixels).
 * Reusable for Create Baseline, Camera capture, and Food AI.
 */
class ImageProcessingUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val MAX_LONG_EDGE_PX = 1080
        private const val JPEG_QUALITY = 75
    }

    /**
     * Processes the image at [uri] and saves an optimized copy to cache.
     * Must be called from a background dispatcher (e.g. Dispatchers.IO).
     * @return Optimized file in cacheDir, or null if uri is null
     * @throws Exception on decode/io failure
     */
    suspend fun process(uri: Uri?): File? {
        if (uri == null) return null
        val contentResolver = context.contentResolver

        val (w, h) = contentResolver.openInputStream(uri)?.use { decodeBounds(it) }
            ?: throw IllegalArgumentException("Cannot open stream for uri: $uri")
        if (w <= 0 || h <= 0) throw IllegalArgumentException("Invalid image dimensions")

        val inSampleSize = computeInSampleSize(w, h, MAX_LONG_EDGE_PX)
        val bitmap = contentResolver.openInputStream(uri)?.use { decodeWithSampleSize(it, inSampleSize) }
            ?: throw IllegalArgumentException("Failed to decode bitmap")

        val orientation = contentResolver.openInputStream(uri)?.use { readOrientation(it) } ?: 0
        var result = applyOrientation(bitmap, orientation)
        if (result != bitmap) bitmap.recycle()

        result = scaleDownIfNeeded(result, MAX_LONG_EDGE_PX)

        val outFile = File(context.cacheDir, "baseline_${System.currentTimeMillis()}.jpg")
        FileOutputStream(outFile).use { out ->
            result.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
        }
        result.recycle()
        return outFile
    }

    /**
     * Processes the image from [file] and saves an optimized copy to cache.
     * Must be called from a background dispatcher (e.g. Dispatchers.IO).
     * Use for camera capture; does not decode full bitmap before inSampleSize.
     * @return Optimized file in cacheDir, or null if file is null or invalid
     * @throws Exception on decode/io failure
     */
    suspend fun process(file: File?): File? {
        if (file == null || !file.exists()) return null
        val (w, h) = FileInputStream(file).use { decodeBounds(it) }
        if (w <= 0 || h <= 0) throw IllegalArgumentException("Invalid image dimensions")
        val inSampleSize = computeInSampleSize(w, h, MAX_LONG_EDGE_PX)
        val bitmap = FileInputStream(file).use { decodeWithSampleSize(it, inSampleSize) }
        val orientation = try {
            ExifInterface(file.absolutePath).let { exif ->
                when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
                    else -> 0
                }
            }
        } catch (_: Exception) { 0 }
        var result = applyOrientation(bitmap, orientation)
        if (result != bitmap) bitmap.recycle()
        result = scaleDownIfNeeded(result, MAX_LONG_EDGE_PX)
        val outFile = File(context.cacheDir, "baseline_${System.currentTimeMillis()}.jpg")
        FileOutputStream(outFile).use { out ->
            result.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
        }
        result.recycle()
        return outFile
    }

    private fun decodeBounds(stream: InputStream): Pair<Int, Int> {
        val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeStream(stream, null, opts)
        return Pair(opts.outWidth, opts.outHeight)
    }

    private fun computeInSampleSize(width: Int, height: Int, maxLongEdge: Int): Int {
        var inSampleSize = 1
        val longEdge = maxOf(width, height)
        if (longEdge > maxLongEdge) {
            val halfLong = longEdge / 2
            while ((halfLong / inSampleSize) >= maxLongEdge) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun decodeWithSampleSize(stream: InputStream, inSampleSize: Int): Bitmap {
        val opts = BitmapFactory.Options().apply { this.inSampleSize = inSampleSize }
        return BitmapFactory.decodeStream(stream, null, opts)
            ?: throw IllegalArgumentException("BitmapFactory.decodeStream returned null")
    }

    private fun readOrientation(stream: InputStream): Int {
        val exif = ExifInterface(stream)
        return when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    }

    private fun applyOrientation(bitmap: Bitmap, degrees: Int): Bitmap {
        if (degrees == 0) return bitmap
        val matrix = Matrix().apply { postRotate(degrees.toFloat()) }
        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }

    private fun scaleDownIfNeeded(bitmap: Bitmap, maxLongEdge: Int): Bitmap {
        val longEdge = maxOf(bitmap.width, bitmap.height)
        if (longEdge <= maxLongEdge) return bitmap
        val scale = maxLongEdge.toFloat() / longEdge
        val newW = (bitmap.width * scale).toInt().coerceAtLeast(1)
        val newH = (bitmap.height * scale).toInt().coerceAtLeast(1)
        return Bitmap.createScaledBitmap(bitmap, newW, newH, true).also {
            if (it != bitmap) bitmap.recycle()
        }
    }
}
