package studios.aestheticapps.linker.extensions

import android.support.v4.app.Fragment
import android.view.ViewGroup

fun Fragment.disableChildrenOf(layout: ViewGroup?)
{
    layout?.let {
        for (i in 0 until layout.childCount)
        {
            val child = layout.getChildAt(i)
            child.isEnabled = false
        }
    }
}

fun Fragment.enableChildrenOf(layout: ViewGroup?)
{
    layout?.let {
        for (i in 0 until layout.childCount)
        {
            val child = layout.getChildAt(i)
            child.isEnabled = true
        }
    }
}