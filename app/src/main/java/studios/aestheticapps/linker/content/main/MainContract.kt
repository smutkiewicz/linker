package studios.aestheticapps.linker.content.main

import studios.aestheticapps.linker.BasePresenter
import studios.aestheticapps.linker.BaseView
import studios.aestheticapps.linker.content.addedit.AddEditFragment
import studios.aestheticapps.linker.content.library.LibraryFragment

interface MainContract
{
    interface View : BaseView<Presenter>
    {
        fun setUpViewPager()
        fun setUpBottomNavigation()
        fun goToEditViewIfNeeded()
        fun createViewFromModel(): AddEditFragment

        fun openBubbles()
        fun closeBubbles()

        fun initThemeManager()
        fun createLibraryView(): LibraryFragment
    }

    interface Presenter : BasePresenter
}