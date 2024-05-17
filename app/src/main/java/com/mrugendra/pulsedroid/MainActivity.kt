package com.mrugendra.pulsedroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mrugendra.pulsedroid.ui.PulseViewModel
import com.mrugendra.pulsedroid.ui.theme.PulseDroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PulseDroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PulseUi(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


@Composable
fun PulseUi(name: String, modifier: Modifier = Modifier) {
    val appViewModel:PulseViewModel = viewModel()
    val appUiState = appViewModel.extUiState.collectAsState().value
//    var playState by remember { mutableStateOf(false) }

    Scaffold{
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = appUiState.server,
                onValueChange = { appViewModel.ServerChange(it) },
                label = { Text(text = "Server ip") }
            )

            TextField(
                value = appUiState.port,
                onValueChange = { appViewModel.PortChange(it) },
                label = { Text(text = "Port number") }
            )

            Button(
                onClick = {
                    appViewModel.playAudio(appUiState.playButton)
                }
            ) {
                Text(
                    text = if (!appUiState.playButton) {
                        "Play"
                    } else {
                        "Stop"
                    }
                )
            }
//            Text(text = "Error appear here : ")
            Text(text = appUiState.error
                )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PulseDroidTheme {
        PulseUi("Android")
    }
}