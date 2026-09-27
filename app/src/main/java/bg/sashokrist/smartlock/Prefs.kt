package bg.sashokrist.smartlock

import android.content.Context

object Prefs {
    private const val FILE = "smartlock"
    private const val KEY_URL = "server_url"

    fun serverUrl(context: Context): String? =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getString(KEY_URL, null)

    fun setServerUrl(context: Context, url: String) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putString(KEY_URL, url).apply()
    }

    /** Добавя https:// ако липсва схема и / накрая, за да работят относителните пътища (api.php). */
    fun normalize(input: String): String? {
        var url = input.trim()
        if (url.isEmpty()) return null
        if (!url.startsWith("http://") && !url.startsWith("https://")) url = "https://$url"
        val uri = android.net.Uri.parse(url)
        if (uri.host.isNullOrBlank()) return null
        if (url.endsWith("/index.php")) url = url.removeSuffix("index.php")
        if (!url.endsWith("/")) url += "/"
        return url
    }
}
