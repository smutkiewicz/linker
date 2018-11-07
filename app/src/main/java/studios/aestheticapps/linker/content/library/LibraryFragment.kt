package studios.aestheticapps.linker.content.browseitems

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.browse_items_content.*
import studios.aestheticapps.linker.MainActivity
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.adapters.LinkAdapter
import studios.aestheticapps.linker.adapters.RecentLinkAdapter
import studios.aestheticapps.linker.floatingmenu.BubbleMenuService
import studios.aestheticapps.linker.model.Link

class BrowseItemsFragment : Fragment(), BrowseItemsContract.View
{
    override var presenter: BrowseItemsContract.Presenter = BrowseItemsPresenter(this)

    private lateinit var recentLinkAdapter: RecentLinkAdapter
    private lateinit var linkAdapter: LinkAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
        = inflater.inflate(R.layout.browse_items_content, container, false)

    override fun onStart()
    {
        super.onStart()
        presenter.start(activity!!.application)

        setUpSearchBox()
        setUpLinksRecyclerView()
        setUpRecentRecyclerView()
    }

    override fun onDestroy()
    {
        super.onDestroy()
        presenter.stop()
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

        recentLinkAdapter = RecentLinkAdapter()
        recentLinkAdapter.elements = presenter.getRecentItems()

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
            isNestedScrollingEnabled = false
            layoutManager = object : LinearLayoutManager(context)
            {
                override fun canScrollVertically() = false
            }
        }

        setUpSwipeGestures()
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
                val holder = viewHolder as LinkAdapter.ViewHolder

                presenter.removeItem(holder.id)
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

        hideKeyboardFrom(view!!)
    }

    override fun hideKeyboardFrom(view: View)
    {
        val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}