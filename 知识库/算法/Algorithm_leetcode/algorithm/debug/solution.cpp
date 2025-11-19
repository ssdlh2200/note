#include <iostream>
#include <vector>

using namespace std;

class Solution {
public:
  //暴力法
  vector<int> maxSlidingWindowV1(vector<int>& nums, int k) {

    vector<int> res;
    //移动滑动窗口到最右边
    for (int i = k - 1; i < nums.size(); i++) {
      int win_max = -10001;
      int l = i-k+1;

      //窗口内k个数组分别比较大小
      for (int j = 0; j < k; j++) {
        win_max = nums[l] > win_max ? nums[l] : win_max;
        l++;
      }
      res.push_back(win_max);
    }
    return res;
  }

  //错误
  vector<int> maxSlidingWindow(vector<int>& nums, int k) {
    if ( k==1 ) return nums;
    vector<int> res;
    int win_max = -10001;
    for (int i = 0; i < k; i++) {
      win_max = nums[i] > win_max ? nums[i] : win_max;
    }
    res.push_back(win_max);
    for (int i = k; i < nums.size(); i++) {
      win_max = nums[i] > win_max ? nums[i] : win_max;
      res.push_back(win_max);
    }
    return res;
  }




};


int main(){
  vector<int> nums{1,3,-1,-3,5,3,6,7};
  auto res = Solution().maxSlidingWindow(nums, 3);


}