package android.template.ui

import android.app.Service
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
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

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val store = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    private var saweriaUrl: String = ""
    private var isLandscape: Boolean = true

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        saweriaUrl = intent?.getStringExtra("SAWERIA_URL") ?: ""
        isLandscape = intent?.getBooleanExtra("IS_LANDSCAPE", true) ?: true
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

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
                    OverlayCanvas(
                        saweriaUrl = saweriaUrl,
                        onClose = { stopSelf() }
                    )
                }
            }
        }

        val screenOrientation = if (isLandscape) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            screenOrientation = screenOrientation // Force orientation
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

@Composable
fun OverlayCanvas(saweriaUrl: String, onClose: () -> Unit) {
    var isCleanMode by remember { mutableStateOf(false) }
    var isMenuOpen by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        
        // Web Alert Layer (Placeholder for WebView)
        DraggableLayer(initialX = 100f, initialY = 100f) {
            Box(modifier = Modifier
                .size(250.dp, 100.dp)
                .border(if (isCleanMode) 0.dp else 2.dp, Color.Green, RoundedCornerShape(8.dp))
                .background(if (isCleanMode) Color.Transparent else Color.Green.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            ) {
                if (!isCleanMode) {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Web Alert Area", color = Color.Green, style = MaterialTheme.typography.labelSmall)
                        if(saweriaUrl.isNotEmpty()) {
                            Text("URL Loaded", color = Color.Green, style = MaterialTheme.typography.labelSmall)
                        } else {
                            Text("No URL", color = Color.Red, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        // Camera Layer (Placeholder for CameraX)
        DraggableLayer(initialX = 100f, initialY = 300f) {
            Box(modifier = Modifier
                .size(120.dp, 160.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(if (isCleanMode) 0.dp else 2.dp, Color.Red, RoundedCornerShape(16.dp))
                .background(if (isCleanMode) Color.Transparent else Color.Red.copy(alpha = 0.2f))
            ) {
                if (!isCleanMode) {
                    Text("Kamera", color = Color.Red, modifier = Modifier.align(Alignment.Center))
                }
            }
        }

        // Floating Action Button (Controller)
        DraggableLayer(initialX = 800f, initialY = 100f) {
            Column(horizontalAlignment = Alignment.End) {
                if (isMenuOpen && !isCleanMode) {
                    Card(
                        modifier = Modifier.padding(bottom = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f))
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Button(onClick = { isCleanMode = true }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                                Text("🔴 GO LIVE (Clean)")
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Button(onClick = onClose, colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)) {
                                Text("TUTUP OVERLAY")
                            }
                        }
                    }
                }
                
                if (isCleanMode) {
                     SmallFloatingActionButton(
                        onClick = { isCleanMode = false },
                        containerColor = Color.White.copy(alpha = 0.3f),
                        contentColor = Color.White
                    ) {
                        Icon(Icons.Default.Build, contentDescription = "Edit")
                    }
                } else {
                    FloatingActionButton(
                        onClick = { isMenuOpen = !isMenuOpen },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(if (isMenuOpen) Icons.Default.Close else Icons.Default.Build, contentDescription = "Menu")
                    }
                }
            }
        }
    }
}

@Composable
fun DraggableLayer(initialX: Float = 0f, initialY: Float = 0f, content: @Composable () -> Unit) {
    var offsetX by remember { mutableStateOf(initialX) }
    var offsetY by remember { mutableStateOf(initialY) }

    Box(
        modifier = Modifier
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
