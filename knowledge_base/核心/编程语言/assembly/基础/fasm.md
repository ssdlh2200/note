# fasm

## 数据定义
### 方式一
```asm
my_data db 1, 2, 3, 4
```
- **`my_data`**: 这是一个**标号（Label）**，表示内存中这个数据区域的起始地址
- **`db`**: 意思是 "Define Byte"（定义字节），每个数据项占1字节（8位）
- **`1, 2, 3, 4`**: 要初始化的具体数据值
执行这段代码后，内存布局如下
```text
内存地址      数据（十六进制）  数据（十进制）
my_data+0:    01               1
my_data+1:    02               2  
my_data+2:    03               3
my_data+3:    04               4
```
## ce-fasm
### \[ENABLE\]
ce中激活脚本时，\[ENABLE\]部分的代码会被执行
```asm
[ENABLE]

// 这部分代码在启用脚本时运行
alloc(newmem,2048)      // 分配内存
label(returnhere)       // 定义标签

newmem:
// 你的主要代码逻辑
mov eax,[ebx+04]
add eax,100
mov [ebx+04],eax

// 挂钩到游戏代码
somegame.exe+12345:
jmp newmem
nop
returnhere:
```
### \[DISABLE\]
ce中停用脚本时，\[DISABLE\]部分的代码会被执行
```asm
[DISABLE]

// 这部分代码在禁用脚本时运行
somegame.exe+12345:
db 89 47 04 90        // 恢复原始字节（原始指令）
dealloc(newmem)       // 释放分配的内存
```
### alloc
分配虚拟内存
```asm
[ENABLE]
// 在目标进程（游戏）中分配 1024 字节
alloc(myMemory, 1024)

// 现在 myMemory 指向游戏进程中的一个有效地址
// 比如：myMemory = 0x12345678
```
底层调用操作系统api
```cpp
// 相当于调用：
VirtualAllocEx(游戏进程, NULL, 大小, MEM_COMMIT | MEM_RESERVE, PAGE_EXECUTE_READWRITE)
```