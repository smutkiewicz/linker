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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.content_library.*
import kotlinx.android.synthetic.main.content_library.view.*
import studios.aestheticapps.linker.MainActivity
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.adapters.LinkAdapter
import studios.aestheticapps.linker.adapters.OnMyAdapterItemClickListener
import studios.aestheticapps.linker.adapters.SortByAdapter
import studios.aestheticapps.linker.content.IntentActionHelper
import studios.aestheticapps.linker.floatingmenu.BubbleMenuService
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.persistence.link.LinkRepository.Companion.CATEGORY_COLUMN
import studios.aestheticapps.linker.persistence.link.LinkRepository.Companion.CREATED_COLUMN
import studios.aestheticapps.linker.persistence.link.LinkRepository.Companion.CREATED_LATEST_COLUMN
import studios.aestheticapps.linker.persistence.link.LinkRepository.Companion.DOMAIN_COLUMN
import studios.aestheticapps.linker.persistence.link.LinkRepository.Companion.TITLE_COLUMN
import studios.aestheticapps.linker.utils.PrefsHelper

class LibraryFragment : Fragment(), LibraryContract.View, AdapterView.OnItemSelectedListener
{
    override var presenter: LibraryContract.Presenter = LibraryPresenter(this)

    private lateinit var linkAdapter: LinkAdapter
    private lateinit var orderByColumn: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
        = inflater.inflate(R.layout.content_library, container, false)

    override fun onStart()
    {
        super.onStart()
        presenter.start(activity!!.application)

        setUpOrderBySection()
        setUpSearchBox()
        setUpLinksRecyclerView()
        setUpSortBySpinner()
        populateViewAdaptersWithContent()
    }

    override fun onDestroy()
    {
        super.onDestroy()
        presenter.stop()
    }

    override fun populateViewAdaptersWithContent()
    {
        linkAdapter.elements = presenter.searchForItem(searchBox.query.toString(), orderByColumn)
    }

    override fun obtainQueryFromArguments()
    {
        arguments?.let {
            val query = arguments!!.getString(TAG_PHRASE, "")
            searchBox.setQuery(query, true)
        }

        arguments = null
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
        linkAdapter = LinkAdapter(presenter as OnMyAdapterItemClickListener)

        linksRecyclerView.apply {
            adapter = linkAdapter
            isNestedScrollingEnabled = true
            layoutManager = LinearLayoutManager(context)
        }

        setUpSwipeGestures()
    }

    override fun setUpSortBySpinner()
    {
        val adapter = ArrayAdapter.createFromResource(
            context,
            R.array.sort_by_array,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortBySpinner.apply {
            this.adapter = adapter
            sortBySpinner.isSelected = false
            sortBySpinner.setSelection(SortByAdapter.columnNameToArrayIndex(orderByColumn), true)
        }
        sortBySpinner.onItemSelectedListener = this
    }

    override fun setUpSwipeGestures()
    {
        val helper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
        {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int)
            {
                val holder = viewHolder as LinkAdapter.ViewHolder
                buildExitDialogAndConfirmDelete(holder.model, viewHolder.adapterPosition)
            }

            override fun onMove(rv: RecyclerView?, h: RecyclerView.ViewHolder?, t: RecyclerView.ViewHolder?) = false
        })

        helper.attachToRecyclerView(linksRecyclerView)
    }

    override fun setUpSearchBox()
    {
        searchBox.apply {
            isActivated = false
            isIconified = false

            obtainQueryFromArguments()
            clearFocus()

            setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener
            {
                override fun onQueryTextSubmit(query: String) = false

                override fun onQueryTextChange(newText: String): Boolean
                {
                    linkAdapter.elements = presenter.searchForItem(newText, orderByColumn)
                    return false
                }
            })
        }
    }

    override fun setUpOrderBySection()
    {
        orderByColumn = PrefsHelper.obtainOrderByColumn(context!!)

        val columnNameForView = SortByAdapter.columnNameToColumnNameForView(context!!, orderByColumn)
        sortByTv.text = getString(R.string.sort_by_column, columnNameForView)
        searchBox.queryHint = getString(R.string.sort_by_column, columnNameForView)
    }

    override fun hideKeyboardFrom(view: View)
    {
        val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun startInternetAction(model: Link) = IntentActionHelper.startInternetAction(context!!, model)

    override fun startDetailsAction(model: Link) = IntentActionHelper.startDetailsAction(fragmentManager!!, model)

    override fun startShareView(model: Link) = IntentActionHelper.startShareView(context!!, model)

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long)
    {
        val newOrderByColumn = when(pos)
        {
            0 -> TITLE_COLUMN
            1 -> CATEGORY_COLUMN
            2 -> DOMAIN_COLUMN
            3 -> CREATED_LATEST_COLUMN
            4 -> CREATED_COLUMN
            else -> TITLE_COLUMN
        }

        val columnNameForView = SortByAdapter.arrayIndexToColumnNameForView(context!!, pos)
        updateOrderByPref(newOrderByColumn, columnNameForView)
    }

    override fun onNothingSelected(parent: AdapterView<*>) {}

    private fun buildExitDialogAndConfirmDelete(model: Link, adapterPosition: Int)
    {
        val builder = AlertDialog
            .Builder(context!!)
            .apply {
                setTitle(R.string.title_confirm_delete)
                setIcon(R.mipmap.ic_launcher)
                setMessage(R.string.message_confirm_delete)
                setNegativeButton(R.string.dont_delete) { _, _ -> linkAdapter.notifyDataSetChanged() }
                setPositiveButton(R.string.please_delete) { _, _ -> deleteItemPermanently(model, adapterPosition) }
                setOnCancelListener { linkAdapter.notifyDataSetChanged() }
            }

        builder.apply {
            create()
            show()
        }
    }

    private fun deleteItemPermanently(model: Link, adapterPosition: Int)
    {
        presenter.removeItem(model)
        linkAdapter.removeItem(adapterPosition)
    }

    private fun updateOrderByPref(column: String, columnNameForView: String)
    {
        orderByColumn = column
        PrefsHelper.setOrderByColumn(context!!, column)

        sortByTv.text = getString(R.string.sort_by_column, columnNameForView)
        searchBox.queryHint = getString(R.string.sort_by_column, columnNameForView)

        // reload content
        populateViewAdaptersWithContent()
    }

    companion object
    {
        const val TAG_PHRASE = "tag_phrase"
    }
}