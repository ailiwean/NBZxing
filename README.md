#  NBZxing
 一个稳定完善的扫码库，几行代码既可接入，完美适配各种分辨率无拉伸，可插拔式自定义UI 。
  
  
# 亮点 #  
       
 - 基于google-cameraView调整优化，大量机型测试，可稳定流畅启动关闭相机
 - Camera2-Camera1分别实现扫码, 高版本默认走Camera2， 低版本Camera1， Camera2启动失败走Camera1
 - 灰度算法处理， 可应付一些特殊场景二维码并可拓展
 - 自定义探测器支持非白边等异形二维码识别
 - zxing源码修改，彻底解决复杂二维码扫出一堆不相干数字问题  
 - 可能是目前最完善的扫码横竖屏切换，可配置不同布局，可动态切换
     
### 依赖

[![](https://jitpack.io/v/ailiwean/NBZxing.svg)](https://.io/#ailiwean/NBZxing)    [![](https://jitpack.io/v/ailiwean/NBZxing-Scale.svg)](https://jitpack.io/#ailiwean/NBZxing-Scale)   [![](https://travis-ci.com/ailiwean/NBZxing.svg?branch=master)](https://travis-ci.com/ailiwean/NBZxing.svg?branch=master)      [![API](https://img.shields.io/badge/API-16%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=16)





```
	        implementation 'com.github.ailiwean:NBZxing:tag'
		//若需要使用灰度算法增强库，再次添加以下依赖(纯java超轻量，两个同时依赖,包体积只增大约400kb,混淆后仅200k)
		implementation 'com.github.ailiwean:NBZxing-Scale:tag'
```
[NBZxing-Scale](https://github.com/ailiwean/NBZxing-Scale "NBZxing-Scale")

#### 说明
- **仅AndroidX** 不提供support，有需要自己改也没多少。
- **库中已经包含zxing源码无需再次依赖**
   
-------

### WIKI
[如何使用-超简易](https://github.com/ailiwean/NBZxing/wiki)


感谢[@guangming](https://github.com/guangmingxiong9999)提供近百台机型应用测试🙏

#### 下载体验
![在这里插入图片描述](https://qr.api.cli.im/newqr/create?data=http%3A%2F%2Fqr61.cn%2FonpFym%2FqAYr5ST&level=H&transparent=0&bgcolor=%23FFFFFF&forecolor=%234caf50&blockpixel=12&marginblock=2&logourl=https%3A%2F%2Fmhimg.clewm.net%2Fcli%2Fimages%2Fbeautify%2Fnew%2Flogo%2F25.png&size=400&logoshape=ellipse&embed_text_fontfamily=simhei.ttc&foretype=1&eye_use_fore=1&background=images%2Fbackground%2Fbg8.png&wper=0.58&hper=0.58&tper=0.21&lper=0.21&qrcode_eyes=circle_star&outcolor=%23239ae9&incolor=%23239ae9&body_type=17&qr_rotate=0&text=&fontfamily=msyh.ttf&fontsize=30&fontcolor=&logo_pos=0&kid=bizcliim&time=1610537973&key=5a908945a809244763d27948301487b0)

- 手动改文件后缀改为apk

###### 😊 <自从写了这个库后，感觉身体与时间被掏空！！真的是花费了太多精力。如果觉得还凑合不错，强烈请求来上一个star ，开源不易，多多鼓励，感谢！>  😊

----

#### 测试二维码

| 标准反色  | ![82984899-9f981600-a025-11ea-9fe6-ad9fead67afa.png](https://i.loli.net/2021/01/11/cmigtU6xebQW1yK.png)  |
| ------------ | ------------ |
| 彩色  | ![caise.png](https://i.loli.net/2021/01/11/jHDvJePlbtqZ8E2.png)  |
| 暗色  | ![over_dart.png](https://i.loli.net/2021/01/11/fmg7UPHnlk5ZqyQ.png)  |
|  曝光 | ![over_light.png](https://i.loli.net/2021/01/11/3eBboifhlCGQgwH.png) |
|  浅色 | ![test_gray.png](https://i.loli.net/2021/01/11/CAgGc64I7PDkioe.png)  |
|  间断 | ![test_inter.png](https://i.loli.net/2021/01/11/2OlTjvscagUxkbL.png)  |
|  黑边 | ![test2.png](https://i.loli.net/2021/01/11/iMGJaTS6vkb1YWK.png)  |


#### 联系我

`QQ群:  444236054`欢迎进群交流


| 😊  |  😊 |
| ------------ | ------------ |
|  请喝咖啡 | ![pay.png](https://i.loli.net/2021/01/11/P13qOdAlpyivuEs.png)  |











