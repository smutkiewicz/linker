package studios.aestheticapps.linker.content.addedit

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_edit.*
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.content.UpdateViewCallback
import studios.aestheticapps.linker.content.addedit.AddEditFragment.Companion.MODE_EDIT
import studios.aestheticapps.linker.extensions.checkForDrawOverlayPermissions
import studios.aestheticapps.linker.extensions.createDrawOverlayPermissionsIntent
import studios.aestheticapps.linker.floatingmenu.BubbleMenuService
import studios.aestheticapps.linker.floatingmenu.theme.BubbleTheme
import studios.aestheticapps.linker.floatingmenu.theme.BubbleThemeManager
import studios.aestheticapps.linker.model.Link
import studios.aestheticapps.linker.model.Link.CREATOR.PARCEL_LINK

class EditActivity : AppCompatActivity(), AddEditFragment.AddEditCallback, UpdateViewCallback
{
    private var edited: Boolean = false
    private var isReturnToBubblesNeeded = false

    private lateinit var fragment: AddEditFragment

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
        }

        if (savedInstanceState == null)
        {
            initFragment()
        }
        else
        {
            restoreFragment(savedInstanceState)
        }
    }

    override fun onResume()
    {
        super.onResume()
        isReturnToBubblesNeeded = isBubbleServiceRunning()
        if(isReturnToBubblesNeeded) closeBubbles()
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)

        outState.putBoolean(EDITED, edited)
        supportFragmentManager.putFragment(outState, FRAGMENT, fragment)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        when (item.itemId)
        {
            android.R.id.home ->
            {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed()
    {
        if (edited)
        {
            buildExitDialogAndConfirmDelete()
        }
        else
        {
            super.onBackPressed()
        }
    }

    override fun returnToMainView()
    {
        NavUtils.navigateUpFromSameTask(this)
        if (isReturnToBubblesNeeded) openBubbles()
    }

    override fun onEdited()
    {
        if (!edited)
        {
            edited = true
        }
    }

    override fun onUpdateView() {}

    private fun initFragment()
    {
        val model = intent.getParcelableExtra<Link>(PARCEL_LINK)

        fragment = AddEditFragment.newInstance(MODE_EDIT)
        fragment.arguments?.putParcelable(PARCEL_LINK, model)

        replaceFragment()
    }

    private fun restoreFragment(savedInstanceState: Bundle)
    {
        edited = savedInstanceState.getBoolean(EDITED)
        fragment = supportFragmentManager.getFragment(savedInstanceState, FRAGMENT) as AddEditFragment

        replaceFragment()
    }

    private fun replaceFragment()
    {
        supportFragmentManager
            .beginTransaction()
            .apply {
                replace(R.id.fragmentContainer, fragment)
                commit()
            }
    }

    private fun buildExitDialogAndConfirmDelete()
    {
        val builder = AlertDialog
            .Builder(this)
            .apply {
                setTitle(R.string.edit_confirm_exit)
                setIcon(R.mipmap.ic_launcher)
                setMessage(R.string.edit_message_confirm_exit)
                setNegativeButton(R.string.edit_save) { _, _ ->  }
                setPositiveButton(R.string.edit_discard) { _, _ -> returnToMainView() }
            }

        builder.apply {
            create()
            show()
        }
    }

    private fun isBubbleServiceRunning(): Boolean
    {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (BubbleMenuService::class.java.name == service.service.className) return true
        }

        return false
    }

    private fun openBubbles()
    {
        if(checkForDrawOverlayPermissions())
        {
            initThemeManager()
            BubbleMenuService.showFloatingMenu(this)
            finish()
        }
        else
        {
            createDrawOverlayPermissionsIntent()
        }
    }

    private fun closeBubbles()
    {
        BubbleMenuService.destroyFloatingMenu(this)
    }

    private fun initThemeManager()
    {
        val defaultTheme = BubbleTheme(
            ContextCompat.getColor(this, R.color.colorPrimary),
            ContextCompat.getColor(this, R.color.colorPrimary)
        )

        BubbleThemeManager.init(defaultTheme)
    }

    private companion object
    {
        const val FRAGMENT = "AddEditFragment"
        const val EDITED = "edited"
    }
}
