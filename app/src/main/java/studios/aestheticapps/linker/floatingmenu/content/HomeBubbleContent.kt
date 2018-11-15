package studios.aestheticapps.linker.floatingmenu.content

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import io.mattcarroll.hover.Content
import kotlinx.android.synthetic.main.content_home.view.*
import studios.aestheticapps.linker.MainActivity
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.adapters.RecentLinkAdapter
import studios.aestheticapps.linker.content.home.HomeContract
import studios.aestheticapps.linker.content.home.HomePresenter
import studios.aestheticapps.linker.floatingmenu.BubbleMenuService

class HomeBubbleContent(context: Context,
                        application: Application,
                        private val callback: BubbleContentCallback) : FrameLayout(context), Content, HomeContract.View
{
    override var presenter: HomeContract.Presenter = HomePresenter(this)

    private lateinit var recentLinkAdapter: RecentLinkAdapter

    init
    {
        LayoutInflater.from(context).inflate(R.layout.content_home, this, true)
        presenter.start(application)

        setUpRecentRecyclerView()
    }

    override fun getView() = this

    override fun isFullscreen() = true

    override fun onShown() {}

    override fun onHidden() {}

    override fun hideBubbles()
    {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(context, intent, null)

        BubbleMenuService.destroyFloatingMenu(context)
    }

    override fun setUpRecentRecyclerView()
    {
        val horizontalLayoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        recentLinkAdapter = RecentLinkAdapter()
        recentLinkAdapter.elements = presenter.getRecentItems()

        recentRecyclerView.apply {
            layoutManager = horizontalLayoutManager
            adapter = recentLinkAdapter
        }
    }

    override fun hideKeyboardFrom(view: View)
    {
        val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun setUpFavoritesRecyclerView()
    {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}