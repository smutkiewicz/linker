package studios.aestheticapps.linker.content.addedit

import android.app.Application
import studios.aestheticapps.linker.adapters.CategoriesAdapter
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.model.LinkMetadataFormatter
import studios.aestheticapps.linker.model.LinkValidator
import studios.aestheticapps.linker.model.LinkValidator.Companion.EMPTY_URL
import studios.aestheticapps.linker.persistence.link.LinkRepository

class AddEditPresenter(val view: AddEditTaskContract.View) : AddEditTaskContract.Presenter
{
    private lateinit var repository: LinkRepository
    private lateinit var categoriesAdapter: CategoriesAdapter

    private val formatter: LinkMetadataFormatter

    init
    {
        view.presenter = this
        formatter = LinkMetadataFormatter(view as LinkMetadataFormatter.BuildModelCallback)
    }

    override fun start(application: Application)
    {
        repository = LinkRepository(application)

        // Part of logic responsible for parsing metadata and category
        categoriesAdapter = CategoriesAdapter(application)
        formatter.categoriesAdapter = categoriesAdapter
    }

    override fun launchItemToSaveMetadataFormatting(model: Link) = formatter.obtainMetadataFromAsync(model)

    override fun saveItem(model: Link)
    {
        repository.insert(model)
        categoriesAdapter.insertItemWithCategory(domain = model.domain, categoryName = model.category, id = null)
    }

    override fun updateItem(model: Link)
    {
        categoriesAdapter.editItemWithCategory(model)
        repository.update(model)
    }

    override fun tagsToString(elements: MutableList<String>) = Link.listOfTagsToString(elements)

    /**
     * Guarantee a valid model, or null.
     */
    override fun buildItemFromUrl(url: String, isNetworkAvailable: Boolean)
    {
        val validator = LinkValidator(url)
        val validUrl = validator.build()

        if (validUrl != EMPTY_URL && isNetworkAvailable)
            formatter.obtainMetadataFromAsync(validUrl)
    }

    override fun provideValidUrl(url: String): String
    {
        val validator = LinkValidator(url)
        return validator.build()
    }

    override fun provideArrayAdapter() = categoriesAdapter.obtainAdapter()
}