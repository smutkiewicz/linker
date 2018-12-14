package studios.aestheticapps.linker.utils

import android.content.Context
import studios.aestheticapps.linker.persistence.LinkRepository.Companion.TITLE_COLUMN

object PrefsHelper
{
    private const val PREFS = "prefs"
    private const val PREF_ORDER_BY = "order_by"

    fun obtainOrderByColumn(context: Context): String
    {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.getString(PREF_ORDER_BY, TITLE_COLUMN)
    }
}