package studios.aestheticapps.linker.floatingmenu

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.view.ContextThemeWrapper
import io.mattcarroll.hover.HoverMenu
import io.mattcarroll.hover.HoverView
import io.mattcarroll.hover.window.HoverMenuService
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.floatingmenu.content.BubbleContentCallback
import studios.aestheticapps.linker.utils.NotificationBuilder
import studios.aestheticapps.linker.utils.PrefsHelper
import studios.aestheticapps.linker.utils.PrefsHelper.VIEW_BUBBLE
import java.io.IOException

class BubbleMenuService : HoverMenuService(), BubbleContentCallback
{
    private lateinit var bubbleMenu: BubbleMenu

    override fun onCreate()
    {
        super.onCreate()
        initReceiver()
        createNotification(R.string.service_is_active)
        PrefsHelper.setLatestView(this, VIEW_BUBBLE)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            startForeground(
                FOREGROUND_ID,
                createNotification(R.string.service_is_active)
            )
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy()
    {
        super.onDestroy()
        destroyReceiver()
        NotificationBuilder.cancelNotification(this, R.string.service_is_active)
    }

    override fun getContextForHoverMenu()= ContextThemeWrapper(this, R.style.AppTheme)

    override fun onHoverMenuLaunched(intent: Intent, hoverView: HoverView)
    {
        hoverView.apply {
            setMenu(createHoverMenu()!!)
            collapse()
        }
    }

    override fun collapseBubble() = hoverView.collapse()

    override fun notifyContentChanged() = bubbleMenu.notifyMenuChanged()

    private fun createHoverMenu(): HoverMenu?
    {
        try
        {
            bubbleMenu = BubbleMenuFactory().createMenu(contextForHoverMenu, application, this)
            return bubbleMenu
        }
        catch (e: IOException)
        {
            throw RuntimeException(e)
        }
    }

    private fun initReceiver()
    {
        val filter = IntentFilter()
        filter.addAction(BCAST_CONFIG_CHANGED)
        this.registerReceiver(broadcastReceiver, filter)
    }

    private fun destroyReceiver()
    {
        unregisterReceiver(broadcastReceiver)
    }

    private fun restartService()
    {
        stopService(Intent(this, BubbleMenuService::class.java))
        startService(Intent(this, BubbleMenuService::class.java))
    }

    private fun createNotification(titleResId: Int): Notification?
    {
        val notificationChannelPriority = when
        {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> NotificationManager.IMPORTANCE_MIN
            else -> 0
        }

        val builder = NotificationBuilder(
            titleResId = titleResId,
            serviceChannelId = SERVICE_CHANNEL_ID,
            isOngoing = true,
            smallIconResId = R.drawable.ic_bubble,
            notificationPriority = NotificationCompat.PRIORITY_MIN,
            notificationChannelPriority = notificationChannelPriority,
            notificationColor = Color.GREEN
        )

        return builder.buildNotification(this)
    }

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context, myIntent: Intent)
        {
            if (myIntent.action == BCAST_CONFIG_CHANGED) restartService()
        }
    }

    companion object
    {
        private const val TAG = "BubbleMenuService"
        private const val FOREGROUND_ID = 2601
        private const val SERVICE_CHANNEL_ID = "linker_channel_id"
        private const val BCAST_CONFIG_CHANGED = "android.intent.action.CONFIGURATION_CHANGED"

        fun showFloatingMenu(context: Context)
        {
            val intent = Intent(context, BubbleMenuService::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                context.startForegroundService(intent)
            else
                context.startService(intent)
        }

        fun destroyFloatingMenu(context: Context)
            = context.stopService(Intent(context, BubbleMenuService::class.java))
    }
}