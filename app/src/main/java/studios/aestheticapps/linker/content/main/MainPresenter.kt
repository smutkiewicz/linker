package studios.aestheticapps.linker.content.main

class MainPresenter(val mainView: MainContract.View) : MainContract.Presenter
{
    init
    {
        mainView.presenter = this
    }

    override fun start() {}

    override fun stop() {}
}