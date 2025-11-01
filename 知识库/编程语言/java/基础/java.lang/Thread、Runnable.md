# Thread、Runnable
## 1. Thread、Runnbale
### 1. 设计理念
```java
public interface Runnable {
	public abstract void run();
}

public class Thread implements Runnable {
}
```

**关注点分离**：Thread 类实现 Runnable 接口，体现了 "关注点分离" 的原则
+ Thread 类负责线程的生命周期管理（如启动、停止等）
+ Runnable 接口负责定义任务的内容，通过实现 Runnable 接口，Thread 类可以更加专注于它的线程控制职责，而无需关心具体的任务逻辑
### 2. 创建线程
+ **方法一：** 继承Thread类重写run()方法（代码采用了匿名内部类写法）
```java
Thread t0 = new Thread("t0"){
    @Override
    public void run() {
        log.info("这是t0线程");
    }
};
t0.start();
```
+ **方法二：** 实现Runnable接口并且实现run()方法（代码采用了匿名内部类写法）
```java
Runnable task = new Runnable(){
    @Override
    public void run() {
        log.info("这是task任务");
    }
};
Thread t1 = new Thread(task, "t1");
t1.start();
```
+ **方法三：** 实现Callable接口并且实现call()方法（代码采用了匿名内部类写法）
```java
Callable<String> callable = new Callable<>() {  
    @Override  
    	public String call() throws Exception {  
        System.out.println("线程执行代码");  
        return "这是call任务返回值";  
    }  
};  
FutureTask<String> task = new FutureTask<>(callable);  
task.run();  
System.out.println(task.get());

```
+ **方法四：** 通过线程池创建线程
``` java
ExecutorService pool = Executors.newFixedThreadPool(2);  
pool.submit(() -> {  
    System.out.println("线程池执行任务");  
});
```
### 3. run与start区别
1. **定义方法关键字不同**
<span>&emsp;&emsp;</span>run方法是Thread类中的一个普通方法，里面可以重写线程中实际运行的代码
<span>&emsp;&emsp;</span>start() 方法中调用start0() 方法，而start0方法用native关键字修饰，用来启动一个新的线程，并在新的线程中调用run方法
2. **直接调用结果不同**
<span>&emsp;&emsp;</span>直接调用run方法，会顺序执行run方法中的内容
<span>&emsp;&emsp;</span>调用start方法，会直接创建一个新的线程，并在新的线程中并行执行run方法中的内容
3. **直接调用状态不同**
<span>&emsp;&emsp;</span>代用start方法启动新的线程时，该线程会进入就绪状态，等待JVM调度它和其它线程执行顺序
<span>&emsp;&emsp;</span>直接调用run方法，会直接在当前线程顺序执行，不会产生新的线程
## 2. Thread
<span>&emsp;&emsp;</span>在JDK1.3之后，商用Java虚拟机普遍开始使用内核线程模型，即1 : 1模型来作为Java线程的实	现。（1:1模型是内核线程(Kernel Level Thread，KLT)是直接由操作系统内核来支持的线程，这种线程由内核来控制切换）
<span>&emsp;&emsp;</span>使用抢占式调度方式的多线程系统，线程的调度由系统分配执行时间，线程的切换由系统决定。这种调度方式下，线程的执行时间可控，不会因单个线程问题导致应用程序堵塞。Java中所使用的线程调度策略就是抢占式，虽然整个调度基于系统来确定，但是可以通过设置优先级的方式给予操作系统一定的建议，总共包含1～10优先级，优先级越高，线程越容易被选择执行。
**线程分为3类**
1. 主线程：JVM虚拟机
2. 用户线程：Java中main函数执行任务
3. 守护线程：Java中GC（垃圾回收器，为主线程服务）
### 线程状态
在Thread.State枚举类中一共有6中线程状态
```java
public enum State {
    NEW,
    RUNNABLE,
    BLOCKED,
    WAITING,
    TIMED_WAITING,
    TERMINATED;
}
```

![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1725391975853-5af26521-8e6d-4ce2-b226-53c22a8a52d8.png)
下面将介绍这6种线程状态
+ **NEW：** 线程刚被创建，但是还没有调用start()方法 
+ **RUNNABLE：** 线程被调用了start()方法后的状态，JAVA 线程把操作系统中的就绪（ready）和运行（running）两种状态统一称为可运行（runnable）
+ **BLOCKED：** 在该BLOCKED状态下，一个线程即将进入一个synchronized块，但当前有另一个线程正在synchronized同一对象的块内运行。然后，第一个线程必须等待第二个线程退出其块，当线程等待获取监视器锁时，它就会进入BLOCKED状态。
<div style="background-color: #ffe4e1; padding: 10px; border-left: 4px solid #f1c40f;">这里BLOCKED状态，测试下来只有线程竞争获取对象锁（监视器monitor锁）的时候,才能让线程进入BLOCKED状态<br/>

- ReentrantLock.lock()让线程进入waiting状态<br/>

- IO阻塞线程仍然是runnable状态👻<br/>

- LockSupport.park()方法，线程在JVM中的状态是WAITING，线程在内核中的状态是Sleep<br/>

<font style="color:#DF2A3F;">只有一种方法让线程进入BLOCKED状态？？？</font><br/>
<font style="color:#DF2A3F;">目前看起来是的，我的结论：Java线程进入BLOCKED状态的唯一方法是获取对象锁未成功时，退出BLOCKED状态的唯一办法就是获取到对象时</font>
</div>
+ **WAITING：** 在该WAITING状态下，一个线程正在等待来自另一个线程的信号，这通常通过调用对象锁的wait()，thread.join()，LockSupport.park()发生。然后该线程将保持此状态，直到另一个线程调用对象锁的notify()或死亡。
+ **TIMED_WAITING：** 超时等待状态，超时时间后自行返回不会像waiting一直等待，一般Thread.sleep()方法就会让当前线程进入timed_waiting状态
+ **TERMINATED：** 终止状态当线程代码运行结束 
下面为观察线程状态的代码
```java
public static void main(String[] args) throws InterruptedException {
    newState();
    runnableState();
    blockedState();
    waitingState();
    timedWaitingState();
    terminatedState();
}
static void newState(){
    Thread t = new Thread(() -> {});
    System.out.println("线程状态："+t.getState());
}
static void runnableState(){
    Thread t = new Thread(() -> {while (true) {}});
    t.start();
    System.out.println("线程状态："+t.getState());
}
static void blockedState() throws InterruptedException {
    Object bolckedLock = new Object();
    Thread t1 = new Thread(() -> {
        synchronized (bolckedLock){
            try {
                Thread.sleep(100000);
            }
            catch (InterruptedException e) {throw new RuntimeException(e);}
        }
    });
    Thread t2 = new Thread(() -> {
        synchronized (bolckedLock){
            //do something
        }
    });
    t1.start();
    t2.start();
    Thread.sleep(500);
    //t1线程持有锁，不释放，t2线程就陷入了BLOCKED状态
    System.out.println("线程状态："+t2.getState());
}
static void waitingState() throws InterruptedException {
    Object waitingLock = new Object();
    Thread t1 = new Thread(() -> {
        synchronized (waitingLock){
            try {
                waitingLock.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    });
    t1.start();
    Thread.sleep(500);
    System.out.println("线程状态："+t1.getState());
}
static void timedWaitingState() throws InterruptedException {
    Thread t1 = new Thread(() -> {
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {throw new RuntimeException(e);}
    });
    t1.start();
    Thread.sleep(500);
    //t1线程等待一段时间后自行返回
    System.out.println("线程状态："+t1.getState());
}
static void terminatedState() throws InterruptedException {
    Thread t = new Thread(() -> {});
    t.start();
    Thread.sleep(500);
    System.out.println("线程状态："+t.getState());
}
```
参考链接
+ [https://blog.csdn.net/x541211190/article/details/109425645](https://blog.csdn.net/x541211190/article/details/109425645)
+ [https://www.cnblogs.com/muzhongjiang/p/15134397.html](https://www.cnblogs.com/muzhongjiang/p/15134397.html)（莫名其妙的，紧跟着标题就无法显示5.2的标题）
### 5.2 常用方法
#### start：启动线程
start()方法用于开启线程，start方法只是让线程进入就绪，里面的代码不一定立刻运行（CPU时间片还没有分配给它），每个线程start()方法只能调用一次，调用了多次会出现 IllegalThreadException
```java
Thread t0 = new Thread(()->{
    log.info("线程t0启动");
},"t0");
t0.start();
```
为什么不能start()多次？
从源码角度分析
+ [https://javabetter.cn/thread/thread-state-and-method.html](https://javabetter.cn/thread/thread-state-and-method.html)（Java线程的6种状态及切换(透彻讲解) _ 二哥的Java进阶之路）
#### Thread.sleep：让当前线程休眠
```java
log.info("线程开始休眠");
new Thread(() -> {
    try {
        Thread.sleep(2000);
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
}, "t1");
Thread.sleep(1000);
log.info("线程休眠结束");
```
#### join：等待线程运行结束
join方法的底层原理是wait方法
```java
//t1线程每间隔0.5s输出1个数，直到5
Thread t1 = new Thread(()->{
    log.info("线程t1启动");
    for (int i = 0; i < 5; i++) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("{}", i);
    }
},"t1");
t1.start();
t1.join(); //传入时间，代表最多等待n秒
log.info("等待t1线程结束！");
```
```java
2024-09-03 18:19:53.271 [t1] [INFO ] - 线程t1启动
2024-09-03 18:19:53.798 [t1] [INFO ] - 0
2024-09-03 18:19:54.303 [t1] [INFO ] - 1
2024-09-03 18:19:54.808 [t1] [INFO ] - 2
2024-09-03 18:19:55.324 [t1] [INFO ] - 3
2024-09-03 18:19:55.825 [t1] [INFO ] - 4
2024-09-03 18:19:55.825 [main] [INFO ] - 等待t1线程结束！
```
#### Thread.yield、Thread.sleep对比
<span>&emsp;&emsp;</span>在 Java 中调用 Thread.yield() 方法几乎体会不到任何变化，因为Java中线程状态将线程的 ready 和 running 两种状态结合在了一起，当我们调用 yield() 方法时，<font color="#c0504d">线程从Runnable(running) --> Runnable(ready)</font>，此时线程控制权仍然处在操作系统当中，我们在 Java 层面无法感知到这个变化
- sleep让线程休眠
- yield让出线程（让出CPU时间片）
其它方面的对比：
1. **线程优先级**
<span>&emsp;&emsp;</span>sleep方法给其它线程运行机会时，不考虑线程优先级，因此低优先级线程有机会运行
<span>&emsp;&emsp;</span>yield方法只会给相同优先级，或者更高优先级线程运行机会
2. **线程状态**
<span>&emsp;&emsp;</span>执行sleep方法，线程进入休眠状态(Runnable(running) --> Timed Waiting --> (Runnable|Blocked))
<span>&emsp;&emsp;</span>执行yield方法，线程进入就绪状态(Runnable(running) --> Runnable(ready))
3. **异常**
<span>&emsp;&emsp;</span>其它线程可以使用interrupt方法打休眠中的线程，这时sleep方法会抛出interruptedException异常,线程进入终止状态
<span>&emsp;&emsp;</span>yield方法不会抛出异常
4. **CPU**
<span>&emsp;&emsp;</span>sleep方法比yield方法（操作系统CPU调度相关）具有更好移植性
<span>&emsp;&emsp;</span>yield更依赖操作系统调用器
5. **改进（没想明白，等待后续）**
<span>&emsp;&emsp;</span>TimeUnit的sleep方法比Thread的sleep方法具有更好可读性
#### 成员方法

| **函数**                                    | **解释**                 |
| ----------------------------------------- | ---------------------- |
| **void setName()**                        | 设置线程名字（构造方法也可以设置名字）    |
| **String getName()**                      | 返回线程名字                 |
| **static native Thread currentThread();** | 获取当前执行的线程              |
| **static native void sleep(long time)**   | 线程休眠指定的时间（单位毫秒）        |
| **void setPriority(int newPriority)**     | 设置线程的优先级（最小1，最大10，默认5） |
| **int getPriority()**                     | 获取线程的优先级               |
| **void setDaemon(boolean on)**            | 设置守护线程                 |
| **native** **void yield()**               | 出让线程/礼让线程              |
| **void join()**                           | 插入线程/插队线程              |

```java
t1.getId();//获得id
t1.getName();//获得名字
t1.setName("t2");//修改名字
t1.getPriority();//获得优先级
t1.setPriority(0);//设置优先级范围（1-10），效果不明显，主要还是由操作系统来决定
t1.isAlive();//当前线程是否存活（还没有运行完毕）
```
### wait/notify、Monitor
#### 简单使用
```java
/*
❌❌❌❌
主线程不算持有lock锁（owner线程不是主线程），所以无法调用notify方法
*/
public static void main(String[] args) throws InterruptedException {
    Object lock = new Object();
    Thread t1 = new Thread(() -> {
        synchronized (lock) {
            try {
                lock.wait(); 
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }, "t1");
    t1.start();
    Thread.sleep(500);
    lock.notify();

}

/*
✔️✔️✔️✔️
主线程持有lock锁（owner线程是主线程），可以调用notify方法
*/
public static void main(String[] args) throws InterruptedException {
    Object lock = new Object();
    Thread t1 = new Thread(() -> {
        synchronized (lock) {
            try {
                log.info("t1进入waiting状态");
                lock.wait();
                log.info("t1结束等待");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }, "t1");
    t1.start();
    Thread.sleep(500);
    synchronized (lock){ // 调用wait()方法时会释放锁，所以我们才能够再次持有锁
        log.info("开始唤醒");
        lock.notify();
        log.info("唤醒结束");

    }
}
```
上面代码中，**<font style="color:#DF2A3F;">线程持有lock锁后（就是monitor中owner为当前线程）</font>**，才能调用wait，notify，notifyAll方法
+ **lock.wait()：**持有锁的线程进入到lock监视器waitSet等待
+ **lock.notify()：**在monitor中waitSet等待的线程中挑一个唤醒
+ **lock.notifuAll()：**waitSet等待的线程全部唤醒
#### notify()唤醒线程
```java
Object lock = new Object();

Thread t0 = new Thread(() -> {
    synchronized (lock) {
        try {
            log.info("t0 线程阻塞，释放锁");
            lock.wait();
            log.info("t0被唤醒");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}, "t0");
Thread t1 = new Thread(() -> {
    synchronized (lock) {
        try {
            log.info("t1 线程阻塞，释放锁");
            lock.wait();
            log.info("t1被唤醒");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}, "t1");
Thread t2 = new Thread(() -> {
    synchronized (lock) {
        try {
            log.info("t2 线程阻塞，释放锁");
            lock.wait();
            log.info("t2被唤醒");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}, "t2");

t0.start();
t1.start();
t2.start();
Thread.sleep(1000);

//让主线程持有锁后才能唤醒waitingSet中的线程
synchronized (lock){
    log.info("唤醒线程");
    lock.notify();
    lock.notify();
}
```
运行结果
```java
2024-09-04 15:20:18.696 [t0] [INFO ] - t0 线程阻塞，释放锁
2024-09-04 15:20:18.700 [t2] [INFO ] - t2 线程阻塞，释放锁
2024-09-04 15:20:18.700 [t1] [INFO ] - t1 线程阻塞，释放锁
2024-09-04 15:20:19.699 [main] [INFO ] - 唤醒线程
2024-09-04 15:20:19.699 [t0] [INFO ] - t0被唤醒
2024-09-04 15:20:19.699 [t2] [INFO ] - t2被唤醒
```
+ notify()，无法唤醒指定线程
+ <font style="color:rgb(34, 34, 34);">唤醒线程的顺序取决于jvm厂商，一般都是顺序唤醒</font>
#### 原理
![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1709733378688-ab94f4bd-7dc4-4594-834d-33061bf67a6b.png)

+ Java对象可以关联一个**Monitor**对象
+ 我们使用**synchronized**给对象上锁，当这对象锁升级为**重量级锁**之后，对象头中的mark word就会成为指向一个**Monitor**的指针

```java
|---------|
| Monitor |
|---------|
| WaitSet | 
|EntryList| 
|  Owner  |
|---------|

假设线程A启动,执行到 synchronized 同步代码块时（假设此时锁已经升级为重量级锁）
对象锁的 mark word 会变为如下：
|---------------------|------------|-----------------------|------|--------------------|
|               prt_to_heavyweight_monitor:30              |  10  | Heavyweight Locked |
|---------------------|------------|-----------------------|------|--------------------|
lock此时会成为指向monitor的地址指针

Owner ---》 A线程

当其它线程同样获取lock对象锁时,发现已经有指向的monitor,
便去查找Owner是否有线程,如果有,则线程进入EntryList等待Owner释放
(线程进入EntryList则线程状态显示为BLOCKED)

monitor锁的实现在JVM内部没有暴露API给Java层面
```

+ 由上可知Monitor对象中分为中有三个概念：
    1. **<font style="color:rgb(13, 13, 13);">WaitSet</font>**：一个线程等待集合，存放因为调用对象锁的wait方法而进入等待的线程。
        1. 如果调用的是无参的**wait()**方法，线程会进入**WAITING**状态，此状态下的线程一直等待直到其它线程唤醒
        2. 如果调用的是传入时间参数的**wait(long timeout)**方法，线程会进入**TIMED-WAITING**状态，此状态的线程超时自动进入entryList。
        3. 当我们调用**notify()**时，将随机唤起一个waitset中的线程进入entryList，当我们调用**<font style="color:rgb(13, 13, 13);">notifyAll()</font>**时，将唤起waitset中的所有线程进入entryList
    2. **<font style="color:rgb(13, 13, 13);">EntryList：</font>**<font style="color:rgb(13, 13, 13);">一个存放等待获取对象锁的线程的列表</font>
        * EntryList<font style="color:rgb(13, 13, 13);"> 中的线程处于阻塞状态（</font>**<font style="color:rgb(13, 13, 13);">BLOCKED</font>**<font style="color:rgb(13, 13, 13);">），EntryList 中的线程竞争获取锁。</font>
    3. **<font style="color:rgb(13, 13, 13);">Owner：</font>**<font style="color:rgb(13, 13, 13);">当前持有对象锁的线程。</font>

#### Thread.sleep() 和 Object#wait() 区别
**行为上区别**
+ Object.wait()之后
    - 把当前线程移到wait set里
    - 释放掉object monitor（释放掉锁）
    - 线程暂停执行, 让出CPU时间片
+ Thread.sleep()之后:
    - 线程暂停执行, 让出CPU时间片，不会有其它操作

### 线程中断/打断机制(已整理😃还有一些些。。。)
#### 中断线程原理方法
Java 中的中断是协作式中断
+ 对于RUNNABLE 或者 BLOCKED 状态下的线程，只会将中断标志位设为true，后续要自行处理
+ 处于 WAITING 或者 TIMED_WAITING 状态的线程，则会抛出 InterruptedException 异常
+ <font style="color:#DF2A3F;">这里结论我目前无法百分百确定，处于BLOCKED状态下的线程，调用interrupted方法只是将中断标志位设置为true，再无其他作用（就是这里，真的BLCKDED状态下的线程再无其他啥变化了？？？）</font>

> <font style="color:rgb(24, 25, 28);">协作式中断：协作式中断不会立即打断正在运行的任务。相反（抢占式中断），它会等待当前任务完成或主动让出执行权后才会进行中断处理。这种机制可以避免在关键时刻被打断，从而减少了复杂性和错误的发生机会。</font>
>

1. **interrupt()**：将线程的中断标志位设置为true
2. **isInterrupted()**：判断当前线程的中断标志位是否为true，不会清除终端标志位
3. **Thread.interrupted()**：判断当前线程的中断标志位是否为true，清除终端标志位，重置为false

如果打断由 Thread.sleep()，Object#wait()，Thread#join() 方法进入 timed_waiting，waiting 状态的线程程

#### 中断 runnable 状态线程
##### a. 中断后退出线程
```java
Thread t0 = new Thread(() -> {
    while (true) {
        Thread thread = Thread.currentThread();
        if (thread.isInterrupted()){
            log.info("是否被打断：{}", thread.isInterrupted());
            log.info("线程被打断了");
            break;
        }
    }
}, "t0");
t0.start();
Thread.sleep(500);
log.info("{}", t0.getState());
t0.interrupt();
Thread.sleep(1000);
log.info("线程是否存活：{}", t0.isAlive());
log.info("是否被打断：{}", t0.isInterrupted());
```

运行结果

```java
2024-09-04 02:00:29.472 [main] [INFO ] - RUNNABLE
2024-09-04 02:00:29.477 [t0] [INFO ] - 是否被打断：true
2024-09-04 02:00:29.478 [t0] [INFO ] - 线程被打断了
2024-09-04 02:00:30.481 [main] [INFO ] - 线程是否存活：false
2024-09-04 02:00:30.481 [main] [INFO ] - 是否被打断：false
```

> 在主线程中再次调用t0.isInterrupted()返回结果为false的原因是：t0线程已经结束，标志位已经重置
>

##### b. 中断后不退出线程
```java
Thread t0 = new Thread(() -> {
    while (true) {
        Thread thread = Thread.currentThread();
        if (thread.isInterrupted()){
            log.info("是否被打断：{}", thread.isInterrupted());
            log.info("线程被打断了");
            break;
        }
    }
    while (true){

    }
}, "t0");
t0.start();
Thread.sleep(500);
log.info("{}", t0.getState());
t0.interrupt();
Thread.sleep(1000);
log.info("线程是否存活：{}", t0.isAlive());
log.info("是否被打断：{}", t0.isInterrupted());
```

```java
2024-09-04 02:09:44.111 [main] [INFO ] - RUNNABLE
2024-09-04 02:09:44.116 [t0] [INFO ] - 是否被打断：true
2024-09-04 02:09:44.117 [t0] [INFO ] - 线程被打断了
2024-09-04 02:09:45.118 [main] [INFO ] - 线程是否存活：true
2024-09-04 02:09:45.118 [main] [INFO ] - 是否被打断：true
```

#### 中断 timed_waiting 状态线程
##### a. 处理完异常退出线程
```java
Thread t0 = new Thread(() -> {
    try {
        Thread.sleep(100000);
    } catch (InterruptedException e) {
        log.error("{}", e.getMessage());
    }
}, "t0");
t0.start();
Thread.sleep(500);
log.info("{}", t0.getState());
t0.interrupt();
Thread.sleep(500);
log.info("线程是否存活：{}", t0.isAlive());
log.info("是否被打断：{}", t0.isInterrupted());
```

运行结果

```java
2024-09-04 02:19:07.333 [main] [INFO ] - TIMED_WAITING
2024-09-04 02:19:07.337 [t0] [ERROR] - sleep interrupted
2024-09-04 02:19:07.847 [main] [INFO ] - 线程是否存活：false
2024-09-04 02:19:07.847 [main] [INFO ] - 是否被打断：false
```

##### b. 处理完异常不退出线程
```java
Thread t0 = new Thread(() -> {
    try {
        Thread.sleep(100000);
    } catch (InterruptedException e) {
        log.error("{}", e.getMessage());
    }
    while (true){

    }
}, "t0");
t0.start();
Thread.sleep(500);
log.info("{}", t0.getState());
t0.interrupt();
Thread.sleep(500);
log.info("线程是否存活：{}", t0.isAlive());
log.info("是否被打断：{}", t0.isInterrupted());
```

```java
2024-09-04 02:20:10.831 [main] [INFO ] - TIMED_WAITING
2024-09-04 02:20:10.834 [t0] [ERROR] - sleep interrupted
2024-09-04 02:20:11.341 [main] [INFO ] - 线程是否存活：true
2024-09-04 02:20:11.341 [main] [INFO ] - 是否被打断：false
```

#### 中断 waiting 状态线程
##### a. 处理完异常退出线程
```java
Object waitingLock = new Object();
Thread t0 = new Thread(() -> {
    synchronized (waitingLock){
        try {
            waitingLock.wait();
        } catch (InterruptedException e) {
            log.info("{}", e.toString());
        }
    }
}, "t0");
t0.start();
Thread.sleep(500);
log.info("{}", t0.getState());
t0.interrupt();
Thread.sleep(1000);
log.info("线程是否存活：{}", t0.isAlive()); 
log.info("是否被打断：{}", t0.isInterrupted());
```

```java
2024-09-04 02:37:49.747 [main] [INFO ] - WAITING
2024-09-04 02:37:49.749 [t0] [INFO ] - java.lang.InterruptedException
2024-09-04 02:37:50.759 [main] [INFO ] - 线程是否存活：false
2024-09-04 02:37:50.759 [main] [INFO ] - 是否被打断：false
```

> 处于waiting状态的线程没有错误信息😧，那为啥 timed_waiting 有 sleep interrupted？？？难道是JDK的BUG？
>

##### b. 处理完异常不退出线程
```java
Object waitingLock = new Object();
Thread t0 = new Thread(() -> {
    synchronized (waitingLock){
        try {
            waitingLock.wait();
        } catch (InterruptedException e) {
            log.info("{}", e.toString());
        }
        while (true){

        }
    }
}, "t0");
t0.start();
Thread.sleep(500);
log.info("{}", t0.getState());
t0.interrupt();
Thread.sleep(1000);
log.info("线程是否存活：{}", t0.isAlive());
log.info("是否被打断：{}", t0.isInterrupted());
```

运行结果

```java
2024-09-04 03:02:58.612 [main] [INFO ] - WAITING
2024-09-04 03:02:58.618 [t0] [INFO ] - java.lang.InterruptedException
2024-09-04 03:02:59.619 [main] [INFO ] - 线程是否存活：true
2024-09-04 03:02:59.619 [main] [INFO ] - 是否被打断：false
```

#### 中断 blocked 状态线程
```java
Object blockedLock = new Object();
Thread t0 = new Thread(() -> {
    synchronized (blockedLock){
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}, "t0");
Thread t1 = new Thread(() -> {
    synchronized (blockedLock) {
    }
}, "t1");
t0.start();
t1.start();
Thread.sleep(500);

log.info("打断前：{}", t1.getState());
t1.interrupt();
Thread.sleep(1000);
log.info("打断后：{}", t1.getState());

log.info("线程是否存活：{}", t1.isAlive());
log.info("是否被打断：{}", t1.isInterrupted());

```
运行结果
```java
2024-09-04 14:44:38.229 [main] [INFO ] - 打断前：BLOCKED
2024-09-04 14:44:39.236 [main] [INFO ] - 打断后：BLOCKED
2024-09-04 14:44:39.236 [main] [INFO ] - 线程是否存活：true
2024-09-04 14:44:39.236 [main] [INFO ] - 是否被打断：true
```

处于BLOCKED状态的线程无法被打断，意味着当我们的先要获取synchronized锁时无法被打断
> 这是否说明处于waitSet集合中的Java线程无法被中断（换到其它状态）????
> 这也说明线程状态要想从BLOCKED状态转移到其它状态，只能通过获取锁？？？
## 3. Spring中的线程
Spring中的Bean是线程安全的吗？
spring中Bean是否线程安全，取决于Bean的状态和Bean是单例还是多例。
1. **有状态的单例Bean是线程不安全**的，因为多个线程可以同时访问和修改成员变量
2. **无状态和多例Bean是线程安全的**
+ 无状态Bean，多线程操作中只会对Bean的成员变量进行查询操作，不会修改成员变量的值，因此无状态的单例Bean不会存在线程安全问题
+ 有状态Bean，多线程操作中如果需要对Bean中的成员变量进行数据更新的操作可能会出现线程安全问题。
+ Bean声明为多例，那么是每一个线程独享一个Bean，不会存在线程安全的问题


### 参考链接
+ [https://davyjones2010.github.io/2023-02-25-java-thread-sync-deep-dive/](https://davyjones2010.github.io/2023-02-25-java-thread-sync-deep-dive/)
+ [https://javabetter.cn/thread/thread-state-and-method.html](https://javabetter.cn/thread/thread-state-and-method.html)