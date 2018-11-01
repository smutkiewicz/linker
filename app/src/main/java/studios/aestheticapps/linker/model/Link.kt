package studios.aestheticapps.linker.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "link_table")
data class Link(@PrimaryKey(autoGenerate = true) val id: Int = 0,
                val title: String,
                val category: String = "Unknown",
                val url: String,
                val domain: String,
                val description: String = "",
                val lastUsed: String = "NEVER")