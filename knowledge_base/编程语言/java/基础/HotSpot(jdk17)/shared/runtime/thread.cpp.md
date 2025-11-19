# thread.cpp

## 线程启动JavaThread::thread_main_inner

设置线程name
```java
this->set_native_thread_name(this->get_thread_name());
```
