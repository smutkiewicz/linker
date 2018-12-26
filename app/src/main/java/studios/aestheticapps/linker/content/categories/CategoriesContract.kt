package studios.aestheticapps.linker.content.categories

import android.app.Application
import android.support.v7.widget.RecyclerView
import studios.aestheticapps.linker.BasePresenter
import studios.aestheticapps.linker.BaseView
import java.util.*

interface CategoriesContract
{
    interface View : BaseView<Presenter>
    {
        fun createRecyclerView(view: android.view.View)
        fun createAddCategoryUI(view: android.view.View)
        fun populateViewAdaptersWithContent()
        fun setUpSwipeGesturesTo(recyclerView: RecyclerView)
    }

    interface Presenter : BasePresenter
    {
        fun start(application: Application)
        fun getAll(): LinkedList<String>
        fun removeItem(categoryName: String)
        fun addItem(categoryName: String): Boolean
    }
}