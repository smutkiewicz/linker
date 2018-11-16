package studios.aestheticapps.linker.content.library

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
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
import studios.aestheticapps.linker.adapters.OnItemClickListener
import studios.aestheticapps.linker.content.IntentActionHelper
import studios.aestheticapps.linker.floatingmenu.BubbleMenuService
import studios.aestheticapps.linker.model.Link

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
        populateViewAdaptersWithContent()
    }

    override fun onDestroy()
    {
        super.onDestroy()
        presenter.stop()
    }

    override fun populateViewAdaptersWithContent()
    {
        linkAdapter.elements = presenter.searchForItem(searchBox.query.toString())
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
        linkAdapter = LinkAdapter(presenter as OnItemClickListener)

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
                buildExitDialogAndConfirmDelete(holder.id, viewHolder.adapterPosition)
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
            isActivated = false
            isIconified = false

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

    override fun hideKeyboardFrom(view: View)
    {
        val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun startInternetAction(link: Link) = IntentActionHelper.startInternetAction(context!!, link)

    override fun startDetailsAction(link: Link) = IntentActionHelper.startDetailsAction(fragmentManager!!, link)

    override fun startShareView(link: Link) = IntentActionHelper.startShareView(context!!, link)

    private fun buildExitDialogAndConfirmDelete(id: Int, adapterPosition: Int)
    {
        val builder = AlertDialog
            .Builder(context!!)
            .apply {
                setTitle(R.string.library_confirm_exit)
                setIcon(R.mipmap.ic_launcher)
                setMessage(R.string.library_message_confirm_exit)
                setNegativeButton(R.string.library_dont_delete) { _, _ -> linkAdapter.notifyDataSetChanged() }
                setPositiveButton(R.string.library_delete) { _, _ -> deleteItemPermanently(id, adapterPosition) }
            }

        builder.apply {
            create()
            show()
        }
    }

    private fun deleteItemPermanently(id: Int, adapterPosition: Int)
    {
        presenter.removeItem(id)
        linkAdapter.removeItem(adapterPosition)
    }
}