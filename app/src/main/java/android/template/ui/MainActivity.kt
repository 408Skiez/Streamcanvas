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

package android.template.ui // Harus sama persis dengan MainActivity di atas

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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun HomeScreen(onSelectCanvas: () -> Unit, onSelectSender: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("STREAMCANVAS", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onSelectCanvas, modifier = Modifier.fillMaxWidth(0.8f).height(60.dp)) {
            Text("🎬 SAYA STREAMER (Buka Kanvas)")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onSelectSender, modifier = Modifier.fillMaxWidth(0.8f).height(60.dp)) {
            Text("🎮 SAYA MAIN GAME (Kirim Layar)")
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

@Composable
fun CanvasScreen(onBack: () -> Unit) {
    var isCleanMode by remember { mutableStateOf(false) }
    var showCamera by remember { mutableStateOf(true) }
    var showWebAlert by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray)) {
            Text("Area Game Stream", color = Color.LightGray, modifier = Modifier.align(Alignment.Center))
        }

        if (showWebAlert) {
            DraggableLayer {
                Box(modifier = Modifier
                    .size(200.dp, 200.dp)
                    .border(if (isCleanMode) 0.dp else 2.dp, Color.Green)
                    .background(if (isCleanMode) Color.Transparent else Color.Green.copy(alpha = 0.2f))
                ) {
                    Text("Web Alert", color = Color.Green, modifier = Modifier.align(Alignment.Center))
                }
            }
        }

        if (showCamera) {
            DraggableLayer {
                Box(modifier = Modifier
                    .size(120.dp, 160.dp)
                    .border(if (isCleanMode) 0.dp else 2.dp, Color.Red)
                    .background(Color.DarkGray)
                ) {
                    Text("Kamera", color = Color.Red, modifier = Modifier.align(Alignment.Center))
                }
            }
        }

        if (!isCleanMode) {
            Column(modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)) {
                Button(onClick = { showCamera = !showCamera }) { Text("Kamera") }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { showWebAlert = !showWebAlert }) { Text("Alert") }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { isCleanMode = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("🔴 GO LIVE")
                }
            }
            Button(onClick = onBack, modifier = Modifier.align(Alignment.TopStart).padding(16.dp)) {
                Text("Kembali")
            }
        } else {
            Button(
                onClick = { isCleanMode = false },
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f))
            ) {
                Text("X", color = Color.White)
            }
        }
    }
}

@Composable
fun SenderScreen(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray)) {
        Text("MODE GAMING (Segera Hadir)", color = Color.White, modifier = Modifier.align(Alignment.Center))
        Button(onClick = onBack, modifier = Modifier.align(Alignment.TopStart).padding(16.dp)) {
            Text("Kembali")
        }
    }
}
