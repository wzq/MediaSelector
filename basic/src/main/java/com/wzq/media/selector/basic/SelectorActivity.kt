package com.wzq.media.selector.basic

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import com.wzq.media.selector.basic.preview.PreviewActivity
import com.wzq.media.selector.core.MediaSelector
import com.wzq.media.selector.core.config.SelectorConfig
import com.wzq.media.selector.core.config.SelectorType
import com.wzq.media.selector.core.model.MediaData

/**
 * create by wzq on 2020/7/16
 * basic MediaSelector
 */
class SelectorActivity : AppCompatActivity() {

    private val data = mutableListOf<Pair<String, List<MediaData>>>()

    private val titleView by lazy {
        findViewById<TextView>(R.id.toolbar_title)
    }
    private val adapter by lazy {
        SelectorAdapter(limit, updateBottom)
    }

    private val popupMenu by lazy {
        PopupMenu(this, titleView)
    }

    private val ensureBtn by lazy {
        findViewById<Button>(R.id.ensure)
    }
    private val previewBtn by lazy {
        findViewById<Button>(R.id.preview)
    }
    private var limit = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selector)
        findViewById<View>(R.id.toolbar_camera).setOnClickListener {
            openCamera()
        }
        init()
    }

    private val REQUEST_IMAGE_CAPTURE = 0x11
    private fun openCamera() {
        val uri = createImageUri() ?: return
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun createImageUri(): Uri? {
        val status = Environment.getExternalStorageState()
        //判断是否有SD卡，优先使用SD卡存储，当没有SD卡时使用手机储存
        return if (status == Environment.MEDIA_MOUNTED) {
            contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                ContentValues()
            )
        } else {
            contentResolver.insert(
                MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                ContentValues()
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            MediaSelector(this, SelectorType.IMAGE).querySource {
                loadImages(it)
            } //refresh data
        }
    }

    private fun init() {
        val config = intent.getParcelableExtra<SelectorConfig>("config")
        limit = config?.limit ?: 1
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

        ensureBtn.text = getString(R.string.basic_ensure, 0, limit)
        ensureBtn.setOnClickListener { submitSelect() }

        val listView = findViewById<RecyclerView>(R.id.listView)
        listView.layoutManager = GridLayoutManager(this, 3)
        listView.adapter = adapter
        val list = intent.getParcelableArrayListExtra<MediaData>("data") ?: return
        loadImages(list)

        if (config?.needPreview == true) {
            previewBtn.visibility = View.VISIBLE
            previewBtn.setOnClickListener { preview() }
        } else {
            previewBtn.visibility = View.GONE
        }
    }

    //选择类目
    private fun selectColumn(index: Int) {
        if (index > -1 && index < data.size) {
            val item = data[index]
            titleView.text = item.first
            titleView.isSelected = false
            adapter.submitList(item.second)
        }
    }

    private fun loadImages(list: List<MediaData>) {
        if (list.isEmpty()) return
        data.clear()
        data.add(Pair("全部", list))
        val columns = list.groupBy { et ->
            et.dirName ?: "未知"
        }.toList().sortedByDescending { p -> p.second.size }
        data.addAll(columns)
        buildMenus()
        selectColumn(0)//default index
    }

    //生成类目
    private fun buildMenus() {
        data.forEachIndexed { index, pair ->
            val str = "${pair.first}  (${pair.second.size})"
            popupMenu.menu.add(0, index, index, str)
        }
        popupMenu.setOnMenuItemClickListener {
            selectColumn(it.itemId)
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
        val data = Intent().putParcelableArrayListExtra("data", adapter.selectedItems)
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

    private val updateBottom = fun(selected: Int, limit: Int) {
        ensureBtn.isEnabled = selected > 0
        previewBtn.isEnabled = selected > 0
        ensureBtn.text = getString(R.string.basic_ensure, selected, limit)
    }
}