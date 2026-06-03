package by.voiteshonok.valacugi.core.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import by.voiteshonok.valacugi.R

object ValacugiNotificationChannels {
    const val Alerts: String = "valacugi_alerts"
}

class ValacugiNotificationSender(
    private val context: Context
) : NotificationSender {
    private val notificationManager: NotificationManagerCompat =
        NotificationManagerCompat.from(context)

    override fun canPostNotifications(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return notificationManager.areNotificationsEnabled()
        }
        val hasPermission: Boolean = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        return hasPermission && notificationManager.areNotificationsEnabled()
    }

    override fun sendBellAlert(): Boolean {
        return sendAlert(
            title = context.getString(R.string.notification_bell_title),
            message = context.getString(R.string.notification_bell_message)
        )
    }

    fun sendAlert(title: String, message: String): Boolean {
        if (!canPostNotifications()) {
            return false
        }
        ensureAlertsChannel()
        val notification = NotificationCompat.Builder(context, ValacugiNotificationChannels.Alerts)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(NotificationIdBellAlert, notification)
        return true
    }

    private fun ensureAlertsChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        val channel = NotificationChannel(
            ValacugiNotificationChannels.Alerts,
            context.getString(R.string.notification_channel_alerts_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.notification_channel_alerts_description)
        }
        val systemManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        systemManager.createNotificationChannel(channel)
    }

    companion object {
        private const val NotificationIdBellAlert: Int = 1001
    }
}
