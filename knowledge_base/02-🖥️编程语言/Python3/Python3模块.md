# Python3模块
## -m启动一个模块

## 网络模块
### http服务器
```
python -m http.server [port] [--bind ADDRESS] [--directory DIRECTORY] [--cgi] [--certfile CERT] [--keyfile KEY]

```

- 绑定地址
```py
python -m http.server 8000 --bind 0.0.0.0 //0.0.0.0监听所有网卡接口
```
- 启动https
```
python -m http.server 8443 --certfile cert.pem --keyfile key.pem
```