package studios.aestheticapps.linker.addedit

import studios.aestheticapps.linker.base.BasePresenter
import studios.aestheticapps.linker.base.BaseView

interface AddEditTaskContract
{
    interface View : BaseView<Presenter>
    {
        fun createFab()
        fun hideBubbles()
    }

    interface Presenter : BasePresenter
    {

    }
}