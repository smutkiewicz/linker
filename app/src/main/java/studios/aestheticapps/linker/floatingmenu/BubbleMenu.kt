package studios.aestheticapps.linker.floatingmenu

import android.content.Context
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.util.TypedValue
import android.view.View
import io.mattcarroll.hover.Content
import io.mattcarroll.hover.HoverMenu
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.floatingmenu.theme.BubbleTheme
import studios.aestheticapps.linker.floatingmenu.ui.BubbleTabView

class BubbleMenu(private val context: Context,
                 private val menuId: String,
                 var theme: BubbleTheme,
                 data: Map<String, Content>) : HoverMenu()
{
    private val sections = ArrayList<Section>()

    init
    {
        for (tabId in data.keys)
        {
            sections.add(
                HoverMenu.Section(
                    HoverMenu.SectionId(tabId),
                    createTabView(tabId),
                    data[tabId]!!
                )
            )
        }
    }

    private fun createTabView(sectionId: String): View
    {
        return when(sectionId)
        {
            ADD_TAB -> createTabView(R.drawable.ic_delete, theme.accentColor, theme.baseColor)
            BROWSE_ITEMS_TAB -> createTabView(R.drawable.ic_arrow_back, theme.accentColor, theme.baseColor)
            else -> throw RuntimeException("Unknown tab selected: $sectionId")
        }
    }

    private fun createTabView(@DrawableRes tabBitmapRes: Int, @ColorInt backgroundColor: Int, @ColorInt iconColor: Int): View
    {
        val resources = context.resources
        val elevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics).toFloat()

        val view = BubbleTabView(context, resources.getDrawable(R.drawable.tab_background), resources.getDrawable(tabBitmapRes))
        view.apply {
            setTabBackgroundColor(backgroundColor)
            setTabForegroundColor(iconColor)
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            view.elevation = elevation
        }
        
        return view
    }

    override fun getId() = menuId

    override fun getSectionCount() = sections.size

    override fun getSection(index: Int) = sections[index]

    override fun getSection(sectionId: HoverMenu.SectionId): HoverMenu.Section?
    {
        sections.forEach { section ->
            if (section.id == sectionId) return section
        }

        return null
    }

    override fun getSections() = ArrayList(sections)

    companion object
    {
        const val ADD_TAB = "add_tab"
        const val BROWSE_ITEMS_TAB = "browse_items_tab"
    }
}