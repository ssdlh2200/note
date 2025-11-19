//
// Created by ssdlh on 2025/11/18.
//
#include <iostream>
#include <unordered_map>
#include <vector>
using namespace std;

class Solution {
public:
  int subarraySum(vector<int> &nums, int k) {
    unordered_map<int, int> map;

    int prefixs = 0;
    int res = 0;
    //前缀和定义prefix[0] = 0
    map[0] = 1;

    for (int i = 0; i < nums.size(); i++) {
      prefixs += nums[i];
      if (map.count(prefixs-k)) {
        res += map[prefixs-k];
      }
      map[prefixs]++;
    }
    return res;
  }
};

int main() {

  vector<int> nums1 = {1, 1, 1};
  vector<int> nums2 = {1};
  vector<int> nums3 = {0, 1, -1, 1, -2, 1};
  int k1 = 2;
  int k2 = 3;
  int k3 = 0;

  Solution* s = new Solution();
  int r1 = s->subarraySum(nums1, k1);
  int r2 = s->subarraySum(nums2, k2);
  int r3 = s->subarraySum(nums3, k3);

  cout << r1 << " " << r2 << " " << r3 << endl;

  delete s;
}
