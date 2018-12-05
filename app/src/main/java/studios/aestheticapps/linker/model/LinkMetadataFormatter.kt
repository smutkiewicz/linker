package studios.aestheticapps.linker.model

import android.os.AsyncTask
import org.jsoup.Jsoup
import java.net.URL


class LinkMetadataFormatter
{
    fun obtainMetadata(model: Link) = GetContentAsyncTask().execute(model).get()

    private class GetContentAsyncTask : AsyncTask<Link, Void, Link>()
    {
        override fun doInBackground(vararg args: Link?): Link?
        {
            val model = args[0]

            model!!.let {

                // Connect to the website
                val doc = Jsoup.connect(model.url).get()

                model.title = doc.title()
                model.domain = URL(model.url).host
                model.description = doc.select("meta[name=description]").first().attr("content")

                // Get image URL
                val img = doc.select("img").first()
                val imgSrc = img.absUrl("src")
                model.imageUrl = imgSrc

            }

            return model
        }
    }
}