package studios.aestheticapps.linker.content.details

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import android.widget.TextView
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.adapters.OnItemClickListener
import studios.aestheticapps.linker.adapters.TagAdapter
import studios.aestheticapps.linker.content.IntentActionHelper
import studios.aestheticapps.linker.model.Link

class DetailsDialogFragment : DialogFragment(), DetailsContract.View
{
    override var presenter: DetailsContract.Presenter = DetailsPresenter(this)

    private lateinit var callback: OnItemClickListener
    private lateinit var tagAdapter: TagAdapter
    private lateinit var model: Link

    override fun onCreateDialog(bundle: Bundle?): Dialog
    {
        val builder = AlertDialog.Builder(context!!)
        val view = inflateAndReturnDetailsView()
        builder.setView(view)

        createViewFromModel(view)
        createTagRecyclerView(view)
        createFab(view)

        populateViewAdaptersWithContent()

        return builder.create()
    }

    override fun onStart()
    {
        super.onStart()
        presenter.start(activity!!.application)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        restoreSavedState(savedInstanceState)
    }

    override fun onAttach(context: Context)
    {
        super.onAttach(context)
        callback = presenter as OnItemClickListener
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)
        outState.putParcelable(Link.PARCEL_LINK, model)
    }

    override fun createViewFromModel(view: View)
    {
        model = arguments!!.getParcelable(Link.PARCEL_LINK)

        view.apply {
            findViewById<TextView>(R.id.detailsTitleTv).text = model.title
            findViewById<TextView>(R.id.detailsCategoryTv).text = model.category
            findViewById<TextView>(R.id.detailsUrlTv).text = model.url
            findViewById<TextView>(R.id.detailsDescrTv).text = model.description
            findViewById<CardView>(R.id.detailsGoToUrlCv).setOnClickListener {
                callback.onItemClicked(model)
            }
        }
    }

    override fun createTagRecyclerView(view: View)
    {
        tagAdapter = TagAdapter(false)

        val rv = view.findViewById<RecyclerView>(R.id.detailsTagRv)
        rv.apply {
            adapter = tagAdapter
            isNestedScrollingEnabled = true
            layoutManager = StaggeredGridLayoutManager(
                resources.getInteger(R.integer.tags_details_column_count),
                StaggeredGridLayoutManager.VERTICAL
            )
        }
    }

    override fun createFab(view: View)
    {
        val fab = view.findViewById<FloatingActionButton>(R.id.shareFab)
        fab.setOnClickListener{
            callback.onShare(model)
        }
    }

    override fun populateViewAdaptersWithContent()
    {
        tagAdapter.elements = model.stringToListOfTags()
    }

    override fun startInternetAction(link: Link) = IntentActionHelper.startInternetAction(context!!, link)

    override fun startDetailsAction(link: Link) = IntentActionHelper.startDetailsAction(fragmentManager!!, link)

    override fun startShareView(link: Link) = IntentActionHelper.startShareView(context!!, link)

    private fun inflateAndReturnDetailsView()
        = activity!!.layoutInflater.inflate(R.layout.content_details, null)

    private fun restoreSavedState(state: Bundle?)
    {
        if (state != null)
            model = state.getParcelable(Link.PARCEL_LINK)
    }
}