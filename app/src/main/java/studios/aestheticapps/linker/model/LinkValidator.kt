package studios.aestheticapps.linker.model

import java.net.URL

object LinkValidator
{
    private const val EMPTY_URL = ""

    fun provideValidUrlOrEmpty(url: String): String
    {
        var processedUrl = url

        //check if repairs are necessary
        if (isValid(url))
        {
            return url
        }
        else
        {
            if (!processedUrl.contains("."))
            {
                //it won't be possible to repair link here
                return EMPTY_URL
            }

            processedUrl.replace(" ", "")

            processedUrl = if (beginsWithValidWww(url))
            {
                addProtocolPrefix(processedUrl)
            }
            else
            {
                addProtocolPrefixAndWww(processedUrl)
            }
        }

        //check again AFTER repairs
        return if (isValid(processedUrl))
            processedUrl
        else
            EMPTY_URL
    }

    fun isValid(url: String): Boolean
    {
        return try
        {
            URL(url).toURI()
            true
        }
        catch (e: Exception)
        {
            false
        }
    }

    fun obtainHost(url: String): String
    {
        return if (isValid(url))
            URL(url).toURI().host
        else
            EMPTY_URL
    }

    private fun beginsWithValidWww(url: String) = url.startsWith("www.")

    private fun beginsWithValidProtocol(url: String) = url.startsWith("https://") or url.startsWith("http://")

    private fun addProtocolPrefixAndWww(url: String) = "http://www.$url"

    private fun addProtocolPrefix(url: String) = "http://$url"
}