package android.template.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    private var hasOverlayPermission by mutableStateOf(false)

    private val overlayPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        hasOverlayPermission = checkOverlayPermission()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        hasOverlayPermission = checkOverlayPermission()
        if (!hasOverlayPermission) {
            requestOverlayPermission()
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var hasOverlayPermission by remember { mutableStateOf(checkOverlayPermission()) }
                    val context = LocalContext.current

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("STREAMCANVAS", style = MaterialTheme.typography.headlineLarge)
                        Text("Standalone Overlay Mode", style = MaterialTheme.typography.bodyLarge)
                        
                        Spacer(modifier = Modifier.height(32.dp))

                        if (!hasOverlayPermission) {
                            Text("Aplikasi butuh izin untuk mengambang di atas game.", color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { requestOverlayPermission() }) {
                                Text("Berikan Izin Overlay")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { hasOverlayPermission = checkOverlayPermission() }) {
                                Text("Saya Sudah Beri Izin (Refresh)")
                            }
                        } else {
                            Text("✅ Izin Overlay Diberikan!", color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    val intent = Intent(context, OverlayService::class.java)
                                    context.startService(intent)
                                },
                                modifier = Modifier.fillMaxWidth(0.8f).height(60.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("🚀 TAMPILKAN LAYOUT KANVAS")
                            }
                            startService(intent)
                            finish() // Tutup dashboard setelah overlay jalan
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hasOverlayPermission = checkOverlayPermission()
    }

    private fun checkOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }
    }

    private fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            overlayPermissionLauncher.launch(intent)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    hasPermission: Boolean,
    onRequestPermission: () -> Unit,
    onStartOverlay: (String, Boolean) -> Unit
) {
    var saweriaUrl by remember { mutableStateOf("") }
    var isLandscape by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("STREAMCANVAS", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
        Text("Dashboard Persiapan", style = MaterialTheme.typography.titleMedium)
        
        Spacer(modifier = Modifier.height(48.dp))

        if (!hasPermission) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Izin Overlay Diperlukan", color = MaterialTheme.colorScheme.onErrorContainer)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onRequestPermission) {
                        Text("Buka Pengaturan Izin")
                    }
                }
            }
        } else {
            OutlinedTextField(
                value = saweriaUrl,
                onValueChange = { saweriaUrl = it },
                label = { Text("URL Web Alert (Saweria/SociaBuzz)") },
                placeholder = { Text("https://saweria.co/widget/alert/...") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Orientasi Game", style = MaterialTheme.typography.titleSmall)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = isLandscape, onClick = { isLandscape = true })
                Text("Landscape (MOBA/FPS)")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = !isLandscape, onClick = { isLandscape = false })
                Text("Portrait (TikTok/IG)")
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = { onStartOverlay(saweriaUrl, isLandscape) },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("MULAI OVERLAY")
            }
        }
    }
}
