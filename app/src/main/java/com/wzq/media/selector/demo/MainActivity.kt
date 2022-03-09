package com.wzq.media.selector.demo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import com.wzq.media.selector.basic.SelectorBasicActivity
import com.wzq.media.selector.core.MediaSelector
import com.wzq.media.selector.core.config.SelectorConfig
import com.wzq.media.selector.core.config.SelectorType
import com.wzq.media.selector.core.model.MediaData

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.btn1).setOnClickListener {
            SelectorBasicActivity.open(this, SelectorType.IMAGE, SelectorConfig(limit = 3))
        }

        findViewById<View>(R.id.btn2).setOnClickListener {
            SelectorBasicActivity.open(
                this,
                SelectorType.VIDEO,
                SelectorConfig(limit = 4, needTakePhoto = false)
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MediaSelector.SELECTOR_REQ) {
            val items = data?.getParcelableArrayListExtra<MediaData>("data") ?: return
            val str = StringBuilder()
            str.append(data.getBooleanExtra("isOrigin", false))
            str.append("\n\n")
            items.forEach { str.append(it); str.append("\n\n") }
            findViewById<TextView>(R.id.content).text = str
        }
    }


}