//
// Created by ssdlh on 2025/11/19.
//
#include <iostream>
#include <unordered_map>
#include <vector>

using namespace std;

class Solution {
public:

  int shortestSubarray(vector<int>& nums, int k) {
    int prefix_sum = 0;
    int res = 100001;

    vector<int> prefix_sums{0};

    for (int i = 0; i < nums.size(); i++) {
      prefix_sum += nums[i];

      for (int j = 0; j < prefix_sums.size(); j++) {
        if (nums[i] - prefix_sums[j] >= k) {
          res = i - j < res ? i - j : res;
        }
      }

      prefix_sums.push_back(prefix_sum);
    }

    return res == 100001 ? -1 : res;
  }

};
int main(){

  Solution s;

  vector<int> nums1{2, -1, 2};
  int res1 = s.shortestSubarray(nums1, 3);
  int nums;
  cin >> nums;
  cout << nums << endl;
  cout << res1 << endl;
}