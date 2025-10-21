package com.example.project1

import android.content.Context
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
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import coil.compose.AsyncImage
import com.example.project1.ui.theme.Project1Theme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
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
                        val country = address.countryName ?: "Unknown Country"
                        "$city, $state, $country"
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
    var addressInfo by remember { mutableStateOf("Long Click on Map") }
    val context = LocalContext.current
    var articleList by remember { mutableStateOf<List<ArticleData>>(emptyList()) }
    val apiKey = context.getString(R.string.NEWS_API_KEY)
    val prefs = remember { context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE) }
    var markerPosition by remember { mutableStateOf<LatLng?>(LatLng(prefs.getFloat("MapLatitude", 0.0f).toDouble(), prefs.getFloat("MapLongitude", 0.0f).toDouble())) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(prefs.getFloat("MapLatitude", 0.0f).toDouble(), prefs.getFloat("MapLongitude", 0.0f).toDouble()), 10.0f)
    }
    // This is called again every time the marker position is changed, which happens whenever we long click
    LaunchedEffect(markerPosition) {
        markerPosition?.let { latLng ->
            addressInfo = withContext(Dispatchers.IO) {
                getAddressGeocodeCurrent(context, latLng)
            }

            val locationList: List<String> = addressInfo.split(", ")
            val result = withContext(Dispatchers.IO) {
                var location = locationList[1]
                if (locationList[1] == "Unknown State")
                {
                    location = locationList[2]
                }
                ArticleManager.retrieveLocalArticles(location, apiKey)
            }

            articleList = result
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
                cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, cameraPositionState.position.zoom)
                prefs.edit { putFloat("MapLatitude", latLng.latitude.toFloat()) }
                prefs.edit { putFloat("MapLongitude", latLng.longitude.toFloat()) }
                addressInfo = "Resolving address..."
            }
        ) {

            markerPosition?.let { position ->
                Marker(state = MarkerState(position = position),
                    title = addressInfo,
                    snippet = "Lat: " + position.latitude + "Lng: " + position.longitude
                )
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
                    items(articleList) { article ->
                        ArticleRowCard(article, modifier = Modifier.padding(5.dp)) { context ->
                            try {
                                if (!article.url.isNullOrBlank()) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                                    context.startActivity(intent)
                                }
                            } catch(e: Exception) {
                                Log.d("EXCEPTION", "" + e.message)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArticleRowCard(article: ArticleData, modifier: Modifier = Modifier, onClick: (Context) -> Unit) {
    val context = LocalContext.current
    Card(
        modifier = modifier.fillMaxHeight()
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        border = BorderStroke(1.dp, Color.Gray),
        onClick = { onClick(context) }
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                modifier = Modifier.fillMaxHeight()
                    .fillMaxWidth(0.2f)
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
//                Image(
//                    painter = painterResource(R.drawable.ic_launcher_background),
//                    contentDescription = null,
//                    modifier = Modifier.size(100.dp)
//                )
                AsyncImage(
                    model = article.icon,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )
            }
            Column (
                modifier = Modifier.fillMaxHeight()
                    .width(200.dp)
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(article.title, fontSize = 10.sp, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline)
                Text(article.source, fontSize = 10.sp)
                Text(article.description, fontSize = 10.sp)

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