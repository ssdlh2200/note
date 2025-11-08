# assembly
## 汇编语法体系

x86架构下主流的汇编语法
- MASM
- NASM
- ATT
arm架构下主流的汇编语法
- arm官方的汇编语法
- gas
```
1️⃣ 同一CPU 架构下，语法不同，但生成的机器码通常相同

NASM / Intel 风格：add eax, 1

AT&T 风格：addl $1, %eax

生成的机器码(x86)：83 C0 01
✅ 结果完全一样
```

```
2️⃣ 不同 CPU 架构下，机器码完全不同

如果用 ARM 汇编和 x86 汇编描述同样的加法操作：
    x86 add eax, 1 
    ARM ADD R0, R0, #1
它们在 CPU 内部的机器码是完全不同的
    x86: 83 C0 01
    ARM: E2800001
❌ 结果不相同
```

## 8086
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

### 流程转移指令
#### jmp
- 同时修改CS、IP的内容
    - jmp 段地址:偏移地址
- 仅修改IP的内容
    - jmp ax（类似于mov IP, ax）
#### call
call属于流程转移指令，会修改IP，或者同时修改CS、IP
CPU指向call指令
- 将当前的IP或者CS和IP压入栈
- 转移到标号处执行指令
```asm
assume cs:codeSeg
codeSeg segment

main:

    mov ax, 1234h

    call s

    mov ax, 4c00h
    int 21h

s:  add ax, 1h
    ret

codeSeg ends
end main
```
以冒号 **:** 结尾的都是一个标签，标记某条指令的地址，汇编器在生成机器码时会给 **s** 分配一个地址
- 我们可以看到S被汇编器翻译成地址
![[20251106-00-54-59.png]]
- 我们可以看到IP新的偏移地址，栈顶偏移地址的变化
![[20251106-00-57-13.png]]

#### call far ptr
实现的是段间转移




## x86(nams,masm,att)
### L
#### lea


#### lock
内存屏障

| 屏障类型                   | 作用位置      | 含义                          | 限制的重排序类型                 |
| ---------------------- | --------- | --------------------------- | ------------------------ |
| **LoadLoad Barrier**   | 两个读之间     | 确保前一个读（load）完成后，后一个读才能开始    | 禁止将第二个读重排到第一个读之前         |
| **LoadStore Barrier**  | 读操作后、写操作前 | 确保前面的读完成后，后面的写才可执行          | 禁止将写操作重排到前一个读之前          |
| **StoreStore Barrier** | 两个写之间     | 确保前一个写的结果对其他处理器可见后，后一个写才可执行 | 禁止将第二个写重排到第一个写之前         |
| **StoreLoad Barrier**  | 写操作后、读操作前 | 确保前面的所有写都对其他处理器可见后，才允许后续读操作 | 禁止将任何读/写重排到前一个写之后（最强的屏障） |

### M
#### mov

|              | 风格        | 写法                            |
| ------------ | --------- | ----------------------------- |
| **寄存器与立即数**  | MASM、NASM | mov eax, 5                    |
|              | AT&T      | movl $5, %eax                 |
| **寄存器与寄存器**  | MASM、NASM | mov eax, ebx                  |
|              | AT&T      | movl %ebx, %eax               |
| **内存 ← 寄存器** | MASM、NASM | mov [edi], eax                |
|              | AT&T      | movl %eax, (%edi)             |
| **寄存器 ← 内存** | MASM、NASM | mov eax, [esi]                |
|              | AT&T      | movl (%esi), %eax             |
| **内存 ← 立即数** | MASM      | mov byte ptr \[eax\], 0x10    |
|              | AT&T      | movb $0x10, (%eax)            |
|              | NASM      | mov byte \[eax\], 0x10        |
| **复杂寻址示例**   | MASM、NASM | mov ecx, \[eax + ebx\*4 + 8\] |
|              | AT&T      | movl 8(%eax, %ebx, 4), %ecx   |



