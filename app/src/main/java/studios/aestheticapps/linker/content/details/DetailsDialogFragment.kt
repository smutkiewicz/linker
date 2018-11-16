package studios.aestheticapps.linker.content.details

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.View
import studios.aestheticapps.linker.R

class DetailsDialogFragment : DialogFragment()
{
    override fun onCreateDialog(bundle: Bundle?): Dialog
    {
        val builder = AlertDialog.Builder(context!!)
        val detailsDialogView = inflateAndReturnDetailsView()

        findViewsAndAssignThem(detailsDialogView)
        setBuildersViewAndTitle(builder, detailsDialogView)
        setBuildersNegativeGoToWebsiteButton(builder)
        setBuildersEditPositiveButton(builder)

        return builder.create()
    }

    private fun inflateAndReturnDetailsView(): View
    {
        return activity!!.layoutInflater.inflate(R.layout.content_details, null)
    }

    private fun setBuildersViewAndTitle(builder: AlertDialog.Builder, view: View)
    {
        builder.setView(view)
        builder.setTitle("")
    }

    private fun setBuildersNegativeGoToWebsiteButton(builder: AlertDialog.Builder)
    {
        builder.setNegativeButton("details_goto_label") { dialog, id ->  }
    }

    private fun setBuildersEditPositiveButton(builder: AlertDialog.Builder)
    {
        builder.setPositiveButton("details_ok_label") { dialog, id -> dialog.cancel() }
    }

    private fun findViewsAndAssignThem(view: View)
    {

    }
}