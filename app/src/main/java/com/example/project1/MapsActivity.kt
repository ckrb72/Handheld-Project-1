package com.example.project1

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
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
                        //val city = address.locality ?: "Unknown City"
                        val state = address.adminArea ?: "Unknown State"
                        val country = address.countryName ?: "Unknown Country"
                        "$state, $country"
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

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
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

        val fakeArticles = getFakeData()

        markerPosition?.let {
            Card(
                modifier = Modifier.fillMaxWidth(0.85f)
                    .fillMaxHeight(0.25f)
                    .padding(0.dp, 0.dp, 0.dp, 25.dp)
            ) {
                LazyRow(
                    modifier = Modifier.fillMaxSize()
                        .padding(10.dp)
                ) {
                    items(fakeArticles) { article ->
                        ArticleRowCard(article) {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse(article.url)
                            }
                            context.startActivity(intent)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArticleRowCard(article: ArticleData, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.fillMaxHeight()
            .fillMaxWidth(0.25f),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        border = BorderStroke(1.dp, Color.Gray),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier.fillMaxHeight()
                    .width(100.dp)
                    .padding(10.dp)
            ) {
                AsyncImage(
                    model = article.icon,
                    contentDescription = null
                )
            }
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(article.title)
                Text(article.source)
                Text(article.description)

            }
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