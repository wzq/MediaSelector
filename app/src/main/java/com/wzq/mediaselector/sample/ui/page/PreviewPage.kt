package com.wzq.mediaselector.sample.ui.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import coil.compose.rememberAsyncImagePainter
import com.wzq.mediaselector.sample.data.PhotoItemData

/**
 * create by wzq on 2023/8/3
 *
 */
@Composable
fun PreviewPage(vararg photos: PhotoItemData) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = Color.Black)) {
        if (photos.isNotEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(photos[0].contentUri),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}