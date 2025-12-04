# modules
## 1. 分类
nodejs中模块分为了三大类，分别是
1. 内置模块（node.js官方提供，例如fs、path、http等）
2. 自定义模块（自行创建的每一个js文件都是自定义模块）
3. 第三方模块（第三方开发的模块）

## 2. CommonJS标准下的模块

### 2.1. require()加载js
我们可以使用require加载其他的js文件，使用require方法加载其他模块时会执行被加载模块中的代码
首先创建一个index0.js文件
```js 
//index0.js
console.log("加载了index0.js文件")
```
然后创建一个index1.js文件
```js
//index1.js
const index = require('./index')
```
执行代码，运行结果
```text
加载了index.js文件
```

### 2.2. module对象
在每一个.js文件中都有一个module对象，它里面存储了和当前模块有关的信息
```js
//index0.js
const username = 'ssdlh'
function hello(){
    console.log('hello')
}
console.log(module) //输出module对象
```
打印结果如下：
```text
{
  id: '.',
  path: 'd:\\workspace\\Algorithm_nodejs',
  exports: {},
  filename: 'd:\\workspace\\Algorithm_nodejs\\index0.js',
  loaded: false,
  children: [],
  paths: [
    'd:\\workspace\\Algorithm_nodejs\\node_modules',
    'd:\\workspace\\node_modules',
    'd:\\node_modules'
  ],
  [Symbol(kIsMainSymbol)]: true,
  [Symbol(kIsCachedByESMLoader)]: false,
  [Symbol(kIsExecuting)]: true
}
```
其中我们可以用exports向外共享成员，让外界访问到

### 2.3. module.exports+require()
我们可以使用module.exports对象，将模块中的成员共享出去，当外界使用require()方法的时候得到的就是module.exports指向的对象
在index0.js文件中
```js
const username = 'ssdlh'
function hello(){
    console.log('hello')
}

//module.exports默认指向一个空对象
module.exports.username = username
module.exports.hello = hello
```
在index1.js文件中
```js
const index = require('./index0')
console.log(index)
console.log(index.username)
index.hello()
```
运行结果为
```text
{ username: 'ssdlh', hello: [Function: hello] }
ssdlh
hello
```

### 2.4. exports
为了简化module.exports，node提供了exports对象，默认情况下module.exports和exports指向同一个对象，最终共享结果还是以module.exports为准
```js
console.log(module.exports === exports) // true
```
在模块内部，this指向的是当前模块的导出对象
```js
console.log(this === module.exports); // true 
console.log(this === exports); // true
```
那么可以将上面的代码可以改造为
```js
const username = 'ssdlh'
function hello(){
     console.log('hello')
}
exports.username = username
exports.hello = hello
```

### 2.5. exports和module.exports
<div style="background-color: #ffe4e1; padding: 10px; border-left: 4px solid #f1c40f;">使用require()方法的时候永远得到的是module.exports指向的对象</div>

```js
const username = 'ssdlh'
function hello(){
     console.log('hello')
}
exports = {
    username: 'exports mod ssdlh',
    hello: function(){
        console.log('exports mod hello')
    }
}
module.exports = {
    username: 'module.exports mod ssdlh',
    hello: function(){
        console.log('module exports mod hello')
    }
}
```
最终的导出结果是module.exports指向的对象

## 3. ES6标准下的模块






## 参考链接
- https://juejin.cn/post/7034022388429750302
    - 详解require和import本文重点讲解了NodeJS中的模块加载原理，和babel转化ESM的机制。CJS和ESM 
- 