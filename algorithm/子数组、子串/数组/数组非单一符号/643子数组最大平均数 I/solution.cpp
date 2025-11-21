#include <iostream>
#include <vector>

using namespace std;

class Solution {
public:
    double findMaxAverage(vector<int>& nums, int k) {
        double win_sum = 0;
        double res = -1e9;

        for (int i = 0; i < nums.size(); i++){
            win_sum += nums[i];
            if (i >= k-1){
                res = max(res, win_sum / 4);
                win_sum -= nums[i-k+1];
            }
        }
        return res;
    }
};

int main(){
    //vector<int> num{1,12,-5,-6,50,3};
    vector<int> num{5};
    int k = 1;

    Solution().findMaxAverage(num, k);
    return 0;
}



