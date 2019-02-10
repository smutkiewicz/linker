package studios.aestheticapps.linker.floatingmenu

import android.app.Application
import android.content.Context
import io.mattcarroll.hover.Content
import studios.aestheticapps.linker.floatingmenu.content.AddEditBubbleContent
import studios.aestheticapps.linker.floatingmenu.content.BubbleContentCallback
import studios.aestheticapps.linker.floatingmenu.content.HomeBubbleContent
import studios.aestheticapps.linker.floatingmenu.content.LibraryBubbleContent
import studios.aestheticapps.linker.floatingmenu.theme.BubbleThemeManager

class BubbleMenuFactory
{
    fun createMenu(context: Context, application: Application, callback: BubbleContentCallback): BubbleMenu
    {
        val menu = LinkedHashMap<String, Content>()
        menu[BubbleMenu.HOME_TAB] = HomeBubbleContent(context, application, callback)
        menu[BubbleMenu.LIBRARY_TAB] = LibraryBubbleContent(context, application, callback)
        menu[BubbleMenu.ADD_TAB] = AddEditBubbleContent(context, application, callback)

        return BubbleMenu(context, MENU_ID, BubbleThemeManager.instance?.bubbleTheme!!, menu)
    }

    private companion object
    {
        const val MENU_ID = "linker2"
    }
}