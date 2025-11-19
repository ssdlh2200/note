# objectMonitor.hpp

## monitor作用
```
|---------| 
| Monitor |  
|---------|  
| WaitSet |
|EntryList|  
|  Owner  |  
|---------|
```
当我们使用synchronized上锁之后，并且<font color="#c00000"><b>该锁升级为重量级锁的时候</b></font>
- object header会指向monitor
- owner会指向持有锁的线程
- 其它线程获取lock时,发现已经有指向的monitor,  便去查找owner是否有线程
    - 如果有，则线程进入EntryList等待Owner释放(线程进入EntryList则线程状态显示为BLOCKED)
    - 如果没有，则线程成为新的Owner（失败则继续进入EntryList）
下面打印lock的对象头
```java
public static void main(String[] args) throws InterruptedException {  
    final Object lock = new Object();  
    System.out.println(ClassLayout.parseInstance(lock).toPrintable());  
    new Thread(() -> {  
        synchronized (lock) {  
            System.out.println(ClassLayout.parseInstance(lock).toPrintable()); 
            try {  
                Thread.sleep(2000);  
            } catch (InterruptedException e) {  
                throw new RuntimeException(e);  
            }  
        }  
  
    }).start();  
    new Thread(() -> {  
        synchronized (lock) {  
            System.out.println(ClassLayout.parseInstance(lock).toPrintable());  
        }  
    }).start();  
}
```
运行结果（jdk17废弃偏向锁，所以是non-biasable禁用偏向锁）
```
(object header: mark)     0x0000000000000001 (non-biasable; age: 0)
(object header: mark)     0x000000edae7ff400 (thin lock: 0x000000edae7ff400)
(object header: mark)     0x000002bbd15187a2 (fat lock: 0x000002bbd15187a2)
```

## monitor中wait、notify原理
![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1709733378688-ab94f4bd-7dc4-4594-834d-33061bf67a6b.png)

**synchronized**给对象上锁，当这对象锁升级为**重量级锁**之后，object header中的mark word就会成为指向一个**Monitor**的指针

```java
假设线程A启动,执行到 synchronized 同步代码块时（假设此时锁已经升级为重量级锁）
对象锁的 mark word 会变为如下：
|--------------------------------|------|
|  prt_to_heavyweight_monitor:30 |  10  |
|--------------------------------|------|
Owner ---> A线程

当其它线程同样获取lock对象锁时,发现已经有指向的monitor,
便去查找Owner是否有线程,如果有,则线程进入EntryList等待Owner释放
(线程进入EntryList则线程状态显示为BLOCKED)
```

+ 由上可知Monitor对象中分为中有三个概念：
    1. **WaitSet**：线程等待集合，存放调用锁的wait()进入等待集合的线程或者获取不到锁的线程。
        - 调用wait()，线程进入WAITING状态，此状态下的线程一直等待直到其它线程唤醒。
        - 调用wait(long timeout)，线程进入TIMED-WAITING状态，此状态的线程超时自动进入entryList。
        - 调用notify()时，随机唤起一个waitset中的线程进入entryList
        - 当我们调用notifyAll()时，将唤起waitset中的所有线程进入entryList。
    2. **EntryList**：一个存放等待获取对象锁的线程的列表。
        * EntryList中的线程处于阻塞状态（BLOCKED），EntryList 中的线程竞争获取锁。
    3. **Owner**：当前持有对象锁的线程。