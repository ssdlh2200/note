# c
## 声明、初始化
- **声明**：告诉编译器“我要使用一个类型为 `struct Person` 的变量”，但没有给它任何初始值。
    `struct Person person1; // 只是声明`
    在这种情况下：
    - 如果变量是 **局部变量（函数内部）**，它的内容是 **未定义的**（里面是垃圾值）。        
    - 如果变量是 **全局变量或静态变量**，它会被 **自动初始化为 0**（`name` 全部为 `\0`，`age` 为 0，`height` 为 0.0）。
- **初始化**：给变量一个确定的初始值。
- **声明 ≠ 初始化**
- 局部变量不会自动清零，值是未定义的。    
- 全局变量或静态变量会自动初始化为 0。

## 内存模型
C程序执行时，内存大致划分为4个区域
- 代码区：存放函数体二进制代码，操作系统管理
- 全局区：存放全局变量、静态变量、常量
- 栈区：编译器自动分配和释放， 函数参数值、局部变量
- 堆区：程序员分配和释放
### 全局区
全局变量和静态变量对比

|特性|全局变量|静态变量|
|---|---|---|
|定义位置|函数外部|函数内部或函数外部加 `static`|
|生命周期|整个程序运行期间|整个程序运行期间|
|作用域|全程序（其他文件可 `extern`）|局部或文件内（受 `static` 限制）|
|存储位置|全局/静态区|全局/静态区|
|默认初始化|0|0|
|是否可隐藏|❌|✅（可隐藏作用域）|
### 栈区
```c
#include <stdio.h>  
int* add()  
{  
    int a = 10;  
#pragma  
    return &a;  
}  
  
int main(void)  
{  
    int* result = add();  
    printf("%d\n", *result);  
    printf("%d\n", *result);  
    getchar();  
    return 0;  
}
```
在反汇编中，可以看到，return &a 的汇编代码是 mov exa, 0 当我们后续想要访问0地址时，程序发生了错误
```c
Process finished with exit code -1073741819 (0xC0000005)
```
是 **Windows 系统特有的错误码**，0xC0000005 表示 **“访问违规（Access Violation）”**。也就是说，程序尝试访问了 **不允许访问的内存地址**

### 堆区
#### 堆创建机制
1. 程序启动时，操作系统会为程序创建一个或多个堆
2. 每次调用malloc()时，会从现有的堆中分配块(heap lock)
3. 当堆内存不够时，操作系统将申请增加堆内存
#### 查看堆内存
```c
int main()  
{  
    setvbuf(stdout, NULL, _IONBF, 0);  
  
    int* pointer = malloc(sizeof(int));  
    printf("pointer: %p\n", pointer);  
    *pointer = 0xffaaccbb;  
  
    *pointer = 0x11223344;  
  
    free(pointer);  
    return 0;  
}
```
使用dbg调试查看内存
1. 首先执行到printf函数打印出pointer所在堆内存的地址pointer: 000001fd47e72710
2. 转到该地址内存
3. 执行赋值ffaaccbb
- ![[20251109-17-22-56.png]]
4. 执行赋值11223344
5. ![[20251109-17-23-42.png]]
6. free内存
7. ![[20251109-17-24-28.png]]
#### 打印堆内存快照
```c
int main()  
{  
    //设置utf8编码，关闭printf缓冲区  
    SetConsoleOutputCP(65001);  
    setvbuf(stdout, NULL, _IONBF, 0);  
  
    //获取heap_snap, heap_list  
    HANDLE heap_snap = CreateToolhelp32Snapshot(TH32CS_SNAPHEAPLIST, GetCurrentProcessId());  
    HEAPLIST32 heap_list = {.dwSize = sizeof(HEAPLIST32)};  
  
    //假设从堆1，2，3，4，5，6开始读取  
    Heap32ListFirst(heap_snap, &heap_list);  
  
    SIZE_T heap_size = 0;  
  
    do  
    {  
        //处理每一个堆中的信息  
        HEAPENTRY32 heap_entry = {.dwSize = sizeof(HEAPENTRY32)};  
  
        printf("heap_id: %llu\n", heap_list.th32HeapID);  
        Heap32First(&heap_entry, GetCurrentProcessId(), heap_list.th32HeapID);  
        do  
        {  
            heap_size += heap_entry.dwBlockSize;  
            printf("heap_addr:%p, size: %5llx, flag: %lu\n", (void*)heap_entry.dwAddress, heap_entry.dwBlockSize,  
                   heap_entry.dwFlags);  
            heap_entry.dwSize = sizeof(HEAPENTRY32);  
            heap_size += heap_entry.dwBlockSize;  
        }  
        while (Heap32Next(&heap_entry));  
    }  
    while (Heap32ListNext(heap_snap, &heap_list));  
  
    printf("heap_size: %llx\n", heap_size);  
  
    return 0;  
}
```


