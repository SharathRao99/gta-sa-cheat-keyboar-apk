package com.sharath.sacheatboard

import android.content.Context
import android.content.pm.PackageManager
import android.view.KeyEvent
import rikka.shizuku.Shizuku

/**
 * Sends cheat text to the game. Order of preference:
 * 1. Shizuku: runs `input keyevent ...` in the system shell, which looks like a real keyboard.
 * 2. The SA CheatBoard keyboard (the game may ignore this).
 * 3. Clipboard.
 */
object Typer {

    fun shizukuRunning(): Boolean = try { Shizuku.pingBinder() } catch (t: Throwable) { false }

    fun shizukuReady(): Boolean = try {
        Shizuku.pingBinder() && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
    } catch (t: Throwable) { false }

    fun requestShizuku(code: Int) {
        try { Shizuku.requestPermission(code) } catch (t: Throwable) { }
    }

    private fun keycodes(text: String): List<String> = text.uppercase().mapNotNull { ch ->
        when (ch) {
            in 'A'..'Z' -> (KeyEvent.KEYCODE_A + (ch - 'A')).toString()
            in '0'..'9' -> (KeyEvent.KEYCODE_0 + (ch - '0')).toString()
            else -> null
        }
    }

    private fun shizukuType(text: String) {
        val codes = keycodes(text)
        if (codes.isEmpty()) return
        Thread {
            try {
                // Shizuku.newProcess is not public in the 13.x API, so call it by reflection.
                val m = Shizuku::class.java.getDeclaredMethod(
                    "newProcess",
                    Array<String>::class.java, Array<String>::class.java, String::class.java
                )
                m.isAccessible = true
                val cmd = arrayOf("input", "keyevent") + codes
                val p = m.invoke(null, cmd, null, null) as Process
                p.waitFor()
            } catch (_: Throwable) { }
        }.start()
    }

    /** Returns a short status message describing what happened. */
    fun send(ctx: Context, text: String): String {
        if (shizukuReady()) {
            shizukuType(text)
            return "sent $text via Shizuku"
        }
        val ime = SAKeyboardService.instance
        if (ime != null && ime.typeText(text)) return "sent $text via keyboard (game may ignore)"
        ctx.copyToClipboard(text)
        return "copied $text"
    }
}
