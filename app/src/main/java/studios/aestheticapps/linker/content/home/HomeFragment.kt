package studios.aestheticapps.linker.content.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.content_home.*
import studios.aestheticapps.linker.MainActivity
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.adapters.FavoritesAdapter
import studios.aestheticapps.linker.adapters.RecentLinkAdapter
import studios.aestheticapps.linker.floatingmenu.BubbleMenuService
import studios.aestheticapps.linker.model.Link

class HomeFragment : Fragment(), HomeContract.View, FavoritesAdapter.OnItemClickListener
{
    override var presenter: HomeContract.Presenter = HomePresenter(this)

    private lateinit var recentLinkAdapter: RecentLinkAdapter
    private lateinit var favLinkAdapter: FavoritesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
        = inflater.inflate(R.layout.content_home, container, false)

    override fun onStart()
    {
        super.onStart()
        presenter.start(activity!!.application)

        setUpRecentRecyclerView()
        setUpFavoritesRecyclerView()
    }

    override fun onDestroy()
    {
        super.onDestroy()
        presenter.stop()
    }

    override fun hideBubbles()
    {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        ContextCompat.startActivity(context!!, intent, null)

        BubbleMenuService.destroyFloatingMenu(context!!)
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

    override fun setUpFavoritesRecyclerView()
    {
        favLinkAdapter = FavoritesAdapter(this)
        favLinkAdapter.elements = presenter.getFavoriteItems()

        favRecyclerView.apply {
            adapter = favLinkAdapter
            isNestedScrollingEnabled = false
            layoutManager = GridLayoutManager(
                context,
                resources.getInteger(R.integer.favs_column_count)
            )
        }
    }

    override fun hideKeyboardFrom(view: View)
    {
        val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onItemClicked(link: Link)
    {
        //TODO Fire browser
    }

    override fun onItemLongClicked(link: Link)
    {
        //TODO Fire DetailsView
    }

    override fun onShare(link: Link)
    {
        //TODO Share
    }
}