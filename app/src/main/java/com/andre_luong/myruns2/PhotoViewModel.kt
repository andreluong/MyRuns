package com.andre_luong.myruns2

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PhotoViewModel: ViewModel() {
    val userImage = MutableLiveData<Bitmap>()
}