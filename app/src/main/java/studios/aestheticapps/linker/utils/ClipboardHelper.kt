package studios.aestheticapps.linker.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast

class ClipboardHelper(val context: Context)
{
    private val clipboardManager: ClipboardManager = obtainClipboardManager()

    fun obtainClipboardContent() = clipboardManager.text?.toString() ?: ""

    fun containsNewContent(savedText: String): Boolean
    {
        val clipboardText = clipboardManager.text.toString()
        return savedText != clipboardText
    }

    fun copyToCliboard(content: String)
    {
        val clip = ClipData.newPlainText("Linker", content)
        clipboardManager.primaryClip = clip
        showToast(content)
    }

    private fun obtainClipboardManager() = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    private fun showToast(content: String) = Toast.makeText(context, "Copied \"$content\" to Clipboard.", Toast.LENGTH_LONG).show()
}