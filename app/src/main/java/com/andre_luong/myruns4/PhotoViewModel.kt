package com.andre_luong.myruns4

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PhotoViewModel: ViewModel() {
    val userImage = MutableLiveData<Bitmap>()
}