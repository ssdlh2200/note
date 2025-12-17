#########aaaaaaaaaaaaaaaaaaaa
#是不是能用c++
#。。。。。。。。。。。。。
ALIASES=(
    "dc=sudo docker compose"
)
TARGET_FILE="~/.bashrc"

for item in "${ALIASES[@]}"; do
    # 拆分键值对
    key="${item%%=*}"
    value="${item#*=}"

    if grep -q "^alias $key=" 

