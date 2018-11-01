package studios.aestheticapps.linker.floatingmenu.content

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import io.mattcarroll.hover.Content
import kotlinx.android.synthetic.main.browse_items_content.view.*
import studios.aestheticapps.linker.MainActivity
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.adapters.LinkAdapter
import studios.aestheticapps.linker.adapters.RecentLinkAdapter
import studios.aestheticapps.linker.content.browseitems.BrowseItemsContract
import studios.aestheticapps.linker.content.browseitems.BrowseItemsPresenter
import studios.aestheticapps.linker.floatingmenu.BubbleMenuService
import studios.aestheticapps.linker.model.Link

class BrowseItemsBubbleContent(context: Context,
                               application: Application,
                               private val callback: BubbleContentCallback) : FrameLayout(context), Content, BrowseItemsContract.View
{
    override var presenter: BrowseItemsContract.Presenter = BrowseItemsPresenter(this)

    private lateinit var recentLinkAdapter: RecentLinkAdapter
    private lateinit var linkAdapter: LinkAdapter

    init
    {
        LayoutInflater.from(context).inflate(R.layout.browse_items_content, this, true)
        presenter.start(application)

        setUpSearchBox()
        setUpRecentRecyclerView()
        setUpLinksRecyclerView()
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

    override fun setUpRecentRecyclerView()
    {
        val horizontalLayoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        recentLinkAdapter = RecentLinkAdapter()
        recentLinkAdapter.elements = presenter.searchForItem(searchBox.query.toString())

        recentRecyclerView.apply {
            layoutManager = horizontalLayoutManager
            adapter = recentLinkAdapter
        }
    }

    override fun setUpLinksRecyclerView()
    {
        linkAdapter = LinkAdapter()
        linkAdapter.elements = presenter.searchForItem(searchBox.query.toString())

        linksRecyclerView.apply {
            adapter = linkAdapter
            layoutManager = object : LinearLayoutManager(context)
            {
                override fun canScrollVertically() = false
            }
        }
    }

    override fun setUpSwipeGestures()
    {
        val helper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            )
            {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int)
                {
                    presenter.removeItem(viewHolder.adapterPosition)
                    linkAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                    recentLinkAdapter.notifyDataSetChanged()
                }

                override fun onMove(rv: RecyclerView?,
                                    h: RecyclerView.ViewHolder?,
                                    t: RecyclerView.ViewHolder?): Boolean { return false }
            })

        helper.attachToRecyclerView(linksRecyclerView)
    }

    override fun setUpSearchBox()
    {
        searchBox.apply {
            isActivated = true
            isIconified = false

            onActionViewExpanded()
            clearFocus()

            setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener
            {
                override fun onQueryTextSubmit(query: String) = false

                override fun onQueryTextChange(newText: String): Boolean
                {
                    linkAdapter.elements = presenter.searchForItem(newText) as List<Link>
                    return false
                }
            })
        }

        hideKeyboardFrom(view)
    }

    override fun hideKeyboardFrom(view: View)
    {
        val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}