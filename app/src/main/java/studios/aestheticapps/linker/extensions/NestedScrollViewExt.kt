package studios.aestheticapps.linker.extensions

import android.support.v4.widget.NestedScrollView

fun NestedScrollView.scrollToBottom()
{
    val lastChild = getChildAt(childCount - 1)
    val bottom = lastChild.bottom + paddingBottom
    val sy = scrollY
    val sh = height
    val delta = bottom - (sy + sh)

    smoothScrollBy(0, delta)
}