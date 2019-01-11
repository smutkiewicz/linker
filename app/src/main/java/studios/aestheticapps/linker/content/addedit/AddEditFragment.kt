package studios.aestheticapps.linker.content.addedit

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.StaggeredGridLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.content_add_edit.*
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.adapters.TagAdapter
import studios.aestheticapps.linker.content.IntentActionHelper
import studios.aestheticapps.linker.content.UpdateViewCallback
import studios.aestheticapps.linker.content.categories.CategoriesChangedCallback
import studios.aestheticapps.linker.extensions.disableChildrenOf
import studios.aestheticapps.linker.extensions.enableChildrenOf
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.model.Link.CREATOR.PARCEL_LINK
import studios.aestheticapps.linker.utils.ClipboardHelper
import studios.aestheticapps.linker.utils.DateTimeHelper
import studios.aestheticapps.linker.utils.PrefsHelper
import studios.aestheticapps.linker.validation.LinkMetadataFormatter
import studios.aestheticapps.linker.validation.LinkValidator.Companion.EMPTY_URL

class AddEditFragment : Fragment(), AddEditTaskContract.View,
    TextWatcher, OnItemSelectedListener,
    LinkMetadataFormatter.BuildModelCallback, TagAdapter.OnTagClickedListener,
    CategoriesChangedCallback
{
    override var presenter: AddEditTaskContract.Presenter = AddEditPresenter(this)

    private var mode: Int = MODE_ADD

    private lateinit var tagAdapter: TagAdapter
    private lateinit var callback: AddEditCallback
    private lateinit var updateViewCallback: UpdateViewCallback
    private lateinit var clipboardHelper: ClipboardHelper

    private var model: Link? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        val view = inflater.inflate(R.layout.content_add_edit, container, false)

        clipboardHelper = ClipboardHelper(context!!.applicationContext)
        restoreSavedState(savedInstanceState)

        return view
    }

    override fun onDestroyView()
    {
        addEditLinkTitleEt.clearFocus()
        addEditUrlEt.clearFocus()
        super.onDestroyView()
    }

    override fun onStart()
    {
        super.onStart()
        presenter.start(activity!!.application)

        createCategoriesSpinner()
        createTagRecyclerView()

        createFab()
        createTagBtn()
        createButtons()
    }

    override fun onResume()
    {
        super.onResume()

        when (mode)
        {
            MODE_ADD ->
            {
                val latestUrl = PrefsHelper.obtainLatestParsedUrl(context!!)

                if (checkIfIntentUrlBuildIsNeeded(latestUrl))
                {
                    // Starting app with intent url
                    val content = arguments!!.getString(Link.INTENT_LINK, "")
                    buildSampleModelFromIntentContent(content)
                }
                else if (model == null || (model != null && clipboardHelper.containsNewContent(latestUrl)))
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

            MODE_EDIT -> createViewFromModel()
        }

        attachListeners()
    }

    private fun checkIfIntentUrlBuildIsNeeded(latestUrl: String): Boolean
    {
        return if (arguments?.containsKey(Link.INTENT_LINK) == true)
        {
            val content = arguments!!.getString(Link.INTENT_LINK, "")
            content != latestUrl && content != EMPTY_URL
        }
        else false
    }

    override fun onAttach(context: Context?)
    {
        super.onAttach(context)
        callback = context as AddEditCallback
        updateViewCallback = context as UpdateViewCallback
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)
        outState.apply {
            putInt(MODE, mode)
            putParcelable(PARCEL_LINK, buildItemFromView())
        }
    }

    override fun obtainModelFromArguments()
    {
        mode = arguments!!.getInt(MODE, MODE_ADD)

        if (mode == MODE_EDIT)
            model = arguments!!.getParcelable(PARCEL_LINK)
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
                    MODE_EDIT ->
                    {
                        activateLoadingView()
                        presenter.updateItem(buildItemFromView())
                    }

                    MODE_ADD ->
                    {
                        presenter.launchItemToSaveMetadataFormatting(buildItemFromView())
                        cleanView()
                    }
                }

                callback.returnToMainView()
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
            updateViewCallback.onUpdateView()
        }
    }

    override fun activateLoadingView()
    {
        addEditProgressBar?.visibility = VISIBLE
        saveLinkFab.hide()
        disableChildrenOf(addEditLayout)
    }

    override fun deactivateLoadingView()
    {
        addEditProgressBar?.visibility = GONE
        enableChildrenOf(addEditLayout)
        saveLinkFab.show()
    }

    override fun cleanView()
    {
        callback.returnToMainView()

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
            callback.onEdited()
        }
        else
        {
            newTagEt.error = getString(R.string.add_edit_blank_tag_error)
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
        // clear arguments from Intent content
        arguments!!.clear()
        arguments!!.putInt(MODE, mode)

        presenter.buildItemFromUrl(content, isNetworkAvailable())
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = callback.onEdited()

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, aft: Int) {}

    override fun afterTextChanged(s: Editable) {}

    override fun onItemSelected(parentView: AdapterView<*>, view: View?, position: Int, id: Long)
    {
        val selectedCategory: String = categoriesSpinner.selectedItem.toString()
        model?.category = selectedCategory

        callback.onEdited()
    }

    override fun onNothingSelected(parentView: AdapterView<*>) {}

    override fun onDeleteTag(tag: String)
    {
        model?.removeTag(tag)
    }

    override fun startCategoriesDialogAction() = IntentActionHelper.startCategoriesDialogAction(fragmentManager!!, this)

    override fun updateCategories()
    {
        createCategoriesSpinner()
    }

    private fun restoreSavedState(state: Bundle?)
    {
        if (state != null)
        {
            val savedModel = state.getParcelable<Link>(PARCEL_LINK)
            mode = state.getInt(MODE)
            model = savedModel
        }
        else
        {
            obtainModelFromArguments()
        }
    }

    private fun isUserLinkValid(): Boolean
    {
        var isValid = true

        // Check fields validity
        if (addEditLinkTitleEt.text.isBlank())
        {
            addEditLinkTitleEt.error = getString(R.string.title_error)
            isValid = false
        }

        // Check URL validity on view level and provide valid url on logical level
        val url = addEditUrlEt.text.toString()
        val validLink = presenter.provideValidUrl(url)

        if (url.isBlank() || validLink == EMPTY_URL)
        {
            addEditUrlEt.error = getString(R.string.url_error)
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

    interface AddEditCallback
    {
        fun returnToMainView() {} // not necessary for some Views
        fun onEdited() {} // not necessary for some Views
    }

    companion object
    {
        const val MODE = "mode"
        const val MODE_ADD = 0
        const val MODE_EDIT = 1

        fun newInstance(mode: Int): AddEditFragment
        {
            val fragment = AddEditFragment()
            val args = Bundle()

            args.putInt(MODE, mode)
            fragment.arguments = args

            return fragment
        }
    }
}