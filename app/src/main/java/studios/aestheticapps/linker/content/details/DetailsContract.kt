package studios.aestheticapps.linker.content.details

import android.app.Application
import studios.aestheticapps.linker.BasePresenter
import studios.aestheticapps.linker.BaseView
import studios.aestheticapps.linker.model.Link

interface DetailsContract
{
    interface View : BaseView<Presenter>
    {
        fun createViewFromModel(view: android.view.View)
        fun createTagRecyclerView(view: android.view.View)
        fun createFab(view: android.view.View)
        fun populateViewAdaptersWithContent()

        fun startInternetAction(link: Link)
        fun startDetailsAction(link: Link)
        fun startShareView(link: Link)
        fun startEditView(link: Link)
    }

    interface Presenter : BasePresenter
    {
        fun start(application: Application)
        fun setItemFavourite(link: Link)
        fun setItemRecent(link: Link)
    }
}