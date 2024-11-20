package com.andre_luong.myruns5.model

enum class InputType(val id: Int, val value: String) {
    MANUAL(0, "Manual Entry"),
    GPS(1, "GPS"),
    AUTOMATIC(2, "Automatic")
}