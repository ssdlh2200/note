# struct declaration

## 基本用法
### 结构体变量单个
struct 关键字用于将不同类型的数据组合在一起
```c
#include <stdio.h>  
  
struct person {  
    char* name;  
    int age;  
};  
  
#define PRINT_PERSON(name, age) printf("name: %s, age: %d\n", name, age);  
  
int main()  
{  
    //声明结构体变量  
    struct person jack;  
  
    //初始化结构体变量  
    jack.name = "Jack";  
    jack.age = 18;  
    PRINT_PERSON(jack.name, jack.age);  

    return 0;  
}
```
### 结构体变量多个
```c
#include <stdio.h>  
  
struct person {  
    char* name;    
    int age;  
}jack, tom; //全局变量  
  
int main()  
{  
    //局部变量  
    struct person joe, sam;  
  
    return 0;  
}
```
### 结构体数组
```c
#include <stdio.h>  
  
struct person {  
    char* name;  
    int age;  
};  
  
#define PRINT_PERSON(person) printf("name: %s, age: %d\n", person.name, person.age);  
  
int main()  
{  
    //声明结构体数组  
    struct person persons[2];  
    persons[0].name = "jack";  
    persons[0].age = 18;  
    persons[1].name = "joe";  
    persons[1].age = 20;  
    PRINT_PERSON(persons[0]);  
    PRINT_PERSON(persons[1]);  
  
    return 0;  
}
```
### 结构体嵌套
```c
#include <stdio.h>  
  
struct address {  
    char* city;  
    char* state;  
};  
struct person {  
    char* name;  
    struct address addr;  
};  
#define PRINT_PERSON(person) printf("name: %s, city: %s, state: %s\n", person.name, person.addr.city, person.addr.state);  
int main()  
{  
    //声明结构体数组  
    struct person jack;  
    jack.name = "jack";  
    jack.addr.city = "guangzhou";  
    jack.addr.state = "china";  
    PRINT_PERSON(jack);  
    return 0;  
}
```
### 结构体指针
```c
#include <stdio.h>  
#include <stdlib.h>  
  
struct person {  
    char* name;  
    int age;  
};  
  
#define PRINT_PERSON(name, age) printf("name: %s, age: %d\n", name, age);  
  
int main()  
{  
    //jack指针在栈内，指向堆内存  
    struct person* jack = malloc(sizeof(struct person));  
  
    //堆中存储一个指针指向"jack"字面量  
    //堆栈还存储一个int类型的整数  
    jack->name = "Jack";  
    jack->age = 18;  
    PRINT_PERSON(jack->name, jack->age);  
  
    free(jack);  
  
    return 0;  
}
```
### 结构体别名
```c
typedef struct person {  
    char* name;  
    int age;  
} p;  
  
int main()  
{  
    //相当于struct person jack == p jack  
    p jack;  
    jack.name = "Jack";  
    jack.age = 18;  
  
    return 0;  
}
```
### 结构体匿名
```c
struct person {  
    char* name;  
    int age;  
};  

//通常需要直接声明变量
struct {  
    char* name;  
    int age;  
} jack, joe;
```