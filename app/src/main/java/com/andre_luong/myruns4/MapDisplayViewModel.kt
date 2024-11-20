package com.andre_luong.myruns4

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class MapDisplayViewModel : ViewModel() {
    private val _locationData = MutableLiveData<ArrayList<LatLng>>(ArrayList())
    val locationData: LiveData<ArrayList<LatLng>> get() = _locationData

    fun addLocation(pos: LatLng) {
        val locations = _locationData.value ?: ArrayList()
        // Adds a location if list is empty
        // Otherwise, adds a location if it is different from the last one
        if (locations.isEmpty() || pos != locations.last()) {
            locations.add(pos)
        }
        _locationData.value = locations
    }
}