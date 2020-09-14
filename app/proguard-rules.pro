# 代码混淆压缩比，在0~7之间，默认为5，一般不做修改
-optimizationpasses 5

# 混合时不使用大小写混合，混合后的类名为小写
-dontusemixedcaseclassnames

# 指定不去忽略非公共库的类
-dontskipnonpubliclibraryclasses

# 指定不去忽略非公共库的类成员
-dontskipnonpubliclibraryclassmembers

# 这句话能够使我们的项目混淆后产生映射文件
# 包含有类名->混淆后类名的映射关系
-verbose

# 不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度。
-dontpreverify

# 保留Annotation不混淆 这在JSON实体映射时非常重要，比如fastJson
-keepattributes *Annotation*,InnerClasses

# 避免混淆泛型
-keepattributes Signature

# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

# 指定混淆是采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不做更改
-optimizations !code/simplification/cast,!field/*,!class/merging/*

# 忽略警告
-ignorewarnings

# 设置是否允许改变作用域
-allowaccessmodification

# 把混淆类中的方法名也混淆了
-useuniqueclassmembernames

# apk 包内所有 class 的内部结构
-dump class_files.txt

# 未混淆的类和成员
-printseeds seeds_txt

# 列出从apk中删除的代码
-printusage unused.txt


-keep public class com.ailiwean.module_grayscale.GrayScaleDispatch


# 混淆前后的映射
-printmapping mapping.txt