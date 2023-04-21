package org.emotionalmusic.yandexauth

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.yandex.authsdk.YandexAuthException
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthSdk

class MainActivity : AppCompatActivity() {
    private val REQUEST_LOGIN_SDK = 1
    private var token = ""
    private lateinit var context: Context
    private lateinit var sdk: YandexAuthSdk

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        context = this

        sdk = YandexAuthSdk(
            context,
            YandexAuthOptions(context, true, 0)
        )

        val signInButton = findViewById<Button>(R.id.button_sign_in)
        signInButton.setOnClickListener {
            ActivityCompat.startActivityForResult(
                this,
                sdk.createLoginIntent(YandexAuthLoginOptions.Builder().build()),
                REQUEST_LOGIN_SDK,
                null
            )
        }

        val copyButton = findViewById<Button>(R.id.button_copy)
        copyButton.setOnClickListener {
            copyToken(context)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_LOGIN_SDK) {
            try {
                val yandexAuthToken = sdk.extractToken(resultCode, data)
                if (yandexAuthToken != null) {
                    token = yandexAuthToken.value
                }
            } catch (e: YandexAuthException) {
                Log.e("Auth error", "error")
            }
        }

        if (token != "") {
            val copyButton = findViewById<Button>(R.id.button_copy)
            copyButton.setTextColor(getColor(R.color.white))
        }
    }

    private fun copyToken(context: Context) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Token", token)
        clipboardManager.setPrimaryClip(clip)
    }
}