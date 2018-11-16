package studios.aestheticapps.linker.content.details

import studios.aestheticapps.linker.persistence.LinkRepository

class DetailsPresenter(val view: DetailsContract.View) : DetailsContract.Presenter
{
    private lateinit var repository: LinkRepository

    init
    {
        view.presenter = this
    }

    override fun start() {}

    override fun stop() {}
}