package com.example.texnostrelka_2025_otbor.presentation.utils

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

fun String.base64ToBitmap(): Bitmap? {
    return try {
        val imageBytes = Base64.decode(this, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    } catch (e: Exception) {
        null
    }
}

fun Bitmap.toBase64(
    format: CompressFormat = CompressFormat.JPEG,
    quality: Int = 80
): String? {
    return try {
        val byteArrayOutputStream = ByteArrayOutputStream()
        this.compress(format, quality, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        Base64.encodeToString(byteArray, Base64.DEFAULT)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}