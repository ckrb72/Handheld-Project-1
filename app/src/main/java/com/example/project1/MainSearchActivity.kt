package com.example.project1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.ui.theme.Project1Theme

class MainSearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project1Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SearchView(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun SearchView(modifier: Modifier = Modifier) {

    var searchTerm by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp).height(150.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            border = BorderStroke(1.dp, Color.Gray)
        ) {
            Row(
                modifier = Modifier.height(IntrinsicSize.Max)
                    .padding(5.dp, 0.dp, 5.dp, 0.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight().fillMaxWidth(0.75f),
                    verticalArrangement = Arrangement.Center
                ) {
                    OutlinedTextField(
                        value = searchTerm,
                        onValueChange = {newValue -> searchTerm = newValue},
                        singleLine = true,
                        label = { Text("Search Terms") },
                        modifier = Modifier.padding(5.dp, 12.dp, 5.dp, 12.dp),
                        trailingIcon = { Icon(Icons.Filled.Search, "Search") }
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(2.dp, 0.dp, 0.dp, 0.dp)
                        .fillMaxHeight()
                ) {
                    Button(
                        onClick = {

                        }
                    ) {
                        Text("Filter")
                    }

                    Button(
                        enabled = searchTerm.isNotBlank(),
                        onClick = {
                            val intent = Intent(context, SourcesActivity::class.java)
                            intent.putExtra("SEARCH_TERM", searchTerm.toString())
                            context.startActivity(intent)
                        }
                    ) {
                        Text(
                            text = "Search",
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        SearchCard(
            title = "Local News",
            onClick = {}
        )

        SearchCard(
            title = "Top Headlines",
            onClick = {}
        )

    }

}

@Composable
fun SearchCard(title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp).height(150.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        border = BorderStroke(1.dp, Color.Gray),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = title)
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun SearchPreview() {
    Project1Theme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            SearchView(modifier = Modifier.padding(innerPadding))
        }
    }
}