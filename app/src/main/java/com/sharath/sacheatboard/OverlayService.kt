package com.sharath.sacheatboard

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Draggable floating cheat list over the game.
 * Tapping a cheat copies its code to the clipboard and collapses back to the small icon.
 */
class OverlayService : Service() {

    companion object {
        const val ACTION_STOP = "com.sharath.sacheatboard.STOP"
        @Volatile var running = false
        private const val CHANNEL = "overlay"
    }

    private lateinit var wm: WindowManager
    private var root: LinearLayout? = null
    private var panel: CheatPanel? = null
    private var status: TextView? = null
    private var expanded = false
    private var applyExpanded: ((Boolean) -> Unit)? = null
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var lp: WindowManager.LayoutParams

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopSelf()
            return START_NOT_STICKY
        }
        if (root == null) {
            startAsForeground()
            buildOverlay()
            running = true
        }
        return START_STICKY
    }

    private fun startAsForeground() {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26) {
            nm.createNotificationChannel(
                NotificationChannel(CHANNEL, "Floating cheat list", NotificationManager.IMPORTANCE_LOW)
            )
        }
        val stop = PendingIntent.getService(
            this, 0,
            Intent(this, OverlayService::class.java).setAction(ACTION_STOP),
            PendingIntent.FLAG_IMMUTABLE
        )
        val builder = if (Build.VERSION.SDK_INT >= 26) Notification.Builder(this, CHANNEL)
        else @Suppress("DEPRECATION") Notification.Builder(this)
        val n = builder
            .setSmallIcon(android.R.drawable.ic_menu_edit)
            .setContentTitle("SA CheatBoard overlay is on")
            .setContentText("Tap Stop to remove it")
            .addAction(Notification.Action.Builder(null, "Stop", stop).build())
            .setOngoing(true)
            .build()
        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(1, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(1, n)
        }
    }

    private fun buildOverlay() {
        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = GradientDrawable().apply {
                setColor(Pal.bg); cornerRadius = dp(14).toFloat()
                setStroke(dp(1), Pal.accent)
            }
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(10), dp(8), dp(6), dp(8))
        }
        val toggle = TextView(this).apply {
            text = "SA ▸"
            setTextColor(Pal.accent)
            textSize = 15f
            setPadding(0, 0, dp(10), 0)
        }
        val st = TextView(this).apply {
            text = "drag me"
            setTextColor(Pal.dim)
            textSize = 11f
        }
        status = st
        val close = TextView(this).apply {
            text = "✕"
            setTextColor(Pal.dim)
            textSize = 16f
            setPadding(dp(10), 0, dp(6), 0)
            setOnClickListener { stopSelf() }
        }
        header.addView(toggle)
        header.addView(st, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        header.addView(close)
        container.addView(header)

        val p = CheatPanel(
            this, withSearch = false,
            onTap = { c -> fire(c.typed(Prefs.preferShort(this)), c.name) },
            onLong = { c -> fire(c.alt(Prefs.preferShort(this)), c.name) }
        ).apply { visibility = View.GONE }
        panel = p
        container.addView(p, LinearLayout.LayoutParams(dp(290), dp(340)))

        // Collapsed = just a small "SA" icon. Expanded = full cheat panel.
        applyExpanded = { open ->
            expanded = open
            p.visibility = if (open) View.VISIBLE else View.GONE
            st.visibility = if (open) View.VISIBLE else View.GONE
            close.visibility = if (open) View.VISIBLE else View.GONE
            toggle.text = if (open) "SA ▾" else "SA"
            toggle.setPadding(0, 0, if (open) dp(10) else 0, 0)
            header.setPadding(if (open) dp(10) else dp(12), dp(8), if (open) dp(6) else dp(12), dp(8))
            if (open) setStatus("tap a cheat to copy")
        }
        toggle.setOnClickListener { applyExpanded?.invoke(!expanded) }
        applyExpanded?.invoke(false)

        lp = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= 26) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,   // game keeps keyboard focus
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = dp(8); y = dp(80)
        }

        // Drag by the header
        var startX = 0; var startY = 0; var touchX = 0f; var touchY = 0f; var moved = false
        header.setOnTouchListener { v, e ->
            when (e.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = lp.x; startY = lp.y; touchX = e.rawX; touchY = e.rawY; moved = false
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (e.rawX - touchX).toInt(); val dy = (e.rawY - touchY).toInt()
                    if (Math.abs(dx) > 6 || Math.abs(dy) > 6) moved = true
                    lp.x = startX + dx; lp.y = startY + dy
                    wm.updateViewLayout(container, lp)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (!moved) {
                        // tap: let children (toggle/close) handle it
                        val child = findChildAt(header, e.x, e.y)
                        child?.performClick()
                    }
                    true
                }
                else -> false
            }
        }

        root = container
        wm.addView(container, lp)
    }

    private fun findChildAt(parent: LinearLayout, x: Float, y: Float): View? {
        for (i in 0 until parent.childCount) {
            val c = parent.getChildAt(i)
            if (c.visibility == View.VISIBLE && x >= c.left && x <= c.right && y >= c.top && y <= c.bottom) return c
        }
        return null
    }

    private fun fire(text: String, name: String) {
        if (KeyInjector.ready()) {
            // Type it into the game as key events, then collapse to the small icon.
            setStatus("typing $text…")
            applyExpanded?.invoke(false)
            KeyInjector.type(text) { ok, msg ->
                setStatus(msg)
                if (!ok) { copyToClipboard(text) }
            }
            return
        }
        copyToClipboard(text)
        setStatus("copied $text (Shizuku not ready)")
        // Collapse to the small icon after copying.
        handler.postDelayed({ applyExpanded?.invoke(false) }, 600)
    }

    private fun setStatus(s: String) { status?.text = s }

    override fun onDestroy() {
        running = false
        handler.removeCallbacksAndMessages(null)
        root?.let { runCatching { wm.removeView(it) } }
        root = null
        super.onDestroy()
    }
}
