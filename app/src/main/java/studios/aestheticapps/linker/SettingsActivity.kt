package studios.aestheticapps.linker

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Html
import studios.aestheticapps.linker.utils.PrefsHelper

class SettingsActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        if (fragmentManager.findFragmentById(android.R.id.content) == null)
        {
            fragmentManager
                .beginTransaction()
                .add(android.R.id.content, SettingsFragment())
                .commit()
        }

        createActionBar()
    }

    private fun createActionBar() = supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    class SettingsFragment : PreferenceFragment()
    {
        private lateinit var sp: SharedPreferences

        override fun onCreate(savedInstanceState: Bundle?)
        {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preferences)
            sp = PrefsHelper.obtainSharedPrefs(activity)

            setShortcutsPreferenceListener()
            setInfoPreferenceListener()
        }

        private fun setShortcutsPreferenceListener()
        {
            val pref = preferenceManager.findPreference(SHORTCUTS)

            pref!!.setOnPreferenceChangeListener { _, newValue ->
                PrefsHelper.setShortcuts(activity, newValue as Boolean)
                true
            }
        }

        @SuppressLint("StringFormatMatches")
        private fun setInfoPreferenceListener()
        {
            val info = preferenceManager.findPreference(INFO)
            info!!.summary = getString(R.string.info_summary, BuildConfig.VERSION_NAME)

            info.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                onInfoPreferenceClicked()
                return@OnPreferenceClickListener true
            }
        }

        @SuppressLint("StringFormatMatches")
        private fun onInfoPreferenceClicked()
        {
            val builder = AlertDialog.Builder(activity)
            val message = Html.fromHtml(resources.getString(R.string.info_author, BuildConfig.VERSION_NAME))

            builder.apply {
                setMessage(message)
                setTitle(R.string.info_author_title)
                setIcon(R.mipmap.ic_launcher_round)

                setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            }.create().show()
        }

        private companion object
        {
            const val SHORTCUTS = "pref_paste_cut_shortcuts"
            const val INFO = "pref_info"
        }
    }
}