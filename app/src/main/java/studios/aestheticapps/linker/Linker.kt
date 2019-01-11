package studios.aestheticapps.linker

import android.app.Application
import android.content.Intent
import android.provider.Settings
import android.support.v4.content.ContextCompat
import studios.aestheticapps.linker.floatingmenu.BubbleMenuService
import studios.aestheticapps.linker.floatingmenu.theme.BubbleTheme
import studios.aestheticapps.linker.floatingmenu.theme.BubbleThemeManager
import studios.aestheticapps.linker.utils.PrefsHelper
import studios.aestheticapps.linker.utils.PrefsHelper.VIEW_APP

class Linker : Application()
{
    override fun onCreate()
    {
        super.onCreate()

        val viewType = PrefsHelper.obtainLatestView(this)

        when (viewType)
        {
            PrefsHelper.VIEW_BUBBLE -> launchBubbles()
            VIEW_APP -> launchSplashActivity()
        }
    }

    private fun launchSplashActivity()
    {
        val intent = Intent(this, SplashActivity::class.java)
        startActivity(intent)
    }

    private fun launchBubbles()
    {
        if(checkForDrawOverlaysPermissions())
        {
            initThemeManager()
            BubbleMenuService.showFloatingMenu(this)
        }
    }

    private fun checkForDrawOverlaysPermissions()
        = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) Settings.canDrawOverlays(applicationContext) else true

    private fun initThemeManager()
    {
        val defaultTheme = BubbleTheme(
            ContextCompat.getColor(this, R.color.colorPrimary),
            ContextCompat.getColor(this, R.color.colorPrimary)
        )

        BubbleThemeManager.init(defaultTheme)
    }
}