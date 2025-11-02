- https://juejin.cn/post/6844904038580879367#heading-1)(# 如何优雅的查看 Java 代码的汇编码)
## 1. 首先安装hsdis.dll
![[20251102-14-52-39.png]]
## 2. 验证是否安装成功
- java -XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly -version
![[20251102-16-14-38.png]]
## 3. 添加运行参数
```
-XX:+UnlockDiagnosticVMOptions 
-XX:+PrintAssembly
-XX:+LogCompilation 
-XX:hotspot.log
```

## 4. 配置config
配置生成 `hotspot.log` 日志的 java 文件所在的 `src` 文件目录和 `class` 文件目录
![[20251102-14-30-51.png]]

配置完成之后，点击 Open Log 按钮，选中 hotspot.log 文件，然后点击 Start 按钮，如果配置正确的话，会得到如下结果：
![[20251102-14-31-01.png]]