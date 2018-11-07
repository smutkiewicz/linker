package studios.aestheticapps.linker.content.addedit

import android.app.Application
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.persistence.LinkRepository

class AddEditPresenter(val view: AddEditTaskContract.View) : AddEditTaskContract.Presenter
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

    override fun start() {}

    override fun stop() {}

    override fun saveItem(link: Link) = repository.insert(link)

    override fun parseDomain(url: String) = "github.com"
}