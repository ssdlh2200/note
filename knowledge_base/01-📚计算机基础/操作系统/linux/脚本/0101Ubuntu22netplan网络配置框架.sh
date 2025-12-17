#自定义网络配置=============================================
#查看所有网卡名：networkctl list
TARGET_DIR="/etc/netplan/"
TARGET_FILE="02-static-wlp3s0.yaml" # <--------需要修改
FULL_PATH="${TARGET_DIR}${TARGET_FILE}"
CONTENT=\
"
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
"

#=====================通用框架谨慎修改=====================
echo "0. ${FULL_PATH}"
echo "1. 尝试创建${FULL_PATH}文件"
sudo touch "${FULL_PATH}"
if [ $? -eq 0 ]; then
    echo "2. 创建文件成功"
else
    echo "error: 创建文件失败"
    exit 1
fi

echo "3. 尝试将配置写入yaml文件"
echo "${CONTENT}" | sudo tee "${FULL_PATH}" > /dev/null
if [ $? -eq 0 ]; then
    echo "4. 写入文件成功"
else
    echo "error: 写入文件失败"
    exit 1
fi

echo "5. --- 文件内容确认 ---"
sudo cat "${FULL_PATH}"
echo "--------------------------"

echo "6. 修改文件权限"
sudo chmod 600 "${FULL_PATH}"


echo "7. netplan generate文件格式验证"
sudo netplan generate
if [ $? -eq 0 ]; then
    echo "8. 验证文件成功"
else
    echo "error: 验证文件失败"
    exit 1
fi

# 交互确认逻辑
read -p "确认内容无误并应用配置？(输入 Y/y 继续): " confirm
if [ "$confirm" != "y" ] && [ "$confirm" != "Y" ]; then
    echo "操作已取消。"
    exit 0
fi


echo "9. netplan apply应用文件"
sudo netplan apply

echo "10. 使用【networkctl status 网卡名】查看状态"
