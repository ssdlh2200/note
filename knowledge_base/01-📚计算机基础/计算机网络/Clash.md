# Clash

## Fake-IP

1. 浏览器访问 learn.microsoft.com
2. Clash DNS 返回 fake-IP（198.18.0.11）
3. 浏览器连接 198.18.0.11 → Clash 捕获这个连接
4. Clash 查询真实 IP（通过 nameserver / fallback）
5. Clash 直连或走代理连接真实服务器
6. 数据返回给客户端，客户端看到的还是正常访问结果

## 系统代理

## TUN模式
Tun模式原理是在电脑上安装了一张虚拟网卡，让所有流量都走这个代理
- [https://www.youtube.com/watch?v=c2UdUj0XpNE](https://www.youtube.com/watch?v=c2UdUj0XpNE)
![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1726906079510-7d3d93bc-608a-43ca-9bd0-879cb9d2f1c8.png)

![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1726906124176-e993a1ee-958f-4e74-b034-d698d203f0ca.png)

![](https://cdn.nlark.com/yuque/0/2024/png/33704534/1726906400669-3adf2793-618f-4ad8-a36c-c22219f7eb2d.png)