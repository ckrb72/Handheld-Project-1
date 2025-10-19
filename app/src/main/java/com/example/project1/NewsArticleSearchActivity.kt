package com.example.project1

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.project1.ui.theme.Project1Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.text.lowercase

class NewsArticleSearchActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val intent = intent;
        setContent {
            Project1Theme {
                Scaffold(modifier = Modifier.fillMaxSize(),
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text("Search: " + intent.getStringExtra("SEARCH_TERM") + " Category: " + intent.getStringExtra("CATEGORY") + " ID: " + intent.getStringExtra("SOURCE_ID")) },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary,)
                    )
                }) { innerPadding ->
                    NewsArticleList(
                        category = intent.getStringExtra("CATEGORY").toString(),
                        searchTerm = intent.getStringExtra("SEARCH_TERM").toString(),
                        sourceId = intent.getStringExtra("SOURCE_ID").toString(),
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun NewsArticleList(category: String, searchTerm: String, sourceId: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val apiKey = context.getString(R.string.NEWS_API_KEY)
    var articleList by remember { mutableStateOf<List<ArticleData>>(emptyList()) }
    val articleManager = remember { ArticleManager() }

    LaunchedEffect(category, searchTerm, sourceId) {
        val result = withContext(Dispatchers.IO) {
            articleManager.retrieveArticles(sourceId = sourceId, searchTerm = searchTerm, apiKey = apiKey)
        }

        articleList = result;
    }

    val fakeList = getFakeData()
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxHeight()
                .padding(0.dp, 0.dp, 0.dp, 10.dp)
        ) {
            items(fakeList) { a ->
                ArticleCard(a, Modifier.fillMaxWidth()) {

                }
            }

        }
    }
}

fun getFakeData(): List<ArticleData> {
    return listOf<ArticleData>(
        ArticleData(title = "Title", icon = "Icon", url = "url", description = "Description"),
        ArticleData(title = "Title", icon = "Icon", url = "url", description = "Description"),
        ArticleData(title = "Title", icon = "Icon", url = "url", description = "Description"),
        ArticleData(title = "Title", icon = "Icon", url = "url", description = "Description"),
        ArticleData(title = "Title", icon = "Icon", url = "url", description = "Description"),
        ArticleData(title = "Title", icon = "Icon", url = "url", description = "Description"),
        ArticleData(title = "Title", icon = "Icon", url = "url", description = "Description"),
        ArticleData(title = "Title", icon = "Icon", url = "url", description = "Description"),
        ArticleData(title = "Title", icon = "Icon", url = "url", description = "Description"),
        ArticleData(title = "Title", icon = "Icon", url = "url", description = "Description"),
        ArticleData(title = "Title", icon = "Icon", url = "url", description = "Description")
    )
}

@Composable
fun ArticleCard(article: ArticleData, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier,
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

            Text("Name: " + article.title)
            Text("Description: " + article.description)
            Text("Category: " + article.url)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    Project1Theme {
        NewsArticleList(category = "Category", searchTerm = "Searching", sourceId = "ID AND STUFF", modifier = Modifier)
    }
}