#include <vector>
using namespace std;



class Solution {
public:
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
            if (i >= minutes){

            }
        }



        return 0;
    }
};


