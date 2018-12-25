package studios.aestheticapps.linker.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import studios.aestheticapps.linker.utils.DateTimeHelper

@Entity(tableName = "category_table",
        indices = [Index(value = ["name"])])
data class Category(@PrimaryKey(autoGenerate = true) val id: Int = 0,
                    var name: String,
                    var ruleDomain: String,
                    var usages: Int = 1,
                    var lastUsed: String = DateTimeHelper.getCurrentDateTimeStamp())
{
    companion object
    {
        const val PARCEL_CATEGORY_NAME = "linker_category_name"
    }
}