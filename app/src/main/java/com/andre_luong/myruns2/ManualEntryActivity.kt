package com.andre_luong.myruns2

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.HOUR_OF_DAY
import java.util.Calendar.MILLISECOND
import java.util.Calendar.MINUTE
import java.util.Calendar.MONTH
import java.util.Calendar.SECOND
import java.util.Calendar.YEAR

class ManualEntryActivity: AppCompatActivity(), View.OnClickListener {
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_entry)

        initButtons()
        listView = findViewById(R.id.list_view_manual_entries)

        listView.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            EntryDetails.entries.map { it.value }.toTypedArray()
        )

        handleEntryDetailClick()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.button_cancel_manual_entry -> finish()
            R.id.button_save_manual_entry -> {
                Toast.makeText(this, "WIP: Save", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initButtons() {
        findViewById<Button>(R.id.button_cancel_manual_entry)
            .setOnClickListener(this)
        findViewById<Button>(R.id.button_save_manual_entry)
            .setOnClickListener(this)
    }

    // Handles functionality for each entry detail from list view items
    private fun handleEntryDetailClick() {
        listView.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                EntryDetails.DATE.id -> showDatePickerDialog()
                EntryDetails.TIME.id -> showTimePickerDialog()
                EntryDetails.DURATION.id -> {
                    DialogUtils.showNumberInputDialog(
                        this,
                        "Duration"
                    ) { duration ->
                        Log.d("debug: ", "duration: $duration")
                    }
                }
                EntryDetails.DISTANCE.id -> {
                    DialogUtils.showNumberInputDialog(
                        this,
                        "Distance"
                    ) { distance ->
                        Log.d("debug: ", "distance: $distance")
                    }
                }
                EntryDetails.CALORIES.id -> {
                    DialogUtils.showNumberInputDialog(
                        this,
                        "Calories"
                    ) { calories ->
                        Log.d("debug: ", "calories: $calories")
                    }
                }
                EntryDetails.HEART_RATE.id -> {
                    DialogUtils.showNumberInputDialog(
                        this,
                        "Heart Rate"
                    ) { heartRate ->
                        Log.d("debug: ", "heart rate: $heartRate")
                    }
                }
                EntryDetails.COMMENT.id -> {
                    DialogUtils.showCommentDialog(this, "") { comment ->
                        Log.d("debug: ", "comment: $comment")
                    }
                }
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(YEAR)
        val month = calendar.get(MONTH)
        val day = calendar.get(DAY_OF_MONTH)

        DatePickerDialog(this, { view, selectedYear, selectedMonth, selectedDay ->
            // Set the selected date in the calendar
            calendar.set(YEAR, selectedYear)
            calendar.set(MONTH, selectedMonth)
            calendar.set(DAY_OF_MONTH, selectedDay)
            calendar.set(HOUR_OF_DAY, 0)
            calendar.set(MINUTE, 0)
            calendar.set(SECOND, 0)
            calendar.set(MILLISECOND, 0)

            val timestamp = calendar.timeInMillis
            Log.d("debug:", "date in long: $timestamp")
        }, year, month, day)
            .show()
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(HOUR_OF_DAY)
        val minute = calendar.get(MINUTE)

        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            // Set the selected time in the calendar
            calendar.set(HOUR_OF_DAY, selectedHour)
            calendar.set(MINUTE, selectedMinute)
            calendar.set(SECOND, 0)
            calendar.set(MILLISECOND, 0)

            val timestamp = calendar.timeInMillis
            Log.d("debug:", "time in long: $timestamp")
        }, hour, minute, true)
            .show()
    }
}
