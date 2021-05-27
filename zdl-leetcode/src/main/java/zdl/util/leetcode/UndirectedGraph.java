package zdl.util.leetcode;

import java.util.*;

/**
 * 无向图
 *
 * @author ZDLegend
 * @version 1.0
 * @date 2021/05/27/ 09:51
 */
public class UndirectedGraph<T> {

    private int edge;
    private final List<T> elements;
    private final Map<T, Integer> indexMap;
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

    private static class Statistic<T> {
        private final boolean[] marked;
        private final int[] id;
        private int count;
        private final UndirectedGraph<T> graph;

        public Statistic(UndirectedGraph<T> graph) {
            this.graph = graph;
            marked = new boolean[graph.size()];
            id = new int[graph.size()];
            for (int i = 0; i < graph.size(); i++) {
                if (!marked[i]) {
                    dfs(i);
                    count++;
                }
            }
        }

        /**
         * 计算无向图的连通分量
         *
         * @return 连通分量列表
         */
        public List<List<T>> to() {
            List<List<T>> ll = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                ll.add(new ArrayList<>());
            }
            for (int i = 0; i < id.length; i++) {
                ll.get(id[i]).add(graph.ofIndex(i));
            }
            return ll;
        }

        /**
         * 处理索引为V的元素的连通分量
         */
        private void dfs(int v) {
            marked[v] = false;
            id[v] = count;
            for (int w : graph.adj(v)) {
                if (!marked[w]) {
                    dfs(w);
                }
            }
        }
    }
}
