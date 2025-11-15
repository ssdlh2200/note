# LockSupport、AQS
## LockSupport（线程阻塞、唤醒）
### 常用方法
相比wait、notify，LockSupport中的park、unpark方法优点如下：
- 无需依赖synchronized内置锁
- 能够唤醒指定线程
- 避免死锁问题
    - park/unpark不使用锁机制，不会产生互相占有锁导致的死锁
    - unpark信号不会丢失，可以发生在park之前
        - unpark会将permit置为1，而park时发现permit为1直接返回

| 方法                                            | 描述                          |
| --------------------------------------------- | --------------------------- |
| void park()                                   | 阻塞当前线程，直到被唤醒                |
| void park(Object blocker)                     | 阻塞当前线程，并关联一个阻塞对象            |
| void parkNanos(long nanos)                    | 阻塞当前线程，最多等待指定纳秒时间           |
| void parkNanos(Object blocker, long nanos)    | 阻塞当前线程，并关联一个阻塞对象，最多等待指定纳秒时间 |
| void parkUntil(long deadline)                 | 阻塞当前线程，直到指定的绝对时间点           |
| void parkUntil(Object blocker, long deadline) | 阻塞当前线程，并关联一个阻塞对象，直到指定的绝对时间点 |
| void unpark(Thread thread)                    | 唤醒指定的线程                     |
| Object getBlocker(Thread thread)              | 获取指定线程关联的阻塞对象               |

### park、unpark
```java
public static void main(String[] args) throws InterruptedException {  
    Thread t1 = new Thread(() -> {  
        System.out.println("thread1 park...");  
        LockSupport.park();  
        System.out.println("threa1 unpark");  
    });  
  
    t1.start();  
    Thread.sleep(1000);  
    System.out.println("t1 state: " + t1.getState());  
    LockSupport.unpark(t1);  
}
```
运行结果
```text
thread1 park...
t1 state: WAITING
threa1 unpark
```
### park(lock)
通过pack(lock)能够通过jstack让我们方便得知线程正在等待哪个锁
```java
public static void main(String[] args) throws InterruptedException {  
    Object lock = new Object();  
    Thread t1 = new Thread(() -> {  
        System.out.println("thread1 park...");  
        LockSupport.park(lock);  
    }, "t1");  
    Thread t2 = new Thread(() -> {  
        System.out.println("thread2 park...");  
        LockSupport.park();  
    }, "t2");  
    t1.start();  
    t2.start();  
    Thread.sleep(1000);  
    System.out.println(ProcessHandle.current().pid());  
    Thread.sleep(100000);  
}
```
![[20251113-00-28-30.png]]
### park/unpark底层原理-link
[[unsafe.cpp#原理总结]]

## AQS
java.util.concurrent 中大部分同步器是基于 aqs 实现的，同步器一般包含两种方法一种是 acquire 和 release

+ acquire 操作阻塞调用的线程，直到或除非同步状态允许其继续运行
+ release 操作通过某种方式改变同步状态，使得一或多个被acquire阻塞的线程继续执行运行

### 参考链接
- AQS作者论文中文
    - https://www.cnblogs.com/dennyzhangdd/p/7218510.html（存档）

## ReentrantLock（锁）
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