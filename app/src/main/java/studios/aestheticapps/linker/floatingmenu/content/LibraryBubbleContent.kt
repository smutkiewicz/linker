package studios.aestheticapps.linker.floatingmenu.content

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import io.mattcarroll.hover.Content
import kotlinx.android.synthetic.main.content_library.view.*
import studios.aestheticapps.linker.MainActivity
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.adapters.LinkAdapter
import studios.aestheticapps.linker.adapters.OnMyAdapterItemClickListener
import studios.aestheticapps.linker.adapters.SortByAdapter
import studios.aestheticapps.linker.content.IntentActionHelper
import studios.aestheticapps.linker.content.library.LibraryContract
import studios.aestheticapps.linker.content.library.LibraryPresenter
import studios.aestheticapps.linker.floatingmenu.BubbleMenuService
import studios.aestheticapps.linker.floatingmenu.ui.BubbleDetailsDialog
import studios.aestheticapps.linker.floatingmenu.ui.BubbleDialog
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.persistence.link.LinkRepository
import studios.aestheticapps.linker.persistence.link.LinkRepository.Companion.LINK_DELETE
import studios.aestheticapps.linker.utils.PrefsHelper
import java.util.*

class LibraryBubbleContent(context: Context,
                           private val application: Application,
                           private val bubbleContentCallback: BubbleContentCallback) : FrameLayout(context),
    Content, LibraryContract.View, AdapterView.OnItemSelectedListener, Observer
{
    override var presenter: LibraryContract.Presenter = LibraryPresenter(this)

    private lateinit var linkAdapter: LinkAdapter
    private lateinit var orderByColumn: String

    init
    {
        LayoutInflater.from(context).inflate(R.layout.content_library, this, true)

        presenter.start(application)
        presenter.attachDataObserver(this)

        setUpOrderBySection()
        setUpSearchBox()
        setUpLinksRecyclerView()
        setUpSortBySpinner()
        populateViewAdaptersWithContent()
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

    override fun populateViewAdaptersWithContent()
    {
        linkAdapter.elements = presenter.searchForItem(searchBox.query.toString(), orderByColumn)
    }

    override fun obtainQueryFromArguments() {}

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
        sortByTv.text = context.getString(R.string.sort_by_column, columnNameForView)
        searchBox.queryHint = context.getString(R.string.sort_by_column, columnNameForView)
    }

    override fun hideKeyboardFrom(view: View)
    {
        val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun startInternetAction(model: Link)
    {
        bubbleContentCallback.collapseBubble()
        IntentActionHelper.startInternetAction(context!!, model)
    }

    override fun startDetailsAction(model: Link) = BubbleDetailsDialog.draw(context, application, bubbleContentCallback, model)

    override fun startShareView(model: Link)
    {
        bubbleContentCallback.collapseBubble()
        IntentActionHelper.startShareView(context!!, model)
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long)
    {
        val newOrderByColumn = when(pos)
        {
            0 -> LinkRepository.TITLE_COLUMN
            1 -> LinkRepository.CATEGORY_COLUMN
            2 -> LinkRepository.DOMAIN_COLUMN
            3 -> LinkRepository.CREATED_LATEST_COLUMN
            4 -> LinkRepository.CREATED_COLUMN
            else -> LinkRepository.TITLE_COLUMN
        }

        val columnNameForView = SortByAdapter.arrayIndexToColumnNameForView(context!!, pos)
        updateOrderByPref(newOrderByColumn, columnNameForView)
    }

    override fun onNothingSelected(parent: AdapterView<*>) {}

    override fun update(o: Observable?, mode: Any?)
    {
        if (mode != LINK_DELETE)
            populateViewAdaptersWithContent()
    }

    private fun buildExitDialogAndConfirmDelete(model: Link, adapterPosition: Int)
    {
        BubbleDialog.draw(context,
            title = context.getString(R.string.title_confirm_delete),
            message = context.getString(R.string.message_confirm_delete),
            negativeBtnTitle = context.getString(R.string.dont_delete),
            positiveBtnTitle = context.getString(R.string.please_delete),
            callback = (object : BubbleDialog.BubbleDialogCallback
            {
                override fun onPositiveBtnPressed() = deleteItemPermanently(model, adapterPosition)

                override fun onNegativeBtnPressed() = linkAdapter.notifyDataSetChanged()

                override fun onCancel() = linkAdapter.notifyDataSetChanged()
            })
        )
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

        sortByTv.text = context.getString(R.string.sort_by_column, columnNameForView)
        searchBox.queryHint = context.getString(R.string.sort_by_column, columnNameForView)

        // reload content
        populateViewAdaptersWithContent()
    }

    companion object
    {
        const val TAG_PHRASE = "tag_phrase"
    }
}