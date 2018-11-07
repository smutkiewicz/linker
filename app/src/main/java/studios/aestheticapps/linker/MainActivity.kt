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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import studios.aestheticapps.linker.content.addedit.AddEditFragment
import studios.aestheticapps.linker.content.home.HomeFragment
import studios.aestheticapps.linker.content.library.LibraryFragment
import studios.aestheticapps.linker.content.main.MainContract
import studios.aestheticapps.linker.content.main.MainPresenter
import studios.aestheticapps.linker.extensions.checkForDrawOverlaysPermissions
import studios.aestheticapps.linker.extensions.createDrawOverlayPermissionsIntent
import studios.aestheticapps.linker.floatingmenu.BubbleMenuService
import studios.aestheticapps.linker.floatingmenu.theme.BubbleTheme
import studios.aestheticapps.linker.floatingmenu.theme.BubbleThemeManager

class MainActivity : AppCompatActivity(),
    MainContract.View,
    BottomNavigationView.OnNavigationItemSelectedListener,
    AddEditFragment.AddEditCallback
{
    override var presenter: MainContract.Presenter = MainPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

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
                viewPager.currentItem = ADD_EDIT
                return true
            }

            R.id.action_home ->
            {
                viewPager.currentItem = HOME
                return true
            }

            R.id.action_library ->
            {
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
        viewPager.currentItem = HOME
        viewPager.pagingEnabled = false
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
        viewPager.currentItem = HOME
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

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm)
    {
        override fun getCount(): Int = PAGES_COUNT

        override fun getItem(position: Int): Fragment?
        {
            return when (position)
            {
                0 -> AddEditFragment()
                1 -> HomeFragment()
                2 -> LibraryFragment()
                else -> null
            }
        }
    }

    companion object
    {
        private const val TAG = "MainActivity"
        private const val ADD_EDIT = 0
        private const val HOME = 1
        private const val LIBRARY = 2
        private const val PAGES_COUNT = 3
        const val MY_PERMISSIONS_REQUEST_DRAW_OVERLAY = 0
    }
}
