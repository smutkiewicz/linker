package studios.aestheticapps.linker.floatingmenu.content

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import io.mattcarroll.hover.Content
import kotlinx.android.synthetic.main.content_add_edit.view.*
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.adapters.TagAdapter
import studios.aestheticapps.linker.content.addedit.AddEditFragment
import studios.aestheticapps.linker.content.addedit.AddEditPresenter
import studios.aestheticapps.linker.content.addedit.AddEditTaskContract
import studios.aestheticapps.linker.content.categories.CategoriesDialogFragment
import studios.aestheticapps.linker.extensions.disableChildren
import studios.aestheticapps.linker.extensions.enableChildren
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.model.LinkMetadataFormatter
import studios.aestheticapps.linker.model.LinkValidator
import studios.aestheticapps.linker.utils.ClipboardHelper
import studios.aestheticapps.linker.utils.DateTimeHelper
import studios.aestheticapps.linker.utils.PrefsHelper

class AddEditBubbleContent(context: Context,
                           application: Application,
                           private val bubbleContentCallback: BubbleContentCallback) : FrameLayout(context),
    Content,
    AddEditTaskContract.View,
    TextWatcher,
    AdapterView.OnItemSelectedListener,
    LinkMetadataFormatter.BuildModelCallback,
    TagAdapter.OnTagClickedListener,
    CategoriesDialogFragment.CategoriesChangedCallback
{
    override var presenter: AddEditTaskContract.Presenter = AddEditPresenter(this)

    private var mode: Int = AddEditFragment.MODE_ADD

    private lateinit var tagAdapter: TagAdapter
    //private lateinit var callback: AddEditFragment.AddEditCallback
    //private lateinit var updateViewCallback: UpdateViewCallback

    private var clipboardHelper: ClipboardHelper
    private var model: Link? = null

    init
    {
        LayoutInflater.from(context).inflate(R.layout.content_add_edit, this, true)
        clipboardHelper = ClipboardHelper(context.applicationContext)

        //callback = context as AddEditFragment.AddEditCallback
        //updateViewCallback = context as UpdateViewCallback

        presenter.start(application)

        createCategoriesSpinner()
        createTagRecyclerView()

        createFab()
        createTagBtn()
        createButtons()
    }

    override fun onShown()
    {
        when (mode)
        {
            AddEditFragment.MODE_ADD ->
            {
                val latestUrl = PrefsHelper.obtainLatestParsedUrl(context!!)

                if (model == null || (model != null && clipboardHelper.containsNewContent(latestUrl)))
                {
                    // Starting app OR app already started and has content, but there's new content in cliboard.
                    buildSampleModelFromClipboardContent()
                }
                else if (model != null)
                {
                    // Configuration changed and model is not null.
                    createViewFromModel()
                }
            }

            AddEditFragment.MODE_EDIT -> createViewFromModel()
        }

        attachListeners()
    }

    override fun getView() = this

    override fun onHidden()
    {
        addEditLinkTitleEt.clearFocus()
        addEditUrlEt.clearFocus()
    }

    override fun isFullscreen() = true

    override fun obtainModelFromArguments()
    {
        //mode = arguments!!.getInt(AddEditFragment.MODE, AddEditFragment.MODE_ADD)
    }

    override fun createViewFromModel()
    {
        if (model == null)
        {
            buildSampleModelFromClipboardContent()
        }
        else
        {
            mapModelToView(model)
        }
    }

    override fun createFab()
    {
        saveLinkFab.setOnClickListener {
            if (isUserLinkValid())
            {
                when (mode)
                {
                    AddEditFragment.MODE_EDIT ->
                    {
                        activateLoadingView()
                        presenter.updateItem(buildItemFromView())
                    }

                    AddEditFragment.MODE_ADD ->
                    {
                        presenter.launchItemToSaveMetadataFormatting(buildItemFromView())
                        cleanView()
                    }
                }

                ////////
            }

            true
        }
    }

    override fun createTagRecyclerView()
    {
        tagAdapter = TagAdapter(true)
        tagAdapter.elements = mutableListOf()
        tagAdapter.onTagClickedListener = this

        tagRecyclerView.apply {
            adapter = tagAdapter
            layoutManager = StaggeredGridLayoutManager(
                resources.getInteger(R.integer.tags_column_count),
                StaggeredGridLayoutManager.VERTICAL
            )
        }
    }

    override fun createTagBtn()
    {
        addTagBtn.setOnClickListener {
            addTag()
        }
    }

    override fun createCategoriesSpinner()
    {
        categoriesSpinner.adapter = presenter.provideArrayAdapter()
        categoriesSpinner.isSelected = false
    }

    override fun createButtons()
    {
        addCategoryIb.setOnClickListener {
            startCategoriesDialogAction()
        }

        pasteUrlIb.setOnClickListener {
            clipboardHelper.pasteTo(addEditUrlEt)
        }

        pasteDescrIb.setOnClickListener {
            clipboardHelper.pasteTo(addEditDescriptionEt)
        }

        cutUrlIb.setOnClickListener {
            clipboardHelper.cutFrom(addEditUrlEt)
        }

        cutDescrIb.setOnClickListener {
            clipboardHelper.cutFrom(addEditDescriptionEt)
        }
    }

    override fun mapModelToView(model: Link?)
    {
        model?.let {
            addEditLinkTitleEt.setText(it.title)
            addEditUrlEt.setText(it.url)
            addEditDescriptionEt.setText(it.description)
            tagAdapter.elements = it.stringToListOfTags()

            categoriesSpinner
                .setSelection((categoriesSpinner.adapter as ArrayAdapter<String>)
                    .getPosition(it.category))
        }
    }

    override fun setNewModel(modelFetchedAsync: Link?)
    {
        this.model = modelFetchedAsync
        modelFetchedAsync?.let {
            PrefsHelper.setLatestParsedUrl(context!!, modelFetchedAsync.url)
        }
    }

    override fun insertSavedModel(result: Link?)
    {
        result?.let {
            presenter.saveItem(result)
            //updateViewCallback.onUpdateView()
        }
    }

    override fun activateLoadingView()
    {
        addEditProgressBar?.visibility = View.VISIBLE
        saveLinkFab.hide()
        addEditLayout.disableChildren()
    }

    override fun deactivateLoadingView()
    {
        addEditProgressBar?.visibility = View.GONE
        addEditLayout.enableChildren()
        saveLinkFab.show()
    }

    override fun cleanView()
    {
        //callback.returnToMainView()

        addEditLinkTitleEt.text.clear()
        addEditUrlEt.text.clear()
        addEditDescriptionEt.text.clear()
        tagAdapter.elements.clear()
        categoriesSpinner.setSelection(0)
        model = null
    }

    override fun addTag()
    {
        if (newTagEt.text.isNotBlank())
        {
            tagAdapter.addItem(newTagEt.text.toString())
            model?.addTag(newTagEt.text.toString())

            newTagEt.text.clear()
            //callback.onEdited()
        }
        else
        {
            newTagEt.error = context.getString(R.string.add_edit_blank_tag_error)
        }
    }

    /**
     * Only needed fields, Presenter will fill blank parts of the model (such as domain).
     */
    override fun buildItemFromView(): Link
    {
        return Link(
            id = model?.id ?: 0,
            title = addEditLinkTitleEt.text.toString(),
            category = model?.category ?: categoriesSpinner.selectedItem?.toString() ?: "Unknown",
            description = addEditDescriptionEt.text.toString(),
            url = addEditUrlEt.text.toString(),
            imageUrl = model?.imageUrl ?: LinkMetadataFormatter.DEFAULT_IMAGE_URL,
            lastUsed = DateTimeHelper.getCurrentDateTimeStamp(),
            created = model?.created ?: DateTimeHelper.getCurrentDateTimeStamp(),
            isFavorite = model?.isFavorite ?: false,
            tags = model?.tags ?: presenter.tagsToString(tagAdapter.elements),
            domain = model?.domain ?: ""
        )
    }

    override fun buildSampleModelFromClipboardContent()
    {
        val newContent = clipboardHelper.obtainClipboardContent()
        presenter.buildItemFromUrl(newContent, isNetworkAvailable())
    }

    override fun buildSampleModelFromIntentContent(content: String)
    {
        presenter.buildItemFromUrl(content, isNetworkAvailable())
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {} //callback.onEdited()

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, aft: Int) {}

    override fun afterTextChanged(s: Editable) {}

    override fun onItemSelected(parentView: AdapterView<*>, view: View?, position: Int, id: Long)
    {
        val selectedCategory: String = categoriesSpinner.selectedItem.toString()
        model?.category = selectedCategory

        //callback.onEdited()
    }

    override fun onNothingSelected(parentView: AdapterView<*>) {}

    override fun onDeleteTag(tag: String)
    {
        model?.removeTag(tag)
    }

    override fun startCategoriesDialogAction()
    {
        // TODO Categories
        //IntentActionHelper.startCategoriesDialogAction(fragmentManager!!, this)
    }

    override fun updateCategories()
    {
        createCategoriesSpinner()
    }

    private fun isUserLinkValid(): Boolean
    {
        var isValid = true

        // Check fields validity
        if (addEditLinkTitleEt.text.isBlank())
        {
            addEditLinkTitleEt.error = context.getString(R.string.title_error)
            isValid = false
        }

        // Check URL validity on view level and provide valid url on logical level
        val url = addEditUrlEt.text.toString()
        val validLink = presenter.provideValidUrl(url)

        if (url.isBlank() || validLink == LinkValidator.EMPTY_URL)
        {
            addEditUrlEt.error = context.getString(R.string.url_error)
            isValid = false
        }
        else
        {
            addEditUrlEt.setText(validLink)
        }

        return isValid
    }

    private fun attachListeners()
    {
        categoriesSpinner.onItemSelectedListener = this
        addEditLinkTitleEt.addTextChangedListener(this)
        addEditUrlEt.addTextChangedListener(this)
        addEditDescriptionEt.addTextChangedListener(this)
    }

    private fun isNetworkAvailable(): Boolean
    {
        val connectivityManager = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null
    }
}
