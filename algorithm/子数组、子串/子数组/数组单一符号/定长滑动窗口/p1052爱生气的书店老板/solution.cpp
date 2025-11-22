#include <vector>
#include <iostream>
using namespace std;



class Solution {
public:
    int maxSatisfied(vector<int>& customers, vector<int>& grumpy, int minutes) {
        int win_sum = 0;
        int win_max = 0;
        int res_sum = 0;

        for (int i = 0; i < customers.size(); i++){
            
            win_sum += customers[i]*grumpy[i];
            res_sum += customers[i]*(grumpy[i]^1);
            
            //开始出窗口
            if (i >= minutes-1){
                win_max  = max(win_max, win_sum);
                win_sum -= customers[i-minutes+1]*grumpy[i-minutes+1];
            }
        }
        return res_sum+win_max;
    }

    /* 
        int maxSatisfied(vector<int>& customers, vector<int>& grumpy, int minutes) {
        int win_sum = 0;
        int win_max = 0;
        int res = 0;

        for (int i = 0; i < customers.size(); i++){
            if(grumpy[i] == 1){
                win_sum += customers[i];
            } else {
                res += customers[i];
            }

            //开始出窗口
            if (i >= minutes-1){
                win_max = max(win_max, win_sum);
                if(grumpy[i-minutes+1] == 1){
                    win_sum -= customers[i-minutes+1];
                }
            }
        }
        return res+win_max;
    }
    */
};

int main(){
    vector<int> cus1{2,6,6,9};
    vector<int> gru1{0,0,1,1};
    int res = Solution().maxSatisfied(cus1, gru1, 1);
    cout << res << endl;
}
