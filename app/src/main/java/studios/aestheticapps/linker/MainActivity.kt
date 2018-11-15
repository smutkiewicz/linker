package studios.aestheticapps.linker

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import studios.aestheticapps.linker.content.addedit.AddEditFragment
import studios.aestheticapps.linker.content.addedit.AddEditFragment.Companion.MODE_ADD
import studios.aestheticapps.linker.content.addedit.AddEditFragment.Companion.MODE_EDIT
import studios.aestheticapps.linker.content.home.HomeFragment
import studios.aestheticapps.linker.content.library.LibraryFragment
import studios.aestheticapps.linker.content.main.MainContract
import studios.aestheticapps.linker.content.main.MainPresenter
import studios.aestheticapps.linker.extensions.checkForDrawOverlaysPermissions
import studios.aestheticapps.linker.extensions.createDrawOverlayPermissionsIntent
import studios.aestheticapps.linker.floatingmenu.BubbleMenuService
import studios.aestheticapps.linker.floatingmenu.theme.BubbleTheme
import studios.aestheticapps.linker.floatingmenu.theme.BubbleThemeManager
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.model.Link.CREATOR.PARCEL_LINK

class MainActivity : AppCompatActivity(),
    MainContract.View,
    BottomNavigationView.OnNavigationItemSelectedListener,
    AddEditFragment.AddEditCallback
{
    override var presenter: MainContract.Presenter = MainPresenter(this)
    var menuState = SHOW_MENU

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out)

        setUpBottomNavigation()
        setUpViewPager()
    }

    override fun onResume()
    {
        super.onResume()
        if(isBubbleServiceRunning()) closeBubbles()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        menuInflater.inflate(R.menu.menu_main, menu)

        if (menuState == HIDE_MENU)
        {
            for (i in 0 until menu.size())
                menu.getItem(i).isVisible = false
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        return when (item.itemId)
        {
            R.id.action_bubbles ->
            {
                openBubbles()
                true
            }

            R.id.action_search ->
            {
                viewPager.currentItem = LIBRARY
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean
    {
        when (item.itemId)
        {
            R.id.action_add ->
            {
                setPageAsLastVisited(ADD_EDIT)
                viewPager.currentItem = ADD_EDIT
                return true
            }

            R.id.action_home ->
            {
                setPageAsLastVisited(HOME)
                viewPager.currentItem = HOME
                return true
            }

            R.id.action_library ->
            {
                setPageAsLastVisited(LIBRARY)
                viewPager.currentItem = LIBRARY
                return true
            }
        }

        return false
    }

    override fun setUpViewPager()
    {
        val adapter = ScreenSlidePagerAdapter(supportFragmentManager)
        viewPager.adapter = adapter
        viewPager.pagingEnabled = false
        viewPager.currentItem = getLastVisitedPage()
    }

    override fun setUpBottomNavigation()
    {
        bottomNavigation.setOnNavigationItemSelectedListener(this)
    }

    override fun openBubbles()
    {
        if(checkForDrawOverlaysPermissions())
        {
            initThemeManager()
            BubbleMenuService.showFloatingMenu(this@MainActivity)
            finish()
        }
        else
        {
            createDrawOverlayPermissionsIntent()
        }
    }

    override fun closeBubbles()
    {
        BubbleMenuService.destroyFloatingMenu(this@MainActivity)
    }

    override fun initThemeManager()
    {
        val defaultTheme = BubbleTheme(
            ContextCompat.getColor(this, R.color.colorPrimary),
            ContextCompat.getColor(this, R.color.colorPrimary)
        )

        BubbleThemeManager.init(defaultTheme)
    }

    override fun returnToMainView()
    {
        menuState = SHOW_MENU
        invalidateOptionsMenu()
        bottomNavigation.visibility = VISIBLE
        viewPager.currentItem = HOME
    }

    override fun prepareEditView()
    {
        menuState = HIDE_MENU
        invalidateOptionsMenu()
        bottomNavigation.visibility = GONE
    }

    override fun prepareAddView()
    {
        bottomNavigation.visibility = VISIBLE
    }

    override fun goToEditViewIfNeeded()
    {
        if (intent.hasExtra(PARCEL_LINK)) viewPager.currentItem = ADD_EDIT
        else viewPager.currentItem = HOME
    }

    override fun createViewFromModel(): AddEditFragment
    {
        return if (intent.hasExtra(PARCEL_LINK))
        {
            val model = intent.getParcelableExtra<Link>(PARCEL_LINK)
            val myFragment = AddEditFragment.newInstance(MODE_EDIT)
            myFragment.arguments!!.putParcelable(PARCEL_LINK, model)

            myFragment
        }
        else
        {
            AddEditFragment.newInstance(MODE_ADD)
        }
    }

    private fun isBubbleServiceRunning(): Boolean
    {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (BubbleMenuService::class.java.name == service.service.className) return true
        }

        return false
    }

    private fun setPageAsLastVisited(currentPage: Int)
    {
        getPreferences(Context.MODE_PRIVATE)
            .edit()
            .putInt(LAST_PAGE, currentPage)
            .apply()
    }

    private fun getLastVisitedPage() = getPreferences(Context.MODE_PRIVATE).getInt(LAST_PAGE, HOME)

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm)
    {
        override fun getCount(): Int = PAGES_COUNT

        override fun getItem(position: Int): Fragment?
        {
            return when (position)
            {
                ADD_EDIT -> createViewFromModel()
                HOME -> HomeFragment()
                LIBRARY -> LibraryFragment()
                else -> null
            }
        }
    }

    companion object
    {
        private const val TAG = "MainActivity"
        private const val LAST_PAGE = "last_visited_page"

        private const val ADD_EDIT = 0
        private const val HOME = 1
        private const val LIBRARY = 2
        private const val PAGES_COUNT = 3

        private const val SHOW_MENU = 4
        private const val HIDE_MENU = 5

        const val MY_PERMISSIONS_REQUEST_DRAW_OVERLAY = 0
    }
}
