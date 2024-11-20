package com.andre_luong.myruns5

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.andre_luong.myruns5.model.Exercise
import com.andre_luong.myruns5.util.ExerciseUtils.buildExerciseViewModel
import com.andre_luong.myruns5.util.ExerciseUtils.getDistanceInString
import com.andre_luong.myruns5.util.Utils.formatDate
import java.util.Locale

class HistoryEntryActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_entry)

        val id = intent.getLongExtra("exerciseId", -1)
        if (id == -1L) {
            finish()
        }

        val exerciseViewModel = buildExerciseViewModel(this, this)

        val exercise = exerciseViewModel.getExerciseById(id)
        exercise.observe(this) { e ->
            if (e == null) {
                finish()
            } else {
                Log.d("debug: ", "Exercise: $e")
                loadExercise(e)
            }
        }

        // Set up delete button
        findViewById<Button>(R.id.history_entry_delete_button).setOnClickListener {
            exercise.value?.let { e ->
                exerciseViewModel.delete(e)
                finish()
            }
        }
    }

    // Load the exercise data into the edit text fields
    private fun loadExercise(exercise: Exercise) {
        // Input Type
        findViewById<EditText>(R.id.history_entry_edit_input_type)
            .setText(exercise.evaluateInputType())

        // Activity Type
        findViewById<EditText>(R.id.history_entry_edit_activity_type)
            .setText(exercise.evaluateActivityType())

        // Date Time
        findViewById<EditText>(R.id.history_entry_edit_date_time)
            .setText(formatDate(exercise.dateTime.time))

        // Duration
        val minutes = exercise.duration.toInt()
        val seconds = ((exercise.duration - minutes) * 60).toInt()
        val duration = "${minutes}mins ${seconds}secs"
        findViewById<EditText>(R.id.history_entry_edit_duration)
            .setText(duration)

        // Distance
        val distance = getDistanceInString(this, exercise)
        findViewById<EditText>(R.id.history_entry_edit_distance)
            .setText(distance)

        // Calories
        val calories = String.format(Locale.US, "%.0f cals", exercise.calories)
        findViewById<EditText>(R.id.history_entry_edit_calories)
            .setText(calories)

        // Heart Rate
        val heartRate = String.format(Locale.US, "%.0f bpm", exercise.heartRate)
        findViewById<EditText>(R.id.history_entry_edit_heart_rate)
            .setText(heartRate)
    }
}