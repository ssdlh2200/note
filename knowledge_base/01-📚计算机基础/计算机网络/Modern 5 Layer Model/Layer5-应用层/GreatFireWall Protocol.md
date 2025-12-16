# GreatFireWall Protocol
## V2ray-Core
### 测试环境
在linux中安装v2ray-core
1. 查看架构uname -m
2. 下载v2ray-linux-64.zip
3. 将其解压放到/user/local/v2ray中
在windows中安装v2ray-core

在windwos中启动
- v2ray.exe run -c config.json
在linux中启动
- ./v2ray run -c config.json

> 为什么不使用clash，v2rayN等客户端更方便？
> v2rayN，clash等客户端默认trojan、vmless开启安全连接，无法裸奔。直接使用内核可实现裸奔


### trojan
#### 抓包trojan
客户端和服务端各自开启一个能解析trojan的服务，然后互相通信，之间的通信就使用了trojan协议
##### 服务端配置
- 服务端配置中好像没啥可说的...
```json
{
  "log": {
    "loglevel": "info"
  },
  "inbounds": [
    {
      "protocol": "trojan",
      "port": 1290,
      "listen": "::",
      "settings": {
        "clients": [
          {
            "password": "123456"
          }
        ]
      }
    }
  ],
  "outbounds": [
    {
      "protocol": "Freedom"
    }
  ]
}
```

##### 客户端配置
```json
{
  "inbounds": [
    {
      "listen": "::",
      "port": 1280,
      "protocol": "socks",
      "settings": {
        "auth": "noauth",
        "udp": true
      }
    }
  ],
  "log": {
    "loglevel": "info"
  },
  "outbounds": [
    {
      "protocol": "trojan",
      "settings": {
        "servers": [
          {
            "address": "192.168.5.2",
            "password": "123456",
            "port": 1290
          }
        ]
      },
      "streamSettings": {
        "network": "tcp",
        "security": "none"
      }
    }
  ]
}
```

##### 使用curl在客户端抓包
```txt
> curl.exe example.com -x socks://127.0.0.1:1280
```
具体可以查看trojan的协议定义，每个字节代表什么
![[20251207-01-21-27.png]]

### vless
同样的配置vless协议
#### 抓包vless
##### 服务端配置
```json
{
  "log": {
    "loglevel": "info"
  },
  "inbounds": [
    {
      "protocol": "vless",
      "port": 1290,
      "listen": "::",
      "settings": {
        "clients": [
          {
            "id": "e4f1c9d2-8f3b-4a6d-9e1b-7f2c3d4a5b6e"
          }
        ],
        "decryption": "none"
      }
    }
  ],
  "outbounds": [
    {
      "protocol": "Freedom"
    }
  ]
}
```
##### 客户端配置
```json
{
  "inbounds": [
    {
      "listen": "::",
      "port": 1280,
      "protocol": "socks",
      "settings": {
        "auth": "noauth",
        "udp": true
      }
    }
  ],
  "log": {
    "loglevel": "info"
  },
  "outbounds": [
    {
      "protocol": "vless",
      "settings": {
        "vnext": [
          {
            "address": "192.168.5.2",
            "port": 1290,
            "users": [
              {
                "id": "e4f1c9d2-8f3b-4a6d-9e1b-7f2c3d4a5b6e",
                "encryption": "none"
              }
            ]

          }
        ]
      },
      "streamSettings": {
        "network": "tcp",
        "security": "none"
      }
    }
  ]
}
```
##### 使用curl在客户端抓包
```txt
> curl.exe example.com -x socks://127.0.0.1:1280
```
![[20251207-01-42-28.png]]
可以发现vless协议还处理了响应回来的数据包
![[20251207-01-42-58.png]]
## Sing-Box
### vless+reality、hy2
#### 客户端配置
```JSON
{
	"log": {
		"level": "info"
	},
	"inbounds": [
		{
			"type": "mixed",
			"listen": "127.0.0.1",
			"listen_port": 10808
		}
	],
	"outbounds": [
		{
			"server": "66.154.107.58",
			"server_port": 12910,
			"up_mbps": 100,
			"down_mbps": 100,
			"password": "df96b6e84354",
			"type": "hysteria2",
			"tag": "proxy",
			"tls": {
				"enabled": true,
				"server_name": "amd.com",
				"insecure": true,
				"alpn": [
					"h3"
				]
			}
		}
	],
	outbounds只能生效一个
	"outbounds": [
		{
			"server": "66.154.107.58",
			"server_port": 12900,
			"uuid": "849385f2-c4ef-43d5-aecf-e90f6d697d91",
			"packet_encoding": "xudp",
			"type": "vless",
			"tag": "proxy",
			"tls": {
				"enabled": true,
				"server_name": "amd.com",
				"insecure": false,
				"utls": {
					"enabled": true,
					"fingerprint": "chrome"
				},
				"reality": {
					"enabled": true,
					"public_key": "GQCu5K5Yzlemp7J2bur4LO2FhG2Qmp8RRWDgjJt_Nlc",
					"short_id": "0123456789abcdef"
				}
			}
		}
	]
}
```

### trojan
#### 服务端配置
```json
{
  "log": {
    "level": "info"
  },
  "inbounds": [
    {
      "type": "trojan",
      "listen": "::",
      "listen_port": 1290,
      "users": [
        {
          "name": "ssdlh",
          "password": "f0frWrq6SBVNvnn0MO8HZQ=="
        }
      ]
    }
  ],
  "outbounds": [
    {
      "type": "direct"
    }
  ]
}
```
#### 客户端配置
```json
{
  "inbounds": [
    {
      "type": "socks",
      "listen": "::",
      "listen_port": 1280,
    }
  ],
  "log": {
    "level": "info"
  },
  "outbounds": [
    {
      "type": "trojan",
      "server": "192.168.5.2",
      "server_port": 1290,
      "password": "f0frWrq6SBVNvnn0MO8HZQ=="
    }
  ]
}
```
#### 使用curl在客户端抓包
```txt
> curl.exe example.com -x socks://127.0.0.1:1280
```

## Hysteria2
### 生成服务端自签证书
```
openssl req -x509 -nodes -newkey ec:<(openssl ecparam -name prime256v1) -keyout /ssdlh/singbox/server.key -out /ssdlh/singbox/server.crt -subj "/CN=amd.com" -days 36500 && sudo chown root /ssdlh/singbox/server.key && sudo chown root /ssdlh/singbox/server.crt
```

服务端配置
```json
{
  "type": "hysteria2",
  "listen": "::",
  "listen_port": 12910,
  "users": [
    {
      "password": "df96b6e84354"
    }
  ],
  "tls": {
    "enabled": true,
    "alpn": [
      "h3"
    ],
    "certificate_path": "/ssdlh/singbox/server.crt",
    "key_path": "/ssdlh/singbox/server.key"
  }
}
```

## Reality
### 生成密钥对
singbox生成密钥对
```txt
./sing-box.exe generate reality-keypair
PrivateKey: OP59X5UO3auGNGew95di6pPBCxh0kRIxjSw8xtGflX4
PublicKey: GQCu5K5Yzlemp7J2bur4LO2FhG2Qmp8RRWDgjJt_Nlc
```
### 服务端配置
```json
{
  "log": {
    "level": "info"
  },
  "inbounds": [
    {
      "type": "trojan",
      "listen": "::",
      "listen_port": 1290,
      "users": [
        {
          "name": "ssdlh",
          "password": "f0frWrq6SBVNvnn0MO8HZQ=="
        }
      ],
      "tls": {
        "enabled": true,
        "server_name": "cloudflare.com",
        "reality": {
          "enabled": true,
          "handshake": {
            "server": "cloudflare.com",
            "server_port": 443
          },
          "private_key": "OP59X5UO3auGNGew95di6pPBCxh0kRIxjSw8xtGflX4",
          "short_id": [
            "0123456789abcdef"
          ]
        }
      }
    }
  ],
  "outbounds": [
    {
      "type": "direct"
    }
  ]
}
```
### 客户端配置
```json
{
  "inbounds": [
    {
      "type": "socks",
      "listen": "::",
      "listen_port": 1280,
    }
  ],
  "log": {
    "level": "info"
  },
  "outbounds": [
    {
      "type": "trojan",
      "server": "192.168.5.2",
      "server_port": 1290,
      "password": "f0frWrq6SBVNvnn0MO8HZQ==",
      "tls": {
        "enabled": true,
        "disable_sni": false,
        "insecure": false,
        "server_name": "cloudflare.com",
        "utls": {
          "enabled": true,
          "fingerprint": "chrome"
        },
        "reality": {
          "enabled": true,
          "public_key": "GQCu5K5Yzlemp7J2bur4LO2FhG2Qmp8RRWDgjJt_Nlc",
          "short_id": "0123456789abcdef"
        }
      }
    }
  ]
}
```
### 抓包
![[20251207-04-04-48.png]]