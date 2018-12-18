package studios.aestheticapps.linker.floatingmenu.content

import android.app.Application
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.widget.FrameLayout
import io.mattcarroll.hover.Content
import kotlinx.android.synthetic.main.content_add_edit.view.*
import studios.aestheticapps.linker.MainActivity
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.adapters.TagAdapter
import studios.aestheticapps.linker.content.addedit.AddEditPresenter
import studios.aestheticapps.linker.content.addedit.AddEditTaskContract
import studios.aestheticapps.linker.floatingmenu.BubbleMenuService
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.model.LinkMetadataFormatter

class AddEditBubbleContent(context: Context,
                            application: Application,
                            private val callback: BubbleContentCallback) : FrameLayout(context),
    Content,
    AddEditTaskContract.View,
    LinkMetadataFormatter.BuildModelCallback
{
    override var presenter: AddEditTaskContract.Presenter = AddEditPresenter(this)

    private lateinit var tagAdapter: TagAdapter

    init
    {
        LayoutInflater.from(context).inflate(R.layout.content_add_edit, this, true)
        presenter.start(application)

        createFab()
        createTagBtn()
        createTagRecyclerView()
        createViewFromModel()
    }

    override fun onShown()
    {
        createViewFromModel()
    }

    override fun getView() = this

    override fun onHidden()
    {
        addEditLinkTitleEt.clearFocus()
        addEditUrlEt.clearFocus()
    }

    override fun isFullscreen() = true

    override fun obtainModelFromArguments() {}

    override fun createViewFromModel()
    {
        getTextFromClipboard()
    }

    override fun createFab()
    {
        saveLinkFab.setOnClickListener {
            if (isLinkValid())
            {
                presenter.saveItem(buildItemFromView())

                cleanView()
                callback.collapseBubble()
            }

            true
        }
    }

    override fun cleanView()
    {
        addEditLinkTitleEt.text.clear()
        addEditUrlEt.text.clear()
        addEditDescriptionEt.text.clear()
    }

    override fun createTagBtn()
    {
        addTagBtn.setOnClickListener{
            addTag()
        }
    }

    override fun addTag()
    {
        if (newTagEt.text.isNotBlank())
        {
            tagAdapter.addItem(newTagEt.text.toString())
            newTagEt.text.clear()
        }
    }

    override fun buildItemFromView() = Link(
        id = 0,
        title = addEditLinkTitleEt.text.toString(),
        url = addEditUrlEt.text.toString(),
        domain = "",
        description = addEditDescriptionEt.text.toString(),
        tags = presenter.tagsToString(tagAdapter.elements)
    )

    override fun createTagRecyclerView()
    {
        tagAdapter = TagAdapter(true)
        tagAdapter.elements = mutableListOf()

        tagRecyclerView.adapter = tagAdapter
        tagRecyclerView.layoutManager = StaggeredGridLayoutManager(
            resources.getInteger(R.integer.tags_column_count),
            StaggeredGridLayoutManager.VERTICAL
        )
    }

    private fun getTextFromClipboard()
    {
        val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        addEditUrlEt.setText(clipboard.text)
    }

    private fun isLinkValid(): Boolean
    {
        var isValid = true

        if (addEditLinkTitleEt.text.isBlank())
        {
            addEditLinkTitleEt.error = resources.getString(R.string.title_error)
            isValid = false
        }

        if (addEditUrlEt.text.isBlank())
        {
            addEditUrlEt.error = resources.getString(R.string.url_error)
            isValid = false
        }

        return isValid
    }

    private fun hideBubbles()
    {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(context, intent, null)

        BubbleMenuService.destroyFloatingMenu(context)
    }

    override fun buildSampleModelFromClipboardContent()
    {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createCategoriesSpinner()
    {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createCopyButtons()
    {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun mapModelToView(model: Link?) {}

    override fun setNewModel(modelFetchedAsync: Link?) {}
}

