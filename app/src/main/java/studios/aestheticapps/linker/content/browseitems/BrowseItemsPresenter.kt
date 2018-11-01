package studios.aestheticapps.linker.content.browseitems

import android.app.Application
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.persistence.LinkRepository
import studios.aestheticapps.linker.persistence.LinkRoomDatabase
import java.util.*

class BrowseItemsPresenter(val browseItemsView: BrowseItemsContract.View) : BrowseItemsContract.Presenter
{
    private lateinit var repository: LinkRepository

    init
    {
        browseItemsView.presenter = this
    }

    override fun start(application: Application)
    {
        repository = LinkRepository(application)
    }

    override fun start() {}

    override fun getAllItems(): LinkedList<Link> = repository.getAll()

    override fun getRecentItems() = repository.getAll()

    override fun searchForItem(phrase: String) = repository.search(phrase)

    override fun addItem(link: Link) = repository.insert(link)

    override fun removeItem(id: Int) = repository.delete(id)

    override fun stop() = LinkRoomDatabase.destroyInstance()
}