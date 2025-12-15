TARGET_DIR="/etc/systemd/system/network-online.target.wants/"
TARGET_FILE="systemd-networkd-wait-online.service"
FULL_PATH="${TARGET_DIR}${TARGET_FILE}"

echo "0. ${FULL_PATH}"
echo "1. 开始修改 ${TARGET_FILE}"

echo "2. 尝试清除旧配置"
sudo sed -i '/^TimeoutStartSec=/d' "${FULL_PATH}"
if [ $? -eq 0 ]; then
    echo "3. 清除旧配置成功"
else
    echo "error: 清除旧配置失败"
    exit 1
fi

echo "4. 尝试添加TimeoutStartSec"
sudo sed -i '/\[Service\]/a\TimeoutStartSec=1sec' "${FULL_PATH}"
if [ $? -eq 0 ]; then
    echo "5. 添加成功"
else
    echo "error: 添加失败"
    exit 1
fi

echo "6. --- 文件内容片段确认 ---"
cat "${FULL_PATH}" | grep -A 5 -B 3 'TimeoutStartSec'
echo "--------------------------"