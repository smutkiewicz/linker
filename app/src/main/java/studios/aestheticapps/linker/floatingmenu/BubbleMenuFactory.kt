package studios.aestheticapps.linker.floatingmenu

import android.content.Context
import io.mattcarroll.hover.Content
import org.greenrobot.eventbus.EventBus
import studios.aestheticapps.linker.floatingmenu.theme.BubbleThemeManager

class DemoHoverMenuFactory
{
    fun createDemoMenuFromCode(context: Context, bus: EventBus): BubbleMenu
    {
        val demoMenu = LinkedHashMap<String, Content>()
        /*demoMenu[BubbleMenu.BROWSE_ITEMS_TAB] = BrowseItemsTabContent(context, Bus.getInstance())
        demoMenu[BubbleMenu.ADD_TAB] = AddTabContent(
            context,
            Bus.getInstance(),
            HoverThemeManager.getInstance(),
            HoverThemeManager.getInstance().getTheme()
        )*/

        return BubbleMenu(context, "linker", BubbleThemeManager.instance?.theme!!, demoMenu)
    }
}