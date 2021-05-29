package zdl.util.leetcode.graph;

import java.util.*;

/**
 * 无向图
 * <p>
 * 图是若干个顶点(Vertices)和边(Edges)相互连接组成的。边仅由两个顶点连接，并且没有方向的图称为无向图。
 *
 * @author ZDLegend
 * @version 1.0
 * @date 2021/05/27/ 09:51
 */
public class UndirectedGraph<T> {

    /**
     * 边个数
     */
    private int edge;

    /**
     * 节点列表
     */
    private final List<T> elements;

    /**
     * 节点对应坐标
     */
    private final Map<T, Integer> indexMap;

    /**
     * 使用邻接列表来表示
     */
    private final List<List<Integer>> adj;

    public UndirectedGraph() {
        edge = 0;
        elements = new ArrayList<>();
        indexMap = new HashMap<>();
        adj = new ArrayList<>();
    }

    /**
     * 添加边
     */
    public void addEdge(T element, T linked) {
        if (Objects.equals(element, linked)) {
            return;
        }
        Integer eIndex = indexMap.get(element);
        if (eIndex == null) {
            eIndex = elements.size();
            elements.add(element);
            adj.add(new ArrayList<>());
            indexMap.put(element, eIndex);
        }
        Integer lIndex = indexMap.get(linked);
        if (lIndex == null) {
            lIndex = elements.size();
            elements.add(linked);
            adj.add(new ArrayList<>());
            indexMap.put(linked, lIndex);
        }
        adj.get(eIndex).add(lIndex);
        adj.get(lIndex).add(eIndex);
        edge++;
    }

    public T ofIndex(int index) {
        return elements.get(index);
    }


    public Integer getIndex(T t) {
        return indexMap.getOrDefault(t, null);
    }

    /**
     * 获取所有index索引的关联变索引
     */
    public Iterable<Integer> adj(int index) {
        return adj.get(index);
    }

    public int degree(int index) {
        return adj.get(index).size();
    }

    public int size() {
        return elements.size();
    }

    public int getEdge() {
        return edge;
    }
}
