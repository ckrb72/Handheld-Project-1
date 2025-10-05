package com.example.project1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.project1.ui.theme.Project1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project1Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Welcome",
            modifier = Modifier.padding(0.dp, 20.dp, 0.dp, 40.dp)
        )

        TextField(
            value = username,
            onValueChange = {newValue -> username = newValue},
            singleLine = true,
            label = { Text("Username") }
        )

        Spacer(
            Modifier.height(25.dp)
        )

        TextField(
            value = password,
            onValueChange = {newValue -> password = newValue},
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            label = { Text("Password") }
        )

        Button(
            enabled = checkCredentials(username, password),
            onClick = {
                val intent = Intent(context, MainSearchActivity::class.java)
                context.startActivity(intent)
            }
        ) { Text("Sign In") }
    }
}

fun checkCredentials(username: String, password: String): Boolean {
    return password.filter{ !it.isWhitespace() }.length >= 8
            && username.filter{ !it.isWhitespace() }.length >= 5
}


@Preview(showBackground = true)
@Composable
fun Preview() {
    Project1Theme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            LoginScreen(
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}