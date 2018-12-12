package studios.aestheticapps.linker.model

import android.os.AsyncTask
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import studios.aestheticapps.linker.utils.DateTimeHelper
import java.net.URL

/**
 * Assumes that gets a VALID url.
 */
class LinkMetadataFormatter
{
    fun obtainMetadataFromAsync(url: String) = GetFromUrlAsyncTask().execute(url).get()

    fun obtainMetadataFromAsync(model: Link) = GetFromModelAsyncTask().execute(model).get()

    /**
     * Model is edited by user and/or has atributes already parsed by obtainMetadataFrom(url).
     * Threfore, check only user-ineditable fields.
     */
    private fun obtainMetadataFrom(model: Link): Link?
    {
        if (model.domain.isEmpty()) model.domain = getDomainFrom(model.url)
        if (model.category == UNKNOWN) model.category = getCategoryByDomain(model.domain)

        // Connect to the website
        val doc = Jsoup.connect(model.url).get()

        if (doc != null)
        {
            if (model.imageUrl.isEmpty()) model.imageUrl = getFaviconUrlFrom(doc)
        }

        return model
    }

    /**
     * Model is empty - we have recently fetched url from Clipboard.
     * Automatize process of filling as many fields as possible.
     */
    private fun obtainMetadataFrom(url: String): Link?
    {
        val model = Link(title = "", url = url, domain = "")

        // Connect to the website
        val doc = Jsoup.connect(url).get()

        if (doc != null)
        {
            model.title = getTitleFrom(doc)
            model.domain = getDomainFrom(url)
            model.description = getDescriptionFrom(doc)
            model.imageUrl = getFaviconUrlFrom(doc)
            model.lastUsed = DateTimeHelper.getCurrentDateTimeStamp()
            model.category = getCategoryByDomain(model.domain)
            model.tags = getTagsFrom(model)
        }
        else
        {
            return null
        }

        return model
    }

    private fun getTitleFrom(doc: Document) = doc.title()?: EMPTY

    private fun getDomainFrom(url: String) = URL(url).host.toString().replace("www.", "")

    private fun getDescriptionFrom(doc: Document): String
    {
        return doc.select("meta[name=description]").first()?.attr("content")?: EMPTY
    }

    private fun getImageUrlFrom(doc: Document): String
    {
        val img = doc.select("img").first()
        return img.absUrl("src")?: getFaviconUrlFrom(doc)
    }

    private fun getFaviconUrlFrom(doc: Document): String
    {
        val icon = doc.head().select("model[href~=.*\\.ico]").first()
        return icon?.attr("href")?: DEFAULT_IMAGE_URL
    }

    private fun getCategoryByDomain(domain: String): String
    {
        // TODO recognizing categories
        return "Science/Education"
    }

    //return "${model.domain};${model.category};"
    private fun getTagsFrom(model: Link): String
    {
        return Link.listOfTagsToString(mutableListOf(
            model.domain,
            model.category
        ))
    }

    private inner class GetFromUrlAsyncTask : AsyncTask<String, Void, Link?>()
    {
        override fun doInBackground(vararg args: String): Link?
        {
            val url = args[0]
            return obtainMetadataFrom(url)
        }
    }

    private inner class GetFromModelAsyncTask : AsyncTask<Link, Void, Link?>()
    {
        override fun doInBackground(vararg args: Link): Link?
        {
            val model = args[0]
            return obtainMetadataFrom(model)
        }
    }

    private companion object
    {
        const val UNKNOWN = "Unknown"
        const val EMPTY = ""
        const val DEFAULT_IMAGE_URL = "https://www.google.com/favicon.ico"
    }
}