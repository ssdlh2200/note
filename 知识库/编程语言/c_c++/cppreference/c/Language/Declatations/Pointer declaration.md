# Pointer declaration
指针是一种对象类型，指向一个函数或者另一种类型的对象，也可能不指向任何东西，这种情况由特殊的空指针值（null pointer value）表示
## 语法
```c
* attr-spec-seq ﻿(optional) qualifiers ﻿(optional) declarator
```
- \*：表示这是一个指针
- attr-sepc-seq：对编译器的提示优化或对齐
- qualifiers：限定符，const，volatile
- declarator：指针的变量名，可以是更复杂的形式，int \*\*P
```

```