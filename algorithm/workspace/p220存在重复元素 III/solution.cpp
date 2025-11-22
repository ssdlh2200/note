#include <vector>
#include <unordered_map>

using namespace std;

class Solution {
public:
    bool containsNearbyAlmostDuplicate(vector<int>& nums, int indexDiff, int valueDiff) {
        
        unordered_map<int, int> map;

        for (int i = 0; i < nums.size(); i++){


            map[nums[i]] = i;
        }


        return false;
    }
};