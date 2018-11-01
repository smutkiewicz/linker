package studios.aestheticapps.linker.floatingmenu

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
import java.io.IOException

class BubbleMenuService : HoverMenuService(), BubbleContentCallback
{
    private lateinit var bubbleMenu: BubbleMenu

    override fun onCreate()
    {
        super.onCreate()
        initReceiver()
        createNotification(R.string.service_is_active)
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

    private fun createNotification(titleResId: Int)
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

        builder.buildNotificationAndNotify(this)
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
        private const val SERVICE_CHANNEL_ID = "linker_channel_id"
        private const val BCAST_CONFIG_CHANGED = "android.intent.action.CONFIGURATION_CHANGED"

        fun showFloatingMenu(context: Context)
            = context.startService(Intent(context, BubbleMenuService::class.java))

        fun destroyFloatingMenu(context: Context)
            = context.stopService(Intent(context, BubbleMenuService::class.java))
    }
}