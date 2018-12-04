package studios.aestheticapps.linker

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.view.PagerAdapter
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header.view.*
import studios.aestheticapps.linker.content.SearchCallback
import studios.aestheticapps.linker.content.addedit.AddEditFragment
import studios.aestheticapps.linker.content.addedit.AddEditFragment.Companion.MODE_ADD
import studios.aestheticapps.linker.content.addedit.AddEditFragment.Companion.MODE_EDIT
import studios.aestheticapps.linker.content.home.HomeFragment
import studios.aestheticapps.linker.content.library.LibraryFragment
import studios.aestheticapps.linker.content.library.LibraryFragment.Companion.TAG_PHRASE
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
    NavigationView.OnNavigationItemSelectedListener,
    AddEditFragment.AddEditCallback,
    SearchCallback
{
    override var presenter: MainContract.Presenter = MainPresenter(this)

    private lateinit var viewPagerAdapter: ScreenSlidePagerAdapter
    private lateinit var auth: FirebaseAuth

    private var currentUser: FirebaseUser? = null
    private var tagPhrase: String = ""

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        setUpBottomNavigation()
        setUpViewPager()
        setUpDrawer()

        authenticateUserAndFetchAccountSettings()

        when
        {
            intent?.action == Intent.ACTION_SEND ->
            {
                if ("text/plain" == intent.type) handleSendText(intent)
            }

            else -> {}
        }

    }

    override fun onResume()
    {
        super.onResume()
        if(isBubbleServiceRunning()) closeBubbles()
    }

    override fun onBackPressed()
    {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else
        {
            super.onBackPressed()
        }
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

            android.R.id.home ->
            {
                drawerLayout.openDrawer(GravityCompat.START)
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

            R.id.nav_settings ->
            {
                item.isChecked = true
                drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }

            R.id.nav_logout ->
            {
                item.isChecked = true
                drawerLayout.closeDrawer(GravityCompat.START)
                signOut()
                return true
            }
        }

        return false
    }

    override fun setUpViewPager()
    {
        viewPagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        viewPager.adapter = viewPagerAdapter
        viewPager.pagingEnabled = false
        viewPager.currentItem = getLastVisitedPage()
    }

    override fun setUpBottomNavigation()
    {
        bottomNavigation.setOnNavigationItemSelectedListener(this)
    }

    override fun setUpDrawer()
    {
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)
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

    override fun createLibraryView(): LibraryFragment
    {
        val libraryView = LibraryFragment()

        return if (tagPhrase.isNotBlank())
        {
            libraryView.arguments = Bundle()
            libraryView.arguments!!.putString(TAG_PHRASE, tagPhrase)
            tagPhrase = ""

            libraryView
        }
        else
        {
            libraryView
        }
    }

    override fun returnToMainView()
    {
        viewPager.currentItem = LIBRARY
    }

    override fun onOpenSearchView(phrase: String)
    {
        tagPhrase = phrase
        viewPagerAdapter.notifyDataSetChanged()

        viewPager.setCurrentItem(LIBRARY, true)
    }

    private fun authenticateUserAndFetchAccountSettings()
    {
        auth = FirebaseAuth.getInstance()
        val headerView = navView.getHeaderView(0)

        currentUser = auth.currentUser
        currentUser?.let {
            headerView.usernameTv.text = currentUser!!.displayName
            headerView.userEmailTv.text = currentUser!!.email

            Picasso.get()
                .load(currentUser!!.photoUrl)
                .resize(200, 200)
                .into(headerView.userIv)
        }
    }

    private fun signOut()
    {
        // Firebase sign out
        auth.signOut()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut().addOnCompleteListener(this) {
            launchLoginActivity()
        }
    }

    private fun handleSendText(intent: Intent)
    {
        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
            //TODO LinkValidators
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

    private fun launchLoginActivity()
    {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm)
    {
        override fun getCount(): Int = PAGES_COUNT

        override fun getItem(position: Int): Fragment?
        {
            return when (position)
            {
                ADD_EDIT -> createViewFromModel()
                HOME -> HomeFragment()
                LIBRARY -> createLibraryView()
                else -> null
            }
        }

        override fun getItemPosition(`object`: Any) = PagerAdapter.POSITION_NONE
    }

    companion object
    {
        private const val TAG = "MainActivity"
        private const val LAST_PAGE = "last_visited_page"

        private const val ADD_EDIT = 0
        private const val HOME = 1
        private const val LIBRARY = 2
        private const val PAGES_COUNT = 3

        const val MY_PERMISSIONS_REQUEST_DRAW_OVERLAY = 0
    }
}
