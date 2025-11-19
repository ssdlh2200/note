#include <iostream>
#include <vector>

using namespace std;

class Solution {
public:

  vector<int> maxSlidingWindow(vector<int>& nums, int k) {
    //滑动窗口大小要小于数组的长度
    if (k > nums.size()) {
      return {};
    }
    vector<int> res;
    //移动滑动窗口到最右边
    for (int i = k - 1; i < nums.size(); i++) {
      int l = i;
      int r = i + k;
      int win_max = -10001;
      for (; l < r; l++) {
        win_max = nums[l] > win_max ? nums[l] : win_max;
      }
      res.push_back(win_max);
    } 
    return res;
  }
};


int main(){
  //vector<int> nums{1,3,-1,-3,5,3,6,7};
  //auto res = Solution().maxSlidingWindow(nums, 3);
  cout << "hello world12345" << endl;
  cout << "hello world" << endl;
  //getchar();
  
}