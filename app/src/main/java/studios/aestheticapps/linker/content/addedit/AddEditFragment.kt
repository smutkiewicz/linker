package studios.aestheticapps.linker.content.addedit

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.content_add_edit.*
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.adapters.TagAdapter
import studios.aestheticapps.linker.model.Link

class AddEditFragment : Fragment(), AddEditTaskContract.View
{
    override var presenter: AddEditTaskContract.Presenter = AddEditPresenter(this)
    private var mode: Int = MODE_ADD

    private lateinit var callback: AddEditCallback
    private lateinit var tagAdapter: TagAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
        = inflater.inflate(R.layout.content_add_edit, container, false)

    override fun onStart()
    {
        super.onStart()
        presenter.start(activity!!.application)

        createFab()
        createTagBtn()
        createTagRecyclerView()
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

    override fun createFab()
    {
        saveLinkFab.setOnClickListener {
            if (isLinkValid())
            {
                presenter.saveItem(buildItem())
                cleanView()
                callback.returnToMainView()
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

    override fun buildItem() = Link(
        title = addEditLinkTitleEt.text.toString(),
        url = addEditUrlEt.text.toString(),
        domain = presenter.parseDomain(addEditUrlEt.text.toString()),
        description = addEditDescriptionEt.text.toString(),
        tags = presenter.tagsToString(tagAdapter.elements)
    )

    private fun createTagRecyclerView()
    {
        tagAdapter = TagAdapter()
        tagAdapter.elements = mutableListOf()

        tagRecyclerView.adapter = tagAdapter
        tagRecyclerView.layoutManager = StaggeredGridLayoutManager(
            resources.getInteger(R.integer.tags_column_count),
            StaggeredGridLayoutManager.VERTICAL
        )
    }

    private fun isLinkValid(): Boolean
    {
        var isValid = true

        if (addEditLinkTitleEt.text.isBlank())
        {
            addEditLinkTitleEt.error = "You need to enter the title."
            isValid = false
        }

        if (addEditUrlEt.text.isBlank())
        {
            addEditUrlEt.error = "You need to enter a valid URL."
            isValid = false
        }

        return isValid
    }

    interface AddEditCallback
    {
        fun returnToMainView()
    }

    companion object
    {
        const val MODE_ADD = 0
        const val MODE_EDIT = 1

        fun newInstance(mode: Int): AddEditFragment
        {
            val fragment = AddEditFragment()
            val args = Bundle()

            args.putInt("mode", mode)
            fragment.arguments = args

            return fragment
        }
    }
}