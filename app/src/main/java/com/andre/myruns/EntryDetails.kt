package com.andre.myruns

enum class EntryDetails(val id: Int, val value: String) {
    DATE(0, "Date"),
    TIME(1, "Time"),
    DURATION(2, "Duration"),
    DISTANCE(3, "Distance"),
    CALORIES(4, "Calories"),
    HEART_RATE(5, "Heart Rate"),
    COMMENT(6, "Comment"),
}