package com.fyp.vault.utilities

import FileSystem.Node
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.fyp.vault.data.ThumbnailProvider
import com.fyp.vault.data.fileExtensionToMIME
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.Locale
import kotlin.math.max

fun calculateSizeInStringFormat(size: Long): String {
    val levels: Array<String> = arrayOf("B", "kB", "MB", "GB", "TB", "PB")
    var level: Int = 0
    var runningSize: Double = size.toDouble()
    while (runningSize.toLong() / 1024L > 0){
        runningSize /= 1024
        level++
        if (level == 5){
            // If the size reaches petaBytes
            break
        }
    }
    return String.format(Locale.getDefault() ,"%.1f %s", runningSize, levels[level])
}

fun getInputStreamFromBitmap(bitmap: Bitmap): InputStream {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    return ByteArrayInputStream(outputStream.toByteArray())
}

fun createThumbnailAsStream(inputStream: InputStream, maxDimension: Int): ThumbnailProvider {
    // Decode the image from the InputStream
    val originalBitmap = BitmapFactory.decodeStream(inputStream)
        ?: throw IOException("Could not decode image from input stream.")

    // Get original bitmap dimensions
    val originalWidth = originalBitmap.width
    val originalHeight = originalBitmap.height

    // Calculate aspect ratio
    val aspectRatio = originalWidth.toFloat() / originalHeight

    // Calculate new dimensions while preserving the aspect ratio
    val newWidth: Int
    val newHeight: Int

    if (originalWidth > originalHeight) {
        // Image is wider than it is tall
        newWidth = minOf(maxDimension, originalWidth)
        newHeight = (newWidth / aspectRatio).toInt()
    } else {
        // Image is taller than it is wide
        newHeight = minOf(maxDimension, originalHeight)
        newWidth = (newHeight * aspectRatio).toInt()
    }

    // Create a scaled bitmap
    val thumbnail = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)

    // Write the thumbnail to a ByteArrayOutputStream
    val baos = ByteArrayOutputStream()
    thumbnail.compress(Bitmap.CompressFormat.PNG, 100, baos) // Use JPEG or PNG depending on your needs
    baos.flush()
    val byteArray = baos.toByteArray()
    // Convert ByteArrayOutputStream to ByteArrayInputStream
    val bais = ByteArrayInputStream(baos.toByteArray())
    baos.close() // Close the ByteArrayOutputStream
    return ThumbnailProvider(bais, byteArray.size.toLong())
}

fun createThumbnailAsStream(bitmap: Bitmap, maxDimension: Int): ThumbnailProvider{
    val originalWidth = bitmap.width
    val originalHeight = bitmap.height

    // Calculate aspect ratio
    val aspectRatio = originalWidth.toFloat() / originalHeight

    // Calculate new dimensions while preserving the aspect ratio
    val newWidth: Int
    val newHeight: Int

    if (originalWidth > originalHeight) {
        // Image is wider than it is tall
        newWidth = minOf(maxDimension, originalWidth)
        newHeight = (newWidth / aspectRatio).toInt()
    } else {
        // Image is taller than it is wide
        newHeight = minOf(maxDimension, originalHeight)
        newWidth = (newHeight * aspectRatio).toInt()
    }

    // Create a scaled bitmap
    val thumbnail = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    // Write the thumbnail to a ByteArrayOutputStream
    val baos = ByteArrayOutputStream()
    thumbnail.compress(Bitmap.CompressFormat.PNG, 100, baos) // Use JPEG or PNG depending on your needs
    baos.flush()
    // Convert ByteArrayOutputStream to ByteArrayInputStream
    val byteArray = baos.toByteArray()
    val bais = ByteArrayInputStream(byteArray)
    baos.close()
    return ThumbnailProvider(bais, byteArray.size.toLong())// Close the ByteArrayOutputStream
}

fun getBitmapFromStream(stream: InputStream): Bitmap{
    return BitmapFactory.decodeStream(stream);
}

fun MIMEFromExtension(extension: String): String?{
        return fileExtensionToMIME[extension]
}

fun getNodeMimeType(node: Node): String?{
    var extension = node.name.split(".").getOrNull(1) ?: return null
    extension = ".$extension"
    val MIME = MIMEFromExtension(extension) ?: return null
    return MIME.split("/")[0]
}

fun isNodeVideo(node: Node): Boolean{
    val type = getNodeMimeType(node) ?: return false
    return type == "video"
}

fun isNodeMedia(node: Node): Boolean{
    val type = getNodeMimeType(node) ?: return false
    return type == "video" || type == "image"
}