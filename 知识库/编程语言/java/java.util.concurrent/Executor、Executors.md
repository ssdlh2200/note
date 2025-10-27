# Executor、Executor

## 1. Executor

### 简介
```java
public interface Executor {

    /**
     * 在未来的某个时间执行给定的命令。该命令
     * 可能会在一个新线程中、在一个池线程中，或在调用线程中执行，具体取决于 {@code Executor} 实现。
     *
     * @param command 要执行的可运行任务
     * @throws RejectedExecutionException 如果该任务无法被接受执行
     * @throws NullPointerException 如果命令为 null
     */
    void execute(Runnable command);
}
```
### ExecutorService
```java
public interface ExecutorService extends Executor {
    ...
    ...
    ...
}
```

ExecutorService 提供了如下行为
+ 任务管理：提供了更高级的任务管理功能，允许用户提交、取消和跟踪任务的执行状态，使异步编程更为便捷
    - **提交一个任务，通过Future等待任务结果返回值**
        *   	Future\<?\> submit (Runnable task)
        * \<T\> Future\<T\> submit (Runnable task, T result)
        * `<T> Future<T> submit (Callable<T> task)`
    - **提交一组任务，等待全部完成，返回全部结果**
        * ****`<T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)`
        *  `<T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)`
    - **提交一组任务并返回第一个完成的任务的结果**
        * `<T> T invokeAny(Collection<? extends Callable<T>> tasks)`
        * `<T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)`
    - **取消任务**
        * `void shutdown()`开始关闭服务，拒绝新的任务提交，但允许已提交的任务执行完毕
        * `List<Runnable> shutdownNow()`立即关闭服务，尝试停止正在执行的任务，并返回未执行的任务列表

### AbstractExecutorService
#### ThreadPoolExecutor
![[20251026-11-28-23.png]]
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
## 2.Executors
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
