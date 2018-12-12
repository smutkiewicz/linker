package studios.aestheticapps.linker

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.model.LinkMetadataFormatter

class LinkMetadataFormatterTest
{
    private val formatter = LinkMetadataFormatter()

    @Test
    fun shouldProperlyParseMetadataFromRawUrl()
    {
        val model = formatter.obtainMetadataFrom("https://stackoverflow.com/questions/17596144/get-favicon-from-html-jsoup")

        assertNotNull(model)
        assertEquals("java - get favicon from html (JSOUP) - Stack Overflow", model!!.title)
        assertEquals("stackoverflow.com", model.domain)
        assertEquals("", model.description)
        assertEquals("Science/Education", model.category)
        assertEquals("stackoverflow.com;Science/Education;", model.tags)
        assertEquals(
            "https://cdn.sstatic.net/Sites/stackoverflow/img/favicon.ico?v=4f32ecc8f43d",
            model.imageUrl
        )
    }

    @Test
    fun shouldProperlyParseMetadataFromNonAutomatedModel()
    {
        val userModel = Link(
            title = "my favourite website - stackoverflow",
            url = "https://stackoverflow.com/questions/17596144/get-favicon-from-html-jsoup",
            domain = "",
            description = "I wrote sth about that, I edited this",
            category = "Science/Education",
            tags = "this;should;be;untouched;"
        )

        val model = formatter.obtainMetadataFrom(userModel)

        assertNotNull(model)
        assertEquals("my favourite website - stackoverflow", model!!.title)
        assertEquals("stackoverflow.com", model.domain)
        assertEquals("I wrote sth about that, I edited this", model.description)
        assertEquals("Science/Education", model.category)
        assertEquals("this;should;be;untouched;", model.tags)
        assertEquals(
            "https://cdn.sstatic.net/Sites/stackoverflow/img/favicon.ico?v=4f32ecc8f43d",
            model.imageUrl
        )
    }

    @Test
    fun shouldProperlyParseMetadataFromAlreadyAutomatedModel()
    {
        val automatedModel = formatter.obtainMetadataFrom("https://stackoverflow.com/questions/17596144/get-favicon-from-html-jsoup")
        assertNotNull(automatedModel)

        val model = formatter.obtainMetadataFrom(automatedModel!!)

        assertNotNull(model)
        assertEquals("java - get favicon from html (JSOUP) - Stack Overflow", model!!.title)
        assertEquals("stackoverflow.com", model.domain)
        assertEquals("", model.description)
        assertEquals("Science/Education", model.category)
        assertEquals("stackoverflow.com;Science/Education;", model.tags)
        assertEquals(
            "https://cdn.sstatic.net/Sites/stackoverflow/img/favicon.ico?v=4f32ecc8f43d",
            model.imageUrl
        )
    }

    @Test
    fun shouldProperlyParseMetadataForAVideo()
    {
        val model = formatter.obtainMetadataFrom("https://www.youtube.com/watch?v=RvSWwVVN4cY")

        assertNotNull(model)
        assertEquals("Symulator Kozy - Analiza sukcesu | Wyzwanie - YouTube", model!!.title)
        assertEquals("youtube.com", model.domain)
        assertEquals(
            "https://i.ytimg.com/vi/RvSWwVVN4cY/hqdefault.jpg?sqp=-oaymwEiCNIBEHZIWvKriqkDFQgBFQAAAAAYASUAAMhCPQCAokN4AQ==&rs=AOn4CLAPan193PnRDS6Zb3Jd_y2DeExLAQ",
            model.imageUrl
        )
    }
}