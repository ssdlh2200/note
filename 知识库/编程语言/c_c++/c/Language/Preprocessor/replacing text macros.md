# replacing text macros(替换文本宏)

## \#define
`#define` **是文本替换工具**，在编译之前，编译器会先处理所有的 `#define` 指令，将代码中的宏名替换为它所定义的值或表达式，`#define` 常用于定义常量、函数式宏和条件编译等。在编译预处理时，对程序中所有出现的宏名，都用宏定义中的字符串去代换，这称为宏替换或宏展开。
### 语法
| 语法形式                                                     | 描述           | 备注   |
| -------------------------------------------------------- | ------------ | ---- |
| `#define identifier replacement-list`                    | 定义对象宏        |      |
| `#define identifier( parameters ) replacement-list`      | 定义函数宏        |      |
| `#define identifier( parameters, ... ) replacement-list` | 定义带可变参数的函数宏  | C99起 |
| `#define identifier( ... ) replacement-list`             | 定义只带可变参数的函数宏 | C99起 |
| `#undef identifier`                                      | 取消宏定义        |      |


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
+ 函数调用在编译后程序运行时进行，并且分配内存。宏替换在编译前进行，不分配内存
- 宏展开不占用运行时间，只占编译时间，函数调用占运行时间(分配内存、保留现场、值传递、返回值)

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