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
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.adapters.CategoriesAdapter

class CategoriesDialogFragment : DialogFragment(), CategoriesContract.View
{
    override var presenter: CategoriesContract.Presenter = CategoriesPresenter(this)

    private lateinit var categoriesAdapter: CategoriesAdapter.RecyclerViewAdapter

    override fun onCreateDialog(bundle: Bundle?): Dialog
    {
        val builder = AlertDialog.Builder(context!!)
        val view = inflateAndReturnView()
        builder.setView(view)

        createRecyclerView(view)

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
        categoriesAdapter = CategoriesAdapter.RecyclerViewAdapter()

        val rv = view.findViewById<RecyclerView>(R.id.categoriesRv)
        rv.apply {
            adapter = categoriesAdapter
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
                    presenter.removeItem(holder.categoryName)
                    categoriesAdapter.removeItem(viewHolder.adapterPosition)
                }

                override fun onMove(rv: RecyclerView?,
                                    h: RecyclerView.ViewHolder?,
                                    t: RecyclerView.ViewHolder?): Boolean { return false }
            })

        helper.attachToRecyclerView(recyclerView)
    }

    override fun populateViewAdaptersWithContent()
    {
        categoriesAdapter.elements = presenter.getAll()
    }

    private fun inflateAndReturnView()
        = activity!!.layoutInflater.inflate(R.layout.content_categories, null)
}