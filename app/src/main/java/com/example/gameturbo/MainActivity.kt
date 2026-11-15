
package com.example.gameturbo

import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.BatteryManager
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.os.SystemClock
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.*
import android.os.Build

class MainActivity : Activity() {

    private val bg = Color.rgb(10, 13, 22)
    private val card = Color.rgb(23, 29, 43)
    private val accent = Color.rgb(0, 190, 255)
    private var gamingMode = false
    private var fullScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showDashboard()
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private fun makeText(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) setTypeface(null, Typeface.BOLD)
        }
    }

    private fun roundedBackground(color: Int): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(18).toFloat()
        }
    }

    private fun showDashboard() {
        window.statusBarColor = bg
        window.navigationBarColor = bg

        val scroll = ScrollView(this)
        scroll.setBackgroundColor(bg)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(25), dp(18), dp(25))
        }

        root.addView(
            makeText("GAME TURBO PRO", 25f, Color.WHITE, true)
        )

        root.addView(
            makeText(
                "YOUR ULTIMATE GAMING CONTROL CENTER",
                11f, accent, true
            )
        )

        val hero = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(20), dp(18), dp(20))
            background = roundedBackground(card)
        }

        val heroTitle = makeText("GAMING PERFORMANCE", 18f, Color.WHITE, true)
        hero.addView(heroTitle)

        hero.addView(
            makeText(
                "Monitor your device and prepare for gaming.",
                13f, Color.LTGRAY
            )
        )

        val modeButton = Button(this).apply {
            text = "TURN GAMING MODE ON"
            isAllCaps = false
            setOnClickListener {
                gamingMode = !gamingMode

                if (gamingMode) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    text = "TURN GAMING MODE OFF"
                    Toast.makeText(
                        this@MainActivity,
                        "Gaming Mode ON",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                    text = "TURN GAMING MODE ON"
                    Toast.makeText(
                        this@MainActivity,
                        "Gaming Mode OFF",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        hero.addView(
            modeButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val heroParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        heroParams.setMargins(0, dp(22), 0, dp(18))
        root.addView(hero, heroParams)

        addSection(root, "GAMING TOOLS")

        addToolCard(
            root,
            "🎮 Game Launcher",
            "Automatically detect and launch installed games"
        ) { launchGame() }

        addToolCard(
            root,
            "🎯 Aim Boost",
            "Sensitivity helper and aim profiles"
        ) { showAimBoost() }

        addToolCard(
            root,
            "⏱ Game Session Timer",
            "Track your gaming session"
        ) { showGameTimer() }

        addToolCard(
            root,
            "👆 Touch Response Test",
            "Test your screen touch response"
        ) { showTouchTest() }

        addToolCard(
            root,
            "🖥 Full-screen Mode",
            "Hide navigation and status bars"
        ) { toggleFullScreen() }

        addSection(root, "PERFORMANCE MONITOR")

        addToolCard(
            root,
            "🧠 RAM Monitor",
            "View available and total RAM"
        ) { showRamInfo() }

        addToolCard(
            root,
            "🌡 Battery Temperature",
            "Check battery temperature and charge"
        ) { showBatteryInfo() }

        addToolCard(
            root,
            "📊 FPS / Refresh Rate",
            "View screen refresh rate information"
        ) { showRefreshInfo() }

        addToolCard(
            root,
            "💾 Storage Monitor",
            "View available internal storage"
        ) { showStorageInfo() }

        addSection(root, "SMART SETTINGS")

        addToolCard(
            root,
            "⚡ Performance Presets",
            "Choose Balanced, Gaming or Cool Mode"
        ) { showPresets() }

        addToolCard(
            root,
            "🔕 DND Mode",
            "Manage Do Not Disturb access"
        ) { toggleDnd() }

        addToolCard(
            root,
            "🔋 Battery Saver",
            "Open your phone's battery settings"
        ) { openSettings(Settings.ACTION_BATTERY_SAVER_SETTINGS) }

        addToolCard(
            root,
            "📱 Background App Viewer",
            "Open Android application settings"
        ) { openSettings(Settings.ACTION_APPLICATION_SETTINGS) }

        addToolCard(
            root,
            "🧹 Storage Cleaner Guide",
            "Learn safe ways to clear app cache"
        ) { showCleanerGuide() }

        root.addView(
            makeText(
                "GAME TURBO PRO  •  VERSION 1.0",
                11f, Color.GRAY, true
            ).apply {
                gravity = Gravity.CENTER
                setPadding(0, dp(25), 0, dp(10))
            }
        )

        scroll.addView(root)
        setContentView(scroll)
    }

    private fun addSection(root: LinearLayout, title: String) {
        val label = makeText(title, 14f, accent, true)
        label.setPadding(0, dp(15), 0, dp(12))
        root.addView(label)
    }

    private fun addToolCard(
        root: LinearLayout,
        title: String,
        description: String,
        action: () -> Unit
    ) {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(15), dp(16), dp(15))
            background = roundedBackground(card)
            isClickable = true
            isFocusable = true
            setOnClickListener { action() }
        }

        box.addView(makeText(title, 16f, Color.WHITE, true))

        val detail = makeText(description, 12f, Color.LTGRAY)
        detail.setPadding(0, dp(5), 0, 0)
        box.addView(detail)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, dp(10))
        root.addView(box, params)
    }

    private fun showAimBoost() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(10), dp(20), dp(10))
        }

        layout.addView(
            makeText(
                "Aim Sensitivity Helper",
                19f, Color.BLACK, true
            )
        )

        layout.addView(
            makeText(
                "Choose a profile and adjust your personal sensitivity guide.",
                13f, Color.DKGRAY
            )
        )

        val valueText = makeText("Sensitivity: 80", 16f, Color.BLACK, true)
        layout.addView(valueText)

        val slider = SeekBar(this).apply {
            max = 100
            progress = 80
        }

        slider.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    valueText.text = "Sensitivity: $progress"
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            }
        )

        layout.addView(slider)

        AlertDialog.Builder(this)
            .setTitle("🎯 AIM BOOST")
            .setView(layout)
            .setItems(
                arrayOf(
                    "Steady Aim Profile",
                    "Balanced Aim Profile",
                    "Fast Swipe Profile"
                )
            ) { _, which ->
                val profile = arrayOf(
                    "Steady Aim: try lower sensitivity",
                    "Balanced Aim: try medium sensitivity",
                    "Fast Swipe: try higher sensitivity"
                )
                AlertDialog.Builder(this)
                    .setMessage(
                        profile[which] +
                        "\n\nAdjust the values in your game's own settings."
                    )
                    .setPositiveButton("OK", null)
                    .show()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun launchGame() {
    val intent = Intent(Intent.ACTION_MAIN)
    intent.addCategory(Intent.CATEGORY_LAUNCHER)

    val apps = packageManager.queryIntentActivities(intent, 0)
        .filter { it.activityInfo.packageName != packageName }
        .distinctBy { it.activityInfo.packageName }
        .sortedBy { it.loadLabel(packageManager).toString() }

    if (apps.isEmpty()) {
        Toast.makeText(this, "No launchable apps found", Toast.LENGTH_SHORT).show()
        return
    }

    val names = apps.map {
        it.loadLabel(packageManager).toString()
    }.toTypedArray()

    AlertDialog.Builder(this)
        .setTitle("SELECT A GAME OR APP")
        .setItems(names) { _, which ->
            val selectedPackage = apps[which].activityInfo.packageName
            val launchIntent =
                packageManager.getLaunchIntentForPackage(selectedPackage)

            if (launchIntent != null) {
                startActivity(launchIntent)
            }
        }
        .setNegativeButton("Close", null)
        .show()
}


    

    private fun showRamInfo() {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val info = ActivityManager.MemoryInfo()
        manager.getMemoryInfo(info)

        val total = info.totalMem / 1024.0 / 1024.0 / 1024.0
        val available = info.availMem / 1024.0 / 1024.0 / 1024.0

        AlertDialog.Builder(this)
            .setTitle("RAM MONITOR")
            .setMessage(
                "Total RAM: %.2f GB\nAvailable RAM: %.2f GB\n\n" +
                "Android manages memory automatically."
                    .format(total, available)
            )
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showBatteryInfo() {
        val intent = registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )

        val temperature = intent?.getIntExtra(
            BatteryManager.EXTRA_TEMPERATURE, -1
        ) ?: -1

        val level = intent?.getIntExtra(
            BatteryManager.EXTRA_LEVEL, -1
        ) ?: -1

        val scale = intent?.getIntExtra(
            BatteryManager.EXTRA_SCALE, 100
        ) ?: 100

        val percent = if (scale > 0 && level >= 0) {
            level * 100 / scale
        } else {
            -1
        }

        val tempText = if (temperature >= 0) {
            "%.1f °C".format(temperature / 10.0)
        } else {
            "Not available"
        }

        AlertDialog.Builder(this)
            .setTitle("BATTERY MONITOR")
            .setMessage(
                "Battery: $percent%\nTemperature: $tempText"
            )
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showRefreshInfo() {
        @Suppress("DEPRECATION")
        val refreshRate = windowManager.defaultDisplay.refreshRate

        AlertDialog.Builder(this)
            .setTitle("DISPLAY INFORMATION")
            .setMessage(
                "Screen refresh rate: %.0f Hz\n\n" +
                "This is display refresh information, not the actual FPS inside a game."
                    .format(refreshRate)
            )
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showStorageInfo() {
        val stats = StatFs(Environment.getDataDirectory().path)

        val total = stats.totalBytes / 1024.0 / 1024.0 / 1024.0
        val free = stats.availableBytes / 1024.0 / 1024.0 / 1024.0

        AlertDialog.Builder(this)
            .setTitle("STORAGE MONITOR")
            .setMessage(
                "Total storage: %.2f GB\nAvailable storage: %.2f GB"
                    .format(total, free)
            )
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showGameTimer() {
        val timer = Chronometer(this).apply {
            textSize = 25f
            setTextColor(Color.BLACK)
            base = SystemClock.elapsedRealtime()
            start()
        }

        AlertDialog.Builder(this)
            .setTitle("GAME SESSION TIMER")
            .setView(timer)
            .setPositiveButton("Stop") { _, _ ->
                timer.stop()
            }
            .show()
    }

    private fun showTouchTest() {
        val testArea = TextView(this).apply {
            text = "Touch anywhere in this area\n\nTouches: 0"
            textSize = 18f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(30, 40, 60))
            minHeight = dp(240)
        }

        var touches = 0

        testArea.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN ||
                event.action == MotionEvent.ACTION_MOVE
            ) {
                touches++
                testArea.text =
                    "Touch detected!\nTouches: $touches\n" +
                    "X: ${event.x.toInt()}  Y: ${event.y.toInt()}"
            }
            true
        }

        AlertDialog.Builder(this)
            .setTitle("TOUCH RESPONSE TEST")
            .setView(testArea)
            .setPositiveButton("Close", null)
            .show()
    }

    private fun showPresets() {
        val options = arrayOf(
            "Balanced Mode",
            "Gaming Mode",
            "Cool Mode"
        )

        AlertDialog.Builder(this)
            .setTitle("PERFORMANCE PRESETS")
            .setItems(options) { _, which ->
                val message = when (which) {
                    0 -> "Balanced: normal screen and battery settings."
                    1 -> "Gaming: keep the screen awake and use DND if enabled."
                    else -> "Cool: lower screen brightness and take breaks if the phone gets hot."
                }

                AlertDialog.Builder(this)
                    .setMessage(
                        message +
                        "\n\nThese presets guide device settings; they do not overclock the CPU or GPU."
                    )
                    .setPositiveButton("OK", null)
                    .show()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun toggleDnd() {
        if (Build.VERSION.SDK_INT < 23) return

        val manager = getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        if (!manager.isNotificationPolicyAccessGranted) {
            startActivity(
                Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            )
            return
        }

        val currentlyOn =
            manager.currentInterruptionFilter !=
            NotificationManager.INTERRUPTION_FILTER_ALL

        manager.setInterruptionFilter(
            if (currentlyOn)
                NotificationManager.INTERRUPTION_FILTER_ALL
            else
                NotificationManager.INTERRUPTION_FILTER_PRIORITY
        )

        Toast.makeText(
            this,
            if (currentlyOn) "DND turned OFF" else "DND turned ON",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun toggleFullScreen() {
        fullScreen = !fullScreen

        window.decorView.systemUiVisibility =
            if (fullScreen) {
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            } else {
                View.SYSTEM_UI_FLAG_VISIBLE
            }
    }

    private fun showCleanerGuide() {
        AlertDialog.Builder(this)
            .setTitle("SAFE STORAGE CLEANUP")
            .setMessage(
                "Open Android Settings > Apps > Choose an app > Storage > Clear cache.\n\n" +
                "Avoid Clear Data unless you understand that it may remove app data and settings."
            )
            .setPositiveButton("OK", null)
            .show()
    }

    private fun openSettings(action: String) {
        try {
            startActivity(Intent(action))
        } catch (_: Exception) {
            Toast.makeText(
                this,
                "This setting is not available on this device.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
