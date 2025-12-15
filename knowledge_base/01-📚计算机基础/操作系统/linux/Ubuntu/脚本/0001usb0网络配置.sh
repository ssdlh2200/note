TARGET_DIR="/etc/netplan/"
TARGET_FILE="01-static-usb0.yaml"
FULL_PATH="${TARGET_DIR}${TARGET_FILE}"
CONTENT=\
"
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
"

echo "0. ${FULL_PATH}"
echo "1. 尝试创建文件"
sudo touch "${FULL_PATH}"
if [ $? -eq 0 ]; then
    echo "2. 创建文件成功:${TARGET_FILE}"
else
    echo "error: 创建文件失败"
    exit 1
fi

echo "2. 添加配置"
sudo echo "${CONTENT}" > "${FULL_PATH}"
if [ $? -eq 0 ]; then
    echo "3. 添加成功"
else
    echo "error: 添加失败"
    exit 1
fi

echo "3. --- 文件内容片段确认 ---"
cat "${FULL_PATH}"
echo "--------------------------"

echo "4. netplan try验证文件"
sudo chmod 600 "${FULL_PATH}"
sudo netplan try

echo "5. netplan apply应用文件"