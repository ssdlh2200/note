# malloc
## 标准规范
```c
void *__cdecl malloc(size_t _Size);
```
- malloc会在堆区开辟指定大小的一块内存空间，但是不会对其中的内容初始化，如果分配成功会返回一个指针，并且该指针符合基本的对齐要求
- malloc是线程安全的
- （C11起）内存的分配释放都存在一个全局顺序，不同担心一个线程free后，另外一个线程马上malloc会乱序或者数据竞争，C 标准保证分配/释放的顺序在每块内存区域上是有序的
- **size：** 要分配的字节数
    - 如果size时0，malloc的行为由实现定义（不同编译器和不同操作系统不同）
        - 可能会返回一个空指针（NULL）
        - 也可能返回一个非空指针，但此时
            - 该指针不能被引用（dereference），即不能访问它的内存
            - 仍然应该将其传递给free来避免内存泄漏
- **返回值：** 
    - 分配成功：内存起始位置的指针，记得free释放
    - 分配失败：返回空指针NULL
## fundamental alignment（基本对齐）
在 C 语言中，不同的数据类型对内存地址有 **对齐要求**，即它们必须存放在某些特定地址的倍数上，否则 CPU 访问效率低，甚至可能出错。

| 类型              | 对齐要求示例（常见）          |
| --------------- | ------------------- |
| `char`          | 任意地址（1 字节对齐）        |
| `short`         | 2 字节对齐（地址必须能被 2 整除） |
| `int` / `float` | 4 字节对齐（地址必须能被 4 整除） |
| `double`        | 8 字节对齐（地址必须能被 8 整除） |
| `struct`        | 取决于最大成员的对齐要求        |
<div style="background-color: #ffe4e1; padding: 10px; border-left: 4px solid #f1c40f;color: black" >
在 x86/x86-64 上，这通常意味着指针地址至少是8字节对齐
</div>



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
