# list initialization
## 基础
### 分类、特性
c++中列表初始化一共由四类
- direct list-initialization：直接列表初始化
- copy list-initialization：拷贝列表初始化
- value list-initialization：值列表初始化
- list-initialization of aggregates：聚合类型的聚合初始化
特性：
- 列表初始化不会产生窄化转换
### 匹配规则
c++会按照以下顺序选择初始化方法：**等下检测下这个说话是否正确**
1. 检测是否为聚合类型
    - 是：使用聚合初始化
2. 检测是否由可匹配的构造函数
    - 是：使用构造函数初始化
3. 检测是否可以使用`{}`转换
    - 是：使用initializer_list构造函数
### 可初始化的类型
✔ C++ 中 **绝大多数“正常”的类型都能用 `{}` 初始化**
包括内置类型、类、结构体、数组、容器…
❌ 不能用 `{}` 的情况：
1. 窄化转换
2. 构造函数被禁用
3. union 的特殊限制
4. 某些非标准扩展类型
5. 没有匹配的构造函数
### 窄化转换
列表初始化使用花括号`{}`来初始化对象，列表初始化不会产生窄化转换(narrowing)
```cpp
int x = 10.2;
int y = {10.2}; //编译器❌报错
```
## 直接列表初始化
### 语法
| 直接列表初始化                                                                                                                         |
| ------------------------------------------------------------------------------------------------------------------------------- |
| **T object { arg1, arg2, ... };<br>T object {.des1 = arg1 , .des2 { arg2 } ... };** c++20起                                      |
| **T { arg1, arg2, ... }<br>T {.des1 = arg1 , .des2 { arg2 } ... }** c++20起                                                      |
| **new T { arg1, arg2, ... }<br>new T {.des1 = arg1 , .des2 { arg2 } ... }** c++20起                                              |
| **Class { T member { arg1, arg2, ... }; };<br>Class { T member {.des1 = arg1 , .des2 { arg2 } ... }; };** c++20起                |
| **Class::Class() : member { arg1, arg2, ... } {...<br>Class::Class() : member {.des1 = arg1 , .des2 { arg2 } ...} {...** c++20起 |
### 特性
1. 对于普通类，如果类有匹配的构造函数，会直接调用
2. 优先匹配std::initializer_list构造函数
### 示例
```cpp
#include<iostream>  
  
class Foo {  
public:  
    int x;  
    int y;  
};  
class DirectInit {  
public:  
    int a;  
    int b;  
    Foo f1 {70, 80};  
    DirectInit (int a, int b) : a{a}, b{b}{}  
    //当然也可以  
    //✔️DirectInit (int a, int b) : a{70}, b{80}{}  
    //✔️DirectInit (int a, int b) : a(a), b(b){};  
DirectInit foo() {  
    return DirectInit{30, 40};  
}  
  
int main(){  
    DirectInit d1{10, 20};  
    DirectInit d2 = foo();  
    DirectInit* d3 = new DirectInit{50, 60};  
    
    delete d3;  
    return 0;  
}
```

## 拷贝列表初始化
| 拷贝列表初始化                                                                                                                  |
| ------------------------------------------------------------------------------------------------------------------------ |
| **T object = { arg1, arg2, ... };**<br>**T object = {.des1 = arg1 , .des2 { arg2 } ... };** c++20起                       |
| **function({ arg1, arg2, ... })**<br>**function({.des1 = arg1 , .des2 { arg2 } ... })** c++20起                           |
| **return { arg1, arg2, ... };**<br>**return {.des1 = arg1 , .des2 { arg2 } ... };** c++20起                               |
| **object[{ arg1, arg2, ... }]**<br>**object[{.des1 = arg1 , .des2 { arg2 } ... }]** c++20起                               |
| **object = { arg1, arg2, ... }**<br>**object = {.des1 = arg1 , .des2 { arg2 } ... }** c++20起                             |
| **U({ arg1, arg2, ... })**<br>**U({.des1 = arg1 , .des2 { arg2 } ... })** c++20起                                         |
| **Class { T member = { arg1, arg2, ... }; };**<br>**Class { T member = {.des1 = arg1 , .des2 { arg2 } ... }; };** c++20起 |
