package com.sharath.sacheatboard

import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import rikka.shizuku.Shizuku
import kotlin.concurrent.thread

/**
 * Sends a cheat to the game as real key events via Shizuku (shell `input keyevent`).
 * The game only reads hardware-keyboard key events, so this is the closest thing to typing.
 */
object KeyInjector {

    const val SHIZUKU_PKG = "moe.shizuku.privileged.api"
    private val main = Handler(Looper.getMainLooper())

    /** Shizuku is running (binder alive). */
    fun running(): Boolean = runCatching { Shizuku.pingBinder() }.getOrDefault(false)

    /** Shizuku is running and this app has been granted access. */
    fun ready(): Boolean = running() && runCatching {
        !Shizuku.isPreV11() && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
    }.getOrDefault(false)

    fun requestPermission() {
        runCatching { Shizuku.requestPermission(100) }
    }

    private fun keyCode(c: Char): Int? = when (c) {
        in 'A'..'Z' -> 29 + (c - 'A')   // KEYCODE_A = 29
        in 'a'..'z' -> 29 + (c - 'a')
        in '0'..'9' -> 7 + (c - '0')    // KEYCODE_0 = 7
        else -> null
    }

    /** Types [text] into whatever app has focus. [done] runs on the main thread: (ok, message). */
    fun type(text: String, done: (Boolean, String) -> Unit) {
        val codes = text.mapNotNull { keyCode(it) }
        if (codes.isEmpty()) { done(false, "nothing to type"); return }
        thread {
            var ok = false
            var msg: String
            try {
                // One `input` call per key keeps the key presses naturally spaced.
                val cmd = "for k in ${codes.joinToString(" ")}; do input keyboard keyevent \$k; done"
                val m = Shizuku::class.java.getDeclaredMethod(
                    "newProcess",
                    Array<String>::class.java, Array<String>::class.java, String::class.java
                )
                m.isAccessible = true
                val p = m.invoke(null, arrayOf("sh", "-c", cmd), null, null) as Process
                val err = p.errorStream.bufferedReader().readText().trim()
                val code = p.waitFor()
                ok = code == 0
                msg = if (ok) "typed $text" else "failed ($code) $err".take(120)
            } catch (t: Throwable) {
                msg = ("error: " + (t.cause?.message ?: t.message)).take(120)
            }
            main.post { done(ok, msg) }
        }
    }
}
