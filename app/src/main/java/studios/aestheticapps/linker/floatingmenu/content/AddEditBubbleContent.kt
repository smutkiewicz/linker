package studios.aestheticapps.linker.floatingmenu.content

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.widget.FrameLayout
import io.mattcarroll.hover.Content
import kotlinx.android.synthetic.main.add_edit_content.view.*
import studios.aestheticapps.linker.MainActivity
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.addedit.AddEditPresenter
import studios.aestheticapps.linker.addedit.AddEditTaskContract
import studios.aestheticapps.linker.floatingmenu.BubbleMenuService

class AddEditBubbleContent(context: Context,
                           private val callback: BubbleContentCallback) : FrameLayout(context), Content, AddEditTaskContract.View
{
    override var presenter: AddEditTaskContract.Presenter = AddEditPresenter(this)

    init
    {
        LayoutInflater.from(context).inflate(R.layout.add_edit_content, this, true)

        createFab()
    }

    override fun getView() = this

    override fun isFullscreen() = true

    override fun onShown() {}

    override fun onHidden() {}

    override fun createFab()
    {
        saveLinkFab.setOnClickListener {
            callback.collapseBubble()
            true
        }
    }

    override fun hideBubbles()
    {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        ContextCompat.startActivity(context, intent, null)

        BubbleMenuService.destroyFloatingMenu(context)
    }
}