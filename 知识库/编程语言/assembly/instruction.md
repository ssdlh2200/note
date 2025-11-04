# instruction

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
