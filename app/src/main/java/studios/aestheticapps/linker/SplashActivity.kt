package studios.aestheticapps.linker

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import studios.aestheticapps.linker.extensions.checkForDrawOverlaysPermissions
import studios.aestheticapps.linker.floatingmenu.BubbleMenuService
import studios.aestheticapps.linker.floatingmenu.theme.BubbleTheme
import studios.aestheticapps.linker.floatingmenu.theme.BubbleThemeManager
import studios.aestheticapps.linker.utils.PrefsHelper
import studios.aestheticapps.linker.utils.PrefsHelper.VIEW_APP
import studios.aestheticapps.linker.utils.PrefsHelper.VIEW_BUBBLE

class SplashActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        val auth = FirebaseAuth.getInstance()
        when(auth.currentUser)
        {
            null -> launchLoginActivity()
            else ->
            {
                val viewType = PrefsHelper.obtainLatestView(this)

                when (viewType)
                {
                    VIEW_APP -> launchMainActivity()
                    VIEW_BUBBLE -> launchBubbles()
                }
            }
        }
    }

    private fun launchMainActivity()
    {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun launchBubbles()
    {
        if(checkForDrawOverlaysPermissions())
        {
            initThemeManager()
            BubbleMenuService.showFloatingMenu(this)
            finish()
        }
        else
        {
            launchMainActivity()
        }
    }

    private fun launchLoginActivity()
    {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun initThemeManager()
    {
        val defaultTheme = BubbleTheme(
            ContextCompat.getColor(this, R.color.colorPrimary),
            ContextCompat.getColor(this, R.color.colorPrimary)
        )

        BubbleThemeManager.init(defaultTheme)
    }
}