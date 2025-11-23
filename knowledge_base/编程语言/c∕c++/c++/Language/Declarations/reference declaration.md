# reference declaration
## 语法
c++声明允许定义具名变量，使其成为引用，也就是**某个已存在对象或函数的别名**

| **& attr(optional) declarator**  | (1) |               |
| -------------------------------- | --- | ------------- |
| **&& attr(optional) declarator** | (2) | (since C++11) |
- 左值引用声明：S& D
    - D是一个左值引用，引用的类型由声明说明符序列S决定
- 右值引用声明：S&& D
    - D是一个右值引用，引用的类型由声明说明符序列S决定
- declarator
    - 不能是引用声明，数组声明，指针声明
- attr
    - 可选属性列表，例如nodiscard
```cpp
声明说明符序列S

int& r;                     // S -> int

const int& r;               // S -> const int

unsigned volatile int& ref; // S -> unsigned volatile int
```

## 引用声明注意点
- 引用必须初始化
```cpp
int& ref;  //❌不能只有声明，必须初始话
```
- 不能声明void的引用类型
```cpp
void& ref; //❌
```
- 引用绑定的对象可以是const或volatile，但引用本身不能是const或volatile
```cpp
int x = 10;  
int& ref1 = x;        //ref1一个int的引用
const int& ref2 = x;  //ref2一个const int的引用

  
int& const r = x;      // ❌ 错误：引用不能 const
int& volatile r2 = x;  // ❌ 错误：引用不能 volatile
int& const volatile r3 = x; // ❌ 错误
```
- 引用不是对象
    1. 不一定占用存储空间
    2. 编译器可能为了实现语义分配空间
- c++不允许把引用在进行一层引用，数组化指针化
    1. 不存在引用的引用
    2. 不存在引用数组
    3. 不存在指向引用的指针
```cpp
int& a[3]; // ❌error
int&* p;   // ❌error
int& &r;   // ❌error
```

## 左值、左值引用
### 左值(Lvalue)
- 具有地址，存储在内存中
- 出现在等号的左侧
- 可以取地址&
- 变量，对象，数组元素都是左值
```cpp
int a = 10;  //a是左值

//反汇编如下

mov DWORD PTR [rbp-0x4],0xa //可以看到变量a是具有地址的
```
### 左值引用
#### 非const左值引用
```cpp
int a = 10;
int &ref = a;

//反汇编如下
mov  DWORD PTR [rbp-0xc],0xa  
lea  rax,[rbp-0xc]              
mov  QWORD PTR [rbp-0x8],rax
```
通过汇编我们会发现ref相当于a的一个别名，都存储着变量a的地址
```cpp
int main(){  
    int a = 10;  
    int& ref = a;  
    cout << &ref << endl;  
    cout << &a << endl;  
    return 0;  
}

//运行结果
0xb695fff7c4
0xb695fff7c4
```
不过在c++语言层面无法得到引用本身的地址，c++编译器当我们使用&ref会自动转为变量a的地址，但我们可以在x64dbg中查看
![[20251123-04-08-19.png]]
我们可以看到rbp-8=0xb695fff7c8，也就是引用的地址中存储的是变量a的地址(小端存储还原后为：)000000b695fff7c4
#### const左值引用
左值引用通常要配合左值使用，但我们可以通过const配合右值使用
```cpp
//const左值引用配合右值使用
const int& ref = 0xcafe;

//反汇编如下：
mov    DWORD PTR [rbp-0xc],0xcafe  
lea    rax,[rbp-0xc]              
mov    QWORD PTR [rbp-0x8],rax
```
## 右值、右值引用
### 右值(Rvalue)
- 通常没有地址，存储在寄存器或者临时内存中
- 不能出现在等号左侧
- 不能取地址&（除非绑定到const左值引用）
- 字面量，表达式的计算结果都是右值
```cpp
int x = 10;   //10是右值
int y = 10+5; //10+5是右值
```
### 右值引用
```cpp
int&& ref1 = 0xcafe;  
const int&& ref2 = 0xcaff; //这种写法很不常见，违背了右值引用的设计理念

//反汇编如下
mov    DWORD PTR [rbp-0x14],0xcafe  
lea    rax,[rbp-0x14]             
mov    QWORD PTR [rbp-0x8],rax    
mov    DWORD PTR [rbp-0x18],0xcaff  
lea    rax,[rbp-0x18]             
mov    QWORD PTR [rbp-0x10],rax
```
✔右值引用本来的用途
- 移动语义（std::move 的底层）
- 完美转发（forward<T&&>）
- 修改临时对象（比如写入 buffer）