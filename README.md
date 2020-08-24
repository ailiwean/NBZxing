#  NBZxing
 一个稳定完善的扫码库，几行代码既可接入，完美适配各种分辨率，无拉伸。
  
### 依赖

[![](https://jitpack.io/v/ailiwean/NBZxing.svg)](https://jitpack.io/#ailiwean/NBZxing)    [![](https://jitpack.io/v/ailiwean/NBZxing-Scale.svg)](https://jitpack.io/#ailiwean/NBZxing-Scale)




```
	        implementation 'com.github.ailiwean:NBZxing:0.0.21'
		//若需要使用灰度算法增强库，再次添加以下依赖//纯java超轻量
		implementation 'com.github.ailiwean:NBZxing-Scale:0.0.1'
```
[NBZxing-Scale](https://github.com/ailiwean/NBZxing-Scale "NBZxing-Scale")

**注意：库中已经包含zxing源码无需再次依赖**



两步搞定

 **step1.  自定义一个View继承NBZxingView**
			
```
class CusZxingView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) : NBZxingView(context, attributeSet, def) {
	
    /***
     * 扫码结果回调
     */
    override fun resultBack(content: String) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
    }

  
    /***
     * 返回扫码类型
     * 1 ScanTypeConfig.HIGH_FREQUENCY 高频率格式(默认)
     * 2 ScanTypeConfig.ALL  所有格式
     * 3 ScanTypeConfig.ONLY_QR_CODE 仅QR_CODE格式
     * 4 ScanTypeConfig.TWO_DIMENSION 所有二维码格式
     * 5 ScanTypeConfig.ONE_DIMENSION 所有一维码格式
     */
    override fun getScanType(): ScanTypeConfig {
        return ScanTypeConfig.HIGH_FREQUENCY
    }

}
```

 **step2.  同步生命周期**

在Activity的onCreate方法中调用

```
   this.<CusZxingView>findViewById(R.id.cusZxing)
                .synchLifeStart(this);
```

在Fragment中
```
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        zxingCameraView = new ZxingCameraView(container.getContext()) {
            @Override
            public void resultBack(@NotNull String content) {
                Toast.makeText(container.getContext(), content, Toast.LENGTH_LONG).show();
            }

            @org.jetbrains.annotations.Nullable
            @Override
            public View provideFloorView() {
                return null;
            }
        };
        return zxingCameraView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        zxingCameraView.synchLifeStart(this);
    }
```

**注意：仅支持AndroidX**


#### 图片资源解析
在NBZxingView的子类中调用		
	
```
  	parseFile(filePath: String);
	
	//当图片过大需要手动压缩时，处理完请传入Bitmap
	parseBitmap(bitmap: Bitmap)

```
重写该方法处理回调
```

protected open fun resultBackFile(content: String) {
	//扫描失败content返回空字符串，可自行提示
}

```
-------

#### 下载体验
![在这里插入图片描述](https://imgconvert.csdnimg.cn/aHR0cHM6Ly93d3cucGd5ZXIuY29tL2FwcC9xcmNvZGUvaWlabg?x-oss-process=image/format,png)

- 安装密码1234

###### 😊 <如果觉得还凑合不错，强烈请求来上一个star 。 开源不易，多多鼓励，感谢！>  😊

----

#### 测试二维码

| 标准反色  | ![标准反色](https://github.com/ailiwean/NBZxing/blob/master/qr_test/82984899-9f981600-a025-11ea-9fe6-ad9fead67afa.png "标准反色")  |
| ------------ | ------------ |
| 彩色  | ![彩色](https://github.com/ailiwean/NBZxing/blob/master/qr_test/caise.png "彩色")  |
| 暗色  | ![暗色](https://github.com/ailiwean/NBZxing/blob/master/qr_test/over_dart.png "暗色")  |
|  曝光 |  ![曝光](https://github.com/ailiwean/NBZxing/blob/master/qr_test/over_light.png "曝光") |
|  浅色 | ![浅色](https://github.com/ailiwean/NBZxing/blob/master/qr_test/test_gray.png "浅色")  |

#### 下版预期
-  `提供完全的自定义界面`

-  `二维码生成`

-  `图片识别引用灰度扩展库`

#### 联系我

`微信： 17391961576`   `QQ:  1581209979`


| 😊  |  😊 |
| ------------ | ------------ |
|  请喝咖啡 | ![pay](https://github.com/ailiwean/NBZxing/blob/master/qr_test/pay.png "pay")  |



