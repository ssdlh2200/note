CONFIG_JSON_DIR="/home/ssdlh/.config/sing-box/"
CONFIG_JSON_FULL_PATH="/home/ssdlh/.config/sing-box/config.json"


SING_BOX_SERVICE_FULL_PATH="/etc/systemd/system/sing-box.service"

CONFIG_JSON=\
"
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
	只能生效一个！！
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
}
"

SING_BOX_SERVICE=\
"
[Unit]
Description=sing-box Service
After=network.target

[Service]
ExecStart=/usr/local/bin/sing-box run -c /home/ssdlh/.config/sing-box/config.json
User=ssdlh
Group=ssdlh
Restart=always
StandardOutput=journal
StandardError=journal
Nice=0

[Install]
# 目标:定义服务应该在哪个“目标”(target)下启用
# multi-user.target 意味着服务将在系统正常启动后自动启动
WantedBy=multi-user.target
"

echo "1. 创建~/.config/sing-box文件夹"
sudo mkdir "${CONFIG_JSON_DIR}" \
&& echo "2. 创建文件夹成功" \
|| echo "2. 文件夹已经存在"

echo "3. 创建~/.config/sing-box/config.json文件"
sudo touch "${CONFIG_JSON_FULL_PATH}" \
&& echo "3. 创建文件成功" \
|| echo "3. 文件已经存在"

echo "4. 尝试写入config.json,自己手动写入shell环境下会丢失\"\",需要每个引号前加转义符"
# sudo echo "${CONFIG_JSON}" | sudo tee "${CONFIG_JSON_FULL_PATH}" > /dev/null \
# && echo "5. 写入config.json文件成功" \
# || { echo "error: 写入config.json文件失败"; exit 1; }

echo "6. 创建service配置"
sudo touch "${SING_BOX_SERVICE_FULL_PATH}"
sudo echo "${SING_BOX_SERVICE}" > "${SING_BOX_SERVICE_FULL_PATH}" \
&& echo "6. 创建service成功" \
|| { echo "error: 创建service失败"; exit 1; }

echo "7. 重新加载systemd"
sudo systemctl daemon-reload

echo "8. 立即启动服务"
sudo systemctl start sing-box