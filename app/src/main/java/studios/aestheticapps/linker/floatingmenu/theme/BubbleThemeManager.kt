package studios.aestheticapps.linker.floatingmenu.theme

/**
 * Global entry point for Hover menu theming.
 */
class BubbleThemeManager private constructor(var bubbleTheme: BubbleTheme)
{
    companion object
    {
        var instance: BubbleThemeManager? = null

        @Synchronized
        fun obtainInstance(): BubbleThemeManager?
        {
            if (null == instance)
            {
                throw RuntimeException("Cannot obtain HoverThemeManager before calling init().")
            }

            return instance
        }

        @Synchronized
        fun init(theme: BubbleTheme)
        {
            if (instance == null)
            {
                instance = BubbleThemeManager(theme)
            }
        }
    }
}