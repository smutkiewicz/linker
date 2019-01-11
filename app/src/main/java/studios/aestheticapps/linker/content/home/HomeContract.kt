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
        fun populateViewAdaptersWithContent()

        fun hideBubbles()
        fun hideKeyboardFrom(view: android.view.View)

        fun setUpRecentRecyclerView()
        fun setUpFavoritesRecyclerView()
        fun setUpTagsCloudRecyclerView()

        fun startInternetAction(model: Link)
        fun startDetailsAction(model: Link)
        fun startShareView(model: Link)
        fun startCopyAction(content: String)

        fun updateRecentLinkAdapter()
        fun updateFavLinkAdapter()
        fun updateTagCloudAdapter()
    }

    interface Presenter : BasePresenter
    {
        fun start(application: Application)

        fun getRecentItems(): LinkedList<Link>
        fun getFavoriteItems(): LinkedList<Link>
        fun getTagsCloudItems(): LinkedList<String>
        fun setItemFavourite(model: Link)
        fun setItemRecent(model: Link)

        fun attachDataObserver(o: Observer)
        fun detachDataObserver(o: Observer)
    }
}