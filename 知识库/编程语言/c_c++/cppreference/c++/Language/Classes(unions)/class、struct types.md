# class、struct types

## 语法
```scss
class-key 
attr ﻿(optional) 
class-head-name 
class-property-specs ﻿(optional) 
base-clause ﻿(optional)
{ member-specification }
```
- **class-key：** 由class、struct、union三个组成
    - class <font color="#7030a0">默认成员是private</font>
    - struct 默认成员是public
    - union 表示联合体
- **attr：** 自c++11起可用，例如alignas指定对其要求
- **class-head-name：** 类名（可以带命名空间限定符）
- **class-property-specs：** c++11起引入的一组修饰符，每个最多使用一次
    - final(c++11)：该类不能被继承
    - trivially_relocatable_if_eligible(c++26)：如果符合条件，标记类为可平凡移动
    - replaceable_if_eligible(c++26)：如果符合条件，标记类可替换
- **baes-clause：** 基类列表，指定继承的类型及其访问控制（public Base，private Base）
- **member-specification：** 类的主体，包含成员变量，成员函数，访问说明符
## 前向声明Forward declaration
### 语法
```scss
//语法

class-key attr identifier;
```
在当前作用域声明一个类类型，但不定义具体内容。
在类的定义出现之前，这个类被称为不完全类（incomplete type）
### 互相引用的类
向前声明允许两个类相互引用🔁
```cpp
class Vector; // 前向声明,后面会有一个Vector的类

class Matrix
{
    // ...
    friend Vector operator*(const Matrix&, const Vector&);
};

class Vector
{
    // ...
    friend Vector operator*(const Matrix&, const Vector&);
};
```
### 减少头文件依赖
如果某个源文件只是用类的指针或引用，那么通过向前声明可以避免包含完整的头文件，减少编译依赖
- 在 C++ 中，当你 \#include 一个头文件时，编译器实际上把头文件内容直接插入到当前文件
- 如果头文件很大或包含很多其他头文件，编译器就需要处理更多代码
- 如果多个文件都包含了同样的头文件，每次编译都会重复处理，导致编译变慢
- 过多依赖还可能引起循环依赖问题（两个类互相包含对方的头文件）
**完整定义：** 类的所有成员，函数，大小都已知。可以创建对象，访问成员
**前向声明：** 只告诉编译器则是一个类或者结构体，但是没有定义，不能创建对象，只能使用指针或者引用
```cpp
class MyClass; //前向声明
MyClass* p; //声明指针
MyClass& r; //声明引用
MyClass obj; //错误，编译器不知道大小
```

```cpp
// MyStruct.h
#include <iosfwd> // 包含前向声明std::ostream
struct MyStruct
{
    int value;
    friend std::ostream& operator<<(std::ostream& os, const MyStruct& s);
    // 函数的定义放在 MyStruct.cpp 中，那里才需要 #include <ostream>
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
    s* p; //指向局部struct s的指针  
    struct s { char* p; }; //定义局部struct s  
}
```
1. 外层有一个全局 struct s { int a; };
2. 函数 g() 里再次写 struct s;，这定义了一个新的、局部的 s（隐藏外层的）。
3. 到函数末尾之前，全局的 s 不可见。
4. 这说明前向声明在局部作用域中也遵循“名字遮蔽规则”
- **复杂情况，作为类型说明符的一部分**
```cpp
class U;  
  
namespace ns  
{  
    class Y f(class T p); //声明函数ns::f, 并声明ns::T, ns::Y  
    class U f();  
    //可以使用指针指向T和Y  
    Y* p;  
    T* q;  
}
```
1. 声明了一个函数 f，返回类型为 Y，参数类型为 T。
2. 由于 T 和 Y 在此命名空间中未定义，C++ 自动把它们当作前向声明的类。
3. 第二个函数 class U f(); 中的 U 是在命名空间外定义的全局 U
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
