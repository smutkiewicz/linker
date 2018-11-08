package studios.aestheticapps.linker.content.details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.content_details.*
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.adapters.TagAdapter
import studios.aestheticapps.linker.content.addedit.DetailsContract
import studios.aestheticapps.linker.content.addedit.DetailsPresenter
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.model.Link.CREATOR.PARCEL_LINK

class DetailsActivity : AppCompatActivity(), DetailsContract.View
{
    override var presenter: DetailsContract.Presenter = DetailsPresenter(this)
    private lateinit var model: Link

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out)

        model = intent.getParcelableExtra(PARCEL_LINK)

        createViewFromModel()
        createTagRecyclerView()
        createFab()
    }

    override fun createViewFromModel()
    {
        detailsTitleTv.text = model.title
        detailsUrlTv.text = model.url
        detailsDescrTv.text = model.description
        detailsGoToUrlCv.setOnClickListener{
            createInternetIntent()
        }
    }

    override fun createTagRecyclerView()
    {
        val tagAdapter = TagAdapter()
        tagAdapter.elements = model.stringToTags()

        detailsTagRv.adapter = tagAdapter
        detailsTagRv.layoutManager = StaggeredGridLayoutManager(
            resources.getInteger(R.integer.tags_column_count),
            StaggeredGridLayoutManager.VERTICAL
        )
    }

    override fun createFab()
    {
        shareFab.setOnClickListener{
            //TODO Sharer
        }
    }

    fun createInternetIntent()
    {
        detailsGoToUrlCv.setOnClickListener{
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(model.url)
            startActivity(i)
        }
    }
}
