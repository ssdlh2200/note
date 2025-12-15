echo "1. 卸载可能存在的版本"
sudo apt-get remove docker \
             docker-engine \
             docker-ce docker.io

echo "2. 更新源"
sudo apt-get update

echo "3. 安装依赖"
sudo apt install -y apt-transport-https ca-certificates curl software-properties-common

echo "4. 添加软件源gpg密钥"
curl -fsSL https://mirrors.aliyun.com/docker-ce/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

echo "5. 在source.list中添加Docker软件源"
echo \
  "deb [arch=amd64 signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://mirrors.aliyun.com/docker-ce/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

echo "6. 更新apt软件包缓存,安装docker-ce"
sudo apt-get update
sudo apt-get install docker-ce docker-ce-cli containerd.io

echo "7. 验证是否成功安装"
docker -v