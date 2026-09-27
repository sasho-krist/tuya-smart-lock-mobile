package bg.sashokrist.smartlock

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)
        title = getString(R.string.setup_title)

        val input = findViewById<EditText>(R.id.url)
        val warning = findViewById<TextView>(R.id.warning)
        input.setText(Prefs.serverUrl(this) ?: "")

        fun updateWarning() {
            warning.visibility = if (input.text.trim().startsWith("http://")) View.VISIBLE else View.GONE
        }
        updateWarning()
        input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) = updateWarning()
        })

        findViewById<Button>(R.id.save).setOnClickListener {
            val url = Prefs.normalize(input.text.toString())
            if (url == null) {
                Toast.makeText(this, R.string.setup_invalid, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            Prefs.setServerUrl(this, url)
            startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            finish()
        }
    }
}
