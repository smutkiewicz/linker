package studios.aestheticapps.linker.floatingmenu.ui

import android.app.KeyguardManager
import android.content.Context
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.os.Build
import android.os.PowerManager
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import studios.aestheticapps.linker.R

/**
 * Creates the head layer view which is displayed directly on window manager.
 * It means that the view is above every application's view on your phone -
 * until another application does the same.
 */
class BubbleDialog(myContext: Context,
                   val title: String,
                   val message: String,
                   val positiveBtnTitle: String,
                   val negativeBtnTitle: String,
                   val callback: BubbleDialogCallback) : View(myContext)
{
    private var frameLayout: FrameLayout? = FrameLayout(context)
    private var windowManager: WindowManager? = null

    init
    {
        addToWindowManager()
        initTitleAndMessage()
        initPositiveBtn()
        initNegativeBtn()
        initOnCancel()
    }

    override fun dispatchConfigurationChanged(newConfig: Configuration?)
    {
        super.dispatchConfigurationChanged(newConfig)
        destroy()
    }

    override fun dispatchKeyEvent(keyEvent: KeyEvent?): Boolean
    {
        super.dispatchKeyEvent(keyEvent)

        return if (keyEvent?.action == KeyEvent.ACTION_UP && keyEvent.keyCode == KeyEvent.KEYCODE_BACK)
        {
            destroy()
            true
        }
        else false
    }

    fun destroy()
    {
        val isAttachedToWindow: Boolean? = frameLayout?.isAttachedToWindow
        if (isAttachedToWindow != null && isAttachedToWindow)
        {
            windowManager!!.removeView(frameLayout)
            frameLayout = null
        }
    }

    private fun addToWindowManager()
    {
        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        }
        else
        {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.CENTER
        frameLayout?.fitsSystemWindows = true

        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager!!.addView(frameLayout, params)

        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        layoutInflater.inflate(R.layout.bubble_dialog, frameLayout)
    }

    private fun initTitleAndMessage()
    {
        val titleTv = frameLayout?.findViewById<TextView>(R.id.dialogTitleTv)
        val msgTv = frameLayout?.findViewById<TextView>(R.id.dialogMsgTv)

        titleTv?.text = title
        msgTv?.text = message
    }

    private fun initNegativeBtn()
    {
        val negativeBtn = frameLayout?.findViewById<Button>(R.id.negativeBtn)
        negativeBtn?.apply {
            text = negativeBtnTitle
            setOnClickListener {
                callback.onNegativeBtnPressed()
                destroy()
            }
        }
    }

    private fun initPositiveBtn()
    {
        val positiveBtn = frameLayout?.findViewById<Button>(R.id.positiveBtn)
        positiveBtn?.apply {
            text = positiveBtnTitle
            setOnClickListener {
                callback.onPositiveBtnPressed()
                destroy()
            }
        }
    }

    private fun initOnCancel()
    {
        val backgroundIv = frameLayout?.findViewById<ImageView>(R.id.backgroundIv)
        backgroundIv?.setOnClickListener {
            callback.onCancel()
            destroy()
        }
    }

    interface BubbleDialogCallback
    {
        fun onPositiveBtnPressed()
        fun onNegativeBtnPressed()
        fun onCancel() {} // optional
    }

    companion object
    {
        fun draw(context: Context,
                 title: String, message: String,
                 positiveBtnTitle: String, negativeBtnTitle: String,
                 callback: BubbleDialogCallback): BubbleDialog?
        {
            val myKM = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            val isScreenOn = powerManager.isScreenOn

            // logic for preventing unnecessary drawing when device has locked or has screen off
            if (!myKM.inKeyguardRestrictedInputMode())
            {
                // device is not locked
                if (isScreenOn)
                {
                    return BubbleDialog(
                        context,
                        title = title,
                        message = message,
                        positiveBtnTitle = positiveBtnTitle,
                        negativeBtnTitle = negativeBtnTitle,
                        callback = callback
                    )
                }
            }

            return null
        }
    }
}