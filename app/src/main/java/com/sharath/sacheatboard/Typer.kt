package com.sharath.sacheatboard

import android.content.Context
import android.util.Log

/**
 * Sends cheat text to the game through the SA CheatBoard keyboard.
 * If the keyboard isn't active, copies the code to the clipboard instead.
 * Logcat tag: SACheatBoard
 */
object Typer {
    private const val TAG = "SACheatBoard"

    /** Returns a short status message describing what happened. */
    fun send(ctx: Context, text: String): String {
        val ime = SAKeyboardService.instance
        Log.d(TAG, "send $text imeActive=${ime != null} connected=${ime?.isConnected()}")
        if (ime != null && ime.typeText(text)) return "sent $text via keyboard"
        ctx.copyToClipboard(text)
        Log.w(TAG, "keyboard not usable, copied $text to clipboard")
        return "copied $text"
    }
}
