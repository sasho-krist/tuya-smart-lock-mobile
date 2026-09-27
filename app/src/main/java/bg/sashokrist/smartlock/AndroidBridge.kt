package bg.sashokrist.smartlock

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.webkit.JavascriptInterface
import android.widget.Toast

/**
 * Достъпен от страницата като window.AndroidBridge. В WebView по http навигаторът няма
 * clipboard/share API, затова страницата ползва тези методи, когато ги има.
 */
class AndroidBridge(private val activity: Activity) {

    @JavascriptInterface
    fun share(text: String) {
        activity.runOnUiThread {
            val send = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            activity.startActivity(Intent.createChooser(send, activity.getString(R.string.share_title)))
        }
    }

    @JavascriptInterface
    fun copy(text: String) {
        activity.runOnUiThread {
            val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("code", text))
            Toast.makeText(activity, R.string.copied, Toast.LENGTH_SHORT).show()
        }
    }
}
