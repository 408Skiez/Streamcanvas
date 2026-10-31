package android.template.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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
fun CanvasScreen(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Text("MODE KANVAS - Segera Hadir!", color = Color.White, modifier = Modifier.align(Alignment.Center))
        Button(onClick = onBack, modifier = Modifier.align(Alignment.TopStart).padding(16.dp)) {
            Text("Kembali")
        }
    }
}

@Composable
fun SenderScreen(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray)) {
        Text("MODE GAMING - Segera Hadir!", color = Color.White, modifier = Modifier.align(Alignment.Center))
        Button(onClick = onBack, modifier = Modifier.align(Alignment.TopStart).padding(16.dp)) {
            Text("Kembali")
        }
    }
}
