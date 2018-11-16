package studios.aestheticapps.linker.content.details

import studios.aestheticapps.linker.BasePresenter
import studios.aestheticapps.linker.BaseView

interface DetailsContract
{
    interface View : BaseView<Presenter>
    {
        fun createViewFromModel()
        fun createTagRecyclerView()
        fun createFab()
        fun openEdit()
    }

    interface Presenter : BasePresenter
}