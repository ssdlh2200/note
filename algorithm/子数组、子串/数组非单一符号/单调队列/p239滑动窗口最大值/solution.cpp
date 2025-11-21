#include <iostream>
#include <vector>
#include <queue>
#include <deque>

using namespace std;

class Solution {
public:

  int find_queue_max(queue<int> que){
    int que_max = -10001;
    //cout << "que size:" << que.size() << endl;
    //cout << "que:";

    while (!que.empty()){
      que_max = que_max > que.front() ? que_max : que.front();
      que.pop();
    }
  
    return que_max;
  }

  vector<int> maxSlidingWindowV1(vector<int>& nums, int k) {
    //数组长度是1直接返回
    if (k == 1) {
      return nums;
    }

    vector<int> res;
    queue<int> que;
    int que_max = -10001;

    //前K的元素入队列
    for (int i = 0; i < k; i++){
      que.push(nums[i]);
      que_max = nums[i] > que_max ? nums[i] : que_max;
    }
    //窗口中最大值入队
    res.push_back(que_max);

    //移动滑动窗口到最右边
    for (int i = k; i < nums.size(); i++){
      //队列中要删除的值
      int to_pop = que.front();
      que.pop();
      que.push(nums[i]);
      //如果要删除的是最大的那个值
      if (to_pop >= que_max){
        //重新寻找最大值
        res.push_back(find_queue_max(que));
      } 
      //如果删除的不是最大值
      else {
        que_max = que_max > nums[i] ? que_max : nums[i];
        res.push_back(que_max);
      }
    }
    return res;
  }



  vector<int> maxSlidingWindow(vector<int>& nums, int k) {
    if (k == 1) return nums;
    vector<int> res;
    deque<int>  deq;

    for (int i = 0; i < nums.size(); i++){


      //等号为了防止出现1 2 3 3 2 2 2这种情况，但是等号又出现个新问题，会让当前队列元素出光
      //如果入队的元素比队尾元素大，那么需要把他放在队首
      while(!deq.empty() && nums[i] >= nums[deq.back()]){
        deq.pop_back();
      }
      /*为什么入队列在出队列的后面？
       *因为要先维护队列的单调性,如果队列中先有一个3，当后面要把4插入队列时
       *因为while中已经将4挪到队首，仍然是最大值
       */
      deq.push_back(i);

      /*
       *对于每一个队首元素检查当前是否超出窗口
       */
      if(i - deq.front() >= k){
        deq.pop_front();
      }

      if(i>=k-1){
        //cout << "i:" << deq.front() << " nums[i]:" << nums[deq.front()] << endl;
        res.push_back(nums[deq.front()]);
      }
    }
    return res;
  }


};


int main(){
  vector<int> nums1{1,3,-1,-3,5,3,6,7};
  vector<int> nums2{3, 1, 1, 3};
  vector<int> nums3{7, 2, 4};
  vector<int> res1 = Solution().maxSlidingWindow(nums1, 3);
  vector<int> res2 = Solution().maxSlidingWindow(nums2, 3);
  vector<int> res3 = Solution().maxSlidingWindow(nums3, 2);
  for (int i : res1){
    cout << i << endl;
  }
  cout << endl;
  for (int i: res2) {
    cout << i << endl;
  }


}