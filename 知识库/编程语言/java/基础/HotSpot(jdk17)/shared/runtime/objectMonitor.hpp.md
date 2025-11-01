# objectMonitor.hpp


## 简介
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