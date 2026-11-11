package com.senzaproject.tosflagship

import java.io.File

object Shell {
    data class Result(val code: Int, val out: String, val err: String)

    /** Jalankan perintah sebagai root lewat `su` (script dikirim lewat stdin). */
    fun run(cmd: String): Result = try {
        val p = ProcessBuilder("su").start()
        p.outputStream.bufferedWriter().use { it.write(cmd); it.write("\nexit\n") }
        var err = ""
        val t = Thread { err = p.errorStream.bufferedReader().readText() }
        t.start()
        val out = p.inputStream.bufferedReader().readText()
        t.join()
        Result(p.waitFor(), out, err)
    } catch (e: Exception) {
        Result(1, "", e.message ?: "su tidak tersedia")
    }

    fun hasRoot(): Boolean = run("id -u").out.trim() == "0"

    fun moduleInstalled(id: String): Boolean =
        run("[ -f /data/adb/modules/$id/module.prop ] && echo yes").out.trim() == "yes"

    /** Pasang ZIP modul lewat Magisk atau KernelSU/APatch. */
    fun installModule(zip: File): Result = run(
        "if command -v magisk >/dev/null 2>&1; then magisk --install-module '${zip.absolutePath}'; " +
        "elif [ -x /data/adb/ksud ]; then /data/adb/ksud module install '${zip.absolutePath}'; " +
        "elif [ -x /data/adb/apd ]; then /data/adb/apd module install '${zip.absolutePath}'; " +
        "else echo 'Magisk/KernelSU tidak ditemukan' >&2; exit 1; fi"
    )
}
