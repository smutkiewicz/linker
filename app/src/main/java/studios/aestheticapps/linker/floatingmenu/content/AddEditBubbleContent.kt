package studios.aestheticapps.linker.floatingmenu.content

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import io.mattcarroll.hover.Content
import studios.aestheticapps.linker.R

class AddEditBubbleContent(context: Context) : FrameLayout(context), Content
{
    init
    {
        LayoutInflater.from(context).inflate(R.layout.content_main, this, true)
    }

    override fun getView() = this

    override fun isFullscreen() = true

    override fun onShown() {}

    override fun onHidden() {}
}