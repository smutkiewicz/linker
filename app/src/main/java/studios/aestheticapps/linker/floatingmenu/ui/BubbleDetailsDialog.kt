package studios.aestheticapps.linker.floatingmenu.ui

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.os.Build
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.adapters.OnMyAdapterItemClickListener
import studios.aestheticapps.linker.adapters.TagAdapter
import studios.aestheticapps.linker.content.IntentActionHelper
import studios.aestheticapps.linker.content.details.DetailsContract
import studios.aestheticapps.linker.content.details.DetailsPresenter
import studios.aestheticapps.linker.floatingmenu.content.BubbleContentCallback
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.utils.ClipboardHelper
import studios.aestheticapps.linker.validation.LinkMetadataFormatter

/**
 * Creates the head layer view which is displayed directly on window manager.
 * It means that the view is above every application's view on your phone -
 * until another application does the same.
 */
class BubbleDetailsDialog(myContext: Context,
                          application: Application,
                          private val model: Link) : View(myContext), DetailsContract.View, TagAdapter.OnTagClickedListener
{
    override var presenter: DetailsContract.Presenter = DetailsPresenter(this)

    private var frameLayout: FrameLayout? = FrameLayout(context)
    private var windowManager: WindowManager? = null

    private var onItemClickCallback: OnMyAdapterItemClickListener
    private var bubbleContentCallback: BubbleContentCallback? = null

    private lateinit var tagAdapter: TagAdapter

    init
    {
        addToWindowManager()

        onItemClickCallback = presenter as OnMyAdapterItemClickListener

        createViewFromModel(frameLayout as View)
        createTagRecyclerView(frameLayout as View)
        createFab(frameLayout as View)
        createBackground(frameLayout as View)

        presenter.start(application)
        populateViewAdaptersWithContent()
    }

    fun destroy()
    {
        val isAttachedToWindow: Boolean? = frameLayout?.isAttachedToWindow
        if (isAttachedToWindow != null && isAttachedToWindow)
        {
            windowManager!!.removeView(frameLayout)
            frameLayout = null
        }
    }

    override fun dispatchConfigurationChanged(newConfig: Configuration?)
    {
        super.dispatchConfigurationChanged(newConfig)
        destroy()
    }

    override fun dispatchKeyEventPreIme(keyEvent: KeyEvent?): Boolean
    {
        if (keyEvent?.action == KeyEvent.ACTION_UP || keyEvent?.keyCode == KeyEvent.KEYCODE_BACK)
        {
            destroy()
            return true
        }

        return super.dispatchKeyEventPreIme(keyEvent)
    }

    override fun onConfigurationChanged(newConfig: Configuration?)
    {
        super.onConfigurationChanged(newConfig)
        destroy()
    }

    override fun createViewFromModel(view: View)
    {
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
            model.description.isBlank() -> context.getString(R.string.no_description)
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

    override fun startInternetAction(link: Link)
    {
        destroyAndCollapseBubble()
        IntentActionHelper.startInternetAction(context!!, link)
    }

    override fun startDetailsAction(link: Link) {}

    override fun startShareView(link: Link)
    {
        destroyAndCollapseBubble()
        IntentActionHelper.startShareView(context!!, link)
    }

    override fun startEditView(link: Link)
    {
        destroyAndCollapseBubble()
        IntentActionHelper.startEditView(context!!, link)
    }

    override fun onSearchTag(tag: String)
    {
        // TODO onSearch
        //searchCallback.onOpenSearchView(tag)
    }

    private fun copyToClipboard()
    {
        val clipboardHelper = ClipboardHelper(context!!)
        clipboardHelper.copyToCliboard(model.url)
    }

    private fun createBackground(view: View)
    {
        val backgroundIv = view.findViewById<ImageView>(R.id.backgroundIv)
        backgroundIv.setOnClickListener { destroy() }
    }

    private fun destroyAndCollapseBubble()
    {
        destroy()
        bubbleContentCallback?.collapseBubble()
    }

    private fun addToWindowManager()
    {
        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }
        else
        {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.CENTER
        frameLayout?.fitsSystemWindows = true

        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager!!.addView(frameLayout, params)

        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        layoutInflater.inflate(R.layout.bubble_details, frameLayout)
    }

    companion object
    {
        fun draw(context: Context, application: Application, callback: BubbleContentCallback, model: Link)
        {
            val dialog = BubbleDetailsDialog(context, application, model)
            dialog.bubbleContentCallback = callback
        }
    }
}