package android.template.ui

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout

class OverlayService : Service() {
    private lateinit var windowManager: WindowManager
    private lateinit var floatingLayout: LinearLayout

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        // Kita buat layout transparan dasar dulu untuk memastikan overlay berhasil
        floatingLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#80000000")) // Hitam transparan
            setPadding(32, 32, 32, 32)
        }

        val btnClose = Button(this).apply {
            text = "TUTUP OVERLAY"
            setOnClickListener { stopSelf() } // Menutup overlay saat dipencet
        }

        floatingLayout.addView(btnClose)

        // Pengaturan ajaib agar jendela bisa melayang di atas aplikasi lain (game)
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 100
        }

        windowManager.addView(floatingLayout, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::floatingLayout.isInitialized) {
            windowManager.removeView(floatingLayout)
        }
    }
}
