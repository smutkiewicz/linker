package studios.aestheticapps.linker.content.details

import android.app.Application
import studios.aestheticapps.linker.adapters.OnMyAdapterItemClickListener
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.persistence.LinkRepository
import studios.aestheticapps.linker.utils.DateTimeHelper

class DetailsPresenter(val view: DetailsContract.View) : DetailsContract.Presenter, OnMyAdapterItemClickListener
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

    override fun onEdit(model: Link)
    {
        view.startEditView(model)
    }
}