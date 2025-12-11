# DHCP
## DHCP配置
DHCP：全称dynamic host configuration protocol
通过dhcp服务可以获得
- ip地址
- 子网掩码
- 网关地址
- DNS服务器地址

打开路由器管理界面可以看到地址管理和dhcp server
- 地址管理
	- 路由器自身配置
- dhcp server
	- 可分配的ip地址池
	- 主DNS
		- 这个地址是路由器自身的ip地址，意味着所有设备的DNS请求会先转发给路由器，再由路由器转发到外部DNS服务器
	- 备份DNS
		- 备份DNS地址为0.0.0.意味着使用未配置
- 租期时间
	- 86400秒意味着租期为一天
![[20251211.png]]

## DHCP详解
### 查看DHCP Server获取的配置
- 输入ipconfig /all查看当前通过DHCP获取的配置
![[20251211-1.png]]

### 主动释放租约
- 输入ipconfig /release释放当前租用的地址
![[20251212.png]]


可以看到当电脑主动释放租用的地址后ip变为169.254.62.39，这是windows的APIPA机制（自动私有ip寻址）
允许DHCP没有分配ip地址的情况下，仍然可以在本地网络进行通信
### 主动获取租约
#### DHCP请求获得configuration流程
- 输入ipconfig /renew
![[20251211-2.png]]
#### Discovery
向当前网络中广播，寻找dhcp服务器，并且请求上次使用过的ip，本机的host name，mac地址等
![[20251211-3.png]]

#### Offer
DHCP服务器提供给客户端的ip、子网掩码、DHCP服务器本身的地址
![[20251211-4.png]]

#### Request
客户端此时广播局域网，告诉自己选的dhcp服务器
>为什么得到了DHCP服务器的地址，仍然在此时仍然选择广播？
>	因为局域网中可能存在多个DHCP服务器，每个DHCP服务器在收到Discovery时会选择提供一个ip
>	所以为了告诉其它DHCP服务器自己的选择，此时仍然要广播

#### ACK
服务器收到request后，回复ACK，知道自己被选中
![[20251211-5.png]]
