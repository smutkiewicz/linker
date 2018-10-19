package studios.aestheticapps.linker.floatingmenu

import android.content.Context
import android.content.Intent
import android.view.ContextThemeWrapper
import io.mattcarroll.hover.HoverMenu
import io.mattcarroll.hover.HoverView
import io.mattcarroll.hover.window.HoverMenuService
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.floatingmenu.theme.BubbleTheme
import studios.aestheticapps.linker.floatingmenu.util.Bus
import java.io.IOException

class BubbleMenuService : HoverMenuService()
{
    private lateinit var bubbleMenu: BubbleMenu

    override fun onCreate()
    {
        super.onCreate()
        Bus.instance.register(this)
    }

    override fun onDestroy()
    {
        Bus.instance.unregister(this)
        super.onDestroy()
    }

    override fun getContextForHoverMenu()= ContextThemeWrapper(this, R.style.AppTheme)

    override fun onHoverMenuLaunched(intent: Intent, hoverView: HoverView)
    {
        hoverView.apply {
            setMenu(createHoverMenu())
            collapse()
        }
    }

    fun onEventMainThread(newTheme: BubbleTheme)
    {
        bubbleMenu.theme = newTheme
    }

    private fun createHoverMenu(): HoverMenu?
    {
        try
        {
            bubbleMenu = DemoHoverMenuFactory().createDemoMenuFromCode(contextForHoverMenu, Bus.instance)
            return bubbleMenu
        }
        catch (e: IOException)
        {
            throw RuntimeException(e)
        }
    }

    companion object
    {
        private val TAG = "DemoHoverMenuService"

        fun showFloatingMenu(context: Context)
            = context.startService(Intent(context, BubbleMenuService::class.java))
    }
}