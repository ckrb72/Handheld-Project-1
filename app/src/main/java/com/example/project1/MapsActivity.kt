package com.example.project1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.project1.ui.theme.Project1Theme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

class MapsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project1Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MapsView(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MapsView(modifier: Modifier = Modifier) {
    val staffordVA = LatLng(38.4221, -77.4083)
    val cameraPositionState = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(staffordVA, 10.0f) }
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        Marker(state = MarkerState(position = staffordVA),
            title = "Stafford",
            snippet = "Marker in Stafford"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    Project1Theme {
        MapsView()
    }
}