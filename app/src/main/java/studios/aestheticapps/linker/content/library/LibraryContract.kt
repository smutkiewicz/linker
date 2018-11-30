package studios.aestheticapps.linker.content.library

import android.app.Application
import studios.aestheticapps.linker.BasePresenter
import studios.aestheticapps.linker.BaseView
import studios.aestheticapps.linker.model.Link
import java.util.*

interface LibraryContract
{
    interface View : BaseView<Presenter>
    {
        fun populateViewAdaptersWithContent()

        fun hideBubbles()
        fun hideKeyboardFrom(view: android.view.View)

        fun setUpSwipeGestures()
        fun setUpSearchBox()
        fun setUpLinksRecyclerView()

        fun startInternetAction(link: Link)
        fun startShareView(link: Link)
        fun startDetailsAction(link: Link)
        fun obtainQueryFromArguments()
    }

    interface Presenter : BasePresenter
    {
        fun start(application: Application)

        fun getAllItems(): LinkedList<Link>
        fun searchForItem(phrase: String): LinkedList<Link>

        fun removeItem(position: Int)
        fun addItem(link: Link)
        fun setItemFavourite(link: Link)
        fun setItemRecent(link: Link)
    }
}