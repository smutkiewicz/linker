package studios.aestheticapps.linker.floatingmenu

import android.content.Context
import android.content.Intent
import android.view.ContextThemeWrapper
import io.mattcarroll.hover.HoverMenu
import io.mattcarroll.hover.HoverView
import io.mattcarroll.hover.window.HoverMenuService
import studios.aestheticapps.linker.R
import java.io.IOException

class BubbleMenuService : HoverMenuService()
{
    private lateinit var bubbleMenu: BubbleMenu

    override fun getContextForHoverMenu()= ContextThemeWrapper(this, R.style.AppTheme)

    override fun onHoverMenuLaunched(intent: Intent, hoverView: HoverView)
    {
        hoverView.apply {
            setMenu(createHoverMenu())
            collapse()
        }
    }

    private fun createHoverMenu(): HoverMenu?
    {
        try
        {
            bubbleMenu = BubbleMenuFactory().createMenu(contextForHoverMenu)
            return bubbleMenu
        }
        catch (e: IOException)
        {
            throw RuntimeException(e)
        }
    }

    companion object
    {
        private const val TAG = "DemoHoverMenuService"

        fun showFloatingMenu(context: Context)
            = context.startService(Intent(context, BubbleMenuService::class.java))

        fun destroyFloatingMenu(context: Context)
            = context.stopService(Intent(context, BubbleMenuService::class.java))
    }
}