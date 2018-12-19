package studios.aestheticapps.linker.model

import org.apache.commons.validator.routines.UrlValidator

/**
 * Provides vaild urls for model.
 */
class LinkValidator(private var url: String)
{
    fun build(): String
    {
        if (isValid())
        {
            return url
        }
        else
        {
            url = repair(url)

            return if (isValid())
            {
                url
            }
            else
            {
                EMPTY_URL
            }
        }
    }

    private fun isValid(): Boolean
    {
        if (url == EMPTY_URL)
            return false

        val urlValidator = UrlValidator()
        return urlValidator.isValid(url)
    }


    private fun repair(url: String): String
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

    private fun beginsWithValidWww(url: String) = url.startsWith("www.")

    private fun beginsWithValidProtocol(url: String) = url.startsWith("http://") or url.startsWith("https://")

    private fun containsCharacteristicChars(url: String) = (url.indexOf(".") >= 0) or (url.indexOf(":") >= 0)

    companion object
    {
        const val EMPTY_URL = ""
    }
}