# Callable、Future、FutureTask
## 1. Callable、FutureTask、Future
### 设计理念
**Callable（接口）**
- Callable 接口相比 Runnable 接口多了返回值和抛出异常
**FutureTask（类）**
- FutureTask 是一个任务容器，用来封装callable和runnable的实现对象，（如果传递Runnable可以设置默认返回结果），将封装好的对象传递给
**Future（接口）**
- Future 接口提供了对FutureTask任务结果的控制，判断是否取消，查询是否完成，获取任务结果
```text
  Callable / Runnable
           │
           │ 封装
           ▼
      +-----------+
      | FutureTask|
      +-----------+
           │
           │ 提交
           ▼
   ┌───────────────────┐
   │ Thread / Executor │
   │       │           │
   └───────────────────┘
           │
           │ 异步执行
           ▼
      +-----------+
      | Future    |
      |  get()    |
      +-----------+
           │
           │ 获取结果
           ▼
     返回值 / 执行完成状态
```
## 2. FutureTask
### 状态
![[20251028-01-49-42.png]]



- ExecutorService
```java
ExecutorService executor = Executors.newFixedThreadPool(1); //创建一个线程
Callable<Integer> task = new Callable<Integer>(){
    @Override
    public Integer call() throws Exception {
        log.info("进行一些计算");
        Thread.sleep(3000);
        return 1000;
    }
};
Future<Integer> future = executor.submit(task);
log.info("阻塞等待获取结果");
Integer result = future.get();
log.info("结果为：{}", result);
```






