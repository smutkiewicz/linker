package studios.aestheticapps.linker.content.home

import android.app.Application
import studios.aestheticapps.linker.adapters.OnItemClickListener
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.persistence.LinkRepository
import studios.aestheticapps.linker.persistence.LinkRepository.Companion.FAVORITES
import studios.aestheticapps.linker.persistence.LinkRepository.Companion.RECENT
import java.text.SimpleDateFormat
import java.util.*

class HomePresenter(val view: HomeContract.View) : HomeContract.Presenter, OnItemClickListener
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

    override fun getRecentItems() = repository.getListOf(RECENT)

    override fun getFavoriteItems() = repository.getListOf(FAVORITES)

    override fun getTagsCloudItems(): LinkedList<String>
    {
        val tagCloudItems = LinkedList<String>()
        val recentList = repository.getListOf(RECENT)
        val recentLastIndex = recentList.lastIndex
        recentList.shuffle()

        val max = when
        {
            recentLastIndex < MAX_TAGS -> recentLastIndex
            else -> MAX_TAGS
        }

        for (index in 0..max)
        {
            val chosenItem = recentList[index]
            val itemsTags = chosenItem.stringToListOfTags()

            tagCloudItems.addAll(itemsTags)
        }

        return tagCloudItems
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

    private fun getCurrentTime(): String
    {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        return formatter.format(Date())
    }

    private companion object
    {
        const val MAX_TAGS = 6
    }
}