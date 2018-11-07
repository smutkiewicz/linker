package studios.aestheticapps.linker.content.library

import android.app.Application
import studios.aestheticapps.linker.adapters.LinkAdapter
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.persistence.LinkRepository
import java.text.SimpleDateFormat
import java.util.*

class LibraryPresenter(val view: LibraryContract.View) : LibraryContract.Presenter, LinkAdapter.OnLibraryItemClickListener
{
    private lateinit var repository: LinkRepository

    init
    {
        view.presenter = this
    }

    override fun start(application: Application)
    {
        repository = LinkRepository(application)
    }

    override fun start() {}

    override fun stop() {}

    override fun getAllItems(): LinkedList<Link> = repository.getAll()

    override fun searchForItem(phrase: String) = repository.search(phrase)

    override fun addItem(link: Link) = repository.insert(link)

    override fun removeItem(id: Int) = repository.delete(id)

    override fun setItemFavourite(link: Link)
    {
        link.isFavorite = !link.isFavorite
        repository.update(link)
    }

    override fun setItemRecent(link: Link)
    {
        link.lastUsed = getCurrentTime()
        repository.update(link)
    }

    override fun onItemClicked(link: Link) = view.startDetailView(link)

    override fun onFavourite(link: Link) = setItemFavourite(link)

    override fun onShare(link: Link)
    {
        setItemRecent(link)
        view.startShareView(link)
    }

    private fun getCurrentTime(): String
    {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        return formatter.format(Date())
    }
}