package com.andre_luong.myruns4

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.andre_luong.myruns4.model.EntryDetails
import com.andre_luong.myruns4.model.Exercise
import com.andre_luong.myruns4.util.DialogUtils
import com.andre_luong.myruns4.util.ExerciseUtils.buildExerciseViewModel
import com.andre_luong.myruns4.util.ExerciseUtils.distanceToMiles
import com.andre_luong.myruns4.util.SettingsUtils.getDistanceUnits
import java.util.Calendar
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.HOUR_OF_DAY
import java.util.Calendar.MILLISECOND
import java.util.Calendar.MINUTE
import java.util.Calendar.MONTH
import java.util.Calendar.SECOND
import java.util.Calendar.YEAR

class ManualEntryActivity: AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var exercise: Exercise

    private var calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_entry)

        val exerciseViewModel = buildExerciseViewModel(this, this)

        exercise = Exercise(
            inputType = intent.getIntExtra("inputType", 0),
            activityType = intent.getIntExtra("activityType", 0)
        )

        // Cancel button
        findViewById<Button>(R.id.button_cancel_manual_entry).setOnClickListener {
            finish()
        }

        // Save button - save exercise to database
        findViewById<Button>(R.id.button_save_manual_entry).setOnClickListener {
            exerciseViewModel.insert(exercise)
            Toast.makeText(this@ManualEntryActivity, "Exercise saved!", Toast.LENGTH_SHORT)
                .show()
            Log.d("debug: ", "Exercise saved with ID: ${exercise.id}")
            finish()
        }

        // Set up the list view
        listView = findViewById(R.id.list_view_manual_entries)
        listView.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            EntryDetails.entries.map { it.value }.toTypedArray()
        )

        handleEntryDetailClick()
    }

    // Handles functionality for each entry detail from list view items
    private fun handleEntryDetailClick() {
        listView.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                EntryDetails.DATE.id -> showDatePickerDialog()
                EntryDetails.TIME.id -> showTimePickerDialog()
                EntryDetails.DURATION.id -> {
                    DialogUtils.showNumberInputDialog(this, "Duration") {
                        duration -> exercise.duration = duration!!.toDouble()
                    }
                }
                EntryDetails.DISTANCE.id -> {
                    DialogUtils.showNumberInputDialog(this, "Distance") { distanceInput ->
                        // Save distance in metric units
                        if (distanceInput != null) {
                            var distance = distanceInput

                            // Convert miles to kilometres
                            if (getDistanceUnits(this) == 1) {
                                distance = distanceToMiles(distance)
                            }
                            exercise.distance = distance
                        }
                    }
                }
                EntryDetails.CALORIES.id -> {
                    DialogUtils.showNumberInputDialog(this, "Calories", false) {
                        calories -> exercise.calories = calories!!.toDouble()
                    }
                }
                EntryDetails.HEART_RATE.id -> {
                    DialogUtils.showNumberInputDialog(this, "Heart Rate", false) {
                        heartRate -> exercise.heartRate = heartRate!!.toDouble()
                    }
                }
                EntryDetails.COMMENT.id -> {
                    DialogUtils.showCommentDialog(this, "") {
                        comment -> exercise.comment = comment!!
                    }
                }
            }
        }
    }

    private fun showDatePickerDialog() {
        val year = calendar.get(YEAR)
        val month = calendar.get(MONTH)
        val day = calendar.get(DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            // Set the selected date in the calendar
            calendar.set(YEAR, selectedYear)
            calendar.set(MONTH, selectedMonth)
            calendar.set(DAY_OF_MONTH, selectedDay)
            calendar.set(HOUR_OF_DAY, 0)
            calendar.set(MINUTE, 0)
            calendar.set(SECOND, 0)
            calendar.set(MILLISECOND, 0)

            exercise.dateTime = calendar

            val timestamp = calendar.timeInMillis
            Log.d("debug:", "date in long: $timestamp")
        }, year, month, day)
            .show()
    }

    private fun showTimePickerDialog() {
        val hour = calendar.get(HOUR_OF_DAY)
        val minute = calendar.get(MINUTE)

        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            // Set the selected time in the calendar
            calendar.set(HOUR_OF_DAY, selectedHour)
            calendar.set(MINUTE, selectedMinute)
            calendar.set(SECOND, 0)
            calendar.set(MILLISECOND, 0)

            exercise.dateTime = calendar

            val timestamp = calendar.timeInMillis
            Log.d("debug:", "time in long: $timestamp")
        }, hour, minute, true)
            .show()
    }
}
