package com.example.minecraftstatus.View.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.minecraftstatus.R
import kotlinx.android.synthetic.main.activity_webview.*

class WebviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        webview.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
        }

        val url = intent.getStringExtra("url")

        webview.loadUrl(url.toString())

    }

    override fun onBackPressed() {
        if (webview.canGoBack())
        {
            webview.goBack()
        }
        else
        {
            finish()
        }
    }

}