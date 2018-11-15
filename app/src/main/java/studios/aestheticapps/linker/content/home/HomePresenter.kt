package studios.aestheticapps.linker.content.home

import android.app.Application
import studios.aestheticapps.linker.persistence.LinkRepository
import studios.aestheticapps.linker.persistence.LinkRepository.Companion.FAVORITES
import studios.aestheticapps.linker.persistence.LinkRepository.Companion.RECENT

class HomePresenter(val view: HomeContract.View) : HomeContract.Presenter
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

    override fun start() {}

    override fun stop() {}
}