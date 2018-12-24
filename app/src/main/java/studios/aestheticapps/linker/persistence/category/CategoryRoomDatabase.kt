package studios.aestheticapps.linker.persistence.category

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.os.AsyncTask
import studios.aestheticapps.linker.model.Category
import studios.aestheticapps.linker.persistence.link.LinkRoomDatabase

@Database(entities = [Category::class], version = 1)
abstract class CategoryRoomDatabase : RoomDatabase()
{
    abstract fun categoryDao(): CategoryDao

    companion object
    {
        private var INSTANCE: CategoryRoomDatabase? = null

        private const val USAGE_VERY_IMPORTANT = 20
        private const val USAGE_IMPORTANT = 10
        private const val USAGE_MEDIUM = 5
        private const val USAGE_MINIMUM = 1
        private const val USAGE_UNUSED = 0

        fun getInstance(context: Context): CategoryRoomDatabase?
        {
            if (INSTANCE == null)
            {
                synchronized(LinkRoomDatabase::class.java)
                {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        CategoryRoomDatabase::class.java,
                        "category_database")
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
                //PopulateDbAsync(INSTANCE!!).execute()
            }
        }

        private class PopulateDbAsync internal constructor(db: CategoryRoomDatabase) : AsyncTask<Void, Void, Void>()
        {
            private val dao: CategoryDao = db.categoryDao()

            override fun doInBackground(vararg params: Void): Void?
            {
                dao.apply {
                    insert(Category(name = "Undefined", ruleDomain = "", usages = 0))
                    insert(Category(name = "Animals", ruleDomain = "", usages = 0))
                    insert(Category(name = "Blogs", ruleDomain = "link.medium.com", usages = USAGE_IMPORTANT))
                    insert(Category(name = "Books", ruleDomain = "goodreads.com", usages = USAGE_VERY_IMPORTANT))
                    insert(Category(name = "Business", ruleDomain = "outlook.office.com", usages = USAGE_MEDIUM))
                    insert(Category(name = "Education", ruleDomain = "developer.android.com", usages = USAGE_MEDIUM))
                    insert(Category(name = "Food", ruleDomain = "", usages = 0))
                    insert(Category(name = "Gifs", ruleDomain = "media2.giphy.com", usages = USAGE_VERY_IMPORTANT))
                    insert(Category(name = "Important", ruleDomain = "", usages = 0))
                    insert(Category(name = "Memes", ruleDomain = "m.9gag.com", usages = USAGE_VERY_IMPORTANT))
                    insert(Category(name = "Memes", ruleDomain = "9gag.com", usages = USAGE_VERY_IMPORTANT))
                    insert(Category(name = "Music", ruleDomain = "soundcloud.com", usages = USAGE_VERY_IMPORTANT))
                    insert(Category(name = "News", ruleDomain = "news.google.com", usages = USAGE_MINIMUM))
                    insert(Category(name = "Science", ruleDomain = "", usages = 0))
                    insert(Category(name = "Social", ruleDomain = "facebook.com", usages = USAGE_VERY_IMPORTANT))
                    insert(Category(name = "Social", ruleDomain = "m.facebook.com", usages = USAGE_VERY_IMPORTANT))
                    insert(Category(name = "Social", ruleDomain = "on.fb.me", usages = USAGE_VERY_IMPORTANT))
                    insert(Category(name = "Social", ruleDomain = "fb.me", usages = USAGE_VERY_IMPORTANT))
                    insert(Category(name = "Social", ruleDomain = "twitter.com", usages = USAGE_VERY_IMPORTANT))
                    insert(Category(name = "Social", ruleDomain = "instagram.com", usages = USAGE_VERY_IMPORTANT))
                    insert(Category(name = "Social", ruleDomain = "tumblr.com", usages = USAGE_VERY_IMPORTANT))
                    insert(Category(name = "Sports", ruleDomain = "premierleague.com", usages = USAGE_IMPORTANT))
                    insert(Category(name = "Technology", ruleDomain = "", usages = 0))
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