package com.example.project1

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.project1.ui.theme.Project1Theme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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

suspend fun getAddressGeocodeCurrent(context: android.content.Context, latLng: LatLng): String =
    suspendCoroutine { continuation ->
        val geocoder = Geocoder(context, Locale.getDefault())
        geocoder.getFromLocation(
            latLng.latitude,
            latLng.longitude,
            1,
            object : Geocoder.GeocodeListener {
                override fun onGeocode(addressList: MutableList<Address>) {
                    val result = if (addressList.isNotEmpty()) {
                        val address = addressList[0]
                        val city = address.locality ?: "Unknown City"
                        val state = address.adminArea ?: "Unknown State"
                        "$city, $state"
                    } else {
                        "No address found."
                    }
                    continuation.resume(result)
                }

                override fun onError(errorMessage: String?) {
                    continuation.resume("Geocoding failed: ${errorMessage ?: "Unknown error"}")
                }
            }
        )
    }

@Composable
fun MapsView(modifier: Modifier = Modifier) {
    val staffordVA = LatLng(38.4221, -77.4083)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(staffordVA, 10.0f)
    }
    var markerPosition by remember { mutableStateOf<LatLng?>(null) }
    var addressInfo by remember { mutableStateOf("Long Click on Map") }
    val context = LocalContext.current

    LaunchedEffect(markerPosition) {
        markerPosition?.let { latLng ->
            addressInfo = withContext(Dispatchers.IO) {
                getAddressGeocodeCurrent(context, latLng)
            }
        }
    }
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapLongClick = { latLng ->
            markerPosition = latLng
            addressInfo = "Resolving address..."
        }
    ) {
        markerPosition?.let { position ->
            Marker(state = MarkerState(position = position),
                title = addressInfo,
                snippet = "Lat: $position.latitude Lng: $position.longitude"
            )

            Log.d("ADDRESS", addressInfo)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    Project1Theme {
        MapsView()
    }
}