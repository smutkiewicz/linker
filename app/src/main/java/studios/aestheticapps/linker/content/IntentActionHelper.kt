package studios.aestheticapps.linker.content

import android.content.Context
import android.content.Intent
import android.net.Uri
import studios.aestheticapps.linker.content.details.DetailsActivity
import studios.aestheticapps.linker.model.Link

object IntentActionHelper
{
    fun startInternetAction(context: Context, link: Link)
    {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(link.url)
        context.startActivity(intent)
    }

    fun startDetailsAction(context: Context, link: Link)
    {
        val intent = Intent(context, DetailsActivity::class.java)
        intent.putExtra(Link.PARCEL_LINK, link)
        context.startActivity(intent)
    }

    fun startShareView(context: Context, link: Link)
    {
        //TODO Facebook Share
    }
}