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
            text = "Tap a cheat to copy it, then type it with a keyboard in the game."
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
