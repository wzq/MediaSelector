package com.wzq.mediaselector.sample

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wzq.mediaselector.sample.data.PhotoItemData
import com.wzq.mediaselector.sample.ui.page.PhotoListPage
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

/**
 * request permissions
 */
@Composable
fun Greeting() {
    val storagePerm = Manifest.permission.READ_EXTERNAL_STORAGE
    val isGranted =
        LocalContext.current.checkSelfPermission(storagePerm) == PackageManager.PERMISSION_GRANTED
    val permissionState = remember {
        mutableStateOf(isGranted)
    }

    val showDialog = remember {
        mutableStateOf(false)
    }

    val requests =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = {
                println(it)
                val ret = it[storagePerm] ?: false
                showDialog.value = !ret
                permissionState.value = ret
            })

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
            },
            confirmButton = {},
            title = { Text(text = "提示") },
            text = { Text(text = "权限已被拒绝") },
            dismissButton = {
                ClickableText(text = AnnotatedString("知道了"), onClick = {
                    showDialog.value = false
                })
            })
    }

    if (permissionState.value) {
        LocalNavHost()
    } else {
        Box(Modifier.fillMaxSize()) {
            Button(modifier = Modifier.align(androidx.compose.ui.Alignment.Center), onClick = {
                requests.launch(arrayOf(storagePerm))
            }) {
                Text(text = "start")
            }
        }
    }
}


@Composable
fun LocalNavHost(
    navController: NavHostController = rememberNavController(),
    globalVm: PhotosViewModel = viewModel()
) {
    NavHost(navController = navController, startDestination = "main") {
        composable("main") { PhotoListPage(navController = navController, globalVm.photos) }
        composable("preview") {
            val p =
                navController.previousBackStackEntry?.savedStateHandle?.get<List<PhotoItemData>>("photos")
                    ?: emptyList()
            val photos = p.toTypedArray()
            PreviewPage(*photos)
        }
    }
}
