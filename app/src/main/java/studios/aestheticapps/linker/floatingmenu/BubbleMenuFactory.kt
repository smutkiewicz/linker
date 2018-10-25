package studios.aestheticapps.linker.floatingmenu

import android.content.Context
import io.mattcarroll.hover.Content
import studios.aestheticapps.linker.floatingmenu.content.AddEditBubbleContent
import studios.aestheticapps.linker.floatingmenu.content.BrowseItemsBubbleContent
import studios.aestheticapps.linker.floatingmenu.theme.BubbleThemeManager

class BubbleMenuFactory
{
    fun createMenu(context: Context): BubbleMenu
    {
        val demoMenu = LinkedHashMap<String, Content>()
        demoMenu[BubbleMenu.BROWSE_ITEMS_TAB] = BrowseItemsBubbleContent(context)
        demoMenu[BubbleMenu.ADD_TAB] = AddEditBubbleContent(context)

        return BubbleMenu(context, MENU_ID, BubbleThemeManager.instance?.bubbleTheme!!, demoMenu)
    }

    private companion object
    {
        const val MENU_ID = "linker"
    }
}