package com.example.togglevpn

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.togglevpn.ui.theme.ToggleVPNTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

enum class VPNConnectionState(val color : Color) {
    Connected(Color.Green),
    Disconnected(Color.Yellow),
    Unknown(Color.LightGray)
}

class MainActivity : ComponentActivity() {
    val IP = "192.168.1.224"
    val port = "8081"
    val url = "http://$IP:$port/"
    var vpnConnectionState by mutableStateOf(VPNConnectionState.Unknown)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                while(true) {
                    status()
                    delay(3000)
                }
            }
        }
        setContent {
            ToggleVPNTheme {
                // A surface container using the 'background' color from the theme
                val backgroundColor by animateColorAsState(vpnConnectionState.color, animationSpec = tween(2000))
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color =  backgroundColor
                ) {
                    Content()
                }
            }
        }
    }

    fun sendRequest(
        cmd: String,
        onSuccess: (String) -> Unit = {},
        onFailure: () -> Unit = {}
    ) {
        val request = Request.Builder().url(url + cmd).build()
        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                onSuccess(response.body?.string() ?: "")
            }

            override fun onFailure(call: Call, e: IOException) {
                onFailure()
            }
        })
    }

    fun status() {
        sendRequest(
           cmd = "status",
           onSuccess = { body ->
               vpnConnectionState = when (body) {
                   "on" -> VPNConnectionState.Connected
                   else -> VPNConnectionState.Disconnected
               }
           },
           onFailure = {
               vpnConnectionState = VPNConnectionState.Unknown
           }
        )
    }

    fun turnOff() {
        sendRequest("off")
    }

    fun turnOn() {
        sendRequest("on")
    }

    @Composable
    fun DefaultOutlineButton(onClick : ()->Unit, text: String) {
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(0.4f).wrapContentHeight().padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Text(text, Modifier.padding(8.dp))
        }
    }

    @Composable
    @Preview(showBackground = true)
    fun Content() {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DefaultOutlineButton(::turnOn, "ON")
            DefaultOutlineButton(::turnOff, "OFF")
        }
    }
}
