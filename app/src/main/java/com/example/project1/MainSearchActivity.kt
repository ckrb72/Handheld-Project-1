package com.example.project1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.project1.ui.theme.Project1Theme

class MainSearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project1Theme {
                SearchView(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun SearchView(modifier: Modifier = Modifier) {

    var searchTerm by remember { mutableStateOf("") }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Max)
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                TextField(
                    value = searchTerm,
                    onValueChange = {newValue -> searchTerm = newValue},
                    singleLine = true,
                    label = { Text("Search Terms") },
                    modifier = Modifier.fillMaxHeight().padding(0.dp, 8.dp, 0.dp, 8.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(5.dp, 0.dp, 0.dp, 0.dp)
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

                    }
                ) {
                    Text("Search")
                }
            }
        }

    }

}

@Preview(showBackground = true)
@Composable
fun SearchPreview() {
    Project1Theme {
        SearchView(modifier = Modifier.fillMaxSize())
    }
}