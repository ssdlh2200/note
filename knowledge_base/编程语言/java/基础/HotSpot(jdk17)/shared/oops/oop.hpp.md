# oop.hpp
## 内存结构
```scss
oopDesc                   // 所有对象的基类（定义对象头）
 ├── instanceOopDesc       // 普通 Java 对象（例如 new A()）
 ├── arrayOopDesc          // 所有数组的基类
 │     ├── objArrayOopDesc     // 对象数组
 │     └── typeArrayOopDesc    // 基本类型数组
```
oopDesc(ordinary object pointer descriptor)是Java中所有对象的基类，其中每个java对象的内存布局如下所示
```text
//普通对象
+-------------------------+
| mark word               |
+-------------------------+
| klass pointer           |
+-------------------------+
| instance fields         |
+-------------------------+
| padding                 |
+-------------------------+

//数组对象
+-------------------------+
| mark word               |  <- 对象头，存储哈希码、锁信息、GC标记等
+-------------------------+
| klass pointer           |  <- 指向数组类的类型指针
+-------------------------+
| array length            |  <- 数组长度
+-------------------------+
| array elements[0]       |  <- 第一个元素
+-------------------------+
| array elements[1]       |
+-------------------------+
| ...                     |
+-------------------------+
| array elements[n-1]     |
+-------------------------+
| padding                 |  <- 对齐填充 (可选)
+-------------------------+
```
- 普通对象：mark word + klass pointer == 对象头
- 数组对象：mark word + klass pointer + array length  == 对象头
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
我们可以看到oopDesc中包含一个markWord和一个类指针
