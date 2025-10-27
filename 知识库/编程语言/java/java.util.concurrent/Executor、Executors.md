# Executor、Executors

## 1. Executor、ExecutorService
### 设计理念
```scss
Executor (接口)
   ↑
   └── ExecutorService (接口)
           ↑
           └── AbstractExecutorService (抽象类)
                   ↑
                   └── ThreadPoolExecutor（具体实现类）

```
**Executor**
- 定义基本任务执行机制
**ExecutorService** 
+ 任务生命周期管理：单独/批量提交、取消和等待任务
    - submit：提交一个任务，返回结果
        - 可以提交runnable和callable任务
    - invokeAll：提交一组任务，返回全部结果
    - invokeAny：提交一组任务，返回第一个完成的任务结果
    - 取消任务
        * shutdown：开始关闭服务，拒绝新的任务提交，允许已提交的任务执行完毕
        * shutdownNow()：立即关闭服务，尝试停止正在执行的任务，并返回未执行的任务列表
## 2. ThreadPoolExecutor

```java
ThreadFactory threadFactory = r -> new Thread(r, "test-" + r.hashCode() + "-");
int cores = Runtime.getRuntime().availableProcessors() * 2; //CPU核心数*2
ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
        cores,
        cores * 2,
        0,
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(),
        threadFactory,
        new ThreadPoolExecutor.AbortPolicy()
);
threadPoolExecutor.submit(() -> {
    for (int i = 0; i < 100; i++) {
        System.out.println(Thread.currentThread().getName() + i);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

});
```
## 3. Executors工具类
### 创建线程池
EXecutors通过封装new ThreadPoolExecutor提供了下面四种常用的线程池
- **newCachedThreadPool**
	- 可缓存的线程池，线程数可根据需求动态调整
    	- 线程数 > 处理需求，空闲一段时间后回收（默认60s）
    	- 线程数 < 处理需求，创建新线程
- **newFixedThreadPool**
	- 固定大小线程池，超出的线程会在队列种等待
- **newSchedualThreadPool**
	- 周期性线程池，支持定时执行任务
- **newSingleThreadExecutor**
	- 单线程的线程池，保证任务按照顺序执行
