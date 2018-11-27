package studios.aestheticapps.linker.content.details

import android.app.Application
import studios.aestheticapps.linker.adapters.OnItemClickListener
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.persistence.LinkRepository
import java.text.SimpleDateFormat
import java.util.*

class DetailsPresenter(val view: DetailsContract.View) : DetailsContract.Presenter, OnItemClickListener
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

    override fun onEdit(link: Link)
    {
        view.startEditView(link)
    }

    private fun getCurrentTime(): String
    {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        return formatter.format(Date())
    }
}