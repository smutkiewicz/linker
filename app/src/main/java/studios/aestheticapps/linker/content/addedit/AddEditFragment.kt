package studios.aestheticapps.linker.content.addedit

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.StaggeredGridLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.content_add_edit.*
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.adapters.TagAdapter
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.model.Link.CREATOR.PARCEL_LINK
import studios.aestheticapps.linker.model.LinkMetadataFormatter
import studios.aestheticapps.linker.model.LinkValidator.Companion.EMPTY_URL
import studios.aestheticapps.linker.utils.ClipboardHelper
import studios.aestheticapps.linker.utils.DateTimeHelper

class AddEditFragment : Fragment(),
    AddEditTaskContract.View,
    TextWatcher,
    OnItemSelectedListener,
    LinkMetadataFormatter.BuildModelCallback
{
    override var presenter: AddEditTaskContract.Presenter = AddEditPresenter(this)

    private var mode: Int = MODE_ADD

    private lateinit var tagAdapter: TagAdapter
    private lateinit var callback: AddEditCallback
    private lateinit var clipboardHelper: ClipboardHelper

    private var model: Link? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        val view = inflater.inflate(R.layout.content_add_edit, container, false)

        clipboardHelper = ClipboardHelper(context!!)
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
        createViewFromModel()

        createFab()
        createTagBtn()
        createEditTexts()
    }

    override fun onResume()
    {
        super.onResume()

        when (mode)
        {
            MODE_ADD ->
            {
                if (model == null || (model != null && clipboardHelper.containsNewContent(model!!.url)))
                {
                    buildSampleModelFromClipboardContent()
                }
            }
        }
    }

    override fun onAttach(context: Context?)
    {
        super.onAttach(context)
        callback = context as AddEditCallback
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
        {
            model = arguments!!.getParcelable(PARCEL_LINK)
        }
    }

    override fun createViewFromModel()
    {
        if (model == null)
        {
            buildSampleModelFromClipboardContent()
        }

        mapModelToView()
    }

    override fun createFab()
    {
        saveLinkFab.setOnClickListener {
            if (isUserLinkValid())
            {
                when (mode)
                {
                    MODE_EDIT -> presenter.updateItem(buildItemFromView())

                    MODE_ADD ->
                    {
                        presenter.saveItem(buildItemFromView())
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
        val adapter = ArrayAdapter.createFromResource(
            context,
            R.array.categories,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    override fun createEditTexts()
    {
        addEditLinkTitleEt.addTextChangedListener(this)
        addEditUrlEt.addTextChangedListener(this)
        addEditDescriptionEt.addTextChangedListener(this)
    }

    override fun createSpinner()
    {
        spinner.onItemSelectedListener = this
    }

    override fun mapModelToView()
    {
        model?.let {
            addEditLinkTitleEt.setText(it.title)
            addEditUrlEt.setText(it.url)
            addEditDescriptionEt.setText(it.description)
            tagAdapter.elements = it.stringToListOfTags()

            spinner.setSelection((spinner.adapter as ArrayAdapter<String>)
                .getPosition(it.category))
        }
    }

    override fun mapModelToView(model: Link?)
    {
        model?.let {
            addEditLinkTitleEt.setText(it.title)
            addEditUrlEt.setText(it.url)
            addEditDescriptionEt.setText(it.description)
            tagAdapter.elements = it.stringToListOfTags()

            spinner
                .setSelection((spinner.adapter as ArrayAdapter<String>)
                .getPosition(it.category))
        }
    }

    override fun setNewModel(modelFetchedAsync: Link?)
    {
        this.model = modelFetchedAsync
    }

    override fun cleanView()
    {
        callback.returnToMainView()

        addEditLinkTitleEt.text.clear()
        addEditUrlEt.text.clear()
        addEditDescriptionEt.text.clear()
        tagAdapter.elements.clear()
        spinner.setSelection(0)
    }

    override fun addTag()
    {
        if (newTagEt.text.isNotBlank())
        {
            tagAdapter.addItem(newTagEt.text.toString())
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
    override fun buildItemFromView() = Link(
        id = model?.id ?: 0,
        title = addEditLinkTitleEt.text.toString(),
        category = spinner.selectedItem?.toString() ?: "Unknown",
        description = addEditDescriptionEt.text.toString(),
        url = addEditUrlEt.text.toString(),
        lastUsed = DateTimeHelper.getCurrentDateTimeStamp(),
        isFavorite = model?.isFavorite ?: false,
        tags = presenter.tagsToString(tagAdapter.elements),
        domain = model?.domain ?: ""
    )

    override fun buildSampleModelFromClipboardContent()
    {
        val newContent = clipboardHelper.obtainClipboardContent()
        presenter.buildItemFromUrl(newContent)
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = callback.onEdited()

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, aft: Int) {}

    override fun afterTextChanged(s: Editable) {}

    override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long) = callback.onEdited()

    override fun onNothingSelected(parentView: AdapterView<*>) {}

    private fun restoreSavedState(state: Bundle?)
    {
        if (state != null)
        {
            val savedModel = state.getParcelable<Link>(PARCEL_LINK)
            mode = state.getInt(MODE)

            when (mode)
            {
                MODE_EDIT -> model = savedModel

                MODE_ADD ->
                {
                    if (clipboardHelper.containsNewContent(savedModel.url))
                        buildSampleModelFromClipboardContent()
                    else
                        model = savedModel
                }
            }
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

    interface AddEditCallback
    {
        fun returnToMainView() {} // not necessary for some Views
        fun onEdited() {} // not necessary for some Views
    }

    companion object
    {
        private const val MODE = "mode"
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