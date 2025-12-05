# Python3模块
## -m启动一个模块

## 网络模块
### http服务器
```
python -m http.server [port] [--bind ADDRESS] [--directory DIRECTORY] [--cgi] [--certfile CERT] [--keyfile KEY]

```
- 绑定地址
```python
python -m http.server 8080 --bind 0.0.0.0 #0.0.0.0监听所有网卡接口
```
- 启动https
```python
python -m http.server 443 --bind 0.0.0.0 --certfile cert.pem --keyfile key.pem

# 如果CRT文件是DER二进制格式,那么可以使用.crt文件
# 比如以-----BEGIN CERTIFICATE-----开头
```

