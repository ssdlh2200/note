# reference declaration
## 语法
c++声明允许定义具名变量，使其成为引用，也就是**某个已存在对象或函数的别名**
```cpp
& attr(optional) declarator

&& attr(optional) declarator
```
- **左值引用**
```cpp
S& D; //D是一个左值引用，引用的类型由声明说明符序列S决定
```
- **右值引用**
```cpp
S&& D; //D是一个右值引用，引用的类型由声明说明符序列S决定
```

```cpp
声明说明符序列S

int& r;                     // s -> int

const int& r;               // s -> const int

unsigned volatile int& ref; // s -> unsigned volatile int
```
注意点：
1. 引用必须初始化
2. 不能形成引用void
3. 引用类型不能被cv限定
## 左值引用