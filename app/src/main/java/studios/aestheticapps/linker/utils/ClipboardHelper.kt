package studios.aestheticapps.linker.utils

import android.content.ClipboardManager
import android.content.Context

class ClipboardHelper(val context: Context)
{
    private val clipboardManager: ClipboardManager = obtainClipboardManager()

    fun obtainClipboardContent() = clipboardManager.text?.toString() ?: ""

    fun containsNewContent(savedText: String): Boolean
    {
        val clipboardText = clipboardManager.text.toString()
        return savedText != clipboardText
    }

    private fun obtainClipboardManager() = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
}