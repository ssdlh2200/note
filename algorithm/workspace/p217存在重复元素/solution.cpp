#include <vector>
#include <unordered_set>

using namespace std;


class Solution {
public:
    bool containsDuplicateV1(vector<int>& nums) {
        unordered_set<int> set;
        for (int i = 0; i < nums.size(); i++){
            if(set.count(nums[i])){
                return true;
            }
            set.insert(nums[i]);
        }
        return false;
    }
    bool containsDuplicate(vector<int>& nums) {
        int res = nums[0];
        for (int i = 1; i < nums.size(); i++){
            int temp = res;
            res = res ^ nums[i];
            if (res == 0 || temp == res){
                return true;
            }
        }
        return false;
    }
};