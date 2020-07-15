package com.wzq.media.selector.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.wzq.media.selector.core.MediaSelector
import com.wzq.media.selector.core.config.MimeType
import com.wzq.media.selector.core.config.SelectorConfig
import com.wzq.media.selector.core.config.SelectorType

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.btn).setOnClickListener {
            val selector = MediaSelector(this, SelectorType.IMAGE)

            selector {
                config(SelectorConfig(3))
                onResult {
                    it.forEach {et->
                        println(et)
                    }
                }
            }
        }
    }

}