//
// Created by ssdlh on 2025/11/19.
//
#include <iostream>
#include <unordered_map>
#include <vector>
#include <deque>

using namespace std;

class Solution {
public:

  //暴力法
  int shortestSubarrayV1(vector<int> &nums, int k) {
    int prefix_sum = 0;
    int res = 100001;

    vector<int> prefix_sums{0};

    for (int i = 0; i < nums.size(); i++) {
      prefix_sum += nums[i];

      for (int j = 0; j < prefix_sums.size(); j++) {
        if (prefix_sum - prefix_sums[j] >= k) {
          res = i-j+1  < res ? i-j+1 : res;
        }
      }
      prefix_sums.push_back(prefix_sum);
    }

    return res == 100001 ? -1 : res;
  }

  //前缀和+单调队列
  int shortestSubarray(vector<int>& nums, int k) {

    deque<int> prefix_sum_deq{0};

    
    int prefix_sum;
    

    for (int i = 0; i < nums.size(); i++){
      prefix_sum += nums[i];
      while(!prefix_sum_deq.empty() && nums[i] <= nums[prefix_sum_deq.front()]){


      }


    }

    return 0;
  }

};
int main(){

  Solution s;
  cout << "hello world" << endl;
  vector<int> nums1{2, -1, 2};
  int res1 = s.shortestSubarray(nums1, 3);

  cout << res1 << endl;
}