package com.senzaproject.tosflagship

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.webkit.JavascriptInterface
import android.webkit.WebView
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors

/**
 * Meniru API `ksu` milik KernelSU WebUI supaya webroot/index.html
 * bisa jalan apa adanya di dalam aplikasi ini (butuh akses root).
 */
class KsuBridge(private val context: Context, private val webView: WebView) {

    private val pool = Executors.newCachedThreadPool()
    private val main = Handler(Looper.getMainLooper())

    @JavascriptInterface
    fun exec(cmd: String, options: String?, callback: String) {
        pool.execute {
            val r = Shell.run(cmd)
            val js = "window['" + callback.replace("'", "") + "'] && window['" + callback.replace("'", "") + "'](" +
                r.code + "," + JSONObject.quote(r.out) + "," + JSONObject.quote(r.err) + ")"
            main.post { webView.evaluateJavascript(js, null) }
        }
    }

    @JavascriptInterface
    fun listPackages(type: String): String {
        val pm = context.packageManager
        val list = pm.getInstalledApplications(0)
        val arr = JSONArray()
        list.filter { type == "all" || (it.flags and ApplicationInfo.FLAG_SYSTEM == 0) }
            .map { it.packageName }.sorted().forEach { arr.put(it) }
        return arr.toString()
    }

    @JavascriptInterface
    fun getPackagesInfo(json: String): String {
        val pm = context.packageManager
        val names = JSONArray(json)
        val out = JSONArray()
        for (i in 0 until names.length()) {
            val pkg = names.getString(i)
            try {
                val ai = pm.getApplicationInfo(pkg, 0)
                out.put(JSONObject().put("packageName", pkg).put("appLabel", pm.getApplicationLabel(ai).toString()))
            } catch (_: PackageManager.NameNotFoundException) {
                out.put(JSONObject().put("packageName", pkg).put("appLabel", ""))
            }
        }
        return out.toString()
    }

    @JavascriptInterface
    fun getPackagesIcons(json: String, size: Int): String {
        val pm = context.packageManager
        val names = JSONArray(json)
        val out = JSONArray()
        for (i in 0 until names.length()) {
            val pkg = names.getString(i)
            var icon: String? = null
            try {
                icon = "data:image/png;base64," + toBase64(pm.getApplicationIcon(pkg), size)
            } catch (_: Exception) {
            }
            out.put(JSONObject().put("packageName", pkg).put("icon", icon ?: JSONObject.NULL))
        }
        return out.toString()
    }

    private fun toBase64(d: Drawable, size: Int): String {
        val s = if (size > 0) size else 96
        val bmp = Bitmap.createBitmap(s, s, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        d.setBounds(0, 0, s, s)
        d.draw(c)
        val bos = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, bos)
        return Base64.encodeToString(bos.toByteArray(), Base64.NO_WRAP)
    }

    @JavascriptInterface
    fun toast(msg: String) {
        main.post { android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show() }
    }
}
