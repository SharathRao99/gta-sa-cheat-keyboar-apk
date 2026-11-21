package com.sharath.sacheatboard

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.KeyEvent
import rikka.shizuku.Shizuku

/**
 * Sends cheat text to the game. Order of preference:
 * 1. Shizuku: runs `input keyevent ...` in the system shell, which looks like a real keyboard.
 * 2. The SA CheatBoard keyboard (the game may ignore this).
 * 3. Clipboard.
 */
object Typer {
    private const val TAG = "SACheatBoard"

    fun shizukuRunning(): Boolean = try { Shizuku.pingBinder() } catch (t: Throwable) {
        Log.e(TAG, "shizukuRunning failed", t); false
    }

    fun shizukuReady(): Boolean = try {
        Shizuku.pingBinder() && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
    } catch (t: Throwable) { Log.e(TAG, "shizukuReady failed", t); false }

    fun requestShizuku(code: Int) {
        try { Shizuku.requestPermission(code) } catch (t: Throwable) { Log.e(TAG, "requestPermission failed", t) }
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
                Log.d(TAG, "running: " + cmd.joinToString(" "))
                val p = m.invoke(null, cmd, null, null) as Process
                val exit = p.waitFor()
                val out = p.inputStream.bufferedReader().readText()
                val err = p.errorStream.bufferedReader().readText()
                Log.d(TAG, "input exit=$exit out=[$out] err=[$err]")
            } catch (t: Throwable) {
                Log.e(TAG, "shizukuType failed", t)
            }
        }.start()
    }

    /** Returns a short status message describing what happened. */
    fun send(ctx: Context, text: String): String {
        Log.d(TAG, "send $text running=${shizukuRunning()} ready=${shizukuReady()}")
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
