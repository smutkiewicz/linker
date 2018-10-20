package studios.aestheticapps.linker.floatingmenu.theme

import org.greenrobot.eventbus.EventBus

/**
 * Global entry point for Hover menu theming.
 */
class BubbleThemeManager private constructor(bubbleTheme: BubbleTheme)
{
    var theme: BubbleTheme = bubbleTheme

    companion object
    {
        var instance: BubbleThemeManager? = null
            @Synchronized get()
            {
                if (field == null) throw RuntimeException("Cannot obtain HoverThemeManager before calling init().")

                return field
            }

        @Synchronized
        fun init(eventBus: EventBus, theme: BubbleTheme)
        {
            if (instance == null)
            {
                instance = BubbleThemeManager(theme)
            }
        }
    }
}