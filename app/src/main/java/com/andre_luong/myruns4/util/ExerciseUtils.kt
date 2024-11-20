package com.andre_luong.myruns4.util

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.andre_luong.myruns4.database.ExerciseDatabase
import com.andre_luong.myruns4.database.ExerciseRepository
import com.andre_luong.myruns4.database.ExerciseViewModel
import com.andre_luong.myruns4.database.ExerciseViewModelFactory
import com.andre_luong.myruns4.model.Exercise
import com.andre_luong.myruns4.util.SettingsUtils.getDistanceUnits
import java.lang.String.format
import java.util.Locale.US

object ExerciseUtils {
    // Build the exercise view model
    fun buildExerciseViewModel(context: Context, viewModelStoreOwner: ViewModelStoreOwner): ExerciseViewModel {
        // Set up the database dao
        val database = ExerciseDatabase.getInstance(context)
        val exerciseDao = database.exerciseDatabaseDao

        // Set up the repository
        val repository = ExerciseRepository(exerciseDao)

        // Set up the view model
        val viewModelFactory = ExerciseViewModelFactory(repository)
        return ViewModelProvider(viewModelStoreOwner, viewModelFactory)[ExerciseViewModel::class.java]
    }

    fun distanceToMiles(distance: Double): Double {
        return distance * 0.621371f
    }

    fun getDistanceInString(context: Context, exercise: Exercise): String {
        if (getDistanceUnits(context) == 0) {
            return format(US, "%.2f Kilometers", exercise.distance)
        } else {
            return format(US, "%.2f Miles", distanceToMiles(exercise.distance))
        }
    }

    // Convert meters to kilometers
    private fun mToKm(metres: Double): Double {
        return metres / 1000
    }

    // Convert meters to miles
    private fun mToMi(metres: Double): Double {
        return metres / 1609
    }

    // Convert meters to a unit type
    fun mToUnitType(meters: Double, unit: String): Double {
        return if (unit == "km") {
            mToKm(meters)
        } else {
            mToMi(meters)
        }
    }

    // Convert meters per second to kilometers per hour
    private fun mpsToKmh(ms: Float): Float {
        return ms * 3600
    }

    // Convert meters per second to a unit type
    fun mpsToUnitType(ms: Float, unit: String): Double {
        val kmh = mpsToKmh(ms)
        if (unit == "km") {
            return mToKm(kmh.toDouble())
        } else {
            return mToMi(kmh.toDouble())
        }
    }

    fun hoursToSeconds(hours: Double): Double {
        return hours * 3600
    }
}