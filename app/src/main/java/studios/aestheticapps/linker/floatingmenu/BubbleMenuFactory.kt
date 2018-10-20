package studios.aestheticapps.linker.floatingmenu

import android.content.Context
import io.mattcarroll.hover.Content
import studios.aestheticapps.linker.floatingmenu.content.AddEditBubbleContent
import studios.aestheticapps.linker.floatingmenu.theme.BubbleThemeManager

class BubbleMenuFactory
{
    fun createMenu(context: Context): BubbleMenu
    {
        val demoMenu = LinkedHashMap<String, Content>()
        demoMenu[BubbleMenu.ADD_TAB] = AddEditBubbleContent(context)

        return BubbleMenu(context, "linker", BubbleThemeManager.instance?.theme!!, demoMenu)
    }
}