package studios.aestheticapps.linker.browseitems

import studios.aestheticapps.linker.base.BasePresenter
import studios.aestheticapps.linker.base.BaseView
import studios.aestheticapps.linker.model.Link
import java.util.*

interface BrowseItemsContract
{
    interface View : BaseView<Presenter>
    {
        fun hideBubbles()
    }

    interface Presenter : BasePresenter
    {
        fun createMockedList(): LinkedList<Link>
    }
}