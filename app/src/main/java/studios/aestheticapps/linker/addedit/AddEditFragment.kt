package studios.aestheticapps.linker.addedit

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.add_edit_content.*
import studios.aestheticapps.linker.MainActivity
import studios.aestheticapps.linker.R
import studios.aestheticapps.linker.floatingmenu.BubbleMenuService

class AddEditFragment : Fragment(), AddEditTaskContract.View
{
    private var presenter: AddEditTaskContract.Presenter = AddEditPresenter(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
        = inflater.inflate(R.layout.add_edit_content, container, false)

    override fun setPresenter(presenter: AddEditTaskContract.Presenter)
    {
        this.presenter = presenter
    }

    override fun createFab()
    {
        saveLinkFab.setOnClickListener {
            //TODO save and return to library
            true
        }
    }

    override fun hideBubbles()
    {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        ContextCompat.startActivity(context!!, intent, null)

        BubbleMenuService.destroyFloatingMenu(context!!)
    }
}