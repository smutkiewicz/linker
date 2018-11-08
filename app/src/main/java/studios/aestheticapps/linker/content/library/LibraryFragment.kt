package studios.aestheticapps.linker.content.library

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
import kotlinx.android.synthetic.main.content_library.*
import studios.aestheticapps.linker.MainActivity
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.adapters.LinkAdapter
import studios.aestheticapps.linker.content.details.DetailsActivity
import studios.aestheticapps.linker.floatingmenu.BubbleMenuService
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.model.Link.CREATOR.PARCEL_LINK

class LibraryFragment : Fragment(), LibraryContract.View
{
    override var presenter: LibraryContract.Presenter = LibraryPresenter(this)

    private lateinit var linkAdapter: LinkAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
        = inflater.inflate(R.layout.content_library, container, false)

    override fun onStart()
    {
        super.onStart()
        presenter.start(activity!!.application)

        setUpSearchBox()
        setUpLinksRecyclerView()
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

    override fun startDetailView(link: Link)
    {
        val intent = Intent(context, DetailsActivity::class.java)
        intent.putExtra(PARCEL_LINK, link)
        startActivity(intent)
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