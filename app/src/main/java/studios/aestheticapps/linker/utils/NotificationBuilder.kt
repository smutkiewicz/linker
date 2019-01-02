package studios.aestheticapps.linker.utils

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.v4.app.NotificationCompat
import studios.aestheticapps.linker.extensions.setNotificationChannel

class NotificationBuilder(private val titleResId: Int,
                          private val serviceChannelId: String = "default_channel",
                          private val isOngoing: Boolean = true,
                          private val smallIconResId: Int,
                          private val notificationPriority: Int = NotificationCompat.PRIORITY_DEFAULT,
                          private val notificationChannelPriority: Int = 0,
                          private val notificationColor: Int = Color.GREEN)
{
    fun buildNotification(context: Context): Notification?
    {
        val nm = context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        val title = context.getString(titleResId)

        val builder = NotificationCompat.Builder(context, serviceChannelId)
            .apply {
                setWhen(System.currentTimeMillis())
                setContentTitle(title)
                setOngoing(isOngoing)
                setSmallIcon(smallIconResId)
                priority = notificationPriority
                color = notificationColor
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            nm.setNotificationChannel(
                serviceChannelId,
                serviceChannelId,
                notificationChannelPriority
            )

            builder.setChannelId(serviceChannelId)
        }

        return builder.build()
    }

    companion object
    {
        fun cancelNotification(context: Context, titleResId: Int)
        {
            val nm = context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
            nm.cancel(titleResId)
        }
    }
}