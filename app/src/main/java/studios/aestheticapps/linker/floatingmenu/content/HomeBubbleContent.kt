package studios.aestheticapps.linker.floatingmenu.content

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import io.mattcarroll.hover.Content
import kotlinx.android.synthetic.main.content_home.view.*
import studios.aestheticapps.linker.MainActivity
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.adapters.FavoritesAdapter
import studios.aestheticapps.linker.adapters.OnMyAdapterItemClickListener
import studios.aestheticapps.linker.adapters.RecentLinkAdapter
import studios.aestheticapps.linker.adapters.TagAdapter
import studios.aestheticapps.linker.content.IntentActionHelper
import studios.aestheticapps.linker.content.SearchCallback
import studios.aestheticapps.linker.content.home.HomeContract
import studios.aestheticapps.linker.content.home.HomePresenter
import studios.aestheticapps.linker.floatingmenu.BubbleMenuService
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.persistence.link.LinkRepository
import studios.aestheticapps.linker.utils.ClipboardHelper
import java.util.*

class HomeBubbleContent(context: Context,
                        application: Application,
                        private val bubbleContentCallback: BubbleContentCallback) : FrameLayout(context),
    Content, HomeContract.View, TagAdapter.OnTagClickedListener, Observer
{
    override var presenter: HomeContract.Presenter = HomePresenter(this)

    private lateinit var searchCallback: SearchCallback

    private lateinit var recentLinkAdapter: RecentLinkAdapter
    private lateinit var favLinkAdapter: FavoritesAdapter
    private lateinit var tagCloudAdapter: TagAdapter

    init
    {
        LayoutInflater.from(context).inflate(R.layout.content_home, this, true)
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
        startActivity(context, intent, null)

        BubbleMenuService.destroyFloatingMenu(context)
    }

    /*override fun onAttach(context: Context?)
    {
        super.onAttach(context)
        searchCallback = context as SearchCallback
        updateViewCallback = context as UpdateViewCallback
    }*/

    override fun populateViewAdaptersWithContent()
    {
        recentLinkAdapter.elements = presenter.getRecentItems()
        favLinkAdapter.elements = presenter.getFavoriteItems()
        tagCloudAdapter.elements = presenter.getTagsCloudItems()

        showEmptyViewIfNeeded()
    }

    override fun setUpRecentRecyclerView()
    {
        val horizontalLayoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        recentLinkAdapter = RecentLinkAdapter(presenter as OnMyAdapterItemClickListener)
        recentRecyclerView.apply {
            layoutManager = horizontalLayoutManager
            adapter = recentLinkAdapter
        }
    }

    override fun setUpFavoritesRecyclerView()
    {
        favLinkAdapter = FavoritesAdapter(presenter as OnMyAdapterItemClickListener)

        favRecyclerView.apply {
            adapter = favLinkAdapter
            isNestedScrollingEnabled = false
            layoutManager = GridLayoutManager(
                context,
                resources.getInteger(R.integer.favs_column_count)
            )
        }
    }

    override fun setUpTagsCloudRecyclerView()
    {
        tagCloudAdapter = TagAdapter(false)
        tagCloudAdapter.onTagClickedListener = this

        tagsCloudRecyclerView.apply {
            adapter = tagCloudAdapter
            isNestedScrollingEnabled = false
            layoutManager = StaggeredGridLayoutManager(
                resources.getInteger(R.integer.tags_column_count),
                StaggeredGridLayoutManager.VERTICAL
            )
        }
    }

    override fun hideKeyboardFrom(view: View)
    {
        val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun startInternetAction(link: Link)
    {
        bubbleContentCallback.collapseBubble()
        IntentActionHelper.startInternetAction(context!!, link)
    }

    override fun startDetailsAction(link: Link)
    {
        // TODO Details
        //IntentActionHelper.startDetailsAction(fragmentManager!!, link)
    }

    override fun startShareView(link: Link)
    {
        bubbleContentCallback.collapseBubble()
        IntentActionHelper.startShareView(context!!, link)
    }

    override fun onSearchTag(tag: String)
    {
        // TODO search
        //searchCallback.onOpenSearchView(tag)
    }

    override fun startCopyAction(content: String) = ClipboardHelper(context!!).copyToCliboard(content)

    override fun update(p0: Observable?, mode: Any?)
    {
        when (mode)
        {
            LinkRepository.LINK_UPDATE ->
            {
                updateFavLinkAdapter()
                updateRecentLinkAdapter()
                updateTagCloudAdapter()
            }

            LinkRepository.RECENT_UPDATE -> updateRecentLinkAdapter()

            LinkRepository.FAV_UPDATE -> updateFavLinkAdapter()
        }
    }

    override fun updateRecentLinkAdapter()
    {
        recentLinkAdapter.elements = presenter.getRecentItems()
    }

    override fun updateFavLinkAdapter()
    {
        favLinkAdapter.elements = presenter.getFavoriteItems()
    }

    override fun updateTagCloudAdapter()
    {
        tagCloudAdapter.elements = presenter.getTagsCloudItems()
    }

    private fun setUpExitToAppBtn()
    {
        exitToAppBtn.visibility = View.VISIBLE
        exitToAppBtn.setOnClickListener {
            hideBubbles()
        }
    }

    private fun showEmptyViewIfNeeded()
    {
        if (recentLinkAdapter.itemCount == 0)
            setEmptyState(recentRecyclerView, emptyShareTv)
        else
            setActiveState(recentRecyclerView, emptyShareTv)

        if (favLinkAdapter.itemCount == 0)
            setEmptyState(favRecyclerView, emptyFavsTv)
        else
            setActiveState(favRecyclerView, emptyFavsTv)

        if (tagCloudAdapter.itemCount == 0)
            setEmptyState(tagsCloudRecyclerView, emptyCloudTv)
        else
            setActiveState(tagsCloudRecyclerView, emptyCloudTv)
    }

    private fun setEmptyState(container: View, emptyStateView: View)
    {
        container.visibility = View.INVISIBLE
        emptyStateView.visibility = View.VISIBLE
    }

    private fun setActiveState(container: View, emptyStateView: View)
    {
        container.visibility = View.VISIBLE
        emptyStateView.visibility = View.GONE
    }
}