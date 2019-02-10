package studios.aestheticapps.linker.floatingmenu.ui

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.os.Build
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.adapters.CategoriesAdapter
import studios.aestheticapps.linker.content.categories.CategoriesChangedCallback
import studios.aestheticapps.linker.content.categories.CategoriesContract
import studios.aestheticapps.linker.content.categories.CategoriesPresenter

/**
 * Creates the head layer view which is displayed directly on window manager.
 * It means that the view is above every application's view on your phone -
 * until another application does the same.
 */
class BubbleCategoriesDialog(myContext: Context, application: Application) : View(myContext), CategoriesContract.View
{
    override var presenter: CategoriesContract.Presenter = CategoriesPresenter(this)

    private var frameLayout: FrameLayout? = FrameLayout(context)
    private var windowManager: WindowManager? = null

    private lateinit var callback: CategoriesChangedCallback
    private lateinit var categoriesRecyclerViewAdapter: CategoriesAdapter.RecyclerViewAdapter

    init
    {
        addToWindowManager()
        createRecyclerView(frameLayout as View)
        createAddCategoryUI(frameLayout as View)

        presenter.start(application)
        populateViewAdaptersWithContent()
    }

    fun destroy()
    {
        val isAttachedToWindow: Boolean? = frameLayout?.isAttachedToWindow
        if (isAttachedToWindow != null && isAttachedToWindow)
        {
            windowManager!!.removeView(frameLayout)
            frameLayout = null
        }
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

                override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean = false
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
        val backgroundIv = view.findViewById<ImageView>(R.id.backgroundIv)

        newCategoryBtn.setOnClickListener {
            val categoryName = newCategoryEditText.text.toString()

            when
            {
                categoryName.isBlank() -> newCategoryEditText.error = resources.getString(R.string.categories_blank_name_error)
                presenter.addItem(categoryName) ->
                {
                    reloadItems()
                    newCategoryEditText.text.clear()
                }
                else -> newCategoryEditText.error = resources.getString(R.string.categories_nonunique_name_error)
            }
        }

        backgroundIv.setOnClickListener { destroy() }
    }

    override fun dispatchConfigurationChanged(newConfig: Configuration?)
    {
        super.dispatchConfigurationChanged(newConfig)
        destroy()
    }

    override fun dispatchKeyEventPreIme(keyEvent: KeyEvent?): Boolean
    {
        if (keyEvent?.action == KeyEvent.ACTION_UP || keyEvent?.keyCode == KeyEvent.KEYCODE_BACK)
        {
            destroy()
            return true
        }

        return super.dispatchKeyEventPreIme(keyEvent)
    }

    private fun reloadItems()
    {
        categoriesRecyclerViewAdapter.elements = presenter.getAll()
        callback.updateCategories()
    }

    private fun buildDialogAndConfirmDelete(categoryName: String, adapterPosition: Int)
    {
        BubbleDialog.draw(context,
            title = context.getString(R.string.title_confirm_delete),
            message = context.getString(R.string.message_confirm_delete),
            negativeBtnTitle = context.getString(R.string.dont_delete),
            positiveBtnTitle = context.getString(R.string.please_delete),
            callback = (object : BubbleDialog.BubbleDialogCallback
            {
                override fun onPositiveBtnPressed() = deleteItemPermanently(categoryName, adapterPosition)

                override fun onNegativeBtnPressed() = categoriesRecyclerViewAdapter.notifyDataSetChanged()

                override fun onCancel() = categoriesRecyclerViewAdapter.notifyDataSetChanged()
            })
        )
    }

    override fun onConfigurationChanged(newConfig: Configuration?)
    {
        super.onConfigurationChanged(newConfig)
        destroy()
    }

    private fun deleteItemPermanently(categoryName: String, adapterPosition: Int)
    {
        presenter.removeItem(categoryName)
        categoriesRecyclerViewAdapter.removeItem(adapterPosition)
        callback.updateCategories()
    }

    private fun addToWindowManager()
    {
        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }
        else
        {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.CENTER
        frameLayout?.fitsSystemWindows = true

        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager!!.addView(frameLayout, params)

        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        layoutInflater.inflate(R.layout.bubble_categories_dialog, frameLayout)
    }

    companion object
    {
        fun draw(context: Context, application: Application, callback: CategoriesChangedCallback)
        {
            val dialog = BubbleCategoriesDialog(context, application)
            dialog.callback = callback
        }
    }
}