package com.wzq.mediaselector.sample.ui.page

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/**
 * create by wzq on 2023/7/31
 *
 */
@Composable
fun BottomActionBar(selectNum: Int, onSelected: () -> Unit) {
    if (selectNum == 0) {
        return
    }
    BottomAppBar(actions = {
        Text(text = "已选 $selectNum 张图片")
    }, floatingActionButton = {
        FloatingActionButton(onClick = onSelected) {
            Icon(imageVector = Icons.Default.Check, contentDescription = null)
        }
    })
}