package studios.aestheticapps.linker.content.addedit

import android.app.Application
import studios.aestheticapps.linker.BasePresenter
import studios.aestheticapps.linker.BaseView
import studios.aestheticapps.linker.model.Link

interface AddEditTaskContract
{
    interface View : BaseView<Presenter>
    {
        fun createFab()
        fun cleanView()
        fun createTagBtn()
        fun addTag()
        fun buildItem(): Link
    }

    interface Presenter : BasePresenter
    {
        fun start(application: Application)
        fun parseDomain(url: String): String
        fun saveItem(link: Link)

        fun tagsToString(elements: MutableList<String>): String
        fun stringToTags(tagString: String): MutableList<String>
    }
}