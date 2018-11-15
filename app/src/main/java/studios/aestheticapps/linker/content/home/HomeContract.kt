package studios.aestheticapps.linker.content.home

import android.app.Application
import studios.aestheticapps.linker.BasePresenter
import studios.aestheticapps.linker.BaseView
import studios.aestheticapps.linker.model.Link
import java.util.*

interface HomeContract
{
    interface View : BaseView<Presenter>
    {
        fun hideBubbles()
        fun hideKeyboardFrom(view: android.view.View)

        fun setUpRecentRecyclerView()
        fun setUpFavoritesRecyclerView()
    }

    interface Presenter : BasePresenter
    {
        fun start(application: Application)

        fun getRecentItems(): LinkedList<Link>
        fun getFavoriteItems(): LinkedList<Link>
    }
}