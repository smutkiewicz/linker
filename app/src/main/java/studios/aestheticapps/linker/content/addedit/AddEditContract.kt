package studios.aestheticapps.linker.content.addedit

import studios.aestheticapps.linker.BasePresenter
import studios.aestheticapps.linker.BaseView

interface AddEditTaskContract
{
    interface View : BaseView<Presenter>
    {
        fun createFab()
        fun hideBubbles()
    }

    interface Presenter : BasePresenter
}