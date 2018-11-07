package studios.aestheticapps.linker.content.home

import android.app.Application
import studios.aestheticapps.linker.persistence.LinkRepository

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

    override fun getRecentItems() = repository.search("")

    override fun start() {}

    override fun stop() {}
}