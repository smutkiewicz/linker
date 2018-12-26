package studios.aestheticapps.linker.content.categories

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.adapters.CategoriesAdapter

class CategoriesDialogFragment : DialogFragment(), CategoriesContract.View
{
    override var presenter: CategoriesContract.Presenter = CategoriesPresenter(this)

    private lateinit var callback: CategoriesChangedCallback
    private lateinit var categoriesRecyclerViewAdapter: CategoriesAdapter.RecyclerViewAdapter

    override fun onCreateDialog(bundle: Bundle?): Dialog
    {
        val builder = AlertDialog.Builder(context!!)
        val view = inflateAndReturnView()
        builder.setView(view)

        createRecyclerView(view)
        createAddCategoryUI(view)

        return builder.create()
    }

    override fun onStart()
    {
        super.onStart()
        presenter.start(activity!!.application)
        populateViewAdaptersWithContent()
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context)
    {
        super.onAttach(context)
    }

    override fun createRecyclerView(view: View)
    {
        categoriesRecyclerViewAdapter = CategoriesAdapter.RecyclerViewAdapter()

        val rv = view.findViewById<RecyclerView>(R.id.categoriesRv)
        rv.apply {
            adapter = categoriesRecyclerViewAdapter
            isNestedScrollingEnabled = true
            layoutManager = LinearLayoutManager(context)
        }

        setUpSwipeGesturesTo(rv)
    }

    override fun setUpSwipeGesturesTo(recyclerView: RecyclerView)
    {
        val helper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
            {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int)
                {
                    val holder = viewHolder as CategoriesAdapter.RecyclerViewAdapter.ViewHolder
                    buildDialogAndConfirmDelete(holder.categoryName, viewHolder.adapterPosition)
                }

                override fun onMove(rv: RecyclerView?, h: RecyclerView.ViewHolder?, t: RecyclerView.ViewHolder?) = false
            })

        helper.attachToRecyclerView(recyclerView)
    }

    override fun populateViewAdaptersWithContent()
    {
        categoriesRecyclerViewAdapter.elements = presenter.getAll()
    }

    override fun createAddCategoryUI(view: View)
    {
        val newCategoryEditText = view.findViewById<EditText>(R.id.addCategoryEt)
        val newCategoryBtn = view.findViewById<ImageButton>(R.id.addCategoryIb)

        newCategoryBtn.setOnClickListener {
            val categoryName = newCategoryEditText.text.toString()

            if (presenter.addItem(categoryName))
            {
                reloadItems()
                newCategoryEditText.text.clear()
            }
            else
            {
                newCategoryEditText.error = resources.getString(R.string.categories_nonunique_name_error)
            }
        }
    }

    private fun inflateAndReturnView()
        = activity!!.layoutInflater.inflate(R.layout.content_categories, null)

    private fun reloadItems()
    {
        categoriesRecyclerViewAdapter.elements = presenter.getAll()
        callback.updateCategories()
    }

    private fun buildDialogAndConfirmDelete(categoryName: String, adapterPosition: Int)
    {
        val builder = AlertDialog
            .Builder(context!!)
            .apply {
                setTitle(R.string.title_confirm_delete)
                setMessage(R.string.message_confirm_delete)
                setIcon(R.mipmap.ic_launcher)
                setNegativeButton(R.string.dont_delete) { _, _ -> categoriesRecyclerViewAdapter.notifyDataSetChanged() }
                setPositiveButton(R.string.please_delete) { _, _ -> deleteItemPermanently(categoryName, adapterPosition) }
                setOnCancelListener { categoriesRecyclerViewAdapter.notifyDataSetChanged() }
            }

        builder.apply {
            create()
            show()
        }
    }

    private fun deleteItemPermanently(categoryName: String, adapterPosition: Int)
    {
        presenter.removeItem(categoryName)
        categoriesRecyclerViewAdapter.removeItem(adapterPosition)
        callback.updateCategories()
    }

    interface CategoriesChangedCallback
    {
        fun updateCategories()
    }

    companion object
    {
        fun newInstance(callback: CategoriesChangedCallback): CategoriesDialogFragment
        {
            val fragment = CategoriesDialogFragment()
            fragment.callback = callback
            return fragment
        }
    }
}