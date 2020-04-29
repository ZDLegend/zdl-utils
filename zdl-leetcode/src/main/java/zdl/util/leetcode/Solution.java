package zdl.util.leetcode;

import java.util.HashMap;

/**
 * Created by ZDLegend on 2020/4/28 11:13
 */
public class Solution {

    /**
     * 给定一个整数数组 nums 和一个目标值 target，请你在该数组中找出和为目标值的那 两个 整数，并返回他们的数组下标。
     * 你可以假设每种输入只会对应一个答案。但是，数组中同一个元素不能使用两遍。
     * example: 给定 nums = [2, 7, 11, 15], target = 9
     *          因为 nums[0] + nums[1] = 2 + 7 = 9
     *          所以返回 [0, 1]
     */
    public int[] twoSum(int[] nums, int target) {
        HashMap<Integer, Integer> table = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int res = target - nums[i];
            table.put(res, i);
            if (table.containsKey(res)) {
                return new int[]{i, table.get(res)};
            }
        }
        return new int[]{};
    }
}
