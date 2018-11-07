package studios.aestheticapps.linker.utils

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

class NonSwipableViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs)
{
    var pagingEnabled: Boolean = false

    init
    {
        this.pagingEnabled = true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean
    {
        return if (this.pagingEnabled)
        {
            super.onTouchEvent(event)
        }
        else false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean
    {
        return if (this.pagingEnabled)
        {
            super.onInterceptTouchEvent(event)
        }
        else false
    }
}