package com.example.workandstudy_app.tienich

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.workandstudy_app.R
import androidx.activity.OnBackPressedCallback

class WebViewActivity : AppCompatActivity() {
    private lateinit var urlPage: String
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.webview_activity)
        val webView: WebView=findViewById(R.id.web_view)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient= WebViewClient()
        urlPage= intent.getStringExtra("URL").toString()
        webView.loadUrl(urlPage)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    finish()
                }
            }
        })
    }
}