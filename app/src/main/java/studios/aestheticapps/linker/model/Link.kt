package studios.aestheticapps.linker.model

import java.util.*

data class Link(val title: String,
                val category: String = "Unknown",
                val url: String,
                val domain: String,
                val description: String = "",
                val tags: LinkedList<String> = LinkedList(),
                val lastUsed: String = "NEVER")