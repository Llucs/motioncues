package com.llucs.motioncues

object Constants {
    // Notification Channel
    const val NOTIFICATION_CHANNEL_ID = "motion_cues_channel"
    const val NOTIFICATION_ID = 101

    // Actions for the Foreground Service
    const val ACTION_START_SERVICE = "ACTION_START_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    const val ACTION_TOGGLE_EFFECT = "ACTION_TOGGLE_EFFECT"
    const val ACTION_OPEN_APP = "ACTION_OPEN_APP"

    // Preferences Keys
    const val PREFS_NAME = "motion_cues_prefs"
    const val KEY_ACTIVATION_MODE = "activation_mode"
    const val KEY_DOT_COLOR = "dot_color"
    const val KEY_DOT_COUNT = "dot_count"
    const val KEY_DOT_SIZE = "dot_size"
    const val KEY_EFFECT_ACTIVE = "effect_active"

    // Default Values
    const val DEFAULT_DOT_COLOR = 0xFF0000FF // Blue
    const val DEFAULT_DOT_COUNT = 10
    const val DEFAULT_DOT_SIZE = 1 // 1=Small, 2=Medium, 3=Large
    const val DEFAULT_ACTIVATION_MODE = "auto" // "off", "on", "auto"

    // GitHub Repository
    const val GITHUB_REPO_URL = "https://github.com/Llucs/motioncues/"

    // Vehicle Detection Thresholds (Empirical values)
    const val VEHICLE_SPEED_THRESHOLD_KMH = 8.0 // km/h
    const val VEHICLE_SPEED_THRESHOLD_MPS = VEHICLE_SPEED_THRESHOLD_KMH / 3.6 // m/s
    const val VEHICLE_DETECTION_DURATION_SECONDS = 5 // Duration to maintain speed above threshold
    const val SENSOR_SAMPLING_RATE_HZ = 50 // Hz (50 samples per second)
}

enum class ActivationMode(val value: String) {
    OFF("off"),
    ON("on"),
    AUTO("auto")
}

enum class DotSize(val value: Int) {
    SMALL(1),
    MEDIUM(2),
    LARGE(3)
}
