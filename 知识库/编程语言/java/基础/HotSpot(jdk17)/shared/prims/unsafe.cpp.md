# unsafe.cpp
## Unsafe_Park
### Unsafe_Park的原函数
```cpp
UNSAFE_ENTRY(
            void, 
            Unsafe_Park(
                        JNIEnv *env, 
                        jobject unsafe, 
                        jboolean isAbsolute, 
                        jlong time))
        {  
            HOTSPOT_THREAD_PARK_BEGIN((uintptr_t)thread->parker(),(int)isAbsolute,time);
            EventThreadPark event;
            JavaThreadParkedState jtps(thread, time != 0);  
            thread->parker()->park(isAbsolute != 0, time);  
            if (event.should_commit()) {  
                const oop obj = thread->current_park_blocker();  
                if (time == 0) {  
                    post_thread_park_event(&event, obj, min_jlong, min_jlong);  
                } else {  
                    if (isAbsolute != 0) {  
                    post_thread_park_event(&event, obj, min_jlong, time);  
                    } else {  
                    post_thread_park_event(&event, obj, time, min_jlong);  
                    }  
                }  
            }  
            HOTSPOT_THREAD_PARK_END((uintptr_t) thread->parker());  
        }
UNSAFE_END
```
### 宏展开
```c
extern "C" 
{  
  static void Unsafe_Park(  
      JNIEnv *env,  
      jobject unsafe,  
      jboolean isAbsolute,  
      jlong time)  
    { 
        //...
        //执行线程状态切换，macos权限切换，debug，栈对齐等等操作
        JavaThread* thread=JavaThread::thread_from_jni_environment(env);  
        MACOS_AARCH64_ONLY(ThreadWXEnable __wx(WXWrite, thread));  
        ThreadInVMfromNative __tiv(thread);  
        debug_only(VMNativeEntryWrapper __vew;)  
        HandleMarkCleaner __hm(thread);  
        JavaThread* THREAD = thread; /* For exception macros. */  
        os::verify_stack_alignment(); 
        //... 
        {  
            //HOTSPOT_THREAD_PARK_BEGIN空宏  
            EventThreadPark event;
            JavaThreadParkedState jtps(thread, time != 0);  
            thread->parker()->park(isAbsolute != 0, time);  
            if (event.should_commit()) 
            {  
                const oop obj = thread->current_park_blocker();  
                if (time == 0) {  
                  post_thread_park_event(&event, obj, min_jlong, min_jlong);  
                } else {  
                  if (isAbsolute != 0) {  
                    post_thread_park_event(&event, obj, min_jlong, time);  
                  } else {  
                    post_thread_park_event(&event, obj, time, min_jlong);  
                  }  
                }  
            } 
            //HOTSPOT_THREAD_PARK_END空宏
        } 
    }  
}

```
### 真正执行park逻辑的代码
```cpp
EventThreadPark event;
JavaThreadParkedState jtps(thread, time != 0);  

//thread->parker()->park 选择不同平台（windows，posix）执行park逻辑
thread->parker()->park(isAbsolute != 0, time);  

if (event.should_commit()) 
{  
    const oop obj = thread->current_park_blocker();  
    if (time == 0) {  
      post_thread_park_event(&event, obj, min_jlong, min_jlong);  
    } else {  
      if (isAbsolute != 0) {  
        post_thread_park_event(&event, obj, min_jlong, time);  
      } else {  
        post_thread_park_event(&event, obj, time, min_jlong);  
      }  
    }  
}
```
#### 不同平台执行park逻辑
##### os::posix
```c
void Parker::park(bool isAbsolute, jlong time) {

//_counter充当permit
//_counter>0 说明被unpark过了，直接消费并且返回
//_co
  if (Atomic::xchg(&_counter, 0) > 0) return;

  JavaThread *jt = JavaThread::current();

//线程被中断返回,不需要等待返回
  if (jt->is_interrupted(false)) {
    return;
  }
  struct timespec absTime;
  if (time < 0 || (isAbsolute && time == 0)) { // don't wait at all
    return;
  }
  if (time > 0) {
    to_abstime(&absTime, time, isAbsolute, false);
  }
  ThreadBlockInVM tbivm(jt);
  
//获取互斥锁（失败说明有人在unpark直接返回）
  if (pthread_mutex_trylock(_mutex) != 0) {
    return;
  }

  int status;
  
//再次检查permit
  if (_counter > 0)  {
    _counter = 0;
    status = pthread_mutex_unlock(_mutex);
    assert_status(status == 0, status, "invariant");
    OrderAccess::fence();
    return;
  }

  OSThreadWaitState osts(jt->osthread(), false /* not Object.wait() */);

//进入阻塞
  assert(_cur_index == -1, "invariant");
  if (time == 0) {
    _cur_index = REL_INDEX; // arbitrary choice when not timed
    status = pthread_cond_wait(&_cond[_cur_index], _mutex);
    assert_status(status == 0 MACOS_ONLY(|| status == ETIMEDOUT),
                  status, "cond_wait");
  }
  else {
    _cur_index = isAbsolute ? ABS_INDEX : REL_INDEX;
    status = pthread_cond_timedwait(&_cond[_cur_index], _mutex, &absTime);
    assert_status(status == 0 || status == ETIMEDOUT,
                  status, "cond_timedwait");
  }
  _cur_index = -1;

  _counter = 0;
  status = pthread_mutex_unlock(_mutex);
  assert_status(status == 0, status, "invariant");
  // Paranoia to ensure our locked and lock-free paths interact
  // correctly with each other and Java-level accesses.
  OrderAccess::fence();
}

void Parker::unpark() {
  int status = pthread_mutex_lock(_mutex);
  assert_status(status == 0, status, "invariant");
  const int s = _counter;
  _counter = 1;
  // must capture correct index before unlocking
  int index = _cur_index;
  status = pthread_mutex_unlock(_mutex);
  assert_status(status == 0, status, "invariant");

  if (s < 1 && index != -1) {
    // thread is definitely parked
    status = pthread_cond_signal(&_cond[index]);
    assert_status(status == 0, status, "invariant");
  }
}
```
### 原理总结
每个线程都会关联一个 Parker 对象，每个 Parker 对象都各自维护了三个角色：计数器、互斥量、条件变量。
park 操作：
1. 获取当前线程关联的 Parker 对象。
2. 将计数器置为 0，同时检查计数器的原值是否为 1，如果是则放弃后续操作。
3. 在互斥量上加锁。
4. 在条件变量上阻塞，同时释放锁并等待被其他线程唤醒，当被唤醒后，将重新获取锁。
5. 当线程恢复至运行状态后，将计数器的值再次置为 0。
6. 释放锁

unpark 操作：
1.  获取目标线程关联的 Parker 对象（注意目标线程不是当前线程）。
2. 在互斥量上加锁。
3. 将计数器置为 1。
4. 唤醒在条件变量上等待着的线程。
5. 释放锁。
## 参考文章
- JVM 源码分析（四）：深入理解 park _ unpark - 张永恒 - 博客园（备份）
    - https://www.cnblogs.com/yonghengzh/p/14280670.html
- 