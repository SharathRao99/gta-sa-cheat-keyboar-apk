package com.sharath.sacheatboard

import android.inputmethodservice.InputMethodService
import android.os.Handler
import android.util.Log
import android.os.Looper
import android.os.SystemClock
import android.view.InputDevice
import android.view.KeyCharacterMap
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.drawable.GradientDrawable

/**
 * Compact keyboard that sends real key events (like a hardware keyboard) to the focused app.
 * The floating overlay also uses typeText() so you can fire cheats without leaving the game.
 */
class SAKeyboardService : InputMethodService() {

    companion object {
        @Volatile
        var instance: SAKeyboardService? = null
        private const val KEY_DELAY_MS = 160L   // gap between keys
        private const val HOLD_MS = 70L         // how long each key stays pressed
    }

    private val handler = Handler(Looper.getMainLooper())
    private var body: FrameLayout? = null
    private var keysView: View? = null
    private var cheatsView: View? = null
    private var showingCheats = false

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun onDestroy() {
        instance = null
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    /** True when the focused app currently has an input connection we can type into. */
    fun isConnected(): Boolean = currentInputConnection != null

    /** Types letters/digits one by one. Returns false if no app is accepting input right now. */
    fun typeText(text: String): Boolean {
        val ic = currentInputConnection
        if (ic == null) {
            Log.w("SACheatBoard", "typeText: no input connection (target app has no focused input)")
            return false
        }
        val ei = currentInputEditorInfo
        Log.d("SACheatBoard", "typeText '$text' -> pkg=${ei?.packageName} inputType=${ei?.inputType} imeOptions=${ei?.imeOptions}")
        text.uppercase().forEachIndexed { i, ch ->
            handler.postDelayed({ sendChar(ch) }, i * KEY_DELAY_MS)
        }
        return true
    }

    /**
     * Press-and-hold like a real keyboard. Games that poll key state each frame miss
     * instant down+up pairs, and many ignore the "soft keyboard" flag, so this tags the
     * events as coming from a hardware keyboard source.
     */
    private fun pressKey(code: Int) {
        val ic = currentInputConnection ?: return
        val down = SystemClock.uptimeMillis()
        ic.sendKeyEvent(
            KeyEvent(down, down, KeyEvent.ACTION_DOWN, code, 0, 0,
                KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0, InputDevice.SOURCE_KEYBOARD)
        )
        handler.postDelayed({
            val ic2 = currentInputConnection ?: return@postDelayed
            ic2.sendKeyEvent(
                KeyEvent(down, SystemClock.uptimeMillis(), KeyEvent.ACTION_UP, code, 0, 0,
                    KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0, InputDevice.SOURCE_KEYBOARD)
            )
        }, HOLD_MS)
    }

    private fun sendChar(ch: Char) {
        val code = when (ch) {
            in 'A'..'Z' -> KeyEvent.KEYCODE_A + (ch - 'A')
            in '0'..'9' -> KeyEvent.KEYCODE_0 + (ch - '0')
            ' ' -> KeyEvent.KEYCODE_SPACE
            else -> return
        }
        Log.d("SACheatBoard", "key $ch -> keycode $code")
        pressKey(code)
    }

    override fun onStartInput(attribute: android.view.inputmethod.EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        Log.d("SACheatBoard", "onStartInput pkg=${attribute?.packageName} inputType=${attribute?.inputType}")
    }

    override fun onCreateInputView(): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Pal.bg)
        }

        // Header
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(8), dp(4), dp(8), dp(4))
        }
        header.addView(TextView(this).apply {
            text = "SA CheatBoard"
            setTextColor(Pal.accent)
            textSize = 14f
        }, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        val toggle = pill("Cheats")
        toggle.setOnClickListener {
            showingCheats = !showingCheats
            toggle.text = if (showingCheats) "Keys" else "Cheats"
            keysView?.visibility = if (showingCheats) View.GONE else View.VISIBLE
            cheatsView?.visibility = if (showingCheats) View.VISIBLE else View.GONE
        }
        header.addView(toggle)
        header.addView(pill("⌨").apply {
            setOnClickListener {
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker()
            }
        })
        header.addView(pill("▼").apply { setOnClickListener { requestHideSelf(0) } })
        root.addView(header)

        // Body
        val keys = buildKeys()
        val cheats = CheatPanel(
            this, withSearch = false,
            onTap = { c -> typeText(c.typed(Prefs.preferShort(this))) },
            onLong = { c -> typeText(c.alt(Prefs.preferShort(this))) }
        ).apply { visibility = View.GONE }
        keysView = keys
        cheatsView = cheats
        body = FrameLayout(this).apply {
            addView(keys)
            addView(cheats)
        }
        root.addView(body, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(230)))
        return root
    }

    private fun buildKeys(): View {
        val col = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(4), dp(2), dp(4), dp(4))
        }
        val rows = listOf("1234567890", "QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM")
        rows.forEach { r ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
            }
            r.forEach { ch ->
                row.addView(key(ch.toString()) { sendChar(ch) }, keyParams())
            }
            if (r.startsWith("Z")) {
                row.addView(key("⌫") { pressKey(KeyEvent.KEYCODE_DEL) }, keyParams(1.5f))
            }
            col.addView(row, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f))
        }
        val bottom = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        bottom.addView(key("SPACE") { pressKey(KeyEvent.KEYCODE_SPACE) }, keyParams(4f))
        bottom.addView(key("ENTER") { pressKey(KeyEvent.KEYCODE_ENTER) }, keyParams(2f))
        col.addView(bottom, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f))
        return col
    }

    private fun keyParams(weight: Float = 1f) =
        LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, weight).apply {
            setMargins(dp(2), dp(2), dp(2), dp(2))
        }

    private fun key(label: String, onClick: () -> Unit) = TextView(this).apply {
        text = label
        textSize = 16f
        setTextColor(Pal.text)
        gravity = Gravity.CENTER
        background = GradientDrawable().apply {
            setColor(Pal.key); cornerRadius = dp(8).toFloat()
        }
        setOnClickListener { onClick() }
    }

    private fun pill(label: String) = TextView(this).apply {
        text = label
        textSize = 13f
        setTextColor(Pal.text)
        gravity = Gravity.CENTER
        setPadding(dp(12), dp(6), dp(12), dp(6))
        background = GradientDrawable().apply {
            setColor(Pal.key); cornerRadius = dp(14).toFloat()
        }
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { marginStart = dp(6) }
    }

}
