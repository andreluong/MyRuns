package com.andre_luong.myruns5.model

enum class ActivityType(val id: Int, val value: String) {
    RUNNING(0, "Running"),
    WALKING(1, "Walking"),
    STANDING(2, "Standing"),
    CYCLING(3, "Cycling"),
    HIKING(4, "Hiking"),
    DOWNHILL_SKIING(5, "Downhill Skiing"),
    CROSS_COUNTRY_SKIING(6, "Cross-Country Skiing"),
    SNOWBOARDING(7, "Snowboarding"),
    SKATING(8, "Skating"),
    SWIMMING(9, "Swimming"),
    MOUNTAIN_BIKING(10, "Mountain Biking"),
    WHEELCHAIR(11, "Wheelchair")
}