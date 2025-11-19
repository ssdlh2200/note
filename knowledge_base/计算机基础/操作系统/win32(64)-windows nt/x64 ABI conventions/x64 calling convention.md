# x64 calling convention
## x64 register usage
在 x64 架构下有：
- 16 个通用整数寄存器（RAX, RBX, RCX, … R15）
- 16 个 XMM/YMM 寄存器（向量/浮点寄存器）
- RSP 栈指针 和 RBP 帧指针
- CPU 的标志寄存器（Flags）中包含方向标志（Direction Flag）等
寄存器的volatile和nonvolatile
- **volatile（易失寄存器/scratch）**
    - 在函数调用中，调用方（caller）假设这些寄存器可能会被被调用函数（callee）破坏，所以调用方需要这些寄存器的值，在调用前需要保存
- **nonvolatile（非易失寄存器/callee-saved）**
    - 被调用函数必须保留这些寄存器的值，如果调用函数要用到这些寄存器，必须先保存旧值，函数返回时恢复
**<font color="#ff0000">通用整数寄存器（RAX, RBX, …）</font>**

| 寄存器                | 易失性         | 用途                 |
| ------------------ | ----------- | ------------------ |
| RAX                | Volatile    | 存放函数返回值            |
| RCX                | Volatile    | 第 1 个整数参数          |
| RDX                | Volatile    | 第 2 个整数参数          |
| R8                 | Volatile    | 第 3 个整数参数          |
| R9                 | Volatile    | 第 4 个整数参数          |
| R10-R11            | Volatile    | caller 自行保存；系统调用使用 |
| R12-R15            | Nonvolatile | callee 必须保存        |
| RDI, RSI, RBX, RBP | Nonvolatile | callee 必须保存        |
| RSP                | Nonvolatile | 栈指针                |
**<font color="#ff0000">浮点 / 向量寄存器（XMM/YMM）</font>**

|寄存器|易失性|用途|
|---|---|---|
|XMM0/YMM0|Volatile|第 1 个浮点参数；__vectorcall 第 1 个向量参数|
|XMM1/YMM1|Volatile|第 2 个浮点参数|
|XMM2/YMM2|Volatile|第 3 个浮点参数|
|XMM3/YMM3|Volatile|第 4 个浮点参数|
|XMM4/YMM4|Volatile|需要时由 caller 保存；第 5 个向量参数|
|XMM5/YMM5|Volatile|需要时由 caller 保存；第 6 个向量参数|
|XMM6-XMM15|Nonvolatile|callee 必须保存|
|YMM6-YMM15 上半部|Volatile|caller 保存|

> XMM 用于 SSE 指令，YMM 用于 AVX 指令（扩展寄存器）

**<font color="#ff0000">其他注意事项</font>**
- 方向标志（DF, Direction Flag）
    - 函数调用前后，DF 必须为清零状态（影响字符串指令如 `REP MOVSB`）。
- 调用约定
    - Windows x64 函数调用使用 **Microsoft x64 calling convention**
        - 前四个整数参数通过 RCX, RDX, R8, R9 传递
        - 前四个浮点参数通过 XMM0-XMM3 传递
        - 多余参数通过栈传递
