package com.wzq.media.selector.demo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.wzq.media.selector.basic.openBasicPage
import com.wzq.media.selector.core.MediaSelector
import com.wzq.media.selector.core.config.SelectorConfig
import com.wzq.media.selector.core.config.SelectorType
import com.wzq.media.selector.core.model.MediaData

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.btn).setOnClickListener {
            MediaSelector(this, SelectorType.IMAGE).config(SelectorConfig(limit = 3))
                .openBasicPage(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MediaSelector.SELECTOR_REQ) {
            val items = data?.getParcelableArrayListExtra<MediaData>("data") ?: return
            println(items.toString())
        }
    }


}