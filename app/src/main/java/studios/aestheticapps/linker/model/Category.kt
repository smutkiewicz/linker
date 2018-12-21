package studios.aestheticapps.linker.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "category_table")
data class Category(@PrimaryKey(autoGenerate = true) val id: Int = 0,
                    var title: String,
                    val ruleDomain: String)