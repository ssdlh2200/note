# masm
## 5.0版本
### asm文件格式
- 汇编源代码，包括：
    1. 段定义（Segment）
    2. 数据定义（Data）
    3. 代码段（Code）
    4. 伪指令（Directive）
    5. 注释（Comment）
- 每一行可以包含：
    - 标签（Label）
    - 指令（Instruction）
    - 操作数（Operand）
    - 注释（以 `;` 开头）
```asm
assume cs:hello

hello segment
    mov ax, 18
    mov ax, ac00h
    int 21h    ;相当于c语言中return 0
hello ends

end
```
#### segment（段定义）
一个汇编程序由多个段组成，这些段通常用来存放代码、数据或者当作栈空间使用
每个段都需要一个段名
```asm
段名 segment 
....
段名 ends
```

### masm
```asm
masm 汇编源文件.asm
```
如果有目录的情况下一定要用：**\\**
使用masm会出现如下提示：
- Object filename：最终要编译的目标文件（可使用folder/file.obj来生成在指定文件夹）
- Source listing：列表文件，源程序编译为目标文件的产生的中间结果
- Cross-reference：交叉引用文件，同列表文件一样是中间结果
如果不想要提示直接在语句后面加上封号：**;**
```asm
masm 汇编源文件.asm;
```

### link
```asm
link 生成的OBJ文件.obj
```
如果有目录的情况下一定要用：**\\**
使用link会出现如下提示：
- Run File(.exe)：最终生成的可执行文件（可使用folder/file.exe来生成在指定文件夹）
- List File(.map)：生成可执行文件时的中间结果
- Libraries(.lib)：调用的库
如果不想要提示直接在语句后面加上封号：**;**
```asm
link 生成的OBJ文件.obj;
```

### debug
```asm
debug 源程序.exe
```
- r：查看寄存器的值

