# Ubuntu常用配置
## 网络配置
### 开机等待网络配置时间
#### 流程
- cd /etc/systemd/system/network-online.target.wants/
- sudo vi systemd-networkd-wait-online.service
- 在\[service\]中添加时间TimeoutStartSec=2sec
### 网络接口
- 进入/etc/netplan
- 创建文件夹01-static-usb0.yaml(ubuntu根据文件顺序读取配置)
- netplan try验证文件并且使用
- netplan apply应用
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