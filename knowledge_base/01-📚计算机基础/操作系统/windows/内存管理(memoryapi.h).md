# 内存管理(memoryapi.h)
## windows虚拟内存
windows的虚拟内存可以分为
- **Reserved（保留）**：操作系统为进程保留了一块虚拟地址空间，但没有分配物理内存或交换空间
- **Committed（已提交）**：操作系统不仅保留了虚拟地址空间，还确保这些地址有实际的物理内存或页面文件（swap）支持。这意味着，如果进程真的使用这些地址，系统能提供实际存储
- **Free（空闲）**：既没有保留也没有提交，可以直接分配
## VirtualAlloc
```cpp
#include <memoryapi.h>  
#include <stdio.h>

int main() {  
  // 1GB  
  int virtual_memory = 1024 * 1024 * 1024;  
  int page_size = 4 * 1024;  
  int page_num = virtual_memory / page_size;  
  
  // 分配虚拟内存并提交  
  void *p = VirtualAlloc(NULL, virtual_memory, MEM_COMMIT, PAGE_READWRITE);  
  printf("%p\n", p);  
  
  printf("entrer to alloc physical page...\n");  
  getchar();  
  // 由于windows的懒分配机制，只有访问这些内存页时，才会分配物理页  
  // 每页的大小是4KB  
  for (int i = 0; i < page_num; i++) {  
    //printf("num: %d page\n", i);  
    ((char *)p)[i*page_size] = 1;  
  }  
  printf("alloc physical page done\n");  
  
  getchar();  
  return 0;  
}
```
分配虚拟内存并提交（就是让操作系统给分配实际的物理内存），由于windows的懒分配机制，我们需要访问这些内存页才能够真正分配物理页
我们运行程序并在VMMap中查看虚拟内存，可以看到private data中分配了1GB
![[20251125-18-30-13.png]]
然后我们进入到任务管理器
![[20251125-18-34-12.png]]
发现只占用6.8M内存，然后我们随意输入按键，让其操作系统分配物理页
![[20251125-18-35-39.png]]
1024MB + 6.8MB = 1030.8MB

## VirtualAllocEx指定进程分配虚拟内存
