package com.llucs.motioncues

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataStore = SettingsDataStore(applicationContext)

        setContent {
            MainScreen(
                onStartService = {},
                onStopService = {},
                dataStore = dataStore
            )
        }
    }
}