# synchronizer
## wait方法
wait方法使得线程进入waitset中等待唤醒
```cpp
int ObjectSynchronizer::wait(Handle obj, jlong millis, TRAPS) {  
  JavaThread* current = THREAD;  
  if (UseBiasedLocking) {  
    BiasedLocking::revoke(current, obj);  
    assert(!obj->mark().has_bias_pattern(), "biases should be revoked by now");  
  }  
  if (millis < 0) {  
    THROW_MSG_0(vmSymbols::java_lang_IllegalArgumentException(), "timeout value is negative");  
  }  
  // The ObjectMonitor* can't be async deflated because the _waiters  
  // field is incremented before ownership is dropped and decremented  // after ownership is regained.  ObjectMonitor* monitor = inflate(current, obj(), inflate_cause_wait);  
  
  DTRACE_MONITOR_WAIT_PROBE(monitor, obj(), current, millis);  
  monitor->wait(millis, true, THREAD); // Not CHECK as we need following code  
  
  // This dummy call is in place to get around dtrace bug 6254741.  Once  // that's fixed we can uncomment the following line, remove the call  // and change this function back into a "void" func.  // DTRACE_MONITOR_PROBE(waited, monitor, obj(), THREAD);  int ret_code = dtrace_waited_probe(monitor, obj, THREAD);  
  return ret_code;  
}
```