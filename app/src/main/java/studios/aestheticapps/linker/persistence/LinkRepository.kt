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

    fun getAll() = LinkedList(SearchAsyncTask(linkDao).execute(EMPTY).get())

    fun search(phrase: String) = LinkedList(SearchAsyncTask(linkDao).execute(phrase).get())

    fun insert(link: Link)
    {
        InsertAsyncTask(linkDao).execute(link)
    }

    fun delete(id: Int)
    {
        DeleteAsyncTask(linkDao).execute(id)
    }

    private class InsertAsyncTask internal constructor(private val asyncTaskDao: LinkDao) : AsyncTask<Link, Void, Void>()
    {
        override fun doInBackground(vararg params: Link): Void?
        {
            asyncTaskDao.insert(params[0])
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

    private companion object
    {
        const val EMPTY = ""
    }
}