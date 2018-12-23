package studios.aestheticapps.linker.persistence.link

import android.app.Application
import android.arch.persistence.db.SimpleSQLiteQuery
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.persistence.DatabaseAsyncTask
import studios.aestheticapps.linker.persistence.DatabaseTask
import java.util.*

class LinkRepository internal constructor(application: Application)
{
    private val db: LinkRoomDatabase = LinkRoomDatabase.getInstance(application)!!
    private val linkDao: LinkDao

    init
    {
        linkDao = db.linkDao()
    }

    fun getListOf(category: Int): LinkedList<Link>
    {
        return when(category)
        {
            ALL -> getAll()
            FAVORITES -> getFavorites()
            RECENT -> getRecent()
            else -> getAll()
        }
    }

    fun search(phrase: String, orderBy: String = TITLE_COLUMN): LinkedList<Link>
    {
        return DatabaseAsyncTask(
            object : DatabaseTask<LinkedList<Link>>
            {
                override fun performOperation() = LinkedList(
                    linkDao.searchRawQuery(getRawOrderByQuery(phrase, orderBy))
                )
            }
        ).execute().get()
    }

    fun update(link: Link)
    {
        DatabaseAsyncTask(
            object : DatabaseTask<Unit>
            {
                override fun performOperation() = linkDao.update(link)
            }
        ).execute()
    }

    fun insert(link: Link)
    {
        DatabaseAsyncTask(
            object : DatabaseTask<Unit>
            {
                override fun performOperation() = linkDao.insert(link)
            }
        ).execute()
    }

    fun delete(id: Int)
    {
        DatabaseAsyncTask(
            object : DatabaseTask<Unit>
            {
                override fun performOperation() = linkDao.delete(id)
            }
        ).execute()
    }

    private fun getAll(): LinkedList<Link>
    {
        return DatabaseAsyncTask(
            object : DatabaseTask<LinkedList<Link>>
            {
                override fun performOperation() = LinkedList(
                    linkDao.searchRawQuery(getRawOrderByQuery(EMPTY, TITLE_COLUMN))
                )
            }
        ).execute().get()
    }

    private fun getFavorites(): LinkedList<Link>
    {
        return DatabaseAsyncTask(
            object : DatabaseTask<LinkedList<Link>>
            {
                override fun performOperation() = LinkedList(linkDao.getFavourites())
            }
        ).execute().get()
    }

    private fun getRecent(): LinkedList<Link>
    {
        return DatabaseAsyncTask(
            object : DatabaseTask<LinkedList<Link>>
            {
                override fun performOperation() = LinkedList(linkDao.getRecent())
            }
        ).execute().get()
    }

    private fun getRawOrderByQuery(phrase: String, orderBy: String = "title") = SimpleSQLiteQuery(
        "SELECT * FROM link_table " +
            "WHERE title LIKE '%' || '$phrase' || '%' " +
            "OR category LIKE '%' || '$phrase' || '%' " +
            "OR domain LIKE '%' || '$phrase' || '%' " +
            "OR url LIKE '%' || '$phrase' || '%' " +
            "OR tags LIKE '%' || '$phrase' || '%' " +
            "ORDER BY $orderBy", null
    )

    companion object
    {
        private const val EMPTY = ""

        const val TITLE_COLUMN = "title"
        const val CATEGORY_COLUMN = "category"
        const val DOMAIN_COLUMN = "domain"
        const val CREATED_COLUMN = "created"
        const val CREATED_LATEST_COLUMN = "created DESC"

        const val ALL = 0
        const val FAVORITES = 1
        const val RECENT = 2
    }
}