package com.wzq.mediaselector.sample.ui.page

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/**
 * create by wzq on 2023/7/24
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorTopBar() {
    CenterAlignedTopAppBar(title = { 
        Text(text = "Selector")
    }, )
}