package studios.aestheticapps.linker.utils

import android.content.Context
import studios.aestheticapps.linker.persistence.link.LinkRepository.Companion.TITLE_COLUMN

object PrefsHelper
{
    private const val PREFS = "prefs"
    private const val PREF_ORDER_BY = "order_by"
    private const val PREF_LATEST_URL = "latest_url"
    private const val PREF_LATEST_VIEW = "latest_view"

    const val VIEW_BUBBLE = "bubble"
    const val VIEW_APP = "app"

    fun obtainOrderByColumn(context: Context) = context
        .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        .getString(PREF_ORDER_BY, TITLE_COLUMN)

    fun obtainLatestParsedUrl(context: Context) = context
        .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        .getString(PREF_LATEST_URL, "")

    fun obtainLatestView(context: Context) = context
        .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        .getString(PREF_LATEST_VIEW, VIEW_APP)

    fun setOrderByColumn(context: Context, column: String) = putStringValue(context, PREF_ORDER_BY, column)

    fun setLatestParsedUrl(context: Context, url: String) = putStringValue(context, PREF_LATEST_URL, url)

    fun setLatestView(context: Context, viewType: String) = putStringValue(context, PREF_LATEST_VIEW, viewType)

    private fun putStringValue(context: Context, pref: String, value: String)
    {
        context
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(pref, value)
            .apply()
    }
}