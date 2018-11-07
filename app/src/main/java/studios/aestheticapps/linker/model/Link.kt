package studios.aestheticapps.linker.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "link_table")
data class Link(@PrimaryKey(autoGenerate = true) val id: Int = 0,
                var title: String,
                var category: String = "Unknown",
                var url: String,
                var domain: String,
                var isFavorite: Boolean = false,
                var description: String = "",
                var tags: String = "",
                var lastUsed: String = "NEVER")