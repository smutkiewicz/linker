package studios.aestheticapps.linker.content.addedit

import android.app.Application
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.persistence.LinkRepository
import java.text.SimpleDateFormat
import java.util.*

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

    override fun updateItem(link: Link) = repository.update(link)

    override fun parseDomain(url: String) = "github.com"

    override fun tagsToString(elements: MutableList<String>) = Link.listOfTagsToString(elements)

    override fun getCurrentDateTimeStamp(): String
    {
        val formatter = SimpleDateFormat(DATE_TIME_FORMAT)
        val date = Date()

        return formatter.format(date)
    }

    private companion object
    {
        const val DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss"
    }
}