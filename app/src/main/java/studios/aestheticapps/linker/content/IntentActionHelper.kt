package studios.aestheticapps.linker.content

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.FragmentManager
import studios.aestheticapps.linker.content.details.DetailsDialogFragment
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.model.Link.CREATOR.PARCEL_LINK

object IntentActionHelper
{
    fun startInternetAction(context: Context, link: Link)
    {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(link.url)
        context.startActivity(intent)
    }

    fun startDetailsAction(manager: FragmentManager, link: Link)
    {
        val detailsDialog = DetailsDialogFragment()
        val args = Bundle()
        args.putParcelable(PARCEL_LINK, link)


        detailsDialog.apply {
            arguments = args
            show(manager, "Details")
        }
    }

    fun startShareView(context: Context, link: Link)
    {
        //TODO Facebook Share
    }
}