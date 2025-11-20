# 快速上手
## 1. 编译
编译是将ts文件编译为js文件
- 手动编译
- 自动编译

### 手动编译
1. 我们先来看手动编译，首先我们安装typescript
``` js
npm i typescript -g
```
2. 安装之后我们就可以使用命令tsc(typescript compiler)，利用tsc我们就可以把ts文件变为js文件，在index.ts文件中
``` ts
const person = {
    name: 'ssdlh'
}
console.log(`my name is ${person.name}`)
```
3. 使用tsc index，自动生成index.js文件
```js
var person = {
    name: 'ssdlh'
};
console.log("my name is ".concat(person.name));
```

### 自动化编译
1. 我们使用init命令生成配置文件
```js
tsc --init
```
2. 此时在该目录下将生成一个tsconfg.json的文件
3. 我们使用watch命令监听目录中.ts文件的变化
```js
tsc --watch
```
小优化，当编译出错时不生成.js文件（也可以修改tsconfig.json中noEmitError配置）
```js
tsc --noEmitOnError --watch
```

## 2. 类型声明
### 变量声明类型
```ts
let aStr: string
let bNum: number
let cBool: boolean

aStr = '3'
bNum = 3
cBool = true
```
### 函数声明类型
a,b后面的number分别是声明a，b的类型，第三个number声明的函数返回值的类型
```ts
function add(a: number, b: number):number{
    return a + b
}
```
### 字面量类型
声明b为hello，以后b中只能存储hello，不能存储其他类型的数据
```ts
let b: 'hello'

b = 'hello'

b = 'word' // ❌ 编译报错
```
### 联合类型
联合类型可以允许变量是多个类型
```ts
let value: string | number;
```
value既可以是string类型，也可以是number类型
联合类型也可以针对字面量类型
```ts
let value: 'hello' | 'hi'

value = 'hello'
value = 'hi'
```

## 3. 类型推断
当我们给a赋值为10的时候，ts已经将变量a推断为number类型，此后不能将boolean类型赋值给变量a
```ts
let a = 10
a = false
不能将类型“boolean”分配给类型“number”
```
## 4. 类型总览
### js、ts类型总览
js中数据类型如下：
- string
- number
- boolean
- null
- undefined
- bigint
- symbol
- object（包含Array，Function，Date，Error）
ts中数据类型如下：
1. js中所有的数据类型
2. 六种新类型
    - any
    - unknown
    - never
    - void
    - tuple
    - enum
3. 两个用于自定义类型的方式
    - type
    - interface
### string和String
string是基本类型字符串，String是包装类型
```ts
let str1: string
str1 = 'hello'
let str2 = new String('hello')

console.log(str1 === str2)
```
上面这段代码运行结果为false
<div style="background-color: #ffe4e1; padding: 10px; border-left: 4px solid #f1c40f;">js中这些内置的构造函数：Number、String、Boolean，在日常开发中很少使用，在ts中也是同样的道理，一般都用小写的number、string、boolean</div>

## 5. 常用类型
### any
any的含义是任意类型，一旦将变量限制为any，意味着放弃了对于该变量的类型检查
- **显示any**
```ts
let a: any
a = 'str'
a = 123
```
- **隐式any**
```ts
let a
a = 'str'
a = 123
```
注意！any可以赋值给其他已经声明类型的变量
```ts
let x: number
let a: any
a = true
x = a
```
### unknown
unknown可以视为一个类型安全的any
【例1】
```ts
let a: unknown
let x: number
a = 1234
x = a //编译器会报错，不能将unknown类型赋值给number类型
```
如果我们加上判断则可以进行赋值
```ts
let a: unknown
let x: number
a = 1234

//第一种：使用if进行类型判断
if(typeof a === 'number'){
    x = a
}

//第二种：使用断言进行类型判断
x = a as number

//第三种：断言写法2
x = <number> a
```
【例2】
``` ts
let str1: string
str1 = 'hello'
str1.toUpperCase()

let str2: unknown
str2 = 'hello'
str2.toUpperCase() //报错，未知类型不能调用


let str3: any
str3 = 'hello'
str3.toUpperCase()
```
### never
【例1】
指定一个变量的数据类型为never，那就意味这变量以后不能存储任何值
```ts
let a: never
a = 1       //报错
a = true    //报错
```
指定函数的返回值为never，则该函数不能正常执行到末尾
```ts
function f1():never{
    throw new Error('程序运行异常')
}
f1()
```
因为never函数无法正常执行完毕，所以只能抛出异常
### void
1. void通常用于函数返回值的声明
```ts
function f1():void{
    return undefined
}
```
<div style="background-color: #ffe4e1; padding: 10px; border-left: 4px solid #f1c40f;">void声明函数返回值，没有显式返回值，但是有一个隐式返回值就是undefined，undefined是void可以接受的一种空</div>

### object
