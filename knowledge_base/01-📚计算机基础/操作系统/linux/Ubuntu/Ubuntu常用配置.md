# Ubuntu常用配置
## 网络配置
### 开机等待网络配置时间(ubuntu有脚本)
#### 流程
- cd /etc/systemd/system/network-online.target.wants/
- sudo vi systemd-networkd-wait-online.service
- 在\[service\]中添加时间TimeoutStartSec=2sec
### netplan
#### usb
```yaml
network:
  ethernets:
    usb0:
      dhcp4: no
      addresses:
        - 192.168.244.3/24
      routes:
        - to: default
          via: 192.168.244.223
      nameservers:
        addresses:
          - 223.5.5.5
          - 8.8.8.8
  version: 2
```
#### wifl

```yaml
network:
  version: 2
  renderer: networkd
  wifis:
    wlp3s0:
      dhcp4: no
      addresses:
        - 192.168.10.22/24
      routes:
        - to: default
          via: 192.168.10.1
          metric: 50
      nameservers:
        addresses:
          - 223.5.5.5
          - 8.8.8.8
      access-points:
        CMCC-PUTs:
          password: yzsy4249
```
- metric值越小，默认路由优先级越高
- 