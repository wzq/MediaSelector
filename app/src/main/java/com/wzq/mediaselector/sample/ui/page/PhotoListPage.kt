package com.wzq.mediaselector.sample.ui.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.onLongClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.wzq.mediaselector.sample.data.PhotoItemData
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/**
 * create by wzq on 2023/7/18
 *
 */


@Composable
fun MediaListPage(
    navController: NavController,
    photos: List<PhotoItemData>
) {
    val selectedIds = rememberSaveable {
        mutableStateOf(emptySet<Int>())
    }

    val inSelectionMode by remember {
        derivedStateOf { selectedIds.value.isNotEmpty() }
    }

    val state = rememberLazyGridState()

    val autoScrollSpeed = remember {
        mutableStateOf(0f)
    }

    LaunchedEffect(key1 = autoScrollSpeed.value, block = {
        if (autoScrollSpeed.value != 0f) {
            while (isActive) {
                state.scrollBy(autoScrollSpeed.value)
                delay(10)
            }
        }
    })

    Scaffold(topBar = {
        SelectorTopBar()
    }, bottomBar = {
        BottomActionBar(selectedIds.value.size) {
            navController.currentBackStackEntry?.savedStateHandle?.apply {
                set("photos", photos.filter { it.id in selectedIds.value })
            }
            navController.navigate("preview")
        }
    }) {
        Box(modifier = Modifier.padding(it)) {
            LazyVerticalGrid(state = state,
                columns = GridCells.Adaptive(minSize = 128.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier.photoGridDragHandler(lazyGridState = state,
                    haptics = LocalHapticFeedback.current,
                    selectedIds = selectedIds,
                    autoScrollSpeed = autoScrollSpeed,
                    autoScrollThreshold = with(LocalDensity.current) { 40.dp.toPx() }),
                content = {
                    items(photos, key = { it.id }) { photo ->
                        val selected by remember {
                            derivedStateOf { selectedIds.value.contains(photo.id) }
                        }
                        MediaItem(photo, inSelectionMode, selected,
                            Modifier
                                .semantics {
                                    if (!inSelectionMode) {
                                        onLongClick("Select") {
                                            selectedIds.value += photo.id
                                            true
                                        }
                                    }
                                }
                                .then(
                                    if (inSelectionMode) {
                                        Modifier.toggleable(value = selected,
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null,
                                            onValueChange = {
                                                if (it) {
                                                    selectedIds.value += photo.id
                                                } else {
                                                    selectedIds.value -= photo.id
                                                }
                                            })
                                    } else {
                                        Modifier.clickable {
                                            navController.currentBackStackEntry?.savedStateHandle?.apply {
                                                set("photos", listOf(photo))
                                            }
                                            navController.navigate("preview")
                                        }
                                    }
                                )
                        )
                    }
                })
        }
    }

}

