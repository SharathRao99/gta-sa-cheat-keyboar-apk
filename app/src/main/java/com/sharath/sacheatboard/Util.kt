package com.sharath.sacheatboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color

fun Context.dp(n: Int): Int = (n * resources.displayMetrics.density).toInt()

object Pal {
    val bg = Color.parseColor("#14161A")
    val panel = Color.parseColor("#1E2127")
    val row = Color.parseColor("#262A31")
    val accent = Color.parseColor("#F5A623")
    val text = Color.parseColor("#ECEFF4")
    val dim = Color.parseColor("#9AA3B2")
    val key = Color.parseColor("#2F343D")
}

object Prefs {
    private const val FILE = "sa_cheatboard"

    private fun sp(ctx: Context) = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun favorites(ctx: Context): Set<String> = sp(ctx).getStringSet("fav", emptySet()) ?: emptySet()

    fun toggleFavorite(ctx: Context, id: String) {
        val cur = favorites(ctx).toMutableSet()
        if (!cur.add(id)) cur.remove(id)
        sp(ctx).edit().putStringSet("fav", cur).apply()
    }

    /** true = type the short code (e.g. HESOYAM / AEZAKMI), false = type the full phrase. */
    fun preferShort(ctx: Context): Boolean = sp(ctx).getBoolean("short", true)

    fun setPreferShort(ctx: Context, v: Boolean) = sp(ctx).edit().putBoolean("short", v).apply()
}

fun Context.copyToClipboard(text: String) {
    val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    cm.setPrimaryClip(ClipData.newPlainText("cheat", text))
}
