package studios.aestheticapps.linker

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import studios.aestheticapps.linker.model.LinkValidator

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class LinkValidatorUnitTest
{
    @Test
    fun shouldReturnValidModelWithUnchangedUrl()
    {
        val validator = LinkValidator("http://validurl.com")
        val model = validator.build()

        assertNotEquals(null, model)
        assertEquals("http://validurl.com", model!!.url)
    }

    @Test
    fun shouldRepairUrlProtocol()
    {
        val validator = LinkValidator("urlwithouthttp.com")
        val model = validator.build()

        assertNotEquals(null, model)
        assertEquals("http://urlwithouthttp.com", model!!.url)
    }

    @Test
    fun shouldRepairUrlSpaces()
    {
        val validator = LinkValidator("url with spaces .com")
        val model = validator.build()

        assertNotEquals(null, model)
        assertEquals("http://urlwithspaces.com", model!!.url)
    }

    @Test
    fun shouldBeUnableToRepairUrl()
    {
        val validator = LinkValidator("http://model pretending its correct com. ")
        val model = validator.build()

        assertEquals(null, model)
    }

    @Test
    fun shouldNotPassValidUriTest()
    {
        val validator = LinkValidator("http://linkpretendingitscorrectButShouldNotPassUriCheck.?")
        val model = validator.build()

        assertEquals(null, model)
    }

    @Test
    fun shouldReturnNullForUrlThatIsASentence()
    {
        val validator = LinkValidator("To nie jest url, i nie ma kropki")
        val model = validator.build()

        assertEquals(null, model)
    }

    @Test
    fun shouldReturnNullForUrlThatIsASentenceWithADot()
    {
        val validator = LinkValidator("To nie jest url, i ma kropke.")
        val model = validator.build()

        assertEquals(null, model)
    }
}
