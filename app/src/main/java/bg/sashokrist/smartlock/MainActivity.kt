package bg.sashokrist.smartlock

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {

    private lateinit var web: WebView
    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var errorView: LinearLayout
    private var serverUrl: String = ""

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val url = Prefs.serverUrl(this)
        if (url == null) {
            startActivity(Intent(this, SetupActivity::class.java))
            finish()
            return
        }
        serverUrl = url

        setContentView(R.layout.activity_main)
        web = findViewById(R.id.web)
        swipe = findViewById(R.id.swipe)
        errorView = findViewById(R.id.error)

        CookieManager.getInstance().setAcceptCookie(true)

        web.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = false
            allowContentAccess = false
        }
        web.addJavascriptInterface(AndroidBridge(this), "AndroidBridge")
        // празен WebChromeClient = WebView показва стандартните диалози за confirm()/prompt()
        web.webChromeClient = WebChromeClient()
        web.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val target = request.url
                // само страниците на сървъра се отварят в приложението; външните — в браузъра
                if (target.host == Uri.parse(serverUrl).host) return false
                startActivity(Intent(Intent.ACTION_VIEW, target))
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                swipe.isRefreshing = false
                CookieManager.getInstance().flush()
            }

            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                if (request.isForMainFrame) showError(error.description?.toString() ?: "")
            }
        }

        swipe.setOnRefreshListener { reload() }
        swipe.setOnChildScrollUpCallback { _, _ -> web.scrollY > 0 }

        findViewById<Button>(R.id.retry).setOnClickListener { reload() }
        findViewById<Button>(R.id.change_url).setOnClickListener { openSetup() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (web.canGoBack()) {
                    web.goBack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        if (savedInstanceState != null) web.restoreState(savedInstanceState) else web.loadUrl(serverUrl)
    }

    private fun reload() {
        errorView.visibility = View.GONE
        if (web.url.isNullOrBlank()) web.loadUrl(serverUrl) else web.reload()
    }

    private fun showError(detail: String) {
        swipe.isRefreshing = false
        findViewById<TextView>(R.id.error_detail).text = "$serverUrl\n$detail"
        errorView.visibility = View.VISIBLE
    }

    private fun openSetup() {
        startActivity(Intent(this, SetupActivity::class.java))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_reload -> { reload(); true }
        R.id.action_change_url -> { openSetup(); true }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        val url = Prefs.serverUrl(this)
        if (url != null && url != serverUrl && ::web.isInitialized) {
            serverUrl = url
            errorView.visibility = View.GONE
            web.loadUrl(serverUrl)
        }
    }

    override fun onPause() {
        super.onPause()
        CookieManager.getInstance().flush()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (::web.isInitialized) web.saveState(outState)
    }
}
