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
                    insert(Category(name = "Blogs", ruleDomain = "link.medium.com"))
                    insert(Category(name = "Books", ruleDomain = "goodreads.com"))
                    insert(Category(name = "Business", ruleDomain = "outlook.office.com"))
                    insert(Category(name = "Education", ruleDomain = "developer.android.com"))
                    insert(Category(name = "Food", ruleDomain = "", usages = 0))
                    insert(Category(name = "Gifs", ruleDomain = "media2.giphy.com"))
                    insert(Category(name = "Important", ruleDomain = "", usages = 0))
                    insert(Category(name = "Memes", ruleDomain = "m.9gag.com"))
                    insert(Category(name = "Memes", ruleDomain = "9gag.com"))
                    insert(Category(name = "Music", ruleDomain = "soundcloud.com"))
                    insert(Category(name = "News", ruleDomain = "news.google.com"))
                    insert(Category(name = "Science", ruleDomain = "", usages = 0))
                    insert(Category(name = "Social", ruleDomain = "facebook.com"))
                    insert(Category(name = "Social", ruleDomain = "m.facebook.com"))
                    insert(Category(name = "Social", ruleDomain = "on.fb.me"))
                    insert(Category(name = "Social", ruleDomain = "fb.me"))
                    insert(Category(name = "Social", ruleDomain = "twitter.com"))
                    insert(Category(name = "Social", ruleDomain = "instagram.com"))
                    insert(Category(name = "Social", ruleDomain = "tumblr.com"))
                    insert(Category(name = "Sports", ruleDomain = "premierleague.com"))
                    insert(Category(name = "Technology", ruleDomain = "", usages = 0))
                    insert(Category(name = "Travel", ruleDomain = "maps.app.goo.gl"))
                    insert(Category(name = "Videos", ruleDomain = "youtu.be"))
                    insert(Category(name = "Videos", ruleDomain = "youtube.com"))
                    insert(Category(name = "Work", ruleDomain = "drive.google.com"))
                    insert(Category(name = "Work", ruleDomain = "github.com"))
                }

                return null
            }
        }
    }
}