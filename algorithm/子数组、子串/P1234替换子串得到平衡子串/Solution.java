package DLeetcode.数据结构与算法.滑动窗口.P1234替换子串得到平衡子串;

public class Solution{
    public static void main(String[] args) {
        Solution solution = new Solution();
        System.out.println(solution.balancedString1("QWER"));
        System.out.println(solution.balancedString1("QQWE"));
        System.out.println(solution.balancedString1("QQQW"));
        System.out.println(solution.balancedString1("QWRWWQWW"));
        /*0 1 2 4*/
    }

    /*
    q w e r
    0 1 2 3
    * */

    public int balancedString(String s) {

        int l = 0, r = 0, n = s.length();
        int target = n / 4, ans = n - 1;
        int[] fre = new int[4];
        char[] index = new char[256], sArr = s.toCharArray();

        /*词频统计*/
        index['Q'] = 0;
        index['W'] = 1;
        index['E'] = 2;
        index['R'] = 3;
        for (char c : sArr){
            fre[index[c]]++;
        }

        while (r < n){
            char rChar = sArr[r++];
            fre[index[rChar]]--;

            while (isOk(fre, target, r - l) && l < r){
                char lChar = sArr[l];
                fre[index[lChar]]++;
                if (isOk(fre, target, r - l + 1)){
                    l++;
                    ans = Math.min(r - l, ans);
                } else {
                    fre[index[lChar]]--;
                    ans = Math.min(r - l, ans);
                    break;
                }
            }


        }
        return ans;
    }
    public int balancedString1(String s) {

        int l = 0, r = 0, n = s.length();

        int target = n / 4, ans = n - 1;
        int[] fre = new int[4];
        char[] index = new char[256], sArr = s.toCharArray();

        /*词频统计*/
        index['Q'] = 0;
        index['W'] = 1;
        index['E'] = 2;
        index['R'] = 3;
        for (char c : sArr){
            fre[index[c]]++;
        }

        while (l < n){
            /*检查窗口是否能满足*/
            while (!isOk(fre, target, r - l) && r < n){
                /*不能满足就扩充, 窗口之外的字母数量减少*/
                fre[index[sArr[r++]]]--;
            }
            /*能满足*/
            if (isOk(fre, target, r - l)){
                ans = Math.min(ans, r - l);
            }
            fre[index[sArr[l++]]]++;
        }
        return ans;
    }

    public static boolean isOk(int[] fre, int target, int len){
        for (int i = 0; i < 4; i++) {
            if (fre[i] > target){
                return false;
            }
            len -= target - fre[i];
        }
        return len == 0;
    }
}
