package com.wzq.mediaselector.sample

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.wzq.mediaselector.sample.data.ImageSource
import com.wzq.mediaselector.sample.data.PhotoItemData
import com.wzq.mediaselector.sample.ui.page.MediaListPage
import com.wzq.mediaselector.sample.ui.page.SelectorTopBar
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

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Greeting() {
    val result = remember { mutableStateOf(false) }

    val storagePermission =
        rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE) {
            result.value = it
        }

    if (storagePermission.status.isGranted) {
        StartPage()
    } else {
        LaunchedEffect(key1 = "request perms") {
            storagePermission.launchPermissionRequest()
        }
    }

    println("the perms is ${result.value}")

//    if (result.value) {
//        StartPage()
//    }
}


@Preview(showBackground = true)
@Composable
fun StartPage(testMode: Boolean = true) {
    if (testMode) {
        Scaffold(
            topBar = {
                SelectorTopBar()
            },
        ) {
            Box(modifier = Modifier.padding(it)) {
                MediaListPage()
            }
        }
        return
    }
    val photos = remember {
        mutableStateListOf<PhotoItemData>()
    }
    val resolver = LocalContext.current.contentResolver
    LaunchedEffect(key1 = "loadPhotos", block = {
        ImageSource(resolver).query {
            photos.addAll(it)
        }
    })
    MediaListPage(photos = photos)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MediaSelectorTheme {
        Greeting()
    }
}