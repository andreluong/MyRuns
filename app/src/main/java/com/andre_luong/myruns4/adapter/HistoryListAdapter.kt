package com.andre_luong.myruns4.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import com.andre_luong.myruns4.R
import com.andre_luong.myruns4.model.Exercise
import com.andre_luong.myruns4.HistoryEntryActivity
import com.andre_luong.myruns4.MapDisplayActivity
import com.andre_luong.myruns4.model.InputType
import com.andre_luong.myruns4.util.ExerciseUtils.getDistanceInString
import com.andre_luong.myruns4.util.Utils.formatDate

class HistoryListAdapter(
    private var context: Context,
    private var list: List<Exercise>
) : BaseAdapter() {

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Exercise {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return list[position].id
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater
            .from(context)
            .inflate(R.layout.history_list_item, parent, false)

        val exercise = getItem(position)

        val nameTextView = view.findViewById<TextView>(R.id.history_list_item_name)
        nameTextView.text = getNameText(exercise)

        val descriptionTextView = view.findViewById<TextView>(R.id.history_list_item_description)
        descriptionTextView.text = getDescriptionText(exercise)

        loadEntryOnClick(view, exercise)

        return view
    }

    private fun getNameText(exercise: Exercise): String {
        return "${exercise.evaluateInputType()}: " +
                "${exercise.evaluateActivityType()}, " +
                formatDate(exercise.dateTime.time)
    }

    private fun getDescriptionText(exercise: Exercise): String {
        val distanceText = getDistanceInString(context, exercise)

        return if (exercise.duration != 0.0) {
            val minutes = exercise.duration.toInt() / 60
            val seconds = exercise.duration.toInt() % 60

            if (minutes == 0) {
                "$distanceText, ${seconds}secs"
            } else {
                "$distanceText, ${minutes}mins ${seconds}secs"
            }
        } else {
            "$distanceText, 0secs"
        }
    }

    // Loads a selected exercise page
    private fun loadEntryOnClick(view: View, exercise: Exercise) {
        view.setOnClickListener {
            val exerciseIntent = when (exercise.inputType) {
                InputType.MANUAL.id -> {
                    Intent(context, HistoryEntryActivity::class.java)
                }
                else -> {
                    Intent(context, MapDisplayActivity::class.java)
                }
            }
            exerciseIntent.putExtra("exerciseId", exercise.id)
            startActivity(context, exerciseIntent, null)
        }
    }

    fun setList(newList: List<Exercise>) {
        list = newList
    }
}