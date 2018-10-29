package studios.aestheticapps.linker.content.browseitems

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
import kotlinx.android.synthetic.main.browse_items_content.*
import studios.aestheticapps.linker.MainActivity
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.adapters.LinksAdapter
import studios.aestheticapps.linker.adapters.RecentLinksAdapter
import studios.aestheticapps.linker.floatingmenu.BubbleMenuService

class BrowseItemsFragment : Fragment(), BrowseItemsContract.View
{
    override var presenter: BrowseItemsContract.Presenter = BrowseItemsPresenter(this)

    private lateinit var recentLinksAdapter: RecentLinksAdapter
    private lateinit var linksAdapter: LinksAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
        = inflater.inflate(R.layout.browse_items_content, container, false)

    override fun onStart()
    {
        super.onStart()
        presenter.start()

        createRecentRecyclerView()
        createLinksRecyclerView()
    }

    override fun hideBubbles()
    {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        ContextCompat.startActivity(context!!, intent, null)

        BubbleMenuService.destroyFloatingMenu(context!!)
    }

    private fun createRecentRecyclerView()
    {
        val horizontalLayoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        recentLinksAdapter = RecentLinksAdapter(presenter.repository)

        recentRecyclerView.apply {
            layoutManager = horizontalLayoutManager
            adapter = recentLinksAdapter
        }
    }

    private fun createLinksRecyclerView()
    {
        linksAdapter = LinksAdapter(presenter.repository)

        linksRecyclerView.apply {
            adapter = linksAdapter
            isNestedScrollingEnabled = false
            layoutManager = object : LinearLayoutManager(context)
            {
                override fun canScrollVertically() = false
            }
        }

        setUpSwipeGestures()
    }

    private fun setUpSwipeGestures()
    {
        val helper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
        {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int)
            {
                presenter.removeItem(viewHolder.adapterPosition)
                linksAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                recentLinksAdapter.notifyDataSetChanged()
            }

            override fun onMove(recyclerView: RecyclerView?,
                                viewHolder: RecyclerView.ViewHolder?,
                                target: RecyclerView.ViewHolder?): Boolean { return false }
        })

        helper.attachToRecyclerView(linksRecyclerView)
    }
}