package studios.aestheticapps.linker.content.library

import android.app.Application
import studios.aestheticapps.linker.adapters.CategoriesAdapter
import studios.aestheticapps.linker.adapters.OnMyAdapterItemClickListener
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.persistence.link.LinkRepository
import studios.aestheticapps.linker.persistence.link.LinkRepository.Companion.ALL
import studios.aestheticapps.linker.persistence.link.LinkRepository.Companion.FAV_UPDATE
import studios.aestheticapps.linker.persistence.link.LinkRepository.Companion.RECENT_UPDATE
import studios.aestheticapps.linker.utils.DateTimeHelper
import java.util.*

class LibraryPresenter(val view: LibraryContract.View) : LibraryContract.Presenter, OnMyAdapterItemClickListener
{
    private lateinit var repository: LinkRepository
    private lateinit var categoriesAdapter: CategoriesAdapter

    init
    {
        view.presenter = this
    }

    override fun start(application: Application)
    {
        repository = LinkRepository(application)

        // Part of logic responsible for parsing metadata and category
        categoriesAdapter = CategoriesAdapter(application)
    }

    override fun getAllItems(): LinkedList<Link> = repository.getListOf(ALL)

    override fun searchForItem(phrase: String, orderBy: String) = repository.search(phrase, orderBy)

    override fun removeItem(model: Link)
    {
        repository.delete(model.id)
        categoriesAdapter.deleteItemWithCategory(model)
    }

    override fun setItemFavourite(model: Link)
    {
        model.isFavorite = !model.isFavorite
        repository.update(model, FAV_UPDATE)
    }

    override fun setItemRecent(model: Link)
    {
        model.lastUsed = DateTimeHelper.getCurrentDateTimeStamp()
        repository.update(model, RECENT_UPDATE)
    }

    override fun attachDataObserver(o: Observer) = repository.addObserver(o)

    override fun detachDataObserver(o: Observer) = repository.deleteObserver(o)

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