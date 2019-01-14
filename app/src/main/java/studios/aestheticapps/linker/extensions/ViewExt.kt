package studios.aestheticapps.linker.extensions

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

// Returns true if keyboard was present before hiding
fun View.hideSoftInput(context: Context): Boolean
{
    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    return imm.hideSoftInputFromWindow(windowToken, 0)
}