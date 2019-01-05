package studios.aestheticapps.linker.model

import org.apache.commons.validator.routines.UrlValidator
import org.apache.commons.validator.routines.UrlValidator.ALLOW_ALL_SCHEMES
import java.util.regex.Pattern

/**
 * Provides vaild urls for model.
 */
class LinkValidator(private var url: String)
{
    fun build(): String
    {
        if (isValidRegex())
        {
            return url
        }
        else
        {
            url = repair(url)

            return if (isValidRegex())
            {
                url
            }
            else
            {
                EMPTY_URL
            }
        }
    }

    private fun isValidRegex(): Boolean
    {
        if ((url == EMPTY_URL) or containsSpaces(url))
            return false

        return matchesRegex(url) && beginsWithValidProtocol(url) && containsCharacteristicChars(url)
    }

    private fun isValidApache(): Boolean
    {
        if (url == EMPTY_URL)
            return false

        val urlValidator = UrlValidator(ALLOW_ALL_SCHEMES)
        return urlValidator.isValid(url)
    }

    private fun isValidSimpleCase(): Boolean
    {
        if ((url == EMPTY_URL) or containsSpaces(url))
            return false

        return beginsWithValidProtocol(url) or containsCharacteristicChars(url)
    }

    private fun repair(url: String): String
    {
        var repairedUrl = url

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

    private fun matchesRegex(url: String) = pattern.matcher(url).find()

    private fun beginsWithValidWww(url: String) = url.startsWith("www.")

    private fun beginsWithValidProtocol(url: String) = url.startsWith("http://") or url.startsWith("https://")

    private fun containsCharacteristicChars(url: String) = (url.indexOf(".") >= 0) or (url.indexOf(":") >= 0)

    private fun containsSpaces(url: String) = url.contains(" ")

    companion object
    {
        const val EMPTY_URL = ""
        private const val URL_REGEX = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"
        private val pattern = Pattern.compile(LinkValidator.URL_REGEX)
    }
}