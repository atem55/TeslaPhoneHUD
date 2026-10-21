package com.atem55.teslahud

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun HudScreen() {
    var accessToken by remember { mutableStateOf("") }
    var vehicleId by remember { mutableStateOf("") }
    var isHudMode by remember { mutableStateOf(true) }
    var showMetrics by remember { mutableStateOf(true) }
    var mirrorMode by remember { mutableStateOf(true) }

    var speed by remember { mutableStateOf(0) }
    var battery by remember { mutableStateOf(0) }
    var range by remember { mutableStateOf(0.0) }
    var destination by remember { mutableStateOf("No route") }
    var minutesToArrival by remember { mutableStateOf(0.0) }
    var milesToArrival by remember { mutableStateOf(0.0) }
    var heading by remember { mutableStateOf(0) }
    var shiftState by remember { mutableStateOf("P") }

    // Polling loop when token and vehicle set
    LaunchedEffect(accessToken, vehicleId, isHudMode) {
        if (accessToken.isNotBlank() && vehicleId.isNotBlank() && isHudMode) {
            while (true) {
                try {
                    val data = TeslaRepository.fetchVehicleData(accessToken, vehicleId)
                    data?.let {
                        speed = it.speed ?: 0
                        battery = it.batteryLevel ?: 0
                        range = it.batteryRange ?: 0.0
                        destination = it.activeRouteDestination ?: "No route"
                        minutesToArrival = it.activeRouteMinutesToArrival ?: 0.0
                        milesToArrival = it.activeRouteMilesToArrival ?: 0.0
                        heading = it.heading ?: 0
                        shiftState = it.shiftState ?: "P"
                    }
                } catch (e: Exception) {
                    // Log or show error
                }
                delay(3000) // Poll every 3 seconds
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .scale(scaleX = if (mirrorMode) -1f else 1f, scaleY = 1f) // Horizontal mirror for windshield reflection
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Speed - large primary display
            Text(
                text = "$speed",
                color = Color(0xFF00FF41), // Bright green HUD color
                fontSize = 120.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "MPH",
                color = Color(0xFF00FF41),
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation info
            Text(
                text = destination,
                color = Color.White,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%.0f", milesToArrival),
                        color = Color(0xFF00BFFF),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("mi left", color = Color.Gray, fontSize = 14.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%.0f", minutesToArrival),
                        color = Color(0xFF00BFFF),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("min left", color = Color.Gray, fontSize = 14.sp)
                }
            }

            // Arrival time approximate
            val arrival = System.currentTimeMillis() + (minutesToArrival * 60 * 1000).toLong()
            Text(
                text = "Arrive ~ ${java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date(arrival))}",
                color = Color.White,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Heading / direction
            Text(
                text = "HDG ${heading}°",
                color = Color(0xFFFFA500),
                fontSize = 22.sp
            )

            if (showMetrics) {
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color.DarkGray, thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MetricItem("BAT", "$battery%", Color(0xFF00FF41))
                    MetricItem("RNG", "${range.toInt()} mi", Color(0xFF00BFFF))
                    MetricItem("GEAR", shiftState, Color.White)
                }
            }

            // TODO: Speed limit and camera warnings
            // Integrate with location services + OSM or external API for speed limit
            // and speed camera alerts based on proximity
            Text(
                text = "SPEED LIMIT / CAMERAS: Coming soon",
                color = Color.Yellow,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Controls overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
        ) {
            Button(
                onClick = { showMetrics = !showMetrics },
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
            ) {
                Text(if (showMetrics) "Hide Metrics" else "Show Metrics", fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = { mirrorMode = !mirrorMode },
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
            ) {
                Text(if (mirrorMode) "Unmirror" else "Mirror", fontSize = 12.sp)
            }
        }
    }

    // Simple token input dialog
    if (accessToken.isBlank()) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Tesla Access Token") },
            text = {
                Column {
                    Text("Enter your Tesla API access token and vehicle ID.")
                    OutlinedTextField(
                        value = accessToken,
                        onValueChange = { accessToken = it },
                        label = { Text("Access Token") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = vehicleId,
                        onValueChange = { vehicleId = it },
                        label = { Text("Vehicle ID") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = { /* state already updates */ }) {
                    Text("Start")
                }
            }
        )
    }
}

@Composable
fun MetricItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = color, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = Color.Gray, fontSize = 12.sp)
    }
}
