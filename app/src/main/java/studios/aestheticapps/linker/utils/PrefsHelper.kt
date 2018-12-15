package studios.aestheticapps.linker.utils

import android.content.Context
import studios.aestheticapps.linker.persistence.LinkRepository.Companion.TITLE_COLUMN

object PrefsHelper
{
    private const val PREFS = "prefs"
    private const val PREF_ORDER_BY = "order_by"
    private const val PREF_LATEST_URL = "latest_url"

    fun obtainOrderByColumn(context: Context) = context
        .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        .getString(PREF_ORDER_BY, TITLE_COLUMN)

    fun obtainLatestParsedUrl(context: Context) = context
        .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        .getString(PREF_LATEST_URL, "")

    fun setOrderByColumn(context: Context, column: String) = putStringValue(context, PREF_ORDER_BY, column)

    fun setLatestParsedUrl(context: Context, url: String) = putStringValue(context, PREF_LATEST_URL, url)

    private fun putStringValue(context: Context, pref: String, value: String)
    {
        context
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(pref, value)
            .apply()
    }
}