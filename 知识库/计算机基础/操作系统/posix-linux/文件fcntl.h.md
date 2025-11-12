# 文件fcntl.h

## 1.1. open()函数

open() 函数的参数如下：

- pathname: 文件的路径名，可以是相对路径或绝对路径。
- flags: 控制文件打开方式的标志（位掩码）可以是以下标志的组合，使用逻辑“或”操作符（|）组合。常用的标志如下：

- 访问模式标志（三选一）：

- O_RDONLY：只读方式打开文件
- O_WRONLY：只写方式打开文件
- O_RDWR：读写方式打开文件

- 文件创建和状态标志（可以组合）：

- O_CREAT：如果文件不存在，则创建该文件。此标志需要额外的参数 mode（文件权限）
- O_EXCL：与 O_CREAT 一起使用。如果文件已经存在，则返回错误
- O_NOCTTY：如果 pathname 指向终端设备，不要将此设备分配为进程的控制终端
- O_TRUNC：如果文件存在并以写入方式打开，则将文件长度截断为 0
- O_APPEND：以追加模式打开文件。写入时数据会被追加到文件末尾

- 同步与缓存控制标志：

- O_SYNC：每次 write 都等到数据实际写入到物理设备上才返回
- O_DSYNC：与 O_SYNC 类似，但只同步文件数据，不同步元数据（例如修改时间）
- O_RSYNC：与 O_SYNC 类似，但同步 read 操作。

- 直接 I/O 标志：

- O_DIRECT：启用直接 I/O，绕过内核页缓存，直接与设备交互（要求对齐）
- 文件锁标志：
- O_EXLOCK：独占锁（仅 FreeBSD 和 macOS 支持）
- O_SHLOCK：共享锁（仅 FreeBSD 和 macOS 支持）

- 非阻塞与目录控制标志：

- O_NONBLOCK 或 O_NDELAY：非阻塞模式
- O_NOFOLLOW：如果目标文件是符号链接，则不跟随
- O_DIRECTORY：如果 pathname 不是目录，则返回错误

- 临时文件标志：

- O_TMPFILE：创建匿名临时文件（Linux 3.11+ 支持）

- 受控访问标志：

- O_CLOEXEC：在执行 exec 时关闭文件描述符