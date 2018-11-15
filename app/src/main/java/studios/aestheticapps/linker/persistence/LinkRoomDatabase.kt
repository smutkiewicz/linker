package studios.aestheticapps.linker.persistence

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
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
            }
        }
    }
}