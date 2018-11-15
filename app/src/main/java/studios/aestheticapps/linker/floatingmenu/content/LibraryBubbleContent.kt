package studios.aestheticapps.linker.floatingmenu.content

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import io.mattcarroll.hover.Content
import kotlinx.android.synthetic.main.content_library.view.*
import studios.aestheticapps.linker.MainActivity
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.adapters.LinkAdapter
import studios.aestheticapps.linker.content.library.LibraryContract
import studios.aestheticapps.linker.content.library.LibraryPresenter
import studios.aestheticapps.linker.floatingmenu.BubbleMenuService
import studios.aestheticapps.linker.model.Link

class LibraryBubbleContent(context: Context,
                           application: Application,
                           private val callback: BubbleContentCallback) : FrameLayout(context), Content, LibraryContract.View
{
    override var presenter: LibraryContract.Presenter = LibraryPresenter(this)

    private lateinit var linkAdapter: LinkAdapter

    init
    {
        LayoutInflater.from(context).inflate(R.layout.content_library, this, true)

        presenter.start(application)

        setUpSearchBox()
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
        ContextCompat.startActivity(context!!, intent, null)

        BubbleMenuService.destroyFloatingMenu(context!!)
    }

    override fun setUpLinksRecyclerView()
    {
        linkAdapter = LinkAdapter(presenter as LinkAdapter.OnLibraryItemClickListener)
        linkAdapter.elements = presenter.searchForItem(searchBox.query.toString())

        linksRecyclerView.apply {
            adapter = linkAdapter
            isNestedScrollingEnabled = true
            layoutManager = LinearLayoutManager(context)
        }

        setUpSwipeGestures()
    }

    override fun setUpSwipeGestures()
    {
        val helper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
            {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int)
                {
                    val holder = viewHolder as LinkAdapter.ViewHolder

                    presenter.removeItem(holder.id)
                    linkAdapter.removeItem(viewHolder.adapterPosition)
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
                    linkAdapter.elements = presenter.searchForItem(newText)
                    return false
                }
            })
        }
    }

    override fun startClickCardAction(link: Link)
    {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(link.url)
        startActivity(context, i, null)
        callback.collapseBubble()
    }

    override fun startShareView(link: Link)
    {
        //TODO Share
    }

    override fun hideKeyboardFrom(view: View)
    {
        val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}