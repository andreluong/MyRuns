package com.andre_luong.myruns5.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import java.util.Calendar

@Entity(tableName = "exercise_table")
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "input_type_column")
    var inputType: Int, // Manual, GPS, or automatic

    @ColumnInfo(name = "activity_type_column")
    var activityType: Int, // Running, cycling, etc.

    @ColumnInfo(name = "date_time_column")
    var dateTime: Calendar = Calendar.getInstance(),

    @ColumnInfo(name = "duration_column")
    var duration: Double = 0.0, // in seconds

    @ColumnInfo(name = "distance_column")
    var distance: Double = 0.0, // meters or feet

    @ColumnInfo(name = "avg_pace_column")
    var avgPace: Double = 0.0,

    @ColumnInfo(name = "avg_speed_column")
    var avgSpeed: Double = 0.0,

    @ColumnInfo(name = "calories_column")
    var calories: Double = 0.0,

    @ColumnInfo(name = "climb_column")
    var climb: Double = 0.0,

    @ColumnInfo(name = "heart_rate_column")
    var heartRate: Double = 0.0,

    @ColumnInfo(name = "comment_column")
    var comment: String = "",

    @ColumnInfo(name = "location_list_column")
    var locationList: ArrayList<LatLng> = ArrayList()
) {
    fun evaluateInputType(): String {
        return when (inputType) {
            InputType.MANUAL.id -> InputType.MANUAL.value
            InputType.GPS.id -> InputType.GPS.value
            InputType.AUTOMATIC.id -> InputType.AUTOMATIC.value
            else -> "Unknown"
        }
    }

    fun evaluateActivityType(): String {
        return when (activityType) {
            ActivityType.RUNNING.id -> ActivityType.RUNNING.value
            ActivityType.WALKING.id -> ActivityType.WALKING.value
            ActivityType.STANDING.id -> ActivityType.STANDING.value
            ActivityType.CYCLING.id -> ActivityType.CYCLING.value
            ActivityType.HIKING.id -> ActivityType.HIKING.value
            ActivityType.DOWNHILL_SKIING.id -> ActivityType.DOWNHILL_SKIING.value
            ActivityType.CROSS_COUNTRY_SKIING.id -> ActivityType.CROSS_COUNTRY_SKIING.value
            ActivityType.SNOWBOARDING.id -> ActivityType.SNOWBOARDING.value
            ActivityType.SKATING.id -> ActivityType.SKATING.value
            ActivityType.SWIMMING.id -> ActivityType.SWIMMING.value
            ActivityType.MOUNTAIN_BIKING.id -> ActivityType.MOUNTAIN_BIKING.value
            ActivityType.WHEELCHAIR.id -> ActivityType.WHEELCHAIR.value
            else -> "Unknown"
        }
    }
}
