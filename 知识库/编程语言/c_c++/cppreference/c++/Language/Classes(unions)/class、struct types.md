# class、struct types

## 语法
```scss
class-key 
attr ﻿(optional) 
class-head-name 
class-property-specs ﻿(optional) 
base-clause ﻿(optional)
{ member-specification }
```
- **class-key：** 由class、struct、union三个组成
    - class <font color="#7030a0">默认成员是private</font>
    - struct 默认成员是public
    - union 表示联合体
- **attr：** 自c++11起可用，例如alignas指定对其要求
- **class-head-name：** 类名（可以带命名空间限定符）
- **class-property-specs：** c++11起引入的一组修饰符，每个最多使用一次
    - final(c++11)：该类不能被继承
    - trivially_relocatable_if_eligible(c++26)：如果符合条件，标记类为可平凡移动
    - replaceable_if_eligible(c++26)：如果符合条件，标记类可替换
- **baes-clause：** 基类列表，指定继承的类型及其访问控制（public Base，private Base）
- **member-specification：** 类的主体，包含成员变量，成员函数，访问说明符
## 向前声明
