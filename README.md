# MediaSelector
   提供android图片选择和预览功能
   
## 简介     

  * 媒体资源选择库
  * 兼容版本 19 ~ 29  
  * 自询问并获取相关权限
  
  ![art1](/screenshot/art1.gif)

## 使用
需要添加
```
    maven { url 'https://jitpack.io' }
```

1. 获取媒体资源
```
implementation 'com.github.wzq.MediaSelector:core:x.x.x'
```

```kotlin
    ImageSource().getMediaSource(context)
```

2. 打开一个带样式和功能的activity，在onActivityResult中返回选择结果
```
    implementation 'com.github.wzq.MediaSelector:basic:1.3.0'
```
```kotlin
    SelectorBasicActivity.open(context)
```

```kotlin
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MediaSelector.SELECTOR_REQ) {
            val items = data?.getParcelableArrayListExtra<MediaData>("data") ?: return
            //TODO
        }
    }
```

3.数据类MediaData
```kotlin
    data class MediaData(
        val id: String,
        val name: String, //名称
        val size: Long = 0,  //文件大小
        val orientation: Int,
        val createTime: String?,
        val dirId: String?, //文件夹ID
        val dirName: String?, //文件夹 名称
        val path: String?, //路径 尽量使用uri
        val duration: Long = -1, //视频时长
    ) : Parcelable
```

