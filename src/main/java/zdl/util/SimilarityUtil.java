package zdl.util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 相似度比较
 *
 * @author ZDLegend
 * @create 2018/3/29
 */

public class SimilarityUtil {

    /**
     * LD算法计算字符串相似度
     * <p>
     * 原理：依据的是一个字符串变为另一个字符串时，最少需要编辑操作的次数，许可的编辑操作包括将一个字符替换成另一个字符，
     * 插入一个字符，删除一个字符，次数越少越相似。
     * 计算LD(str1, str2)的过程：
     * ①. 计算str1的长度len1, str2的长度len2, 若其中有一个长度为0, 则返回非0长度的值
     * ②. 初始化一个二维数组d[ len1+1 ][ len2+1 ]用来计算两个字符串之间的距离，并初始化第一行，第一列的数据从0开始按照行列数自然增长
     * ③. 循环比较str1中每个字符与str2中每个字符的关系，若str1[i]等于str2[j], 记eq=1, 否侧记eq=0, 计算d[i][j] = min(d[i][j-1]+1, d[i-1][j]+1, d[i-1][j-1]+eq)
     * ④. 返回最后的结果d[len1][len2]，其值最大为max(len1, len2), 时间复杂度为O(len1*len2)
     */
    private static int compare(String str1, String str2) {
        int len1 = str1.length();
        int len2 = str2.length();
        if (len1 == 0) {
            return len2;
        }
        if (len2 == 0) {
            return len1;
        }

        int d[][] = new int[len1 + 1][len2 + 1];

        //初始化第一列
        for (int i = 0; i <= len1; i++) {
            d[i][0] = i;
        }

        //初始化第一行
        for (int j = 0; j <= len2; j++) {
            d[0][j] = j;
        }
        int eq;
        char char1, char2;
        for (int i = 1; i <= len1; i++) {
            char1 = str1.charAt(i - 1);
            for (int j = 1; j <= len2; j++) {
                char2 = str2.charAt(j - 1);
                if (char1 == char2 || char1 + 32 == char2 || char1 - 32 == char2) {
                    eq = 0;
                } else {
                    eq = 1;
                }
                d[i][j] = Math.min(d[i - 1][j - 1] + eq, Math.min(d[i][j - 1] + 1, d[i - 1][j] + 1));
            }
        }
        return d[len1][len2];
    }

    /**
     * 获取两字符串的相似度
     *
     * @return 相似度
     */
    public static float getSimilarityRatio(String str, String target) {
        if (StringUtils.isNotBlank(str) && StringUtils.isNotBlank(target))
            return 1 - (float) compare(str, target) / Math.max(str.length(), target.length());
        else
            return 0f;
    }

    public static void main(String[] args) {
        System.out.println("similarityRatio=" + getSimilarityRatio("11111111111111111111111111", "121212121212121212121"));
    }
}
