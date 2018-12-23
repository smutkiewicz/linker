package studios.aestheticapps.linker.content.addedit

import android.app.Application
import android.widget.ArrayAdapter
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
        fun createButtons()
        fun addTag()
        fun buildItemFromView(): Link
        fun buildSampleModelFromClipboardContent()
        fun buildSampleModelFromIntentContent(content: String)
    }

    interface Presenter : BasePresenter
    {
        fun start(application: Application)
        fun launchItemToSaveMetadataFormatting(model: Link)
        fun saveItem(model: Link)
        fun updateItem(model: Link)

        fun tagsToString(elements: MutableList<String>): String
        fun buildItemFromUrl(url: String, isNetworkAvailable: Boolean)
        fun provideValidUrl(url: String): String
        fun provideArrayAdapter(): ArrayAdapter<String>
    }
}