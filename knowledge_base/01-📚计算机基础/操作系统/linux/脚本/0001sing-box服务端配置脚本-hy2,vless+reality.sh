# 目前模板就是这样，以后有需要的再弄吧
#
CONFIG_JSON=\
"
{
  "log": {
    "level": "info"
  },
  "inbounds": [
    {
      "type": "vless",
      "listen": "::",
      "listen_port": 12900,
      "users": [
        {
          "name": "ssdlh",
          "uuid": "849385f2-c4ef-43d5-aecf-e90f6d697d91"
        }
      ],
      "tls": {
        "enabled": true,
        "server_name": "amd.com",
        "reality": {
          "enabled": true,
          "handshake": {
            "server": "amd.com",
            "server_port": 443
          },
          "private_key": "OP59X5UO3auGNGew95di6pPBCxh0kRIxjSw8xtGflX4",
          "short_id": [
            "0123456789abcdef"
          ]
        }
      }
    },
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
        "certificate_path": "~/.config/sing-box/server.crt",
        "key_path": "~/.config/sing-box/server.key"
      }
    }

  ],
  "outbounds": [
    {
      "type": "direct"
    }
  ]
}
"

SING_BOX_SERVICE=\
"
[Unit]
Description=Sing-Box Service
After=network.target

[Service]
# 确保替换为您的实际用户名和配置文件路径！
ExecStart=/usr/local/bin/sing-box run -c /home/YOUR_USERNAME/.config/sing-box/config.json

# 以您的普通用户身份运行，提高安全性
User=YOUR_USERNAME

# 当进程意外退出时，Systemd 会自动重启它
Restart=always

# 进程优先级设置（可选）
Nice=10

[Install]
WantedBy=multi-user.target
"

echo "1. 创建~/.config/sing-box/config.json文件"
