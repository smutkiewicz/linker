package studios.aestheticapps.linker.extensions

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import studios.aestheticapps.linker.MainActivity.Companion.MY_PERMISSIONS_REQUEST_DRAW_OVERLAY



/**
 * Helper extension function for showing a [Toast]
 */
fun Activity.showToast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

/**
 * Permission for draw overlays is only needed for API >= 23
 */
fun Activity.checkForDrawOverlaysPermissions()
    = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Settings.canDrawOverlays(applicationContext) else true

/**
 * Opens app's settings menu, as DRAW_OVERLAY permission requires intent to Settings.
 */
fun Activity.createDrawOverlayPermissionsIntent()
{
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
    {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:$packageName")
        startActivityForResult(intent, MY_PERMISSIONS_REQUEST_DRAW_OVERLAY)
    }
}