package studios.aestheticapps.linker.content.addedit

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.content_add_edit.*
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.adapters.TagAdapter
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.model.Link.CREATOR.PARCEL_LINK

class AddEditFragment : Fragment(), AddEditTaskContract.View
{
    override var presenter: AddEditTaskContract.Presenter = AddEditPresenter(this)
    private var mode: Int = MODE_ADD

    private lateinit var callback: AddEditCallback
    private lateinit var tagAdapter: TagAdapter
    private var model: Link? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        val view = inflater.inflate(R.layout.content_add_edit, container, false)
        restoreSavedState(savedInstanceState)
        return view
    }

    override fun onStart()
    {
        super.onStart()
        presenter.start(activity!!.application)

        obtainInfoFromArguments()

        createFab()
        createTagBtn()
        createCategoriesSpinner()
        createTagRecyclerView()
        createViewFromModel()
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)
        outState.putInt(MODE, mode)
        outState.putParcelable(PARCEL_LINK, buildItem())
    }

    override fun onDestroyView()
    {
        addEditLinkTitleEt.clearFocus()
        addEditUrlEt.clearFocus()
        super.onDestroyView()
    }

    override fun onAttach(context: Context)
    {
        super.onAttach(context)
        callback = context as AddEditCallback
    }

    override fun obtainInfoFromArguments()
    {
        mode = arguments!!.getInt(MODE, MODE_ADD)

        if (mode == MODE_EDIT)
        {
            model = arguments!!.getParcelable(PARCEL_LINK)
        }
    }

    override fun createViewFromModel()
    {
        when (mode)
        {
            MODE_EDIT -> callback.prepareEditView()
            MODE_ADD -> callback.prepareAddView()
        }

        if (model != null)
            mapModelToView()
        else
            buildSampleItemFromClipboardContent()
    }

    override fun createFab()
    {
        saveLinkFab.setOnClickListener {
            if (isLinkValid())
            {
                when (mode)
                {
                    MODE_EDIT -> presenter.updateItem(buildItem())
                    MODE_ADD -> presenter.saveItem(buildItem())
                }

                cleanView()
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
        addTagBtn.setOnClickListener{
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

    override fun mapModelToView()
    {
        addEditLinkTitleEt.setText(model!!.title)
        addEditUrlEt.setText(model!!.url)
        addEditDescriptionEt.setText(model!!.description)
        tagAdapter.elements = model!!.stringToListOfTags()

        spinner.setSelection((spinner.adapter as ArrayAdapter<String>)
            .getPosition(model!!.category))
    }

    override fun cleanView()
    {
        val args = Bundle()
        args.putInt(MODE, MODE_ADD)
        arguments = args

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
        }
        else
        {
            newTagEt.error = getString(R.string.add_edit_blank_tag_error)
        }
    }

    override fun buildItem() = Link(
        id = model?.id ?: 0,
        title = addEditLinkTitleEt.text.toString(),
        category = spinner.selectedItem.toString(),
        url = addEditUrlEt.text.toString(),
        domain = presenter.parseDomain(addEditUrlEt.text.toString()),
        description = addEditDescriptionEt.text.toString(),
        lastUsed = presenter.getCurrentDateTimeStamp(),
        tags = presenter.tagsToString(tagAdapter.elements)
    )

    override fun buildSampleItemFromClipboardContent()
    {
        val clipboard = context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        //TODO build item in presenter
        addEditUrlEt.setText(clipboard.text)
    }

    private fun restoreSavedState(state: Bundle?)
    {
        if (state != null)
        {
            mode = state.getInt(MODE)
            model = state.getParcelable(PARCEL_LINK)
        }
    }

    private fun isLinkValid(): Boolean
    {
        var isValid = true

        if (addEditLinkTitleEt.text.isBlank())
        {
            addEditLinkTitleEt.error = getString(R.string.title_error)
            isValid = false
        }

        if (addEditUrlEt.text.isBlank())
        {
            addEditUrlEt.error = getString(R.string.url_error)
            isValid = false
        }

        return isValid
    }

    interface AddEditCallback
    {
        fun returnToMainView()
        fun prepareEditView()
        fun prepareAddView()
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