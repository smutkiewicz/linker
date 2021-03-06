package studios.aestheticapps.linker.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import studios.aestheticapps.linker.utils.DateTimeHelper

@Entity(tableName = "link_table")
data class Link(@PrimaryKey(autoGenerate = true) val id: Int = 0,
                var title: String,
                var category: String = "Unknown",
                var url: String,
                var imageUrl: String = "",
                var domain: String,
                var isFavorite: Boolean = false,
                var description: String = "",
                var tags: String = "",
                var created: String = DateTimeHelper.getCurrentDateTimeStamp(),
                var lastUsed: String = DateTimeHelper.getCurrentDateTimeStamp()) : Parcelable
{
    constructor(parcel: Parcel) : this(
        id = parcel.readInt(),
        title = parcel.readString(),
        category = parcel.readString(),
        url = parcel.readString(),
        imageUrl = parcel.readString(),
        domain = parcel.readString(),
        isFavorite = (parcel.readByte().toInt() != 0),
        description = parcel.readString(),
        tags = parcel.readString(),
        created = parcel.readString(),
        lastUsed = parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int)
    {
        parcel.apply {
            writeInt(id)
            writeString(title)
            writeString(category)
            writeString(url)
            writeString(imageUrl)
            writeString(domain)
            writeByte((if (isFavorite) 1 else 0).toByte())
            writeString(description)
            writeString(tags)
            writeString(created)
            writeString(lastUsed)
        }
    }

    override fun describeContents() = 0

    fun addTag(tag: String)
    {
        val tagsList = stringToListOfTags()
        tagsList.add(tag)
        tags = listOfTagsToString(tagsList)
    }

    fun removeTag(tag: String)
    {
        val tagsList = stringToListOfTags()
        tagsList.remove(tag)
        tags = listOfTagsToString(tagsList)
    }

    fun stringToListOfTags(): MutableList<String>
    {
        val list = mutableListOf<String>()
        if (tags.isNotBlank())
            list.addAll(tags.split(DELIMITER))

        return list
    }

    companion object CREATOR : Parcelable.Creator<Link>
    {
        private const val DELIMITER = ";"
        const val PARCEL_LINK = "linker_link"
        const val INTENT_LINK = "intent_linker_link"

        override fun createFromParcel(parcel: Parcel) = Link(parcel)

        override fun newArray(size: Int): Array<Link?> = arrayOfNulls(size)

        fun listOfTagsToString(elements: MutableList<String>) = TextUtils.join(DELIMITER, elements)
    }
}