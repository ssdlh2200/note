#include <vector>
#include <iostream>
using namespace std;

class Solution
{
public:
    // 暴力法
    vector<int> getAveragesV1(vector<int> &nums, int k)
    {
        if (k <= 0)
            return nums;
        if (k >= nums.size())
            return vector<int>(nums.size(), -1);

        int win_size = 2 * k + 1;
        long long win_sum = 0;
        // k > 0 && k < num.size()

        vector<int> res;

        for (int i = 0; i < nums.size(); i++)
        {

            win_sum += nums[i];

            if (i < k)
            {
                res.push_back(-1);
            }
            else
            {

                // 后面的元素够k个
                if (i + k < nums.size())
                {
                    long long find_k_element = win_sum;
                    for (int j = i + 1; j <= i + k; j++)
                    {
                        find_k_element += nums[j];
                    }
                    res.push_back(find_k_element / win_size);
                }
                // 后面的元素不足k个
                else
                {
                    res.push_back(-1);
                }

                // 窗口首元素出窗口
                win_sum -= nums[i - k];
            }
        }

        return res;
    }

    vector<int> getAverages(vector<int> &nums, int k)
    {
        if (k <= 0)
            return nums;
        if (k >= nums.size())
            return vector<int>(nums.size(), -1);


        vector<long long> prefix_sum(nums.size()+1);
        vector<int> res;

        int win_size = 2*k+1;

        prefix_sum[0] = 0;

        //计算出前缀和
        for(int i = 0; i < nums.size(); i++){
            prefix_sum[i+1] = prefix_sum[i] + nums[i];
        }

        for(int i = 0; i < nums.size(); i++){
            if ( i < k ){
                res.push_back(-1);
            } else {
                if (i+k < nums.size()){
                    res.push_back((prefix_sum[i+k+1] - prefix_sum[i-k]) / win_size);
                } else {
                    res.push_back(-1);
                }
            } 
                

        }

        
        return res;
    }
};

int main()
{
    vector<int> nums1{7};
    nums1[1] = 2;

    for (int x : nums1){
        cout<<x<<endl;
    }
    
}