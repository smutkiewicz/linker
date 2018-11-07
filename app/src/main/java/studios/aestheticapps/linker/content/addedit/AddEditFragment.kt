package studios.aestheticapps.linker.content.addedit

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.content_add_edit.*
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.model.Link

class AddEditFragment : Fragment(), AddEditTaskContract.View
{
    override var presenter: AddEditTaskContract.Presenter = AddEditPresenter(this)
    private lateinit var callback: AddEditCallback

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
        = inflater.inflate(R.layout.content_add_edit, container, false)

    override fun onStart()
    {
        super.onStart()
        presenter.start(activity!!.application)
        createFab()
    }

    override fun onAttach(context: Context)
    {
        super.onAttach(context)
        callback = context as AddEditCallback
    }

    override fun createFab()
    {
        saveLinkFab.setOnClickListener {
            presenter.saveItem(buildItem())
            cleanView()
            callback.returnToMainView()
            true
        }
    }

    override fun cleanView()
    {
        addEditLinkTitleEt.text.clear()
        addEditUrlEt.text.clear()
        addEditDescriptionEt.text.clear()
    }

    private fun buildItem() = Link(
        title = addEditLinkTitleEt.text.toString(),
        url = addEditUrlEt.text.toString(),
        domain = presenter.parseDomain(addEditUrlEt.text.toString()),
        description = addEditDescriptionEt.text.toString()
    )

    interface AddEditCallback
    {
        fun returnToMainView()
    }
}