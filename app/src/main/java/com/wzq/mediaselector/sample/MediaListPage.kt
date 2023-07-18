package com.wzq.mediaselector.sample

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wzq.mediaselector.sample.data.ImageSource
import com.wzq.mediaselector.sample.data.PhotoItemData

/**
 * create by wzq on 2023/7/18
 *
 */
@Composable
fun MediaListPage() {
    val photos = remember {
        mutableStateListOf<PhotoItemData>()
    }
    val resolver = LocalContext.current.contentResolver
    LaunchedEffect(key1 = null, block = {
        ImageSource(resolver).query {
            println("photo size = ${it.size}")
            photos.addAll(it)
        }
    })

    val selectedIds = rememberSaveable {
        mutableStateOf(emptySet<Long>())
    }

//    val inSelectionMode by remember {
//        derivedStateOf { selectedIds.value.isNotEmpty() }
//    }

    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 96.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        content = {
            this.items(photos, key = { it.id }) {
                val selected = selectedIds.value.contains(it.id)
                MediaItem(it, selected, Modifier.clickable {
                    selectedIds.value = if (selected) {
                        selectedIds.value.minus(it.id)
                    } else {
                        selectedIds.value.plus(it.id)
                    }
                })
            }
        })

}

