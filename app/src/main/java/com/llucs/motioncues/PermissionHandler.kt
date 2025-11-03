package com.llucs.motioncues

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher

object PermissionHandler {

    fun hasOverlayPermission(context: Context): Boolean {
        return Settings.canDrawOverlays(context)
    }

    fun requestOverlayPermission(context: Context) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}")
        )
        if (context !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun hasSensorPermission(context: Context): Boolean {
        // Gyroscope + Accelerometer don't need runtime permission normally
        // but Android 14+ might block motion sensors without consent
        val sensorAllowed = Settings.System.getInt(
            context.contentResolver,
            "sensor_privacy_motion",
            0
        ) == 0

        return sensorAllowed
    }

}