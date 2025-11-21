#include <vector>
#include <unordered_map>

using namespace std;


class Solution {
public:
    long long maxSum(vector<int>& nums, int m, int k) {

        unordered_map<int, int> map;
        int dif = 0;
        long long res = 0;
        long long win_sum = 0;

        for (int i = 0; i < nums.size(); i++){
            int cur = nums[i];

            win_sum += cur;

            if(!map.count(cur) || map[cur] == 0){
                dif++;
            }
            map[cur]++;

            if(i >= k-1){

                if (dif >= m){
                    res = max(res, win_sum);
                }
                
                //溢出窗口头元素
                int to_pop = nums[i-k+1];
                map[to_pop]--;
                if (map[to_pop] == 0){
                    dif--;
                }
                win_sum -= to_pop;

            }

        }

        return res;
    }
};

int main(){


//    vector<int> nums1{2,6,7,3,1,7};
    vector<int> nums1{1,2,1,2,1,2,1};
    int m = 3;
    int k = 3;

    Solution().maxSum(nums1, m, k);

}