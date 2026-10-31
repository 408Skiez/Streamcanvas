/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.template.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var hasOverlayPermission by remember { mutableStateOf(checkOverlayPermission()) }
                    val context = LocalContext.current // Alat untuk memanggil Service

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
                                    // PERINTAH MENYALAKAN OVERLAY SERVICE
                                    val intent = Intent(context, OverlayService::class.java)
                                    context.startService(intent)
                                },
                                modifier = Modifier.fillMaxWidth(0.8f).height(60.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("🚀 TAMPILKAN LAYOUT KANVAS")
                            }
                        }
                    }
                }
            }
        }
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
            startActivity(intent)
        }
    }
}
