FULL_PATH="/etc/apt/sources.list"

echo "1. 检查是否已经备份、再选择备份软件源"
if [ ! -f "${FULL_PATH}" ]; then
    sudo cp "${FULL_PATH}" "${FULL_PATH}".bak
    if [ $? -eq 0 ]; then
        echo "2. 备份成功"
    else
        echo "error: 备份失败"
        exit 1
    fi
else
    echo "2. 已经存在备份"
fi

echo "3. 替换软件源"
sudo sed -i 's/archive.ubuntu/mirrors.aliyun/g' "${FULL_PATH}"
sudo sed -i 's/security.ubuntu/mirrors.aliyun/g' "${FULL_PATH}"

echo "4. --- 文件内容片段确认 ---"
cat "${FULL_PATH}"
echo "--------------------------"