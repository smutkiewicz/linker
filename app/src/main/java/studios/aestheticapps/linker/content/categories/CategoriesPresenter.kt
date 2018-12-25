package studios.aestheticapps.linker.content.categories

import android.app.Application
import studios.aestheticapps.linker.adapters.CategoriesAdapter
import java.util.*

class CategoriesPresenter(val view: CategoriesContract.View) : CategoriesContract.Presenter
{
    private lateinit var categoriesAdapter: CategoriesAdapter

    init
    {
        view.presenter = this
    }

    override fun start(application: Application)
    {
        // Part of logic responsible for parsing metadata and category.
        categoriesAdapter = CategoriesAdapter(application)
    }

    override fun getAll() = LinkedList(categoriesAdapter.obtainAllCategories())

    override fun addItem(categoryName: String)
    {
        // TODO add item to db
    }

    override fun removeItem(categoryName: String)
    {
        // TODO remove item from db
    }
}