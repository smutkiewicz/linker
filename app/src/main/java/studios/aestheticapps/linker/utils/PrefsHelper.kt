package studios.aestheticapps.linker.utils

import android.content.Context
import studios.aestheticapps.linker.persistence.link.LinkRepository.Companion.TITLE_COLUMN

object PrefsHelper
{
    private const val PREFS = "prefs"
    private const val PREF_ORDER_BY = "order_by"
    private const val PREF_LATEST_URL = "latest_url"
    private const val PREF_LATEST_VIEW = "latest_view"
    private const val PREF_FIRST_RUN = "first_run"
    private const val PREF_SHORTCUTS = "pref_paste_cut_shortcuts"

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

    fun obtainShortcuts(context: Context) = context
        .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        .getBoolean(PREF_SHORTCUTS, true)

    fun obtainFirstRun(context: Context): Boolean
    {
        val firstRun = context
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(PREF_FIRST_RUN, true)

        // return firstRun
        // TODO only for presentation purposes.
        return true
    }

    fun obtainSharedPrefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun setOrderByColumn(context: Context, column: String) = putStringValue(context, PREF_ORDER_BY, column)

    fun setLatestParsedUrl(context: Context, url: String) = putStringValue(context, PREF_LATEST_URL, url)

    fun setLatestView(context: Context, viewType: String) = putStringValue(context, PREF_LATEST_VIEW, viewType)

    fun setShortcuts(context: Context, isEnabled: Boolean) = putBooleanValue(context, PREF_SHORTCUTS, isEnabled)

    fun setFirstTimeRun(context: Context) = putBooleanValue(context, PREF_FIRST_RUN, false)

    private fun putStringValue(context: Context, pref: String, value: String)
    {
        context
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(pref, value)
            .apply()
    }

    private fun putBooleanValue(context: Context, pref: String, value: Boolean)
    {
        context
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(pref, value)
            .apply()
    }
}