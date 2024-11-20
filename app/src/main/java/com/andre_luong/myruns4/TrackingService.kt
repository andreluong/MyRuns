package com.andre_luong.myruns4

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.content.IntentFilter
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class TrackingService : Service(), LocationListener {
    private lateinit var locationManager: LocationManager
    private lateinit var notificationManager: NotificationManager
    private lateinit var trackingServiceBroadcastReceiver: TrackingServiceBroadcastReceiver

    private val NOTIFY_ID = 11
    private val CHANNEL_ID = "notification channel"
    private val PENDING_INTENT_REQUEST_CODE = 0

    private val weightInKg = 70
    private var startTime = System.currentTimeMillis()
    private var totalDuration = 0.0
    private var totalDistance = 0.0
    private var avgSpeed = 0.0
    private var calories = 0.0

    private lateinit var lastKnownLocation : Location

    companion object{
        const val STOP_SERVICE_ACTION = "stop service action"
    }

    override fun onCreate() {
        super.onCreate()

        Log.d("TrackingService", "onCreate() called")
        initLocationManager()

        showNotification()
        trackingServiceBroadcastReceiver = TrackingServiceBroadcastReceiver()

        val intentFilter = IntentFilter()
        intentFilter.addAction(STOP_SERVICE_ACTION)

        // Register the broadcast receiver
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(trackingServiceBroadcastReceiver, intentFilter, RECEIVER_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            registerReceiver(trackingServiceBroadcastReceiver, intentFilter)
        }

        startTime = System.currentTimeMillis()
    }

    // Initialize a location manager with last known location and request location updates
    private fun initLocationManager() {
        try {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

            if (!locationManager.isProviderEnabled(GPS_PROVIDER)) return

            val location = locationManager.getLastKnownLocation(GPS_PROVIDER)
            if (location != null) onLocationChanged(location)

            locationManager.requestLocationUpdates(GPS_PROVIDER, 0, 0f, this)
        } catch (e: SecurityException) {
            Log.d("TrackingService", "Security exception error in initLocationManager(): ${e.message}")
        }
    }

    // Update the location data and statistics when the location changes
    override fun onLocationChanged(loc: Location) {
        Log.d("TrackingService", "Location changed: ${loc.latitude}, ${loc.longitude}")

        if (::lastKnownLocation.isInitialized) {
            // Calculate the distance between the last known location and the current location in meters
            val distance = lastKnownLocation.distanceTo(loc)
            totalDistance += distance

            // Calculate the total duration in hours
            totalDuration = (System.currentTimeMillis() - startTime) / 3600000.0

            // Calculate the avg speed in meters per hour
            avgSpeed = if (totalDuration> 0) {
                totalDistance / totalDuration
            } else {
                0.0
            }

            // Calculate the calories burned
            val metValue = if (avgSpeed > 2.0) {
                1.25
            } else {
                0.75
            }
            calories += (weightInKg * metValue * distance)
        }

        lastKnownLocation = loc
        sendLocationBroadcast()
    }

    // Show a notification when the service is online
    private fun showNotification() {
        Log.d("TrackingService", "showNotification() called")

        // Create an intent to open the map activity
        val mapIntent = Intent(this, MapDisplayActivity::class.java)
        mapIntent.flags = FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(
            this,
            PENDING_INTENT_REQUEST_CODE,
            mapIntent,
            FLAG_IMMUTABLE
        )

        // Setup the notification
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
        notificationBuilder.setContentTitle("MyRuns")
        notificationBuilder.setContentText("Recording your path now")
        notificationBuilder.setContentIntent(pendingIntent)
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
        notificationBuilder.setSilent(true)
        notificationBuilder.setOngoing(true)

        createNotificationChannel()

        notificationManager.notify(NOTIFY_ID, notificationBuilder.build())
        Log.d("TrackingService", "Notification shown")
    }

    // Create a notification channel for the notification
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "channel name",
                IMPORTANCE_DEFAULT
            )
            notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    // Send a broadcast with the location data and statistics
    private fun sendLocationBroadcast() {
        val intent = Intent("com.andre_luong.myruns4.LOCATION_UPDATE")
        intent.putExtra("latitude", lastKnownLocation.latitude)
        intent.putExtra("longitude", lastKnownLocation.longitude)
        intent.putExtra("currentSpeed", lastKnownLocation.speed)
        intent.putExtra("totalDistance", totalDistance)
        intent.putExtra("averageSpeed", avgSpeed)
        intent.putExtra("totalDuration", totalDuration)
        intent.putExtra("calories", calories)
        sendBroadcast(intent)
    }

    // Broadcast receiver to stop the service
    inner class TrackingServiceBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            stopSelf()
            notificationManager.cancel(NOTIFY_ID)
            unregisterReceiver(trackingServiceBroadcastReceiver)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    // Stop the location updates when the service is destroyed
    override fun onDestroy() {
        super.onDestroy()

        if (::locationManager.isInitialized) {
            locationManager.removeUpdates(this)
        }
        notificationManager.cancel(NOTIFY_ID)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}