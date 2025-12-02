# class template
## 语法
c++的类模板是在编译期生成类，允许创建泛型类，处理不同的逻辑
类模板的基本语法
```cpp
template <parameter-list> class-declaration

template <parameter-list> requires constraint class-declaration // c++20起
```
- **class-declaration**：类声明
- **parameter-list**：一个非空，由逗号分隔的模板参数列表，列表中每一个参数可以是：
    - 常量参数
    - 类型参数
    - 模板参数
    - 以上任意一种的参数包

```cpp
//常量参数

//类型参数

//模板参数
```