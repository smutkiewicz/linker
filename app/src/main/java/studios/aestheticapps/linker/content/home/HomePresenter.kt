package studios.aestheticapps.linker.content.home

import android.app.Application
import studios.aestheticapps.linker.adapters.OnMyAdapterItemClickListener
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.persistence.link.LinkRepository
import studios.aestheticapps.linker.persistence.link.LinkRepository.Companion.FAVORITES
import studios.aestheticapps.linker.persistence.link.LinkRepository.Companion.RECENT
import studios.aestheticapps.linker.persistence.link.LinkRepository.Companion.RECENT_UPDATE
import studios.aestheticapps.linker.utils.DateTimeHelper
import java.util.*

class HomePresenter(val view: HomeContract.View) : HomeContract.Presenter, OnMyAdapterItemClickListener
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
        val tagCloudSet = HashSet<String>()
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

            for (tag in itemsTags)
            {
                if (!tagCloudSet.contains(tag))
                    tagCloudSet.add(tag)
            }
        }

        return LinkedList(tagCloudSet)
    }

    override fun setItemFavourite(model: Link)
    {
        model.isFavorite = !model.isFavorite
        repository.update(model)
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

    override fun onCopy(content: String) = view.startCopyAction(content)

    private companion object
    {
        const val MAX_TAGS = 10
    }
}