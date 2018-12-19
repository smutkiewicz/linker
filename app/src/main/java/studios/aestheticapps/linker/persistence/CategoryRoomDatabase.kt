package studios.aestheticapps.linker.persistence

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import studios.aestheticapps.linker.model.Category

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
            override fun onOpen(db: SupportSQLiteDatabase)
            {
                super.onOpen(db)
            }
        }
    }
}