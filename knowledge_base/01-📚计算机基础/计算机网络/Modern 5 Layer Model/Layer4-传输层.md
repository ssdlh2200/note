# Layer4-传输层
## 传输层概念
1. 传输层主要作用
	- 传输层解决了
		- <font color="#d83931"><b>不同主机上进程之间的通信</b></font>
	- 物理层，数据链数层，网络层一起解决了
		- <font color="#d83931"><b>主机到主机之间的通信</b></font>
2. 传输层PDU（protocol data unit）
	- Segment
3. 传输层主要协议
	+ TCP（Transmission Control Protocol）
	    - 通信前需要建立连接
	    - TCP仅支持单播，一对一通信
	    - TCP对于上层交付的数据会保证数据的完整性和顺序
	    - 网页浏览HTTP，远程登录操作，数据库访问，文件传输FTP这些都是基于TCP协议的
	    - 面向字节流：数据被视为一个连续的字节序列，发送方和接收方之间没有明确的消息边界。数据可以是任意长度，接收方需要自行处理字节流的划分。
	+ UDP（User Datagram Protocol)
	    - 通信前不需要建立连接
	    - UDP支持单播，多播，以及广播（广播相比多播可以发给自身）
	    - UDP对于上层交付的数据并不会保证完整性和顺序
	    - UDP适合视频会议，音频流传输，实时性要求比较高的领域
	    - 面向报文：数据被视为独立的报文，发送方发送的每个数据报都有明确的边界。每个报文都是完整的实体，接收方按报文边界接收。

## PDU
> 以IPv4为例，IP头部的第9个字节是“协议字段”，它占8位，表示传输层协议的类型。例如：
> + 值为`6`表示报文的载荷是TCP协议。
> + 值为`17`表示报文的载荷是UDP协议。

### UDP-PDU


### TCP-PDU



## UDP、TCP区别
- UDP
	- 支持单播、多播以及广播
	- 面向报文
- TCP
	- 仅支持单播
	- 面向字节流
![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1710597304128-30413700-50bc-4ef3-95ef-27b131a7a33f.png)

![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1710597621462-3c3fa339-f19a-4c73-80dc-75425f88255e.png)

![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1710598284452-0439d46d-38c6-451d-a8a4-2fabe4c167c9.png)


# TCP
> TCP具体规范在RFC793中[https://datatracker.ietf.org/doc/html/rfc793#autoid-1](https://datatracker.ietf.org/doc/html/rfc793#autoid-1)
>

+ [https://www.bilibili.com/video/BV1oo4y1j7Eb](https://www.bilibili.com/video/BV1oo4y1j7Eb)
+ [https://blog.csdn.net/weixin_45043334/article/details/130538683](https://blog.csdn.net/weixin_45043334/article/details/130538683)

## TCP基本介绍
### 什么是TCP？
TCP 是面向连接的、可靠的、基于字节流的传输层通信协议。

+ **面向连接：**一定是「一对一」才能连接，不能像 UDP 协议可以一个主机同时向多个主机发送消息，也就是一对多是无法做到的；
+ **可靠的：**TCP 都可以保证一个报文一定能够到达接收端；
+ **字节流：**TCP 报文面向字节流（byte stream）的意思是，它将数据视为一个连续的字节序列，而不是独立的消息或数据包。这种设计使得 TCP 能够为应用程序提供可靠的、顺序的和无界的数据传输。具体来说，字节流特性体现在以下几个方面：
    1. **无消息边界**：在 TCP 中，发送的数据没有固定的消息边界。应用程序可以随意发送任意长度的数据，TCP 将这些数据分成适合网络传输的小片段（TCP 报文段），并负责在接收端重新组装这些片段。
    2. **数据流的顺序性**：TCP 保证数据的顺序传输。尽管在网络中数据包可能以不同的顺序到达，但 TCP 会在接收端根据序列号对数据进行排序，确保应用程序接收到的数据顺序与发送顺序一致。
    3. **流量控制和拥塞控制**：TCP 使用流量控制和拥塞控制机制来动态调整数据发送的速率，确保接收方能够处理数据并避免网络拥塞。这些机制在字节流传输中起着关键作用。
    4. **可靠性**：TCP 提供可靠的数据传输，确保所有发送的数据都会被接收方接收到。丢失的数据会被重新发送，确保字节流的完整性。

### 传输数据简单模型
![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1725526988719-80579baf-bcf8-45a0-875b-88f2a2512372.png)

### TCP报文头的格式
![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1709233302738-56c86a9f-cb03-49e0-87df-6ef6b5e51686.png)

**<font style="color:rgb(51, 51, 51);">固定首部长度为20字节,可变部分0~40字节,各字段解释：</font>**

1. 0-4字节，源端口，目的端口
2. <font style="color:#262626;">4-8字节，序列号（sequence number）</font>
+ <font style="color:rgb(51, 51, 51);">TCP 连接中传送的数据流中的每一个字节都编上一个序号。序号字段的值则指的是本报文段所发送的数据的第一个字节的序号。</font>

```plain
理解1：
假设有一段文本数据需要通过 TCP 进行传输，内容为："Hello, World!"。

这段文本数据共有 13 个字符，每个字符占据一个字节的空间。现在，发送方将这段数据分成了两个数据段进行传输：

第一个数据段包含前面 6 个字符："Hello,"
第二个数据段包含后面 7 个字符：" World!"
现在，让我们假设这两个数据段的序列号分别为：

第一个数据段的序列号为 1000
第二个数据段的序列号为 1006
这里的序列号表示数据段的第一个字节在整个数据流中的位置。因此，第一个数据段的序列号为 1000，
意味着它包含的数据从整个数据流中的第 1000 个字节开始；第二个数据段的序列号为 1006，意味着
它包含的数据从整个数据流中的第 1006 个字节开始。

在接收方收到这两个数据段后，根据序列号可以正确地重组数据，从而还原出原始的文本数据："Hello, World!"。

理解2：
假如我有tcp报文中，序列号为100，数据流中有10个字节的数据，那么这次报文,数据的序列号就为100-110。
就这样服务端拼凑收集的 100-110，111-120,121-130 组成完整的数据
```

3. <font style="color:#262626;">8-12字节确认号：</font>
+ <font style="color:rgb(51, 51, 51);">acknoledgement number： 确认号，32bits，期望收到对方的下一个报文段的数据的第一个字节的序号。</font>

```plain
假设主机 A 向主机 B 发送了两个数据段，分别包含序列号为 1000 和 1006 的数据。
在发送这两个数据段之后，主机 B 需要向主机 A 发送确认消息，以确认它已经收到了这两个数据段。

当主机 B 收到第一个数据段（序列号为 1000）时，它需要向主机 A 发送一个确认消息，
确认它已经成功接收了序列号为 1000 的数据段。因此，主机 B 的确认号应该设置为 1007，
表示主机 B 期望收到的下一个数据段的序列号。

当主机 B 收到第二个数据段（序列号为 1006）时，它需要再次向主机 A 发送一个确认消息，
确认它已经成功接收了序列号为 1006 的数据段。因此，主机 B 的确认号应该设置为 1013，
表示主机 B 期望收到的下一个数据段的序列号。

通过设置确认号，主机 B 可以通知主机 A 它已经成功接收了哪些数据段，
并告知主机 A 下一个期望接收的数据段的序列号。这样可以确保数据的可靠传输和正确重组。
```

4. 12-16字节：
+ <font style="color:rgb(51, 51, 51);">数据偏移：4bits，单位为4字节，它指出报文数据距TCP报头的起始处有多远(TCP报文头长度)。</font>
+ <font style="color:rgb(51, 51, 51);">保留字段：6bits，保留今后使用，目前置0处理。</font>
+ <font style="color:rgb(51, 51, 51);">URG：紧急比特，1bit，当 URG=1 时，表明紧急指针字段有效。它告诉系统此报文段中有紧急数据，应尽快传送(相当于高优先级的数据)</font>
+ <font style="color:#E4495B;">ACK：确认比特，1bit，只有当 ACK=1时确认号字段才有效。当 ACK=0 时，确认号无效</font>
+ <font style="color:rgb(51, 51, 51);">PSH：推送比特，1bit，接收方 TCP 收到推送比特置1的报文段，就尽快地交付给接收应用进程，而不再等到整个缓存都填满了后再向上交付</font>
+ <font style="color:rgb(51, 51, 51);">RST：复位比特，1bit，当RST=1时，表明TCP连接中出现严重差错(如由于主机崩溃或其他原因)，必须释放连接，然后再重新建立运输连接</font>
+ <font style="color:#E4495B;">SYN：同步比特，1bit，同步比特 SYN 置为 1，就表示这是一个连接请求或连接接受报文</font>
+ <font style="color:#E4495B;">FIN：终止比特，1bit，用来释放一个连接。当FIN=1 时，表明此报文段的发送端的数据已发送完毕，并要求释放运输连接</font>
+ <font style="color:rgb(51, 51, 51);">窗口大小：16bits，窗口字段用来控制对方发送的数据量，单位为字节。TCP 连接的一端根据设置的缓存空间大小确定自己的接收窗口大小，然后通知对方以确定对方的发送窗口的上限。</font>
5. 16-20字节：
+ <font style="color:rgb(51, 51, 51);">检验和：16bits，检验和字段检验的范围包括首部和数据这两部分。在计算检验和时，要在 TCP 报文段的前面加上 12 字节的伪首部。</font>
+ <font style="color:rgb(51, 51, 51);">紧急指针字段：16bits，紧急指针指出在本报文段中的紧急数据的最后一个字节的序号。</font>
6. <font style="color:rgb(51, 51, 51);">选项字段，长度可变。TCP首部可以有多达40字节的可选信息，用于把附加信息传递给终点，或用来对齐其它选项。 这部分最多包含40字节，因为TCP头部最长是60字节（其中还包含前面讨论的20字节的固定部分）  
</font><font style="color:rgb(51, 51, 51);">选项的第一个字段kind说明选项的类型。有的TCP选项没有后面两个字段，仅包含1字节的kind字段。第二个字段length（如果有的话）指定该选项的总长度，该长度包括kind字段和length字段占据的2字节。第三个字段info（如果有的话）是选项的具体信息. </font>

<font style="color:rgb(51, 51, 51);">kind=0是选项表结束选项</font>

<font style="color:rgb(51, 51, 51);">kind=1是空操作（nop）选项，没有特殊含义，一般用于将TCP选项的总长度填充为4字节的整数倍</font>

<font style="color:rgb(51, 51, 51);">kind=2是最大报文段长度选项,TCP连接初始化时，通信双方使用该选项来协商最大报文段长度（Max Segment Size，MSS）。TCP模块通常将MSS设置为（MTU-40）字节（减掉的这40字节包括20字节的TCP头部和20字节的IP头部）。这样携带TCP报文段的IP数据报的长度就不会超过MTU（假设TCP头部和IP头部都不包含选项字段，并且这也是一般情况），从而避免本机发生IP分片。对以太网而言，MSS值是1460（1500-40）字节。</font>

## TCP建立，断开连接

### 三报文握手
![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1709236075323-122308d5-5d65-4d6e-b376-3258c5c66a6b.png)

+ 第一次握手：
    - 客户端和服务端都处于<font style="color:#C75C00;">CLOSE</font>状态。先是服务端主动监听某个端口，处于<font style="color:#C75C00;">LISTEN</font>状态
    - 客户端会随机初始化序号(client_isn)（假设为x），将此序号置于TCP首部的「序号」字段中，同时把SYN标志位应置为1，表示SYN报文。接着把第一个SYN报文发送给服务端，表示向服务端发起连接，该报文不包含应用层数据，之后客户端处于SYN-SENT状态。
+ 第二次握手：
    - 服务端收到客户端的SYN报文后，首先服务端也随机初始化自己的序号(server_isn)（假设为y），将此序号填入TCP首部的「序号」字段中，其次在TCP首部的「确认应答号」字段填入x+1，接着把SYN和ACK标志位置为1。最后把该报文发给客户端，该报文也不包含应用层数据，之后服务端处于SYN-RCVD（<font style="color:rgb(13, 13, 13);">SYN Received</font>）状态。
+ 第三次握手：
    - 客户端收到服务端报文后，还要向服务端回应最后一个应答报文，首先该应答报文TCP首部ACK标志位置为1，其次「确认应答号」字段填入x+1,最后把报文发送给服务端，这次报文可以携带客户到服务端的数据，之后客户端散处于<font style="color:#C75C00;">ESTABLISHED</font>状态。服务端收到客户端的应答报文后，也进入<font style="color:#C75C00;">ESTABLISHED</font>状态。

Tips：

1. 第三次握手是可以携带数据的，前两次握手是不可以携带数据的。
2. <font style="color:rgb(13, 13, 13);">（Client Initial Sequence Number）isn</font>
3. <font style="color:rgb(13, 13, 13);">tcp规定syn报文不携带数据，但要消耗一个序号</font>

:::info
1. **为什么第三次握手是x+1，y+1？**
+ 因为客户端syn报文消耗一个序号，服务端syn报文消耗了一个序号。
2. **为什么不使用两次握手？**
+ 因为两次握手无法阻止历史连接，比如客户端发送第一次握手的tcp报文延迟没有到达，又重新发了一次，建立链接，发送数据，断开连接，结果第一次的延迟报文在断开连接后到达，会导致又重新建立连接。（可能会导致建立多个冗余的连接，造成资源的浪费）
3. **为什么要随机初始化一个序列号？**
+ 防止旧连接的混淆：TCP通过序列号来保证数据的有序传输和确认。为了防止历史连接中的数据包被误认为是当前连接的数据包，每次新建立连接时都会选择一个不同的初始序列号。  

:::

### 四报文挥手
#### a. TTL和MSL是什么？
TTL（Time to Live，生存时间）和 MSL（Maximum Segment Lifetime，最大报文段生存时间）都是网络协议中的重要概念，但它们用于不同的层面，且作用和目的不同。以下是它们的区别和联系：

TTL（Time to Live）

+ **层次**：TTL是网络层（通常是IP协议）的概念。
+ **定义**：TTL表示一个IP数据包在网络中可以存在的最大“跳数”（即数据包可以经过的路由器数量）。
+ **作用**：每当数据包经过一个路由器时，TTL值会减1。当TTL值达到0时，数据包被丢弃。这种机制可以防止数据包在网络中无限循环（避免路由环路）。
+ **初始值**：TTL的初始值通常是64、128或255，但这个值可以由发送方设置。
+ **实现**：主要用于控制数据包的寿命，避免网络阻塞和拥塞。

MSL（Maximum Segment Lifetime）

+ **层次**：MSL是传输层（主要是TCP协议）的概念。
+ **定义**：MSL表示一个TCP报文段在网络中被认为有效的最长时间。这个时间限制了TCP连接在关闭后保留的时间。
+ **作用**：TCP协议在连接关闭时进入TIME_WAIT状态，保持时间通常是2倍的MSL（2 * MSL）。这是为了确保网络中任何可能延迟的报文段被成功丢弃，确保双方的TCP连接已经安全关闭并避免旧连接的数据干扰到新连接。
+ **典型值**：MSL的典型值是30秒到2分钟。

区别

1. **作用层次**：
    - TTL用于网络层（IP协议），控制数据包在网络中的生存时间。
    - MSL用于传输层（TCP协议），管理TCP连接在关闭后的状态时间。
2. **控制对象**：
    - TTL控制的是IP数据包的生存时间。
    - MSL控制的是TCP报文段在网络中的最长寿命。
3. **应用场景**：
    - TTL用于防止数据包在网络中无限循环。
    - MSL用于确保TCP连接在关闭时所有数据已正确传输并避免旧连接数据的干扰。

#### b. 四次挥手
![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1725599322399-a862f298-aa08-419f-a9ce-3bb95b342747.png)

TCP建立连接后，双方都可以释放连接，下面以客户端主动释放连接举例

**连接释放报文：FIN = 1，ACK = 1**

**普通确认报文：ACK = 1**

+ 第一次挥手
    - 客户端发送TCP连接释放报文段，ack字段同时也对之前收到的报文进行确认，seq字段是之前已经传送的字节序号+1，TCP规定FIN=1的终止报文即使不携带数据也要消耗掉一个序号，<font style="color:#DF2A3F;">客户端终止等待1状态（FIN-WAIT-1）</font>
+ 第二次挥手
    - 服务端接收到连接释放报文后，会回复一个普通的TCP确认报文。<font style="color:#DF2A3F;">服务端进入关闭等待状态（CLOSE-WAIT），客户端收到该报文进入终止等待2状态（FIN-WAIT-2）</font>
+ 第三次挥手
    - 此时从客户端到服务端这个方向的连接就释放了，TCP处于半关闭状态，客户端已经没有数据要发送了，服务端如果还有数据要发送TCP客户端仍要接收（说明TCP从服务端到客户端这个方向的连接还没有关闭）过一段时间后，服务端已经没有数据要发送，此时会发送TCP连接释放报文进入。<font style="color:#DF2A3F;">服务端进入最后确认状态（LAST-ACK）</font>
+ 第四次挥手
    - 客户端收到报文后，客户端会回复一个普通的TCP确认报文。<font style="color:#DF2A3F;">客户端进入时间等待状态（TIME-WAIT），服务端收到该报文进入关闭状态（CLOSED）</font>

> MSL(Maximum Segment Lifetime)意思是最长报文段寿命，RFC793建议为2分钟。
>
> 最长报文段寿命：防止数据包在网络中无限制传递，从而避免网络环路
>

#### c. 为什么会客户端要等待2MSL？
服务端发送给客户端连接释放报文后，防止客户端回复的确认报文丢失，使得服务端超时重传过来后发现客户端已经关闭，服务端一直无法释放连接从而导致网络资源的浪费。总结：确保服务端能够正确收到最后一个TCP确认报文从而进入关闭状态。

还有一点2MSL可以确保网络中TCP报文消失，从而再次建立新的连接时不会收到就的报文影响

![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1725600085009-197720a2-d0f6-4cb8-a7bf-eaad579694df.png)

## TCP保活计时器
在建立TCP连接后，如果一方突发故障，那么另外一方就再也无法收到TCP报文，因此应当有措施使得正常的一方不能白白等待下去

![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1725600463413-046813e5-8ea6-4898-83ae-600b853d9bc8.png)

+ [https://www.cnblogs.com/awkflf11/p/12622274.html](https://www.cnblogs.com/awkflf11/p/12622274.html)

## TCP超时重传
超时重传次数！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！

默认的话是16次

### 报文往返时间（RTT）
当两台主机建立TCP连接之后，主机A发送数据报文段0，主机B返回确认报文段0。这两个报文之间的时间差就叫RTT0（0代表第几个报文段的往返时间）

![画板](https://cdn.nlark.com/yuque/0/2024/jpeg/33704534/1725559665754-1330ffbf-0f84-4d2f-85b6-8910c60f0fd8.jpeg)

### 超时重传时间（RTO）
#### a. 发展一：单一的RTO
当报文丢失后，我们要选择多久来重新发送这个报文

+ 情况1：如果小于RTT的话，会导致报文的重复发送
+ 情况2：如果过大于RTT的话，会导致网络空闲时间增大
+ 情况3：略大于RTT，刚好

![画板](https://cdn.nlark.com/yuque/0/2024/jpeg/33704534/1725558256940-3bed44e6-a7d3-452b-9a0e-7cda0d2a9dd8.jpeg)

#### b. 发展二：加权的RTT
但是由于互联网的复杂环境，不同的路由或者不同的网络环境会导致不同的往返时间（比如报文0恰好此时网络不拥堵，传输很快，而报文1却遇到了网络堵塞）

![画板](https://cdn.nlark.com/yuque/0/2024/jpeg/33704534/1725558450487-db286e89-07b3-4211-b903-5b81d66328fb.jpeg)

当每个报文的往返时间RTT不相同的时候，超时重传时间RTO会很难选择，不能直接使用某次测量得到的RTT样本来计算超时重传时间RTO。

+ 利用每次测量得到的RTT样本，计算加权平均往返时间RTTs(又称为平滑的往返时间)。
+ $ RTTs_{新}  = (1-α) × RTTs_{旧} + α × RTT_{n} $

在上式中，0≤ α <1：

+ 若 α 接近于0，则新RTT样本对RTTs的影响不大：
+ 若 α 接近于1，则新RTT样本对RTTs的影响较大：
+ 已成为建议标准的RFC6298推荐的 α 值为1/8，即0.125

#### c. 发展三：RFC6298中RTO的计算公式
![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1725559127331-8d5fbc18-328a-404a-bf25-44bd7ad32e9e.png)

#### d. 发展四：超时重传下RTT值的抉择
![画板](https://cdn.nlark.com/yuque/0/2024/jpeg/33704534/1725560063222-8d3bad12-0dce-42a9-ba46-644243fbe24e.jpeg)

+ 针对出现超时重传时无法测准往返时间RTT的问题，Karn提出了一个算法：在计算加权平均往返时间RTTs时，只要报文段重传了，就不采用其往返时间RTT样本。也就是出现重传时，不重新计算RTTs,进而超时重传时间RTO也不会重新计算。
+ 这又引起了新的问题。**<font style="color:#DF2A3F;">设想出现这样的情况：报文段的时延突然增大了很多，并且之后很长一段时间都会保持这种时延。</font>**因此在原来得出的重传时间内，不会收到确认报文段。于是就重传报文段。但根据Karn算法，不考虑重传的报文段的往返时间样本。这样，超时重传时间就无法更新。这会导致报文段反复被重传。因此，要对Karn算法进行修正。方法是：报文段每重传一次，就把超时重传时间RTO增大一些。典型的做法是将新RTO的值取为旧RTO值的2倍。

#### e. 最终章：实际例子
![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1725560796249-8f75e237-e3e0-45b6-b13a-b81b5a57723f.png)

## TCP流量控制
流量控制（flow control）使得发送方发送的速率匹配接收方接收的速率，就是让发送方的发送速率不要太快，要让接收方来得及接收

利用滑动窗口机制可以很方便地在TCP连接上实现对发送方的流量控制，TCP基于以字节为单位的滑动窗口实现可靠传输

### RWND (Receiver Window) 滑动窗口
![画板](https://cdn.nlark.com/yuque/0/2024/jpeg/33704534/1725532807921-707e9b1b-c6c2-479d-a556-a6c45a1ac53f.jpeg)

+ 虽然发送方的发送窗口是根据接收方的接收窗口设置的，但在同一时刻，发送方的发送窗口并不总是和接收方的接收窗口一样大。
    - 网络传送窗口值需要经历一定的时间滞后，并且这个时间还是不确定的。
    - 发送方还可能根据网络当时的拥塞情况适当减小自己的发送窗口尺寸。
+ 对于不按序到达的数据应如何处理，TP并无明确规定：
    - 如果接收方把不按序到达的数据一律丢弃，那么接收窗口的管理将会比较简单，但这样做对网络资源的利用不利因为发送方会重复传送较多的数据。
    - TCP通常对不按序到达的数据是先临时存放在接收窗口中，等到字节流中所缺少的字节收到后，再按序交付上层的应用进程。
+ TCP要求接收方必须有累积确认和捎带确认机制，这样可以减小传输开销。接收方可以在合适的时候发送确认，也可以在自己有数据要发送时把确认信息顺便捎带上。
    - 接收方不应过分推迟发送确认，否则会导致发送方不必要的超时重传，这反而浪费了网络的资源。TCP标准规定，确认推迟的时间不应超过0.5秒。若收到一连串具有最大长度的报文段，则必须每隔一个报文段就发送一个确认[RFC1122]。
    - 捎带确认实际上并不经常发生，因为大多数应用程序很少同时在两个方向上发送数据。
+ TCP的通信是全双工通信。通信中的每一方都在发送和接收报文段。因此，每一方都有自己的发送窗口接收窗口。在谈到这些窗口时，一定要弄清楚是哪一方的窗口。

### CWND（Congestion Window）拥塞窗口


## TCP可靠传输的实现
TCP的滑动窗口是以字节为单位的

## Nagle算法
+ Nagle算法的定义和背景‌‌

‌Nagle算法‌是由‌约翰·纳格（John Nagle）提出的，旨在减少网络传输中的小数据包数量，从而提高网络效率。该算法主要应用于TCP协议中，特别是在广域网环境中，能够有效减少网络拥塞，提高网络性能。

+ Nagle算法的基本原理和核心规则‌

Nagle算法的基本原理是当一个TCP连接上有数据要发送时，并不立即发送，而是等待一小段时间（通常由一个RTT，即往返时延来估计），看看是否有更多的数据要发送。如果在这段时间内有额外的数据产生，那么这些数据会被组装成一个更大的报文一起发送。这样做可以减少网络中由于过多的小包而引起的拥塞。

+ Nagle算法的核心规则如下

如果当前有一个部分填写的报文段（即还有可用空间可添加数据）在等待发送，那么等到这个报文段填满或者达到最大延迟时间后再发送。对于包含ACK（确认应答）但不携带数据的报文段，可以立即发送，不受上述规则限制。如果发生了超时或者收到了三个重复的ACK，则立即发送一个报文段，即使它没有填满。

# TCP实际应用中的问题
## 半包，粘包问题
+ [https://mp.weixin.qq.com/s?__biz=MzkxNTU5MjE0MQ==&mid=2247492768&idx=1&sn=686087613bf6689e899b2cdb606ea780&source=41#wechat_redirect](https://mp.weixin.qq.com/s?__biz=MzkxNTU5MjE0MQ==&mid=2247492768&idx=1&sn=686087613bf6689e899b2cdb606ea780&source=41#wechat_redirect)
+ <font style="color:rgb(24, 25, 28);">https://mp.weixin.qq.com/s/0-YBxU1cSbDdzcZEZjmQYA</font>

### 为什么会出现？
TCP 是面向字节流的协议。TCP在运输层并不了解上层交付数据的含义，它自身缓冲区（滑动窗口）实际情况进行包的划分，一**个完整的包可能会被TCP拆分成多个包进行发送**，**也有可能把多个小的包封装成一个大的数据包发送**，这就是所谓的TCP粘包和拆包问题

+ **在****<font style="color:#DF2A3F;">数据链路层</font>****导致半包原因**
    - <font style="color:rgb(37, 41, 51);">待发送数据大于MSS（最大报文长度），TCP在传输前将进行拆包。即TCP报文长度-TCP头部长度>MSS（向localhost发送数据一般不会产生这个问题，本地回环地址大小一般为65535）</font>🤠<font style="color:#C75C00;">感觉这个还有挺多内容以后碰到多注意注意</font>
+ **在****<font style="color:#DF2A3F;">运输层面</font>****产生半包和黏包的原因**
    - 发送的数据小于TCP发送缓冲区（滑动窗口）大小，TCP将多次写入缓冲区的数据一次发送出去，从而导致产生粘包
    - 发送的数据大于TCP发送缓冲区（滑动窗口）大小，将会发生拆包
    - Nagle算法：数据包不会立刻发送而是等待一小段时间一起发送（避免小包太多导致网络拥堵）
+ **在****<font style="color:#DF2A3F;">应用层面</font>****产生半包和黏包的原因**
    - netty中接收方ByteBuf设置太大，导致粘包或者在nio中将ByteBuffer设置太大，导致粘包
    - netty中接收方ByteBuf设置太小，导致半包或者在nio中将ByteBuffer设置太小，导致半包

导致以上原因的本质还是TCP面向流没有消息边界的问题

+ **<font style="color:#DF2A3F;">应用层面</font>****<font style="color:rgb(37, 41, 51);">解决方法</font>**
    - <font style="color:rgb(37, 41, 51);">在数据末尾添加字符作为分割标志位（例如'\n'）</font>

<font style="color:rgb(37, 41, 51);">下图为运输层面原因的示意图</font>

![](https://cdn.nlark.com/yuque/0/2024/webp/33704534/1725606784222-13e949da-23a9-496c-9575-2d3b9df5efdd.webp)



<font style="color:#DF2A3F;">等待添加一个应用层面的示意图.......!!!!!</font>



:::tips
如果接收方的缓冲数组只有80字节大小，剩下的20字节会被如何处理？  

**对于 UDP：**

    - 丢弃：如果接收到的 UDP 数据报的大小超过接收方的缓冲区（80 字节），接收方将丢弃整个数据报。这是因为 UDP 不支持分段或重组，数据报被视为独立的实体。因此，只有缓冲区足够大的数据报才能被接收，超出部分的数据将无法处理。
    - 返回 ICMP 消息：在某些情况下，接收方的操作系统可能会发送一个 ICMP “端口不可达”（Destination Unreachable）消息给发送方，指示发送的数据报被丢弃。

**对于 TCP：**

    - 缓冲区满：如果接收方的 TCP 缓冲区只有 80 字节，而发送方发送了超过这个大小的数据，接收方会接收数据并将其放入缓冲区，直到缓冲区满。此时，TCP 协议会通过流量控制机制，阻止发送方继续发送数据。
    - 阻塞或丢包：在 TCP 中，如果接收缓冲区满，接收方可能会阻止发送方发送更多数据，直到它处理了缓冲区中的数据并释放出空间。这是通过调整 TCP滑动 窗口大小实现的。如果应用程序未及时读取缓冲区中的数据，TCP 可能会最终出现超时或其他错误。

UDP 会出现半包问题吗？

不会，因为UDP是面向报文的协议，每个数据报都有明确边界，而 TCP 没有明确的边界。并且 UDP 会直接丢弃超过边界的数据，而 TCP 的数据要确保数据的完整。

:::

### 解决方法
1. **固定长度消息**

+ 每条消息的长度是固定的，因此接收方可以根据预定义的长度来识别每条消息的边界。
+ 缺点：如果消息的长度是可变的，使用固定长度的消息可能会浪费带宽。

2. **消息分隔符**

+ 使用特定的分隔符（如 `\n`、空字符 `\0` 等）来标识消息的结束。
+ 发送方在消息末尾添加分隔符，接收方通过查找分隔符来判断消息边界。
+ 缺点：需要确保消息内容中不会意外包含分隔符。

3. **长度前缀**

+ 在每条消息的前面添加一个长度字段，该字段指定后续消息的字节数。
+ 接收方首先读取长度字段，然后根据长度字段来读取完整的消息。
+ 优点：能够处理可变长度的消息，常用于复杂协议中。

4. **帧格式**

+ 一些协议（如 WebSocket、TLS、HTTP/2 等）使用帧结构，每个帧包含消息的元数据（如长度、类型）以及实际的消息数据。
+ 接收方解析帧头信息后，可以识别消息边界并处理消息。

5. **应用层协议设计**

+ 在应用层协议中设计明确的消息结构，包括头部、长度、校验和其他控制信息，使得接收方能够准确地解析和识别消息边界。
+ 例如，HTTP 协议中使用了请求行、头部和空行来分隔消息内容。

#### Java NIO 处理
![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1723649380416-d0e79da5-d03b-4f8a-8100-348abd549a71.png)

**通过 \n 作为分割符，确定数据边界**

```java
public static void main(String[] args) throws IOException {
    ServerSocketChannel server = ServerSocketChannel.open();
    server.bind(new InetSocketAddress(9090));
    server.configureBlocking(false);

    Selector selector = Selector.open();
    server.register(selector, SelectionKey.OP_ACCEPT, null);

    System.out.println("开始监听....");
    while (true) {

        selector.select();
        Iterator<SelectionKey> keysIterator = selector.selectedKeys().iterator();
        while (keysIterator.hasNext()){
            SelectionKey key = keysIterator.next();
            if (key.isAcceptable()){
                SocketChannel client = server.accept();
                client.configureBlocking(false);

                ByteBuffer clientBuffer = ByteBuffer.allocate(5);
                client.register(selector, SelectionKey.OP_READ, clientBuffer);

                System.out.println("    端口：" + client.getRemoteAddress() + "建立连接");
            } else if (key.isReadable()) {
                SocketChannel client = (SocketChannel)key.channel();
                ByteBuffer clientBuffer = (ByteBuffer)key.attachment();


                int read = client.read(clientBuffer);

                if (read == -1){
                    key.cancel();
                    client.close();
                } else {
                    debugAll(clientBuffer); // 打印bytebuffer

                    split(clientBuffer); // 分割数据

                    if (clientBuffer.position() == clientBuffer.limit()){
                        // flip()    ==> position = 0              limit = position
                        // compact() ==> position = limit-position
                        // position == limit 说明未读取数据，需要扩容
                        ByteBuffer newBuffer = ByteBuffer.allocate(clientBuffer.capacity() << 1);
                        clientBuffer.flip();
                        newBuffer.put(clientBuffer);
                        key.attach(newBuffer);
                    }

                }

                split(clientBuffer);
            }

            keysIterator.remove();
        }
    }
}

private static void split(ByteBuffer source){
    source.flip(); // 读模式
    ByteBuffer target = ByteBuffer.allocate(source.limit());
    for (int i = 0; i < source.limit(); i++) {
        byte b = source.get(i);
        target.put(b);
        if (0xa == b){
            target.flip();
            System.out.println(StandardCharsets.UTF_8.decode(target));// 打印target中的内容
            target.clear();
            /*
            * compact 压缩数据从 position 开始拷贝 limit - position个数据到 buffer 前端
            * 将 position 设置为 limit - position
            * */
            source.position(i + 1);
        }
    }
    source.compact(); // 压缩
}
// debug方法，在Netty_01包下
```

#### 等待添加其他语言处理方法.......
## 长连接和短连接
+ **长连接**：客户端和服务器之间建立TCP连接之后，开始传送数据，如果当前数据传输完毕后不主动断开连接。当有数据再次传输的时候直接利用当前的TCP通道传输数据
+ **短连接**：客户端和服务器之间建立TCP连接之后，开始传送数据，当数据传输完毕，客户端或者服务端主动断开连接
    - 在HTTP1.0当中就是使用的这种短连接
    - 在HTTP1.1时可以在头部添加 connection：keep-alive 字段保持长连接

> connection：keep-alive 一般默认时间为60秒到120秒，也可以自己设置超时时间 keep-alive: timeout=20
>

:::tips
+ <font style="color:rgb(24, 25, 28);">TCP协议栈是在操作系统内核实现的， 内核只是单纯应用层提供功能和默认行为， 至于长链接和短链接在一定程度上是应用层如何构建，是对连接的描述方式，如何定义行为，应用层没有业务数据要传输时， 要保持长链接是需要应用定时发送心跳包给对端来让内核tcp协议栈保持连接活跃状态， 同理， 短链接通常也是应用层主动断开的， 应用层如果没有主动行为， tcp连接的行为就是操作系统网络协议栈的默认行为。</font>
+ <font style="color:rgb(24, 25, 28);">TCP的keepalive是在ESTABLISHED状态的时候，双方如何检测连接的可用性。而HTTP的keep-alive说的是如何避免进行重复的TCP三次握手和四次挥手的环节，两者不一样的</font>

:::

