package studios.aestheticapps.linker.content.addedit

import android.app.Application
import studios.aestheticapps.linker.BasePresenter
import studios.aestheticapps.linker.BaseView
import studios.aestheticapps.linker.model.Link

interface AddEditTaskContract
{
    interface View : BaseView<Presenter>
    {
        fun obtainModelFromArguments()
        fun createFab()
        fun cleanView()
        fun createTagBtn()
        fun createCategoriesSpinner()
        fun createViewFromModel()
        fun createTagRecyclerView()
        fun createEditTexts()
        fun createSpinner()
        fun addTag()
        fun buildItem(): Link
        fun mapModelToView()
        fun buildSampleModelFromClipboardContent()
    }

    interface Presenter : BasePresenter
    {
        fun start(application: Application)
        fun parseDomain(url: String): String
        fun saveItem(link: Link)
        fun updateItem(link: Link)

        fun tagsToString(elements: MutableList<String>): String
        fun getCurrentDateTimeStamp(): String
        fun buildItemFromUrl(url: String): Link
    }
}