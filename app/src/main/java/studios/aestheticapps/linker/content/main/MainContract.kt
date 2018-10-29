package studios.aestheticapps.linker.content.main

import studios.aestheticapps.linker.BasePresenter
import studios.aestheticapps.linker.BaseView

interface MainContract
{
    interface View : BaseView<Presenter>
    {
        fun setUpViewPager()
        fun setUpBottomNavigation()

        fun openBubbles()
        fun closeBubbles()

        fun initThemeManager()
    }

    interface Presenter : BasePresenter
}