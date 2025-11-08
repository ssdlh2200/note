# masm(5.0)
## asm文件格式
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

### 伪指令
```asm
assume cs:hello
hello segment

    mov ax, 18H   ;推荐使用16进制方便查看寄存器内的值
    
    mov ax, ac00h ;将程序控制权交换给dos
    int 21h       ;相当于c语言中return 0
    
hello ends

end
```
其中
```
assume cs:hello
hello segment
    ...
    ...
    ...
hello ends
end
```
其中assume、segment、ends、end都是伪指令，伪指令只会由编译器识别
- 不会生成机器机器码
- 不会修改CPU寄存器
- 只在汇编阶段有效
MASM 伪指令主要分为：
1. **段相关**：SEGMENT, ENDS, ASSUME, ORG, GROUP
2. **数据定义**：DB, DW, DD, DQ, DT, DUP
3. **程序控制**：END, PROC/ENDP, MACRO/ENDM, INCLUDE, TITLE
4. **结构类型**：STRUC/ENDSTRUC, FIELD, TYPEDEF, EQU, SET
5. **条件汇编**：IF/ELSE/ENDIF, IFDEF/IFNDEF
6. **辅助/符号**：PUBLIC, EXTRN, ALIGN, OPTION
#### segment（段定义）
一个汇编程序由多个段组成，这些段通常用来存放代码、数据或者当作栈空间使用
每个段都需要一个段名
```asm
段名 segment 
....
段名 ends
```

#### assume（假设）
assume时masm提供的伪指令
```
assume ds:SsdlhData
assume cs:SsdlhCode

SsdlhData segment
    a DB 11h
    b DB 22h
    c DB 33h
SsdlhData ends

SsdlhCode segment
MAIN:
    mov ax, SsdlhData ;初始化 DS
    mov ds, ax        ;a b c都可以通过[DS:偏移]访问了
    
    mov ax, a
    mov bx, b
    mov cx, c
    
    mov ah, 4ch ;相等于return 0
    int 21h
SsdlhCode ends

end MAIN
```
当我们访问变量时
```
mov ax, SsdlhData ;会把SsdlhData翻译成16位的值
mov AL, a ; 汇编会生成[ds:a]
```
#### end
如果没有end，默认从第一条指令开始执行
```
MAIN:                ; 入口标签
    MOV AX, 4C00h
    INT 21h
END MAIN              ; 入口标签指定给 END
```

```
START:                ; 入口标签
    MOV AX, 4C00h
    INT 21h
END START              ; 入口标签指定给 END
```
#### db、dw、dd、dq
| 指令   | 英文                | 含义         | 占用字节 |
| ---- | ----------------- | ---------- | ---- |
| `DB` | Define Byte       | 定义字节       | 1    |
| `DW` | Define Word       | 定义字（16 位）  | 2    |
| `DD` | Define Doubleword | 定义双字（32 位） | 4    |
| `DQ` | Define Quadword   | 定义四字（64 位） | 8    |

## masm
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

## link
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

## debug
```asm
debug 源程序.exe
```

| 指令                | 含义         | 功能说明                 |
| ----------------- | ---------- | -------------------- |
| **A [地址]**        | Assemble   | 汇编指令（将汇编代码转成机器码）     |
| **U [地址]**        | Unassemble | 反汇编，显示内存中的机器码对应的汇编指令 |
| **D [地址]**        | Dump       | 以十六进制形式显示内存内容        |
| **E [地址]**        | Enter      | 向内存写入（编辑）数据或机器码      |
| **R [寄存器]**       | Register   | 查看或修改寄存器内容           |
| **T [步数]**        | Trace      | 单步执行（跟踪一条汇编指令）       |
| **P [步数]**        | Proceed    | 执行一条过程（跳过 CALL）      |
| **G [地址]**        | Go         | 运行程序直到断点或结束          |
| **L [参数]**        | Load       | 从磁盘读取文件或扇区到内存        |
| **W [参数]**        | Write      | 将内存内容写回磁盘            |
| **M 源地址 目标地址 长度** | Move       | 拷贝一段内存内容             |
| **N 文件名**         | Name       | 指定加载或保存的文件名          |
| **H 数1 数2**       | Hex        | 十六进制加减计算             |
| **I 端口**          | Input      | 从 I/O 端口读取一个字节       |
| **O 端口 数据**       | Output     | 向 I/O 端口输出一个字节       |
| **Q**             | Quit       | 退出 DEBUG             |

