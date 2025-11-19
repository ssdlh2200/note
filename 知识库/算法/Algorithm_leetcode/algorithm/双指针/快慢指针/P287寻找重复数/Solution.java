package DLeetcode.数据结构与算法.双指针.快慢指针.P287寻找重复数;

public class Solution {
    public static void main(String[] args) {
        Solution solution = new Solution();
        int duplicate = solution.findDuplicate(new int[]{3,1,3,4,2});
        System.out.println(duplicate);
    }
    public int findDuplicate(int[] nums) {
        int fast = nums[nums[0]];
        int slow = nums[0];

        while (fast != slow){
            fast = nums[nums[fast]];
            slow = nums[slow];
        }
        fast = 0;
        while (fast != slow){
            fast = nums[fast];
            slow = nums[slow];
        }
        return fast;
    }

}
