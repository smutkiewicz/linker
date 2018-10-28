package studios.aestheticapps.linker.browseitems

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.browse_items_content.*
import studios.aestheticapps.linker.MainActivity
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.adapters.LinksAdapter
import studios.aestheticapps.linker.adapters.RecentLinksAdapter
import studios.aestheticapps.linker.floatingmenu.BubbleMenuService

class BrowseItemsFragment : Fragment(), BrowseItemsContract.View
{
    private var presenter: BrowseItemsContract.Presenter = BrowseItemsPresenter(this)
    private lateinit var recentLinksAdapter: RecentLinksAdapter
    private lateinit var linksAdapter: LinksAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
        = inflater.inflate(R.layout.browse_items_content, container, false)

    override fun onStart()
    {
        super.onStart()
        createRecentRecyclerView()
        createLinksRecyclerView()
    }

    override fun setPresenter(presenter: BrowseItemsContract.Presenter)
    {
        this.presenter = presenter
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
        val horizontalLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
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
            layoutManager = object : LinearLayoutManager(context)
            {
                override fun canScrollVertically() = false
            }

            adapter = linksAdapter
        }
    }

    private fun hideKeyboardFrom(context: Context, view: View)
    {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}