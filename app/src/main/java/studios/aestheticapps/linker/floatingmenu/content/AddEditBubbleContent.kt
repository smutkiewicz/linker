package studios.aestheticapps.linker.floatingmenu.content

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import io.mattcarroll.hover.Content
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.floatingmenu.ui.BubbleMotion

class AddEditBubbleContent(context: Context) : FrameLayout(context), Content
{
    private var bubbleMotion: BubbleMotion

    init
    {
        LayoutInflater.from(context).inflate(R.layout.content_main, this, true)
        bubbleMotion = BubbleMotion()
    }

    override fun getView() = this

    override fun isFullscreen() = true

    override fun onShown() {}

    override fun onHidden() {}
}