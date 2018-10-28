package studios.aestheticapps.linker.addedit

class AddEditPresenter(val addEditView: AddEditTaskContract.View) : AddEditTaskContract.Presenter
{
    init
    {
        addEditView.setPresenter(this)
    }

    override fun start() {}
}