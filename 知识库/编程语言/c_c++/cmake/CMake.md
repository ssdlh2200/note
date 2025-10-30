# CMake
## 编译过程
源文件`.c`或者`.cpp`变为可执行程序需要经过下面步骤
1. 预处理
2. 编译
3. 汇编
4. 链接
## 注释
### 行注释
CMake使用#注释，可以放在任何位置

```cmake
# 这是一个CMake注释
```

### 块注释
```cmake
#[[
  这是CMake块注释
  这是CMake块注释
  这是CMake块注释
  ]]
```

## 文件结构
```cmake
cmake_minimum_required(VERSION 3.0)

project(项目名称)

add_executable(可执行程序名称 源文件名称...)
```

+ `cmake_minimum_required`：指定使用的cmake最低版本
+ `project`：定义工程名称，指定工程版本，描述，web主页地址，支持的语言，编译后库的位置
+ `add_executable`：定义工程生成一个可执行程序

## 常用宏

| 宏名称                               | 描述                                    |
| --------------------------------- | ------------------------------------- |
| **CMAKE_SOURCE_DIR**              | **项目根目录的路径**                          |
| CMAKE_BINARY_DIR                  | 二进制文件目录的路径                            |
| CMAKE_CURRENT_SOURCE_DIR          | 当前处理的 CMakeLists.txt 文件所在的目录          |
| CMAKE_CURRENT_BINARY_DIR          | 与 CMAKE_CURRENT_SOURCE_DIR 对应的二进制文件目录 |
| CMAKE_MODULE_PATH                 | 指定查找 CMake 模块的路径                      |
| CMAKE_C_COMPILER                  | C 编译器的路径                              |
| CMAKE_CXX_COMPILER                | C++ 编译器的路径                            |
| CMAKE_BUILD_TYPE                  | 构建类型，例如 Debug 或 Release               |
| CMAKE_INSTALL_PREFIX              | 安装目录的路径                               |
| CMAKE_C_FLAGS                     | C 编译器的编译选项                            |
| CMAKE_CXX_FLAGS                   | C++ 编译器的编译选项                          |
| CMAKE_EXE_LINKER_FLAGS            | 可执行文件链接器的选项                           |
| CMAKE_SHARED_LINKER_FLAGS         | 共享库链接器的选项                             |
| CMAKE_LIBRARY_OUTPUT_DIRECTORY    | 共享库输出目录的路径                            |
| CMAKE_ARCHIVE_OUTPUT_DIRECTORY    | 静态库输出目录的路径                            |
| CMAKE_INSTALL_RPATH               | 安装后运行时库搜索路径                           |
| CMAKE_INSTALL_RPATH_USE_LINK_PATH | 在运行时添加链接器搜索路径                         |
| CMAKE_PREFIX_PATH                 | 查找第三方库的路径                             |


## 命令
### set
#### 定义变量
set命令可以定义变量，使得我们可以随处引用

```cmake
add_executable(test main.c math_utils.c)
```
我们可以将main.c math_utils.c定义为SRC_LIST变量，方便我们后续引用
```cmake
set(SRC_LIST main.c math_utils.c)
add_executable(test ${SRC_LIST})
```

#### 指定c/c++标准
```cmake
set(CMAKE_C_STANDARD 11)
```

#### 指定输出路径
```cmake
set(EXECUTABLE_OUTPUT_PATH 存放可执行程序的文件夹)
```

### 搜索文件

#### aux_source_directory（不会递归搜索）
可以查找某个路径下的所有的源文件（.c或者.cpp）
```cmake
aux_source_directory(<dir> <variable>)
```
+ dir：要搜索的目录
+ variable：将从dir目录下搜索到的源文件存储到该变量
【例子】：将${CMAKE_CURRENT_SOURCE_DIR}目录下的源文件存到变量SRC_LIST中
```cmake
aux_source_directory(${CMAKE_CURRENT_SOURCE_DIR} SRC_LIST)  
add_executable(Algorithm_c__ ${SRC_LIST})
```
#### file（可设置递归搜索）
```cmake
file(GLOB/GLOB_RECURSE 变量名 要搜索的文件路径和文件类型)
```
+ GLOB：将指定目录下搜索到满足条件的所有文件名生产一个列表，并将其存储到变量
+ GLOB_RECURSE：递归搜索指定目录，将搜索到满足条件的所有文件名生产一个列表，并将其存储到变量
```cmake
file(GLOB SRC_LIST ${CMAKE_CURRENT_SOURCE_DIR}/*.c)
```

### 搜索头文件
```cmake
include_directories(头文件所在路径)
```



