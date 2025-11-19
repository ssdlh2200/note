#include <stdio.h>

// 暴力法
int subarraySumV1(int *nums, int numsSize, int k) {
  int res = 0;
  int temp = 0;

  for (int i = 0; i < numsSize; i++) {
    temp = nums[i];

    // 处理特殊情况：数组只有一个元素
    if (temp == k && numsSize == 1) {
      res++;
      continue;
    }
    // 处理特殊情况：一个元素就满足k值
    if (temp == k)res++;


    //printf("temp: %d\n", temp);
    for (int j = i + 1; j < numsSize; j++) {
      temp += nums[j];
      if (temp == k) res++;
      //printf("temp+=nums[j]: %d\n", temp);
      //printf("res: %d\n", res);
    }

    temp = 0;
  }

  return res;
}



int main() {
  int nums1[] = {1, 2, 3};
  int nums1_size = sizeof(nums1) / sizeof(nums1[0]);
  int nums2[] = {1};
  int nums2_size = sizeof(nums2) / sizeof(nums2[0]);
  int nums3[] = {1, -1, 0};
  int nums3_size = sizeof(nums3) / sizeof(nums3[0]);

  int res1 = subarraySum(nums1, nums1_size, 3);
  int res2 = subarraySum(nums2, nums2_size, 3);
  int res3 = subarraySum(nums3, nums3_size, 0);

  printf("%d\n", res1); //2
  printf("%d\n", res2); //0
  printf("%d\n", res3); //3

  return 0;
}