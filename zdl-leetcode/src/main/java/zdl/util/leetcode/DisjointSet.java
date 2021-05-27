package zdl.util.leetcode;

import java.util.*;

/**
 * @author ZDLegend
 * @version 1.0
 * @date 2021/05/25/ 17:34
 */
public class DisjointSet {

    /**
     * 题目：给定一个字符串的集合，格式如：{aaa bbb ccc}， {bbb ddd}，{eee fff}，{ggg}，{ddd hhh}要求将其中交集不为空的集合合并，要求合并完成后的集合之间无交集，例如上例应输出{aaa bbb ccc ddd hhh}，{eee fff}， {ggg}。
     * （1）请描述你解决这个问题的思路；
     * （2）请给出主要的处理流程，算法，以及算法的复杂度
     * （3）请描述可能的改进。
     * 解答：
     * 1. 假定每个集合编号为0，1，2，3...
     * 2. 创建一个hash_map，key为字符串，value为一个链表，链表节点为字符串所在集合的编号。遍历所有的集合，将字符串和对应的集合编号插入到hash_map中去。
     * 3. 创建一个长度等于集合个数的int数组，表示集合间的合并关系。例如，下标为5的元素值为3，表示将下标为5的集合合并到下标为3的集合中去。开始时将所有值都初始化为-1，表示集合间没有互相合并。在集合合并的过程中，我们将所有的字符串都合并到编号较小的集合中去。
     * 遍历第二步中生成的hash_map，对于每个value中的链表，首先找到最小的集合编号（有些集合已经被合并过，需要顺着合并关系数组找到合并后的集合编号），然后将链表中所有编号的集合都合并到编号最小的集合中（通过更改合并关系数组）。
     * 4.现在合并关系数组中值为-1的集合即为最终的集合，它的元素来源于所有直接或间接指向它的集合。
     * 0: {aaa bbb ccc}
     * 1: {bbb ddd}
     * 2: {eee fff}
     * 3: {ggg}
     * 4: {ddd hhh}
     * 生成的hash_map，和处理完每个值后的合并关系数组分别为
     * aaa: 0          [-1, -1, -1, -1, -1]
     * bbb: 0, 1       [-1, 0, -1, -1, -1]
     * ccc: 0          [-1, 0, -1, -1, -1]
     * ddd: 1, 4       [-1, 0, -1, -1, 0]
     * eee: 2          [-1, 0, -1, -1, 0]
     * fff: 2          [-1, 0, -1, -1, 0]
     * ggg: 3          [-1, 0, -1, -1, 0]
     * hhh: 4          [-1, 0, -1, -1, 0]
     * 所以合并完后有三个集合，第0，1，4个集合合并到了一起，
     * 第2，3个集合没有进行合并。
     * Use "Disjoin-set".But I use "HashSet" and "HashMap" of Java API.Does "Disjoin-set" have its own data structure?
     * see also [url]http://www.csie.ntnu.edu.tw/~u91029/DisjointSets.html[/url]
     */
    private final int SIZE = 7;
    private int[] father;//the root in disjion set.
    private static List<Set<String>> resultList = new ArrayList<>();

    public static void main(String[] args) {
        List<String> str0 = Arrays.asList("aaa", "bbb", "ccc");
        List<String> str1 = Arrays.asList("bbb", "ddd");
        List<String> str2 = Arrays.asList("eee", "fff");
        List<String> str3 = Arrays.asList("ggg");
        List<String> str4 = Arrays.asList("ddd", "hhh");
        List<String> str5 = Arrays.asList("xx", "yy");
        List<String> str6 = Arrays.asList("zz", "yy");

        List<List<String>> strs = Arrays.asList(str0, str1, str2, str3, str4, str5, str6);

        DisjointSet disjointSet = new DisjointSet();
        disjointSet.disJoin(strs);
    }

    public void disJoin(List<List<String>> strs) {

        if (strs == null || strs.size() < 2) return;

        //change String[][] to List<Set>
        for (List<String> str : strs) {
            //when I write--"Arraylist list=Arrays.asList(strArray)","addAll()" is unsupported for such a arraylist.
            Set<String> set = new HashSet<>(str);
            resultList.add(set);
        }

        initial();
        Map<String, List<Integer>> map = storeInHashMap(strs);
        union(map);
    }

    //in the beginning,each element is in its own "group".
    public void initial() {
        father = new int[SIZE];
        for (int i = 0; i < SIZE; i++) {
            father[i] = i;
        }
    }

    /*Map<k,v>
     * key:String
     * value:List<Integer>-in which sets the string shows up.
     */
    public Map<String, List<Integer>> storeInHashMap(List<List<String>> strings) {
        Map<String, List<Integer>> map = new HashMap<>();
        for (int i = 0; i < strings.size(); i++) {
            for (String each : strings.get(i)) {
                if (!map.containsKey(each)) {
                    List<Integer> list = new ArrayList<>();
                    list.add(i);
                    map.put(each, list);
                } else {
                    map.get(each).add(i);
                }
            }
        }
        //traverse the hashmap
        for (Map.Entry<String, List<Integer>> entry : map.entrySet()) {
            String key = entry.getKey();
            List<Integer> value = entry.getValue();
            System.out.println(key + ":" + value);
        }
        return map;
    }

    public void union(Map<String, List<Integer>> map) {
        for (Map.Entry<String, List<Integer>> entry : map.entrySet()) {
            List<Integer> value = entry.getValue();
            unionHelp(value);//the arrays whose indexes are in the same list should be merged to one set.
        }
        System.out.println("the father array is " + Arrays.toString(father));
        //merge two sets
        for (int i = 0; i < SIZE; i++) {
            if (i != father[i]) {
                Set<String> dest = resultList.get(father[i]);
                Set<String> source = resultList.get(i);
                dest.addAll(source);
            }
        }
        //clear a set which has been added.
        for (int i = 0; i < SIZE; i++) {
            if (i != father[i]) {
                resultList.get(i).clear();
            }
        }
        System.out.println("after merge:" + resultList);
    }

    public void unionHelp(List<Integer> list) {
        int minFather = getFather(list.get(0));//list[0] is the smaller.
        for (Integer integer : list) {
            father[integer] = minFather;
        }
    }

    public int getFather(int x) {
        while (x != father[x]) {
            x = father[x];
        }
        return x;
    }

}