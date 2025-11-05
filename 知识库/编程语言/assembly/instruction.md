# instruction
## 汇编语言体系架构图

目前主流的汇编语法
- MASM
- NASM
- ATT
1️⃣ **同一 CPU 架构下，语法不同，但生成的机器码通常相同**
**例子：把 `eax` 加 1**
- NASM / Intel 风格：
    `add eax, 1`
- AT&T 风格：
    `addl $1, %eax`
- 机器码（x86）：
    `83 C0 01`
✅ 结果完全一样


2️⃣ **不同 CPU 架构下，机器码完全不同**
- 如果用 ARM 汇编和 x86 汇编描述同样的加法操作：
    `; x86 add eax, 1 ; ARM ADD R0, R0, #1`
- 它们在 CPU 内部的机器码是 **完全不同的**：
    - x86: `83 C0 01`
    - ARM: `E2800001`（ARM 指令集)
- 原因是 CPU 指令集不同。

## 8086指令
### mov 数据传送指令
```
MOV 目的操作数, 源操作数
```
把源操作数内容复制到目的操作数

| 类型          | 示例                 | 说明                   |     |
| ----------- | ------------------ | -------------------- | --- |
| 寄存器 ← 寄存器   | MOV AX, BX         | 把 BX 的内容复制到 AX       |     |
| 寄存器 ← 内存    | MOV AL, \[1234H\]  | 把内存单元 1234H 的内容送入 AL |     |
| 内存    ← 寄存器 | MOV \[2000H\], DL  | 把 DL 的内容存入内存 2000H   |     |
| 寄存器 ← 立即数   | MOV CX, 1234H      | 把立即数 1234H 送入 CX     |     |
| 内存    ← 立即数 | MOV \[3000H\], 25H | 把 25H 写入内存 3000H     |     |
- mov只是复制数据，不影响标志寄存器（FLAGS）
- 源数据不会变，只是复制
- ❌不允许内存到内存传送

### add
```asm
assume cs:hello

hello segment

    mov ax, 18h ;将18存入寄存器ax
    mov ah, 78h ;将78存入寄存器ax的高8位
    add al, 0F0h

    mov ax, 4c00h
    int 21h     ;相当于c语言中return 0
hello ends

end
```
- add al bl 若其中al溢出，那么是否会存储到ah中?
- 不会
### jmp
在8086中，jmp指令将直接修改CS、IP的值

## L
### lock
内存屏障

| 屏障类型                   | 作用位置      | 含义                          | 限制的重排序类型                 |
| ---------------------- | --------- | --------------------------- | ------------------------ |
| **LoadLoad Barrier**   | 两个读之间     | 确保前一个读（load）完成后，后一个读才能开始    | 禁止将第二个读重排到第一个读之前         |
| **LoadStore Barrier**  | 读操作后、写操作前 | 确保前面的读完成后，后面的写才可执行          | 禁止将写操作重排到前一个读之前          |
| **StoreStore Barrier** | 两个写之间     | 确保前一个写的结果对其他处理器可见后，后一个写才可执行 | 禁止将第二个写重排到第一个写之前         |
| **StoreLoad Barrier**  | 写操作后、读操作前 | 确保前面的所有写都对其他处理器可见后，才允许后续读操作 | 禁止将任何读/写重排到前一个写之后（最强的屏障） |

## M
### mov

| |风格|写法|
|---|---|---|
|**寄存器与立即数**|MASM|mov eax, 5|
||AT&T|movl $5, %eax|
||NASM|mov eax, 5|
|**寄存器与寄存器**|MASM|mov eax, ebx|
||AT&T|movl %ebx, %eax|
||NASM|mov eax, ebx|
|**内存 ← 寄存器**|MASM|mov [edi], eax|
||AT&T|movl %eax, (%edi)|
||NASM|mov [edi], eax|
|**寄存器 ← 内存**|MASM|mov eax, [esi]|
||AT&T|movl (%esi), %eax|
||NASM|mov eax, [esi]|
|**内存 ← 立即数**|MASM|mov byte ptr [eax], 0x10|
||AT&T|movb $0x10, (%eax)|
||NASM|mov byte [eax], 0x10|
|**复杂寻址示例**|MASM|mov ecx, [eax + ebx*4 + 8]|
||AT&T|movl 8(%eax, %ebx, 4), %ecx|
||NASM|mov ecx, [eax + ebx*4 + 8]|
