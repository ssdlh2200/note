# replacing text macros(替换文本宏)

## \#define
`#define` **是文本替换工具**，在编译之前，编译器会先处理所有的 `#define` 指令，将代码中的宏名替换为它所定义的值或表达式，`#define` 常用于定义常量、函数式宏和条件编译等。在编译预处理时，对程序中所有出现的宏名，都用宏定义中的字符串去代换，这称为宏替换或宏展开。

- 宏替换在编译前进行，不分配内存
- 宏展开不占用运行时间，只占编译时间，函数调用占运行时间(分配内存、保留现场、值传递、返回值)
### 语法
| 语法形式                                                                        | 描述           | 备注   |
| :-------------------------------------------------------------------------- | :----------- | :--- |
| \#define identifier replacement-list                                        | 定义对象宏        |      |
| <font color="#ffc000">ex：define PI 3.14                             </font> |              |      |
| \#define identifier( parameters ) replacement-list                          | 定义函数宏        |      |
| <font color="#ffc000">ex：define SQUARE(x) ((x) \* (x))</font>               |              |      |
| \#define identifier( parameters, ... ) replacement-list                     | 定义带可变参数的函数宏  | C99起 |
| \#define identifier( ... ) replacement-list                                 | 定义只带可变参数的函数宏 | C99起 |
| \#undef identifier                                                          | 取消宏定义        |      |

### 对象宏
```c
#define PI 3.14
int main(void) {
    double radius = 5.0;
    double ared = PI * radius * radius;
    return 0;
}
```
宏展开后（使用gcc -E main.c -o main.i查看预处理后的文件）
```c
int main(void) {
    double radius = 5.0;
    double ared = 3.14 * radius * radius;
    return 0;
}
```
<div style="background-color: #ffe4e1; padding: 10px; border-left: 4px solid #f1c40f;color: black" >注意，这种情况下使用const定义常量可能更好，因为const常量有数据类型，而宏常量没有数据类型。编译器可以对前者进行类型安全检查，而对后者只进行简单的字符文本替换，没有类型安全检查，并且在字符替换时可能会产生意料不到的错误。</div>

### 函数式宏
#### 普通函数宏
```c
#define SQUARE(x) ((x) * (x))
int main() {
    int result = SQUARE(5);
    return 0;
}
```
宏展开后
```c
int main() {
    int result = ((5) * (5));
    return 0;
}
```

**【理解1】** 我们需要理解宏定义是文本替换这个概念，下面将举一个错误的例子
```c
#define SQUARE(x) (void)((x) * (x))
int main() {
    int result = SQUARE(5);
    return 0;
}
```
我们可以执行gcc -E main.c -o main.i命令进行预处理替换文本（也就是宏展开）
```c
int main() {
    int result = (void)((5) * (5));
    return 0;
}
```
但是这个预处理的结果只会在后续编译阶段将会报错，因为预处理阶段无语法检查

**【理解2】** 宏定义时建议所有层次都要加括号
```c
#define SUM(a,b) a+b
int main() {
    int result = SUM(1,2) * 3;
    return 0;
}
```
宏展开后
```c
int main() {
    int result = 1 +2 * 3;
    return 0;
}
```
我们会发现应该时(1+2)\*3，变成了1+2\*3

#### 可变参数宏（带普通参数）
\_\_VA\_ARGS\_\_：是编译器固定的标识
\#\#\_\_VA\_ARGS\_\_ 
-  \#\#\_\_VA\_ARGS\_\_ 是 GCC/Clang 扩展，用于在没有可变参数时删除前面的逗号
```c
#define LOG(fmt, ...) printf(fmt, ##__VA_ARGS__)  
int main()  
{  
    LOG("num is %d\n", 10);  
    return 0;  
}
```
不使用\#\#的情况下：
```c
#define LOG(fmt, ...) printf(fmt, __VA_ARGS__)  
int main()  
{  
    LOG("hello world\n"); //❌报错，必须要有可变参数
    return 0;  
}
```
**c23特性**
\_\_VA\_OPT\_\_
- 如果\_\_VA\_ARGS\_\_ 非空，则\_\_VA\_OPT\_\_( content )会被替换成 content
- 如果\_\_VA\_ARGS\_\_ 为空，则\_\_VA\_OPT\_\_( content ) 什么都不会生成
```c
#define F(...) f(0 __VA_OPT__(,) __VA_ARGS__)
F(a, b, c) // replaced by f(0, a, b, c)
F()        // replaced by f(0)
 
#define G(X, ...) f(0, X __VA_OPT__(,) __VA_ARGS__)
G(a, b, c) // replaced by f(0, a, b, c)
G(a, )     // replaced by f(0, a)
G(a)       // replaced by f(0, a)
 
#define SDEF(sname, ...) S sname __VA_OPT__(= { __VA_ARGS__ })
SDEF(foo);       // replaced by S foo;
SDEF(bar, 1, 2); // replaced by S bar = { 1, 2 };
```

#### 可变参数宏（无普通参数）
```c
#define LOG(...) printf(__VA_ARGS__)  
int main()  
{  
    LOG("hello world\n");  
    return 0;  
}
```

### 宏的续行符（多行宏）
```c
#define DEBUG_PRINT(msg) \  
    printf("debug: %s\n", msg);  
    
int main()  
{  
    DEBUG_PRINT("Hello World");  
    return 0;  
}
```

### 宏的运算符\#和\#\#
#### \#
- 当它在替换列表中出现在某个标识符前时，会先对该标识符进行参数替换，然后将结果用引号括起来，形成一个字符串字面量。同时，预处理器会自动：
    - 首尾空白会去掉
    - 中间多个空格压缩成一个空格（字符串字面量内部除外）
    - 字符串中的引号会被自动加 `\` 转义
    - 字符串中的 `\` 会被加倍成 `\\`
这种操作称为 **“字符串化” (stringification)**。
如果字符串化的结果不是一个有效的字符串字面量，则行为未定义。
```c
#define STR(x) #x  
int main()  
{  
    printf(STR( a + b )); // "a + b"  
    return 0;  
}
```
当 `#` 出现在 \_\_VA_ARGS\_\_ 前面时，整个展开的 \_\_VA_ARGS\_\_ 会被放入引号中：
```c
#define showlist(...) puts(#__VA_ARGS__)
showlist();            // 展开为 puts("")
showlist(1, "x", int); // 展开为 puts("1, \"x\", int")

```

#### \#\#
在替换列表中，两个连续的标识符之间使用 `##`，会先对这两个标识符进行参数替换，然后将结果拼接成一个单独的标记（token）。  
这种操作称为 **“拼接” (concatenation) 或 “标记粘贴” (token pasting)**。
- 只有可以合法形成一个标记的内容才能拼接：
- 两个标识符 → 形成更长的标识符
- 两个数字 → 形成一个数字
- 运算符 `+` 和 `=` → 形成 `+=`
- 注释不能通过 `/` 和 `*` 拼接形成，因为注释在宏展开前就会被删除
如果拼接结果不是合法标记 → 行为未定义
```c
#define MAKE_VAR(name, num) name##num  
  
int main()  
{  
    int MAKE_VAR(num, 1); //相等于int num1;  
    num1 = 100;  
    return 0;  
}
```


## 取消宏定义#undef
```c
#define PI   3.14159
int main(void){
    //……
}
#undef PI
int func(void){
    //……
}
```
表示PI只在main函数中有效，在func中无效

## ANSI预定义的宏
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

## 参考资料
- c语言预处理命令详解（已存档）
    - https://www.cnblogs.com/clover-toeic/p/3851102.html
- cppreference
    - https://en.cppreference.com/w/c/preprocessor/replace.html
