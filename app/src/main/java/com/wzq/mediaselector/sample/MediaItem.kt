package com.wzq.mediaselector.sample

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.wzq.mediaselector.sample.data.PhotoItemData

@Composable
fun MediaItem(
    photo: PhotoItemData, selected: Boolean, modifier: Modifier
) {
    Surface(
        tonalElevation = 3.dp, modifier = modifier.aspectRatio(0.75f)
    ) {

        Box {
            val transition = updateTransition(targetState = selected, label = "selected")
            val padding by transition.animateDp(label = "padding") { selected ->
                if (selected) 10.dp else 0.dp
            }

            val roundCornerShape by transition.animateDp(label = "corner") {
                if (it) 16.dp else 0.dp
            }

            Image(
                painter = rememberAsyncImagePainter(photo.contentUri),
                contentDescription = null,
                modifier = Modifier
                    .matchParentSize()
                    .padding(all = padding)
                    .clip(
                        RoundedCornerShape(roundCornerShape)
                    )
            )
            if (selected) {
                val bgColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                Icon(
                    Icons.Filled.CheckCircle,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(4.dp)
                        .border(2.dp, bgColor, CircleShape)
                        .clip(CircleShape)
                        .background(bgColor)
                )
            } else {
                Icon(
                    Icons.Outlined.CheckCircle,
                    tint = Color.White.copy(alpha = 0.7f),
                    contentDescription = null,
                    modifier = Modifier.padding(6.dp)
                )
            }
        }
    }
}