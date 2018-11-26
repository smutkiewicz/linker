package studios.aestheticapps.linker.content

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.FragmentManager
import studios.aestheticapps.linker.content.addedit.EditActivity
import studios.aestheticapps.linker.content.details.DetailsDialogFragment
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.model.Link.CREATOR.PARCEL_LINK

object IntentActionHelper
{
    fun startInternetAction(context: Context, model: Link)
    {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(model.url)
        }

        context.startActivity(intent)
    }

    fun startDetailsAction(manager: FragmentManager, model: Link)
    {
        val detailsDialog = DetailsDialogFragment()
        val args = Bundle()
        args.putParcelable(PARCEL_LINK, model)


        detailsDialog.apply {
            arguments = args
            show(manager, "Details")
        }
    }

    fun startShareView(context: Context, model: Link)
    {
        //TODO Facebook Share
    }

    fun startEditView(context: Context, model: Link)
    {
        val intent = Intent(context, EditActivity::class.java).apply {
            putExtra(PARCEL_LINK, model)
        }
        
        context.startActivity(intent)
    }
}