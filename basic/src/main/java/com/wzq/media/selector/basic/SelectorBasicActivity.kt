package com.wzq.media.selector.basic

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.wzq.media.selector.basic.preview.PreviewActivity
import com.wzq.media.selector.core.MediaSelector
import com.wzq.media.selector.core.PermissionFragment
import com.wzq.media.selector.core.config.MimeType
import com.wzq.media.selector.core.config.SelectorConfig
import com.wzq.media.selector.core.config.SelectorType
import com.wzq.media.selector.core.model.MediaData

/**
 * create by wzq on 2020/7/16
 * basic MediaSelector
 */
class SelectorBasicActivity : AppCompatActivity() {


    companion object {
        fun open(
            activity: Activity?,
            type: SelectorType,
            mConfig: SelectorConfig? = SelectorConfig()
        ) {
            activity ?: return
            val intent = Intent(activity, SelectorBasicActivity::class.java)
            intent.putExtra("config", mConfig)
            intent.putExtra("type", type)
            activity.startActivityForResult(intent, MediaSelector.SELECTOR_REQ)
        }
    }


    private val titleView by lazy {
        findViewById<TextView>(R.id.toolbar_title)
    }

    private val popupMenu by lazy {
        PopupMenu(this, titleView)
    }

    private val mediaSelector by lazy {
        val type = intent.getSerializableExtra("type") as SelectorType
        MediaSelector.create(this, type).mime(MimeType.JPEG, MimeType.PNG)
    }
    private val config by lazy { intent.getParcelableExtra<SelectorConfig>("config") }

    private val adapter = SelectorAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selector)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        titleView.setOnClickListener {
            it.isSelected = !it.isSelected
            if (it.isSelected) {
                popupMenu.show()
            } else {
                popupMenu.dismiss()
            }
        }
        PermissionFragment.request(supportFragmentManager) { hasPermission ->
            if (hasPermission) {
                init()
            } else {
                Toast.makeText(this, R.string.basic_permission, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun init() {
        config?.run { mediaSelector.config(this) }
        val limit = config?.limit ?: 1
        val previewBtn = findViewById<Button>(R.id.preview)
        if (config?.needPreview == true) {
            previewBtn.visibility = View.VISIBLE
            previewBtn.setOnClickListener { preview() }
        } else {
            previewBtn.visibility = View.GONE
        }
        val takePhoto = findViewById<View>(R.id.toolbar_camera)
        if (config?.needTakePhoto == true) {
            takePhoto.visibility = View.VISIBLE
            takePhoto.setOnClickListener {
                openCamera()
            }
        } else {
            takePhoto.visibility = View.GONE
        }
        val ensureBtn = findViewById<Button>(R.id.ensure)
        ensureBtn.setOnClickListener { submitSelect() }
        ensureBtn.text = getString(R.string.basic_ensure, 0, limit)

        val listView = findViewById<RecyclerView>(R.id.listView)
        listView.layoutManager = GridLayoutManager(this, 3)
        listView.adapter = adapter
        adapter.setLimit(config?.limit ?: 1)
        adapter.setOnSelected { selected ->
            ensureBtn.isEnabled = selected > 0
            previewBtn.isEnabled = selected > 0
            ensureBtn.text = getString(R.string.basic_ensure, selected, limit)
        }

        refreshData()
    }

    private fun refreshData() {
        mediaSelector.querySource {
            val data = mutableListOf<Pair<String, List<MediaData>>>()
            data.add(Pair("全部", it))
            val columns = it.groupBy { et ->
                et.dirName ?: "未知"
            }.toList().sortedByDescending { p -> p.second.size }
            data.addAll(columns)
            buildMenus(data)
            selectColumn(data, 0)//default index
        }
    }

    //选择类目
    private fun selectColumn(data: List<Pair<String, List<MediaData>>>, index: Int) {
        if (index > -1 && index < data.size) {
            val item = data[index]
            titleView.text = item.first
            titleView.isSelected = false
            adapter.submitList(item.second)
        }
    }

    //生成类目
    private fun buildMenus(data: List<Pair<String, List<MediaData>>>) {
        popupMenu.menu.clear()
        data.forEachIndexed { index, pair ->
            val str = "${pair.first}  (${pair.second.size})"
            popupMenu.menu.add(0, index, index, str)
        }
        popupMenu.setOnMenuItemClickListener {
            selectColumn(data, it.itemId)
            true
        }
        popupMenu.setOnDismissListener { titleView.isSelected = false }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun submitSelect() {
        val selected = adapter.selectedItems
        if (selected.isNullOrEmpty()) return

        val isOrigin = findViewById<CheckBox>(R.id.origin).isChecked
        val data = Intent().putParcelableArrayListExtra("data", adapter.selectedItems)
            .putExtra("isOrigin", isOrigin)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    //内容预览
    private fun preview() {
        val mediaType = intent?.getSerializableExtra("type") as? SelectorType ?: return

        val selected = adapter.selectedItems
        if (selected.isNullOrEmpty()) return
        val intent = Intent(this, PreviewActivity::class.java)
            .putParcelableArrayListExtra("data", adapter.selectedItems)
            .putExtra("type", mediaType)
        startActivity(intent)
    }

    private val REQUEST_IMAGE_CAPTURE = 0x11
    private var photoUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            refreshData() //refresh data
        }
    }

}