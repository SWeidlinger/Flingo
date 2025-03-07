package com.flingoapp.flingo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.ByteArrayOutputStream

/**
 * Decode base64to image
 *
 * @param image
 * @return
 */
fun decodeBase64ToImage(image: String): ImageBitmap? {
    val byteArray = Base64.decode(image, Base64.NO_WRAP)
    return if (byteArray.isNotEmpty()) BitmapFactory.decodeByteArray(
        byteArray,
        0,
        byteArray.size
    ).asImageBitmap()
    else null
}

/**
 * Bitmap to string
 *
 * @param bitmap
 * @return
 */
fun bitmapToString(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    val b = outputStream.toByteArray()
    return Base64.encodeToString(b, Base64.DEFAULT)
}