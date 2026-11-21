package com.sharath.sacheatboard

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var overlayBtn: TextView
    private lateinit var shizukuBtn: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Pal.bg)
            setPadding(dp(8), dp(28), dp(8), dp(8))
        }

        root.addView(TextView(this).apply {
            text = "SA CheatBoard"
            setTextColor(Pal.accent)
            textSize = 22f
            setPadding(dp(8), 0, 0, dp(2))
        })
        root.addView(TextView(this).apply {
            text = "With Shizuku connected, the overlay types cheats into the game. Otherwise tap to copy."
            setTextColor(Pal.dim)
            textSize = 12f
            setPadding(dp(8), 0, 0, dp(8))
        })

        // Setup buttons
        val row1 = buttonRow()
        row1.addView(button("1 Overlay permission") { openOverlayPermission() })
        overlayBtn = button("") { toggleOverlay() }
        row1.addView(overlayBtn)
        root.addView(row1)

        // Shizuku key injection (types the cheat into the game)
        val row2 = buttonRow()
        shizukuBtn = button("") { connectShizuku() }
        row2.addView(shizukuBtn)
        row2.addView(button("4 Test (types in 5s)") { testInject() })
        root.addView(row2)

        // Cheat list with search; tap copies
        root.addView(
            CheatPanel(this, withSearch = true,
                onTap = { c ->
                    val t = c.typed(Prefs.preferShort(this))
                    copyToClipboard(t)
                    Toast.makeText(this, "Copied $t", Toast.LENGTH_SHORT).show()
                }),
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f)
        )

        setContentView(root)

        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
        }
    }

    override fun onResume() {
        super.onResume()
        updateButtons()
    }

    private fun updateButtons() {
        overlayBtn.text = if (OverlayService.running) "2 Stop overlay" else "2 Start overlay"
        shizukuBtn.text = when {
            KeyInjector.ready() -> "3 Shizuku: connected ✓"
            KeyInjector.running() -> "3 Shizuku: tap to allow"
            else -> "3 Shizuku: not running"
        }
    }

    private fun connectShizuku() {
        when {
            KeyInjector.ready() -> Toast.makeText(this, "Shizuku already connected", Toast.LENGTH_SHORT).show()
            KeyInjector.running() -> {
                KeyInjector.requestPermission()
                shizukuBtn.postDelayed({ updateButtons() }, 1500)
            }
            else -> {
                Toast.makeText(this, "Start Shizuku first (wireless debugging), then come back", Toast.LENGTH_LONG).show()
                packageManager.getLaunchIntentForPackage(KeyInjector.SHIZUKU_PKG)?.let { startActivity(it) }
            }
        }
    }

    /** Gives you 5s to switch to the game, then types INEEDSOMEHELP (health, armor, $250,000). */
    private fun testInject() {
        if (!KeyInjector.ready()) {
            Toast.makeText(this, "Connect Shizuku first (button 3)", Toast.LENGTH_LONG).show()
            return
        }
        Toast.makeText(this, "Switch to the game now…", Toast.LENGTH_LONG).show()
        shizukuBtn.postDelayed({
            KeyInjector.type("INEEDSOMEHELP") { _, msg ->
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            }
        }, 5000)
    }

    private fun toggleOverlay() {
        if (OverlayService.running) {
            startService(Intent(this, OverlayService::class.java).setAction(OverlayService.ACTION_STOP))
            overlayBtn.postDelayed({ updateButtons() }, 300)
            return
        }
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Grant overlay permission first (button 1)", Toast.LENGTH_LONG).show()
            return
        }
        val i = Intent(this, OverlayService::class.java)
        if (Build.VERSION.SDK_INT >= 26) startForegroundService(i) else startService(i)
        overlayBtn.postDelayed({ updateButtons() }, 300)
    }

    private fun openOverlayPermission() {
        startActivity(
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
        )
    }

    private fun buttonRow() = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
    }

    private fun button(label: String, onClick: () -> Unit): TextView = TextView(this).apply {
        text = label
        setTextColor(Pal.text)
        textSize = 13f
        gravity = Gravity.CENTER
        setPadding(dp(8), dp(10), dp(8), dp(10))
        background = GradientDrawable().apply {
            setColor(Pal.key); cornerRadius = dp(10).toFloat()
        }
        setOnClickListener { onClick() }
        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            .apply { setMargins(dp(4), dp(3), dp(4), dp(3)) }
    }

}
