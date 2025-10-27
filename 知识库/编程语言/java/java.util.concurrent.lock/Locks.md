# java.util.concurrent.locks
## LockSupport
### 常用方法
+ Lock.park()

### 原理
#### park()底层原理
+ [https://www.cnblogs.com/yonghengzh/p/14280670.html](https://www.cnblogs.com/yonghengzh/p/14280670.html)

## AQS（AbstractQueuedSynchronizer）
java.util.concurrent 中大部分同步器是基于 aqs 实现的，同步器一般包含两种方法一种是 acquire 和 release

+ acquire 操作阻塞调用的线程，直到或除非同步状态允许其继续运行
+ release 操作通过某种方式改变同步状态，使得一或多个被acquire阻塞的线程继续执行运行

### 参考链接
+ [https://www.bilibili.com/video/BV1yJ411v7er](https://www.bilibili.com/video/BV1yJ411v7er)
+ AQS作者论文
    - [https://www.cnblogs.com/dennyzhangdd/p/7218510.html](https://www.cnblogs.com/dennyzhangdd/p/7218510.html)（中文）
    - [https://gee.cs.oswego.edu/dl/papers/aqs.pdf](https://gee.cs.oswego.edu/dl/papers/aqs.pdf)（英文原版）
## Semaphore（信号量）
用来限制访问共享资源的线程上限，Semaphore 适合一个线程一个资源的场景，例如数据库连接池。

1. **构造方法**
+ `**Semaphore(int permits)**`: 创建一个具有给定许可证数的 Semaphore，这些许可证最初是全部可用的。
+ `**Semaphore(int permits, boolean fair)**`: 创建一个具有给定许可证数 Semaphore，并指定是否应遵循公平性策略。公平性策略指的是在多个线程竞争许可证时，Semaphore 会按线程请求的先后顺序分配许可证。
2. **核心方法**
+ `**void acquire() throws InterruptedException**`**: **从信号量中获取一个许可证，如果没有可用的许可证，它会阻塞直到一个许可证可用。
+ `**void acquire(int permits) throws InterruptedException**`**: **获取指定数量的许可证。
+ `**void release()**`**: **释放一个许可证，将其返回给信号量。
+ `**void release(int permits)**`**: **释放指定数量的许可证，将它们返回给信号量。
+ `**int availablePermits()**`**: **返回当前可用的许可证数量。
+ `**boolean hasQueuedThreads()**`**: **检查是否有线程正在等待获取许可证。
+ `**int getQueueLength()**`**: **返回正在等待获取许可证的线程数目。

### acquire()原理