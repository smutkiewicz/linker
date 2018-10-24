package studios.aestheticapps.linker.floatingmenu.content

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.view.LayoutInflater
import android.widget.FrameLayout
import io.mattcarroll.hover.Content
import kotlinx.android.synthetic.main.activity_main.view.*
import studios.aestheticapps.linker.MainActivity
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.floatingmenu.BubbleMenuService

class BrowseItemsBubbleContent(context: Context) : FrameLayout(context), Content
{
    init
    {
        LayoutInflater.from(context).inflate(R.layout.activity_main, this, true)

        fab.setOnClickListener { _ ->
            hideBubbles()
        }
    }

    override fun getView() = this

    override fun isFullscreen() = true

    override fun onShown() {}

    override fun onHidden() {}

    private fun hideBubbles()
    {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(context, intent, null)

        BubbleMenuService.destroyFloatingMenu(context)
    }
}