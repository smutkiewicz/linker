package studios.aestheticapps.linker.content.browseitems

import studios.aestheticapps.linker.model.Link
import java.util.*

class BrowseItemsPresenter(val browseItemsView: BrowseItemsContract.View) : BrowseItemsContract.Presenter
{
    override var repository: LinkedList<Link> = LinkedList()

    init
    {
        browseItemsView.presenter = this
        createMockedList()
    }

    override fun start() {}

    override fun createMockedList(): LinkedList<Link>
    {
        //TODO make REAL repository
        repository.apply {
            add(Link(title = "First link", domain = "github.com", url = "https://github.com/smutkiewicz"))
            add(Link("Github", domain = "github.com", url = "https://github.com/smutkiewicz"))
            add(Link("sth else", domain = "github.com", url = "https://github.com/smutkiewicz"))
            add(Link("woooooooooooww", domain = "github.com", url = "https://github.com/smutkiewicz"))
            add(Link("a loooooot of text", domain = "github.com", url = "https://github.com/smutkiewicz"))
            add(Link("it works", domain = "github.com", url = "https://github.com/smutkiewicz"))
            add(Link("nice try mate", domain = "github.com", url = "https://github.com/smutkiewicz"))
            add(Link("Nice.", domain = "github.com", url = "https://github.com/smutkiewicz"))
        }

        return repository
    }

    override fun removeItem(position: Int)
    {
        //TODO make REAL repository
        repository.removeAt(position)
    }
}