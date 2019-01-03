package studios.aestheticapps.linker.floatingmenu.content
/*
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import io.mattcarroll.hover.Content
import studios.aestheticapps.linker.MainActivity
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.adapters.OnMyAdapterItemClickListener
import studios.aestheticapps.linker.adapters.TagAdapter
import studios.aestheticapps.linker.content.IntentActionHelper
import studios.aestheticapps.linker.content.SearchCallback
import studios.aestheticapps.linker.content.details.DetailsContract
import studios.aestheticapps.linker.content.details.DetailsPresenter
import studios.aestheticapps.linker.floatingmenu.BubbleMenuService
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.model.LinkMetadataFormatter
import studios.aestheticapps.linker.utils.ClipboardHelper

class DetailsBubbleContent(context: Context,
                           application: Application,
                           private val callback: BubbleContentCallback) : FrameLayout(context),
    Content,
    DetailsContract.View,
    TagAdapter.OnTagClickedListener
{
    override var presenter: DetailsContract.Presenter = DetailsPresenter(this)

    private lateinit var onItemClickCallback: OnMyAdapterItemClickListener
    private lateinit var searchCallback: SearchCallback

    private lateinit var tagAdapter: TagAdapter
    private lateinit var model: Link

    init
    {
        LayoutInflater.from(context).inflate(R.layout.content_details, this, true)
        presenter.start(application)
        presenter.attachDataObserver(this)

        setUpRecentRecyclerView()
        setUpFavoritesRecyclerView()
        setUpTagsCloudRecyclerView()
        setUpExitToAppBtn()
        populateViewAdaptersWithContent()
    }

    override fun getView() = this

    override fun isFullscreen() = true

    override fun onShown() {}

    override fun onHidden() {}

    override fun hideBubbles()
    {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        ContextCompat.startActivity(context, intent, null)

        BubbleMenuService.destroyFloatingMenu(context)
    }

    /////////

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
        onItemClickCallback = presenter as OnMyAdapterItemClickListener
        searchCallback = context as SearchCallback
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
            findViewById<CardView>(R.id.detailsGoToUrlCv).setOnClickListener {
                onItemClickCallback.onItemClicked(model)
            }

            if (LinkMetadataFormatter.hasCompatibleImageUrl(model.imageUrl))
            {
                Picasso.get()
                    .load(model.imageUrl)
                    .into(findViewById<ImageView>(R.id.iconIv))
            }
        }

        val descrTv = view.findViewById<TextView>(R.id.detailsDescrTv)
        descrTv.text = when
        {
            model.description.isBlank() -> getString(R.string.no_description)
            else -> model.description
        }
    }

    override fun createTagRecyclerView(view: View)
    {
        tagAdapter = TagAdapter(false)
        tagAdapter.onTagClickedListener = this

        val rv = view.findViewById<RecyclerView>(R.id.detailsTagRv)
        rv.apply {
            adapter = tagAdapter
            isNestedScrollingEnabled = false
            layoutManager = StaggeredGridLayoutManager(
                resources.getInteger(R.integer.tags_details_column_count),
                GridLayout.VERTICAL
            )
        }
    }

    override fun createFab(view: View)
    {
        val shareFab = view.findViewById<FloatingActionButton>(R.id.shareFab)
        shareFab.setOnClickListener {
            onItemClickCallback.onShare(model)
        }

        val editFab = view.findViewById<FloatingActionButton>(R.id.editFab)
        editFab.setOnClickListener {
            onItemClickCallback.onEdit(model)
        }

        val copyFab = view.findViewById<FloatingActionButton>(R.id.copyFab)
        copyFab.setOnClickListener {
            copyToClipboard()
        }
    }

    override fun populateViewAdaptersWithContent()
    {
        tagAdapter.elements = model.stringToListOfTags()
    }

    override fun startInternetAction(link: Link) = IntentActionHelper.startInternetAction(context!!, link)

    override fun startDetailsAction(link: Link) = IntentActionHelper.startDetailsAction(fragmentManager!!, link)

    override fun startShareView(link: Link) = IntentActionHelper.startShareView(context!!, link)

    override fun startEditView(link: Link) = IntentActionHelper.startEditView(context!!, link)

    override fun onSearchTag(tag: String)
    {
        dismiss()
        searchCallback.onOpenSearchView(tag)
    }

    private fun inflateAndReturnDetailsView()
        = activity!!.layoutInflater.inflate(R.layout.content_details, null)

    private fun restoreSavedState(state: Bundle?)
    {
        if (state != null)
            model = state.getParcelable(Link.PARCEL_LINK)
    }

    private fun copyToClipboard()
    {
        val clipboardHelper = ClipboardHelper(context!!)
        clipboardHelper.copyToCliboard(model.url)
    }
}*/