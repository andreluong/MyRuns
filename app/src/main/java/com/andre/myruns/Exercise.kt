package com.andre.myruns

data class Exercise(
    val date: Long,
    val time: Long,
    val duration: Double,
    val distance: Double,
    val calories: Double,
    val heartRate: Double,
    val comment: String,
    val activity: Int
)
