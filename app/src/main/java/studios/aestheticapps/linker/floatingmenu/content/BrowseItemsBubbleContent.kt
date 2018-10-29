package studios.aestheticapps.linker.floatingmenu.content

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import io.mattcarroll.hover.Content
import kotlinx.android.synthetic.main.browse_items_content.view.*
import studios.aestheticapps.linker.MainActivity
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.adapters.LinksAdapter
import studios.aestheticapps.linker.adapters.RecentLinksAdapter
import studios.aestheticapps.linker.content.browseitems.BrowseItemsContract
import studios.aestheticapps.linker.content.browseitems.BrowseItemsPresenter
import studios.aestheticapps.linker.floatingmenu.BubbleMenuService

class BrowseItemsBubbleContent(context: Context,
                               private val callback: BubbleContentCallback) : FrameLayout(context), Content, BrowseItemsContract.View
{
    override var presenter: BrowseItemsContract.Presenter = BrowseItemsPresenter(this)

    private lateinit var recentLinksAdapter: RecentLinksAdapter
    private lateinit var linksAdapter: LinksAdapter

    init
    {
        LayoutInflater.from(context).inflate(R.layout.browse_items_content, this, true)

        createRecentRecyclerView()
        createLinksRecyclerView()
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

    private fun createRecentRecyclerView()
    {
        val horizontalLayoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        recentLinksAdapter = RecentLinksAdapter(presenter.createMockedList())

        recentRecyclerView.apply {
            layoutManager = horizontalLayoutManager
            adapter = recentLinksAdapter
        }
    }

    private fun createLinksRecyclerView()
    {
        linksAdapter = LinksAdapter(presenter.createMockedList())

        linksRecyclerView.apply {
            adapter = linksAdapter
            layoutManager = object : LinearLayoutManager(context)
            {
                override fun canScrollVertically() = false
            }
        }
    }

    private fun hideKeyboardFrom(context: Context, view: View)
    {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}