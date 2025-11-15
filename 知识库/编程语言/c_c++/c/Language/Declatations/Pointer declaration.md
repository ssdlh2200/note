# pointer declaration
指针是一种对象类型，指向一个函数或者另一种类型的对象，也可能不指向任何东西，这种情况由特殊的空指针值（null pointer value）表示
## 语法
```c
* attr-spec-seq ﻿(optional) qualifiers ﻿(optional) declarator
```
- \*：表示这是一个指针
- attr-sepc-seq：对编译器的提示优化或对齐
- qualifiers：限定符，const，volatile
- declarator：指针的变量名，可以是更复杂的形式，int \*\*P
```c
int arr[10] = {0};  
int*p1, *p2, **p3;  
  
p1 = arr;  
p2 = &arr[0];  
p3 = &p2;

```
汇编
```asm
lea    rax,[rbp-0x40]             ;p1 = arr
mov    QWORD PTR [rbp-0x8],rax  
  
lea    rax,[rbp-0x40]             
mov    QWORD PTR [rbp-0x48],rax   ;p2 = &arr[0]

lea    rax,[rbp-0x48]             
mov    QWORD PTR [rbp-0x10],rax   ;p3 = &p2
```
## const pointer、pointer const、pointer const pointer
1. **指向常量的指针**
```c
int a = 10, b = 20;  
const int* c_p = &a;  

//*c_p = 1;  
//错误，指向的值不能修改  
c_p = &b;  
a = 1;
```
- 指向的值不能修改
- 指针本身可以改变（可以指向别的地址）
- 可以通过a本身修改
2. **常量指针**
```c
int a = 10, b = 20;  
int* const c_p = &a;  
  
  
*c_p = 1;  
//c_p = &b;  
//错误，指针本身不能变
```
- 指向的值可以修改
- 指针本身不能改变
3. **指向常量指针的指针**
```c
int a = 10;  
int* const  c_p   = &a;  
int* const* p_c_p = &c_p;
```
## indirection（间接访问）
指针用于**间接访问（indirection）**，这是一种普遍存在的编程技术。  
指针可以用于：
- 实现 **引用传递（pass-by-reference）** 的语义；
- 访问 **动态存储期对象**（如用 `malloc`、`new` 创建的内存）；
- 实现 **可选类型**（例如空指针表示“无值”）；
- 表示 **结构体之间的聚合关系**（一个结构体包含另一个结构体的指针）；
- 实现 **回调函数**（通过函数指针）；
- 实现 **通用接口**（通过 `void*` 指针）；
### Pointers to Objects（对象指针）
一个对象指针可以用取地址符的结果来初始化，这个操作符作用于一个对象类型的表达式（该类型可以是不完整的）
1. 基础类型
```c
int  n;  
int* p_n = &n;  
int* const *p_p_n = &p_n;
```
- p_n指向n
- p_p_n指向一个const指针，且该const指针又指向一个int
2. 数组
```c
int arr[2];  
  
int (*p_arr)[2] = &arr;  
int *p_arr_0 = arr;  
  
//int *p_arr[2] 是包含两个指针的数组，区别非常大
```
- int (\*p_arr)\[2\] 表示指向整个数组的指针
- int \*p_arr_0 表示指向数组第一个元素的指针
    - **指向整个数组的值和指向数组第一个元素的指针**
        - 两个地址值相等
        - 指针+1运算时，p_arr跳过整个数组，p_arr_0跳过一个元素
        - p_arr可以传递整个数组，p_arr_0传递数组第一个元素
- int \*p_arr\[2\] 表示包含两个指针的数组，区别非常大
3. 结构体
```c
struct S  
{  
    int n;  
} s = {1};  
  
int* p_s = &s.n;
```
- p_s指向结构体成员s.n
4. lvalue
指针可以作为间接访问运算符（单目\*）的操作数，该运算符返回一个lvalue，即指向对象本身
```c
int n;
int* p = &n;
*p = 7;
```
指向结构体或者联合体类型对象的指针，也可以作为成员访问运算符 -> 的左操作数
```c
struct Point {int x, y;};
Point pt = {1, 2}
Point* p = &pt;
p -> x = 10; //等价于(*p).x = 10;
```

### ...等待添加https://en.cppreference.com/w/c/language/pointer.html

### Pointer to functions
#### 函数指针定义
**在C语言中，函数名本身代表该函数的地址，比如 f 与 &f 等价**
```c
void f(int);

void (*pf1)(int) = &f;
void (*pf2)(int) = f; // same as &f
```
与函数不同，函数指针时对象，因此可以被存储在数组中、复制、赋值、作为参数传递给其他函数等
```c
void (*arr[3])(int); //函数指针数组
arr[0] = f;
```
函数指针可以像调用普通函数那样出现在函数调用操作符()的左边，从而调用它指向的函数
```c
#include <stdio.h>

int f(int n)
{
    printf("%d\n", n);
    return n * n;
}

int main(void)
{
    int (*p)(int) = f; // 定义并初始化函数指针
    int x = p(7);      // 通过指针调用函数，相当于 f(7)
}

```
对函数指针解引用（\*p）会得到该函数的“函数名称标识符”
```c
int f();
int (*p)() = f;    // 指针 p 指向 f
(*p)(); // 通过函数指针解引用调用 f
p();    // 直接用指针调用 f（等价）
```
(\*p)() 和 p() 都等价于 f()
#### 函数指针比较
函数指针可以用\=\=或者\!\=比较，如果它们指向同一个函数则相等
顶层const对函数参数没有影响（因为传参时可以互换）
int (\*)(int) 和 int (\*)(const int )类型可以互换
```c
int f(int), fc(const int);
int (*pc)(const int) = f; // OK
int (*p)(int) = fc;       // OK
pc = p;                   // OK

```

函数指针在汇编层面就是函数指针保存着函数调用call的地址
- 汇编层面的函数调用：call 0x7ff659d315e0
- 函数指针就是保存着0x7ff659d315e0
### Pointers to void
任何类型对象的指针都可以Implicit conversions（隐式）地转换为 void*
```c
int n = 1;  
int* p = &n;   //int*  隐式转换为 void*
void* pv = p;  //void* 再转换回 int*
int* p2 = pv;

printf("%d\n", *p2); //输出1
```
- void* 是万能指针，可以指向任何类型的数据，但不能直接引用（因为无法得知对象的大小和类型）
- 必须再使用之前强制类型转换
- 不能做指针运算，void没有大小
### Null pointers
1. 每一种指针类型都有一个特殊的取值，称为该类型的空指针值（null pointer value）
2. 一个空指针不指向任何对象或函数，使用\*p会导致未定义的行为
3. 所有相同类型的空指针彼此比较时都相等
4. **要将一个指针初始化为空（null）或赋值为空，可以使用空指针常量（NULL或者值为0的任意整型常量）**
```c
int* p = NULL;     // 用宏 NULL 初始化
int* q = 0;        // 0 也可以代表空指针常量
static char* s;    // 静态变量自动初始化为 NULL

int* p = NULL;
printf("%d", *p);  // ❌ 未定义行为（程序可能崩溃）

```