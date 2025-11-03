package com.llucs.motioncues

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MotionService : LifecycleService() {

    private lateinit var sensorDetector: SensorDetector
    private lateinit var dataStore: SettingsDataStore
    private var isEffectActive = false
    private var activationMode = Constants.DEFAULT_ACTIVATION_MODE

    override fun onCreate() {
        super.onCreate()
        sensorDetector = SensorDetector(this)
        dataStore = SettingsDataStore(this)

        // Observar o estado de movimento do veículo
        lifecycleScope.launch {
            sensorDetector.isMovingInVehicle.collectLatest { isMoving ->
                handleAutomaticActivation(isMoving)
            }
        }

        // Observar o modo de ativação
        lifecycleScope.launch {
            dataStore.activationModeFlow.collectLatest { mode ->
                activationMode = mode
                // Reavaliar o estado do efeito ao mudar o modo
                if (mode == ActivationMode.ON.value) {
                    setEffectActive(true)
                } else if (mode == ActivationMode.OFF.value) {
                    setEffectActive(false)
                }
            }
        }

        // Observar o estado manual do efeito
        lifecycleScope.launch {
            dataStore.effectActiveFlow.collectLatest { active ->
                isEffectActive = active
                updateNotification()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        when (intent?.action) {
            Constants.ACTION_START_SERVICE -> {
                startForeground(Constants.NOTIFICATION_ID, createNotification())
                sensorDetector.startDetection()
            }
            Constants.ACTION_STOP_SERVICE -> {
                sensorDetector.stopDetection()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
            Constants.ACTION_TOGGLE_EFFECT -> {
                // Alternar o estado do efeito apenas se o modo não for automático
                if (activationMode != ActivationMode.AUTO.value) {
                    lifecycleScope.launch {
                        dataStore.saveEffectActive(!isEffectActive)
                    }
                }
            }
            else -> {
                // Iniciar como Foreground Service por padrão
                startForeground(Constants.NOTIFICATION_ID, createNotification())
                sensorDetector.startDetection()
            }
        }

        return START_STICKY
    }

    private fun handleAutomaticActivation(isMoving: Boolean) {
        if (activationMode == ActivationMode.AUTO.value) {
            setEffectActive(isMoving)
        }
    }

    private fun setEffectActive(active: Boolean) {
        if (isEffectActive != active) {
            lifecycleScope.launch {
                dataStore.saveEffectActive(active)
            }
        }
    }

    private fun createNotification(): Notification {
        createNotificationChannel()

        // Intent para abrir o app (clique longo)
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            action = Constants.ACTION_OPEN_APP
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingOpenAppIntent = PendingIntent.getActivity(
            this, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE
        )

        // Intent para alternar o efeito (clique curto/botão)
        val toggleIntent = Intent(this, MotionService::class.java).apply {
            action = Constants.ACTION_TOGGLE_EFFECT
        }
        val pendingToggleIntent = PendingIntent.getService(
            this, 1, toggleIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val text = if (isEffectActive) getString(R.string.notification_text_on) else getString(R.string.notification_text_off)

        val builder = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_notification) // Ícone a ser criado
            .setContentIntent(pendingOpenAppIntent) // Clique longo abre o app
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true) // Notificação persistente

        // Adicionar botão de ação (Alternar Efeito)
        builder.addAction(
            R.drawable.ic_notification, // Ícone a ser criado
            getString(R.string.notification_action_toggle),
            pendingToggleIntent
        )

        return builder.build()
    }

    private fun updateNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(Constants.NOTIFICATION_ID, createNotification())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_description)
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        throw UnsupportedOperationException("Not yet implemented")
    }
}
