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

**右值(Rvalue)**
- 通常没有地址，存储在寄存器或者临时内存中
- 不能出现在等号左侧
- 不能取地址&（除非绑定到const左值引用）
- 字面量，表达式的计算结果都是右值
```cpp
int y = 10+5; //10+5是右值

int& ref = 10        //❌普通引用不能绑定右值
const int& ref = 10; //✔️const引用可以绑定右值
```
