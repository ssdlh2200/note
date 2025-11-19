//
// Created by ssdlh on 2025/11/19.
//
#include<iostream>
#include<vector>
#include<unordered_map>

using namespace std;
class Solution {
  public:
  //暴力法
    int subarraysDivByKV1(vector<int>& nums, int k) {

      unordered_map<int, int> map;

      vector<int> prefix_sum;

      int prefixs = 0;
      int res = 0;
      prefix_sum.push_back(0);


      for (int i = 0; i < nums.size(); ++i) {
        prefixs += nums[i];

        for (int j = 0; j <prefix_sum.size(); ++j) {
          if ((prefixs - prefix_sum[j]) % k == 0) res++;
        }

        prefix_sum.push_back(prefixs);
      }

      return res;
    }

  int subarraysDivByK(vector<int>& nums, int k) {

      unordered_map<int, int> map;
      int prefixs = 0;
      int res = 0;
      int key = 0;
      map[0] = 1;

      for (int i = 0; i < nums.size(); ++i) {
        prefixs += nums[i];
        key = (prefixs%k + k) % k;
        if (map.count(key)) res+=map[key];
        map[key]++;
      }

      return res;
    }
};

int main() {
  vector<int> nums{-1, 2, 9};
  vector<int> nums1{4,5,0,-2,-3,1};
  vector<int> nums2{5};

  int res = Solution().subarraysDivByK(nums, 2);
  int res1 = Solution().subarraysDivByK(nums1, 5);
  int res2 = Solution().subarraysDivByK(nums2, 9);

  cout << res << endl;
  cout << res1 << endl;
  cout << res2 << endl;
}