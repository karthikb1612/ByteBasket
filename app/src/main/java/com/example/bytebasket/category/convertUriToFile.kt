package com.example.bytebasket.category

import android.content.Context
import android.net.Uri
import java.io.*
import kotlin.io.copyTo
import kotlin.io.use

fun convertUriToFile(context: Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri)
        ?: throw IOException("Unable to open input stream from URI")

    val file = File(context.cacheDir, "temp_image.jpg")
    FileOutputStream(file).use { outputStream ->
        inputStream.copyTo(outputStream)
    }
    return file
}
