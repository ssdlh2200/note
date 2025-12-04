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
    - 常量参数（constant parameter）
    - 类型参数（type parameter）
    - 模板参数（template parameter）
    - 以上任意一种的参数包（parameter pack）
```cpp
//常量参数
template <int N> class Box {  
public:  
  int arr[N]; //N是一个编译器常量
};
//常量参数包
template <int... Ns> class IntSeq {  
public:  
  int arr[1024]{Ns...};  
};


//类型参数
template <typename T> class Box {  
public:  
  T data; //T是一个类型，例如int、double、std::string
};
//类型参数包
template <typename... Ts> class Box{
}


//模板参数
template <template <typename> class Container> class Wrapper {  
public:  
  Container<int> container;  
};
//模板参数包
template <template <typename> class... Container> class Wrapepr{
}
```
- **constraint**：一个约束表达式，模板参数必须满足什么条件，这个模板才能被实例化