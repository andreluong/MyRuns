package com.andre_luong.myruns4

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Color.BLACK
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.andre_luong.myruns4.database.ExerciseViewModel
import com.andre_luong.myruns4.model.ActivityType
import com.andre_luong.myruns4.model.Exercise
import com.andre_luong.myruns4.model.InputType
import com.andre_luong.myruns4.util.ExerciseUtils.buildExerciseViewModel
import com.andre_luong.myruns4.util.ExerciseUtils.hoursToSeconds
import com.andre_luong.myruns4.util.ExerciseUtils.mpsToUnitType
import com.andre_luong.myruns4.util.ExerciseUtils.mToUnitType
import com.andre_luong.myruns4.util.SettingsUtils.getDistanceUnits
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_GREEN
import com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import java.lang.String.format
import java.util.Locale.US

class MapDisplayActivity:
    AppCompatActivity(),
    View.OnClickListener,
    OnMapReadyCallback {

    // Map variables
    private lateinit var map: GoogleMap
    private var mapCentered = false
    private lateinit var markerOptions: MarkerOptions
    private lateinit var polylineOptions: PolylineOptions
    private lateinit var polylines: ArrayList<Polyline>
    private var originMarker: Marker? = null
    private var destinationMarker: Marker? = null
    private val placeholderPos = LatLng(0.0, 0.0)

    private lateinit var mapDisplayViewModel : MapDisplayViewModel
    private lateinit var extras: Bundle
    private lateinit var exerciseViewModel: ExerciseViewModel
    private lateinit var exercise: Exercise

    // GPS - Configurable text views
    private lateinit var activityTypeTV: TextView
    private lateinit var avgSpeedTV: TextView
    private lateinit var curSpeedTV: TextView
    private lateinit var climbTV: TextView
    private lateinit var caloriesTV: TextView
    private lateinit var distanceTV: TextView

    // Exercise details
    private var locations = ArrayList<LatLng>()
    private var curSpeed = 0.0
    private var totalDistance = 0.0
    private var avgSpeed = 0.0
    private var durationInHours = 0.0
    private var calories = 0.0
    private var unitOfMeasure = "km"
    private var unitOfMeasureFull = "Kilometers"

    // Create a broadcast receiver to receive location updates from TrackingService
    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Get and set data from Tracking service
            val latitude = intent.getDoubleExtra("latitude", 0.0)
            val longitude = intent.getDoubleExtra("longitude", 0.0)
            curSpeed = mpsToUnitType(intent.getFloatExtra("currentSpeed", curSpeed.toFloat()), unitOfMeasure)
            totalDistance = mToUnitType(intent.getDoubleExtra("totalDistance", totalDistance), unitOfMeasure)
            avgSpeed = mToUnitType(intent.getDoubleExtra("averageSpeed", avgSpeed), unitOfMeasure)
            durationInHours = intent.getDoubleExtra("totalDuration", durationInHours)
            calories = mToUnitType(intent.getDoubleExtra("calories", calories), unitOfMeasure)

            Log.d("MapDisplayActivity", "Received location: $latitude, $longitude")

            // Add the location to the view model
            val loc = LatLng(latitude, longitude)
            mapDisplayViewModel.addLocation(loc)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_display)

        // Initialize map fragment and request notification when the map is ready
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment)
                as SupportMapFragment
        mapFragment.getMapAsync(this)

        initGpsTextViews()
        initButtons()

        mapDisplayViewModel = ViewModelProvider(this).get(MapDisplayViewModel::class.java)
        exerciseViewModel = buildExerciseViewModel(this, this)

        // Set the unit of measure based on user settings
        // 0 = Kilometers, 1 = Miles
        val unit = getDistanceUnits(this)
        if (unit == 1) {
            unitOfMeasure = "mi"
            unitOfMeasureFull = "Miles"
        }

        extras = intent.extras!!
        setupByInputType()

        // Register the location receiver
        registerReceiver(
            locationReceiver,
            IntentFilter("com.andre_luong.myruns4.LOCATION_UPDATE"),
            RECEIVER_EXPORTED
        )

        // Observe location data from the ViewModel
        mapDisplayViewModel.locationData.observe(this) { locationList ->
            handleMapPolyLines(locationList)
            locations = locationList
        }
    }

    // Set up this activity based on the input type
    // Either as a history entry or as a new exercise
    private fun setupByInputType() {
        // View as a history entry
        if (extras.containsKey("exerciseId")) {
            // Get exercise by ID
            val id = extras.getLong("exerciseId")
            exerciseViewModel.getExerciseById(id)
                .observe(this) { e ->
                    if (e == null) {
                        finish()
                    } else {
                        exercise = e
                        setUpActivityTypeTextView(exercise.inputType, exercise.activityType)
                        totalDistance = exercise.distance
                        avgSpeed = exercise.avgSpeed
                        calories = exercise.calories
                    }
                }

        // Interact as a new exercise
        } else if (extras.containsKey("inputType")) {
            findViewById<Button>(R.id.map_delete_button).visibility = GONE
            val inputType = extras.getInt("inputType")
            val activityType = extras.getInt("activityType")

            exercise = Exercise(
                inputType = inputType,
                activityType = activityType
            )
            setUpActivityTypeTextView(inputType, activityType)
        }
    }

    private fun setUpActivityTypeTextView(inputType: Int, activityType: Int) {
        when (inputType) {
            // GPS entry
            InputType.GPS.id -> {
                ActivityType.entries.find { it.id == activityType }?.let {
                    activityTypeTV.text = buildString {
                        append("Type: ")
                        append(it.value)
                    }
                }
            }
            // Automatically detect and track an activity
            InputType.AUTOMATIC.id -> {
                activityTypeTV.text = buildString {
                    append("Type: Unknown ")
                }
            }
        }
    }

    // Handle on click functionality of each button
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.map_delete_button -> {
                // Delete exercise from database
                exerciseViewModel.delete(exercise)
                Toast.makeText(this, "Exercise deleted!", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
            R.id.map_cancel_button -> finish()
            R.id.map_save_button -> {
                // Save exercise to database
                exercise.locationList = locations
                exercise.distance = totalDistance
                exercise.avgSpeed = avgSpeed
                exercise.calories = calories
                exercise.duration = hoursToSeconds(durationInHours)
                exerciseViewModel.insert(exercise)
                Log.d("MapDisplayActivity", "Exercise saved: $exercise")

                Toast.makeText(this, "Exercise saved!", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    private fun initButtons() {
        findViewById<Button>(R.id.map_delete_button).setOnClickListener(this)
        findViewById<Button>(R.id.map_cancel_button).setOnClickListener(this)
        findViewById<Button>(R.id.map_save_button).setOnClickListener(this)
    }

    private fun initGpsTextViews() {
        activityTypeTV = findViewById(R.id.map_activity_type)
        avgSpeedTV = findViewById(R.id.map_avg_speed)
        curSpeedTV = findViewById(R.id.map_cur_speed)
        climbTV = findViewById(R.id.map_climb)
        caloriesTV = findViewById(R.id.map_calories)
        distanceTV = findViewById(R.id.map_distance)
    }

    // Initialize the map and set listeners for map clicks
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.mapType = MAP_TYPE_NORMAL

        polylineOptions = PolylineOptions()
        polylineOptions.color(BLACK)
        polylines = ArrayList()
        markerOptions = MarkerOptions()

        // History entry
        if (extras.containsKey("exerciseId")) {
            // Load exercise data
            exerciseViewModel.getExerciseById(extras.getLong("exerciseId"))
                .observe(this) { exercise ->
                    if (exercise == null) {
                        finish()
                    } else {
                        handleMapPolyLines(exercise.locationList)
                    }
                }
        // New exercise
        } else {
            checkPermission()
            addPlaceholderMarker()
        }
    }

    private fun handleMapPolyLines(locationList: ArrayList<LatLng>) {
        if (locationList.isNotEmpty()) {
            polylineOptions.points.clear()
            polylineOptions.addAll(locationList)
            map.addPolyline(polylineOptions)

            // Add origin marker if it doesn't already exist with the first location
            val firstPos = locationList.first()
            if (originMarker == null) {
                originMarker = map.addMarker(
                    MarkerOptions()
                        .position(firstPos)
                        .title("Origin")
                )
                centerMap(firstPos)
            }

            // Update destination marker with the last location
            val lastPos = locationList.last()
            if (destinationMarker?.position != lastPos && firstPos != lastPos) {
                mapCentered = false
                updateDestinationMarker(lastPos)
            }

            avgSpeedTV.text = format(US, "Avg speed: %.2f %s/h", avgSpeed, unitOfMeasure)
            climbTV.text = format(US, "Climb: %.2f %s", 0.0, unitOfMeasureFull)
            caloriesTV.text = format(US, "Calories: %.0f", calories)
            distanceTV.text = format(US, "Distance: %.2f %s", totalDistance, unitOfMeasureFull)

            // Viewed as a history entry
            if (extras.containsKey("exerciseId")) {
                curSpeedTV.text = "Cur speed: n/a"
            // Viewed as a new exercise
            } else {
                curSpeedTV.text = format(US, "Cur speed: %.2f %s/h", curSpeed, unitOfMeasure)
            }
        }
    }

    // Center the map to the given position
    private fun centerMap(pos: LatLng) {
        if (!mapCentered) {
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(pos, 17f)
            map.animateCamera(cameraUpdate)
            mapCentered = true
        }
    }

    // Add a default placeholder marker to the map
    private fun addPlaceholderMarker() {
        map.addMarker(
            MarkerOptions()
                .position(placeholderPos)
                .title("Marker")
        )
    }

    // Update the map with a new location for the destination marker
    private fun updateDestinationMarker(latLng: LatLng) {
        destinationMarker?.remove()
        destinationMarker = map.addMarker(
            MarkerOptions()
                .position(latLng)
                .title("Destination")
                .icon(defaultMarker(HUE_GREEN))
        )
        centerMap(latLng)
    }

    // Check location permission
    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), 0)
        } else {
            startTrackingService()
        }
    }

    // Handle location permission request result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            // Permission is granted, start the tracking service
            if ((grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED)) {
                startTrackingService()

            // Permission denied
            } else {
                Toast.makeText(this, "Fine location permission required to track location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Start the tracking service
    private fun startTrackingService() {
        val trackingServiceIntent = Intent(this, TrackingService::class.java)
        startService(trackingServiceIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the location receiver
        unregisterReceiver(locationReceiver)
    }

    override fun finish() {
        super.finish()
        // Stop tracking service
        val intent = Intent()
        intent.action = TrackingService.STOP_SERVICE_ACTION
        sendBroadcast(intent)
    }
}