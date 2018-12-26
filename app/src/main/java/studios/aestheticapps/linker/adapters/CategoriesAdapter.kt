package studios.aestheticapps.linker.adapters

import android.app.Application
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.model.Category
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.persistence.category.CategoryRepository
import studios.aestheticapps.linker.persistence.link.LinkRepository
import studios.aestheticapps.linker.utils.DateTimeHelper
import java.util.*

/**
 * Empty Categories are only when noone set any domain in this particular Category.
 */
class CategoriesAdapter(private val application: Application)
{
    private val repository: CategoryRepository = CategoryRepository(application)

    fun obtainAdapter(): ArrayAdapter<String>
    {
        val adapter = buildAdapter(application.applicationContext)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        return adapter
    }

    /**
     * It is assumed that this process is fired on building new item's for user view.
     */
    fun obtainCategory(domain: String): Category
        = chooseBest(
            repository.getCategoriesByDomain(domain)
        )

    /**
     * New empty Category insert.
     */
    fun insertCategory(categoryName: String): Boolean
    {
        // Check if categoryName is unique.
        val allCategories = obtainAllCategories()
        if (allCategories.contains(categoryName)) return false

        // New empty Category
        repository.insert(buildUnused(categoryName))

        return true
    }

    /**
     * It is assumed that this process is fired on new item's insert.
     */
    fun insertItemWithCategory(domain: String, categoryName: String, id: Int?)
    {
        if (categoryName == UNDEFINED) return

        id?.let {
            repository.update(id)
            return
        }

        val domains = repository.getDomainsByCategory(categoryName)

        if (domains.contains(domain))
        {
            // Update for particular category-ruleDomain.
            repository.update(categoryName, domain)
        }
        else
        {
            // When there is only one domain in the list, it means that we have to check for empty domain.
            if (domains.size == 1 && domains[0] == EMPTY_DOMAIN_RULE)
            {
                repository.updateEmptyCategory(categoryName, domain)
                return
            }

            // 1 or more non-empty ruleDomains in database.
            repository.insert(
                Category(
                    name = categoryName,
                    ruleDomain = domain,
                    usages = 1,
                    lastUsed = DateTimeHelper.getCurrentDateTimeStamp()
                )
            )
        }
    }

    /**
     * Edit Category logic when user edits an existing Link.
     */
    fun editItemWithCategory(newItem: Link)
    {
        // Get old item from Link table and check if the category was changed.
        val linkRepository = LinkRepository(application)
        val oldItem = linkRepository.getById(newItem.id)

        if (oldItem.category == newItem.category) return

        // Delete usages of old category from domain.
        deleteItemWithCategory(oldItem)

        // Insert usages of new category with this domain.
        insertItemWithCategory(newItem.domain, newItem.category, null)
    }

    /**
     * Permanently delete particular Category.
     */
    fun deleteCategory(categoryName: String)
    {
        // Update all Link entries with this Category to 'Undefined'.
        val linkRepository = LinkRepository(application)
        linkRepository.updateDeletedCategoryEntries(categoryName)

        // Delete all category entries in category table.
        repository.deleteCategory(categoryName)
    }

    /**
     * It is assumed that this process is fired on item's delete.
     */
    fun deleteItemWithCategory(model: Link)
    {
        if (model.category == UNDEFINED) return

        val category = repository.getCategoryWithDomain(model.category, model.domain)

        if (category.usages > 1)
        {
            category.usages = category.usages - 1
            repository.update(category)
        }
        else
        {
            val domainsInThisCategory = repository.getDomainsByCategory(model.category)

            if (domainsInThisCategory.size == 1)
            {
                // If there are no more domains with this Category, just update item and switch to unused state.
                repository.update(buildUnused(category))
            }
            else
            {
                // If there are more domains with this Category, delete item.
                repository.delete(category.id)
            }
        }
    }

    /**
     * Get list of Strings for Spinner
     */
    fun obtainAllCategories(): List<String> = repository.getAllCategories()

    /**
     * Categories are pre-sorted by Repository in order: 1. usages; 2. lastUsed; 3. id;
     */
    private fun chooseBest(categories: LinkedList<Category>): Category
    {
        if (categories.isEmpty()) return buildUndefined()

        return categories[0]
    }

    private fun buildUndefined() = Category(name = "Undefined", ruleDomain = "")

    private fun buildUnused(category: Category) = Category(
        id = category.id,
        name = category.name,
        ruleDomain = EMPTY_DOMAIN_RULE,
        lastUsed = DateTimeHelper.getDateTimeNever(),
        usages = 0
    )

    private fun buildUnused(categoryName: String) = Category(
        id = 0,
        name = categoryName,
        ruleDomain = EMPTY_DOMAIN_RULE,
        lastUsed = DateTimeHelper.getDateTimeNever(),
        usages = 0
    )

    private fun buildAdapter(context: Context) = ArrayAdapter(
        context,
        android.R.layout.simple_spinner_item,
        obtainAllCategories()
    )

    class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>()
    {
        var elements: MutableList<String> = LinkedList()
            set(value)
            {
                field = value
                notifyDataSetChanged()
            }

        override fun getItemCount() = elements.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
        {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.category_item,
                parent,
                false
            )

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int)
        {
            holder.apply {
                categoryName = elements[position]
                titleTv.text = categoryName
            }
        }

        fun removeItem(position: Int)
        {
            elements.removeAt(position)
            notifyItemRemoved(position)
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
        {
            lateinit var categoryName: String
            val titleTv: TextView = itemView.findViewById(R.id.categoryTitleTv)
        }
    }

    private companion object
    {
        const val EMPTY_DOMAIN_RULE = ""
        const val UNDEFINED = "Undefined"
    }
}