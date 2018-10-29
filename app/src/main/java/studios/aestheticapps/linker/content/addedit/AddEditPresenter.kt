package studios.aestheticapps.linker.content.addedit

class AddEditPresenter(val addEditView: AddEditTaskContract.View) : AddEditTaskContract.Presenter
{
    init
    {
        addEditView.presenter = this
    }

    override fun start() {}
}