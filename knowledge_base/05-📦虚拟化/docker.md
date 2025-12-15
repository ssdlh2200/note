# 常见问题
## docker tcp模式启动
[https://blog.csdn.net/qq_31387691/article/details/138630461](https://blog.csdn.net/qq_31387691/article/details/138630461)

# 安装/换源
## Ubuntu安装
1. 卸载可能存在的版本

```plain
sudo apt-get remove docker \
             docker-engine \
             docker-ce docker.io
```

2. 更新源

```plain
sudo apt-get update
```

3. 安装依赖

```plain
sudo apt install -y apt-transport-https ca-certificates curl software-properties-common
```

4. 为了确认所下载软件包的合法性，需要添加软件源的 GPG 密钥

```plain
curl -fsSL https://mirrors.aliyun.com/docker-ce/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg


# 官方源
# curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
```

5. 然后，我们需要向 sources.list 中添加 Docker 软件源

```plain
echo \
  "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://mirrors.aliyun.com/docker-ce/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null


# 官方源
# echo \
#   "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu \
#   $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
```

以上命令会添加稳定版本的 Docker APT 镜像源，如果需要测试版本的 Docker 请将 stable 改为 test。

6. 更新 apt 软件包缓存，并安装 docker-ce

```plain
sudo apt-get update

sudo apt-get install docker-ce docker-ce-cli containerd.io
```

7. 验证

```plain
docker -v
```
# 容器数据卷
![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1685434018194-ef2a8360-a109-448b-803a-9d15d8ba69c0.png)

+ 数据卷
    - 数据卷是宿主机中的一个目录或文件
    - 当容器目录和数据卷目录绑定后，对方的修改会立即同步
    - 一个数据卷可以被多个容器同时挂载
    - 一个容器也可以被挂载多个数据卷
+ 数据卷作用
    - 容器数据持久化
    - 外部机器和容器间接通信
    - 容器之间数据交换

### 配置数据卷
+ 创建启动容器时，使用-v参数设置数据卷

```java
docker run ... -v 宿主机目录:容器内目录

docker run -it --name=testDocker2 -v /root/data:/root/data_container centos:centos7 /bin/bash
```

注意事项： 

+ 目录必须是绝对路径
+ 如果目录不存在，会自动创建
+ 可以挂载多个数据卷

```java
docker run -it --name=testDocker2 -v /root/data:/root/data_container 
    							  -v /root/data:/root/data_container centos:centos7 /bin/bash
```

### 数据卷容器
![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1685443225073-2555bf75-ced1-49c1-aaa1-a0af522a0b81.png)

#### 配置数据卷容器
```java
docker run -it --name=c3 -v /volume centos:7 /bin/bash

docker run -it --name=c1 --volumes-from c3 centos:7 /bin/bash
docker run -it --name=c2 --volumes-form c3 centos:7 /bin/bash
```

:::info
数据卷小结

数据卷概念

1. 宿主机的一个目录或文件
2. 数据卷作用

容器数据持久化

1. 客户端和容器数据交换
2. 容器间数据交换
3. 数据卷容器

创建一个容器，挂载一个目录，让其他容器继承自该容器(--volume-from)。

1. 通过简单方式实现数据卷配置

:::

# 应用部署
[https://hub.docker.com](https://hub.docker.com) 可以查看镜像版本

## Docker Run命令
```java
docker run [OPTIONS] IMAGES [COMMAND] [ARGS]
```

[OPTIONS]说明：

+ -d: 后台运行容器，并返回容器ID
+ -i: 以交互模式运行容器，通常与 -t 同时使用
+ -t: 为容器重新分配一个伪输入终端，通常与 -i 同时使用
+ --name="nginx-lb": 为容器指定一个名称
+ --volume , -v: 绑定一个卷
+ -p: 指定端口映射，格式为：主机端口:容器端口
+ -P: 随机端口映射，容器内部端口随机映射到主机的端口

## mysql
1. 搜索mysql

```plain
docker search mysql
```

2. 拉取mysql5.7版本

```plain
docker pull mysql:5.7
```

3. 在本地创建存储数据的文件，

```plain
一般可以创建一个docker-data文件夹专门存放
mkdir mysql
cd mysql
mkdir log conf data
```

4. 创建并且启动容器

```java
docker run \
-it \
--name t_mysql \
-p 3306:3306 \
-v /docker-data/mysql/conf/:/etc/mysql/my.cnf \
-v /docker-data/mysql/log:/var/log/mysql \
-v /docker-data/mysql/data:/var/lib/mysql \
-e MYSQL_ROOT_PASSWORD=thisis@1 \
-d mysql:5.7
```

+ p：端口映射
+ v：将宿主机下的目录挂载到容器目录下
+ e：设置root账号的密码
+ d：后台运行

## rabbitmq
```c
docker run --name t_rabbitmq -p 5672:5672 -p 15672 15672 -d rabbitmq
```

## nacos
1. 搜索nacos

```plain
docker search nacos
```

2. 拉取nacos

```plain
docker pull nacos/nacos-server  #不写版本默认最新

#这里的/也是名称的一部分,完整名称是nacos/nacos-server
```

3. 创建conf目录

```java
mkdir -p nacos/conf
```

4. application.yml放到conf里面
+ [nacos-docker/build/conf at master · nacos-group/nacos-docker](https://github.com/nacos-group/nacos-docker/tree/master/build/conf)

```java
修改
db.url.0=jdbc:mysql://${MYSQL_SERVICE_HOST:8.130.42.209}:${MYSQL_SERVICE_PORT:3306}/${MYSQL_SERVICE_DB_NAME:mall_nacos}?${MYSQL_SERVICE_DB_PARAM:characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true&useSSL=false}
db.user.0=${MYSQL_SERVICE_USER:root}
db.password.0=${MYSQL_SERVICE_PASSWORD:thisis@1}
```

5. 在nacos的github上面找到sql文件，创建数据库，并且执行sql
6. 执行docker-compose文件

```java
version: "3.0"
services:
  nacos:
    image: nacos/nacos-server:v2.3.1
    container_name: t_nacos
    volumes:
      - /docker-data/nacos/logs:/home/nacos/logs
      - /docker-data/nacos/conf/application.properties:/home/nacos/conf/application.properties
    ports:
      - "8848:8848"
      - "9848:9848"
```



## Tomcat
![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1685448778154-58287304-a59c-4eda-8d85-6f96b587366b.png)

## Ngix
![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1685449163341-dfc95ed0-0879-4dde-b654-dcbf7742be91.png)

```java
user nginx;
worker_processes 1;

error_log /var/log/nginx/error.log warn;
pid /var/run/nginx.pid;


events{
	worker_connections 1024;
}


http{
    include /etc/nginx/mime.types;
	default_type application/octet-stream;
	log_format main '$remote_addr - $remote_user [$time_local] "$request" '
					'$status $body_bytes_sent "$http_referer" '
					'"$http_user_agent" "$http_x_forwarded_for"';

	access_log /var/log/nginx/access.log main;
	sendfile on;
	#tcp_nopush on;
	keepalive_timeout 65;
	#gzip on;

	include /etc/nginx/conf.d/*.conf;
}
```

```java
docker run -id --name=c_nginx \
-p 80:80 \
-v $PWD/conf/nginx.conf:/etc/nginx/nginx.conf \
-v $PWD/logs:/var/log/nginx \
-v $PWD/html:/usr/share/nginx/html \
nginx
```

参数说明

+ **-p 80:80**：将容器的80端口映射到宿主机的80端口。
+ -**v $PWD/conf/nginx.conf/etc/nginx/nginx.conf**：将主机当前目录下的/conf/nginx.conf挂载到容器的：/etc/nginx/nginx.conf。配置目录
+ **-v $PWD/logs:var/log/nginx**：将主机当前目录下的logs目录挂载到容器的var/log/nginx,日志目录

## Java
```java
docker run -it --name t_java17 \
-v /root/java17/:/usr/test/ \
-d openjdk:17
```

## Redis
1. 查找redis

```plain
docker search redis
```

2. 拉取redis

```plain
# 获取最新版本
docker pull redis
# 获取指定版本号
docker pull redis
```

3. 创建挂载目录并且进入

```plain
mkdir redis

cd redis
```

4. 下载redis.conf配置文件

```plain
wget http://download.redis.io/redis-stable/redis.conf
```

5. 权限

```java
chmod 777 redis.conf
```

6. 修改默认配置

```java
vi redis.conf

bind 127.0.0.1 # 这行要注释掉，解除本地连接限制
protected-mode no # 默认yes，如果设置为yes，则只允许在本机的回环连接，其他机器无法连接。
daemonize no # 默认no 为不守护进程模式，docker部署不需要改为yes，docker run -d本身就是后台启动，不然会冲突
requirepass 123456 # 设置密码
appendonly yes # 持久化
```

7. 创建并且启动容器

```java
docker run \
-it \
-p 6379:6379 \
--name t_redis \
-v /docker-data/redis/redis.conf:/etc/redis/redis.conf \
-v /docker-data/redis/redis-data:/data \
-d redis redis-server /etc/redis/redis.conf
```

+ <font style="color:rgb(51, 51, 51);">-d redis redis-server /etc/redis/redis.conf：表示后台启动redis，以配置文件启动redis，加载容器内的conf文件。</font>

## 
# DockerFile
## Docker镜像
1. **本质是什么？**
+ Docker中一个centos镜像为什么只有200MB,而一个centos操作系统的iso文件要几个个G?
+ Docker中一个tomcat镜像为什么有500MB,而一个
+ tomcat:安装包只有70多MB?
2. **docker镜像原理**

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1685450900546-ced96d74-0b8b-4909-84f6-c0d3814a9723.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1685451156609-abb2bbd0-ff19-4ecf-a59a-3ca1c01119b9.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1685451302852-770a8ac1-c9b1-437c-9d8a-9801d34e0cce.png)

## 镜像制作
1. 容器转为镜像

```java
docker commit 容器id 镜像名称:版本号
```

```java
docker save -o 压缩文件名称 镜像名称:版本号
```

```java
docker load -i 压缩文件名称
```

在容器中挂载的文件不能commit上去

在容器中创建的目录可以commit上去

2. dockerfile

本质就是一个文本文件

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1685452038934-1f5bfa6f-89be-4a3c-90de-f690e806b48e.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1685452250757-bf698f91-04b4-4446-8fec-1f323ec8d3df.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1685452266843-40f3a897-529a-41e9-959a-9cc884baadda.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1685452286244-c3d90058-d66d-44a0-a06b-99aa56012320.png)

## 部署springboot项目
1. 将jar包放到root/docker-file/目录
2. ![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1685452523713-364a7855-d1c3-4a58-b95d-590599dd408b.png)

```java
FROM java:8
MAINTAINER ssdlh
ADD shopping-server-1.0.jar app.jar
CMD java -jar app.jar
    


docker build -f ./shoppingserver_dockerfile -t app .
    
docker run -id -p 9090:9090 -p 9091:9091 --name=c_app \
-v $PWD/static/img:/static/img \
-v $PWD/static/xlsx:/static/xlsx \
app
    
docker logs -f -t --tail 100 c_app

```

# DockerCompose
| **Docker Compose 版本** | **配置文件格式版本** |
| :--- | :--- |
| <font style="color:rgb(13, 13, 13);">1.0</font> | <font style="color:rgb(13, 13, 13);">1</font> |
| <font style="color:rgb(13, 13, 13);">1.1</font> | <font style="color:rgb(13, 13, 13);">2</font> |
| <font style="color:rgb(13, 13, 13);">1.3</font> | <font style="color:rgb(13, 13, 13);">2</font> |
| <font style="color:rgb(13, 13, 13);">1.4</font> | <font style="color:rgb(13, 13, 13);">2</font> |
| <font style="color:rgb(13, 13, 13);">1.5</font> | <font style="color:rgb(13, 13, 13);">2</font> |
| <font style="color:rgb(13, 13, 13);">1.6</font> | <font style="color:rgb(13, 13, 13);">2</font> |
| <font style="color:rgb(13, 13, 13);">1.7</font> | <font style="color:rgb(13, 13, 13);">2</font> |
| <font style="color:rgb(13, 13, 13);">1.8</font> | <font style="color:rgb(13, 13, 13);">2</font> |
| <font style="color:rgb(13, 13, 13);">1.9</font> | <font style="color:rgb(13, 13, 13);">2</font> |
| <font style="color:rgb(13, 13, 13);">1.10</font> | <font style="color:rgb(13, 13, 13);">2</font> |
| <font style="color:rgb(13, 13, 13);">1.11</font> | <font style="color:rgb(13, 13, 13);">2.1</font> |
| <font style="color:rgb(13, 13, 13);">1.12</font> | <font style="color:rgb(13, 13, 13);">2.1</font> |
| <font style="color:rgb(13, 13, 13);">1.13</font> | <font style="color:rgb(13, 13, 13);">2.1</font> |
| <font style="color:rgb(13, 13, 13);">1.14</font> | <font style="color:rgb(13, 13, 13);">2.1</font> |
| <font style="color:rgb(13, 13, 13);">1.15</font> | <font style="color:rgb(13, 13, 13);">2.1</font> |
| <font style="color:rgb(13, 13, 13);">1.16</font> | <font style="color:rgb(13, 13, 13);">2.2</font> |
| <font style="color:rgb(13, 13, 13);">1.17</font> | <font style="color:rgb(13, 13, 13);">3.0</font> |
| <font style="color:rgb(13, 13, 13);">1.18</font> | <font style="color:rgb(13, 13, 13);">3.0</font> |
| <font style="color:rgb(13, 13, 13);">1.19</font> | <font style="color:rgb(13, 13, 13);">3.0</font> |
| <font style="color:rgb(13, 13, 13);">1.20</font> | <font style="color:rgb(13, 13, 13);">3.0</font> |
| <font style="color:rgb(13, 13, 13);">1.21</font> | <font style="color:rgb(13, 13, 13);">3.0</font> |
| <font style="color:rgb(13, 13, 13);">1.22</font> | <font style="color:rgb(13, 13, 13);">3.0</font> |
| <font style="color:rgb(13, 13, 13);">1.23</font> | <font style="color:rgb(13, 13, 13);">3.0</font> |
| <font style="color:rgb(13, 13, 13);">1.24</font> | <font style="color:rgb(13, 13, 13);">3.0</font> |
| <font style="color:rgb(13, 13, 13);">1.25</font> | <font style="color:rgb(13, 13, 13);">3.0</font> |
| <font style="color:rgb(13, 13, 13);">1.26</font> | <font style="color:rgb(13, 13, 13);">3.0</font> |
| <font style="color:rgb(13, 13, 13);">1.27</font> | <font style="color:rgb(13, 13, 13);">3.8</font> |
| <font style="color:rgb(13, 13, 13);">1.28</font> | <font style="color:rgb(13, 13, 13);">3.8</font> |
| <font style="color:rgb(13, 13, 13);">1.29</font> | <font style="color:rgb(13, 13, 13);">3.8</font> |
| <font style="color:rgb(13, 13, 13);">1.30</font> | <font style="color:rgb(13, 13, 13);">3.8</font> |
| <font style="color:rgb(13, 13, 13);">1.31</font> | <font style="color:rgb(13, 13, 13);">3.8</font> |
| <font style="color:rgb(13, 13, 13);">1.32</font> | <font style="color:rgb(13, 13, 13);">3.8</font> |
| <font style="color:rgb(13, 13, 13);">1.33</font> | <font style="color:rgb(13, 13, 13);">3.8</font> |


# 参考链接
+ [https://yeasy.gitbook.io/docker_practice/install/ubuntu](https://yeasy.gitbook.io/docker_practice/install/ubuntu)

