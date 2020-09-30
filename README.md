# MediaSelector
   提供android图片、视频等媒体资源选择和预览功能
   
## 简介     

  * 媒体资源选择库，支持图片，视频等资源
  * 兼容版本 19 ~ 29  
  * 同时支持androidx 和 support
  * 支持询问并获取相关权限
  
  ![art1](/screenshot/art1.gif)

## 使用
需要添加
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

1. 获取媒体资源
```
implementation 'com.github.wzq.MediaSelector:core:1.2.0'
```

```kotlin
   MediaSelector(context, SelectorType.IMAGE) //SelectorType资源类型
           .config(SelectorConfig(limit = 3)) //一些常用设置，如选择个数限制、是否预览等
           .mime(MimeType.JPEG) //资源mime类型，支持多个mime
           .querySource { list ->
              //处理资源列表
           }
```

2. 打开一个带样式和功能的activity，在onActivityResult中返回选择结果
```
implementation 'com.github.wzq.MediaSelector:basic:1.2.0'
```
```kotlin
  SelectorBasicActivity.open(context, SelectorType.IMAGE, SelectorConfig(limit = 4, needTakePhoto = true))
```

3.数据类MediaData
```kotlin
       data class MediaData(
          val uri: Uri,  //uri
          val name: String?, //名称
          val size: Int = 0,  //文件大小
          val path: String?, //路径（该属性已经废弃，尽量使用uri）
          val dirId: String?, //文件夹ID
          val dirName: String?, //文件夹 名称
          val duration: Long = -1, //视频时长
          var state: Boolean = false //选择状态
      ) : Parcelable
```

## TODO

  * ~~支持视频预览~~
  * ~~支持拍照功能~~
  * 提供更多样式
