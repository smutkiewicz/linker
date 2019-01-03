package studios.aestheticapps.linker.extensions

import android.view.ViewGroup

fun ViewGroup.disableChildren()
{
    this.let {
        for (i in 0 until childCount)
        {
            val child = getChildAt(i)
            child.isEnabled = false
        }
    }
}

fun ViewGroup.enableChildren()
{
    this.let {
        for (i in 0 until childCount)
        {
            val child = getChildAt(i)
            child.isEnabled = true
        }
    }
}