package com.senzaproject.tosflagship

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import java.io.File
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {

    companion object {
        const val MODULE_ID = "TOS_Flagship_Tweaks"
    }

    private lateinit var web: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        web = WebView(this).apply {
            setBackgroundColor(Color.parseColor("#0A0D1A"))
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = false
            settings.cacheMode = WebSettings.LOAD_NO_CACHE
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            addJavascriptInterface(KsuBridge(this@MainActivity, this), "ksu")
        }
        setContentView(web)
        web.loadUrl("file:///android_asset/webroot/index.html")

        Executors.newSingleThreadExecutor().execute { checkRootAndModule() }
    }

    private fun checkRootAndModule() {
        if (!Shell.hasRoot()) {
            runOnUiThread {
                AlertDialog.Builder(this)
                    .setTitle("Akses root diperlukan")
                    .setMessage("Aplikasi ini mengubah pengaturan sistem lewat root (Magisk/KernelSU/APatch). Beri izin root lalu buka ulang aplikasi.")
                    .setPositiveButton("OK", null).show()
            }
            return
        }
        if (Shell.moduleInstalled(MODULE_ID)) return
        runOnUiThread {
            AlertDialog.Builder(this)
                .setTitle("Modul belum terpasang")
                .setMessage("Fitur aplikasi memakai skrip dari modul TOS Flagship Tweaks. Pasang sekarang? Setelah selesai, reboot HP.")
                .setPositiveButton("Pasang") { _, _ -> Executors.newSingleThreadExecutor().execute { installBundledModule() } }
                .setNegativeButton("Nanti", null).show()
        }
    }

    private fun installBundledModule() {
        val zip = File(cacheDir, "module.zip")
        assets.open("module.zip").use { i -> zip.outputStream().use { o -> i.copyTo(o) } }
        val r = Shell.installModule(zip)
        runOnUiThread {
            AlertDialog.Builder(this)
                .setTitle(if (r.code == 0) "Modul terpasang" else "Gagal memasang modul")
                .setMessage(if (r.code == 0) "Reboot HP agar modul aktif." else (r.err.ifBlank { r.out }).takeLast(400))
                .setPositiveButton("OK", null).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (web.canGoBack()) web.goBack() else super.onBackPressed()
    }
}
