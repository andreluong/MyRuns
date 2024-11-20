package com.andre_luong.myruns4.util

import android.Manifest.permission.CAMERA
import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat.checkSelfPermission
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Utils {
    fun checkPermissions(activity: Activity?) {
        if (checkSelfPermission(activity!!, CAMERA) != PERMISSION_GRANTED) {
            requestPermissions(activity, arrayOf(CAMERA), 0)
        }
    }

    fun getBitmap(context: Context, imgUri: Uri): Bitmap {
        val input = context.contentResolver.openInputStream(imgUri)
        val bitmap = BitmapFactory.decodeStream(input)
        val matrix = Matrix()
        matrix.setRotate(90f) // Adjust for phone camera settings
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

    fun copyUriToFile(context: Context, uri: Uri, file: File): File {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)

            inputStream?.let {
                copy(inputStream, outputStream)
            }

            outputStream.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file
    }

    // Overwrites data in output using input
    private fun copy(input: InputStream, output: OutputStream) {
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var length: Int
        while (input.read(buffer).also { length = it } > 0) {
            output.write(buffer, 0, length)
        }
    }

    // Format a given date to a string
    fun formatDate(date: Date): String {
        return SimpleDateFormat("HH:mm:ss MMM dd yyyy", Locale.getDefault()).format(date)
    }
}
