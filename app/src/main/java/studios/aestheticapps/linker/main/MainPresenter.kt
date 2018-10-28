package studios.aestheticapps.linker.main

class MainPresenter(val mainView: MainContract.View) : MainContract.Presenter
{
    init
    {
        mainView.presenter = this
    }

    override fun start() {}
}