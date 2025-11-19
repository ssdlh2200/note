//
// Created by ssdlh on 2025/11/18.
//
#include <iostream>
#include <vector>
#include <unordered_map>

using namespace std;

class Solution {
public:
  //暴力法
  vector<int> twoSumV1(vector<int>& nums, int target) {
    for (int i = 0; i < nums.size(); i++) {
      for (int j = i + 1; j < nums.size(); j++) {
        if (nums[i] + nums[j] == target) {
          return vector<int>{i, j};
        }
      }
    }
    return vector<int>{1,2,3,4};
  }

  //哈希表
  vector<int> twoSum(vector<int>& nums, int target) {
    unordered_map<int, int> map;
    int to_find = 0;
    for (int i = 0; i < nums.size(); i++) {
      to_find = target - nums[i];
      if (map.count(to_find)){
        return vector<int>{i, map[to_find]};
      }
      map[nums[i]] = i;
    }
    return {};
  }

};

int main() {


  cout << "hello world" << endl;
}