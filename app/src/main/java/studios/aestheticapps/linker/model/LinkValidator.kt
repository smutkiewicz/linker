package studios.aestheticapps.linker.model

import java.net.URL

class LinkValidator
{
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
            //czy wystepuja spacje, usun jesli tak
            //czy wystepuja kropki

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

    private fun beginsWithValidProtocol(url: String): Boolean
    {
        return url.startsWith("https://") or url.startsWith("http://")
    }

    private fun beginsWithValidWww(url: String): Boolean
    {
        return url.startsWith("www.")
    }

    private fun addProtocolPrefixAndWww(url: String): String
    {
        var processedUrl = url

        processedUrl = "http://www.$processedUrl"
        return processedUrl
    }

    private fun addProtocolPrefix(url: String): String
    {
        var processedUrl = url

        processedUrl = "http://$processedUrl"
        return processedUrl
    }

    private companion object
    {
        const val EMPTY_URL = ""
    }
}