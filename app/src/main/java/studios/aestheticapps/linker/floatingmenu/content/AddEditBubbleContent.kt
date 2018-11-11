package studios.aestheticapps.linker.floatingmenu.content

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.widget.FrameLayout
import io.mattcarroll.hover.Content
import kotlinx.android.synthetic.main.content_add_edit.view.*
import studios.aestheticapps.linker.MainActivity
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.content.addedit.AddEditPresenter
import studios.aestheticapps.linker.content.addedit.AddEditTaskContract
import studios.aestheticapps.linker.floatingmenu.BubbleMenuService
import studios.aestheticapps.linker.model.Link

class AddEditBubbleContent(context: Context,
                           private val callback: BubbleContentCallback) : FrameLayout(context), Content, AddEditTaskContract.View
{
    override fun obtainModelAndMode()
    {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createTagRecyclerView()
    {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override var presenter: AddEditTaskContract.Presenter = AddEditPresenter(this)

    init
    {
        LayoutInflater.from(context).inflate(R.layout.content_add_edit, this, true)

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

    override fun cleanView()
    {

    }

    override fun createTagBtn()
    {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addTag()
    {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun buildItem(): Link
    {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createViewFromModel()
    {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun hideBubbles()
    {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        ContextCompat.startActivity(context, intent, null)

        BubbleMenuService.destroyFloatingMenu(context)
    }
}