package com.andre_luong.myruns5

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PhotoViewModel: ViewModel() {
    val userImage = MutableLiveData<Bitmap>()
}