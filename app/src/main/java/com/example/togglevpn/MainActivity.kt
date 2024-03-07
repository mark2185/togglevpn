package com.example.togglevpn

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.togglevpn.ui.theme.ToggleVPNTheme
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToggleVPNTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
                }
            }
        }
    }
}

@Composable
fun Greeting() {
    val context = LocalContext.current
    var isConnected by remember {
        mutableStateOf(false)
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            //execute network request here
            //set isConnected status
            isConnected = true
        }
    }
    Column(modifier = Modifier.fillMaxSize().background(if (isConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally){
        var IP = "192.168.1.224"
        var port = "8081"
        TextField(
            value = "192.168.1.224",
            onValueChange = { IP = it },
            label = { Text("IP") }
        )
        TextField(
            value = "8081",
            onValueChange = { IP = it },
            label = { Text("IP") }
        )
    Button(onClick = {
        val url = "http://$IP:$port/"
        val request = Request.Builder()
            .url(url + "on")
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()/
            }
            override fun onResponse(call: Call, response: Response) {
               response.use {
                   Handler(Looper.getMainLooper()).post {
                       Toast.makeText(
                           context,
                           if (response.isSuccessful) "Turned on!" else "Already turned on!",
                           Toast.LENGTH_SHORT
                       ).show()
                   }
               }
            }
        })
    },
        modifier = Modifier.fillMaxWidth()) {
        Text("On")
    }
    Button(onClick = {
        val url = "http://$IP:$port/"
        val request = Request.Builder()
            .url(url + "off")
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) {}
        })
    },
        modifier = Modifier.fillMaxWidth()) {
        Text("Off")
    }
    }
}