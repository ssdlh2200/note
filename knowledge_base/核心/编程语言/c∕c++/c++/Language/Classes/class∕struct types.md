# class∕struct types

## 语法
语法一：
```scss
class-key 
attr ﻿(optional) 
class-head-name 
class-property-specs ﻿(optional) 
base-clause ﻿(optional)
{ member-specification }
```
语法二：
```scss
class-key
attr ﻿(optional) 
base-clause ﻿(optional)
{ member-specification }
```

- **class-key：** 由class、struct、union三个组成，其中**class和struct几乎是完全相同的**，唯一区别在于默认成员访问权限、默认基类继承权限。如果使用union，那么声明的是一个联合体类型
    - class默认成员是private，struct默认成员是public
    - class默认private继承，struct默认public继承
- **attr：** 自c++11起可用，例如alignas指定对其要求
- **class-head-name：** 类名（可以带命名空间限定符例如：MyNamespace::MyClass）
- **class-property-specs：** c++11起引入的一组修饰符，每个最多使用一次
    - final(c++11)：该类不能被继承
    - trivially_relocatable_if_eligible(c++26)：如果符合条件，标记类为可平凡移动
    - replaceable_if_eligible(c++26)：如果符合条件，标记类可替换
- **baes-clause：** 基类列表，指定继承的类型及其访问控制（public Base，private Base）
- **member-specification：** 类的主体，包含成员变量，成员函数，访问说明符
## 前向声明Forward declaration
### 前向声明的使用
```scss
//语法
class-key attr identifier;
```
在当前作用域声明一个类，但是没有具体定义，此类就是前向声明。前向声明的类是不完整类型（incomplete type），对于前向声明的类
- 可以声明指针或引用
- 不能声明对象（因为编译器无法得知大小）
完整定义与前向声明的不同
- **完整定义：** 类的所有成员，函数，大小都已知，可以创建对象，访问成员
- **前向声明：** 只告诉编译器则是一个类或者结构体，但是没有定义，不能创建对象，只能使用指针或者引用
```cpp
class B;

class A {
    B* b; //可以使用B*或B&
          //B b2❌错误，不能声明对象
}

class B {
          //A此时已经是完整定义
    A a;  //B中可以直接包含A
}
```
如果某个源文件只是使用类的指针或引用，那么前向声明可以
- 避免包含完整的头文件，减少编译依赖
- 如果多个文件都包含了同样的头文件，每次编译都会重复处理，导致编译变慢
- 过多依赖还可能引起循环依赖问题（两个类互相包含对方的头文件）
```cpp
// MyStruct.h
#include <iosfwd> // 轻量级前向声明头文件，包含前向声明std::ostream
class Foo {  
public:  
  //函数的定义放在ostream.hpp文件中，此时编译不会将ostream头文件插入到这里
  std::ostream& os;  
};
```
### 局部作用域中的前向声明
如果向前声明出现在局部作用域（函数内部），它会隐藏外层作用域中相同名字的声明
```cpp
struct s { int a; };  
struct s; //无影响，全局作用域内s已定义  
  
void g()  
{  
    struct s; //声明一个新的struct s(隐藏全局s)  
    s* p;     //s是指向局部struct s的指针  
    struct s { char* p; }; //定义局部struct s  
}
```
- 到函数末尾之前，全局的 s 不可见。
- 这说明前向声明在局部作用域中也遵循“名字遮蔽规则”
注意，一个新的类名也可以通过**完整类型说明符（elaborated type specifier）在其他声明中引入，但前提是名字查找找不到之前已声明的同名类**。
```cpp
struct A; // 前向声明

void f() {
    class A* p; // 使用前向声明的 A，不会创建新类
    class B* q; // B 尚未声明，会引入新的类名 B
}

```
## 成员规范Member specification
### 语法
```cpp
attr(optinal) decl-specifier-seq(optional) member-declarator-list(optional);
```
- attr
    - c++11开始可以写任意数量的属性，例如nodiscard
- del-specifier-seq
    - 说明符序列，例如int const，static
- member-declarator-list
    - 初始化声明列表，额外允许（位域，纯虚函数声明，虚函数特性）
### 成员声明
成员声明可以包括，静态和非静态数据成员，成员函数，成员类型定义，成员枚举，嵌套类，友元声明
```cpp
#include <string>  
  
class S  
{  
    int d1; //非静态数据成员  
    int a[10] = {1, 2}; //非静态数据成员带初始化（c++11）  
  
    static const int d2 = 1; //静态数据成员带初始化  
  
    virtual void f1(int) = 0; //纯虚函数  
  
    std::string d3, *d4, f2(int); //两个数据成员 + 一个成员函数  
  
    enum {NORTH, SOUTH, EAST, WEST}; //成员枚举  
  
    struct NestedS  
    {  
        std::string s;  
    }  d5, *d6; //嵌套结构体 + 数据成员  
  
    typedef NestedS value_type, *pointer_type; //类型别名  
};
```
### 成员函数定义（Function definitions）
可以同时声明并定义成员函数或友元函数
- 类体内定义的函数默认是 inline（内联函数），除非附属于命名模块（c++20）
```cpp
#include <iosfwd>  
#include <vector>  
  
  
class M  
{  
    std::size_t C;  
    std::vector<int> data;  
  
public:  
    //构造函数定义  
    M(std::size_t R, std::size_t C) : C(C), data(R*C) {}  
  
    //成员函数定义  
    int operator()(std::size_t  r, std::size_t c) const  
    {  
        return data[r * C + C];  
    }  
  
    //另外一个成员函数定义  
    int& operator()(std::size_t r, std::size_t c)  
    {  
        return data[r * C + C];  
    }  
  
};
```
### ...等待后续学习https://en.cppreference.com/w/cpp/language/class.html
