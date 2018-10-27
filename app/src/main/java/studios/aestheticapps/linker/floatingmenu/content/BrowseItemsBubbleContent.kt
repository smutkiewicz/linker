package studios.aestheticapps.linker.floatingmenu.content

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import io.mattcarroll.hover.Content
import studios.aestheticapps.linker.Link
import studios.aestheticapps.linker.MainActivity
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.adapters.LinksAdapter
import studios.aestheticapps.linker.adapters.RecentLinksAdapter
import studios.aestheticapps.linker.floatingmenu.BubbleMenuService
import java.util.*

class BrowseItemsBubbleContent(context: Context,
                               private val callback: BubbleContentCallback) : FrameLayout(context), Content
{
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

    private fun hideBubbles()
    {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(context, intent, null)

        BubbleMenuService.destroyFloatingMenu(context)
    }

    private fun createRecentRecyclerView()
    {
        val recyclerView = findViewById<RecyclerView>(R.id.recentRecyclerView)
        val horizontalLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recentLinksAdapter = RecentLinksAdapter(createMockedList())

        recyclerView.apply {
            layoutManager = horizontalLayoutManager
            adapter = recentLinksAdapter
        }
    }

    private fun createLinksRecyclerView()
    {
        val recyclerView = findViewById<RecyclerView>(R.id.linksRecyclerView)
        linksAdapter = LinksAdapter(createMockedList())

        recyclerView.apply {
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

    private fun createMockedList(): LinkedList<Link>
    {
        val list = LinkedList<Link>()
        list.apply {
            add(Link("First link"))
            add(Link("Github"))
            add(Link("sth else"))
            add(Link("woooooooooooww"))
            add(Link("a loooooot of text"))
            add(Link("it works"))
            add(Link("nice try mate"))
            add(Link("Nice."))
        }

        return list
    }
}