package com.andre.myruns

import android.Manifest.permission.CAMERA
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat.checkSelfPermission

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
}
