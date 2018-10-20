package studios.aestheticapps.linker.floatingmenu.content

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import io.mattcarroll.hover.Content
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.floatingmenu.theme.BubbleTheme
import studios.aestheticapps.linker.floatingmenu.ui.BubbleMotion

//TODO add real content
class BrowseItemsBubbleContent(context: Context) : FrameLayout(context), Content
{
    private var logo: View
    private var bubbleMotion: BubbleMotion

    init
    {
        LayoutInflater.from(context).inflate(R.layout.activity_main, this, true)

        logo = findViewById(R.id.action_bar_title)
        bubbleMotion = BubbleMotion()
    }

    override fun getView() = this

    override fun isFullscreen() = true

    override fun onShown() = bubbleMotion.start(logo)

    override fun onHidden() = bubbleMotion.stop()

    fun onEventMainThread(newTheme: BubbleTheme)
    {
        /*mHoverTitleTextView!!.setTextColor(newTheme.getAccentColor())
        mGoalsTitleTextView!!.setTextColor(newTheme.getAccentColor())*/
    }
}