# 1. 运行前必读

1. 执行 `mvn compile` 编译 classloader 模块
2. 在磁盘上新建 `common-sdk` 文件夹，将 `TestUtils#COMMON_PATH` 常量值修改为 `common-sdk` 目录的路径
3. 将 app-service 模块编译后 target 文件夹下的 `indi` 整个目录拷贝到 `common-sdk` 目录下再运行
