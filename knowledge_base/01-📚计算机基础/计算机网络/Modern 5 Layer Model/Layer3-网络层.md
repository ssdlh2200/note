# Layer3-网络层
## 网络层概念
1. 网络层主要作用
	- 负责IP寻址、路由、数据包的封装与分片
2. 网络层PDU（protocol data unit）
	- **数据包（Packet）**
3. 网络层主要协议
	- IP协议（Internet Protocol）
	- 路由协议（运行在路由器上，用于路由器之间交换信息，维护路由表，确保数据包能找到最佳路径）
		- RIP
		- OSPF
		- EIGRP
	- 辅助协议（协助IP协议完成其功能）
		- ICMP
		- ARP
## IP协议



## ARP地址解析协议
通过IP地址找到MAC地址

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687597540713-942fadc9-2304-4ff5-9bfc-532ec24cec85.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687597594884-3012ca3a-d5f3-4434-a0f0-23e20dab3761.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687597661719-4e2d0274-2e8d-40d3-80f6-96d44dc97950.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687597713833-e6f1f12a-fd5f-4934-8747-b90f0a632450.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687597783816-bb4d737c-9795-4e8a-bdeb-ecc006a11be1.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687597825667-4a99fba1-2145-488f-8bb6-a0dba95122d6.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687597883933-4312d84c-1e3a-4796-a8ae-18cf0d46f8d8.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687597959583-106db264-8f35-4457-818d-2499f9a453c2.png)

# IP地址
## IPv4
ipv4的编制经历如下三个阶段

![画板](https://cdn.nlark.com/yuque/0/2024/jpeg/33704534/1725219155215-8d9f3112-5849-4f58-bd31-0ad1ed4781be.jpeg)

### 分类编址IPv4地址
![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687598495903-1716d553-708b-4c0a-baa6-9273e75a091a.png)



#### A类地址（小于127）
![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687598747358-228d066f-62f1-4a8d-b6bf-3f320f6bba6e.png)

#### B类地址（128-191）
![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687599373451-ebe79811-71f8-4415-a891-03bd9067b089.png)

#### C类地址（192-223）  
![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687599518582-e8f7fe8b-2081-4b6f-a932-faead4c98a14.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687600208412-8e5ff2a7-5790-4324-b7e4-0faa5c9d6149.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687600526530-cc6a5a25-606b-4ed7-9f40-5e552f36c060.png)

### 划分子网(subnet)的IPv4地址
从IPv4地址的主机号部分，借用几位作为子网的区分，就可以利用原有网络中大量剩余的IP地址

32比特的子网掩码可以**<font style="color:#DF2A3F;">表明分类IP地址的主机号被借用了几个比特作为子网号</font>**

+ 子网掩码使用连续的**比特1来对应网络号和子网号**
+ 子网掩码使用连续的**比特0来对应主机号**
+ 将划分子网的1PV4地址与其相应的子网掩码进行逻辑与运算就可得到IPV4地址所在子网的网络地址

> 网络地址是指仅包含网络号而不包含主机号的IP地址
>

:::tips
如有一个IPv4地址 218.75.230.0，使用子网掩码 255.255.255.128 对其进行子网划分，给出划分细节



1. 首先根据分类编址得出 218.75.230.0 是C类网络地址，说明**前24位为网络号**，后8位为主机号
2. 其次 255.255.255.128 可以很轻松看出其中有**25位被用来当做网络号和子网号**
3. 最后我们可以得出只有1位主机号被当做了子网号，说明只能划分2个子网

![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1725222467377-b7396a72-ec26-44ed-ba9e-120d89f33ebe.png)

:::

:::tips
某主机的ip地址为180.80.77.55，子网掩码为255.255.252.0，如该主机向其所在子网，发送广播分组，则目的地址可以是

A 180.80.76.0		B 180.80.76.255		C 180.80.77.255		D 180.80.79.255



1. 180.80.77.55 是一个B类网络地址，说明前16位被当做网络号
2. 255.255.252.0 说明前22位被用作网络号和子网号
3. 由此可以得出子网号占据6位

网络号：【1011 0100 . 0101 0000】子网：【0100 11】主机号：【01 0011 0111】

如果要为广播地址：

网络号：【1011 0100 . 0101 0000】子网：【0100 11】主机号：【11 1111 1111】

180 . 80 . xx . 255

76 = 0100 1100

77 = 0100 1101

79 = 0100 1111

我们看到只有 79 中最后两位都为 11，所以选D

:::

#### 默认子网掩码
![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687605890257-08f7dfe0-3fc7-4e5f-914b-ec17bf5ace52.png)

#### 定长子网掩码
![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687610793940-289598e9-3bb3-4918-83ec-86891db467e2.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687610857116-3e4b5932-be59-4236-b8e5-4c0cec03fd4f.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687610950654-d708f387-bdda-40f3-a1a2-7d2e18c2ab0e.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687610971865-fca5b5ef-e7d5-4fe3-9df9-6b50090a89a1.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687611011220-cdb7683f-1cf1-494d-9576-2dcfa8bacfe7.png)

#### 变长子网掩码
![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687611167371-7a30ba82-3fb3-491c-97ea-43145a4295e4.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687611309448-30ae87bc-ba58-44e1-b041-20bda2b0d165.png)

### 无分类编址的IPv4地址
![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687604654164-5f15c27c-c5b7-49a1-b1ae-5dcff22dc94b.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687604722646-d02d4fd5-3c9f-46ec-84c0-3584b35a6168.png)

#### 路由聚合
![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687607185270-8274bc3b-56d1-4ba8-9ce7-77130a5c82ed.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687607398602-76fc0851-3a1b-483b-948e-0a053e5a5edd.png)

# 路由（routing）
路由是指分组从源到目的地时，决定端到端路径的网络范围的进程。 

路由是网络层最主要的工作任务

## 路由器
网络层的基本设备

数据转发

一个端口代表一个网段，路由器中存放着通往各个网段的表格，叫做路由表

## 路由表(routing table)或称路由择域信息库(RIB,Routing Information Base)
是个存储在路由器或者联网计算机中的电子表格（文件）或类数据库。路由表存储着指向特定网络地址的路径

## 网关
网关(Gateway)又称网间连接器、协议转换器，用于两个高层协议不同的网络互连。网关既可以用于广域网互连，也可以用于局域网互连。

## IP数据报的发送和转发过程
![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687615657186-f1660e3a-f2ac-48b9-b894-62efea206924.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687615721140-d5760941-ff34-4716-81ea-03918ebe14fd.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687615776675-582c719e-24fd-460b-9ea3-dad52a946038.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687615818378-949216f3-c033-4929-9a8b-daa3274e10dc.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687615910131-250aed7e-91cc-4c34-aeb3-f786af1206b7.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687615954733-365ffa3c-2f0f-4e29-8c0e-6bcede45a850.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687616039131-631f20b3-662c-40a0-8525-28b5b1c921d9.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687616190137-6de042f1-7435-475a-8967-adcb42a23ddd.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687616226739-281ae73b-4008-4ce9-b915-2347b4a9f8b8.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687616554729-fc631c49-3f23-4ed4-9409-8d1d86cd955f.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687616594648-51d03ed3-3461-42c1-afde-7b4d0f7937cb.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687616648431-e5a20df1-8b11-4d07-9a3d-045b41b7735a.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687616789925-ae8ec9f0-7472-4a3a-933e-a6b653068ec7.png)



## 路由的获取方式
### 直连路由
### 静态路由配置及其可能产生的路由环路问题
![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687614524206-e7ce325d-a78b-4fbe-906d-6c75334f8148.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687614959565-22576e15-739f-4c83-9a2a-cc02fe0233ed.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687615313879-b7ef9b65-689b-4f5f-9c95-294590df8bdd.png)



### 动态路由
## 路由选择协议概述
![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687621683406-e3d908b5-7c0a-45d7-b270-dc48defca249.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687621742432-615695fb-04c4-4fc0-8c0c-e6ffad86e574.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687621917484-2e0833dd-a2d9-4208-92b4-e2c6ff89e953.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687622030218-029ade49-c508-4fcc-a5e9-71e72c6b5261.png)

### RIP
RIP协议采用距离向量算法

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687622139060-5a3a8331-ad87-454e-80c9-fd8a00e25e15.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687622211467-8f806489-ab5b-4365-a6b8-2f9e1fc90bd1.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687622334200-2b248b35-f6b2-4e11-ae47-223c4d45e7bd.png)



![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687623314955-06d8024e-55f3-4b50-bb1c-4a380ccb2466.png)

####  基本工作过程
![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687623388870-353c66e9-ff41-41f1-8ffc-0bf504d425eb.png)

#### 基本工作原理
![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687623479076-83d31f1b-5da5-411f-b535-a277427e2178.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687623602463-6e3c9c66-f2be-435f-a057-a57ac5670200.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687623945495-f26285b0-56cd-4140-9d6b-349a1fc02a38.png)

#### RIP缺点
![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687624209325-f4984e7b-9a77-4a82-bdee-7c9cdf8cf9fd.png)

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687624282122-178eeefc-67fb-4ae6-b239-b88bb415bbc9.png)



### OSPF
#### 基本工作原理
最短路径优先算法

![](https://cdn.nlark.com/yuque/0/2023/png/33704534/1687624560632-5d2360b8-dd6f-4753-afa0-b019ec574c55.png)



