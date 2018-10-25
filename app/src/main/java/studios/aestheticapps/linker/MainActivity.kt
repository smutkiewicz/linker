package studios.aestheticapps.linker

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import studios.aestheticapps.linker.extensions.checkForDrawOverlaysPermissions
import studios.aestheticapps.linker.extensions.createDrawOverlayPermissionsIntent
import studios.aestheticapps.linker.floatingmenu.BubbleMenuService
import studios.aestheticapps.linker.floatingmenu.theme.BubbleTheme
import studios.aestheticapps.linker.floatingmenu.theme.BubbleThemeManager

class MainActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        createFab()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        return when (item.itemId)
        {
            R.id.action_bubbles ->
            {
                openBubbles()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun createFab()
    {
        fab.setOnClickListener { _ ->
            //TODO
        }
    }

    private fun openBubbles()
    {
        if(checkForDrawOverlaysPermissions())
        {
            initThemeManager()
            BubbleMenuService.showFloatingMenu(this@MainActivity)
            finish()
        }
        else
        {
            createDrawOverlayPermissionsIntent()
        }
    }

    private fun initThemeManager()
    {
        val defaultTheme = BubbleTheme(
            ContextCompat.getColor(this, R.color.colorPrimary),
            ContextCompat.getColor(this, R.color.colorPrimary)
        )

        BubbleThemeManager.init(defaultTheme)
    }

    companion object
    {
        private const val TAG = "MainActivity"
        const val MY_PERMISSIONS_REQUEST_DRAW_OVERLAY = 0
    }
}
