# MediaSelector
   提供android图片选择和预览功能 
   
   ### *[点击查看compose版本](https://github.com/wzq/MediaSelector/tree/compose#mediaselector)*
   
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
