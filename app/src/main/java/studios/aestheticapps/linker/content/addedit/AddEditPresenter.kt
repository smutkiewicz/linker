package studios.aestheticapps.linker.content.addedit

import android.app.Application
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.model.LinkMetadataFormatter
import studios.aestheticapps.linker.model.LinkValidator
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

    override fun saveItem(model: Link)
    {
        val formatter = LinkMetadataFormatter()
        val modelWithMetadata = formatter.obtainMetadata(model)

        repository.insert(modelWithMetadata)
    }

    override fun updateItem(model: Link) = repository.update(model)

    override fun tagsToString(elements: MutableList<String>) = Link.listOfTagsToString(elements)

    /**
     * Guarantee a valid model, or null.
     */
    override fun buildItemFromUrl(url: String): Link?
    {
        val validator = LinkValidator(url)
        return validator.build()
    }

    override fun provideValidUrl(url: String): String
    {
        val validator = LinkValidator(url)
        return validator.repair(url)
    }
}