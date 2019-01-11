package studios.aestheticapps.linker.persistence

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.os.AsyncTask
import studios.aestheticapps.linker.model.Category
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.persistence.category.CategoryDao
import studios.aestheticapps.linker.persistence.link.LinkDao
import java.util.*

@Database(entities = [Link::class, Category::class], version = 1, exportSchema = false)
abstract class LinkRoomDatabase : RoomDatabase()
{
    abstract fun linkDao(): LinkDao
    abstract fun categoryDao(): CategoryDao

    val observers: LinkedList<Observer> = LinkedList()

    companion object
    {
        private var INSTANCE: LinkRoomDatabase? = null

        private const val USAGE_VERY_IMPORTANT = 20
        private const val USAGE_IMPORTANT = 10
        private const val USAGE_MEDIUM = 5
        private const val USAGE_MINIMUM = 1
        private const val USAGE_UNUSED = 0

        fun getInstance(context: Context): LinkRoomDatabase?
        {
            if (INSTANCE == null)
            {
                synchronized(LinkRoomDatabase::class.java)
                {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        LinkRoomDatabase::class.java,
                        "linker_database")
                        .addCallback(roomDatabaseCallback)
                        .build()
                }
            }

            return INSTANCE
        }

        fun destroyInstance()
        {
            INSTANCE!!.close()
            INSTANCE = null
        }

        private val roomDatabaseCallback = object : RoomDatabase.Callback()
        {
            override fun onCreate(db: SupportSQLiteDatabase)
            {
                super.onCreate(db)
                PopulateDbAsync(INSTANCE!!).execute()
            }

            override fun onOpen(db: SupportSQLiteDatabase)
            {
                super.onOpen(db)
            }
        }

        private class PopulateDbAsync internal constructor(db: LinkRoomDatabase) : AsyncTask<Void, Void, Void>()
        {
            private val dao: CategoryDao = db.categoryDao()

            override fun doInBackground(vararg params: Void): Void?
            {
                dao.deleteAll()
                dao.apply {
                    insert(Category(name = "Undefined", ruleDomain = "", usages = 0))
                    insert(Category(name = "Blogs", ruleDomain = "link.medium.com", usages = USAGE_IMPORTANT))
                    insert(Category(name = "Books", ruleDomain = "goodreads.com", usages = USAGE_VERY_IMPORTANT))
                    insert(Category(name = "Business", ruleDomain = "outlook.office.com", usages = USAGE_MEDIUM))
                    insert(Category(name = "Education", ruleDomain = "pl.m.wikipedia.org", usages = USAGE_VERY_IMPORTANT))
                    insert(Category(name = "Education", ruleDomain = "en.m.wikipedia.org", usages = USAGE_VERY_IMPORTANT))
                    insert(Category(name = "Education", ruleDomain = "developer.android.com", usages = USAGE_MEDIUM))
                    insert(Category(name = "Education", ruleDomain = "stackoverflow.com", usages = USAGE_MEDIUM))
                    insert(Category(name = "Food/Lifestyle", ruleDomain = "", usages = 0))
                    insert(Category(name = "Gifs", ruleDomain = "media1.giphy.com", usages = USAGE_VERY_IMPORTANT))
                    insert(Category(name = "Gifs", ruleDomain = "media2.giphy.com", usages = USAGE_VERY_IMPORTANT))
                    insert(Category(name = "Gifs", ruleDomain = "giphy.com", usages = USAGE_VERY_IMPORTANT))
                    insert(Category(name = "Gifs", ruleDomain = "tenor.com", usages = USAGE_VERY_IMPORTANT))
                    insert(Category(name = "Gifs", ruleDomain = "media1.tenor.co", usages = USAGE_VERY_IMPORTANT))
                    insert(Category(name = "Gifs", ruleDomain = "media2.tenor.co", usages = USAGE_VERY_IMPORTANT))
                    insert(Category(name = "Important", ruleDomain = "", usages = 0))
                    insert(Category(name = "Memes", ruleDomain = "m.9gag.com", usages = USAGE_VERY_IMPORTANT))
                    insert(Category(name = "Memes", ruleDomain = "9gag.com", usages = USAGE_VERY_IMPORTANT))
                    insert(Category(name = "Music", ruleDomain = "soundcloud.com", usages = USAGE_VERY_IMPORTANT))
                    insert(Category(name = "Music", ruleDomain = "genius.com", usages = USAGE_VERY_IMPORTANT))
                    insert(Category(name = "News", ruleDomain = "news.google.com", usages = USAGE_MINIMUM))
                    insert(Category(name = "Social", ruleDomain = "facebook.com", usages = USAGE_VERY_IMPORTANT))
                    insert(Category(name = "Social", ruleDomain = "m.facebook.com", usages = USAGE_VERY_IMPORTANT))
                    insert(Category(name = "Social", ruleDomain = "on.fb.me", usages = USAGE_VERY_IMPORTANT))
                    insert(Category(name = "Social", ruleDomain = "fb.me", usages = USAGE_VERY_IMPORTANT))
                    insert(Category(name = "Social", ruleDomain = "twitter.com", usages = USAGE_VERY_IMPORTANT))
                    insert(Category(name = "Social", ruleDomain = "instagram.com", usages = USAGE_VERY_IMPORTANT))
                    insert(Category(name = "Social", ruleDomain = "tumblr.com", usages = USAGE_VERY_IMPORTANT))
                    insert(Category(name = "Sports", ruleDomain = "premierleague.com", usages = USAGE_VERY_IMPORTANT))
                    insert(Category(name = "Technology", ruleDomain = "github.com", usages = 0))
                    insert(Category(name = "Travel", ruleDomain = "maps.app.goo.gl", usages = USAGE_IMPORTANT))
                    insert(Category(name = "Videos", ruleDomain = "youtu.be", usages = USAGE_IMPORTANT))
                    insert(Category(name = "Videos", ruleDomain = "youtube.com", usages = USAGE_IMPORTANT))
                    insert(Category(name = "Work", ruleDomain = "drive.google.com", usages = USAGE_MEDIUM))
                    insert(Category(name = "Work", ruleDomain = "github.com", usages = USAGE_MEDIUM))
                }

                return null
            }
        }
    }
}