package studios.aestheticapps.linker.model

import android.os.AsyncTask
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import studios.aestheticapps.linker.model.LinkValidator.Companion.EMPTY_URL
import studios.aestheticapps.linker.utils.DateTimeHelper
import java.net.URL

/**
 * Assumes that gets a VALID url.
 */
class LinkMetadataFormatter(val callback: BuildModelCallback)
{
    fun obtainMetadataFromAsync(url: String)
    {
        if (url != EMPTY_URL)
            GetFromUrlAsyncTask()
                .execute(url)
                .get()
    }

    fun obtainMetadataFromAsync(model: Link)
    {
        if (model.url != EMPTY_URL)
            GetFromModelAsyncTask(executeInsert = true)
                .execute(model)
                .get()
    }

    /**
     * Model is edited by user and/or has atributes already parsed by obtainMetadataFrom(url).
     * Threfore, check only user-ineditable fields.
     */
    private fun obtainMetadataFrom(model: Link): Link?
    {
        if (model.domain.isEmpty()) model.domain = getDomainFrom(model.url)
        if (model.category == UNKNOWN) model.category = getCategoryByDomain(model.domain)
        if (!hasCompatibleImageUrl(model.imageUrl)) model.imageUrl = getGoogleFaviconUrlFrom(model.url)

        return model
    }

    /**
     * Model is empty - we have recently fetched url from Clipboard.
     * Automatize process of filling as many fields as possible.
     */
    private fun obtainMetadataFrom(url: String): Link?
    {
        val model = Link(title = "", url = url, domain = "")

        // Info not jsoup-needed in case that HTTP connection will fail
        model.domain = getDomainFrom(url)
        model.title = model.domain
        model.imageUrl = getGoogleFaviconUrlFrom(url)
        model.lastUsed = DateTimeHelper.getCurrentDateTimeStamp()
        model.category = getCategoryByDomain(model.domain)

        try
        {
            // Try to connect to the website
            val doc = Jsoup.connect(url).get()

            // jsoup-needed fields
            if (doc != null)
            {
                model.title = getTitleFrom(doc)
                model.description = getDescriptionFrom(doc)
                model.tags = getTagsFrom(model)
            }
        }
        catch (e: Exception)
        {
            return model
        }

        return model
    }

    private fun getTitleFrom(doc: Document) = doc.title()?: EMPTY

    private fun getDomainFrom(url: String) = URL(url).host.toString().replace("www.", "")

    private fun getDescriptionFrom(doc: Document) = doc.select("meta[name=description]").first()?.attr("content")?: EMPTY

    private fun getGoogleFaviconUrlFrom(url: String) = GOOGLE_FAVICON_GET + url

    private fun getCategoryByDomain(domain: String): String
    {
        // TODO recognizing categories
        return "Science/Education"
    }

    private fun getFaviconUrlFrom(doc: Document): String
    {
        var iconUrl: String

        val pngElement = doc.head().select("link[href~=.*\\.(ico|png)]").first()
        iconUrl = pngElement?.attr("href")?: DEFAULT_IMAGE_URL

        // Try again for different type of icon
        if (iconUrl == DEFAULT_IMAGE_URL)
        {
            val icoElement = doc.head().select("meta[itemprop=image]").first()
            iconUrl = icoElement?.attr("content")?: DEFAULT_IMAGE_URL
        }

        // Try again for different type of icon
        if (iconUrl == DEFAULT_IMAGE_URL)
        {
            val icoElement = doc.head().select("link[rel=\".(ico|png)\"]").first()
            iconUrl = icoElement?.attr("content")?: DEFAULT_IMAGE_URL
        }

        if (iconUrl == "") iconUrl = DEFAULT_IMAGE_URL

        return iconUrl
    }

    private fun getImageUrlFrom(doc: Document): String
    {
        val img = doc.select("img").first()
        return img.absUrl("src")?: getFaviconUrlFrom(doc)
    }

    private fun getTagsFrom(model: Link): String
    {
        return Link.listOfTagsToString(mutableListOf(
            model.domain,
            model.category
        ))
    }

    private inner class GetFromUrlAsyncTask : AsyncTask<String, Void, Link?>()
    {
        override fun doInBackground(vararg args: String): Link? = obtainMetadataFrom(url = args[0])

        override fun onPostExecute(result: Link?)
        {
            super.onPostExecute(result)
            callback.mapModelToView(result)
            callback.setNewModel(result)
        }
    }

    private inner class GetFromModelAsyncTask(val executeInsert: Boolean) : AsyncTask<Link, Void, Link?>()
    {
        override fun doInBackground(vararg args: Link): Link? = obtainMetadataFrom(model = args[0])

        override fun onPostExecute(result: Link?)
        {
            super.onPostExecute(result)
            if (executeInsert)
                callback.insertSavedModel(result)
        }
    }

    interface BuildModelCallback
    {
        fun mapModelToView(model: Link?)
        fun setNewModel(modelFetchedAsync: Link?)
        fun insertSavedModel(result: Link?)
    }

    companion object
    {
        private const val UNKNOWN = "Unknown"
        private const val GOOGLE_FAVICON_GET = "https://s2.googleusercontent.com/s2/favicons?domain_url="

        const val EMPTY = ""
        const val DEFAULT_IMAGE_URL = "https://www.google.com/favicon.ico"

        fun hasCompatibleImageUrl(imageUrl: String) = imageUrl != EMPTY && imageUrl != DEFAULT_IMAGE_URL
    }
}