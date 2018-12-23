package studios.aestheticapps.linker.utils

import android.app.Application
import android.content.Context
import android.widget.ArrayAdapter
import studios.aestheticapps.linker.model.Category
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.persistence.category.CategoryRepository
import java.util.*

/**
 * Empty Categories are only when noone set any domain in this particular Category.
 */
class CategoriesAdapter(application: Application)
{
    private val repository: CategoryRepository = CategoryRepository(application)

    fun obtainAdapter(context: Context): ArrayAdapter<String>
    {
        val adapter = buildAdapter(context)
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
     * It is assumed that this process is fired on new item's insert.
     */
    fun insertCategory(domain: String, categoryName: String, id: Int?)
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
     * It is assumed that this process is fired on item's delete.
     */
    fun deleteCategory(model: Link)
    {
        if (model.category == UNDEFINED) return

        val category: Category = repository.getCategoryWithDomain(model.category, model.domain)

        if (category.usages > 1)
        {
            category.usages = category.usages - 1
            repository.update(category)
        }
        else
        {
            repository.update(buildUnused(category))
        }
    }

    /**
     * Get list of Strings for Spinner
     */
    private fun obtainAllCategories(): List<String> = repository.getAllCategories()

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

    private fun buildAdapter(context: Context) = ArrayAdapter(
        context,
        android.R.layout.simple_spinner_item,
        obtainAllCategories()
    )

    private companion object
    {
        const val EMPTY_DOMAIN_RULE = ""
        const val UNDEFINED = "Undefined"
    }
}