package com.example.project1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project1.ui.theme.Project1Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SourcesActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent;
        enableEdgeToEdge()
        setContent {
            Project1Theme {
                Scaffold(modifier = Modifier.fillMaxSize(),
                        topBar = { CenterAlignedTopAppBar(
                            title = { Text("Search for " + intent.getStringExtra("SEARCH_TERM").toString())},
                            actions = {},
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary,)
                        )}
                    ) { innerPadding ->
                    SourcesView(
                        searchTerm = intent.getStringExtra("SEARCH_TERM").toString(),
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourcesView(modifier: Modifier = Modifier, searchTerm: String = "") {
    val categories = listOf("Business", "Entertainment", "General", "Health", "Science", "Sports", "Technology")
    var selectedCategory by remember { mutableStateOf(0) }
    var dropdownExpanded by remember { mutableStateOf(false) }


    val context = LocalContext.current
    val apiKey = context.getString(R.string.NEWS_API_KEY)
    val sourcesManager = remember { SourceManager() }
    var sourceList by remember { mutableStateOf<List<SourceData>>(emptyList()) }


    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Category:",
        )

        Card(
            modifier = Modifier.fillMaxWidth(0.75f).height(60.dp)
                .padding(25.dp, 0.dp, 25.dp, 16.dp)
                .height(75.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            border = BorderStroke(1.dp, Color.Gray),
            onClick = {
                dropdownExpanded = true
            }
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.padding(15.dp, 0.dp, 0.dp, 0.dp)
                ) {
                    Text(
                        text = categories[selectedCategory],
                        fontSize = 20.sp
                    )

                    Icon(Icons.Filled.ArrowDropDown, "Dropdown")
                }

                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = {
                        dropdownExpanded = false
                    }
                ) {
                    for ((index, item) in categories.withIndex()) {
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                selectedCategory = index
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }



        LaunchedEffect(selectedCategory) {
            val result = withContext(Dispatchers.IO) {
                sourcesManager.retrieveSources(categories[selectedCategory].lowercase(), apiKey)
            }
            sourceList = result;
        }

        //val source = getSources(searchTerm, categories[selectedCategory])
        LazyColumn(
            modifier = Modifier.fillMaxHeight(0.80f)
                .padding(0.dp, 0.dp, 0.dp, 10.dp)
        ) {
            items(sourceList) { s ->
                SourceCard(s, Modifier.fillMaxWidth()) {
                    val intent = Intent(context, NewsArticleSearchActivity::class.java)
                    intent.putExtra("CATEGORY", s.category)
                    intent.putExtra("SEARCH_TERM", searchTerm)
                    intent.putExtra("SOURCE_ID", s.id)
                    intent.putExtra("SOURCE_NAME", s.name)
                    context.startActivity(intent)
                }
            }
        }


        Button(
            onClick = {
                val intent = Intent(context, NewsArticleSearchActivity::class.java)
                intent.putExtra("SEARCH_TERM", searchTerm)
                intent.putExtra("CATEGORY", categories[selectedCategory])
                intent.putExtra("SOURCE_ID", "")
                intent.putExtra("SOURCE_NAME", "")
                context.startActivity(intent)
            },
            modifier = Modifier.padding(0.dp, 20.dp)
        ) {
            Text("Skip (Search All Sources)")
        }
    }
}

@Composable
fun SourceCard(source: SourceData, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.fillMaxHeight(0.25f)
            .padding(10.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        border = BorderStroke(1.dp, Color.Gray),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(source.name, fontWeight = FontWeight.Bold, fontSize = 20.sp, textDecoration = TextDecoration.Underline)
            Text(source.description)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Project1Theme {
        Scaffold(modifier = Modifier.fillMaxSize(),
                topBar = { CenterAlignedTopAppBar(
                    title = { Text("Search: Search Term")},
                    actions = {},
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,)
                )
                }
        ) { innerPadding ->
            SourcesView(
                searchTerm = "Search Term",
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}