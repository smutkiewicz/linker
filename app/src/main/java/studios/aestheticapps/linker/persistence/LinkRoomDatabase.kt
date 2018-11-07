package studios.aestheticapps.linker.persistence

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.os.AsyncTask
import studios.aestheticapps.linker.model.Link

@Database(entities = [Link::class], version = 1)
abstract class LinkRoomDatabase : RoomDatabase()
{
    abstract fun linkDao(): LinkDao

    companion object
    {
        private var INSTANCE: LinkRoomDatabase? = null

        fun getInstance(context: Context): LinkRoomDatabase?
        {
            if (INSTANCE == null)
            {
                synchronized(LinkRoomDatabase::class.java)
                {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        LinkRoomDatabase::class.java,
                        "link_database")
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
            override fun onOpen(db: SupportSQLiteDatabase)
            {
                super.onOpen(db)
                //PopulateDbAsync(INSTANCE!!).execute()
            }
        }

        private class PopulateDbAsync internal constructor(db: LinkRoomDatabase) : AsyncTask<Void, Void, Void>()
        {
            private val dao: LinkDao = db.linkDao()

            override fun doInBackground(vararg params: Void): Void?
            {
                dao.apply {
                    insert(Link(title = "First link", domain = "github.com", url = "https://github.com/smutkiewicz"))
                    insert(Link(title = "Github", domain = "github.com", url = "https://github.com/smutkiewicz"))
                    insert(Link(title = "sth else", domain = "github.com", url = "https://github.com/smutkiewicz"))
                    insert(Link(title = "woooooooooooww", domain = "github.com", url = "https://github.com/smutkiewicz"))
                    insert(Link(title = "a loooooot of text", domain = "github.com", url = "https://github.com/smutkiewicz"))
                    insert(Link(title = "it works", domain = "github.com", url = "https://github.com/smutkiewicz"))
                    insert(Link(title = "nice try mate", domain = "github.com", url = "https://github.com/smutkiewicz"))
                    insert(Link(title = "Nice.", domain = "github.com", url = "https://github.com/smutkiewicz"))
                }

                return null
            }
        }
    }
}