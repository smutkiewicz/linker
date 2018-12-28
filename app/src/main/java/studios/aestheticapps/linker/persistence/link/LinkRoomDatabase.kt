package studios.aestheticapps.linker.persistence.link

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.migration.Migration
import android.content.Context
import studios.aestheticapps.linker.model.Link
import java.util.*

@Database(entities = [Link::class], version = 1, exportSchema = false)
abstract class LinkRoomDatabase : RoomDatabase()
{
    abstract fun linkDao(): LinkDao
    val observers: LinkedList<Observer> = LinkedList()

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

        private val MIGRATION_1_2: Migration = object : Migration(1, 2)
        {
            override fun migrate(database: SupportSQLiteDatabase)
            {
                database.execSQL("ALTER TABLE link_table " + "ADD COLUMN imageUrl TEXT")
            }
        }
    }
}