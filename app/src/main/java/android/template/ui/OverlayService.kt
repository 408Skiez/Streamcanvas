package android.template.ui // Pastikan package ini sesuai

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner // INI YANG SEBELUMNYA KURANG
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import kotlin.math.roundToInt

class OverlayService : Service(), LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {
    private lateinit var windowManager: WindowManager
    private lateinit var composeView: ComposeView

    // Pengaturan wajib agar Compose bisa berjalan di luar Activity
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val store = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        // Inisialisasi Lifecycle agar Compose aktif
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@OverlayService)
            setViewTreeViewModelStoreOwner(this@OverlayService)
            setViewTreeSavedStateRegistryOwner(this@OverlayService)
            
            setContent {
                MaterialTheme {
                    OverlayCanvas(onClose = { stopSelf() })
                }
            }
        }

        // Pengaturan Jendela Overlay
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT, // Memenuhi seluruh layar!
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
        }

        windowManager.addView(composeView, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        if (::composeView.isInitialized) {
            windowManager.removeView(composeView)
        }
    }

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry
    override val viewModelStore: ViewModelStore get() = store
}

// --- KOMPONEN UI COMPOSE (Mirip seperti di Fase 1) ---

@Composable
fun OverlayCanvas(onClose: () -> Unit) {
    var isCleanMode by remember { mutableStateOf(false) }
    var showCamera by remember { mutableStateOf(true) }
    var showWebAlert by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) { // Background sekarang TRANSPARAN!

        if (showWebAlert) {
            DraggableLayer {
                Box(modifier = Modifier
                    .size(200.dp, 200.dp)
                    .border(if (isCleanMode) 0.dp else 2.dp, Color.Green)
                    .background(if (isCleanMode) Color.Transparent else Color.Green.copy(alpha = 0.2f))
                ) {
                    if (!isCleanMode) {
                        Text("Web Alert", color = Color.Green, modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
        }

        if (showCamera) {
            DraggableLayer {
                Box(modifier = Modifier
                    .size(120.dp, 160.dp)
                    .border(if (isCleanMode) 0.dp else 2.dp, Color.Red)
                    .background(if (isCleanMode) Color.Transparent else Color.Red.copy(alpha = 0.2f)) // Transparan saat Clean Mode sementara belum ada kamera asli
                ) {
                    if (!isCleanMode) {
                        Text("Kamera", color = Color.Red, modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
        }

        if (!isCleanMode) {
            Column(modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).background(Color.Black.copy(alpha = 0.5f)).padding(8.dp)) {
                Button(onClick = { showCamera = !showCamera }) { Text(if (showCamera) "Tutup Kamera" else "Buka Kamera") }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { showWebAlert = !showWebAlert }) { Text(if (showWebAlert) "Tutup Alert" else "Buka Alert") }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { isCleanMode = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("🔴 GO LIVE")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onClose, colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)) {
                    Text("TUTUP APP")
                }
            }
        } else {
            Button(
                onClick = { isCleanMode = false },
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f))
            ) {
                Text("X", color = Color.White)
            }
        }
    }
}

@Composable
fun DraggableLayer(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
    ) {
        content()
    }
}
