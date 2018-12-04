package studios.aestheticapps.linker.model

import org.apache.commons.validator.routines.UrlValidator
import java.net.URL

class LinkValidator(private var url: String)
{
    fun build(): Link?
    {
        if (isValid())
        {
            return makeModel()
        }
        else
        {
            url = repair(url)

            return if (isValid())
            {
                makeModel()
            }
            else
            {
                null
            }
        }
    }

    fun isValid(): Boolean
    {
        if (url == EMPTY_URL)
            return false

        val urlValidator = UrlValidator()
        return urlValidator.isValid(url)
    }

    fun repair(url: String): String
    {
        var repairedUrl = url

        if (repairedUrl.indexOf(" ") >= 0)
            repairedUrl = repairedUrl.replace(" ", "")

        if (beginsWithValidProtocol(repairedUrl))
        {
            return repairedUrl
        }
        else
        {
            return if (containsCharacteristicChars(repairedUrl))
                "http://$repairedUrl"
            else
                EMPTY_URL
        }
    }

    private fun makeModel(): Link
    {
        val href = URL(url)
        val domain = href.host

        return Link(
            title = domain,
            category = "Unknown",
            url = url,
            domain = domain,
            tags = ""
        )
    }

    private fun beginsWithValidWww(url: String) = url.startsWith("www.")

    private fun beginsWithValidProtocol(url: String) = url.startsWith("http://") or url.startsWith("https://")

    private fun containsCharacteristicChars(url: String) = (url.indexOf(".") >= 0) or (url.indexOf(":") >= 0)

    companion object
    {
        const val EMPTY_URL = ""
    }
}