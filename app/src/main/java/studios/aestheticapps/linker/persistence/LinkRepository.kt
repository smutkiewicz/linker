package studios.aestheticapps.linker.persistence

import android.app.Application
import android.os.AsyncTask
import studios.aestheticapps.linker.model.Link
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

    fun search(phrase: String) = LinkedList(SearchAsyncTask(linkDao).execute(phrase).get())

    fun update(link: Link)
    {
        UpdateAsyncTask(linkDao).execute(link)
    }

    fun insert(link: Link)
    {
        InsertAsyncTask(linkDao).execute(link)
    }

    fun delete(id: Int)
    {
        DeleteAsyncTask(linkDao).execute(id)
    }

    private fun getAll() = LinkedList(SearchAsyncTask(linkDao).execute(EMPTY).get())

    private fun getFavorites() = LinkedList(GetFavoritesAsyncTask(linkDao).execute().get())

    private fun getRecent() = LinkedList(GetRecentAsyncTask(linkDao).execute().get())

    private class InsertAsyncTask internal constructor(private val asyncTaskDao: LinkDao) : AsyncTask<Link, Void, Void>()
    {
        override fun doInBackground(vararg params: Link): Void?
        {
            asyncTaskDao.insert(params[0])
            return null
        }
    }

    private class UpdateAsyncTask internal constructor(private val asyncTaskDao: LinkDao) : AsyncTask<Link, Void, Void>()
    {
        override fun doInBackground(vararg params: Link): Void?
        {
            asyncTaskDao.update(params[0])
            return null
        }
    }

    private class DeleteAsyncTask internal constructor(private val asyncTaskDao: LinkDao) : AsyncTask<Int, Void, Void>()
    {
        override fun doInBackground(vararg params: Int?): Void?
        {
            asyncTaskDao.delete(params[0]!!)
            return null
        }
    }

    private class SearchAsyncTask internal constructor(private val asyncTaskDao: LinkDao) : AsyncTask<String, Void, List<Link>>()
    {
        override fun doInBackground(vararg params: String?): List<Link> = asyncTaskDao.search(params[0]!!)
    }

    private class GetFavoritesAsyncTask internal constructor(private val asyncTaskDao: LinkDao) : AsyncTask<Void, Void, List<Link>>()
    {
        override fun doInBackground(vararg params: Void?): List<Link> = asyncTaskDao.getFavourites()
    }

    private class GetRecentAsyncTask internal constructor(private val asyncTaskDao: LinkDao) : AsyncTask<Void, Void, List<Link>>()
    {
        override fun doInBackground(vararg params: Void?): List<Link> = asyncTaskDao.getRecent()
    }

    companion object
    {
        private const val EMPTY = ""

        const val ALL = 0
        const val FAVORITES = 1
        const val RECENT = 2
    }
}