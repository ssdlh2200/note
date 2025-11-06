# C
## 基础类型

| 类型                                                               | 存储大小                                                  | 值范围                                                                                           |
| ---------------------------------------------------------------- | ----------------------------------------------------- | --------------------------------------------------------------------------------------------- |
| **char**                                                         | 1 字节                                                  | -128 到 127                                                                                    |
| **unsigned char**                                                | 1 字节                                                  | 0 到 255                                                                                       |
| **signed char**                                                  | 1 字节                                                  | -128 到 127                                                                                    |
| int                                                              | <font style="color:rgb(51, 51, 51);">4 字节（大多数）</font> | <font style="color:rgb(51, 51, 51);">-32,768 到 32,767 或 -2,147,483,648 到 2,147,483,647</font> |
| **<font style="color:rgb(51, 51, 51);">unsigned int</font>**     | <font style="color:rgb(51, 51, 51);">4 字节（大多数）</font> | <font style="color:rgb(51, 51, 51);">0 到 65,535 或 0 到 4,294,967,295</font>                    |
| **<font style="color:rgb(51, 51, 51);">short</font>**            | <font style="color:rgb(51, 51, 51);">2 字节</font>      | <font style="color:rgb(51, 51, 51);">-32,768 到 32,767</font>                                  |
| **<font style="color:rgb(51, 51, 51);">unsigned short</font>**   | <font style="color:rgb(51, 51, 51);">2 字节</font>      | <font style="color:rgb(51, 51, 51);">0 到 65,535</font>                                        |
| **<font style="color:rgb(51, 51, 51);">long（也叫long int）</font>** | <font style="color:rgb(51, 51, 51);">4 字节</font>      | <font style="color:rgb(51, 51, 51);">-9223372036854775808 到 9223372036854775807</font>        |
| **<font style="color:rgb(51, 51, 51);">unsigned long</font>**    | <font style="color:rgb(51, 51, 51);">4 字节</font>      | <font style="color:rgb(51, 51, 51);">0 到 18446744073709551615</font>                          |

> 不同操作系统上的类型可能会有差异，为了得到某个类型或某个变量在特定平台上的准确大小，可以使用sizeof运算符。表达式sizeof(type)得到对象或类型的存储字节大小。下面的实例演示了获取 int 类型的大小：
```c
#include <stdio.h>

int main(void)
{
    printf("int存储大小: %lu 字节", sizeof(int));
    return 0;
}
```

运行结果

```cmake
int存储大小: 4 字节
```

### 浮点类型
### char
#### a. 简介
char 通常用来表示字符，但也可以用来处理较小的整数值

+ `char` 在大多数编译器中默认是有符号类型（`signed char`），范围是 -128 到 127
+ 如果需要表示更大的正整数，可以使用 `unsigned char`，范围是 0 到 255

```c
char          num1 = 0xff;
signed char   num2 = 0xff;
unsigned char num3 = 0xff;

printf("num1:%d\n", num1);
printf("num2:%d\n", num2);
printf("num3:%d\n", num3);
```

运行结果

```cmake
num1:-1
num2:-1
num3:255
```

#### b. char *str 和 char str[]
+ char *str = “hello”表示在动态变量区开辟一个存放指针的存储单元，指针变量名为str，这个str指向常量区的”hello”，因此不能更改这个字符串的值，比如 str[0]='e'这样不行。 
+ char str[] = "hello"   表示在动态变量区开辟一个能连续存放6字节的字符串数组，str是数组名称，其数组内容可以被修改，定义在函数中因为是局部变量不能return

### int
```c
int          num0 = 0xffffffff; //-1补码，1首位为1，剩下位取反
unsigned int num1 = 0xffffffff; //1111 1111 1111 1111 1111 1111 1111 1111
int          num2 = 0x7fffffff; //0111 1111 1111 1111 1111 1111 1111 1111

printf("%d\n", num0);
printf("%u\n", num1);
printf("%d\n", num2);
```

运行结果

```cmake
-1
4294967295
2147483647
```

### short
```c
short          num0 = 0xffff;
unsigned short num1 = 0xffff;

printf("%d\n", num0);
printf("%d\n", num1);
```

运行结果

```cmake
-1
65535
```

### long
```c
// 0x7fffffffffffffff => 0111 1111 1111 1111 1111 1111 1111 1111 1111 1111 1111 1111 1111 1111 1111 1111

long int          num0 = 0x7fffffffffffffff;
long              num1 = 0x7fffffffffffffff;
unsigned long     num2 = 0xffffffffffffffff;
unsigned long int num3 = 0xffffffffffffffff;

printf("%ld\n", num0);
printf("%ld\n", num1);
printf("%lu\n", num2);
printf("%lu\n", num3);
```

运行结果

```cmake
9223372036854775807
9223372036854775807
18446744073709551615
18446744073709551615
```

### 浮点型


<font style="color:rgb(51, 51, 51);">下表列出了关于标准浮点类型的存储大小、值范围和精度的细节：</font>

| <font style="color:rgb(255, 255, 255);">类型</font> | <font style="color:rgb(255, 255, 255);">存储大小</font> | <font style="color:rgb(255, 255, 255);">值范围</font> | <font style="color:rgb(255, 255, 255);">精度</font> |
| --- | --- | --- | --- |
| <font style="color:rgb(51, 51, 51);">float</font> | <font style="color:rgb(51, 51, 51);">4 字节</font> | <font style="color:rgb(51, 51, 51);">1.2E-38 到 3.4E+38</font> | <font style="color:rgb(51, 51, 51);">6 位有效位</font> |
| <font style="color:rgb(51, 51, 51);">double</font> | <font style="color:rgb(51, 51, 51);">8 字节</font> | <font style="color:rgb(51, 51, 51);">2.3E-308 到 1.7E+308</font> | <font style="color:rgb(51, 51, 51);">15 位有效位</font> |
| <font style="color:rgb(51, 51, 51);">long double</font> | <font style="color:rgb(51, 51, 51);">16 字节</font> | <font style="color:rgb(51, 51, 51);">3.4E-4932 到 1.1E+4932</font> | <font style="color:rgb(51, 51, 51);">19 位有效位</font> |


### 数组
#### 数组未初始化
在C语言中，未初始化的数组可能会导致不可预测的行为。这是因为数组中的元素在未初始化时，其值是未定义的，也就是所谓的“垃圾值”。

1. **自动变量**

```cmake
void function() {
    int arr[10];
    // arr的元素值是未定义的
}
```

2. **静态变量**

静态变量（包括全局变量和使用 `static` 关键字声明的局部变量）如果未初始化，则默认初始化为0。对于静态数组，它们的每个元素也会被初始化为0。例如：

```cmake
static int arr[10];
// arr的每个元素默认初始化为0
```

或者

```cmake
int arr[10];
// 如果在函数外部声明，arr的每个元素默认初始化为0
```

3. **动态分配的数组**

使用 `malloc` 或 `calloc` 动态分配的内存，`malloc` 分配的内存未初始化，其内容是未定义的，而 `calloc` 分配的内存会初始化为0。例如：

```cmake
int *arr = (int *)malloc(10 * sizeof(int));
// arr的内容未定义

int *arr2 = (int *)calloc(10, sizeof(int));
// arr2的内容被初始化为0
```

为了避免使用未初始化的数组导致的潜在问题，最好在声明数组时对其进行初始化。例如：

```cmake
int arr[10] = {0};  // 所有元素初始化为0
```

#### char[]
<font style="color:rgb(51, 51, 51);">在C语言中，如果一个字符串数组没有被初始化，它的内容将不会自动设置为零（'\0'），这意味着字符串的末尾将不会被系统识别，这可能会导致一些未预期的行为。</font>

<font style="color:rgb(51, 51, 51);">例如，如果你有一个字符串数组，但没有初始化，它可能包含垃圾值。当你试图打印这个字符串时，它可能会继续读取内存，直到它遇到一个'\0'字符，这可能导致不可预测的行为，包括程序崩溃或读取不应该访问的内存区域。</font>

### 字符串
#### 何时在末尾加 \0
在 C 语言中，编译器在处理字符串常量时会自动在字符串的末尾添加一个空终止符 (`\0`)。

1. **字符串字面量**： 当你使用双引号创建一个字符串字面量时，编译器会在字符串的末尾自动添加一个空终止符。例如：

```cmake
char str[] = "hello";
```

这个定义会创建一个包含 6 个字符的数组：`{'h', 'e', 'l', 'l', 'o', '\0'}`。

2. **字符串初始化**： 当你用一个字符串字面量来初始化一个字符数组时，编译器也会在末尾添加空终止符。例如：

```cmake
char str[4] = "abc";
```

这里，数组 str 的大小是 6，包含了字符串 "abc" 和一个空终止符。

> 这里str数组的长度要大于字符串的长度才会添加，否则不会添加。
>

## 指针
<font style="color:rgb(51, 51, 51);">指针也就是内存地址，指针变量是用来存放内存地址的变量。就像其他变量或常量一样，您必须在使用指针存储其他变量地址之前，对其进行声明。指针变量声明的一般形式为：</font>

```cmake
type *var_name;

int    *ip;    /* 一个整型的指针 */
double *dp;    /* 一个 double 型的指针 */
float  *fp;    /* 一个浮点型的指针 */
char   *ch;    /* 一个字符型的指针 */
struct *sp;	   /* 一个struct型的指针 */

```

```cmake
int main() {
    int i = 10;
    int *p;
    p = &i;

    printf("i 变量的地址： %p\n", p);
    return 0;
}
```

运行结果

```cmake
i 变量的地址： 0x7fff2621324c
```

### NULL 指针
<font style="color:rgb(51, 51, 51);">在变量声明的时候，如果没有确切的地址可以赋值，为指针变量赋一个 NULL 值是一个良好的编程习惯。赋为 NULL 值的指针被称为</font>**<font style="color:rgb(51, 51, 51);">空</font>**<font style="color:rgb(51, 51, 51);">指针。</font>

```cmake
#include <stdio.h>
 
int main ()
{
   int  *ptr = NULL;
 
   printf("ptr 的地址是 %p\n", ptr  );
 
   return 0;
}
```

运行结果

```cmake
ptr 的地址是 0x0
```

<font style="color:rgb(51, 51, 51);">在大多数的操作系统上，程序不允许访问地址为 0 的内存，因为该内存是操作系统保留的。然而，内存地址 0 有特别重要的意义，它表明该指针不指向一个可访问的内存位置。但按照惯例，如果指针包含空值（零值），则假定它不指向任何东西。</font>

<font style="color:rgb(51, 51, 51);">如需检查一个空指针，您可以使用 if 语句，如下所示：</font>

```cmake
if(ptr)     /* 如果 p 非空，则完成 */
if(!ptr)    /* 如果 p 为空，则完成 */
```

<font style="color:rgb(51, 51, 51);">在 C 中，有很多指针相关的概念，这些概念都很简单，但是都很重要。下面列出了 C 程序员必须清楚的一些与指针相关的重要概念：</font>

| <font style="color:rgb(255, 255, 255);">概念</font> | <font style="color:rgb(255, 255, 255);">描述</font> |
| --- | --- |
| [<font style="color:rgb(51, 51, 51);">指针的算术运算</font>](https://www.runoob.com/cprogramming/c-pointer-arithmetic.html) | <font style="color:rgb(51, 51, 51);">可以对指针进行四种算术运算：++、--、+、-</font> |
| [<font style="color:rgb(51, 51, 51);">指针数组</font>](https://www.runoob.com/cprogramming/c-array-of-pointers.html) | <font style="color:rgb(51, 51, 51);">可以定义用来存储指针的数组。</font> |
| [<font style="color:rgb(51, 51, 51);">指向指针的指针</font>](https://www.runoob.com/cprogramming/c-pointer-to-pointer.html) | <font style="color:rgb(51, 51, 51);">C 允许指向指针的指针。</font> |
| [<font style="color:rgb(51, 51, 51);">传递指针给函数</font>](https://www.runoob.com/cprogramming/c-passing-pointers-to-functions.html) | <font style="color:rgb(51, 51, 51);">通过引用或地址传递参数，使传递的参数在调用函数中被改变。</font> |
| [<font style="color:rgb(51, 51, 51);">从函数返回指针</font>](https://www.runoob.com/cprogramming/c-return-pointer-from-functions.html) | <font style="color:rgb(51, 51, 51);">C 允许函数返回指针到局部变量、静态变量和动态内存分配。</font> |


### 指针的算术运算[https://www.runoob.com/cprogramming/c-pointer-arithmetic.html](https://www.runoob.com/cprogramming/c-pointer-arithmetic.html)
#### a. char指针运算
```cmake
char *str0 = "hello world";
char *str1 = str0 + 1;
char *str2 = str1 + 1;

printf("%s\n", str0);
printf("%s\n", str1);
printf("%s\n", str2);

printf("%p\n", str0);
printf("%p\n", str1);
printf("%p\n", str2);
```

运行结果

```cmake
hello world
ello world
llo world
0x55ba2c2ae118
0x55ba2c2ae119
0x55ba2c2ae11a
```

#### b. int 指针运算
```cmake
/* 为了占据连续的16个字节存储 */
int num0 = 0x00000009;
int num1 = 0x00000001;
int num2 = 0x00000002;
int num3 = 0x00000003;

int *p1 = &num0;
int *p2 = p1 + 1; //每次增加4个字节的长度
int *p3 = p2 + 1; //每次增加4个字节的长度
int *p4 = p3 + 1; //每次增加4个字节的长度

printf("p1_num: %d\n", *p1);
printf("p2_num: %d\n", *p2);
printf("p3_num: %d\n", *p3);
printf("p4_num: %d\n", *p4);

printf("p1: %p\n", p1);
printf("p2: %p\n", p2);
printf("p3: %p\n", p3);
printf("p4: %p\n", p4);

printf("num1: %p\n", &num1);
printf("num2: %p\n", &num2);
printf("num3: %p\n", &num3);
```

运行结果

```cmake
p1_num: 9
p2_num: 1
p3_num: 2
p4_num: 3
p1: 0x7ffdc420f758
p2: 0x7ffdc420f75c
p3: 0x7ffdc420f760
p4: 0x7ffdc420f764
num1: 0x7ffdc420f75c
num2: 0x7ffdc420f760
num3: 0x7ffdc420f764
```

### 指针数组[https://www.runoob.com/cprogramming/c-array-of-pointers.html](https://www.runoob.com/cprogramming/c-array-of-pointers.html)
```cmake
int a = 1, b = 2, c = 3;
int *arr[3] = {&a, &b, &c};  // 指针数组

for(int i = 0; i < 3; i++) {
    printf("%d ", *arr[i]);   // 输出每个指针指向的值
}
```

### 指向指针的指针[https://www.runoob.com/cprogramming/c-pointer-to-pointer.html](https://www.runoob.com/cprogramming/c-pointer-to-pointer.html)
### 传递指针给函数[https://www.runoob.com/cprogramming/c-passing-pointers-to-functions.html](https://www.runoob.com/cprogramming/c-passing-pointers-to-functions.html)
### 从函数返回指针[https://www.runoob.com/cprogramming/c-return-pointer-from-functions.html](https://www.runoob.com/cprogramming/c-return-pointer-from-functions.html)
## 关键字
### struct
#### a. 基本用法
struct 关键字用于将不同类型的数据组合在一起

```cmake
#include <stdio.h>

// 定义一个名为 Person 的结构体
struct Person {
    char name[50];
    int age;
    float height;
};

int main() {
    // 声明并初始化一个结构体变量
    struct Person person1;

    // 为结构体成员赋值
    strcpy(person1.name, "John Doe");
    person1.age = 30;
    person1.height = 5.9;

    // 打印结构体成员的值
    printf("Name: %s\n", person1.name);
    printf("Age: %d\n", person1.age);
    printf("Height: %.2f\n", person1.height);

    return 0;
}
```

**声明结构体数组变量**

```cmake
int main(){
    struct Person people[2];
    
    strcpy(people[0].name, "Alice");
    people[0].age = 25;
    people[0].height = 5.5;
    
    strcpy(people[1].name, "Bob");
    people[1].age = 28;
    people[1].height = 6.0;
}
```

**嵌套结构体**

```cmake
struct Address {
    char city[50];
    char state[50];
};

struct Person {
    char name[50];
    int age;
    float height;
    struct Address address;
};

int main(){
    struct Person person2;
    strcpy(person2.address.city, "New York");
    strcpy(person2.address.state, "NY");
}
```

**使用指针访问结构体**

```cmake
int main(){
    struct Person *ptr = &person1;
    printf("Name: %s\n", ptr->name);
    printf("Age: %d\n", ptr->age);
    printf("Height: %.2f\n", ptr->height);
}
```

#### b. 其它用法
```cmake
struct person {
    int age;
} jack;
jack.age = 100;
printf("%d\n", jack.age);
```

```cmake
typedef struct person {
    int age;
} jack;

struct person p1;
jack p2;//等同于 struct person p1
```

### typedef
用于定义类型别名的关键字，通过使用 typedef 可以为已有的类型创建一个新的名字

```cmake
typedef unsigned long ulong;
ulong a = 1000; //相当于 usgined long a = 1000;
```

还可以为结构体，联合体和其它复杂类型定义别名

```cmake
typedef struct{
    int x;
    int y;
} Point;

Point p1; //现在可以使用 Point 来定义变量
```

## 内置运算符
### sizeof()
在C语言中，`sizeof`是一个运算符，用于返回一个数据类型或变量所占用的字节数。它可以用于基本数据类型、结构体、数组和其他类型。

**基本用法**

1. **基本数据类型**:

```cmake
int a;
printf("%zu\n", sizeof(a)); // 返回int类型所占的字节数
```

2. **数据类型**:

```cmake
printf("%zu\n", sizeof(int)); // 返回int类型的字节数
```

3. **数组**:

```cmake
int arr[10];
printf("%zu\n", sizeof(arr)); // 返回整个数组所占的字节数 (10 * sizeof(int))
```

4. **结构体**:

```cmake
struct Point {
    int x;
    int y;
};
printf("%zu\n", sizeof(struct Point)); // 返回结构体所占的字节数
```

**注意事项**

+ `sizeof` 在编译时计算，所以它的结果是一个常量。
+ `sizeof` 可以用于未定义变量（如`sizeof(type)`），但对于已定义变量，必须是实际变量名（如`sizeof(variable)`）。
+ 对于动态分配的内存，`sizeof` 不会返回内存的实际大小，只会返回指针的大小。

## 函数
### 函数定义
在 C/C++ 中函数要先声明才能使用

```cmake
#include <cstdio>

// 函数声明
int max(int a, int b);

int main() {
    printf("%d", max(14, 21)); // 函数调用比较大小
    return 0;
}

// 函数定义
int max(int a, int b) {
    return a > b ? a : b;
}
```

### 函数参数
#### 值传递&引用传递
#### void * & void
**void ***

在 C 语言中，`void *` 是一种通用指针类型，表示它可以指向任何类型的数据。使用 `void *` 作为函数参数时，函数可以接受不同类型的指针，这在编写通用函数时非常有用。为了使用 `void *` 指针指向的数据，通常需要将其转换为具体的类型。

```cmake
#include <stdio.h>

void printValue(void *ptr, char type) {
    if (type == 'i') {
        printf("%d\n", *(int *)ptr);
    } else if (type == 'f') {
        printf("%f\n", *(float *)ptr);
    } else if (type == 'c') {
        printf("%c\n", *(char *)ptr);
    }
}
int main() {
    int i = 10;
    float f = 3.14;
    char c = 0x61;

    printValue(&i, 'i');
    printValue(&f, 'f');
    printValue(&c, 'c');
    return 0;
}
```

运行结果

```cmake
10
3.140000
a
```

**void**

在函数参数中，使用 `void` 表示该函数不接受任何参数。

```cmake
void hello(void) {
    printf("Hello, World!\n");
}
```

## 头文件
头文件通常包含：

1. 函数声明
2. 宏定义
3. 类型定义
4. 结构体，联合体，枚举
5. 其他头文件
6. 条件编译指令

```cmake
// math_utils.h
// 函数声明

int add(int a, int b);

int sub(int a, int b);
```

```cmake
// math_utils.c

#include "math_utils.h"

//实现函数 add
int add(int a, int b){
    return a+b;
}

//实现函数 calculate_area
int sub(int a, int b){
    return a-b;
}
```

## 动态内存分配
### malloc()
```cmake
int *arr = malloc(10 * sizeof(int));

if (NULL == arr) {
    printf("申请内存失败\n");
    return;
}
printf("申请成功后指针地址：%p\n", arr);
for (int i = 0; i < 10; ++i) {
    *(arr + i) = i + 1;
    printf("%d\n", *(arr + i));
}
free(arr);

printf("释放后指针地址：%p\n", arr);

for (int i = 0; i < 10; ++i) {
    printf("%d\n", *(arr + i));
}
```

运行结果

```cmake
申请成功后指针地址：0000027e4c0f69c0
1
2
3
4
5
6
7
8
9
10
释放后指针地址：0000027e4c0f69c0
1276056144
638
1276051792
638
5
6
7
8
9
10
```

### calloc()
会将申请的内存初始化为0

```cmake
int *arr = calloc(10, sizeof(int));

if (NULL == arr) {
    printf("申请内存失败\n");
    return;
}
for (int i = 0; i < 10; ++i) {
    printf("%d:%d\n", i, *(arr + i));
}
free(arr);
```

运行结果

```cmake
0:0
1:0
2:0
3:0
4:0
5:0
6:0
7:0
8:0
9:0
```

### realloc()内存扩容
```cmake
int *arr = calloc(10, sizeof(int));
int *newArr = realloc(arr, 15 * sizeof(int));

if (NULL == arr || NULL == newArr) {
    printf("申请内存失败\n");
    return;
}

for (int i = 0; i < 15; ++i) {
    printf("%d:%d\n", i, *(newArr + i));
}

//free(arr); 已经被释放过了？要不控制台不打印信息
free(newArr);
```

## C 标准函数库
### <font style="color:#A58F04;"><stdio.h></font>
# C编译过程
## GCC编译器
```cmake
# 1. 只预处理
gcc -E main.c -o main.i

# 2. 生成汇编代码
gcc -S main.c -o main.s

# 3. 生成目标文件
gcc -c main.c -o main.o

# 4. 生成可执行文件
gcc main.c -o my_program
```

## 预处理（Preprocessing）
c语言中预处理会处理源代码中#开头的指令，预处理阶段不会有语法检查

c语言提供多种预处理功能，如下：

+ 宏定义（#define）
+ 文件包含（#include）
+ 条件编译（#ifdef、#ifndef、#if、#else、#elif、#endif）
+ 取消宏定义（#undef）
+ 错误指令（#error）
+ 编译器特定指令（#pragma）
+ 行指令（#line）

### #define
`#define` **是文本替换工具**，在编译之前，编译器会先处理所有的 `#define` 指令，将代码中的宏名替换为它所定义的值或表达式，`#define` 常用于定义常量、函数式宏和条件编译等。在编译预处理时，对程序中所有出现的宏名，都用宏定义中的字符串去代换，这称为宏替换或宏展开。

#### 无参宏定义
```c
#define 标识符 字符串
```

```c
#define PI 3.14
int main(void) {
    double radius = 5.0;
    double ared = PI * radius * radius;
    return 0;
}
```

替换后（使用gcc -E main.c -o main.i查看预处理后的文件）

```c
int main(void) {
    double radius = 5.0;
    double ared = 3.14 * radius * radius;
    return 0;
}
```

注意，这种情况下使用const定义常量可能更好，因为const常量有数据类型，而宏常量没有数据类型。编译器可以对前者进行类型安全检查，而对后者只进行简单的字符文本替换，没有类型安全检查，并且在字符替换时可能会产生意料不到的错误。

#### 定义带参数的函数宏
+ <font style="color:rgb(0, 0, 0);background-color:rgb(254, 254, 242);">函数调用在编译后程序运行时进行，并且分配内存。宏替换在编译前进行，不分配内存。</font>
+ <font style="color:rgb(0, 0, 0);background-color:rgb(254, 254, 242);">宏展开不占用运行时间，只占编译时间，函数调用占运行时间(分配内存、保留现场、值传递、返回值)。</font>

```c
#define 宏名（形参表） 字符串
```

调用方式为

```plain
宏名（实参表）;
```

【例1】简单使用

```c
#define SQUARE(x) ((x) * (x))
int main() {
    int result = SQUARE(5);
    return 0;
}
```

替换后

```c
int main() {
    int result = ((5) * (5));
    return 0;
}
```

【例2】我们需要理解宏定义是文本替换这个概念，下面将举一个错误的例子

```c
#define SQUARE(x) (void)((x) * (x))
int main() {
    int result = SQUARE(5);
    return 0;
}
```

我们可以执行gcc -E main.c -o main.i命令进行预处理替换文本

```c
int main() {
    int result = (void)((5) * (5));
    return 0;
}
```

但是这个预处理的结果只会在后续编译阶段将会报错，因为预处理阶段无语法检查

【例3】宏定义时建议所有层次都要加括号

```c
#define SUM(a,b) a+b
int main() {
    int result = SUM(1,2) * 3;
    return 0;
}
```

替换后

```c
int main() {
    int result = 1 +2 * 3;
    return 0;
}
```

我们会发现应该时(1+2)*3，变成了1+2*3

#### 取消宏定义#undef
```c
=#define PI   3.14159
int main(void){
    //……
}
#undef PI
int func(void){
    //……
}
```

表示PI只在main函数中有效，在func中无效

#### ANSI预定义的宏
ANSI C标准定义了五个预定义的宏，对于调试，版本控制，平台适配有作用

```c
#include <stdio.h>
int main() {
    printf("当前文件: %s\n", __FILE__);
    printf("当前行号: %d\n", __LINE__);
    printf("编译日期: %s\n", __DATE__);
    printf("编译时间: %s\n", __TIME__);
    //如果实现是标准的，则宏__STDC__含有十进制常量1，如果它含有任何其它数，则实现是非标准的
    printf("STDC: %d\n", __STDC__);
    return 0;
}
```

运行结果

```plain
当前文件: D:/workspace/c_demo1/src/main.c
当前行号: 4
编译日期: Oct 24 2025
编译时间: 01:16:50
STDC: 1
```

### 条件编译
#### #ifdef
```c
#ifdef 标识符
    程序段1
#else
    程序段2
#endif
```

也可以写为

```c
#ifdef 标识符
    程序段1
#endif
```

这两段程序的意思时如果标识符已经被#define定义过，则编译编译程序段1，否则编译程序段2或者不编译

【例1】

```c
#define SIGNAL OK

int main(){
#ifdef SIGNAL
    printf("ok\n");
#else
    printf("no\n");
#endif
    return 0;
}
```

运行结果

```c
yes
```

【例2】

```c
int main(){
#ifdef SIGNAL
    printf("ok\n");
#else
    printf("no\n");
#endif
    return 0;
}
```

运行结果

```c
no
```

#### ifndef
```c
#ifndef 标识符
    程序段1
#else
    程序段2
#endif
```

如果标识符没有被#define定义过，则编译程序段1，否则编译程序段2。与#ifdef刚好相反

#### #if
```c
#if 常量表达式
    程序段1
#else
    程序段2
#endif
```

如果常量表达式的值为真（非0），则编译程序段1，否则编译程序段2

### #include
`#include`是 C 中一个预处理指令，用于将一个文件的内容包含到另外一个文件中

#### 两种形式的#include
1. 尖括号形式：`#include<filename>`
+ 使用尖括号时，预处理器会在系统默认的标准库路径中查找文件。通常用于包含标准库头文件/

```cmake
#include <stdio.h>  // 引入标准输入输出头文件
#include <stdlib.h> // 引入标准库头文件
```

2. 双引号形式： `#include<filename>`
+ 使用双引号时，预处理器首先在当前源文件所在目录查找文件。如果没有找到，再到标准库路径中查找。通常用于包含自定义头文件

#### 工作原理
`#include`的工作原理也是**<font style="color:#DF2A3F;">文本替换</font>****，**它会将指定文件的内容直接插入到该指令所在的位置，就像将该文件的内容复制粘贴到该位置一样

假设我们有两个文件 **main.cpp** 和 **myheader.h**

```cmake
// myheader.h
void sayHello();
```

```cmake
// main.cpp
#include <iostream>
#include "myheader.h"

void sayHello() {
    std::cout << "Hello, World!" << std::endl;
}

int main() {
    sayHello();
    return 0;
}

```

在预处理阶段，编译器会将 **#include **指令替换为 **myheader.h **文件的内容

预处理后的main.cpp

```cmake
#include <iostream>

void sayHello();

void sayHello() {
    std::cout << "Hello, World!" << std::endl;
}

int main() {
    sayHello();
    return 0;
}

```

#### 避免重复
为了避免头文件被多次包含，可以使用预处理器指令 **#ifndef（if not define） #define #endif，**或者使用 **#pragma once**

```cmake
// myheader.h
#ifndef MYHEADER_H
#define MYHEADER_H

void sayHello();

#endif // MYHEADER_H
```

```cmake
// myheader.h
#pragma once

void sayHello();
```

### 参考文章
+ [https://www.cnblogs.com/clover-toeic/p/3851102.html](https://www.cnblogs.com/clover-toeic/p/3851102.html)(C语言预处理命令详解 - clover_toeic - 博客园)

## 编译（Compilation）
## 汇编（Assembly）
## 链接（Linking）
链接时将多个目标文件（.o）和库文件合成最终的可执行文件

链接分为两种：静态链接和动态链接

+ 静态链接（static linking）
    - 在编译期将.a库中的代码直接打包到可执行文件
    - 不依赖外部库，可独立运行
+ 动态链接（dynamic linking）
    - 在运行期通过动态链接器加载.so/.dll库
    - 程序中只存在函数符号的引用，而非实际代码

### 静态库
以下将制作一个静态库

```c
.
├── main.c
├── math.c
└── math.h
```

其中

```c
// math.h
int add(int a, int b);
```

```c
// math.c
#include "math.h"
int add(int a, int b){
    return a+b;
}
```

```c
// main.c
#include <stdio.h>
#include "math.h"

int main() {
    printf("%d\n", add(1, 2));
    return 0;
}
```

首先我们生成一个 math.o 文件

```c
gcc -c math.c
.
├── main.c
├── math.c
├── math.h
└── math.o
```

然后我们打包刚才生成的math.o文件

```c
ar rcs libmath.a math.o
.
├── libmath.a
├── main.c
├── math.c
├── math.h
└── math.o
```

最后将生成的静态链接库打包进我们的程序

```c
gcc main.c -L. -lmath -o app1
.
├── app1
├── libmath.a
├── main.c
├── math.c
├── math.h
└── math.o
```

app1最终生成的可执行程序

### 动态库
以下将制作一个动态

```c
.
├── main.c
├── math.c
└── math.h
```

其中

```c
// math.h
int add(int a, int b);
```

```c
// math.c
#include "math.h"
int add(int a, int b){
    return a+b;
}
```

```c
// main.c
#include <stdio.h>
#include "math.h"

int main() {
    printf("%d\n", add(1, 2));
    return 0;
}
```

将 math.c 生成为 math.o

```c
gcc -fPIC -c math.c
.
├── main.c
├── math.c
├── math.h
└── math.o
```

将 math.o 打包为动态库

```c
gcc -shared -o libmath.so math.o
.
├── libmath.so
├── main.c
├── math.c
├── math.h
└── math.o
```

生成可执行程序

```c
gcc main.c -L. -lmath -o app2
.
├── app2
├── libmath.so
├── main.c
├── math.c
├── math.h
└── math.o
```

此时执行时会报错

```c
./app2
=error while loading shared libraries: libmath.so: cannot open shared object file: No such file or directory
```

我们将生成的动态链接库添加到环境变量

```c
export LD_LIBRARY_PATH=.
```

再次执行就可以

