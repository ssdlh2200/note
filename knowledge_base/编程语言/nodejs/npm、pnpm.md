# npm、pnpm
## 1. npm(node package manager)
### 1.1. npm官网包
下面网址可以搜索到npm所有的包
- https://www.npmjs.com
下面服务器提供了npm下载包的地址
- https://registry.npmjs.org
### 1.2. 常用命令
#### init
init命令是用来初始化一个新的Node.js项目，它的作用是创建一个packge.json文件

#### install
npm install --verbose可以查看安装时的详细日志

### 1.3. package.json文件
#### scripts
**npm run \<script\>** 会执行scripts中定义的脚本
```json
{
  "scripts": {
    "dev": "next dev",
    "custom": "next build",
    "demo": "next start"
  }
}
```
- npm run dev == next dev
- npm run custom == next build
- npm run demo == next start

### 1.3. package-lock.json文件

## 2. pnpm
- 查看包下载位置pnpm store path
- 更改包存储位置pnpm config set store-dir 需要存储的位置 --global

