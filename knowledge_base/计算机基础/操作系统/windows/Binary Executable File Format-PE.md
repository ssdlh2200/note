# Binary Executable File Foramt-PE
## PE文件格式简介
windows中二进制可执行文件格式采用的是PE，其中PE文件拓展名一般包括(.exe、.dll、.sys)
## PE文件头
### 与PE有关的基本概念
#### 地址
PE中涉及的地址有四类，分别是

| 地址类型                              | 描述                             | 示例字段                                    |
| --------------------------------- | ------------------------------ | --------------------------------------- |
| **File Offset（文件偏移）**             | 文件内部偏移，相对于文件开头的字节位置            | DOS Header、PE Header、Section Table      |
| **RVA（Relative Virtual Address）** | 相对虚拟地址，相对于模块加载基址（ImageBase）的偏移 | Export Table、Import Table、Section Start |
| **VA（Virtual Address）**           | 虚拟地址，模块在内存中的实际地址               | VA = ImageBase + RVA                    |
| **Section Relative Offset**       | 某段内的偏移                         | 用于计算 Section 内具体数据                      |

- 虚拟内存地址
当PE文件（例如exe）文件被操作系统加载进内存后，PE对应的进程支配了自己独立的虚拟空间