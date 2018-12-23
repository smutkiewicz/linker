package studios.aestheticapps.linker.persistence.category

import android.app.Application
import studios.aestheticapps.linker.model.Category
import studios.aestheticapps.linker.persistence.DatabaseAsyncTask
import studios.aestheticapps.linker.persistence.DatabaseTask
import studios.aestheticapps.linker.utils.DateTimeHelper
import java.util.*

class CategoryRepository internal constructor(application: Application)
{
    private val db: CategoryRoomDatabase = CategoryRoomDatabase.getInstance(application)!!
    private val categoryDao: CategoryDao

    init
    {
        categoryDao = db.categoryDao()
    }

    fun getAllCategories(): LinkedList<String>
    {
        return DatabaseAsyncTask(
            object : DatabaseTask<LinkedList<String>>
            {
                override fun performOperation() = LinkedList(categoryDao.getAllCategories())
            }
        ).execute().get()
    }

    /**
     * Fired in Formatter's AsyncTask, so no new thread needed.
     */
    fun getCategoriesByDomain(domain: String) = LinkedList(categoryDao.getCategoriesByDomain(domain))

    fun getDomainsByCategory(categoryName: String): LinkedList<String>
    {
        return DatabaseAsyncTask(
            object : DatabaseTask<LinkedList<String>>
            {
                override fun performOperation() = LinkedList(categoryDao.getDomainsByCategory(categoryName))
            }
        ).execute().get()
    }

    fun getCategoryWithDomain(categoryName: String, domain: String): Category
    {
        return DatabaseAsyncTask(
            object : DatabaseTask<Category>
            {
                override fun performOperation() = categoryDao.getCategoryWithDomain(categoryName, domain)
            }
        ).execute().get()
    }

    fun insert(category: Category)
    {
        DatabaseAsyncTask(
            object : DatabaseTask<Unit>
            {
                override fun performOperation() = categoryDao.insert(category)
            }
        ).execute()
    }


    fun update(id: Int, lastUsed: String = DateTimeHelper.getCurrentDateTimeStamp())
    {
        DatabaseAsyncTask(
            object : DatabaseTask<Unit>
            {
                override fun performOperation() = categoryDao.update(id, lastUsed)
            }
        ).execute()
    }

    /**
     * Updates only one Category where name = categoryName and ruleDomain = domain.
     */
    fun update(categoryName: String, domain: String, lastUsed: String = DateTimeHelper.getCurrentDateTimeStamp())
    {
        DatabaseAsyncTask(
            object : DatabaseTask<Unit>
            {
                override fun performOperation() = categoryDao.update(categoryName, domain, lastUsed)
            }
        ).execute()
    }

    fun update(category: Category)
    {
        DatabaseAsyncTask(
            object : DatabaseTask<Unit>
            {
                override fun performOperation() = categoryDao.update(category)
            }
        ).execute()
    }

    /**
     * Updates only one Category with empty ruleDomain.
     */
    fun updateEmptyCategory(categoryName: String, newDomain: String, lastUsed: String = DateTimeHelper.getCurrentDateTimeStamp())
    {
        DatabaseAsyncTask(
            object : DatabaseTask<Unit>
            {
                override fun performOperation() = categoryDao.updateEmptyCategory(categoryName, newDomain, lastUsed)
            }
        ).execute()
    }
}