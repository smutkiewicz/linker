package studios.aestheticapps.linker.content

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.FragmentManager
import studios.aestheticapps.linker.LoginActivity
import studios.aestheticapps.linker.MainActivity
import studios.aestheticapps.linker.SettingsActivity
import studios.aestheticapps.linker.WelcomeActivity
import studios.aestheticapps.linker.content.addedit.EditActivity
import studios.aestheticapps.linker.content.categories.CategoriesChangedCallback
import studios.aestheticapps.linker.content.categories.CategoriesDialogFragment
import studios.aestheticapps.linker.content.details.DetailsDialogFragment
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.model.Link.CREATOR.PARCEL_LINK

object IntentActionHelper
{
    private const val SHARE_INTENT_TITLE = "Share Link to..."

    fun startInternetAction(context: Context, model: Link)
    {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            addFlags(FLAG_ACTIVITY_NEW_TASK)
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
        val intent = Intent(android.content.Intent.ACTION_SEND)
        intent.apply {
            type = "text/plain"
            addFlags(FLAG_ACTIVITY_NEW_TASK)

            putExtra(Intent.EXTRA_SUBJECT, model.title)
            putExtra(Intent.EXTRA_TEXT, model.url)
        }

        context.startActivity(Intent.createChooser(intent, SHARE_INTENT_TITLE).addFlags(FLAG_ACTIVITY_NEW_TASK))
    }

    fun startEditView(context: Context, model: Link)
    {
        val intent = Intent(context, EditActivity::class.java).apply {
            addFlags(FLAG_ACTIVITY_NEW_TASK)
            putExtra(PARCEL_LINK, model)
        }
        
        context.startActivity(intent)
    }

    fun startCategoriesDialogAction(manager: FragmentManager, callback: CategoriesChangedCallback)
        = CategoriesDialogFragment.newInstance(callback).show(manager, "Categories")

    fun startSettingsAction(context: Context) = startActivityFrom(context, SettingsActivity::class.java)

    fun startLoginView(context: Context) = startActivityFrom(context, LoginActivity::class.java)

    fun startWelcomeView(context: Context) = startActivityFrom(context, WelcomeActivity::class.java)

    fun startMainView(context: Context) = startActivityFrom(context, MainActivity::class.java)

    private fun startActivityFrom(context: Context, clazz: Class<*>)
    {
        val intent = Intent(context, clazz)
        context.startActivity(intent)
    }
}