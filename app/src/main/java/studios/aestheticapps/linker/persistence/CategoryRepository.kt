package studios.aestheticapps.linker.persistence

import android.app.Application
import android.os.AsyncTask
import studios.aestheticapps.linker.model.Category
import java.util.*

class CategoryRepository internal constructor(application: Application)
{
    private val db: CategoryRoomDatabase = CategoryRoomDatabase.getInstance(application)!!
    private val categoryDao: CategoryDao

    init
    {
        categoryDao = db.categoryDao()
    }

    fun getAll() = LinkedList(SearchAsyncTask(categoryDao).execute(EMPTY).get())

    fun search(phrase: String) = LinkedList(SearchAsyncTask(categoryDao).execute(phrase).get())

    fun update(model: Category)
    {
        UpdateAsyncTask(categoryDao).execute(model)
    }

    fun insert(model: Category)
    {
        InsertAsyncTask(categoryDao).execute(model)
    }

    fun delete(id: Int)
    {
        DeleteAsyncTask(categoryDao).execute(id)
    }

    private class InsertAsyncTask internal constructor(private val asyncTaskDao: CategoryDao) : AsyncTask<Category, Void, Void>()
    {
        override fun doInBackground(vararg params: Category): Void?
        {
            asyncTaskDao.insert(params[0])
            return null
        }
    }

    private class UpdateAsyncTask internal constructor(private val asyncTaskDao: CategoryDao) : AsyncTask<Category, Void, Void>()
    {
        override fun doInBackground(vararg params: Category): Void?
        {
            asyncTaskDao.update(params[0])
            return null
        }
    }

    private class DeleteAsyncTask internal constructor(private val asyncTaskDao: CategoryDao) : AsyncTask<Int, Void, Void>()
    {
        override fun doInBackground(vararg params: Int?): Void?
        {
            asyncTaskDao.delete(params[0]!!)
            return null
        }
    }

    private class SearchAsyncTask internal constructor(private val asyncTaskDao: CategoryDao) : AsyncTask<String, Void, List<Category>>()
    {
        override fun doInBackground(vararg params: String?): List<Category> = asyncTaskDao.getAll()
    }

    companion object
    {
        private const val EMPTY = ""
    }
}