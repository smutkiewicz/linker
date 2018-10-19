package studios.aestheticapps.linker.floatingmenu.theme

import org.greenrobot.eventbus.EventBus

/**
 * Global entry point for Hover menu theming.
 */
class BubbleThemeManager
{
    private val bus: EventBus

    var theme: BubbleTheme? = null
        set(theme)
        {
            field = theme
            bus.postSticky(theme)
        }

    private constructor(eventBus: EventBus, bubbleTheme: BubbleTheme)
    {
        bus = eventBus
        theme = bubbleTheme
    }

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
                instance = BubbleThemeManager(eventBus, theme)
            }
        }
    }

}