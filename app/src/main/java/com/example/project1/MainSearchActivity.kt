package com.example.project1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import com.example.project1.ui.theme.Project1Theme

class MainSearchActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project1Theme {
                Scaffold(modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text("Home") },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary,)
                        )
                    }
                ) { innerPadding ->
                    SearchView(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun SearchView(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE) }
    var searchTerm by remember { mutableStateOf(prefs.getString("SEARCH_TERM", "") ?: "") }

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
                modifier = Modifier.fillMaxHeight()
                    .padding(5.dp, 0.dp, 5.dp, 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchTerm,
                    onValueChange = {newValue -> searchTerm = newValue},
                    singleLine = true,
                    label = { Text("Search Terms") },
                    modifier = Modifier.padding(5.dp, 12.dp, 5.dp, 12.dp)
                        .fillMaxWidth(0.75f),
                    trailingIcon = { Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        modifier = Modifier.clickable{
                            if (searchTerm.isNotBlank()) {
                                prefs.edit { putString("SEARCH_TERM", searchTerm) }
                                val intent = Intent(context, SourcesActivity::class.java)
                                intent.putExtra("SEARCH_TERM", searchTerm.toString())
                                context.startActivity(intent)
                            }
                        }
                    )
                    }
                )

                Button(
                    enabled = searchTerm.isNotBlank(),
                    onClick = {
                        prefs.edit { putString("SEARCH_TERM", searchTerm) }
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

        SearchCard(
            title = "Local News",
            onClick = {
                val intent = Intent(context, MapsActivity::class.java)
                context.startActivity(intent)
            }
        )

        SearchCard(
            title = "Top Headlines",
            onClick = {
                val intent = Intent(context, TopHeadlinesActivity::class.java)
                context.startActivity(intent)
            }
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

            Text(
                text = "Click Here to View",
                modifier = Modifier.fillMaxWidth()
                    .padding(20.dp),
                textAlign = TextAlign.Center,
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun SearchPreview() {
    Project1Theme {
        Scaffold(modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Home") },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,)
                )
            }
            ) { innerPadding ->
            SearchView(modifier = Modifier.padding(innerPadding))
        }
    }
}