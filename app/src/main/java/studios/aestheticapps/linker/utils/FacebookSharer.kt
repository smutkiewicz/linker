package studios.aestheticapps.linker.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v4.app.Fragment
import android.widget.Toast
import com.facebook.share.model.ShareMessengerGenericTemplateContent
import com.facebook.share.model.ShareMessengerGenericTemplateElement
import com.facebook.share.model.ShareMessengerURLActionButton
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.model.Link

class FacebookSharer(private val context: Context)
{
    fun share(link: Link, fragment: Fragment)
    {
        val actionButton = ShareMessengerURLActionButton.Builder()
            .setTitle(link.title)
            .setUrl(Uri.parse(link.url))
            .build()

        val genericTemplateElement = ShareMessengerGenericTemplateElement.Builder()
            .setTitle(link.title)
            .setSubtitle(link.domain)
            .setButton(actionButton)
            .build()

        val genericTemplateContent = ShareMessengerGenericTemplateContent.Builder()
            .setPageId(getAppId())
            .setGenericTemplateElement(genericTemplateElement)
            .build()

        /*if (MessageDialog.canShow(genericTemplateContent)) {
            MessageDialog.show(fragment, genericTemplateContent)
        }*/
    }

    fun shareViaIntent(link: Link)
    {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, "<---YOUR TEXT HERE--->.")
        sendIntent.type = "text/plain"
        sendIntent.setPackage("com.facebook.orca")

        try
        {
            context.startActivity(sendIntent)
        }
        catch (ex: ActivityNotFoundException)
        {
            Toast.makeText(context, "Please Install Facebook Messenger", Toast.LENGTH_LONG).show()
        }
    }

    private fun getAppId() = context.resources.getString(R.string.search_menu_title)
}