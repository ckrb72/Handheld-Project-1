package com.example.project1

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import com.example.project1.ui.theme.Project1Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.ceil

class TopHeadlinesActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project1Theme {
                Scaffold(modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text("Top Headlines") },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary,)
                        )
                    }
                ) { innerPadding ->
                    TopHeadlines(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun TopHeadlines(modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE) }
    val categories = listOf("Business", "Entertainment", "General", "Health", "Science", "Sports", "Technology")
    var selectedCategory by remember { mutableStateOf(prefs.getInt("SavedCategory", 0)) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    val apiKey = context.getString(R.string.NEWS_API_KEY)
    var articleList by remember { mutableStateOf<List<ArticleData>>(emptyList()) }
    var pageIndex by remember { mutableStateOf(1) }
    var pageCount by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }
    val articlePerPage = 20;

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
            },
            enabled = !isLoading
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
                                prefs.edit { putInt("SavedCategory", selectedCategory) }
                                dropdownExpanded = false
                                pageIndex = 1

                                // Might not need this
                                pageCount = 1
                            }
                        )
                    }
                }
            }
        }

        LaunchedEffect(selectedCategory, pageIndex) {
            isLoading = true
            val result = withContext(Dispatchers.IO) {
                ArticleManager.retrieveTopHeadlines(categories[selectedCategory].lowercase(), pageIndex, apiKey)
            }
            articleList = result.first
            pageCount = ceil(result.second / articlePerPage.toDouble()).toInt()
            Log.d("ARTICLES", "Page Count: " + pageCount)
            isLoading = false
        }

        val fakeData = getFakeData()

        if (isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
                    .fillMaxHeight(0.90f)
            ) {
                items(articleList) { article ->
                    ArticleCard(article, Modifier) { context ->

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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        pageIndex--
                    },
                    enabled = (pageIndex != 1) && !isLoading
                ) {
                    Text("Previous")
                }

                Text("$pageIndex / $pageCount")

                Button(
                    onClick = {
                        pageIndex++
                    },
                    enabled = (pageIndex != pageCount) && !isLoading
                ) {
                    Text("Next")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview4() {
    Project1Theme {
        TopHeadlines()
    }
}