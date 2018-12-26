package studios.aestheticapps.linker.content.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager.VERTICAL
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.content_home.*
import studios.aestheticapps.linker.MainActivity
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.adapters.FavoritesAdapter
import studios.aestheticapps.linker.adapters.OnMyAdapterItemClickListener
import studios.aestheticapps.linker.adapters.RecentLinkAdapter
import studios.aestheticapps.linker.adapters.TagAdapter
import studios.aestheticapps.linker.content.IntentActionHelper
import studios.aestheticapps.linker.content.SearchCallback
import studios.aestheticapps.linker.content.UpdateViewCallback
import studios.aestheticapps.linker.floatingmenu.BubbleMenuService
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.utils.ClipboardHelper

class HomeFragment : Fragment(), HomeContract.View, TagAdapter.OnTagClickedListener
{
    override var presenter: HomeContract.Presenter = HomePresenter(this)

    private lateinit var callback: SearchCallback
    private lateinit var updateViewCallback: UpdateViewCallback

    private lateinit var recentLinkAdapter: RecentLinkAdapter
    private lateinit var favLinkAdapter: FavoritesAdapter
    private lateinit var tagCloudAdapter: TagAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
        = inflater.inflate(R.layout.content_home, container, false)

    override fun onStart()
    {
        super.onStart()
        presenter.start(activity!!.application)

        setUpRecentRecyclerView()
        setUpFavoritesRecyclerView()
        setUpTagsCloudRecyclerView()
        populateViewAdaptersWithContent()
    }

    override fun onDestroy()
    {
        super.onDestroy()
        presenter.stop()
    }

    override fun onAttach(context: Context?)
    {
        super.onAttach(context)
        callback = context as SearchCallback
        updateViewCallback = context as UpdateViewCallback
    }

    override fun populateViewAdaptersWithContent()
    {
        recentLinkAdapter.elements = presenter.getRecentItems()
        favLinkAdapter.elements = presenter.getFavoriteItems()
        tagCloudAdapter.elements = presenter.getTagsCloudItems()

        showEmptyViewIfNeeded()
    }

    override fun hideBubbles()
    {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        ContextCompat.startActivity(context!!, intent, null)

        BubbleMenuService.destroyFloatingMenu(context!!)
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
                VERTICAL
            )
        }
    }

    override fun hideKeyboardFrom(view: View)
    {
        val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun startInternetAction(link: Link) = IntentActionHelper.startInternetAction(context!!, link)

    override fun startDetailsAction(link: Link) = IntentActionHelper.startDetailsAction(fragmentManager!!, link)

    override fun startShareView(link: Link) = IntentActionHelper.startShareView(context!!, link)

    override fun onSearchTag(tag: String) = callback.onOpenSearchView(tag)

    override fun startCopyAction(content: String) = ClipboardHelper(context!!).copyToCliboard(content)

    override fun updateRecentLinkAdapter()
    {
        recentLinkAdapter.elements = presenter.getRecentItems()
    }

    override fun updateFavLinkAdapter()
    {
        favLinkAdapter.elements = presenter.getFavoriteItems()
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