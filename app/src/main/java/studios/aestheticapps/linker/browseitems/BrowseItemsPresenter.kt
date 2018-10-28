package studios.aestheticapps.linker.browseitems

import studios.aestheticapps.linker.model.Link
import java.util.*

class BrowseItemsPresenter(val browseItemsView: BrowseItemsContract.View) : BrowseItemsContract.Presenter
{
    init
    {
        browseItemsView.setPresenter(this)
    }

    override fun start() {}

    override fun createMockedList(): LinkedList<Link>
    {
        val list = LinkedList<Link>()
        list.apply {
            add(Link("First link"))
            add(Link("Github"))
            add(Link("sth else"))
            add(Link("woooooooooooww"))
            add(Link("a loooooot of text"))
            add(Link("it works"))
            add(Link("nice try mate"))
            add(Link("Nice."))
        }

        return list
    }
}