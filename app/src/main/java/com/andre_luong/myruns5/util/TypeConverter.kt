package com.andre_luong.myruns5.util

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.Calendar

// TypeConverter class for Room database
class TypeConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Calendar? {
        return value?.let {
            Calendar.getInstance().apply { timeInMillis = it }
        }
    }

    @TypeConverter
    fun calendarToTimestamp(calendar: Calendar?): Long? {
        return calendar?.timeInMillis
    }

    @TypeConverter
    fun arrayListToByteArray(value: ArrayList<LatLng>?): ByteArray? {
        if (value == null) {
            return null
        }

        // Convert arraylist to pair<double, double> arraylist for serialization
        val pairList = value.map { Pair(it.latitude, it.longitude) }

        val byteArrayOutputStream = ByteArrayOutputStream()
        ObjectOutputStream(byteArrayOutputStream).use { output ->
            output.writeObject(pairList)
            output.flush()
        }
        return byteArrayOutputStream.toByteArray()
    }

    @TypeConverter
    fun byteArrayToArrayList(value: ByteArray?): ArrayList<LatLng>? {
        if (value == null) {
            return null
        }

        // Read object from byte array of pair<double, double> arraylist
        // Then convert to LatLng arraylist
        val byteArrayInputStream = ByteArrayInputStream(value)
        ObjectInputStream(byteArrayInputStream).use { input ->
            val pairList = input.readObject() as ArrayList<Pair<Double, Double>>
            return ArrayList(pairList.map { LatLng(it.first, it.second) })
        }
    }
}