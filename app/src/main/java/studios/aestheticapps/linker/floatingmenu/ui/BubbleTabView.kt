package studios.aestheticapps.linker.floatingmenu.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.util.TypedValue
import android.view.View

/**
 * Visual representation of a top-level tab in a Hover menu.
 */
class BubbleTabView(private val myContext: Context,
                    private val circleDrawable: Drawable,
                    private var iconDrawable: Drawable) : View(myContext)
{
    private var bckgroundColor: Int = 0
    private var foregroundColor: Int = 0
    private var iconInsetLeft: Int = 0
    private var iconInsetTop: Int = 0
    private var iconInsetRight: Int = 0
    private var iconInsetBottom: Int = 0

    init
    {
        val insetsDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            10f,
            myContext.resources.displayMetrics
        ).toInt()

        iconInsetBottom = insetsDp
        iconInsetRight = iconInsetBottom
        iconInsetTop = iconInsetRight
        iconInsetLeft = iconInsetTop
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int)
    {
        super.onSizeChanged(w, h, oldw, oldh)

        // Make circle as large as View minus padding.
        circleDrawable.setBounds(
            paddingLeft,
            paddingTop,
            w - paddingRight,
            h - paddingBottom
        )

        // Re-size the icon as necessary.
        updateIconBounds()
        invalidate()
    }

    override fun onDraw(canvas: Canvas)
    {
        circleDrawable.draw(canvas)
        iconDrawable.draw(canvas)
    }

    fun setTabBackgroundColor(@ColorInt backgroundColor: Int)
    {
        this.bckgroundColor = backgroundColor
        circleDrawable.setColorFilter(this.bckgroundColor, PorterDuff.Mode.SRC_ATOP)
    }

    fun setTabForegroundColor(@ColorInt foregroundColor: Int)
    {
        this.foregroundColor = foregroundColor
        iconDrawable.setColorFilter(this.foregroundColor, PorterDuff.Mode.SRC_ATOP)
    }

    fun setIcon(icon: Drawable)
    {
        iconDrawable = icon
        iconDrawable.setColorFilter(foregroundColor, PorterDuff.Mode.SRC_ATOP)

        updateIconBounds()
        invalidate()
    }

    private fun updateIconBounds()
    {
        val bounds = Rect(circleDrawable.bounds)
        bounds.set(
            bounds.left + iconInsetLeft,
            bounds.top + iconInsetTop,
            bounds.right - iconInsetRight,
            bounds.bottom - iconInsetBottom
        )

        iconDrawable.bounds = bounds
    }
}