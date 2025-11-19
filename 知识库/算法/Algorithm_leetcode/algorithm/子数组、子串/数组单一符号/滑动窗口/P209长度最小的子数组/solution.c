#include <stdio.h>

// 暴力法
int minSubArrayLenV1(int target, int *nums, int numsSize) {
  int temp = 0;

  // 长度1，2，3，4，5...numsize
  for (int i = 0; i < numsSize; i++) {
    // 对于每个值都要取,剩余的数量要大于长度
    for (int j = 0; j < numsSize && (numsSize - j > i); j++) {
      temp = nums[j];

      // 对于每个值向后取k个
      for (int k = 0; k < i && j + k + 1 < numsSize; k++) {
        temp += nums[j + k + 1];
      }
      if (temp >= target) {
        return i + 1;
      }
      temp = 0;
    }
  }
  return 0;
}

// 滑动窗口
int minSubArrayLenV2(int target, int *nums, int numsSize) {
  int l = 0, r = 0;
  int res = 100001;
  int win_sum = 0;

  while (r <= numsSize && l <= r) {
    if (win_sum < target) {
      if (r >= numsSize) {
        l++;
      } else {
        win_sum += nums[r];
        r++;
      }
    } else if (win_sum >= target) {
      res = r - l < res ? r - l : res;
      win_sum -= nums[l];
      l++;
    }
  }
  return res == 100001 ? 0 : res;
}

int minSubArrayLen(int target, int *nums, int numsSize) {
  int l = 0, r = 0;
  int res = 100001;
  int win_sum = 0;


  while (r < numsSize) {
    win_sum += nums[r];
    r++;
    while (win_sum >= target) {
      res = r - l < res ? r - l : res;
      win_sum -= nums[l];
      l++;
    }
  }
  return res == 100001 ? 0 : res;
}

int main() {
  int nums[] = {2, 3, 1, 2, 4, 3};
  int target = 7;

  int res1 = minSubArrayLen(target, nums, sizeof(nums) / sizeof(nums[0]));

  printf("%d\n", res1);

  return 0;
}