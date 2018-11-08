package studios.aestheticapps.linker.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable

@Entity(tableName = "link_table")
data class Link(@PrimaryKey(autoGenerate = true) val id: Int = 0,
                var title: String,
                var category: String = "Unknown",
                var url: String,
                var domain: String,
                var isFavorite: Boolean = false,
                var description: String = "",
                var tags: String = "",
                var lastUsed: String = "NEVER") : Parcelable
{
    constructor(parcel: Parcel) : this(
        id = parcel.readInt(),
        title = parcel.readString(),
        category = parcel.readString(),
        url = parcel.readString(),
        domain = parcel.readString(),
        isFavorite = (parcel.readByte().toInt() != 0),
        description = parcel.readString(),
        tags = parcel.readString(),
        lastUsed = parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int)
    {
        parcel.apply {
            writeInt(id)
            writeString(title)
            writeString(category)
            writeString(url)
            writeString(domain)
            writeByte((if (isFavorite) 1 else 0).toByte())
            writeString(description)
            writeString(tags)
            writeString(lastUsed)
        }
    }

    override fun describeContents() = 0

    fun stringToTags(): MutableList<String>
    {
        val list = mutableListOf<String>()
        list.addAll(tags.split(";"))
        return list
    }

    companion object CREATOR : Parcelable.Creator<Link>
    {
        const val PARCEL_LINK = "linker_link"

        override fun createFromParcel(parcel: Parcel) = Link(parcel)

        override fun newArray(size: Int): Array<Link?> = arrayOfNulls(size)
    }
}