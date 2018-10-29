package studios.aestheticapps.linker.content.browseitems

import studios.aestheticapps.linker.BasePresenter
import studios.aestheticapps.linker.BaseView
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
        val repository: LinkedList<Link>

        fun createMockedList(): LinkedList<Link>
        fun removeItem(position: Int)
    }
}