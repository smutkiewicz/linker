package studios.aestheticapps.linker.model

import java.net.URL
import java.util.regex.Pattern

class LinkValidator
{
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

    fun provideValidUrlOrEmpty(url: String): String
    {
        var processedUrl = url

        if (isValid(url))
        {
            return url
        }
        else
        {
            if (!beginsWithValidProtocol(url))
            {
                if (beginsWithValidWww(url))
                {
                    processedUrl = addProtocolPrefix(url)
                    return processedUrl
                }
                else
                {
                    processedUrl = addProtocolPrefixAndWww(url)
                    return processedUrl
                }
            }
        }

        return EMPTY_URL
    }

    private fun beginsWithValidProtocol(url: String): Boolean
    {
        return url.startsWith("https://") or url.startsWith("http://")
    }

    private fun beginsWithValidWww(url: String): Boolean
    {
        return url.startsWith("www.")
    }

    private fun addProtocolPrefix(url: String): String
    {
        var processedUrl = url

        val p = Pattern.compile("www")
        val m = p.matcher(processedUrl)

        if (m.find())
        {
            val i = m.start()
            processedUrl = processedUrl.substring(i)
            processedUrl = "http://$processedUrl"

            return processedUrl
        }

        return url
    }

    private companion object
    {
        const val EMPTY_URL = ""
    }
}