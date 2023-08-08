package com.wzq.mediaselector.sample

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.wzq.mediaselector.sample.data.PhotoItemData
import com.wzq.mediaselector.sample.ui.page.MediaListPage
import com.wzq.mediaselector.sample.ui.page.PhotosViewModel
import com.wzq.mediaselector.sample.ui.page.PreviewPage
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
        LocalNavHost()
    } else {
        LaunchedEffect(key1 = "request perms") {
            storagePermission.launchPermissionRequest()
        }
    }

    println("the perms is ${result.value}")
}


@Composable
fun LocalNavHost(
    navController: NavHostController = rememberNavController(),
    globalVm: PhotosViewModel = viewModel()
) {
    NavHost(navController = navController, startDestination = "main") {
        composable("main") { MediaListPage(navController = navController, globalVm.photos) }
        composable("preview") {
            val p =
                navController.previousBackStackEntry?.savedStateHandle?.get<List<PhotoItemData>>("photos") ?: emptyList()
            val photos = p.toTypedArray()
            PreviewPage(*photos)
        }
    }
}
