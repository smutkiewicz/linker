package studios.aestheticapps.linker.content.library

import android.app.Application
import studios.aestheticapps.linker.adapters.OnMyAdapterItemClickListener
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.persistence.link.LinkRepository
import studios.aestheticapps.linker.persistence.link.LinkRepository.Companion.ALL
import studios.aestheticapps.linker.adapters.CategoriesAdapter
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
        categoriesAdapter.deleteCategory(model)
    }

    override fun setItemFavourite(model: Link)
    {
        model.isFavorite = !model.isFavorite
        repository.update(model)
    }

    override fun setItemRecent(model: Link)
    {
        model.lastUsed = DateTimeHelper.getCurrentDateTimeStamp()
        repository.update(model)
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