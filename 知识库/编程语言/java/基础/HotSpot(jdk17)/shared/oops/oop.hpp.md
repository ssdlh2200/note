# oop.hpp
## 结构
```scss
oopDesc                   // 所有对象的基类（定义对象头）
 ├── instanceOopDesc       // 普通 Java 对象（例如 new A()）
 ├── arrayOopDesc          // 所有数组的基类
 │     ├── objArrayOopDesc     // 对象数组
 │     └── typeArrayOopDesc    // 基本类型数组
```
oopDesc(ordinary object pointer descriptor)是Java中所有对象的基类，其中每个java对象的内存布局如下所示
```
+-------------------------------------------------------------+
| mark word               | 锁状态、哈希等                      |
+-------------------------------------------------------------+
| klass pointer           | 指向类元数据（Klass* 或 narrowKlass）|
+-------------------------------------------------------------+
| instance fields         | Java 层定义的成员变量                |
+-------------------------------------------------------------+
| padding                 | 内存对齐用                          |
+-------------------------------------------------------------+

```
其中markword和klass pointer组成了对象头，如果是数组还有一个额外字段存储长度
## 源码解析
```cpp
class oopDesc{  
private:  
    volatile markWord _mark;  
  
    union _metadata {  
        Klass* _klass; 
        narrowKlass _compressed_klass; //压缩类指针
    }; _metadata;  
};
```
我们可以看到对象包含一个markWord和一个类指针

