# LockSupport、AQS、ReentrantLock
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
- 正常park和unpark
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
- 提前unpark和park
```java
public static void main(String[] args) {  
  
    Thread t1 = new Thread(() -> {  
        try {  
            Thread.sleep(3000);  
        } catch (InterruptedException e) {  
            throw new RuntimeException(e);  
        }  
        System.out.println("2. t1 start");  
        LockSupport.park();  
        System.out.println("3. t1 wake up");  
    }, "t1");  
  
    t1.start();  
    System.out.println("1. main thread unpark t1");  
    LockSupport.unpark(t1);  
}
```
运行结果
```text
1. main thread unpark t1
2. t1 start
3. t1 wake up
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
### AQS原理概述
在 Java 中，AQS是一个 **基于队列的锁框架:**
- 获取锁失败的线程，会被 **封装成节点** 加入 **双向 FIFO 队列**
- 队列节点自旋或阻塞等待前驱释放锁
- 支持公平锁和非公平锁、支持可重入锁和条件队列
- AQS整体框架
![[20251116-12-01-24.png]]
- AQS将每条请求共享资源的线程封装成一个节点来实现锁的分配，AQS中的队列是CLH变体的虚拟双向队列
    - 通过使用state成员变量表示同步状态
    - 通过内置双向队列完成资源获取的排队工作
    - 通过CAS完成对state值的修改
![[20251116-12-06-08.png]]

### AQS-Node
#### 数据结构
```java
abstract static class Node {  
    //队列前驱节点
    //通过CAS将当前节点插入到队尾时设置
    volatile Node prev;
    
    //队列后继节点
    //当一个节点可能被唤醒（signallable）时必须为非null
    volatile Node next;
    
    //记录此节点代表的等待线程
    //入队后才必定非null
    //非volatile是因为thread加入后本身不会改变（只读）
    Thread waiter;
    
    //保存节点状态，通常包括
        //是否被取消
        //是否在等待
        //是否需要signal
    volatile int status;
  
    // 原子操作方法   
    final boolean casPrev(Node c, Node v) {  // for cleanQueue  
        return U.weakCompareAndSetReference(this, PREV, c, v);  
    }  
    final boolean casNext(Node c, Node v) {  // for cleanQueue  
        return U.weakCompareAndSetReference(this, NEXT, c, v);  
    }  
    final int getAndUnsetStatus(int v) {     // for signalling  
        return U.getAndBitwiseAndInt(this, STATUS, ~v);  
    }  
    final void setPrevRelaxed(Node p) {      // for off-queue assignment  
        U.putReference(this, PREV, p);  
    }  
    final void setStatusRelaxed(int s) {     // for off-queue assignment  
        U.putInt(this, STATUS, s);  
    }  
    final void clearStatus() {               // for reducing unneeded signals  
        U.putIntOpaque(this, STATUS, 0);  
    }  
  
    //得到status字段在对象内存中的偏移，这是cas原子操作需要的地址
    private static final long STATUS  
        = U.objectFieldOffset(Node.class, "status");  
    private static final long NEXT  
        = U.objectFieldOffset(Node.class, "next");  
    private static final long PREV  
        = U.objectFieldOffset(Node.class, "prev");  
}


//独占模式的队列节点，只有一个线程能够获取锁
static final class ExclusiveNode extends Node { }
//共享模式的队列节点，多个线程可以同时获取锁
static final class SharedNode extends Node { }  
//条件队列节点
static final class ConditionNode extends Node  
    implements ForkJoinPool.ManagedBlocker {  
    ConditionNode nextWaiter;
```
#### status
```java
//表示节点正在等待被某个条件唤醒（unpark），调用LockSupport.park进入等待，节点status会被设置为waiting
//must be 1
static final int WAITING   = 1;

//表示节点取消了等待（超时，线程中断或者显示取消）
//为什么为负数？因为队列扫描中只要status < 0,就可以判断节点为取消
// must be negative  
static final int CANCELLED = 0x80000000;

//
static final int COND      = 2;          // in a condition wait
```
### AQS-state
每个AQS队列中都有一个state字段，用来展示锁的获取情况
```java
private volatile int state;
```
state在不同的子类实现中有不同的含义，以ReentrantLock为例

| ReentrantLock |                 |
| ------------- | --------------- |
| state = 0     | 当前共享资源未被加锁      |
| state = 1     | 当前共享资源被一个线程加锁   |
| state > 1     | 当前共享资源被一个线程多次加锁 |

- **注意：AQS中的state是同步器状态和Node中的status节点状态不同**
下面是访问state的字段的方法
```java
//获取state值
protected final int getState()

//设置state值
protected final void setState(int newState)

//cas方式更新state
protected final boolean compareAndSetState(int expect, int update)
```


### AQS参考链接
- 《The java.util.concurrent Synchronizer Framework》 JUC同步器框架（AQS框架）原文翻译 - 只会一点java - 博客园
    - https://www.cnblogs.com/dennyzhangdd/p/7218510.html（存档）
- 从ReentrantLock的实现看AQS的原理及应用 - 美团技术团队
    - https://tech.meituan.com/2019/12/05/aqs-theory-and-apply.html(存档)
- AQS源码分析(一)AQS类定义与Node数据结构详解
    - https://www.bilibili.com/video/BV1Fd4y1b7Qp




## ReentrantLock（锁）
### 默认锁
ReentrantLock默认使用非公平锁
```java
public ReentrantLock() {  
    sync = new NonfairSync();  
}
```
### NonfairSync-Lock解析
#### fast path或slow path
当我们调用ReentrantLock中的lock方法时会调用到这里
```java
//new ReentrantLock().lock()会调用到这里
public void lock() {  
    sync.lock();
}

//sync.lock()会调用到这里
final void lock() {  
    if (!initialTryLock())  
        acquire(1);  
}

//initialTryLock()会调用这里，具体实现取决于采用公平锁还是非公平锁
abstract boolean initialTryLock();
```
- initialTryLock()：尝试立即获取锁（快速路径）
    - 公平锁获取
    - 非公平锁获取
- acquire(1)：获取失败，则进入 AQS 的“排队阻塞”流程（慢路径）
这就是典型的 fast path + slow path 设计
#### fast path获取锁 - initialTryLock
- 获取锁成功
- 不进入aqs队列
- 只做一次cas或者可重入检查
那我们下面来看一下非公平锁的initialTryLock
```java
//ReentrantLock.class

final boolean initialTryLock() {  
    Thread current = Thread.currentThread();  
    
    //直接尝试修改aqs中state的值
    // 对于reentrantLock来说state的值代表
    // 0:代表未加锁
    // 1:被一个线程加锁
    // >1:被同一个线程多次加锁
    if (compareAndSetState(0, 1)) {
    
        //exclusiveOwnerThread被设置为当前线程
        //只有这个线程可以进行锁重入
        setExclusiveOwnerThread(current);  
        return true;} 
        
    //如果线程持有锁，state != 0,意味着第一个if分支cas失败
    //判断当前获取锁的线程是否为exclusiveThread
    //如果是的话代表那么就重入该锁
    else if (getExclusiveOwnerThread() == current) {  
        //
        int c = getState() + 1;  
        if (c < 0) // overflow  
            throw new Error("Maximum lock count exceeded");  
        setState(c);  
        return true;} 
    
    //cas失败、重入失败，说明锁已经被其他线程持有
    //返回false进入slow path等待锁
    else  
        return false;  
}
```
#### slow path获取锁 - AQS - acquire
- 获取锁失败
- 必须进入aqs队列
- 线程需要park阻塞
```java
//ReentrantLock.class

public final void acquire(int arg) {  
    if (!tryAcquire(arg))  
        acquire(null, arg, false, false, false, 0L);  
}

protected boolean tryAcquire(int arg) {  
    throw new UnsupportedOperationException();  
}
```
NonfairSync中的tryAcquire，再次尝试fast path获取锁
```java
//ReentrantLock.class --> NonfairSync.class
    protected final boolean tryAcquire(int acquires) {  
        if (getState() == 0 && compareAndSetState(0, acquires)) {  
            setExclusiveOwnerThread(Thread.currentThread());  
            return true;  
        }  
        return false;  
    }  
}
```
失败则进入slow path acquire(null, arg, false, false, false, 0L);
```java
//AQS.class

final int acquire(Node node, int arg, boolean shared,  
                  boolean interruptible, boolean timed, long time) {  
    Thread current = Thread.currentThread();  
    byte spins = 0, postSpins = 0;   // retries upon unpark of first thread  
    boolean interrupted = false, first = false;  
    Node pred = null;                // predecessor of node when enqueued  
  
    /*     * Repeatedly:     *  Check if node now first     *    if so, ensure head stable, else ensure valid predecessor     *  if node is first or not yet enqueued, try acquiring     *  else if node not yet created, create it     *  else if not yet enqueued, try once to enqueue     *  else if woken from park, retry (up to postSpins times)     *  else if WAITING status not set, set and retry     *  else park and clear WAITING status, and check cancellation     */  
    for (;;) {  
        if (!first && (pred = (node == null) ? null : node.prev) != null &&  
            !(first = (head == pred))) {  
            if (pred.status < 0) {  
                cleanQueue();           // predecessor cancelled  
                continue;  
            } else if (pred.prev == null) {  
                Thread.onSpinWait();    // ensure serialization  
                continue;  
            }  
        }  
        if (first || pred == null) {  
            boolean acquired;  
            try {  
                if (shared)  
                    acquired = (tryAcquireShared(arg) >= 0);  
                else  
                    acquired = tryAcquire(arg);  
            } catch (Throwable ex) {  
                cancelAcquire(node, interrupted, false);  
                throw ex;  
            }  
            if (acquired) {  
                if (first) {  
                    node.prev = null;  
                    head = node;  
                    pred.next = null;  
                    node.waiter = null;  
                    if (shared)  
                        signalNextIfShared(node);  
                    if (interrupted)  
                        current.interrupt();  
                }  
                return 1;  
            }  
        }  
        if (node == null) {                 // allocate; retry before enqueue  
            if (shared)  
                node = new SharedNode();  
            else  
                node = new ExclusiveNode();  
        } else if (pred == null) {          // try to enqueue  
            node.waiter = current;  
            Node t = tail;  
            node.setPrevRelaxed(t);         // avoid unnecessary fence  
            if (t == null)  
                tryInitializeHead();  
            else if (!casTail(t, node))  
                node.setPrevRelaxed(null);  // back out  
            else  
                t.next = node;  
        } else if (first && spins != 0) {  
            --spins;                        // reduce unfairness on rewaits  
            Thread.onSpinWait();  
        } else if (node.status == 0) {  
            node.status = WAITING;          // enable signal and recheck  
        } else {  
            long nanos;  
            spins = postSpins = (byte)((postSpins << 1) | 1);  
            if (!timed)  
                LockSupport.park(this);  
            else if ((nanos = time - System.nanoTime()) > 0L)  
                LockSupport.parkNanos(this, nanos);  
            else  
                break;  
            node.clearStatus();  
            if ((interrupted |= Thread.interrupted()) && interruptible)  
                break;  
        }  
    }  
    return cancelAcquire(node, interrupted, interruptible);  
}

```

## Semaphore（信号量）
用来限制访问共享资源的线程上限，Semaphore 适合一个线程一个资源的场景，例如数据库连接池。

1. 构造方法
+ Semaphore(int permits): 创建一个具有给定许可证数的 Semaphore，这些许可证最初是全部可用的。
+ `Semaphore(int permits, boolean fair)**`: 创建一个具有给定许可证数 Semaphore，并指定是否应遵循公平性策略。公平性策略指的是在多个线程竞争许可证时，Semaphore 会按线程请求的先后顺序分配许可证。
1. 核心方法
+ `void acquire() throws InterruptedException**`: 从信号量中获取一个许可证，如果没有可用的许可证，它会阻塞直到一个许可证可用。
+ `void acquire(int permits) throws InterruptedException**`: 获取指定数量的许可证。
+ `void release()`: 释放一个许可证，将其返回给信号量。
+ `void release(int permits): 释放指定数量的许可证，将它们返回给信号量。
+ `int availablePermits()`: 返回当前可用的许可证数量。
+ `boolean hasQueuedThreads()`: 检查是否有线程正在等待获取许可证。
+ `int getQueueLength()**`: 返回正在等待获取许可证的线程数目。

### acquire()原理