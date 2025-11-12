# 网络sys-socket.h

## 6.1. socket

位于`#include <sys/socket.h>`中，以下函数 Linux 系统调用函数（除了send，recv）

```
socket()                    : 创建套接字
bind()                      : 绑定套接字到本地地址
listen()                    : 监听网络连接
accept()                    : 接受网络连接
connect()                   : 连接到远程主机

send(), recv()              : 发送和接收数据（面向连接的套接字）
sendto(), recvfrom()        : 发送和接收数据（无连接的套接字）

// 用read ，write方法来读和写也行，适用范围更广

close() ,shutdown()         : 关闭套接字
getsockopt(), setsockopt()  : 获取和设置套接字选项
```

```
recv() 是一个用户空间的函数，recvfrom() 是一个系统调用函数

ssize_t recv(int sockfd, void *buf, size_t len, int flags) {
    return recvfrom(sockfd, buf, len, flags, NULL, NULL);
}
ssize_t recvfrom(int sockfd, void *buf, size_t len, int flags, struct sockaddr *src_addr, socklen_t *addrlen);
```

### 基本使用

创建一个服务端

```
int main(){
    // AF_INET：ipv4 地址；SOCK_STREAM：面向 TCP 的 socket stream,protocol；认协议，根据前面的选择而来 TCP
    int socket_fd = socket(AF_INET, SOCK_STREAM, 0);

    struct sockaddr_in server_addr = {0};
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(55660);
    server_addr.sin_addr.s_addr = INADDR_ANY; // 0 = 0.0.0.0

    if(bind(socket_fd, (struct sockaddr *)&server_addr, sizeof(server_addr)) == -1) {
        perror("绑定失败\n");
    }

    if (listen(socket_fd, 255) == -1) {
        perror("监听失败\n");
    }

    int client_fd = accept(socket_fd, NULL, NULL);
    if (client_fd == -1) {
        perror("接收失败\n");
    }

    printf("等待客户端输入....\n");
    fflush(stdout);
    char buf[50];
    /*
        read方法，recv方法都能够从socket中读取数据，但是recv方法是专门用来读取socket中数据的函数
        相比read方法有更多的参数，read方法是一个通用的系统调用
    */
    read(client_fd, buf, sizeof(buf));

    printf("%s", buf);
    fflush(stdout);
    return 0;
}
```

Java 客户端

```
public static void main(String[] args) throws IOException {
    Socket socket = new Socket("192.168.75.128", 55660);

    OutputStream outputStream = socket.getOutputStream();
    PrintWriter writer = new PrintWriter(outputStream);
    
    writer.println("this is java client");
    writer.flush();
}
```

### 简答原理图

![](https://cdn.nlark.com/yuque/0/2024/webp/33704534/1723045733932-45dd0851-1c33-4545-98b8-4617a94a5222.webp)

以 read 为例

**第一步：等待网络数据的到来**

当网络数据到达时，网络接口卡（NIC）首先通过直接内存访问（DMA）将数据传输到内核空间分配的 socket 接收缓冲区中，无需 CPU 参与。

**第二步：CPU 复制数据至用户空间**

一旦数据通过 DMA 传输到内核的 socket 接收缓冲区，用户进程的 read 系统调用会被唤醒（如果它在等待数据的话）。接下来，CPU 会介入，将数据从内核缓冲区复制到用户空间提供的缓冲区中。

也就是说，在 I/O 操作的过程中，**存在两个潜在的等待时间点 ：一个是等待网络数据到达 socket 接收缓冲区，另一个是等待 CPU 复制数据至用户空间。**

![](https://cdn.nlark.com/yuque/0/2024/jpeg/33704534/1723047445483-d7900fe2-b5c1-4917-a349-0ff4416e1ee1.jpeg)

### listen()方法

`listen` 方法的作用是将一个已创建的套接字（socket）设置为监听状态，以便接受传入的连接请求。具体来说，它有以下几个关键作用：

1. **设置监听状态**：在调用 `listen` 之前，套接字处于未连接状态。调用 `listen` 后，该套接字可以接受客户端的连接请求。
2. **定义连接队列长度**：`listen` 的第二个参数指定了未决连接的最大数量，也就是允许排队的连接请求的数量。当客户端尝试连接时，如果在此队列中的连接数已满，新的连接请求可能会被拒绝。
3. **为后续的** `**accept**` **准备**：`listen` 方法的主要目的是准备好接收客户端的连接。之后，程序通常会调用 `accept` 函数来实际接受连接。

#### 原理

```
#include <sys/socket.h>
// 成功返回0,错误返回-1,同时错误码设置在errno
int listen(int sockfd, int backlog);
```

注意，这边的 listen 调用是被 glibc 的 INLINE_SYSCALL 装过一层，其将返回值修正为只有0和-1这两个选择，同时将错误码的绝对值设置在errno内。 这里面的 backlog 是个非常重要的参数，如果设置不好，是个很隐蔽的坑。

对于java开发者而言，基本用的现成的框架，而java本身默认的 backlog 设置大小只有50。这就会引起一些微妙的现象，public ServerSocket(int port) throws IOException {this(port, 50, null);}

在 `sysdeps/unix/sysv/linux/listen.c`（GNU源代码中）

```
int
listen (int fd, int backlog)
{
#ifdef __ASSUME_LISTEN_SYSCALL
  return INLINE_SYSCALL (listen, 2, fd, backlog);
#else
  return SOCKETCALL (listen, fd, backlog);
#endif
}
weak_alias (listen, __listen);
```

- **系统调用号**：`INLINE_SYSCALL` 会生成一个系统调用号，并将其与参数一起传递给内核
- **内核入口**：内核接收到系统调用请求后，检查系统调用号，找到对应的处理函数。对于 `listen`，这将是 `SYSCALL_DEFINE2(listen, int, fd, int, backlog)` 的实现

在 `net/socket.c`中

```
SYSCALL_DEFINE2(listen, int, fd, int, backlog)
{
	return __sys_listen(fd, backlog);
}
```

在 `net/socket.c`中

```
int __sys_listen(int fd, int backlog)
{
	struct socket *sock;
	int err, fput_needed;

	sock = sockfd_lookup_light(fd, &err, &fput_needed);
	if (sock) {
		err = __sys_listen_socket(sock, backlog);
		fput_light(sock->file, fput_needed);
	}
	return err;
}
```

在 `net/socket.c`中

```
int __sys_listen_socket(struct socket *sock, int backlog)
{
	int somaxconn, err;

	somaxconn = READ_ONCE(sock_net(sock->sk)->core.sysctl_somaxconn);
	if ((unsigned int)backlog > somaxconn)
		backlog = somaxconn;

	err = security_socket_listen(sock, backlog);
	if (!err)
		err = READ_ONCE(sock->ops)->listen(sock, backlog); //sock->ops->listen对应的方法为inet_listen
	return err;
}
```

以上调用关系如下

```
listen
	|->INLINE_SYSCALL(listen......)
		|->SYSCALL_DEFINE2(listen, int, fd, int, backlog)
			/* 检测对应的描述符fd是否存在，不存在，返回-BADF
			|->sockfd_lookup_light
			/* 限定传过来的backlog最大值不超出 /proc/sys/net/core/somaxconn
			|->if ((unsigned int)backlog > somaxconn) backlog = somaxconn
			|->sock->ops->listen(sock, backlog) <=> inet_listen
            
            
```

值得注意的是，Kernel对于我们传进来的backlog值做了一次调整，让其无法>内核参数设置中的somaxconn

inet_listen

核心调动程序 **inet_listen** 在 `net/ipv4/af_inet.c`

```
int inet_listen(struct socket *sock, int backlog)
{
	struct sock *sk = sock->sk;
	int err = -EINVAL;

	lock_sock(sk);

	if (sock->state != SS_UNCONNECTED || sock->type != SOCK_STREAM)
		goto out;

	err = __inet_listen_sk(sk, backlog);

out:
	release_sock(sk);
	return err;
}
```

这里github上给的源码与文章中的[https://heapdump.cn/article/2300883](https://heapdump.cn/article/2300883)不一样。。。。。，看不懂了，等日后分析

#### 半连接&全连接队列

- **全连接队列（Completed Connection Queue）**

全连接队列也被称为已完成连接队列，用于存储已经建立好三次握手的连接。当服务器通过`accept()`函数接受了客户端的连接请求后，该连接会在全连接队列中等待被服务器进程处理。

- **半连接队列（Half-Open Connection Queue）**

半连接队列也称为未完成连接队列，用于存储那些已经收到客户端连接请求并发送了 SYN+ACK 响应，但服务器还没有执行完全的三次握手建立连接的请求。这些连接处于半开放状态，等待服务器进程继续完成连接建立。

半连接队列为了防止 syn + ack 攻击？

- `**listen()**`**函数中的**`**backlog**`**参数**

`listen()`函数中的`backlog`参数指定了服务器正在处理的连接队列的最大长度，即全连接队列的长度。这个参数影响着服务器能够同时处理的等待连接的数量。

backlog 的值含义从来就没有被严格定义过。原先 Linux 实现中，backlog 参数定义了该套接字对应的未完成连接队列的最大长度 （pending connections)。如果一个连接到达时，该队列已满，客户端将会接收一个 ECONNREFUSED 的错误信息，如果支持重传，该请求可能会被忽略，之后会进行一次重传。

从 Linux 2.2 开始，backlog 的参数内核有了新的语义，它现在定义的是已完成连接队列的最大长度，表示的是已建立的连接（established connection），正在等待被接收（accept 调用返回），而不是原先的未完成队列的最大长度。现在，未完成队列的最大长度值可以通过 /proc/sys/net/ipv4/tcp_max_syn_backlog 完成修改，默认值为 128。至于已完成连接队列，如果声明的 backlog 参数比 /proc/sys/net/core/somaxconn 的参数要大，那么就会使用我们声明的那个值。实际上，这个默认的值为 128。注意在 Linux 2.4.25 之前，这个值是不可以修改的一个固定值，大小也是 128。

设计良好的程序，在 128 固定值的情况下也是可以支持成千上万的并发连接的，这取决于 I/O 分发的效率，以及多线程程序的设计。在后面的性能篇里，我们的目标就是设计这样的程序。

#### 总结图

![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1723051413776-e2b710c5-007f-4133-ab72-318334702b9e.png)

### sokcet 缓冲区

socket 缓冲区用于临时存储从网络接收或即将发送的数据。具体来说，socket 缓冲区分为接收缓冲区和发送缓冲区：

- **接收缓冲区**：用于存储从网络接收到的数据，直到应用程序读取它们。数据到达网络接口时，首先会被存放在接收缓冲区中。应用程序调用 recv 或类似的读取函数时，会从这个缓冲区中提取数据。

- **发送缓冲区**：用于存储应用程序要发送到网络的数据。应用程序调用 send 或类似的写入函数时，数据会被复制到发送缓冲区，然后操作系统负责将这些数据发送到网络。

这两个缓冲区的大小是可配置的，默认值取决于操作系统的设置。可以使用 `getsockopt` 和 `setsockopt` 函数来获取和设置缓冲区大小。

```
int getsockopt(int socket, int level, int optname, void *optval, socklen_t *optlen);
```

**参数说明**

1. `**socket**`：

- socket 的文件描述符，表示你想要获取选项的 socket。

2. `**level**`：

- 指定要获取的选项的级别。通常，对于 socket 选项，这个参数使用 `SOL_SOCKET`，表示获取 socket 本身的选项。

3. `**optname**`：

- 具体的选项名称，指定你要获取的选项。例如：

- `SO_RCVBUF`：接收缓冲区的大小。
- `SO_SNDBUF`：发送缓冲区的大小。
- 其他选项如 `SO_REUSEADDR`、`SO_KEEPALIVE` 等。

4. `**optval**`：

- 一个指向存储选项值的缓冲区的指针。在调用 `getsockopt` 后，这个缓冲区将会被填充为当前选项的值。

5. `**optlen**`：

- `optlen` 是一个指向 `socklen_t` 类型的变量的指针，用于指定和接收 socket 选项值的缓冲区大小。
- **调用** `**setsockopt**`：在调用 `setsockopt` 时，你需要提供 `optlen`，表示你为 `optval` 分配的空间大小。这个函数会将你提供的值应用到指定的 socket 选项中。
- **调用** `**getsockopt**`：在调用 `getsockopt` 时，你也需要提供 `optlen`，在这种情况下，它表示你希望接收的选项值的缓冲区大小。调用后，它会更新为实际填充到 `optval` 中的选项值的大小。

6. 函数成功时返回0，‌失败时返回-1并设置相应的错误码。‌

下面采用代码，说明 socket 缓冲区

```
int socketfd = socket(AF_INET, SOCK_STREAM, 0);
if (socketfd < 0) {
    perror("创建socket失败");
}
int optval;
socklen_t optlen = sizeof(optval); // 用于指定和返回选项值的长度
if (getsockopt(socketfd, SOL_SOCKET, SO_RCVBUF, &optval, &optlen) == -1) {
    perror("获取失败");
}
printf("%d\n", optval);
fflush(stdout);
return 0;
```

运行结果

```
131072
```

### 设置缓冲区大小

1. optval > sysctl_rmem_max，则设置为最大值的2倍：2 * sysctl_wmem_max；
2. optval * 2 < SOCK_MIN_SNDBUF，则设置成最小值：SOCK_MIN_SNDBUF
3. optval < sysctl_rmem_max，且 val*2 > SOCK_MIN_SNDBUF，则设置成 2 * val。

```
存放接收缓冲区最大值的位置：cat /proc/sys/net/core/rmem_max
    212992
存放发送缓冲区最大值的位置：cat /proc/sys/net/core/wmem_max
    212992
```

```
int socketfd = socket(AF_INET, SOCK_STREAM, 0);
if (socketfd < 0) {
    perror("创建socket失败");
}

int optval = 3000; // 3000 字节
socklen_t optlen = sizeof(optval);
if (setsockopt(socketfd, SOL_SOCKET, SO_RCVBUF, &optval, optlen) == -1) {
    perror("设置失败");
}
if (getsockopt(socketfd, SOL_SOCKET, SO_RCVBUF, &optval, &optlen) == -1) {
    perror("获取失败");
}
printf("%d\n", optval);
fflush(stdout);
return 0;
```

运行结果

```
6000
```

### accept()方法

`accept()` 的主要作用是从监听队列中取出一个连接请求并创建一个新的 socket 来处理这个连接。

监听队列（也称为连接队列）是操作系统内核维护的一个数据结构，它用于存储等待被接受的新连接请求。当一个 socket 被设置为监听模式（通过调用 `listen()` 函数）后，任何客户端尝试与该 socket 建立连接时，它们的连接请求都会被放置到这个监听队列中，直到服务器端通过 `accept()` 函数处理这些请求。

**监听队列的工作原理：**

1. **监听模式**：首先，服务器程序需要创建一个套接字，并将其设置为监听模式，通常是通过调用 `listen()` 函数来完成。这使得套接字准备好接收连接请求。
2. **客户端发起连接**：客户端尝试与服务器通信时，它会通过 `connect()` 函数尝试建立连接。此时，如果服务器端的套接字处于监听模式，连接请求会被捕获，并加入到监听队列中。
3. **接受连接**：服务器端的应用程序会调用 `accept()` 函数来处理监听队列中的连接请求。每当 `accept()` 被调用时，如果监听队列中有可用的连接请求，那么这个请求会被移除，并为这个连接创建一个新的套接字描述符。这个新套接字将用于后续的数据传输。
4. **数据传输**：一旦连接被接受，服务器和客户端就可以通过新创建的套接字进行数据交换。

**谁来监听：**

- **操作系统内核**：监听队列实际上是操作系统内核的一部分，内核负责维护这个队列，并确保连接请求被正确地处理。
- **服务器应用程序**：服务器应用程序通过调用 `listen()` 和 `accept()` 来配置和监听连接请求。服务器程序通常在一个无限循环中调用 `accept()`，以便能够持续处理新的连接请求。

### read()方法

```
ssize_t read(int fd, void *buf, size_t count);
```

**参数说明：**

- `**int fd**`: 文件描述符，表示要读取的文件或设备的标识符。通常通过调用 `open()` 函数获取。
- `**void *buf**`: 指向一个内存缓冲区的指针，读取的数据将存储在这个缓冲区中。该缓冲区必须足够大，以容纳要读取的数据。
- `**size_t count**`: 要读取的字节数，即希望从文件中读取多少字节的数据。通常，这个值应该小于或等于缓冲区的大小。

**返回值：**

- 成功时，`read()` 返回实际读取的字节数（类型为 `ssize_t`），可能少于请求的字节数。如果到达文件末尾，返回值可能为 0。
- 失败时，返回 -1，并且 `errno` 会被设置为指示错误类型。

而是用于读取从 `client_fd`（一个已经建立的客户端连接的 socket ）中接收到的数据。

**原理说明：**

1. **读取数据**：

- `read` 函数会尝试从 `client_fd` 的读取缓冲区中获取数据，并将数据存储在 `buf` 中。
- 如果缓冲区中有数据可供读取，`read` 将返回读取的字节数。如果没有数据可读，它会阻塞（等待数据到达）直到有数据可用，除非 `client_fd` 连接关闭。

2. **阻塞行为**：

- 在没有数据的情况下，`read` 将会阻塞，直到有数据可供读取或发生错误。因此，可以说 `read` 是在“等待”或“阻塞”直到数据可用，但这与监听（如 `listen` 或 `select`）的概念不同。
- 如果你想要在不阻塞的情况下检查 `client_fd` 是否有数据可以读取，你可以使用 `select`、`poll` 或 `epoll` 等机制。

3. **读取后的数据处理**：

- 一旦 `read` 成功返回，数据就被复制到了 `buf` 中，可以对其进行处理。

### write()方法

`write` 方法用于向套接字发送数据。在 C 语言的 socket 编程中，它用于将数据写入一个连接的套接字，通常是客户端或服务器端的套接字。`write` 函数的基本用法和语法如下：

```
ssize_t write(int fd, const void *buf, size_t count);
```

**参数解释**

- `**fd**`：要写入的文件描述符（套接字文件描述符），通常是通过 `socket` 和 `accept` 创建的套接字。
- `**buf**`：指向要发送数据的缓冲区的指针。
- `**count**`：要写入的字节数。

**返回值**

- 如果成功，`write` 返回实际写入的字节数。
- 如果发生错误，返回 -1，并设置 `errno` 以指示错误类型。

### 6.1.1. 参考链接

- [https://www.cnblogs.com/xiaokang-coding/p/18024684](https://www.cnblogs.com/xiaokang-coding/p/18024684)
- [https://heapdump.cn/article/2300883](https://heapdump.cn/article/2300883)
- [https://blog.csdn.net/weixin_50448879/article/details/135049118](https://blog.csdn.net/weixin_50448879/article/details/135049118)
- [https://mp.weixin.qq.com/s?__biz=MjM5Njg5NDgwNA==&mid=2247484834&idx=1&sn=b8620f402b68ce878d32df2f2bcd4e2e&scene=21#wechat_redirect](https://mp.weixin.qq.com/s?__biz=MjM5Njg5NDgwNA==&mid=2247484834&idx=1&sn=b8620f402b68ce878d32df2f2bcd4e2e&scene=21#wechat_redirect)

## 6.2. 同步阻塞网络

首先我们来看下面这段代码，创建了一个 socket 客户端

```
int main()
{
    int sk = socket(AF_INET, SOCK_STREAM, 0);
    connect(sk, ...);
    recv(sk, ...);
}
```

下面是这段代码的大概原理图

![](https://cdn.nlark.com/yuque/0/2024/webp/33704534/1723283021952-c9bfb852-9830-4086-a29d-f550a7b4ee29.webp)

### 6.2.1. 创建socket时内核的工作

在我们创建 socket 时，内核已经在内部创建了一系列的 socket 相关内核对象，下图是简化版

![](https://cdn.nlark.com/yuque/0/2024/webp/33704534/1723283103172-7f2a5cd4-00d3-4653-82d9-339a66969462.webp)

在 `net/socket.c`中

```
SYSCALL_DEFINE3(socket, int, family, int, type, int, protocol)
{
	return __sys_socket(family, type, protocol);
}
```

```
int __sys_socket(int family, int type, int protocol)
{
	struct socket *sock;
	int flags;

	sock = __sys_socket_create(family, type,
				   update_socket_protocol(family, type, protocol));
	if (IS_ERR(sock))
		return PTR_ERR(sock);

	flags = type & ~SOCK_TYPE_MASK;
	if (SOCK_NONBLOCK != O_NONBLOCK && (flags & SOCK_NONBLOCK))
		flags = (flags & ~SOCK_NONBLOCK) | O_NONBLOCK;

	return sock_map_fd(sock, flags & (O_CLOEXEC | O_NONBLOCK));
}
```

```
static struct socket *__sys_socket_create(int family, int type, int protocol)
{
	struct socket *sock;
	int retval;

	/* Check the SOCK_* constants for consistency.  */
	BUILD_BUG_ON(SOCK_CLOEXEC != O_CLOEXEC);
	BUILD_BUG_ON((SOCK_MAX | SOCK_TYPE_MASK) != SOCK_TYPE_MASK);
	BUILD_BUG_ON(SOCK_CLOEXEC & SOCK_TYPE_MASK);
	BUILD_BUG_ON(SOCK_NONBLOCK & SOCK_TYPE_MASK);

	if ((type & ~SOCK_TYPE_MASK) & ~(SOCK_CLOEXEC | SOCK_NONBLOCK))
		return ERR_PTR(-EINVAL);
	type &= SOCK_TYPE_MASK;

	retval = sock_create(family, type, protocol, &sock);
	if (retval < 0)
		return ERR_PTR(retval);

	return sock;
}
```

```
int sock_create(int family, int type, int protocol, struct socket **res)
{
	return __sock_create(current->nsproxy->net_ns, family, type, protocol, res, 0);
}
EXPORT_SYMBOL(sock_create);
```

```
int __sock_create(struct net *net, int family, int type, int protocol,
			 struct socket **res, int kern)
{
	int err;
	struct socket *sock;
	const struct net_proto_family *pf;

    // ....

    // 分配sock对象
	sock = sock_alloc();    

    // 获得每个协议族的操作表
	pf = rcu_dereference(net_families[family]);

    //调用每个协议族创建函数，对于AF_NETT对应的是 inet_create 方法
     err = pf->create(net, sock, protocol, kern);

    
}
```

在`net/ipv4/af_inet.c`中

```
static int inet_create(struct net *net, struct socket *sock, int protocol,
		       int kern)
{
	struct sock *sk;

	if (protocol < 0 || protocol >= IPPROTO_MAX)
		return -EINVAL;

	sock->state = SS_UNCONNECTED;

	/* 查找对应的协议，对于TCP SOCK_STREAM 就是获取到了 */
lookup_protocol:
    // ....
	list_for_each_entry_rcu(answer, &inetsw[sock->type], list)
    // ....

    // 将 inet_stream_ops 赋值给 socket -> ops 
	sock->ops = answer->ops;
    // 获得 tcp_prot
	answer_prot = answer->prot;

    // ....
    // 分配 sock 对象，并把 tcp_prot 赋值给 sock -> sk_prot上
	sk = sk_alloc(net, PF_INET, GFP_KERNEL, answer_prot, kern);
    // ....
}
```

在 inet_create 中，根据类型 SOCK_STREAM 查找到对于 tcp 定义的操作方法实现集合 inet_stream_ops 和 tcp_prot。并把它们分别设置到 socket->ops 和 sock->sk_prot 上。  
![](https://cdn.nlark.com/yuque/0/2024/webp/33704534/1723285504519-e35617bf-5aa3-4f46-9fa8-315cd9bb991f.webp)

我们再往下看到了 sock_init_data。在这个方法中将 sock 中的 sk_data_ready 函数指针进行了初始化，设置为默认 sock_def_readable()。

![](https://cdn.nlark.com/yuque/0/2024/webp/33704534/1723285565088-1a8f6833-71c0-4bdd-b8fd-963ca625570b.webp)

在 `net/core/sock.c`中

```
// 没找到，应该是更新了
void sock_init_data(struct socket *sock, struct sock *sk) 
{
    sk->sk_data_ready   =   sock_def_readable;
    sk->sk_write_space  =   sock_def_write_space;
    sk->sk_error_report =   sock_def_error_report;
}
```

当软中断上收到数据包时会通过调用 sk_data_ready 函数指针（实际被设置成了 sock_def_readable()） 来唤醒在 sock 上等待的进程。这个咱们后面介绍软中断的时候再说，这里记住这个就行了。

至此，一个 tcp对象，确切地说是 AF_INET 协议族下 SOCK_STREAM对象就算是创建完成了。这里花费了一次 socket 系统调用的开销

### 6.2.2. 继续整理......

[https://mp.weixin.qq.com/s?__biz=MjM5Njg5NDgwNA==&mid=2247484834&idx=1&sn=b8620f402b68ce878d32df2f2bcd4e2e&scene=21#wechat_redirect](https://mp.weixin.qq.com/s?__biz=MjM5Njg5NDgwNA==&mid=2247484834&idx=1&sn=b8620f402b68ce878d32df2f2bcd4e2e&scene=21#wechat_redirect)

[https://cloud.tencent.com/developer/article/2188691](https://cloud.tencent.com/developer/article/2188691)![](https://cdn.nlark.com/yuque/0/2024/jpeg/33704534/1723287053331-426c0bf4-8c1c-4c49-b9b4-1aca18a7a836.jpeg)

![](https://cdn.nlark.com/yuque/0/2024/webp/33704534/1723285953234-12faeee0-46bf-48ed-9196-096cd543ee48.webp)

## 6.3. IO 多路复用/转接

IO 多路复用就是使用 IO 多路复用转接函数委托内核检测服务端文件描述符

### 6.3.1. select()

#### 简单使用

```
#include<sys/select.h>
struct timeval {
    time_t 		tv_sec;
    suseconds_t tv_usec;
}
int select (int __nfds, 
            fd_set *__restrict __readfds,
    		fd_set *__restrict __writefds,
    		fd_set *__restrict __exceptfds,
    		struct timeval *__restrict __timeout);
```

- `nfds`: 要监视的文件描述符集中的最大文件描述符加1

- 内核需要线性遍历这些集合中的文件描述符，这个值是循环结束的条件
- 在 windows 中这个参数是无效的，指定为 -1 即可

- `readfds`: 用于检查可读性的文件描述符集
- `writefds`: 用于检查可写性的文件描述符集。
- `exceptfds`: 用于检查异常条件的文件描述符集。
- `timeout`: 等待的超时时间。

```
int main(){
    int socketfd = socket(AF_INET, SOCK_STREAM, 0);
    if (socketfd == -1) {
        perror("创建socket失败");
        exit(1);
    }

    struct sockaddr_in server_addr = {0};
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(8080);
    server_addr.sin_addr.s_addr = INADDR_ANY; // 0 = 0.0.0.0
    if (bind(socketfd, (struct sockaddr *) &server_addr, sizeof(server_addr)) == -1) {
        perror("绑定端口失败");
        exit(1);
    }

    if (listen(socketfd, 255) == -1) {
        perror("监听失败");
        exit(1);
    }

    fd_set fds;

    // 将fds数组中所有位置清0
    FD_ZERO(&fds);

    // 将要添加监听的文件描述符添加到集合中
    FD_SET(socketfd, &fds);


    int maxfd = socketfd;

    while (1) {
        fd_set tmp = fds;

        // 调用 select 监听这些文件描述符
        // 传入一个副本，防止要监听的fds集合被修改
        int ret = select(maxfd + 1, &tmp, NULL, NULL, NULL);

        // 遍历文件描述符集合
        for (int i = 0; i <= maxfd; ++i) {
            // FD_ISSET 检查指定的文件描述符是否处于已设置状态（准备好IO操作）
            if (FD_ISSET(i, &tmp)) {
                if (socketfd == i) {
                    // socketfd准备好 iO
                    int cfd = accept(socketfd, NULL, NULL);
                    FD_SET(cfd, &fds);
                    maxfd = cfd > maxfd ? cfd : maxfd;
                    printf("v_1.0有客户端建立连接,fd=%d\n", cfd);
                } else {
                    // 客户端fd 准备好IO
                    char buf[1024];
                    int len = recv(i, buf, sizeof(buf) - 1, 0); // 减去1是为了留出一个位置给\0
                    if (len == -1) {
                        perror("接受客户端消息出错\n");
                    } else if (len == 0) {
                        printf("fd=%d，客户端已经断开连接\n", i);
                        FD_CLR(i, &fds);
                        close(i);
                        break;
                    }
                    buf[len] = '\0'; // 将 \0 之前的字符全部打印
                    printf("fd:%d，客户端消息:%s", i, buf);
                    char msg[] = "接收到数据\n";
                    if (send(i, msg, strlen(msg) + 1, 0) == -1) {
                        perror("发送数据失败\n");
                    }
                }
            }
        }
    }
}
```

#### `select` 的底层实现原理

![](https://cdn.nlark.com/yuque/0/2024/jpeg/33704534/1723373293621-2b63bf3f-e92a-4f8b-a392-1d468ccb7f3c.jpeg)

1. **遍历文件描述符集**： 当 `select` 被调用时，内核会遍历文件描述符集合中的文件描述符，并将它们添加到等待队列中。内核为每个文件描述符设置一个等待事件，比如等待文件变得可读、可写或出现异常。
2. **进入休眠**： 如果没有文件描述符立即满足条件，`select` 使调用进程进入休眠状态，直到文件描述符集中的某一个文件描述符的状态发生变化，或者超时时间到达。
3. **唤醒与返回**： 当一个或多个文件描述符的状态发生变化时，内核唤醒休眠中的进程，并更新相应的文件描述符集合，以表明哪些文件描述符已经准备好执行 I/O 操作。`select` 返回准备好的文件描述符数量。

**源码讲解：**[https://blog.csdn.net/qq_37058442/article/details/78004507](https://blog.csdn.net/qq_37058442/article/details/78004507)

#### fd_set 结构体

```
typedef long int __fd_mask;
#define __FD_SETSIZE		1024
#define __NFDBITS	(8 * (int) sizeof (__fd_mask))

typedef struct
  {
    __fd_mask __fds_bits[__FD_SETSIZE / __NFDBITS];
  } fd_set;

// 结合上面的定义我们可以看出，在ubuntu中sizeof(long int) = 8
// __NFDBITS == (8 * (int) sizeof (__fd_mask)) == (8 * (int) sizeof (long int)) == 64
// [__FD_SETSIZE / __NFDBITS] = 1024 / 64 = 16
{
    long int _fds_bits[16]
}
```

下图 fd_set 中存储了要委托内核检测的文件描述符集合

- 如果集合标志位为0，代表不检测这个文件描述符
- 如果集合标志位为1，代表检测这个文件描述符状态

![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1723291438871-05263edc-2ca9-4c3b-84fd-e615d13001e8.png)

内核在遍历这个读集合的过程中，如果被检测的文件描述符对应的读缓冲区中沿有数据，内核将修改这个文件描述符在读集合fd_set中对应的标志位，改为0，如果有数据那么这个标志位的值不变还是1。

#### FD_SET宏定义

```
#define	FD_SET(fd, fdsetp)	__FD_SET (fd, fdsetp)

#define __FD_SET(d, set) \
  ((void) (__FDS_BITS (set)[__FD_ELT (d)] |= __FD_MASK (d)))

# define __FDS_BITS(set) ((set)->__fds_bits)

// __FDS_BITS (set) 其实就是 fd_set 结构体中的 fds_bits 数组
// __FD_ELT 用来计算索引的值
// __FD_MASK用来计算索引对应位置的值
```

**__FD_ELT**

```
__FD_ELT 
    ==> (d) / ( 8 * (int) sizeof (long int)) //(8 * (int) sizeof (long int)) 在 ubuntu 中为 64
    ==> (d) / 64
```

**__FD_MASK**

```
__FD_MASK 
    ==> ((long int) (1UL << (d) % ( 8 * (int) sizeof (long int)) ) )
    ==> ((long int) (1UL << (d) % 64 ) )
```

我们可以看出FD_SET宏的具体实现原理

```
// d 代表文件描述符
fds_bits [d / 64] |= ((long int) (1UL << (d) % 64 ) )
```

```
FD_SET(0, &fds);  // fds_bits[0] = 1    对应二进制 1
FD_SET(1, &fds);  // fds_bits[0] = 3    对应二进制 11
FD_SET(2, &fds);  // fds_bits[0] = 7    对应二进制 111
FD_SET(4, &fds);  // fds_bits[0] = 23   对应二进制 10111
...
//每监听一个文件描述符就将对应的 bit_position 设置为1
```

#### FD_ISSET宏定义

```
#define	FD_ISSET(fd, fdsetp)	__FD_ISSET (fd, fdsetp)

#define __FD_ISSET(d, set) \
  ((__FDS_BITS (set)[__FD_ELT (d)] & __FD_MASK (d)) != 0)

//由前面可知
// __FDS_BITS (set) ==> fds_bits
// __FD_ELT ==> d / 64
// __FD_MASK ==> ((long int) (1UL << (d) % 64 ) )

fds_bits[d / 64]  & ((long int) (1UL << (d) % 64 ) ) != 0
```

### 6.3.2. poll()

![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1723401623630-fc052e04-1aea-4f1a-ab55-e149ce32d9c7.png)![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1723401640250-89785d70-fd04-44c1-aa06-41bc0f5f0ed3.png)

```
int socketfd = socket(AF_INET, SOCK_STREAM, 0);
if (socketfd == -1) {
    perror("创建socket失败");
    exit(1);
}

struct sockaddr_in server_addr = {0};
server_addr.sin_family = AF_INET;
server_addr.sin_port = htons(8080);
server_addr.sin_addr.s_addr = INADDR_ANY; // 0 = 0.0.0.0
if (bind(socketfd, (struct sockaddr *) &server_addr, sizeof(server_addr)) == -1) {
    perror("绑定端口失败");
    exit(1);
}

if (listen(socketfd, 255) == -1) {
    perror("监听失败");
    exit(1);
}

struct pollfd fds[1024];

for (int i = 0; i < 1024; ++i) {
    fds[i].fd = -1; // 将 fds 中 fd 的初始值设置为 -1
    fds[i].events = POLLIN; // 监听这些文件描述符的POLLIN事件
}
fds[0].fd = socketfd; // 监听 socketfd

int maxfd = 0;

while (1) {
    // 内核监听fds数组中的文件描述符
    int ret = poll(fds, maxfd + 1, -1);

    // 检查是不是 socketfd 的事件
    if (fds[0].revents & POLLIN) {
        int clientfd = accept(socketfd, NULL, NULL);
        printf("v_1.1有客户端建立连接,fd=%d\n", clientfd);
        // 将 clientfd 添加到 fds 中
        for (int i = 0; i < 1024; ++i) {
            if (fds[i].fd == -1) {
                fds[i].fd = clientfd;
                fds[i].events = POLLIN; // 监听客户端的读事件
                break;
            }
        }
        maxfd = maxfd > clientfd ? maxfd : clientfd;
    }

    // 检查是不是 clientfd 的事件
    for (int i = 1; i <= maxfd; ++i) {
        if (fds[i].revents & POLLIN) {
            // 客户端fd，发现读事件
            char buf[1024];
            int len = recv(fds[i].fd, buf, sizeof(buf) - 1, 0); // 减去1是为了留出一个位置给\0
            if (len == -1) {
                perror("接受客户端消息出错\n");
            } else if (len == 0) {
                printf("fd=%d，客户端已经断开连接\n", i);
                fds[i].fd = -1;
                close(fds[i].fd);
                break;
            } else {
                buf[len] = '\0'; // 将 \0 之前的字符全部打印
                printf("fd:%d，客户端消息:%s", fds[i].fd, buf);
                char msg[] = "接收到数据\n";
                if (send(fds[i].fd, msg, strlen(msg) + 1, 0) == -1) {
                    perror("发送数据失败\n");
                }
            }
        }
    }
}
```

### 6.3.3. epoll()

#### 简单使用

epoll 全称 event poll

```
int socketfd = socket(AF_INET, SOCK_STREAM, 0);
if (socketfd == -1) {
    perror("创建socket失败");
    exit(1);
}
struct sockaddr_in server_addr = {0};
server_addr.sin_family = AF_INET;
server_addr.sin_port = htons(8080);
server_addr.sin_addr.s_addr = INADDR_ANY; // 0 = 0.0.0.0
if (bind(socketfd, (struct sockaddr *) &server_addr, sizeof(server_addr)) == -1) {
    perror("绑定端口失败");
    exit(1);
}
if (listen(socketfd, 255) == -1) {
    perror("监听失败");
    exit(1);
}

// 创建一个 epoll 实例，返回对应的文件描述符
int epfd = epoll_create(1);
if (epfd == -1) {
    perror("创建红黑树失败");
    exit(1);
}

struct epoll_event ev;
ev.events = EPOLLIN; //监听读事件
ev.data.fd = socketfd;
epoll_ctl(epfd, EPOLL_CTL_ADD, socketfd, &ev);

struct epoll_event evs[1024];
int size = sizeof(evs) / sizeof(evs[0]);

while (1) {
    int ret = epoll_wait(epfd, evs, size, -1);
    for (int i = 0; i < ret; ++i) {
        int fd = evs[i].data.fd;
        if (fd == socketfd) {
            int cfd = accept(socketfd, NULL, NULL);
            ev.events = EPOLLIN; //监听读事件
            ev.data.fd = cfd;
            // 红黑树添加节点
            epoll_ctl(epfd, EPOLL_CTL_ADD, cfd, &ev);
            printf("v_1.epoll有客户端建立连接,fd=%d\n", cfd);
        } else {
            char buf[1024];
            int len = recv(fd, buf, sizeof(buf) - 1, 0);
            if (len == -1) {
                perror("接受客户端消息出错\n");
            } else if (len == 0) {
                printf("fd=%d，客户端已经断开连接\n", fd);
                // 删除红黑树上的节点
                epoll_ctl(epfd, EPOLL_CTL_DEL, fd, NULL);
                close(fd);
                break;
            }
            buf[len] = '\0'; // 将 \0 之前的字符全部打印
            printf("fd:%d，客户端消息:%s", fd, buf);
            char msg[] = "接收到数据\n";
            if (send(fd, msg, strlen(msg) + 1, 0) == -1) {
                perror("发送数据失败\n");
            }
        }
    }
}
```

#### epoll_create()

epoll_create 创建一个 eventpoll 内核对象

`fs/eventpoll.c`

```
struct eventpoll {

    // ....

	/* Wait queue used by sys_epoll_wait() */
    // 等待队列链表，存放阻塞的进程
	wait_queue_head_t wq;

    
	/* List of ready file descriptors */
    // 存放数据就绪的文件描述符
	struct list_head rdllist;


	/* RB tree root used to store monitored fd structs */
    // 红黑树，存放进程下所有添加进来的文件描述符以及相关的事件
	struct rb_root_cached rbr;

    // ....
};

```

![](https://cdn.nlark.com/yuque/0/2024/jpeg/33704534/1723540144178-7649b534-ac1d-47b5-bfb5-4309dacc69a8.jpeg)

#### epoll_ctl() & epoll_event

**epoll_event：**是一个用于封装文件描述符和监听事件的结构体

```
struct epoll_event
{
  uint32_t events;	/* Epoll events */
  epoll_data_t data;	/* User data variable */
} __EPOLL_PACKED;

typedef union epoll_data
{
  void *ptr;
  int fd;
  uint32_t u32;
  uint64_t u64;
} epoll_data_t;
```

**epoll_ctl：**可以向内核中的红黑树，添加、删除epoll_event

#### event_wait

1. [https://www.cnblogs.com/88223100/p/Deeply-learn-the-implementation-principle-of-IO-multiplexing-select_poll_epoll.html](https://www.cnblogs.com/88223100/p/Deeply-learn-the-implementation-principle-of-IO-multiplexing-select_poll_epoll.html)
2. **边缘触发与水平触发**:

- `epoll_wait` 支持边缘触发（Edge Triggered, ET）和水平触发（Level Triggered, LT）两种模式：

- **水平触发**：当某个文件描述符上有事件发生时，`epoll_wait` 会不断返回这个事件，直到事件被处理（**比如缓冲区的数据被处理干净了**）
- **边缘触发**：当某个文件描述符上有事件发生时，`epoll_wait` 只会通知一次，除非有新的事件发生。

### 6.3.4. 三者对比

|   |   |   |   |
|---|---|---|---|
||select|poll|epoll|
|数量限制|FD_SETSIZE 通常为1024|无|无|
|监视方式|采用固定大小的位图来表示要监视的文件描述符，在处理大量稀疏的文件描述符低效|使用一个结构体来监听存储文件描述符和事件，处理大量稀疏的文件描述符高效||
|文件描述符集合|会被修改|不会被修改||
|内核检测文件描述符方式|线性扫描|线性扫描||
|内核和用户空间交互|频繁|频繁|采用共享内存，减少内存拷贝|
||需要对文件描述符逐一判断|需要对文件描述符逐一判断|返回具体的文件描述符|

![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1723456058131-da1803b8-5791-4d57-98f8-e08d89e2dd77.png)

![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1723456040923-b905efe2-e4d8-4cba-b711-8b9408687e57.png)

### 6.3.5. 参考链接

- [https://www.bilibili.com/video/BV15X4y1Y7T9](https://www.bilibili.com/video/BV15X4y1Y7T9)
- [https://www.bilibili.com/video/BV1fg411376j](https://www.bilibili.com/video/BV1fg411376j)