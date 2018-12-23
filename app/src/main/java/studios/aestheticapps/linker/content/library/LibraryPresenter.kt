package studios.aestheticapps.linker.content.library

import android.app.Application
import studios.aestheticapps.linker.adapters.OnMyAdapterItemClickListener
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.persistence.link.LinkRepository
import studios.aestheticapps.linker.persistence.link.LinkRepository.Companion.ALL
import studios.aestheticapps.linker.utils.DateTimeHelper
import java.util.*

class LibraryPresenter(val view: LibraryContract.View) : LibraryContract.Presenter, OnMyAdapterItemClickListener
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

    override fun searchForItem(phrase: String, orderBy: String) = repository.search(phrase, orderBy)

    override fun addItem(link: Link) = repository.insert(link)

    override fun removeItem(id: Int) = repository.delete(id)

    override fun setItemFavourite(link: Link)
    {
        link.isFavorite = !link.isFavorite
        repository.update(link)
    }

    override fun setItemRecent(link: Link)
    {
        link.lastUsed = DateTimeHelper.getCurrentDateTimeStamp()
        repository.update(link)
    }

    override fun onItemClicked(model: Link)
    {
        setItemRecent(model)
        view.startInternetAction(model)
    }

    override fun onItemLongClicked(model: Link) = view.startDetailsAction(model)

    override fun onFavourite(model: Link) = setItemFavourite(model)

    override fun onShare(model: Link)
    {
        setItemRecent(model)
        view.startShareView(model)
    }
}