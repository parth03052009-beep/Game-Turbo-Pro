
package com.example.gameturbo

import android.app.*
import android.content.*
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.*
import android.provider.Settings
import android.view.*
import android.widget.*

class FloatingAssistantService : Service() {

    private var wm: WindowManager? = null
    private var bubble: View? = null
    private var panel: View? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        if (Build.VERSION.SDK_INT >= 23 &&
            !Settings.canDrawOverlays(this)) {
            stopSelf()
            return START_NOT_STICKY
        }

        startAssistantForeground()
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        showBubble()
        return START_STICKY
    }

    private fun startAssistantForeground() {
        val channel = "game_assistant"

        if (Build.VERSION.SDK_INT >= 26) {
            val manager = getSystemService(
                NOTIFICATION_SERVICE
            ) as NotificationManager

            manager.createNotificationChannel(
                NotificationChannel(
                    channel,
                    "Game Assistant",
                    NotificationManager.IMPORTANCE_LOW
                )
            )
        }

        val notification = if (Build.VERSION.SDK_INT >= 26) {
            Notification.Builder(this, channel)
        } else {
            Notification.Builder(this)
        }
            .setSmallIcon(android.R.drawable.ic_menu_view)
            .setContentTitle("Game Assistant is active")
            .setContentText("Floating gaming panel is running")
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(
                101,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(101, notification)
        }
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private fun params(
        width: Int,
        height: Int,
        gravityValue: Int,
        xValue: Int,
        yValue: Int
    ): WindowManager.LayoutParams {
        val type = if (Build.VERSION.SDK_INT >= 26) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        return WindowManager.LayoutParams(
            width,
            height,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = gravityValue
            x = xValue
            y = yValue
        }
    }

    private fun showBubble() {
        if (bubble != null) return

        val icon = TextView(this).apply {
            text = "🎮"
            textSize = 25f
            gravity = Gravity.CENTER
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.rgb(0, 150, 255))
            }
            setOnClickListener { showPanel() }
        }

        try {
            wm?.addView(
                icon,
                params(
                    dp(58), dp(58),
                    Gravity.CENTER_VERTICAL or Gravity.END,
                    dp(10), 0
                )
            )
            bubble = icon
        } catch (_: Exception) {
            stopSelf()
        }
    }

    private fun showPanel() {
        if (panel != null) return

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))
            background = GradientDrawable().apply {
                setColor(Color.rgb(18, 25, 42))
                cornerRadius = dp(18).toFloat()
            }
        }

        val heading = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val title = TextView(this).apply {
            text = "GAME ASSISTANT"
            textSize = 17f
            setTextColor(Color.CYAN)
        }

        val close = TextView(this).apply {
            text = "  ✕  "
            textSize = 20f
            setTextColor(Color.WHITE)
            setOnClickListener { hidePanel() }
        }

        heading.addView(
            title,
            LinearLayout.LayoutParams(0, dp(42), 1f)
        )
        heading.addView(close)
        card.addView(heading)

        val memory = ActivityManager.MemoryInfo()
        val activityManager = getSystemService(
            ACTIVITY_SERVICE
        ) as ActivityManager
        activityManager.getMemoryInfo(memory)

        val battery = registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        val temp = battery?.getIntExtra(
            BatteryManager.EXTRA_TEMPERATURE, -1
        ) ?: -1

        val temperature = if (temp >= 0) {
            "%.1f °C".format(temp / 10f)
        } else {
            "Unavailable"
        }

        val info = TextView(this).apply {
            text = "Available RAM: %.2f GB".format(
                memory.availMem / 1073741824.0
            ) + "\nBattery temperature: $temperature" +
                "\nGame timer: Available in main app" +
                "\nVoice changer: Not implemented yet"
            textSize = 14f
            setTextColor(Color.WHITE)
            setPadding(0, dp(12), 0, dp(12))
        }
        card.addView(info)

        val stop = Button(this).apply {
            text = "STOP ASSISTANT"
            setOnClickListener { stopSelf() }
        }
        card.addView(stop)

        try {
            wm?.addView(
                card,
                params(
                    dp(270),
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP or Gravity.END,
                    dp(12), dp(90)
                )
            )
            panel = card
        } catch (_: Exception) {
            hidePanel()
        }
    }

    private fun hidePanel() {
        panel?.let {
            try {
                wm?.removeView(it)
            } catch (_: Exception) {}
        }
        panel = null
    }

    override fun onDestroy() {
        hidePanel()
        bubble?.let {
            try {
                wm?.removeView(it)
            } catch (_: Exception) {}
        }
        bubble = null
        super.onDestroy()
    }
}
