package studios.aestheticapps.linker.content.library

import android.app.Application
import studios.aestheticapps.linker.adapters.OnItemClickListener
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.persistence.LinkRepository
import studios.aestheticapps.linker.persistence.LinkRepository.Companion.ALL
import java.text.SimpleDateFormat
import java.util.*

class LibraryPresenter(val view: LibraryContract.View) : LibraryContract.Presenter, OnItemClickListener
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

    override fun getAllItems(): LinkedList<Link> = repository.getListOf(ALL)

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

    override fun onItemClicked(link: Link)
    {
        setItemRecent(link)
        view.startInternetAction(link)
    }

    override fun onItemLongClicked(link: Link) = view.startDetailsAction(link)

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