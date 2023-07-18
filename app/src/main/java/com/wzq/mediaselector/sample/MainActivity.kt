@file:OptIn(ExperimentalPermissionsApi::class)

package com.wzq.mediaselector.sample

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.wzq.mediaselector.sample.ui.theme.MediaSelectorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MediaSelectorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
                }
            }
        }
    }
}

@Composable
fun Greeting() {
    val result = remember { mutableStateOf(false) }

    val storagePermission =
        rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE) {
            result.value = it
        }

    if (storagePermission.status.isGranted) {
        MediaListPage()
    } else {
        LaunchedEffect(key1 = "request perms") {
            storagePermission.launchPermissionRequest()
        }
    }

    if (result.value) {
        MediaListPage()
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MediaSelectorTheme {
        Greeting()
    }
}