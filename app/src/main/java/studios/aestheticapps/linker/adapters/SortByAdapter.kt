package studios.aestheticapps.linker.adapters

import android.content.Context
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.persistence.link.LinkRepository.Companion.CATEGORY_COLUMN
import studios.aestheticapps.linker.persistence.link.LinkRepository.Companion.CREATED_COLUMN
import studios.aestheticapps.linker.persistence.link.LinkRepository.Companion.CREATED_LATEST_COLUMN
import studios.aestheticapps.linker.persistence.link.LinkRepository.Companion.DOMAIN_COLUMN
import studios.aestheticapps.linker.persistence.link.LinkRepository.Companion.TITLE_COLUMN

object SortByAdapter
{
    fun arrayIndexToColumnNameForView(context: Context, index: Int): String
    {
        val array = obtainSortByArray(context)
        return array[index]?: array[0]
    }

    fun columnNameToColumnNameForView(context: Context, column: String): String
    {
        val array = obtainSortByArray(context)

        return when (column)
        {
            TITLE_COLUMN -> array[0]
            CATEGORY_COLUMN -> array[1]
            DOMAIN_COLUMN -> array[2]
            CREATED_LATEST_COLUMN -> array[3]
            CREATED_COLUMN -> array[4]
            else -> array[0]
        }
    }

    fun columnNameToArrayIndex(column: String): Int
    {
        return when (column)
        {
            TITLE_COLUMN -> 0
            CATEGORY_COLUMN -> 1
            DOMAIN_COLUMN -> 2
            CREATED_LATEST_COLUMN -> 3
            CREATED_COLUMN -> 4
            else -> 0
        }
    }

    private fun obtainSortByArray(context: Context) = context.resources.getStringArray(R.array.sort_by_array)
}